package com.example.makaotalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makaotalk.wifiscan.PreferenceManager;

public class StartServiceActivity extends AppCompatActivity {

    public static Switch switch1;
    public static TextView tv_status;

     public static FrameLayout line1, line2;

    public static Intent foregroundServiceIntent;

    private TextView tv_wifi;
    public static boolean checkSwitch;

    private Context mContext;
    public String wifi_saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startservice);
        this.getIntent();

        mContext = this.getApplicationContext();

        tv_wifi = findViewById(R.id.tv_svWifiName);
        tv_status = findViewById(R.id.tv_status);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);

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
                            //tv_status.setTextColor(Color.parseColor("#db7c4d"));
                            line1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_line_on1));
                            line2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_line_on2));
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
                                //tv_status.setTextColor(Color.parseColor("#4e7da6"));
                                line1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_line));
                                line2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_line2));
                                onResume();
                            }
                        }
                }
            }
        };
        switch1.setOnClickListener(listener);



    }


    //WIFI FILE 불러오기
    public void wLoadFile(){
        wifi_saved = PreferenceManager.getString(mContext,"Wifi_ssid");
        tv_wifi.setText(wifi_saved);
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
    protected void onResume() {
        super.onResume();

        if(checkSwitch){
            switch1.setChecked(true); //앱 종료해도 스위치 상태 고정
            tv_status.setText("ON");
            //tv_status.setTextColor(Color.parseColor("#db7c4d"));
            line1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_line_on1));
            line2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_line_on2));
        }else{
            tv_status.setText("OFF");
            //tv_status.setTextColor(Color.parseColor("#4e7da6"));
            line1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_line));
            line2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_line2));
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