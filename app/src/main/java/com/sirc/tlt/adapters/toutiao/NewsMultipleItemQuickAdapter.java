package com.sirc.tlt.adapters.toutiao;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.R;
import com.sirc.tlt.model.toutiao.NewsItem;
import com.sirc.tlt.utils.DateUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NewsMultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<NewsItem, BaseViewHolder> {
    public NewsMultipleItemQuickAdapter(List<NewsItem> data) {
        super(data);
        addItemType(NewsItem.ITEM_TYPE_TEXT, R.layout.item_news_text);
        addItemType(NewsItem.ITEM_TYPE_TEXT_ONE_PICTURE, R.layout.item_news_text_one_picture);
        addItemType(NewsItem.ITEM_TYPE_TEXT_THREE_PICTURE, R.layout.item_news_text_three_picture);
        addItemType(NewsItem.ITEM_TYPE_TEXT_GALLERY, R.layout.item_news_text_gallery);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsItem item) {
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setText(R.id.tv_origin, item.getOrigin());
        helper.setText(R.id.tv_reply, String.valueOf(item.getReply())+"评论");
        helper.setText(R.id.tv_post_time, DateUtils.friendlyTime(item.getPost_time()));
        switch (helper.getItemViewType()) {
            case NewsItem.ITEM_TYPE_TEXT_ONE_PICTURE:
            case NewsItem.ITEM_TYPE_TEXT_GALLERY:
                Glide.with(mContext).load(item.getThumbnail().get(0)).into((ImageView) helper.getView(R.id.iv_one));
                break;
            case NewsItem.ITEM_TYPE_TEXT_THREE_PICTURE:
                Glide.with(mContext).load(item.getThumbnail().get(0)).into((ImageView) helper.getView(R.id.iv_one));
                Glide.with(mContext).load(item.getThumbnail().get(1)).into((ImageView) helper.getView(R.id.iv_two));
                Glide.with(mContext).load(item.getThumbnail().get(2)).into((ImageView) helper.getView(R.id.iv_three));
                break;
        }
    }
}
