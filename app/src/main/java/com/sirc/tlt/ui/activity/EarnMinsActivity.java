package com.sirc.tlt.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.utils.SetTranslucentStatus;

public class EarnMinsActivity extends BaseActivity {

    private static final String TAG = EarnMinsActivity.class.getSimpleName();

    private TextView tv_invite_friends,tv_sign_for_mins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_earn_mins);

        tv_invite_friends = (TextView) findViewById(R.id.tv_invite_friends);
        tv_invite_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EarnMinsActivity.this,InviteActivity.class);
                startActivity(intent);
            }
        });

        tv_sign_for_mins = (TextView) findViewById(R.id.tv_sign_for_mins);
        tv_sign_for_mins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EarnMinsActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}
