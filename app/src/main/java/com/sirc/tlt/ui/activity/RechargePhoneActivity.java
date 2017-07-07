package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.RechargePhoneAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.RechargePhoneModel;
import com.sirc.tlt.model.Resulit;
import com.sirc.tlt.model.TlwServiceModel;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.view.CustomProgressDialog;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DensityUtil;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.sirc.tlt.utils.SpaceItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 手机话费流量充值
 */

public class RechargePhoneActivity extends BaseActivity {

    private static final String TAG = RechargePhoneActivity.class.getSimpleName();
    private RecyclerView gridView_phone,gridView_flow;
    private EditText input_phone;
    private TextView tv_phone_belong;

    private String phone;
    private Button btn_recharge;
    private String type = "";
    private RadioButton rb_phone,rb_flow;

    private List<RechargePhoneModel> phone_list;
    private List<RechargePhoneModel> flow_list;
    private Resulit resulit;
    Dialog dialog;

    private RechargePhoneAdapter phone_adapter;
    private RechargePhoneAdapter flow_adapter;

    private RechargePhoneModel rechargeModel;
    private View lastView;

    //充值面值
    private int amount;

    private static final int GO_PAY = 100;


    private Handler handler = new Handler();

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {

            initData();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus status = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_recharge_phone);

