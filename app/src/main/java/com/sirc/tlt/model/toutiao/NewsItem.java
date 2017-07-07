package com.sirc.tlt.model.toutiao;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NewsItem implements MultiItemEntity {
    public static final int ITEM_TYPE_TEXT = 1;
    public static final int ITEM_TYPE_TEXT_ONE_PICTURE = 2;
    public static final int ITEM_TYPE_TEXT_THREE_PICTURE = 3;
    public static final int ITEM_TYPE_TEXT_GALLERY = 4;
    private int id;
    private int itemType;
    private String title;
    private String origin;
    private String post_time;
    private String url;
    private int reply;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(List<String> thumbnail) {
        this.thumbnail = thumbnail;
    }

    private List<String> thumbnail;
}
