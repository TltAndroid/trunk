package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.RechargePhoneAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.RechargePhoneModel;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DensityUtil;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.sirc.tlt.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MyWalletActivity extends BaseActivity {

    private static final String TAG = MyWalletActivity.class.getSimpleName();
    private TextView tv_take_mins, tv_my_left_mins;
    private static final int SIGN_IN = 00001;
    int min = 30;

    private String[] price = new String[]{
            "100", "200", "300", "400"
    };

    private RecyclerView gridView;

    private Button btn_pay_for_others,btn_recharge;

    private View lastView;
    private int amount;
    private RechargePhoneModel rechargePhoneModel;
    RechargePhoneAdapter rechargePhoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_my_wallet);

        TemplateTitle title = (TemplateTitle) findViewById(R.id.my_wallet_title);
        title.setMoreTextAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWalletActivity.this,MyChargeActivity.class);
                startActivity(intent);
            }
        });

        tv_my_left_mins = (TextView) findViewById(R.id.tv_my_left_mins);
        tv_my_left_mins.setText(Config.NET_PHONE_LEFT_MINS + "分钟");

        tv_take_mins = (TextView) findViewById(R.id.tv_take_mins);
        tv_take_mins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWalletActivity.this, EarnMinsActivity.class);
                startActivityForResult(intent, SIGN_IN);
            }
        });

//        liner_recharge_item = (LinearLayout) findViewById(R.id.liner_recharge_item);
        btn_pay_for_others = (Button) findViewById(R.id.btn_pay_for_others);
        btn_pay_for_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShortToast(MyWalletActivity.this,
                        "正在开发中...");
            }
        });
        gridView = (RecyclerView) findViewById(R.id.recycler_recharge_phone);
        //初始化充值模块
        initItemView();

    }

    /**
     * 自定义初始化充值模块
     */

    private void initItemView() {

        final List<RechargePhoneModel> list = new ArrayList<>();

        RechargePhoneModel model = new RechargePhoneModel();
        model.setAmount(200);
        model.setSale_price(200);
        model.setIs_on_sale(false);
        list.add(model);

        RechargePhoneModel model1 = new RechargePhoneModel();
        model1.setAmount(100);
        model1.setSale_price(100);
        model1.setIs_on_sale(false);
        list.add(model1);

        RechargePhoneModel model2 = new RechargePhoneModel();
        model2.setAmount(50);
        model2.setSale_price(50);
        model2.setIs_on_sale(false);
        list.add(model2);


        RechargePhoneModel model3 = new RechargePhoneModel();
        model3.setAmount(30);
        model3.setSale_price(30);
        model3.setIs_on_sale(false);
        list.add(model3);

        Log.i(TAG,list.size()+"");
        //GridLayout 3列
        GridLayoutManager mgr=new GridLayoutManager(this,3);
        gridView.setLayoutManager(mgr);
        rechargePhoneAdapter = new RechargePhoneAdapter(list,"话费");
        rechargePhoneAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);


        int spacingInPixels = DensityUtil.dip2px(MyWalletActivity.this,10);
        gridView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        gridView.setAdapter(rechargePhoneAdapter);
        rechargePhoneAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                rechargePhoneModel = list.get(i);
                amount = rechargePhoneModel.getAmount();
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
        });

//        for (int i = 0; i < 2; i++) {
//            //item有几行
//            LinearLayout linearLayout = new LinearLayout(MyWalletActivity.this);
//            LinearLayout.LayoutParams liner_params = new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
//            );
//            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//            //每行的个数
//            for (int j = 0;j<4;j++){
//                LinearLayout linearLayout1 = new LinearLayout(MyWalletActivity.this);
//                LinearLayout.LayoutParams liner_params1 = new LinearLayout.LayoutParams(
//                        0, ViewGroup.LayoutParams.WRAP_CONTENT,1
//                );
//                linearLayout1.setOrientation(LinearLayout.VERTICAL);
//                linearLayout1.setBackgroundResource(R.drawable.actions_text_background);
//
//                //面价的textview
//                TextView orignal_price = new TextView(this);
//                LinearLayout.LayoutParams liner_orignal_price = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
//                liner_orignal_price.gravity = Gravity.CENTER_HORIZONTAL;
//                liner_orignal_price.setMargins(0, 10, 0, 0);
//                orignal_price.setGravity(Gravity.CENTER);
//                orignal_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                orignal_price.setTextColor(getResources().getColor(R.color.text_black));
//                orignal_price.setText(price[j]);
//
//                //售价的textview
//                TextView sale_price = new TextView(this);
//                LinearLayout.LayoutParams liner_sale_price = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
////                liner_sale_price.gravity = Gravity.CENTER_HORIZONTAL;
////                liner_sale_price.setMargins(0, 10, 0, 0);
//                sale_price.setGravity(Gravity.CENTER);
//                sale_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                sale_price.setTextColor(getResources().getColor(R.color.text_black));
//                sale_price.setText(String.valueOf(Integer.valueOf(price[j])-j));
//
//                //折后价的textview
//                TextView on_sale_price = new TextView(this);
//                LinearLayout.LayoutParams liner_on_sale_price = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
////                liner_on_sale_price.gravity = Gravity.CENTER_HORIZONTAL;
////                liner_on_sale_price.setMargins(0, 10, 0, 0);
//                on_sale_price.setGravity(Gravity.CENTER);
//                on_sale_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                on_sale_price.setTextColor(getResources().getColor(R.color.red));
//                on_sale_price.setText(String.valueOf(Integer.valueOf(price[j])-j*3));
//                on_sale_price.setVisibility(View.VISIBLE);
//
//                linearLayout1.addView(orignal_price,liner_orignal_price);
//                linearLayout1.addView(sale_price,liner_orignal_price);
//                linearLayout1.addView(on_sale_price,liner_orignal_price);
//
//                linearLayout.addView(linearLayout1,liner_params1);
//            }
//            liner_recharge_item.addView(linearLayout,liner_params);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SIGN_IN) {
                min += 3;
                tv_my_left_mins.setText(String.valueOf(min) + "分钟");
                Toast.makeText(MyWalletActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
