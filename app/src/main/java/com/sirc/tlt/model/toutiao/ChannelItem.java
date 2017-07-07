package com.sirc.tlt.model.toutiao;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Administrator on 2017/4/24.
 */

public class ChannelItem implements MultiItemEntity {
    public static final int ITEM_TYPE_MY = 1;
    public static final int ITEM_TYPE_OTHER = 2;
    public static final int ITEM_TYPE_MY_CHANNEL = 3;
    public static final int ITEM_TYPE_OTHER_CHANNEL = 4;
    private int itemType;
    private String title;
    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
