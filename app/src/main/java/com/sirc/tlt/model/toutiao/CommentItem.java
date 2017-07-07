package com.sirc.tlt.model.toutiao;

/**
 * Created by Administrator on 2017/5/3.
 */

public class CommentItem {
    private int id;
    private String author;
    private String time;
    private String content;
    private int praise_num;//点赞数
    private boolean praise;//当前登录用户是否点赞
    private int userid;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPraise_num() {
        return praise_num;
    }

    public boolean isPraise() {
        return praise;
    }

    public void setPraise_num(int praise_num) {
        this.praise_num = praise_num;
    }

    public void setPraise(boolean praise) {
        this.praise = praise;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
