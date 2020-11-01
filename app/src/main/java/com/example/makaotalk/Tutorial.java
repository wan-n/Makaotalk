package com.example.makaotalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

public class Tutorial extends AppCompatActivity {

    ViewFlipper flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);


        for (int i = 0; i < 4; i++) {
            ImageView img = new ImageView(this);
            img.setImageResource(R.drawable.tutorial_1 + i);
            flipper.addView(img);
        }

        Animation showIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        flipper.setInAnimation(showIn);
        flipper.setOutAnimation(this, android.R.anim.slide_out_right);

    }


    public void mOnClick(View v) {
        switch (v.getId()) {

            case R.id.btn_previous:

                flipper.showPrevious();
                break;

            case R.id.btn_next:

                flipper.showNext();

                break;
            case R.id.btn_fin:
                finish();
        }
    }

}
