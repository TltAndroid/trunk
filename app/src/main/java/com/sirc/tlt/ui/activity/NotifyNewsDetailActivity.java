package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.NotifyNewsAdapter;
import com.sirc.tlt.adapters.NotifyNewsTypeAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.model.NotifyNewsModel;
import com.sirc.tlt.ui.view.CustomProgressDialog;
import com.sirc.tlt.ui.view.SwipeItemLayout;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class NotifyNewsDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.RequestLoadMoreListener{


    private RecyclerView mRecyclerView;
    List<NotifyNewsModel> list;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    int currPage;
    private Dialog load_dialog;

    private NotifyNewsAdapter mAdapter;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus status = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_notify_news_detail);

        if (getIntent() != null){
            type = getIntent().getStringExtra("type");
        }


        load_dialog = CustomProgressDialog.createCustomDialog(this,getString(R.string.loading));
        load_dialog.setCanceledOnTouchOutside(false);
        load_dialog.show();

        TemplateTitle title = (TemplateTitle) findViewById(R.id.news_detail_title);
        if (TextUtils.isEmpty(type)) {
            title.setTitleText(getString(R.string.notify_news));
            type = getString(R.string.notify_news);
        }
        else title.setTitleText(type);

        Log.i("123321",type);
        currPage = 1;
        setData(currPage, false,type);
    }

    private void setData(int page, final boolean loadMore,String type) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        OkHttpUtils.post()
                .url(Config.URL_NOTIFY_NEWS)
                .addParams("cmd","r")
                .addParams("user_id","123")
                .addParams("s", String.valueOf(Config.PAGE_SIZE))
                .addParams("p", String.valueOf(page))
                .addParams("type",type)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(NotifyNewsDetailActivity.this,
                                getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        if (load_dialog != null){
                            load_dialog.cancel();
                        }
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        List<NotifyNewsModel> data = JSON.parseObject(s,new TypeReference<List<NotifyNewsModel>>() {
                        });
                        if (mAdapter == null) {
                            list = data;
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotifyNewsDetailActivity.this);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            mRecyclerView.setLayoutManager(linearLayoutManager);// 布局管理器。
                            mAdapter = new NotifyNewsAdapter(list,NotifyNewsDetailActivity.this);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener
                                    (NotifyNewsDetailActivity.this));

                            //setItemListener();
                            //上拉加载
                            mAdapter.setOnLoadMoreListener(NotifyNewsDetailActivity.this,
                                    mRecyclerView);
                            //下拉刷新
                            mSwipeRefreshLayout.setOnRefreshListener(NotifyNewsDetailActivity.this);
                            mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
                        } else {
                            if (loadMore) { //加载更多
                                mSwipeRefreshLayout.setEnabled(false);
                                if (data.size() > 0) {
                                    mAdapter.addData(data);
                                    mAdapter.loadMoreComplete();
                                } else {
                                    mAdapter.loadMoreEnd();
                                    currPage--;
                                }
                                mSwipeRefreshLayout.setEnabled(true);
                            } else {
                                list = data;
                                mAdapter.setEnableLoadMore(false);
                                mAdapter.setNewData(list);
                                mSwipeRefreshLayout.setRefreshing(false);
                                mAdapter.setEnableLoadMore(true);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                        if (load_dialog != null){
                            load_dialog.cancel();
                        }
                    }

                });
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                currPage = 1;
                setData(currPage, false,type);
            }
        }, 2000);
    }

    @Override
    public void onLoadMoreRequested() {
        currPage++;
        setData(currPage, true,type);
    }
}
