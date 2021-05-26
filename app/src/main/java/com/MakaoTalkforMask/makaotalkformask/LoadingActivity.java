package com.MakaoTalkforMask.makaotalkformask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(1000); //대기 초 설정
            startActivity(new Intent(this,MainActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
