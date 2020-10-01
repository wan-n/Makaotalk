package com.example.wifitest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class PopupActivity extends Activity {

    private Button button_ok, button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        Log.d("popup", "팝업창 표시");

        WifiReceiver.checkPop = false;  //notification 끄기

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON   //Screen을 켜진 상태로 유지
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD    //Keyguard 를 해지
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON    //Screen On
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);    //Lock 화면 위로 실행




        button_ok = findViewById(R.id.button_ok);
        button_cancel = findViewById(R.id.button_cancel);
        //버튼 이벤트
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_ok:
                        Toast.makeText(getApplicationContext(), "확인!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.button_cancel:
                        Toast.makeText(getApplicationContext(), "취소!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        button_ok.setOnClickListener(listener);
        button_cancel.setOnClickListener(listener);

    }


    @Override
    protected void onResume() {
        super.onResume();
        WifiReceiver.checkPop = false;   // ex) 홈버튼 이후 다시 실행할 경우
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiReceiver.checkPop = true;
    }


    //백버튼
   @Override
    public void onBackPressed() {
        super.onBackPressed();
       WifiReceiver.checkPop = true;
    }

    //메뉴버튼
    @Override
    protected void onPause() {
        super.onPause();
        WifiReceiver.checkPop = true;
        /*
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);

         */
    }

    //홈버튼
    @Override
    protected void onStop() {
        super.onStop();
        WifiReceiver.checkPop = true;
    }
}