package com.example.makaotalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class StartServiceActivity extends AppCompatActivity {

    public static Switch switch1;
    public static TextView tv_status;

    public static Intent foregroundServiceIntent;

    private TextView tv_wifi;
    public static boolean checkSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startservice);

        tv_wifi = findViewById(R.id.tv_svWifiName);

        tv_status = findViewById(R.id.tv_status);

        //와이파이 강도 측정 기능 제어할 스위치 삽입
        switch1 = findViewById(R.id.switch1);

        //이미 foreground service가 작동중인 상태에서 앱을 재실행한 상태라면
        if (null != UndeadService.serviceIntent){
            foregroundServiceIntent = UndeadService.serviceIntent;
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_SHORT).show();
        }


        wLoadFile();      //텍스트뷰에 등록된 와이파이 표시

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.switch1:
                        if(switch1.isChecked()){
                            //foreground service 시작
                            WifiReceiver.checkPop = false;
                            checkSwitch = true;
                            startForeground();

                            tv_status.setText("ON");
                            onResume();
                        }else {
                            //foreground service 종료
                            if (null != foregroundServiceIntent) {
                                Log.d("system", "foreground service 종료");
                                stopService(foregroundServiceIntent);
                                foregroundServiceIntent = null;
                                UndeadService.serviceIntent = null;
                                UndeadService.am.cancel(UndeadService.sender);
                                checkSwitch = false;

                                tv_status.setText("OFF");
                                onResume();
                            }
                        }
                }
            }
        };
        switch1.setOnClickListener(listener);


        if(checkSwitch){


        }else {
            tv_status.setText("OFF");


        }

    }


    public void wLoadFile(){
        StringBuilder buffer= new StringBuilder();
        try {
            //FileInputStream 객체생성, 파일명 "data.txt"
            FileInputStream fis=openFileInput("WIFI_SSID.txt");
            BufferedReader reader= new BufferedReader(new InputStreamReader(fis));
            String str=reader.readLine();//한 줄씩 읽어오기
            while(str!=null){
                buffer.append(str).append("\n");
                str=reader.readLine();
            }
            tv_wifi.setText(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startForeground(){
        if (null == UndeadService.serviceIntent) {
            foregroundServiceIntent = new Intent(this, UndeadService.class);
            startService(foregroundServiceIntent);
            Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_SHORT).show();
        } else {
            foregroundServiceIntent = UndeadService.serviceIntent;
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_SHORT).show();
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