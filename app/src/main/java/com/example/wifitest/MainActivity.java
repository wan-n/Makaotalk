package com.example.wifitest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

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

    public Intent foregroundServiceIntent;

    private Button button1, button2;

    IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    public String wifi_ssid;

    @SuppressLint("BatteryLife")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_recyclerview);
/*
        //Doze 모드 진입 시 foreground service가 동작하고 있는 애플리케이션을 백그라운드로 옮겨
        //프로세스의 중요도를 FOREGROUND_SERVICE로 조정한다. - 네트워크를 사용할 수 있다.
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(startMain);

        //설명 참고 : https://brunch.co.kr/@huewu/3
 */
        //권한설정
        AutoPermissions.Companion.loadAllPermissions(this,101);

        //화이트리스트에 등록되어있는지 확인 - 도즈와 어플 대기모드의 대상으로부터 제외되는 화이트 리스트
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }

        //이미 foreground service가 작동중인 상태에서 앱을 재실행한 상태라면
        if (null != UndeadService.serviceIntent){
            foregroundServiceIntent = UndeadService.serviceIntent;
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_SHORT).show();
        }

        //와이파이 강도 측정 기능 제어할 버튼 삽입
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button1:
                        //foreground service 시작
                        startForeground();
                        break;
                    case R.id.button2:
                        //foreground service 종료
                        if (null != foregroundServiceIntent) {
                            Log.d("system", "foreground service 종료");
                            stopService(foregroundServiceIntent);
                            foregroundServiceIntent = null;
                            UndeadService.serviceIntent = null;
                            UndeadService.am.cancel(UndeadService.sender);
                        }


                        break;

                }
            }
        };
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);


        //Wifi Scan
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
    }



    public void clickWifiScan(View view){
        boolean success = wifiManager.startScan();
        if (!success) Toast.makeText(MainActivity.this, "Wifi Scan에 실패하였습니다." ,Toast.LENGTH_SHORT).show();
    }

    // wifiManager.startScan(); 시  발동되는 메소드
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
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
        mAdapter=new WifiAdapter(results);
        recyclerView.setAdapter(mAdapter);
    }

    // Wifi검색 실패
    private void scanFailure() {
    }

    //wifidialog가 끝나면 eventbus를 통해 전달된 ssid 저장
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void enterPWEvent(wifiDialog.WifiData event){
        wifi_ssid = event.ssid;

        Log.d("wifi","Main\nssid : " + wifi_ssid);
    }

    /*
    //wbutton을 클릭했을 때 호출
    public void wSaveFile(){
        //todo ssid+pwd 가져오기
        String data = "";
        try {
            //FileOutputStream 객체생성, 파일명 "data.txt", 새로운 텍스트 추가하기 모드
            FileOutputStream fos = openFileOutput("data.txt", Context.MODE_APPEND);
            PrintWriter writer= new PrintWriter(fos);
            writer.println(data);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //소프트 키보드 없애기
        InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    //저장된 wifi 파일 읽어오기
    public void wReadFile() {
        //todo 저장된 wifi 정보 이용 연결
        StringBuffer buffer = new StringBuffer();
        try {
            //FileInputStream 객체생성, 파일명 "data.txt"
            FileInputStream fis = openFileInput("data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String str = reader.readLine();//한 줄씩 읽어오기

            while (str != null) {
                buffer.append(str + "\n");
                str = reader.readLine();
            }
            wtext.setText(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void startForeground(){
        if (null == UndeadService.serviceIntent) {
            foregroundServiceIntent = new Intent(this, UndeadService.class);
            startService(foregroundServiceIntent);
            Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_SHORT).show();
        } else {
            foregroundServiceIntent = UndeadService.serviceIntent;
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != foregroundServiceIntent) {
            stopService(foregroundServiceIntent);
            foregroundServiceIntent = null;

        }

    }

    //Permission에 관한 메소드
    @Override
    public void onDenied(int i, String[] strings) {
        Toast.makeText(this, "onDenied~~", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(int i, String[] strings) {
        Toast.makeText(this, "onGranted~~", Toast.LENGTH_SHORT).show();
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }
}