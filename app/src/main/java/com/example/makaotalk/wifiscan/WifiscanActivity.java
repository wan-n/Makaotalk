package com.example.makaotalk.wifiscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.makaotalk.MainActivity;
import com.example.makaotalk.R;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class WifiscanActivity extends AppCompatActivity implements View.OnClickListener, AutoPermissionsListener {

    IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    private RecyclerView recyclerView;
    public String wifi_ssid;
    public String wifi_saved;
    private TextView tv_wifi;
    private Context mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter<WifiAdapter.WifiViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiscan);
        this.getIntent();

        mContext = this.getApplicationContext();
        tv_wifi = findViewById(R.id.tv_svWifiName);
        Button s_wifi = findViewById(R.id.scan_button);
        recyclerView = findViewById(R.id.rv_recyclerview);
        recyclerView.setHasFixedSize(true);

        //layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        //권한설정
        AutoPermissions.Companion.loadAllPermissions(this,101);

        //swipe refresh 객체, 새로고침
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchWifi();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //새로고침 완료
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 4000);
            }
        });




        //새로고침 아이콘 색상
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light
        );

        s_wifi.setOnClickListener(this);

        wLoadFile();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_button:
                Log.d("wifi scan", "한다! 와이파이 스캔!");
                searchWifi();
                break;

            /*case R.id.del_button:
                //key 삭제
                PreferenceManager.removeKey(mContext,"Wifi_ssid");
                Log.d("wifi", "삭제란다 애송아");
                break;
             */
        }
    }

    public void searchWifi(){
        //Wifi Scan
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        //Event Bus
        try{ EventBus.getDefault().register(this); }catch (Exception ignored){}

        boolean success = wifiManager.startScan();
        if (!success)
            Toast.makeText(WifiscanActivity.this, "Wifi Scan에 실패하였습니다." , Toast.LENGTH_SHORT).show();
    }


    // wifiManager.startScan(); 시  발동되는 메소드
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("wifi", "여기는 wifiScanReceiver!");
            boolean success =intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if(success) {
                scanSuccess();
            } else {
                scanFailure();
            }
        }
    };

    // Wifi 검색 성공
    private void scanSuccess() {
        Log.d("wifi", "여기는 scanSuccess!");
        List<ScanResult> results = wifiManager.getScanResults();
        mAdapter = new WifiAdapter(results);
        recyclerView.setAdapter(mAdapter);
    }

    // Wifi 검색 실패
    private void scanFailure() {
    }

    //Wifi dialog 가 끝나면 Event bus 를 통해 전달된 ssid 저장
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wifiEvent(wifiDialog.WifiData event){
        wifi_ssid = event.ssid;
        Log.d("wifi","Main ssid : " + wifi_ssid);
        Log.d("wifi", "저장할것인가 자네");
        wSaveFile(wifi_ssid);
    }

    //wifi 저장
    public void wSaveFile(String ssid){

        wifi_saved = PreferenceManager.getString(mContext,"Wifi_ssid");

        //저장된 와이파이가 없을 때
        PreferenceManager.setString(mContext,"Wifi_ssid",ssid);

        //ACTIVITY REFRESH
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
    }


    //WIFI FILE 불러오기
    public void wLoadFile(){
        wifi_saved = PreferenceManager.getString(mContext,"Wifi_ssid");
        tv_wifi.setText(wifi_saved);
    }


    //권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, @NonNull String[] strings) {

    }

    @Override
    public void onGranted(int i, @NonNull String[] strings) {

    }
}