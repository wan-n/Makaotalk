package com.example.makaotalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

import com.example.makaotalk.popup.Classifier;
import com.example.makaotalk.wifiscan.PreferenceManager;
import com.example.makaotalk.wifiscan.WifiAdapter;
import com.example.makaotalk.wifiscan.WifiscanActivity;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener{

    String btext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //권한설정
        AutoPermissions.Companion.loadAllPermissions(this, 101);


            Context mContext = this;

            //어플을 한번이라도 실행시켰으면 user 로 등록함
            btext = PreferenceManager.getString(mContext, "begin");

            if (btext.equals("user")) {
                //user 값일 때
                Log.d("Main", "유우우우저!");
                WifiReceiver.checkSSID = false;  //알림울림 방지
                Intent intent1 = new Intent(MainActivity.this, StartServiceActivity.class);
                startActivity(intent1);
            } else {
                //begin 이라는 key 로 저장된 데이터가 default 값일때 데이터 저장한다.
                Log.d("Main", "초보자임돠");
                PreferenceManager.setString(mContext, "begin", "user");
                Intent intent2 = new Intent(MainActivity.this, Tutorial.class);
                startActivity(intent2);
            }
            finish();

        }



    @Override
    public void onDenied(int i,@NonNull String[] strings) {
    }

    @Override
    public void onGranted(int i,@NonNull String[] strings) {
    }
}