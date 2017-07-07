package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.ui.view.ClearableEditText;
import com.sirc.tlt.utils.SetTranslucentStatus;

public class SuggestActivity extends BaseActivity {

    private Button btn_submit_suggest;
    private ClearableEditText edit_suggest, edit_contact_suggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_suggest);

        edit_suggest = (ClearableEditText) findViewById(R.id.edit_suggest);
        edit_contact_suggest = (ClearableEditText) findViewById(R.id.edit_contact_suggest);

        btn_submit_suggest = (Button) findViewById(R.id.btn_submit_suggest);

        btn_submit_suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edit_suggest.getText()) ||
                        TextUtils.isEmpty(edit_contact_suggest.getText())) {
                    Toast.makeText(SuggestActivity.this, getString(R.string.toast_suggest), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SuggestActivity.this, getString(R.string.toast_suggest_submit), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
