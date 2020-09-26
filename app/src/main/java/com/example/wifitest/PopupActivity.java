package com.example.wifitest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class PopupActivity extends Activity {

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        Log.d("popup", "팝업창 표시");




        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON   //Screen을 켜진 상태로 유지
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD    //Keyguard 를 해지
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON    //Screen On
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);    //Lock 화면 위로 실행





    }
}