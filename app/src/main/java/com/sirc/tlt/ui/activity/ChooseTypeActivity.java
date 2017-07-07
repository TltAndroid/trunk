package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.ChooseTypeAdapter;
import com.sirc.tlt.adapters.RechargePhoneAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.model.AALableModel;
import com.sirc.tlt.model.RollViewModel;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.activity.toutiao.NewsDetailActivity;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DensityUtil;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.sirc.tlt.utils.SpaceItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ChooseTypeActivity extends BaseActivity {


    private ImageView icon_close;
    private RecyclerView recyclerView;
    private List<AALableModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel);

        //setSystemBarTintColor(getResources().getColor(android.R.color.white));

        icon_close = (ImageView) findViewById(R.id.icon_close);
        icon_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        initRecyclerView();

    }

    private void initRecyclerView() {

        OkHttpUtils
                .post()
                .url(Config.URL_ASK_LABLE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(ChooseTypeActivity.this,
                                "访问网络失败"+e.toString(), Toast.LENGTH_SHORT).show();
                        System.out.print("lable"+e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("lable",response.toString());
                        list = new ArrayList<>();
                        list = JSON.parseObject(response,new TypeReference<List<AALableModel>>() {
                        });

                        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                        GridLayoutManager mgr = new GridLayoutManager(ChooseTypeActivity.this, 4);
                        recyclerView.setLayoutManager(mgr);
                        ChooseTypeAdapter adapter = new ChooseTypeAdapter(R.layout.item_type_view, list);
                        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);

                        int spacingInPixels = DensityUtil.px2dip(ChooseTypeActivity.this, 20);
                        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                                Intent intent = new Intent();
                                intent.putExtra("type", list.get(i).getLable());
                                setResult(RESULT_OK, intent);
                                finish();

                            }
                        });
                    }
                });



    }
}
