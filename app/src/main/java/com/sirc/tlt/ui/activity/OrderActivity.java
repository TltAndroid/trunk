package com.sirc.tlt.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.sdk.app.PayTask;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.ALiPayResult;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

import static com.sirc.tlt.utils.Config.SDK_PAY_FLAG;

public class OrderActivity extends BaseActivity{


    private static final String TAG = OrderActivity.class.getCanonicalName();
    Button btn_commit_pay;
    private TextView tv_goods_type,tv_goods_name,tv_goods_count,tv_goods_total_money;
    private String type,phone;
    private int amount,isLocal,count;
    private double sale_price;
    private String pay_type;
    private RelativeLayout rl_ali_pay,rl_wx_pay;
    private ImageView img_choose_ali,img_choose_wx;

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    ALiPayResult payResult = new ALiPayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(OrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        //支付成功后，发起充值请求
                        //本机话费和流量充值
                        if (TextUtils.equals(type,Config.NATIVE_PHONE) ||
                                TextUtils.equals(type,Config.NATIVE_FLOW)){
                            initRechargeNative();
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(OrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
//                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”且result_code
//                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                        // 传入，则支付账户为该授权账户
//                        Toast.makeText(PayDemoActivity.this,
//                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
//                                .show();
//                    } else {
//                        // 其他状态值则为授权失败
//                        Toast.makeText(PayDemoActivity.this,
//                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
//
//                    }
//                    break;
//                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_order);


        //amount表示充值的面额(话费或者流量)
        Intent intent = getIntent();
        if (intent != null){
            type = intent.getStringExtra("type");
            phone =intent.getStringExtra("phone");
            amount = intent.getIntExtra("amount",0);
            isLocal = intent.getIntExtra("isLocal",2);
            //sale_price销售单价
            sale_price = intent.getDoubleExtra("sale_price",0);
            //count表示购买数量
            count = intent.getIntExtra("count",0);
        }

        initView();

    }

    private void initView(){

        tv_goods_type = (TextView) findViewById(R.id.tv_goods_type);

        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);

        tv_goods_count = (TextView) findViewById(R.id.tv_goods_count);

        tv_goods_total_money = (TextView) findViewById(R.id.tv_goods_total_money);

        if (TextUtils.equals(type,Config.NATIVE_PHONE)){
            setTextData("话费充值",sale_price+"元充值卡");
        }else if (TextUtils.equals(type,Config.NATIVE_FLOW)){
            setTextData("流量充值",sale_price+"元充值卡");
        }


        pay_type = Config.ALI_PAY;

        rl_ali_pay = (RelativeLayout) findViewById(R.id.rl_ali_pay);
        img_choose_ali = (ImageView) findViewById(R.id.img_choose_ali);

        rl_wx_pay = (RelativeLayout) findViewById(R.id.rl_wx_pay);
        img_choose_wx = (ImageView) findViewById(R.id.img_choose_wx);


        rl_ali_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPayState();
                img_choose_ali.setImageResource(R.drawable.ssdk_oks_classic_check_checked);
                pay_type = Config.ALI_PAY;

            }
        });

        rl_wx_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPayState();
                img_choose_wx.setImageResource(R.drawable.ssdk_oks_classic_check_checked);
                pay_type = Config.WX_PAY;
            }
        });

        btn_commit_pay = (Button) findViewById(R.id.btn_commit_pay);
        btn_commit_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(pay_type,Config.ALI_PAY)){
                    goALiPay();
                }else if (TextUtils.equals(pay_type,Config.WX_PAY)){
                    boolean wx_support = isWXAppInstalledAndSupported();
                    if (wx_support){
                        go_WXPay();
                    }else ToastUtil.showShortToast(OrderActivity.this,"请先安装微信客户端");
                }

            }
        });
    }

    private void goALiPay(){
                        OkHttpUtils.post()
                                .url(Config.URL_ALI_PAY)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                                Toast.makeText(OrderActivity.this
                                        , "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(final String s, int i) {
                                Log.d("","orderinfo==="+s);
                                Runnable payRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        PayTask alipay = new PayTask(OrderActivity.this);
                                        Map<String, String> result = alipay.payV2(s, true);
                                        Log.d("TAG",result.toString());
                                        Message msg = new Message();
                                        msg.what = SDK_PAY_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };

                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            }
                        });
    }


    /**
     *
     */
    private void initRechargeNative(){
        OkHttpUtils.post()
                .url(Config.URL_RECHARGE)
                .addParams("cmd","w")
                .addParams("type",type)
                .addParams("phone",phone)
                .addParams("isLocal",isLocal+"")
                .addParams("amount",amount+"")
                .addParams("user_id","1234")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(OrderActivity.this,
                                getString(R.string.net_error),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Result result = JSONObject.parseObject(s,Result.class);
                        if (result.getErr_code() == 0){
                            Toast.makeText(OrderActivity.this
                                    ,"订单提交成功,10分钟后到账",Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"result:"+result.getResult());
                        }else{
                            Toast.makeText(OrderActivity.this
                                    ,"订单提交失败，请稍后重试",Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"result:"+result.getResult());
                        }
                    }
                });
    }


    private void setTextData(String type,String name){
        tv_goods_type.setText(type);
        tv_goods_name.setText(name);
        tv_goods_count.setText(count+"");
        tv_goods_total_money.setText("合计:"+(count*sale_price)+"元");
    }

    //清除支付方式的选中状态
    private void cleanPayState(){
        img_choose_ali.setImageResource(R.drawable.ssdk_oks_classic_check_default);
        img_choose_wx.setImageResource(R.drawable.ssdk_oks_classic_check_default);
    }

    private void  go_WXPay(){
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Config.WX_APP_PAY_ID, false);
        api.registerApp(Config.WX_APP_PAY_ID);

        OkHttpUtils.post()
                .url(Config.URL_WX_PAY)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.showShortToast(OrderActivity.this,
                                getString(R.string.net_error));
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Result result = JSONObject.parseObject(s,Result.class);
                        if (result.getErr_code() == 0){
                            String msg  = result.getErr_msg();
                            Log.i("微信返回结果:",msg);
                            if (!TextUtils.isEmpty(msg)){
                                Map<String, Object> map = JSON.parseObject(
                                        msg,new TypeReference<Map<String, Object>>(){} );
                                Log.i("微信map",map.toString());
                                PayReq request = new PayReq();
                                request.appId = map.get("appid").toString();
                                request.partnerId = map.get("partnerid").toString();
                                request.prepayId= map.get("prepayid").toString();
                                request.packageValue = map.get("package").toString();
                                request.nonceStr= map.get("noncestr").toString();
                                request.timeStamp= map.get("timestamp").toString();
                                request.sign= map.get("sign").toString();
                                api.sendReq(request);
                            }else ToastUtil.showShortToast(OrderActivity.this,
                                    "微信支付请求数据为空");
                        }else {
                            ToastUtil.showShortToast(OrderActivity.this,
                                    "微信支付请求失败");
                        }

                    }
                });
    }

    /**
     *
     * 判断是否安装了微信客户端
     * @return
     */

    private boolean isWXAppInstalledAndSupported() {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        msgApi.registerApp(Config.WX_APP_PAY_ID);

        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();

        return sIsWXAppInstalledAndSupported;
    }
}
