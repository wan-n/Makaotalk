package com.example.wifitest;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class UndeadService extends Service {
    public static Intent serviceIntent = null;
    private PowerManager pm;
    private KeyguardManager km;

    int i;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("system", "최초 호출");
        registerReceiver(rssiReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("system", "서비스 호출됨");
        serviceIntent = intent;

        //wifi 스캔 설정
        IntentFilter rssiFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(rssiReceiver, rssiFilter);
        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiMan != null;
        wifiMan.startScan();

        initializeNotification();


        return START_STICKY;
    }


    public void initializeNotification() {
        Log.d("system", "알림 표시 중...");

        //상태바에 아이콘 표시(notification)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.drawable.cat);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("앱 실행하기");
        style.setBigContentTitle(null);
        style.setSummaryText("Wifi 상태 체크 중");
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {  //버전이 0 이상이면
            manager.createNotificationChannel(new NotificationChannel("1", "undead_service", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        Log.d("system", "종료됨");
        unregisterReceiver(rssiReceiver);   //wifi


        if(serviceIntent != null){
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);   //3초 간격으로
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }

    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // 앱 목록에서 kill 했을 경우
        Log.d("system", "실행 목록에서 삭제됨");
        //unregisterReceiver(rssiReceiver);    //kill시 onDestroy()도 같이 실행되어 막아둠


        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }



    private BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("system", "broadcastreceiver, " + i);
            i++;

            WifiManager wifiMan = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiMan.startScan();
            int newRssi= wifiMan.getConnectionInfo().getRssi();
            Toast.makeText(getApplicationContext(), "" + newRssi, Toast.LENGTH_SHORT).show();


            //측정한 신호세기가 -80 이하이면
            if(newRssi <= -80){
                Log.d("rssi", ""+newRssi);
                //일정 수치 이하일때 || 연결이 끊어졌을 때, 두 경우 모두 고려하기
                //알림 해제까진 신호 측정 중지하도록(이미지인식 기능과 연결)


                if(isScreenOn()){
                    Log.d("popup", "Screen ON");
                    //푸시알림
                    Intent popup = new Intent(getApplicationContext(), PopupActivity.class);
                    popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    createNotification(newRssi);
                    startActivity(popup);
                }else{
                    Log.d("popup", "Screen OFF");
                    Intent popup = new Intent(getApplicationContext(), PopupActivity.class);
                    popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    //푸시알림
                    createNotification(newRssi);

                    startActivity(popup);
                }



            }

        }
    };


    //스크린이 켜져있나?
    private boolean isScreenOn(){
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return pm.isInteractive();
    }

    //기기가 잠겨있나?
    private boolean checkDeviceLock(){
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }


    private void createNotification(int rssi){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default");
        builder.setContentTitle("Wifi 체크");
        builder.setContentText("" + rssi);
        builder.setSmallIcon(R.drawable.cat);

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }
}
