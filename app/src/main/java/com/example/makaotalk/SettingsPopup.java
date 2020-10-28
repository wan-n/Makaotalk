package com.example.makaotalk;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.makaotalk.wifiscan.WifiscanActivity;

public class SettingsPopup extends StartServiceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_popup);

        Button button = findViewById(R.id.btn_wifi_reset);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent11 = new Intent(SettingsPopup.this, WifiscanActivity.class);
                startActivity(intent11);
            }
        });

    }

}