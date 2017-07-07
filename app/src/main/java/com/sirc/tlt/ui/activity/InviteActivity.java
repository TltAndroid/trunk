package com.sirc.tlt.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.SetTranslucentStatus;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_invite);

        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_invite_friends})
    public void showShare(View view) {
        CommonUtil.showShare(this, "邀请好友下载台陆通APP", Config.URL_QR_CODE, "邀请好友下载台陆通APP", Config.URL_QR_CODE);
    }
}
