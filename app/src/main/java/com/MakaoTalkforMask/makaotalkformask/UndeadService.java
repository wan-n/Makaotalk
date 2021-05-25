package com.MakaoTalkforMask.makaotalkformask;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class UndeadService extends Service {
    public static Intent serviceIntent = null;

    public static AlarmManager am = null;
    public static PendingIntent sender = null;

    //private static PowerManager.WakeLock sCpuWakeLock;


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
        Log.d("system", "Forground Service 최초 호출");
    }


    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("system", "서비스 호출됨");
        serviceIntent = intent;

        startForegroundService();


        if(StartServiceActivity.checkSwitch){
            StartServiceActivity.switch1.setChecked(true); //앱 종료해도 스위치 상태 고정
        }


        //wifi rssi 측정을 위한 알람매니저
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3);   //3초 간격으로 콜
        Intent intent2 = new Intent(getApplicationContext(), WifiReceiver.class);
        sender = PendingIntent.getBroadcast(getApplicationContext(), 0,intent2,0);
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            //am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), sender), sender);
        }else {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }


        return START_STICKY;   //서비스가 종료되었을 때, 서비스를 재 실행 함. onStartCommand()를 호출
    }

    public void startForegroundService() {
        Log.d("system", "아이콘 표시 중...");

        //상태바에 아이콘 표시(notification)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.drawable.icon_forground);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("    앱을 실행하려면 누르세요.");
        style.setBigContentTitle(null);
        style.setSummaryText("Wifi 상태 체크 중 ...");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            manager.createNotificationChannel(new NotificationChannel("1", "undead_service", NotificationManager.IMPORTANCE_MIN));
        }
        Notification notification = builder.build();
        startForeground(1, notification);

    }




    public void setAlarm() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), sender), sender);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        Log.d("system", "종료됨");

        if(serviceIntent != null){
            setAlarm();
        }

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // 앱 목록에서 kill 했을 경우
        Log.d("system", "실행 목록에서 삭제됨");

        if(serviceIntent != null) {
            setAlarm();
        }


    }

}
