package com.sirc.tlt.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.NotifyNewsTypeAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.model.AskAndAnswerModel;
import com.sirc.tlt.model.NotifyNewsModel;
import com.sirc.tlt.model.NotifyNewsType;
import com.sirc.tlt.ui.view.CustomProgressDialog;
import com.sirc.tlt.ui.view.SwipeItemLayout;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class NotifyNewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.RequestLoadMoreListener{


    private RecyclerView recyclerView_type;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    int currPage;
    private Dialog load_dialog;

    private NotifyNewsTypeAdapter mAdapter;

    private List<NotifyNewsType> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus status = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_notify_news);

        load_dialog = CustomProgressDialog.createCustomDialog(this,getString(R.string.loading));
        load_dialog.setCanceledOnTouchOutside(false);
        load_dialog.show();
        currPage = 1;
        setData(currPage, false);
    }


    private void setData(int page, final boolean loadMore) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        recyclerView_type = (RecyclerView) findViewById(R.id.recyclerView_type);
        list = new ArrayList<>();
        OkHttpUtils.post()
                .url(Config.URL_NOTIFY_NEWS)
                .addParams("cmd","type")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(NotifyNewsActivity.this,
                                getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        if (load_dialog != null){
                            load_dialog.cancel();
                        }
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        List<NotifyNewsType> data = JSON.parseArray(s,NotifyNewsType.class);
                        if (mAdapter == null) {
                            list = data;
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotifyNewsActivity.this);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView_type.setLayoutManager(linearLayoutManager);// 布局管理器。
                            mAdapter = new NotifyNewsTypeAdapter(list,NotifyNewsActivity.this);
                            recyclerView_type.setAdapter(mAdapter);
                            recyclerView_type.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener
                                    (NotifyNewsActivity.this));

                            //setItemListener();
                            //上拉加载
//                            mAdapter.setOnLoadMoreListener(NotifyNewsActivity.this,
//                                    recyclerView_type);
                            //下拉刷新
                            mSwipeRefreshLayout.setOnRefreshListener(NotifyNewsActivity.this);
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
                setData(currPage, false);
            }
        }, 2000);
    }

    @Override
    public void onLoadMoreRequested() {
//        currPage++;
//        setData(currPage, true);
    }
}
