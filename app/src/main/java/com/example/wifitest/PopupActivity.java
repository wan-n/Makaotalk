package com.example.wifitest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON   //Screen을 켜진 상태로 유지
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD    //Keyguard 를 해지
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON    //Screen On
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);    //Lock 화면 위로 실행



        button_ok = findViewById(R.id.button_ok);
        button_cancel = findViewById(R.id.button_cancel);

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
}