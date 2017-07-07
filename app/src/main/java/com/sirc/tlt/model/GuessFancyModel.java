package com.sirc.tlt.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 *
 * 猜你喜欢模型（好看头条跑马灯同样适用）
 * Created by Hooliganiam on 17/4/20.
 */
public class GuessFancyModel {

    private int id;
    private List<String> thumbnail;  //资讯图片地址
    private String title;   //资讯标题
    private String origin;    //资讯来源
    private String post_time;    //发布时间
    private int reply;      //跟帖回复数量
    private int type;
    private String url;

    public void setId(int id) {
        this.id = id;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int getId() {
        return id;
    }

    public List<String> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(List<String> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getOrigin() {
        return origin;
    }

    public String getPost_time() {
        return post_time;
    }

    public int getReply() {
        return reply;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
