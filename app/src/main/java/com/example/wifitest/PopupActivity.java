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
        WifiReceiver.checkPop = false;  //포그라운드서비스에서 notification 기능 건너뜀
        WifiReceiver.tt.cancel();    //알림 반복 종료

        button_ok = findViewById(R.id.button_ok);
        button_cancel = findViewById(R.id.button_cancel);
        //버튼 이벤트
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_ok:
                        Toast.makeText(getApplicationContext(), "확인!", Toast.LENGTH_SHORT).show();
                        WifiReceiver.checkPop=true;  //마스크 인증 완료 시
                        WifiReceiver.tt.cancel();    //알림 반복 종료
                        finish();
                        break;
                    case R.id.button_cancel:
                        Toast.makeText(getApplicationContext(), "취소!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        button_ok.setOnClickListener(listener);
        button_cancel.setOnClickListener(listener);


        /*
          무사히 마스크 인증을 완료했을 경우 checkPop = true 로 설정해주기
          마스크 인식을 완료하기 전까지는 포그라운드 서비스는 돌아가지만 와이파이 강도 측정과 알림기능은 멈추기 위해
         */

    }


    @Override
    protected void onResume() {
        super.onResume();

        WifiReceiver.checkPop = false;  //포그라운드서비스에서 notification 기능 건너뜀
        WifiReceiver.tt.cancel();    //알림 반복 종료

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    //백버튼
   @Override
    public void onBackPressed() {
        super.onBackPressed();

    }



    //하단버튼
    @Override
    protected void onStop() {
        super.onStop();

        //알람 재가동
        if(!WifiReceiver.checkPop){
            WifiReceiver.repeatNotification(getBaseContext());
            finish();
        }

    }

}