package com.example.wifitest;

import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class WifiReceiver extends BroadcastReceiver {

    private PowerManager pm;
    private KeyguardManager km;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("system", "check wifi");

        //WIFI 강도 스캔
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiMan.startScan();
        int newRssi = wifiMan.getConnectionInfo().getRssi();
        Toast.makeText(context, "" + newRssi, Toast.LENGTH_SHORT).show();
        Log.d("WIFI", "" + newRssi);


        //측정한 신호세기가 -80 이하이면
        if(newRssi <= -80) {
            Log.d("rssi", "" + newRssi);
            //일정 수치 이하일때 || 연결이 끊어졌을 때, 두 경우 모두 고려하기
            //알림 해제까진 신호 측정 중지하도록(이미지인식 기능과 연결)


            if (isScreenOn(context)) {
                //화면이 켜져있을 경우
                Log.d("popup", "Screen ON");
                //푸시알림
                Intent popup = new Intent(context, PopupActivity.class);
                popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                createNotification(context, newRssi);
                context.startActivity(popup);
            } else {
                //화면이 꺼져있을 경우
                Log.d("popup", "Screen OFF");
                Intent popup = new Intent(context, PopupActivity.class);
                popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                //푸시알림
                createNotification(context,newRssi);

                context.startActivity(popup);
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
    private void createNotification(Context context, int rssi){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
        builder.setContentTitle("Wifi 체크");
        builder.setContentText("" + rssi);
        builder.setSmallIcon(R.drawable.cat);

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }
}