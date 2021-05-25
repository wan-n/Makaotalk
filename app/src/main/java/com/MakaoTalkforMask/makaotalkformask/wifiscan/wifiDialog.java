package com.MakaoTalkforMask.makaotalkformask.wifiscan;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.MakaoTalkforMask.makaotalkformask.R;

import org.greenrobot.eventbus.EventBus;

public class wifiDialog {
    private Context wcontext;

    public wifiDialog(Context wContext) {
        this.wcontext = wContext;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final String ssid) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(wcontext);
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dlg.setContentView(R.layout.wifi_dialog);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        TextView title = dlg.findViewById(R.id.title);
        Button okButton = dlg.findViewById(R.id.okButton);
        Button cancelButton = dlg.findViewById(R.id.cancelButton);
        title.setText(ssid);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시
                //ssid 전달
                Log.d("wifi","wifiDialog : " + ssid);
                EventBus.getDefault().post(new WifiData(ssid));
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
                Toast.makeText(wcontext, "설정이 완료되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });

    }

    //Event Bus
    public static class WifiData {

        public final String ssid;

        public WifiData(String ssid) {
            this.ssid = ssid;
        }
    }
}
