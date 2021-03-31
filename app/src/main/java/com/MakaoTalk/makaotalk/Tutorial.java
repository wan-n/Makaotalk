package com.MakaoTalk.makaotalk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.MakaoTalk.makaotalk.wifiscan.WifiscanActivity;

public class Tutorial extends AppCompatActivity {

    ViewFlipper flipper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);

        flipper = (ViewFlipper) findViewById(R.id.flipper);


        for (int i = 0; i < 4; i++) {
            ImageView img = new ImageView(this);
            img.setImageResource(R.drawable.tutorial_1 + i);
            flipper.addView(img);
        }

        Animation showIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        flipper.setInAnimation(showIn);
        flipper.setOutAnimation(this, android.R.anim.fade_out);

    }



    public void mOnClick(View v) {
        switch (v.getId()) {

            case R.id.btn_previous:

                flipper.showPrevious();
                break;

            case R.id.btn_next:

                flipper.showNext();

                break;
            case R.id.btn_fin: {
                Intent intent = new Intent(getApplicationContext(), WifiscanActivity.class);
                startActivity(intent);
            }
        }
    }

}
