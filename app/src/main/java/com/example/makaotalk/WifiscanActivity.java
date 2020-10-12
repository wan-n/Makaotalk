package com.example.makaotalk;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

public class WifiscanActivity extends AppCompatActivity implements AutoPermissionsListener {

    IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    private RecyclerView recyclerView;
    public String wifi_ssid;
    private TextView tv_wifi;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiscan);

        mContext = this.getApplicationContext();
        recyclerView = findViewById(R.id.rv_recyclerview);
        tv_wifi = findViewById(R.id.tv_svWifiName);

        //권한설정
        AutoPermissions.Companion.loadAllPermissions(this,101);

        //Wifi Scan
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        //Event Bus
        try{ EventBus.getDefault().register(this); }catch (Exception ignored){}

        wLoadFile();
    }

    //wifi scan 클릭 이벤트
    public void clickWifiScan(View view){
        boolean success = wifiManager.startScan();
        if (!success) Toast.makeText(WifiscanActivity.this, "Wifi Scan에 실패하였습니다." ,Toast.LENGTH_SHORT).show();
    }

    // wifiManager.startScan(); 시  발동되는 메소드
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success =intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if(success) {
                scanSuccess();
            } else {
                scanFailure();
            }
        }
    };

    // Wifi검색 성공
    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        RecyclerView.Adapter<WifiAdapter.WifiViewHolder> mAdapter = new WifiAdapter(results);
        recyclerView.setAdapter(mAdapter);
    }

    // Wifi검색 실패
    private void scanFailure() {
    }

    //wifidialog가 끝나면 eventbus를 통해 전달된 ssid 저장
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wifiEvent(wifiDialog.WifiData event){
        wifi_ssid = event.ssid;

        Log.d("wifi","Main ssid : " + wifi_ssid);
        Log.d("wifi", "저장할것인가 자네");
        wSaveFile(wifi_ssid);
    }

    public void wSaveFile(String ssid){
        try {
            //저장된 파일 삭제 후 저장
            mContext.deleteFile("WIFI_SSID.txt");

            //FileOutputStream 객체생성, 파일명 "data.txt", 새로운 텍스트 추가하기 모드
            FileOutputStream fos=openFileOutput("WIFI_SSID.txt", Context.MODE_APPEND);
            PrintWriter writer= new PrintWriter(fos);
            writer.println(ssid);
            writer.close();

            //ACTIVITY REFRESH
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            this.finish();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //WIFI FILE 불러오기
    public void wLoadFile(){
        StringBuilder buffer= new StringBuilder();
        try {
            //FileInputStream 객체생성, 파일명 "data.txt"
            FileInputStream fis=openFileInput("WIFI_SSID.txt");
            BufferedReader reader= new BufferedReader(new InputStreamReader(fis));
            String str=reader.readLine();//한 줄씩 읽어오기
            while(str!=null){
                buffer.append(str).append("\n");
                str=reader.readLine();
            }
            tv_wifi.setText(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //와이파이 파일 삭제
    public void clickWifiDel(View view){
        mContext.deleteFile("WIFI_SSID.txt");
        Log.d("wifi", "삭제란다 애송아");

        //ACTIVITY REFRESH
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {
    }
}
