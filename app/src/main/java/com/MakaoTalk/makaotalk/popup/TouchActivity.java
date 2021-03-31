package com.MakaoTalk.makaotalk.popup;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.MakaoTalk.makaotalk.PreferenceManager;
import com.MakaoTalk.makaotalk.R;
import com.MakaoTalk.makaotalk.WifiReceiver;

public class TouchActivity extends Activity {
    private TextView textviewtouch;
    private Button btn;

    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch);

        // 설정 값을 불러와 count에 저장
        count = PreferenceManager.getInt(this, "rebuild");

        // SubActivity onPuase하기 위한 반투명 코드
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
        layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount= 0.7f;getWindow().setAttributes(layoutParams);
        setContentView(R.layout.touch);

        textviewtouch = (TextView)findViewById(R.id.textviewtouch);
        btn = (Button)findViewById(R.id.btn);

        // count 값 세팅
        textviewtouch.setText(Integer.toString(count));

        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("popup", "터치 화면 재시작");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(count==0){
                    WifiReceiver.tt.cancel();    //알림 반복 종료
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(2);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(getApplicationContext(), "알람이 종료되었습니다", Toast.LENGTH_SHORT).show();
                }

                else{
                    count--;
                    textviewtouch.setText(Integer.toString(count));
                    break;
                }
        }
        return super.onTouchEvent(event);
    }
}
