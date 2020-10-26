package com.example.makaotalk;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.makaotalk.wifiscan.wifiDialog;

public class SettingsPopup extends StartServiceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_popup);
    }

}