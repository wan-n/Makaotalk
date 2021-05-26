package com.MakaoTalkforMask.makaotalkformask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.MakaoTalkforMask.makaotalkformask.wifiscan.WifiscanActivity;

public class Tutorial extends AppCompatActivity {

    ViewFlipper flipper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);

        AlertDialog.Builder builder = new AlertDialog.Builder(Tutorial.this);

        builder.setCancelable(true);
        builder.setTitle("위치정보 수집 동의에 대한 알림");
        builder.setMessage("이 앱은 [와이파이 탐색 후 홈 와이파이 등록], [와이파이 신호세기를 통한 사용자의 외출 탐지]를 위해 사용자의 위치 정보를 수집하고 있습니다. " +
                "수집된 사용자의 위치 정보는 해당 기능에만 사용됩니다. 위치정보 수집에 동의하지 않을 경우 앱 사용에 제한이 있을 수 있습니다.");

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();

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
