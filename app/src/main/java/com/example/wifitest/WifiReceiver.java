package com.example.wifitest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WifiReceiver extends BroadcastReceiver  {

    private PowerManager pm;
    private KeyguardManager km;

    public static boolean checkPop;
    private static PowerManager.WakeLock sCpuWakeLock;




    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("system", "check wifi");
        boolean checkSSID = false;  //현재 연결되어있는 와이파이가 사용자의 저장목록에 있는지 확인하기 위한 변수

        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);


        //현재 연결된 wifi SSID 가져오기
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();
        String mySSID = wifiInfo.getSSID();

        try {
            //FileInputStream 객체생성, 파일명 "data.txt"
            FileInputStream fis=context.openFileInput("WIFI_SSID.txt");
            BufferedReader reader= new BufferedReader(new InputStreamReader(fis));
            String str= reader.readLine();//한 줄씩 읽어오기
            String str2 = '"' + str + '"';
            while(str!=null){
                Log.d("array", str2);
                Log.d("array", mySSID);
                if(mySSID.equals(str2)){

                    checkSSID = true;
                    break;
                }
                str= reader.readLine();
                str2='"' + str + '"';
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //현재 연결된 와이파이가 저장된 목록에 있으면 WIFI 스캔
        if (checkSSID) {
            //WIFI 강도 스캔
            wifiMan.startScan();
            int newRssi = wifiMan.getConnectionInfo().getRssi();
            Toast.makeText(context, "" + newRssi, Toast.LENGTH_SHORT).show();
            Log.d("WIFI", "" + newRssi);


            //측정한 신호세기가 -80 이하이면
            if (newRssi <= -80) {
                //일정 수치 이하일때 || 연결이 끊어졌을 때, 두 경우 모두 고려하기
                //알림 해제까진 신호 측정 중지하도록(이미지인식 기능과 연결)

                //팝업창이 떠있는가?
                if (checkPop == true) {
                    //푸시알림
                    createNotification(context, newRssi);
                } else {
                    //알림 없음
                }
            }
        }


        //foreground service 실행  -> onStartCommand()부터 시작됨.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, UndeadService.class);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, UndeadService.class);
            context.startService(in);
        }

    }

    //스크린이 켜져있나?
    private boolean isScreenOn(Context context){
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isInteractive();
    }

    //기기가 잠겨있나?
    private boolean checkDeviceLock(Context context){
        km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    //상단바에 알림
    @SuppressLint("InvalidWakeLockTag")
    private void createNotification(Context context, int rssi){
        if (!isScreenOn(context)) {
            Log.d("display", "LOCK");
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            sCpuWakeLock = pm.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE, "hi");

            sCpuWakeLock.acquire();
        }


        Log.d("WIFI", "알림생성");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2");
        builder.setContentTitle("Wifi 체크");
        builder.setContentText("" + rssi);
        builder.setSmallIcon(R.drawable.cat);
        builder.setOngoing(true);
        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);
        Intent popup = new Intent(context, PopupActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, popup, 0);
        builder.setContentIntent(pendingIntent);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("2", "기본 채널", NotificationManager.IMPORTANCE_HIGH));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(2, builder.build());

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}