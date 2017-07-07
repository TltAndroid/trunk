package com.sirc.tlt.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sirc.tlt.R;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.ui.activity.OrderActivity;
import com.sirc.tlt.utils.Config;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Config.WX_APP_PAY_ID);

        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.i("微信支付结果：",resp.errCode+"---"+resp.errStr);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("微信支付结果" +String.valueOf(resp.errCode)+"----"+resp.errStr);
			builder.show();
		}
		int errCode = resp.errCode;
		switch (errCode){
			case 0 :
				ToastUtil.showShortToast(WXPayEntryActivity.this,"微信支付失败,请联系客服");
				break;
			case -1 :
				ToastUtil.showShortToast(WXPayEntryActivity.this,"微信支付失败,请联系客服");
				break;
			case -2 :
				ToastUtil.showShortToast(WXPayEntryActivity.this,"用户取消支付");
				break;
		}
	}


}