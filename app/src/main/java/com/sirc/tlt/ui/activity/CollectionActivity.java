package com.sirc.tlt.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.AskAndAnswerAdapter;
import com.sirc.tlt.adapters.SwipeAskAndAnswerAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.model.AskAndAnswerModel;
import com.sirc.tlt.ui.fragment.AskAnswerFragment;
import com.sirc.tlt.ui.view.CustomProgressDialog;
import com.sirc.tlt.ui.view.SwipeItemLayout;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.ImageUtil;
import com.sirc.tlt.utils.PermissionsChecker;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class CollectionActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.RequestLoadMoreListener{

    private static final String TAG = CollectionActivity.class.getSimpleName();
    private TemplateTitle collect_title;
    private RecyclerView listview_collect;

    private SwipeAskAndAnswerAdapter askAndAnswerAdapter;
    List<AskAndAnswerModel> list;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    int currPage;
    private Dialog load_dialog;

//    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_SETTINGS,
//           };
//    private PermissionsChecker mPermissionsChecker; // 权限检测器
//    private static final int REQUEST_PERMISSION = 4;  //权限请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_collection);

        collect_title = (TemplateTitle) findViewById(R.id.collect_title);
        collect_title.setTitleText("收藏夹");
        collect_title.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        load_dialog = CustomProgressDialog.createCustomDialog(this,getString(R.string.loading));
        load_dialog.setCanceledOnTouchOutside(false);
        load_dialog.show();
        currPage = 1;
        setData(currPage, false);
    }

    private void setData(int page, final boolean loadMore) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.collection_refresh_layout);
        listview_collect = (RecyclerView) findViewById(R.id.listview_collect);
        list = new ArrayList<>();

        OkHttpUtils.post()
                .url(Config.URL_COLLECT)
                .addParams("cmd","r")
                .addParams("s", String.valueOf(Config.PAGE_SIZE))
                .addParams("p", String.valueOf(page))
                .addParams("user_id","123")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(CollectionActivity.this,
                                getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        if (load_dialog != null){
                            load_dialog.cancel();
                        }
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        List<AskAndAnswerModel> data = JSON.parseObject(s,new TypeReference<List<AskAndAnswerModel>>() {
                        });
                        if (askAndAnswerAdapter == null) {
                            list = data;
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CollectionActivity.this);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            listview_collect.setLayoutManager(linearLayoutManager);// 布局管理器。
                            askAndAnswerAdapter = new SwipeAskAndAnswerAdapter(list,CollectionActivity.this);
                            listview_collect.setAdapter(askAndAnswerAdapter);
                            listview_collect.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener
                                    (CollectionActivity.this));

                            //setItemListener();
                            //上拉加载
                            askAndAnswerAdapter.setOnLoadMoreListener(CollectionActivity.this,
                                    listview_collect);
                            //下拉刷新
                            mSwipeRefreshLayout.setOnRefreshListener(CollectionActivity.this);
                            mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
                        } else {
                            if (loadMore) { //加载更多
                                mSwipeRefreshLayout.setEnabled(false);
                                if (data.size() > 0) {
                                    askAndAnswerAdapter.addData(data);
                                    askAndAnswerAdapter.loadMoreComplete();
                                } else {
                                    askAndAnswerAdapter.loadMoreEnd();
                                    currPage--;
                                }
                                mSwipeRefreshLayout.setEnabled(true);
                            } else {
                                list = data;
                                askAndAnswerAdapter.setEnableLoadMore(false);
                                askAndAnswerAdapter.setNewData(list);
                                mSwipeRefreshLayout.setRefreshing(false);
                                askAndAnswerAdapter.setEnableLoadMore(true);
                                askAndAnswerAdapter.notifyDataSetChanged();
                            }

                        }
                        if (load_dialog != null){
                            load_dialog.cancel();
                        }
                    }

                });

    }

    private void setItemListener() {
         askAndAnswerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                 Toast.makeText(CollectionActivity.this, "点击事件", Toast.LENGTH_SHORT).show();

             }
         });
    }

//        collect_title.setMoreTextAction(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isCanDel) {
//                    isCanDel = false;
//                    Toast.makeText(CollectionActivity.this,
//                            "左滑删除关闭", Toast.LENGTH_SHORT).show();
//                    collect_title.setMoreTextContext("编辑");
//                    if (listview_collect != null) {
//                        Log.d(TAG,"开启");
//                        listview_collect.setLongPressDragEnabled(false); // 开启长按拖拽。
//                        listview_collect.setItemViewSwipeEnabled(false); // 开启滑动删除。
//                    }
//                } else {
//                    isCanDel = true;
//                    Toast.makeText(CollectionActivity.this,
//                            "左滑删除开启", Toast.LENGTH_SHORT).show();
//                    collect_title.setMoreTextContext("完成");
//                    if (listview_collect != null) {
//                        Log.d(TAG,"关闭");
//                        listview_collect.setLongPressDragEnabled(true); // 开启长按拖拽。
//                        listview_collect.setItemViewSwipeEnabled(true);  // 开启滑动删除。
//                    }
//                }
//
//            }
//
//        });


    /**
     * 刷新监听。
     */
    @Override
    public void onRefresh() {
        setmSwipeRefresh();
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMoreRequested() {
        currPage++;
        setData(currPage, true);
    }

    public void  setmSwipeRefresh(){
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                currPage = 1;
                setData(currPage, false);
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setmSwipeRefresh();
    }
}

