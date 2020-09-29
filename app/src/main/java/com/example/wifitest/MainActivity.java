package com.example.wifitest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public Intent foregroundServiceIntent;
    public UndeadService uservice;

    private Button button1, button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //화이트리스트에 등록되어있는지 확인 - 도즈와 어플 대기모드의 대상으로부터 제외되는 화이트 리스트, 배터리 최적화에 대응
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }



        //이미 foreground service가 작동중인 상태에서 앱을 재실행한 상태라면
        if (null != UndeadService.serviceIntent){
            foregroundServiceIntent = UndeadService.serviceIntent;
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_SHORT).show();
        }



        //와이파이 강도 측정 기능 제어할 버튼 삽입
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button1:
                        //foreground service 시작
                        startForeground();
                        break;
                    case R.id.button2:
                        //foreground service 종료
                        if (null != foregroundServiceIntent) {
                            Log.d("system", "foreground service 종료");
                            stopService(foregroundServiceIntent);
                            foregroundServiceIntent = null;
                            UndeadService.serviceIntent = null;
                            UndeadService.am.cancel(UndeadService.sender);   //알람매니저 종료
                        }
                        break;

                }
            }
        };
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);

    }



    public void startForeground(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            if (null == UndeadService.serviceIntent) {
                foregroundServiceIntent = new Intent(this, UndeadService.class);
                startForegroundService(foregroundServiceIntent);
                Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_SHORT).show();
            } else {
                foregroundServiceIntent = UndeadService.serviceIntent;
                Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != foregroundServiceIntent) {
            stopService(foregroundServiceIntent);
            foregroundServiceIntent = null;
        }

    }





}