package com.example.makaotalk;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener{

    String btext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //권한설정
        AutoPermissions.Companion.loadAllPermissions(this, 101);

        Context mContext = this;

        //어플을 한번이라도 실행시켰으면 user 로 등록함
        btext = PreferenceManager.getString(mContext, "begin");

        if (btext.equals("user")) {
            //user 값일 때
            Log.d("Main", "유우우우저!");
            Intent intent1 = new Intent(MainActivity.this, StartServiceActivity.class);
            startActivity(intent1);
            finish();
        } else {
            //begin 이라는 key 로 저장된 데이터가 default 값일때 데이터 저장한다.
            Log.d("Main", "초보자임돠");
            PreferenceManager.setString(mContext, "begin", "user");
            Intent intent2 = new Intent(MainActivity.this, WifiscanActivity.class);
            startActivity(intent2);
            finish();
        }

    }

    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {
    }
}