        initView();

    }

    private void initView(){
        gridView_phone = (RecyclerView) findViewById(R.id.recyclerView_phone);

        gridView_flow = (RecyclerView) findViewById(R.id.recyclerView_flow);

        input_phone = (EditText) findViewById(R.id.edit_input_phone);

        input_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(delayRun!=null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                phone = s.toString();
                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun,800);
            }
        });

        input_phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    initData();
                    CommonUtil.hideSoftkeyboard(RechargePhoneActivity.this);
                    return true;
                }
                return false;
        }
        });

        tv_phone_belong = (TextView) findViewById(R.id.tv_phone_belong);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        rb_phone = (RadioButton) findViewById(R.id.rb_recharge_phone);
        rb_flow = (RadioButton) findViewById(R.id.rb_recharge_flow);
        type = "话费";
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                if (!TextUtils.isEmpty(phone)){
                if (checkedId == rb_phone.getId()){
                    gridView_phone.setVisibility(View.VISIBLE);
                    gridView_flow.setVisibility(View.GONE);
                    type = "话费";

                }
                if (checkedId == rb_flow.getId()){
                    gridView_phone.setVisibility(View.GONE);
                    gridView_flow.setVisibility(View.VISIBLE);
                    type = "流量";
                }

//                }else
//                    Toast.makeText(RechargePhoneActivity.this,
//                            "请先输入手机号", Toast.LENGTH_SHORT).show();

            }
        });

        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(phone)&&CommonUtil.isChinaPhoneLegal(phone))
                {
                    if (rechargeModel!=null){
                        Intent intent = new Intent(RechargePhoneActivity.this,OrderActivity.class);
                        intent.putExtra("type",type);
                        intent.putExtra("phone",phone);
                        intent.putExtra("amount",rechargeModel.getAmount());
                        intent.putExtra("isLocal",2);
                        intent.putExtra("sale_price",rechargeModel.getSale_price());
                        intent.putExtra("count",1);
                        startActivityForResult(intent,GO_PAY);
//                        initRechargeNative();
                    }else {
                        Toast.makeText(RechargePhoneActivity.this,
                                "请选择充值面额", Toast.LENGTH_SHORT).show();
                        Log.i(TAG,"请选择充值面额");
                    }
                }else
                {Toast.makeText(RechargePhoneActivity.this,
                            "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"请输入正确的手机号");}
            }
        });



    }

    /**
     *话费充值模块
     */
    private void getRechargePhone(){

        if (phone_list != null && phone_list.size()>0){

        //GridLayout 3列
        GridLayoutManager mgr=new GridLayoutManager(this,3);
        gridView_phone.setLayoutManager(mgr);
            phone_adapter = new RechargePhoneAdapter(phone_list,"话费");
            phone_adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        int spacingInPixels = DensityUtil.px2dip(RechargePhoneActivity.this,10);
        gridView_phone.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        gridView_phone.setAdapter(phone_adapter);
            phone_adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                    rechargeModel = phone_list.get(i);
                    amount = rechargeModel.getAmount();
//                    TextView tv_amount = (TextView) view.findViewById(R.id.tv_orignal_price);
//                    TextView tv_sale = (TextView) view.findViewById(R.id.tv_sale_price);
//                    if (lastView != null) {
//                        lastView.setBackgroundResource(R.drawable.item_view_border);
//                        TextView last_tv_amount = (TextView) lastView.findViewById(R.id.tv_orignal_price);
//                        TextView last_tv_sale = (TextView) lastView.findViewById(R.id.tv_sale_price);
//                        last_tv_amount.setTextColor(getResources().getColor(R.color.text_black));
//                        last_tv_sale.setTextColor(getResources().getColor(R.color.text_black));
//                    }
//                    lastView = view;
//                    tv_amount.setTextColor(getResources().getColor(R.color.white));
//                    tv_sale.setTextColor(getResources().getColor(R.color.white));
//                    view.setBackgroundResource(R.drawable.item_view_border_pressed);
//                    view.setBackgroundResource(R.drawable.item_view_border_pressed);
                    setClickBackground(view);
                }
            });
        }
    }

    /**
     *流量充值模块
     */
    private void getRechargeFlow(){
//        final List<RechargePhoneModel> list = new ArrayList<>();
//        for (int i=0;i<8;i++){
//            RechargePhoneModel model = new RechargePhoneModel();
//            model.setOrignal_price(50);
//            model.setSale_price(100);
//            model.setOn_sale_price(89);
//            model.setIs_on_sale(false);
//            list.add(model);
//        }
        if (flow_list != null && flow_list.size()>0){
            //GridLayout 3列
            GridLayoutManager mgr=new GridLayoutManager(this,3);
            gridView_flow.setLayoutManager(mgr);
            flow_adapter = new RechargePhoneAdapter(flow_list,"流量");
            flow_adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);

            int spacingInPixels = DensityUtil.px2dip(RechargePhoneActivity.this,20);
            gridView_flow.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
            gridView_flow.setAdapter(flow_adapter);
            flow_adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                    rechargeModel = flow_list.get(i);
                    amount = rechargeModel.getFlow();
                    setClickBackground(view);
                }
            });
        }
    }

    private void initData(){
        if (!TextUtils.isEmpty(input_phone.getText())){
            if (CommonUtil.isChinaPhoneLegal(input_phone.getText().toString())){
                dialog = CustomProgressDialog.createCustomDialog(RechargePhoneActivity.this,
                        "请稍等...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                OkHttpUtils.get()
                        .url(Config.URL_RECHARGE)
                        .addParams("cmd","r")
                        .addParams("phone",phone)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                                Toast.makeText(RechargePhoneActivity.this,
                                        getString(R.string.net_error),
                                        Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }

                            @Override
                            public void onResponse(String response, int i) {
                                JSONObject json = JSON.parseObject(response);
                                phone_list = JSON.parseObject(json.get("phone").toString(), new TypeReference<List<RechargePhoneModel>>() {
                                });
                                resulit = JSON.parseObject(json.get("resulit").toString(),Resulit.class);
                                flow_list = JSON.parseObject(json.get("flow").toString(), new TypeReference<List<RechargePhoneModel>>() {
                                });
                                if (resulit== null){
                                    tv_phone_belong.setText("手机号不正确");
                                }else
                                    tv_phone_belong.setText(resulit.getCompany()+" "+resulit.getProvince()
                                            +resulit.getCity());
                                getRechargePhone();
                                getRechargeFlow();

                                dialog.cancel();
                            }
                        });

            }else {Toast.makeText(RechargePhoneActivity.this, "请输入正确的手机号",
                    Toast.LENGTH_SHORT).show();
                tv_phone_belong.setText("手机号格式不正确");
            }
            CommonUtil.hideSoftkeyboard(RechargePhoneActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (resultCode == RESULT_OK){
                if (requestCode == GO_PAY){

                }
            }
        }

    }

    private void setClickBackground(View view){
        TextView tv_amount = (TextView) view.findViewById(R.id.tv_orignal_price);
        TextView tv_sale = (TextView) view.findViewById(R.id.tv_sale_price);
        if (lastView != null) {
            lastView.setBackgroundResource(R.drawable.item_view_border);
            TextView last_tv_amount = (TextView) lastView.findViewById(R.id.tv_orignal_price);
            TextView last_tv_sale = (TextView) lastView.findViewById(R.id.tv_sale_price);
            last_tv_amount.setTextColor(getResources().getColor(R.color.text_black));
            last_tv_sale.setTextColor(getResources().getColor(R.color.text_black));
        }
        lastView = view;
        tv_amount.setTextColor(getResources().getColor(R.color.white));
        tv_sale.setTextColor(getResources().getColor(R.color.white));
        view.setBackgroundResource(R.drawable.item_view_border_pressed);
        view.setBackgroundResource(R.drawable.item_view_border_pressed);
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
                .addParams("isLocal","2")
                .addParams("amount",amount+"")
                .addParams("user_id","1234")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(RechargePhoneActivity.this,
                                getString(R.string.net_error),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Result result = JSONObject.parseObject(s,Result.class);
                        if (result.getErr_code() == 0){
                            Toast.makeText(RechargePhoneActivity.this
                                    ,"订单提交成功,10分钟后到账",Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"result:"+result.getResult());
                        }else{
                            Toast.makeText(RechargePhoneActivity.this
                                    ,"订单提交失败，请稍后重试",Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"result:"+result.getResult());
                        }
                    }
                });
    }
}
