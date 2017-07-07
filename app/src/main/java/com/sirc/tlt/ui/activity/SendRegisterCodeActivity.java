package com.sirc.tlt.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.mob.MobSDK;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.view.ActionSheetDialog;
import com.sirc.tlt.ui.view.ClearableEditText;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.PermissionsChecker;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import cn.smssdk.utils.SMSLog;
import okhttp3.Call;


/**
 * 用户注册界面
 *
 */

public class SendRegisterCodeActivity extends BaseActivity implements View.OnClickListener{

    private TemplateTitle register_user_title;
    private TextView tv_law_register,tv_not_have_phone,tv_register_area;
    private Button btn_send_register_code;
    private ClearableEditText edit_register;
    private boolean isUsePhone;
    private String title;
    //是否拨打大陆号码
    private boolean isLandPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_send_register_code);

//        Glide.with(this)
//                .load(R.drawable.img_login_backgrpund)
//                .fitCenter()
//                .into((ImageView) findViewById(R.id.img_background));

        //当前显示用手机号注册界面
        isUsePhone = true;

        //显示大陆手机号
        isLandPhone = true;

        title = getString(R.string.sms_registe);

        register_user_title = (TemplateTitle) findViewById(R.id.send_register_code_title);

        edit_register = (ClearableEditText) findViewById(R.id.edit_register);

        btn_send_register_code = (Button) findViewById(R.id.btn_send_register_code);

        tv_law_register = (TextView) findViewById(R.id.tv_law_register);

        tv_not_have_phone = (TextView) findViewById(R.id.tv_not_have_phone);

        tv_register_area = (TextView) findViewById(R.id.tv_register_area);

        tv_register_area.setOnClickListener(this);

        tv_not_have_phone.setOnClickListener(this);

        btn_send_register_code.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_not_have_phone:
                if (isUsePhone){
                    //跳转使用邮箱注册
                    register_user_title.setTitleText(getString(R.string.email_registe));
                    title = getString(R.string.email_registe);
                    edit_register.setHint(getString(R.string.input_email));
                    btn_send_register_code.setText(getString(R.string.send_email_code));
                    tv_not_have_phone.setText(getString(R.string.sms_registe));
                    isUsePhone = false;
                }else{
                    //跳转手机注册
                    title = getString(R.string.sms_registe);
                    register_user_title.setTitleText(getString(R.string.sms_registe));
                    edit_register.setHint(getString(R.string.input_phone));
                    btn_send_register_code.setText(getString(R.string.send_sms_code));
                    tv_not_have_phone.setText(getString(R.string.email_registe));
                    isUsePhone = true;
                }
                break;

            case R.id.btn_send_register_code:
                if (TextUtils.isEmpty(edit_register.getText())){
                    ToastUtil.showShortToast(SendRegisterCodeActivity.this,
                            "注册信息不能为空");
                }else{
                    if (TextUtils.equals(getString(R.string.sms_registe),title)){
                        navToRegister();
                    }else if (TextUtils.equals(getString(R.string.email_registe),title)){
                        if (isValidEmail(edit_register.getText().toString())){
                            navToRegister();
                        }else ToastUtil.showShortToast(SendRegisterCodeActivity.this,
                                "请输入正确的邮箱");
                    }

                }
                break;
            case R.id.tv_register_area:
                chooseArea();
                break;
        }

    }

    /**
     * 验证邮箱格式
     *
     * @param mail
     * @return
     */
    private boolean isValidEmail(String mail) {
        Pattern pattern = Pattern
                .compile("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
        Matcher mc = pattern.matcher(mail);
        return mc.matches();
    }

    /**
     * 跳转注册界面
     *
     */
    private void navToRegister(){

        if (isUsePhone){
            String area = "+86";
            if (isLandPhone){
                area = "+86";
            }else area = "+886";
            MobSDK.init(SendRegisterCodeActivity.this, Config.MOB_APPKEY, Config.MOB_AppSecret);
            SMSSDK.getVerificationCode(area, edit_register.getText().toString());
            SMSSDK.getSupportedCountries();
            Intent intent = new Intent(SendRegisterCodeActivity.this,
                    RegisterUserActivity.class);
            intent.putExtra("title",title);
            intent.putExtra("isUserPhone",isUsePhone);
            intent.putExtra("username",edit_register.getText()+"");
            startActivity(intent);
            finish();
        }else{
            OkHttpUtils.get()
                    .url(Config.URL_SEND_EMAIL_CODE)
                    .addParams("cmd","vc")
                    .addParams("email",edit_register.getText().toString())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            ToastUtil.showShortToast(SendRegisterCodeActivity.this,
                                    getString(R.string.net_error));
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Result result = JSON.parseObject(s,Result.class);
                            if (result.getErr_code()== 0){
                                ToastUtil.showShortToast(SendRegisterCodeActivity.this,
                                        result.getErr_msg());
                                Intent intent = new Intent(SendRegisterCodeActivity.this,
                                        RegisterUserActivity.class);
                                intent.putExtra("title",title);
                                intent.putExtra("isUserPhone",isUsePhone);
                                intent.putExtra("username",edit_register.getText()+"");
                                Log.i("username",edit_register.getText()+"");
                                startActivity(intent);
                                finish();
                            }else ToastUtil.showShortToast(SendRegisterCodeActivity.this,
                                    result.getErr_msg()+",请联系客服");
                        }
                    });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

        }
    }

    /**
     * 选择局域拨打
     */
    private void chooseArea(){
        new ActionSheetDialog(SendRegisterCodeActivity.this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(getString(R.string.tv_choose_cl), ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                tv_register_area.setText(R.string.tv_choose_cl);
                                isLandPhone = true;
                            }
                        })
                .addSheetItem(getString(R.string.tv_choose_tw), ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                tv_register_area.setText(R.string.tv_choose_tw);
                                isLandPhone = false;
                            }
                        }).show();
    }

}
