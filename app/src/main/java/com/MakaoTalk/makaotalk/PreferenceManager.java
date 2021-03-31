package com.MakaoTalk.makaotalk;

import android.content.Context;
import android.content.SharedPreferences;

// 설정값 저장을 위한 클래스

public class PreferenceManager {
    public static final String PREFERENCES_NAME = "touch_count_save";
    private static final int DEFAULT_VALUE_INT = 100;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * int 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * int 값 로드
     * @param context
     * @param key
     * @return
     */

    public static int getInt(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        int value = prefs.getInt(key, DEFAULT_VALUE_INT);
        return value;
    }
}