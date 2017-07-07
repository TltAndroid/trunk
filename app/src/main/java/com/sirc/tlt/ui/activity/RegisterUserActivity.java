package com.sirc.tlt.ui.activity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.mob.MobSDK;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.view.ClearableEditText;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import android.os.Handler.Callback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import okhttp3.Call;

public class RegisterUserActivity extends BaseActivity implements Callback{

    private String username;
    private ClearableEditText edit_register_code,edit_input_password;
    private TextView tv_show_username;
    boolean isUserPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_register_user);


        TemplateTitle title = (TemplateTitle) findViewById(R.id.register_user_title);

        edit_register_code = (ClearableEditText) findViewById(R.id.edit_register_code);
        edit_input_password = (ClearableEditText) findViewById(R.id.edit_input_password);
        tv_show_username = (TextView) findViewById(R.id.tv_show_username);

        Button btn_resigter = (Button) findViewById(R.id.btn_register_user);
        registerSDK();
        Intent data= getIntent();
        if (data != null){
            title.setTitleText(data.getStringExtra("title"));
            isUserPhone = data.getBooleanExtra("isUserPhone",true);
            username = data.getStringExtra("username");
            if (isUserPhone){
                edit_register_code.setHint(getString(R.string.input_sms_code));
            }else edit_register_code.setHint(getString(R.string.input_email_code));
            Log.i("username",username);
            tv_show_username.setText("注册账号:"+username);

        }

        btn_resigter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(edit_register_code.getText())
                        && !TextUtils.isEmpty(edit_input_password.getText()))
                {
                    if (isUserPhone){
                        SMSSDK.submitVerificationCode("86",username
                                ,edit_register_code.getText()+"");
                    }else {
                        OkHttpUtils.post()
                                .url(Config.URL_REGISTER)
                                .addParams("cmd","reg")
                                .addParams("email",username)
                                .addParams("vc",edit_register_code.getText()+"")
                                .addParams("pwd",edit_input_password.getText()+"")
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int i) {
                                        ToastUtil.showShortToast(RegisterUserActivity.this,
                                                getString(R.string.net_error));
                                    }

                                    @Override
                                    public void onResponse(String s, int i) {
                                        Result result = JSON.parseObject(s,Result.class);
                                        if (result.getErr_code() == 0){
                                            ToastUtil.showShortToast(RegisterUserActivity.this,
                                                    "注册成功");
                                            finish();
                                        }else if (result.getErr_code() == 1){
                                            ToastUtil.showShortToast(RegisterUserActivity.this,
                                                    result.getErr_msg());
                                        }
                                    }
                                });
                    }
                }else ToastUtil.showShortToast(RegisterUserActivity.this
                ,"请填写完整信息");
            }

        });

    }

    private void registerSDK() {
//        if (Config.SMS_APPKEY.equalsIgnoreCase(MobSDK.getAppkey())) {
//            Toast.makeText(this, "验证签名错误,请联系客服", Toast.LENGTH_SHORT).show();
//        }
        final Handler handler = new Handler(this);
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }


    @Override
    public boolean handleMessage(Message msg) {
        int event = msg.arg1;
        final int result = msg.arg2;
        Object data = msg.obj;
        //验证码验证成功
        if (result == SMSSDK.RESULT_COMPLETE){
                ToastUtil.showShortToast(RegisterUserActivity.this,
                        "响应成功");
            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){//发送验证码成功
                ToastUtil.showShortToast(RegisterUserActivity.this,
                        "短信验证码发送成功");
            }else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                ToastUtil.showShortToast(RegisterUserActivity.this,
                        "短信验证成功");
                OkHttpUtils.post()
                        .url(Config.URL_REGISTER)
                        .addParams("cmd","reg")
                        .addParams("mobile",username)
                        .addParams("pwd",edit_input_password.getText()+"")
                        .addParams("district","86")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                                ToastUtil.showShortToast(RegisterUserActivity.this,
                                        getString(R.string.net_error));
                            }
                            @Override
                            public void onResponse(String s, int i) {
                                Result register_result = JSON.parseObject(s,Result.class);
                                if (register_result.getErr_code() == 0){
                                    ToastUtil.showShortToast(RegisterUserActivity.this,
                                            register_result.getErr_msg());
                                    finish();
                                }else if (register_result.getErr_code() == 1){
                                    ToastUtil.showShortToast(RegisterUserActivity.this,
                                            register_result.getErr_msg());
                                }else {
                                    ToastUtil.showShortToast(RegisterUserActivity.this,
                                            "注册失败");
                                }
                                Log.i("register_result",register_result.getErr_msg());

                            }
                        });
            }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                //返回支持发送验证码的国家列表
                ToastUtil.showShortToast(RegisterUserActivity.this
                ,"国家列表");
                ArrayList<HashMap<String,Object>> list = (ArrayList<HashMap<String, Object>>) data;
                Log.i("SMSSDK返回的国家列表:",list+"");
            }
        }else {
            ToastUtil.showShortToast(RegisterUserActivity.this,
                    "注册失败："+result);
            try {
                Throwable throwable = (Throwable) data;
                throwable.printStackTrace();
                JSONObject object = new JSONObject(throwable.getMessage());
                String des = object.optString("detail");//错误描述
                int status = object.optInt("status");//错误代码
                if (status > 0 && !TextUtils.isEmpty(des)) {
                    Toast.makeText(RegisterUserActivity.this, des, Toast.LENGTH_SHORT).show();
                    Log.i("SMSSDK des:",des);
                }
            } catch (Exception e) {
                //do something
            }
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            SMSSDK.unregisterAllEventHandler();
    }
}
