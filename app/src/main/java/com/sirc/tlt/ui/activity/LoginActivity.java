package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.feiyucloud.sdk.FYClient;
import com.feiyucloud.sdk.FYClientListener;
import com.feiyucloud.sdk.FYError;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.Dump;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.Resulit;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.view.ClearableEditText;
import com.sirc.tlt.ui.view.SearchEditText;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private ImageView img_logo;
    private ClearableEditText edit_phone,edit_psw;
    private Button btn_login;
    private TextView tv_login_register,tv_forget_psw,tv_login_by_email;
    private boolean islogin = true;
    private TemplateTitle title;

    private String key_login_type_name = "mobile";
    private String login_type = "邮箱登陆";

    boolean Canback = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_login);

        title = (TemplateTitle) findViewById(R.id.login_title);

        Intent intent = getIntent();
        if (intent != null){
            Canback = intent.getBooleanExtra("canBack",true);
        }
        ImageView imageView = (ImageView) findViewById(R.id.img_back);
        if (!Canback){
            imageView.setVisibility(View.GONE);
        }else {
            imageView.setVisibility(View.VISIBLE);
        }
        initView();
    }

    private void initView(){

//        Glide.with(LoginActivity.this)
//                .load(R.drawable.img_login_backgrpund)
//                .fitCenter()
//                .into((ImageView) findViewById(R.id.img_background));

        img_logo = (ImageView) findViewById(R.id.img_logo);
        edit_phone = (ClearableEditText) findViewById(R.id.edit_phone);
        edit_psw = (ClearableEditText) findViewById(R.id.edit_psw);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_login_register = (TextView) findViewById(R.id.tv_login_register);
        tv_forget_psw = (TextView) findViewById(R.id.tv_forget_psw);


        tv_login_register.setText(R.string.go_to_register);
        btn_login.getBackground().setAlpha(100);

//        Glide.with(LoginActivity.this).load(R.drawable.img_logo).into(img_logo);
    //  img_logo.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_login_register.setOnClickListener(this);
        tv_forget_psw.setOnClickListener(this);

        tv_login_by_email = (TextView) findViewById(R.id.tv_login_by_email);
        tv_login_by_email.setText(login_type);
        tv_login_by_email.setOnClickListener(this);


        edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(edit_psw.getText()) && !TextUtils.isEmpty(edit_phone.getText())){
                    btn_login.setBackgroundColor(getResources().getColor(R.color.background_blue));
                }else{
                    btn_login.setBackgroundColor(getResources().getColor(R.color.background_gray3));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_psw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(edit_psw.getText()) && !TextUtils.isEmpty(edit_phone.getText())){
                    btn_login.setBackgroundColor(getResources().getColor(R.color.background_blue));
                }else{
                    btn_login.setBackgroundColor(getResources().getColor(R.color.background_gray3));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                if (!TextUtils.isEmpty(edit_psw.getText()) && !TextUtils.isEmpty(edit_phone.getText())
                        ){
                    OkHttpUtils.post()
                            .url(Config.URL_USER_LOGIN)
                            .addParams(key_login_type_name,edit_phone.getText()+"")
                            .addParams("pwd",edit_psw.getText()+"")
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int i) {
                                    ToastUtil.showShortToast(LoginActivity.this,
                                            getString(R.string.net_error));
                                }

                                @Override
                                public void onResponse(String s, int i) {
                                    Result result = JSON.parseObject(s, Result.class);
                                    if (result.getErr_code() == 0){
                                        ToastUtil.showShortToast(LoginActivity.this,
                                                result.getErr_msg());
                                        CommonUtil.saveLoginUser(LoginActivity.this,
                                                edit_phone.getText()+"");
                                        CommonUtil.saveLoginpwd(LoginActivity.this,
                                                edit_psw.getText()+"");
                                        CommonUtil.saveIsLogin(LoginActivity.this,
                                                true);
                                        CommonUtil.setFyAccountId(LoginActivity.this,result.getFyAccountId());
                                        CommonUtil.setFyAccountPwd(LoginActivity.this,result.getFyAccountPwd());
                                        Config.FyPhone_district = result.getDistrict();
                                        Log.i("feiyu 登陆账户",CommonUtil.getFyAccountId(LoginActivity.this));
                                        Log.i("feiyu 登陆密码",CommonUtil.getFyAccountPwd(LoginActivity.this));
                                        CommonUtil.initFy(LoginActivity.this);
                                        FYClient.addListener(mClientListener);
                                        if (Canback){
                                            finish();
                                        }else {
                                            Intent intent  = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }else if (result.getErr_code() != 0){
                                        ToastUtil.showShortToast(LoginActivity.this,
                                                result.getErr_msg());
                                    }

                                }
                            });
                }

                break;
            case R.id.tv_login_register:
//                if (islogin){
//                    tv_login_register.setText(R.string.go_to_login);
//                    islogin = false;
//                    btn_login.setText(getString(R.string.register));
//                    tv_forget_psw.setVisibility(View.GONE);
//                    title.setTitleText(getString(R.string.register));
//                }else{
//                    tv_login_register.setText(R.string.go_to_register);
//                    islogin = true;
//                    btn_login.setText(getString(R.string.login));
//                    tv_forget_psw.setVisibility(View.VISIBLE);
//                    title.setTitleText(getString(R.string.login));
//                }
                Intent intent = new Intent(LoginActivity.this,
                        SendRegisterCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget_psw:

                break;

            case R.id.tv_login_by_email:
                if (TextUtils.equals("邮箱登陆",login_type)){
                    login_type = "手机号登陆";
                    tv_login_by_email.setText(login_type);
                    key_login_type_name = "mobile";
                    SpannableString s = new SpannableString(getString(R.string.input_email));//这里输入自己想要的提示文字
                    edit_phone.setHint(s);
                }else if(TextUtils.equals("手机号登陆",login_type)){
                    login_type = "邮箱登陆";
                    tv_login_by_email.setText(login_type);
                    key_login_type_name = "email";
                    SpannableString s = new SpannableString(getString(R.string.input_phone));//这里输入自己想要的提示文字
                    edit_phone.setHint(s);
                }
                break;
        }
    }

    private FYClientListener mClientListener = new FYClientListener() {

        @Override
        public void onConnectionSuccessful() {
            Log.i("feiyu connect","successful");
            Dump.d("FYCloud connect successful");

        }

        @Override
        public void onConnectionFailed(FYError fyError) {
            Dump.d("FYCloud connect failed:" + fyError);
            Log.i("feiyu connect failed",fyError.toString());
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FYClient.isConnected()) {
            Dump.d("disconnect FeiyuClient");
            FYClient.instance().disconnect();
        } else {
            Dump.d("feiyu already disconnected.");
        }
    }
}
