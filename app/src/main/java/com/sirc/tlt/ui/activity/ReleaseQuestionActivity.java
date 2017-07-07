package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.view.ClearableEditText;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class ReleaseQuestionActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = ReleaseQuestionActivity.class.getSimpleName();

    private ClearableEditText edit_question_type,edit_question_title,edit_question_describe;
    private Button btn_submit_question;

    private static final int CHOOSE_TYPE = 00001;
    String lable="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_release_question);

        TemplateTitle title = (TemplateTitle) findViewById(R.id.release_question_title);

        edit_question_type = (ClearableEditText) findViewById(R.id.edit_question_type);
        edit_question_type.setOnClickListener(this);

        edit_question_title = (ClearableEditText) findViewById(R.id.edit_question_title);

        edit_question_describe = (ClearableEditText) findViewById(R.id.edit_question_describe);

        btn_submit_question = (Button) findViewById(R.id.btn_submit_question);
        btn_submit_question.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_question_type:
                Intent intent = new Intent(ReleaseQuestionActivity.this,
                        ChooseTypeActivity.class);
                startActivityForResult(intent,CHOOSE_TYPE);
                break;

            case R.id.btn_submit_question:
                if (TextUtils.isEmpty(edit_question_title.getText())){
                    Toast.makeText(ReleaseQuestionActivity.this,
                            "提问的内容不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    OkHttpUtils.post()
                            .url(Config.URL_Questions)
                            .addParams("cmd","w")
                            .addParams("user_id","111")
                            .addParams("lable",lable)
                            .addParams("title",edit_question_title.getText().toString())
                            .addParams("content",edit_question_describe.getText().toString())
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int i) {
                                    Toast.makeText(ReleaseQuestionActivity.this,
                                            getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int i) {
                                    Result result = JSON.parseObject(response, Result.class);
                                    if (result.getErr_code() == 0) {
                                        Toast.makeText(ReleaseQuestionActivity.this, "发表成功！", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        Toast.makeText(ReleaseQuestionActivity.this, "发表失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (resultCode == RESULT_OK){
                if (requestCode == CHOOSE_TYPE){
                    lable = data.getStringExtra("type");
                    edit_question_type.setText(lable);
                }
            }
        }

    }
}
