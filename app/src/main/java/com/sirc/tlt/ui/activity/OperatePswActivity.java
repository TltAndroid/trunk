package com.sirc.tlt.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.ui.view.ClearableEditText;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.SetTranslucentStatus;

public class OperatePswActivity extends BaseActivity {

    private String type = "";
    private static final int UPDATE_PSW = 0;
    private static final int RESET_PSW = 1;
    private ClearableEditText update_psw_edit_phone,original_psw_edit_psw,update_psw_edit_psw;

    private Button btn_operate_psw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_operate_psw);

        TemplateTitle operate_psw_title = (TemplateTitle) findViewById(R.id.operate_psw_title);
        operate_psw_title.setTitleText("密码设置");

        update_psw_edit_phone = (ClearableEditText) findViewById(R.id.update_psw_edit_phone);
        update_psw_edit_psw = (ClearableEditText) findViewById(R.id.update_psw_edit_psw);
        original_psw_edit_psw = (ClearableEditText) findViewById(R.id.original_psw_edit_psw);

        if (getIntent() != null){
            if (getIntent().getIntExtra("type",0) == UPDATE_PSW){
                type = getString(R.string.update_password);
                original_psw_edit_psw.setVisibility(View.VISIBLE);
            }else {
                type = getString(R.string.reset_password);
                original_psw_edit_psw.setVisibility(View.GONE);
            }
        }

        btn_operate_psw = (Button) findViewById(R.id.btn_operate_psw);
        btn_operate_psw.setText(type);
        operate_psw_title.setTitleText(type);
    }
}
