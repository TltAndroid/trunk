package com.sirc.tlt.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.permission.PermissionRequest;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

public class SplashActivity extends BaseActivity {


    private MyCountDownTimer mc;
    private TextView tv_count_down;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        tv_count_down = (TextView) findViewById(R.id.tv_count_down);
        tv_count_down.setText("5 S");
        mc = new MyCountDownTimer(5000, 1000);
        mc.start();

//        final SharedPreferences getPrefs = PreferenceManager
//                .getDefaultSharedPreferences(getBaseContext());
//        //  Create a new boolean and preference and set it to true
//        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
//        //  If the activity has never started before...
//        if (isFirstStart) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                     //  Launch app intro
//                    Intent i = new Intent(SplashActivity.this, IntroActivity.class);
//                    startActivity(i);
//
//                    //  Make a new preferences editor
//                    SharedPreferences.Editor e = getPrefs.edit();
//
//                    //  Edit preference to make it false because we don't want this to run again
//                    e.putBoolean("firstStart", false);
//
//                    //  Apply changes
//                    e.apply();
//
//                    finish();
//                }
//            },2000);
//        }else{
//            tv_count_down = (TextView) findViewById(R.id.tv_count_down);
//            tv_count_down.setText("5 S");
//            mc = new MyCountDownTimer(5000, 1000);
//            mc.start();
//        }
    }

    /**
     * 继承 CountDownTimer 防范
     *
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer extends CountDownTimer {
        /**
         *
         * @param millisInFuture
         *      表示以毫秒为单位 倒计时的总数
         *
         *      例如 millisInFuture=1000 表示1秒
         *
         * @param countDownInterval
         *      表示 间隔 多少微秒 调用一次 onTick 方法
         *
         *      例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            tv_count_down.setText("正在跳转");
            final SharedPreferences getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            //  Create a new boolean and preference and set it to true
            boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
            //  If the activity has never started before...
            if (isFirstStart) {
                Intent i = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(i);

                //  Make a new preferences editor
                SharedPreferences.Editor e = getPrefs.edit();

                //  Edit preference to make it false because we don't want this to run again
                e.putBoolean("firstStart", false);

                //  Apply changes
                e.apply();
            }else{
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
            }
            finish();
        }

        public void onTick(long millisUntilFinished) {
            tv_count_down.setText( millisUntilFinished / 1000 +" S");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

        }

    }
}
