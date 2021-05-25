package com.MakaoTalkforMask.makaotalkformask;


import android.annotation.SuppressLint;
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

import androidx.core.app.NotificationCompat;

import com.MakaoTalkforMask.makaotalkformask.popup.SubActivity;
import com.MakaoTalkforMask.makaotalkformask.wifiscan.PreferenceManager;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.WIFI_SERVICE;

public class WifiReceiver extends BroadcastReceiver  {

    public static boolean checkSSID;  //현재 등록된 와이파이와 연결된 와이파이가 같은지 구분하기 위한 변수
    private static PowerManager.WakeLock sCpuWakeLock;
    public static TimerTask tt;
    private static NotificationManager notificationManager;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("system", "check wifi");


        WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

        //현재 연결된 wifi SSID 가져오기
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();
        String mySSID = wifiInfo.getSSID();    //현재 연결된 와이파이명
        String str, str2 = null;      //등록된 와이파이명을 저장할 변수

        try {
            str= PreferenceManager.getString(context,"Wifi_ssid");
            str2 = '"' + str + '"';

            Log.d("array", str2);
            Log.d("array", mySSID);
            if(mySSID.equals(str2)){
                checkSSID = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //등록된 와이파이명과 연결된 와이파이명이 같을 경우
        if (checkSSID) {

            //타이머(알림반복)가 작동 중이라면
            if (tt != null) {
                tt.cancel();   //타이머 종료
                notificationManager.cancel(2);   //알림 종료
            }


            if (!str2.equals(mySSID)) {
                //푸시알림 반복
                repeatNotification(context);   //3초에 한번씩 알림 울리게 타이머 제작.
                checkSSID = false;
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
    private static boolean isScreenOn(Context context){
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isInteractive();
    }

    //기기가 잠겨있나?
    private boolean checkDeviceLock(Context context){
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }


    //타이머 - 3초에 한번 알림 울리도록
    public static void repeatNotification(final Context context){

        tt = new TimerTask() {
            @Override
            public void run() {
                 createNotification(context);
            }
        };

        Timer timer = new Timer();
        timer.schedule(tt,0,3000);

    }


    //상단바에 알림
    @SuppressLint("InvalidWakeLockTag")
    public static void createNotification(Context context){

        //화면이 꺼져있으면 켠다.
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
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "2");
        noti.setContentTitle("외출하셨나요?");
        noti.setContentText("눌러서 마스크 인증하기");
        noti.setSmallIcon(R.drawable.icon_notification);
        noti.setOngoing(true);
        noti.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        noti.setAutoCancel(true);
        Intent popup = new Intent(context, SubActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, popup, 0);
        noti.setContentIntent(pendingIntent);

        // 알림 표시
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("2", "기본 채널", NotificationManager.IMPORTANCE_HIGH));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(2, noti.build());

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}