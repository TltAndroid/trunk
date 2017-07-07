package com.sirc.tlt.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.feiyucloud.sdk.FYClient;
import com.mob.MobSDK;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.ui.activity.LoginActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 *
 * 通用工具类
 * Created by Hooliganiam on 17/5/5.
 */

public class CommonUtil {

    private static final String PREF_NAME = "tlw_setting";
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
        return getPreference(context).getString(Config.KEY_LOGIN_USER, "");
    }

    public static void saveLoginUser(Context context, String number) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(Config.KEY_LOGIN_USER, number);
        editor.commit();
    }

    public static String getLoginPwd(Context context) {
        return getPreference(context).getString(Config.KEY_LOGIN_PWD, "");
    }

    public static void saveLoginpwd(Context context, String number) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(Config.KEY_LOGIN_PWD, number);
        editor.commit();
    }

    public static void saveIsShowNumber(Context context, boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(Config.KEY_IS_SHOW_NUMBER, flag);
        editor.commit();
    }

    public static boolean getIsShowNumber(Context context) {
        return getPreference(context).getBoolean(Config.KEY_IS_SHOW_NUMBER, false);
    }

    public static String getIsRemindTime(Context context) {
        return getPreference(context).getString(Config.KEY_IS_REMIND_TIME, null);
    }

    public static void saveIsRemindTime(Context context, String number) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(Config.KEY_IS_REMIND_TIME, number);
        editor.commit();
    }

    public static boolean getIsLogin(Context context) {
        return getPreference(context).getBoolean(Config.KEY_IS_LOGIN, false);
    }

    public static void saveIsLogin(Context context, boolean result) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(Config.KEY_IS_LOGIN, result);
        editor.commit();
    }

    public static String getFyAccountPwd(Context context) {
        return getPreference(context).getString(Config.KEY_FyAccountPwd, "");
    }

    public static void setFyAccountPwd(Context context, String result) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(Config.KEY_FyAccountPwd, result);
        editor.commit();
    }

    public static String getFyAccountId(Context context) {
        return getPreference(context).getString(Config.KEY_FyAccountId,"");
    }

    public static void setFyAccountId(Context context, String result) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(Config.KEY_FyAccountId, result);
        editor.commit();
    }






    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param millisecond
     * @return
     */
    public static String getDateTimeFromMillisecond(Long millisecond){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    /**
     * 将时间转化成毫秒
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static Long timeStrToSecond(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long second = format.parse(time).getTime();
            return second;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1l;
    }

    public static void showShare(Context context, String title, String titleUrl, String text, String url) {
        MobSDK.init(context, Config.MOB_APPKEY, Config.MOB_AppSecret);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(titleUrl);
        // text是分享文本，所有平台都需要这个字段
        //oks.setText(text);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);

        // 启动分享GUI
        oks.show(context);
    }

    /**
     * 获取状态栏高度,在页面还没有显示出来之前
     *
     * @param a
     * @return
     */
    public static int getStateBarHeight(Activity a) {
        int result = 0;
        int resourceId = a.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = a.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 隐藏软键盘
     * @param activity
     */
    public static void hideSoftkeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {

        }
    }


    /**
     * 跳转登陆Activity
     *
     */
    public static void startToLoginActivity(Activity activity){
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    /**
     *
     * 初始化飞语，连接云平台
     */
    public static void  initFy(Context context){
        FYClient.instance().connect(Config.APPID_FY,Config.APPTOKEN_FY
                ,CommonUtil.getFyAccountId(context),
                CommonUtil.getFyAccountPwd(context));
        Log.i("feiyu accountid",CommonUtil.getFyAccountId(context));
        Log.i("feiyu psw",CommonUtil.getFyAccountPwd(context));
        Log.i("feiyu state",FYClient.isConnected()+"");
    }
}
