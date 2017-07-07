package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.feiyucloud.sdk.FYClient;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.ui.view.CustomProgressDialog;
import com.sirc.tlt.utils.ActivityCollector;
import com.sirc.tlt.utils.CacheDataManagerUtil;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.SetTranslucentStatus;

public class MoreSettingActivity extends BaseActivity implements View.OnClickListener{



    private Dialog dialog;

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {

                case 0:
                    if (dialog != null ){
                        dialog.dismiss();
                    }
                    ToastUtil.showShortToast(MoreSettingActivity.this,"清理完成");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_more_setting);

        TextView tv_about_us = (TextView) findViewById(R.id.tv_about_us);
        tv_about_us.setOnClickListener(this);


        TextView tv_clear_cache = (TextView) findViewById(R.id.tv_clear_cache);
        tv_clear_cache.setOnClickListener(this);

        TextView tv_law_declare = (TextView) findViewById(R.id.tv_law_declare);
        tv_law_declare.setOnClickListener(this);


        Button btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_about_us:
                navToActivity(AboutUsActivity.class);
                break;
            case R.id.btn_exit :
                CommonUtil.saveLoginUser(MoreSettingActivity.this,"");
                CommonUtil.saveLoginpwd(MoreSettingActivity.this,"");
                CommonUtil.saveIsLogin(MoreSettingActivity.this,false);
                CommonUtil.setFyAccountId(MoreSettingActivity.this,"");
                CommonUtil.setFyAccountPwd(MoreSettingActivity.this,"");
                ActivityCollector.finishAll();
                Intent intent = new Intent(MoreSettingActivity.this,LoginActivity.class);
                intent.putExtra("canBack",false);
                startActivity(intent);
                FYClient.instance().disconnect();
                break;

            case R.id.tv_clear_cache :
                dialog = CustomProgressDialog.createCustomDialog(MoreSettingActivity.this,
                        "正在清理缓存...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CacheDataManagerUtil.clearAllCache(MoreSettingActivity.this);
                            Thread.sleep(3000);
                            if (CacheDataManagerUtil.getTotalCacheSize(MoreSettingActivity.this).startsWith("0")) {
                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception e) {
                            return;

                        }
                    }
                }).start();
                break;

            case R.id.tv_law_declare:
                Intent intent1 = new Intent(MoreSettingActivity.this,WebViewActivity.class);
                intent1.putExtra("title", getString(R.string.law_declaration));
                intent1.putExtra("url", "file:///android_asset/法律声明.html");
                startActivity(intent1);
                break;
        }

    }


    private void  navToActivity(Class<? extends Activity> activity){
        Intent intent = new Intent(MoreSettingActivity.this,
                activity);
        startActivity(intent);
    }
}
