package com.sirc.tlt.feiyucloud.cfg;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingConfig {
    private static final String PREF_NAME = "feiyucloud_example_setting";
    private static SharedPreferences preferences;

    private static SharedPreferences getPreference(Context context) {
        if (preferences == null) {
            preferences = context.getApplicationContext().getSharedPreferences(
                    PREF_NAME, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreference(context).edit();
    }

    // --------------------------------------------------------------------

    public static String getLoginUser(Context context) {
        return getPreference(context).getString(Const.KEY_LOGIN_USER, null);
    }

    public static void saveLoginUser(Context context, String number) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(Const.KEY_LOGIN_USER, number);
        editor.commit();
    }

    public static String getLoginPwd(Context context) {
        return getPreference(context).getString(Const.KEY_LOGIN_PWD, null);
    }

    public static void saveLoginpwd(Context context, String number) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(Const.KEY_LOGIN_PWD, number);
        editor.commit();
    }

    public static void saveIsShowNumber(Context context, boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(Const.KEY_IS_SHOW_NUMBER, flag);
        editor.commit();
    }

    public static boolean getIsShowNumber(Context context) {
        return getPreference(context).getBoolean(Const.KEY_IS_SHOW_NUMBER, false);
    }

    public static void saveIsRecord(Context context, boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(Const.KEY_IS_RECORD, flag);
        editor.commit();
    }

    public static boolean getIsRecord(Context context) {
        return getPreference(context).getBoolean(Const.KEY_IS_RECORD, false);
    }

}
