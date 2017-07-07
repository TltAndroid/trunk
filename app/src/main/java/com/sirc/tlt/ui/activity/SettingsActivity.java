package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.SetTranslucentStatus;

public class SettingsActivity extends BaseActivity {


    private TextView tv_update_psw,tv_reset_psw;
    private static final int UPDATE_PSW = 0;
    private static final int RESET_PSW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_settings);


        TemplateTitle setting_title = (TemplateTitle) findViewById(R.id.setting_title);
        setting_title.setTitleText("设置");

        tv_update_psw = (TextView) findViewById(R.id.tv_update_psw);
        tv_reset_psw = (TextView) findViewById(R.id.tv_reset_psw);

        tv_update_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,OperatePswActivity.class);
                intent.putExtra("type",UPDATE_PSW);
                startActivity(intent);
            }
        });

        tv_reset_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,OperatePswActivity.class);
                intent.putExtra("type",RESET_PSW);
                startActivity(intent);
            }
        });
    }

}
