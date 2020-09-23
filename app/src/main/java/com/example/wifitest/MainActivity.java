package com.example.wifitest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //기능 제어할 스위치 삽입
        switch1 = findViewById(R.id.switch1);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.switch1:
                        if(switch1.isChecked()) {   //스위치가 ON일 때
                            //신호세기 측정 시작
                            registerReceiver(rssiReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
                        }else { //스위치가 OFF일 때
                            //기능 정지
                            onDestroy();
                        }
                        break;
                }
            }
        };
        switch1.setOnClickListener(listener);

    };

    private BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiManager wifiMan = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiMan.startScan();
            int newRssi= wifiMan.getConnectionInfo().getRssi();
            //Toast.makeText(MainActivity.this, "" + newRssi, Toast.LENGTH_SHORT).show();

            //측정한 신호세기가 -80 이하이면 토스트 메시지 실행
            if(newRssi <= -80){
                Toast.makeText(MainActivity.this, "알림 생성 (" + newRssi + ")", Toast.LENGTH_SHORT).show();
                //일정 수치 이하일때 || 연결이 끊어졌을 때, 두 경우 모두 고려하기
                //알림 해제까진 신호 측정 중지하도록(딥러닝 기능과 연결하는 함수 삽입이 적당할 듯


                //푸시알림
                createNotification(newRssi);

            }

        }
    };

    private void createNotification(int rssi){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "default");

        //builder.setSmallIcon(R.mipmap.ic_launcher);
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

    protected void onResume() {
        super.onResume();
        IntentFilter rssiFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(rssiReceiver, rssiFilter);

        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiMan.startScan();
    }

    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(rssiReceiver);
    }

}