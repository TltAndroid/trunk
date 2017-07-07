package com.sirc.tlt.model;

/**
 *
 * 网页轮播图模型
 * Created by Hooliganiam on 17/5/7.
 */

public class RollViewModel {

    private String img;
    private String url;
    private String title;



    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
