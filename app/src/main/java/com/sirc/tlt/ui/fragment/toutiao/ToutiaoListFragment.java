package com.sirc.tlt.ui.fragment.toutiao;


import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.toutiao.NewsMultipleItemQuickAdapter;
import com.sirc.tlt.data.DataServer;
import com.sirc.tlt.model.toutiao.NewsItem;
import com.sirc.tlt.ui.activity.toutiao.NewsDetailActivity;
import com.sirc.tlt.ui.activity.toutiao.ToutiaoActivity;
import com.sirc.tlt.utils.Config;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToutiaoListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private String label;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private NewsMultipleItemQuickAdapter mAdapter;
    private int delayMillis = 1000;

    private static final int PAGE_SIZE = 10;
    private List<NewsItem> mData;
    private int currPage;

    private String type;

    public ToutiaoListFragment() {

    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_toutiao_list, container, false);
        unbinder = ButterKnife.bind(this, view);



        currPage = 1;
        getData(currPage, false);
        return view;
    }

    //从后台获取新闻列表
    private void getData(int page, final boolean loadMore) {
        OkHttpUtils.get().url(Config.URL_TOUTIAO_LIST).
                addParams("s", String.valueOf(PAGE_SIZE)).
                addParams("p", String.valueOf(page)).
                addParams("label", label)
                .addParams("type",type)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), "服务器开小差啦！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                List<NewsItem> data = JSON.parseArray(response, NewsItem.class);
                if (data != null && !data.isEmpty()) {
                    for (int i = 0; i < data.size(); i++) {
                        NewsItem item = data.get(i);
                        List<String> thumbnails = item.getThumbnail();
                        if (thumbnails == null || thumbnails.isEmpty()) {//没有图片，只有文字
                            item.setItemType(NewsItem.ITEM_TYPE_TEXT);
                        } else if (thumbnails.size() < 3) {
                            if (item.getType() == 1) {
                                item.setItemType(NewsItem.ITEM_TYPE_TEXT_GALLERY);
                            } else {
                                item.setItemType(NewsItem.ITEM_TYPE_TEXT_ONE_PICTURE);
                            }
                        } else {
                            item.setItemType(NewsItem.ITEM_TYPE_TEXT_THREE_PICTURE);
                        }
                    }
                }
                if (mAdapter == null) {
                    mData = data;
                    mAdapter = new NewsMultipleItemQuickAdapter(mData);
                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(manager);
                    mRecyclerView.setAdapter(mAdapter);

                    setListener();

                    //上拉加载更多数据
                    mAdapter.setOnLoadMoreListener(ToutiaoListFragment.this, mRecyclerView);

                    //下拉刷新
                    mSwipeRefreshLayout.setOnRefreshListener(ToutiaoListFragment.this);
                    mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
                } else {
                    if (loadMore) {//加载更多
                        mSwipeRefreshLayout.setEnabled(false);
                        int dataSize = data.size();
                        if (dataSize > 0) {
                            mAdapter.addData(data);
                            mAdapter.loadMoreComplete();
                        } else {
                            mAdapter.loadMoreEnd();
                            currPage--;
                        }
                        mSwipeRefreshLayout.setEnabled(true);
                    } else {//下拉刷新
                        mData = data;
                        mAdapter.setEnableLoadMore(false);
                        mAdapter.setNewData(mData);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.setEnableLoadMore(true);
                    }
                }
            }
        });
    }

    private void setListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                String url = mData.get(position).getUrl();
                intent.putExtra("url", url);
                String title = mData.get(position).getTitle();
                intent.putExtra("title", title);
                int id = mData.get(position).getId();
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //下拉刷新回调方法
    @Override
    public void onRefresh() {
        currPage = 1;
        getData(currPage, false);
    }

    //上拉更多数据回调方法
    @Override
    public void onLoadMoreRequested() {
        currPage++;
        getData(currPage, true);
    }

    public void setType(String str){
        type = str;
        Log.i("类型",type);
    }
}