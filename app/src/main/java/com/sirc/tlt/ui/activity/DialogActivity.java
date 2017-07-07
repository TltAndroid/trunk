package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.sirc.tlt.base.BaseActivity;


public class DialogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        //显示对话框
        new AlertDialog.Builder(DialogActivity.this).
                setTitle("签到提醒").//设置标题
                setMessage("该去签到赚分钟数了！").//设置内容
                setPositiveButton("知道了", new DialogInterface.OnClickListener() {//设置按钮
            public void onClick(DialogInterface dialog, int which) {
                DialogActivity.this.finish();//关闭Activity
            }
        }).create().show();


        setFinishOnTouchOutside(false);


    }


}
