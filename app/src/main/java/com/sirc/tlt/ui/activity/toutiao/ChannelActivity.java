package com.sirc.tlt.ui.activity.toutiao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.toutiao.ChannelAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.data.DataServer;
import com.sirc.tlt.listener.ItemDragHelperCallBack;
import com.sirc.tlt.model.toutiao.ChannelItem;
import com.sirc.tlt.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ChannelActivity extends BaseActivity implements ChannelAdapter.OnChannelDragListener {
    static final int RESULT_CHANNEL = 1;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private ChannelAdapter mAdapter;
    private List<ChannelItem> mDatas;
    private ItemTouchHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        setSystemBarTintColor(getResources().getColor(android.R.color.white));

        ButterKnife.bind(this);

        mDatas = DataServer.initChannelItemData();
        mAdapter = new ChannelAdapter(mDatas);
        mAdapter.bindToRecyclerView(mRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mAdapter.getItemViewType(position);
                return itemViewType == ChannelItem.ITEM_TYPE_MY_CHANNEL || itemViewType == ChannelItem.ITEM_TYPE_OTHER_CHANNEL ? 1 : 4;
            }
        });
        mAdapter.setOnChannelDragListener(this);
        ItemDragHelperCallBack callBack = new ItemDragHelperCallBack(this);
        mHelper = new ItemTouchHelper(callBack);
        mHelper.attachToRecyclerView(mRecyclerView);

        String selectedTabTitle = getIntent().getStringExtra(ToutiaoActivity.SELECTED_TAB_TITLE);
        mAdapter.setSelectedTitle(selectedTabTitle);
        mAdapter.setActivity(this);
    }

    @OnClick(R.id.icon_close)
    public void close() {
        finish();
    }

    @Override
    public void onStarDrag(BaseViewHolder baseViewHolder) {
        //开始拖动
        mHelper.startDrag(baseViewHolder);
    }

    @Override
    public void onItemMove(int starPos, int endPos) {
//        if (starPos < 0||endPos<0) return;
        ChannelItem startChannel = mDatas.get(starPos);
        //先删除之前的位置
        mDatas.remove(starPos);
        //添加到现在的位置
        mDatas.add(endPos, startChannel);
        mAdapter.notifyItemMoved(starPos, endPos);
    }

    @Override
    public void finish() {
        Intent intent = getIntent();
        intent.putExtra(ToutiaoActivity.SELECTED_TAB_TITLE, mAdapter.getSelectedTitle());
        setResult(RESULT_CHANNEL, intent);
        super.finish();
    }
}
