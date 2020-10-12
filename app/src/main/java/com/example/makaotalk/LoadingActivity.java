package com.example.makaotalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(1000); //대기 초 설정
            startActivity(new Intent(this, StartServiceActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
