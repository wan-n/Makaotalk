package com.example.makaotalk;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.makaotalk.wifiscan.WifiscanActivity;

public class SettingsPopup extends StartServiceActivity {
    private SeekBar seekBar;
    private TextView TextView_touch_count;
    private int count;

    private Button btn_minus;
    private Button btn_plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_popup);
/**
 * 와이파이 설정
 */
        Button button = findViewById(R.id.btn_wifi_reset);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent11 = new Intent(SettingsPopup.this, WifiscanActivity.class);
                startActivity(intent11);
            }
        });
/**
 * 터치 횟수 설정
 */
        count = PreferenceManager.getInt(this,"rebuild");

        TextView_touch_count = findViewById(R.id.TextView_touch_count);

        seekBar = findViewById(R.id.seekBar);
        // 설정 불러와 count에 저장

        // 불러온 count 값을 시크바에 적용
        seekBar.setProgress(count);

        TextView_touch_count.setText(Integer.toString(count));

        // 시크바 움직임
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // 시크바를 움직일 때
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView_touch_count.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            // 시크바를 멈추면 자동으로 값을 저장함
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                count = Integer.valueOf(seekBar.getProgress());
            }
        });

        // 버튼 누름
        btn_minus = findViewById(R.id.btn_minus);
        btn_plus = findViewById(R.id.btn_plus);

        btn_minus.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count-50>=100){
                    count = count-50;
                    seekBar.setProgress(count);
                    TextView_touch_count.setText(Integer.toString(count));
                }
                else{
                    count = 100;
                    seekBar.setProgress(count);
                    TextView_touch_count.setText(Integer.toString(count));
                }
            }
        });

        btn_plus.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count+50 <=1000){
                    count = count+50;
                    seekBar.setProgress(count);
                    TextView_touch_count.setText(Integer.toString(count));
                }
                else{
                    count = 1000;
                    seekBar.setProgress(count);
                    TextView_touch_count.setText(Integer.toString(count));
                }
            }
        });
    }

    @Override
    protected void onStop() {
        PreferenceManager.setInt(getApplicationContext(),"rebuild",count);
        super.onStop();
    }
}