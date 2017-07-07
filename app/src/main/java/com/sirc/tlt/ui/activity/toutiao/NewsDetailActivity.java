package com.sirc.tlt.ui.activity.toutiao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.toutiao.NewsCommentAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.model.toutiao.CommentItem;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.view.KeyMapDailog;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/4/28.
 */

public class NewsDetailActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private NewsCommentAdapter mAdapter;
    private List<CommentItem> mData;
    KeyMapDailog dialog;
    private int currPage;
    private static final int PAGE_SIZE = 10;

    private String title;
    private String url;
    private int tt_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);

        setSystemBarTintColor(getResources().getColor(android.R.color.white));

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        tt_id = intent.getIntExtra("id", -1);

        mAdapter = new NewsCommentAdapter(null,Config.TCOMMENT);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        View headerView = getLayoutInflater().inflate(R.layout.layout_webview, (ViewGroup) mRecyclerView.getParent(), false);
        mAdapter.addHeaderView(headerView);
        mRecyclerView.setAdapter(mAdapter);
        WebView webview = (WebView) headerView.findViewById(R.id.webview);
        webview.loadUrl(getIntent().getStringExtra("url"));

        currPage = 1;
        getData(currPage, false);
    }

    private void getData(int page, final boolean loadMore) {

        getData(page, loadMore, false);
    }
    //从后台获取评论列表
    private void getData(int page, final boolean loadMore, final boolean afterWriteComment) {
        OkHttpUtils.get().url(Config.URL_TOUTIAO_COMMENT)
                .addParams("cmd", "r")
                .addParams("s", String.valueOf(PAGE_SIZE))
                .addParams("p", String.valueOf(page))
                .addParams("tt_id", String.valueOf(tt_id))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mContext, "服务器开小差啦！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("新闻评论", response);
                        List<CommentItem> data = JSON.parseArray(response, CommentItem.class);
                        if (!loadMore) {
                            mData = data;
                            mAdapter.setNewData(mData);
//                            LinearLayoutManager manager = new LinearLayoutManager(mContext);
//                            mRecyclerView.setLayoutManager(manager);
//                            mRecyclerView.setAdapter(mAdapter);

                            //上拉加载更多数据
                            mAdapter.setOnLoadMoreListener(NewsDetailActivity.this, mRecyclerView);
                        } else {
                                int dataSize = data.size();
                                if (dataSize > 0) {
                                    mAdapter.addData(data);
                                    mAdapter.loadMoreComplete();
                                } else {
                                    mAdapter.loadMoreEnd();
                                    currPage--;
                                }
                        }
                        if (afterWriteComment) {
                        	mRecyclerView.scrollToPosition(1);
                        }
                    }
                });
    }

    @OnClick(R.id.iv_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.tv_reply)
    public void reply() {
        dialog = new KeyMapDailog(String.valueOf(getResources().getText(R.string.say_something)), new KeyMapDailog.SendBackListener() {
            @Override
            public void sendBack(final String inputText) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hideProgressdialog();
                        OkHttpUtils
                                .post()
                                .url(Config.URL_TOUTIAO_COMMENT)
                                .addParams("cmd", "w")
                                .addParams("tt_id", String.valueOf(tt_id))
                                .addParams("author", "guest")
                                .addParams("content", inputText)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Toast.makeText(NewsDetailActivity.this, "服务器开小差啦！", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        Result result = JSON.parseObject(response, Result.class);
                                        if (result.getErr_code() == 0) {
                                            Toast.makeText(NewsDetailActivity.this, "评论发表成功！", Toast.LENGTH_SHORT).show();
                                            dialog.getInputDlg().setText("");
                                            currPage = 1;
                                            getData(currPage, false, true);
                                        } else {
                                            Toast.makeText(NewsDetailActivity.this, "评论发表失败！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
//                        Toast.makeText(NewsDetailActivity.this, "发表成功", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }, 2000);
            }
        });

        dialog.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onLoadMoreRequested() {
        currPage++;
        getData(currPage, true);
    }

    @OnClick({R.id.iv_share, R.id.action_new_share})
    public void showShare(View view) {
        CommonUtil.showShare(this, title, url, "这则新闻很不错哦！", url);
    }
}
