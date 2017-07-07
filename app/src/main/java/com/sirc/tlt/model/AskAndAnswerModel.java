package com.sirc.tlt.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Hooliganiam on 17/4/24.
 */
public class AskAndAnswerModel implements Parcelable {

    private int q_id;//问题id
    private String title;   //问题
    private String username;  //发布者
    private byte[] head_img; //发布者头像
    private String lable;    //类型 (吃喝玩乐等)
    private String content; //问题内容
    private String create_time;    //发布时间
    private int reply; //评论数
    private int praise_num;  //点赞数
    private boolean concern;   //是否关注
    private boolean praise;//当前用户是否点赞


    public  AskAndAnswerModel(){

    }


    protected AskAndAnswerModel(Parcel in) {
        q_id = in.readInt();
        title = in.readString();
        username = in.readString();
        head_img = in.createByteArray();
        lable = in.readString();
        content = in.readString();
        create_time = in.readString();
        reply = in.readInt();
        praise_num = in.readInt();
        concern = in.readByte() != 0;
        praise = in.readByte() != 0;
    }

    public static final Creator<AskAndAnswerModel> CREATOR = new Creator<AskAndAnswerModel>() {
        @Override
        public AskAndAnswerModel createFromParcel(Parcel in) {
            return new AskAndAnswerModel(in);
        }

        @Override
        public AskAndAnswerModel[] newArray(int size) {
            return new AskAndAnswerModel[size];
        }
    };

    public void setConcern(boolean concern) {
        this.concern = concern;
    }

    public boolean isConcern() {

        return concern;
    }

    public byte[] getHead_img() {
        return head_img;
    }

    public String getLable() {
        return lable;
    }


    public String getCreate_time() {
        return create_time;
    }

    public int getReply() {
        return reply;
    }

    public int getPraise_num() {
        return praise_num;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {

        return title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {

        return username;
    }

    public void setHead_img(byte[] head_img) {
        this.head_img = head_img;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }


    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public void setPraise_num(int praise_num) {
        this.praise_num = praise_num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public int getQ_id() {
        return q_id;
    }

    public void setQ_id(int q_id) {
        this.q_id = q_id;
    }

    public boolean isPraise() {
        return praise;
    }

    public void setPraise(boolean praise) {
        this.praise = praise;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(q_id);
        dest.writeString(title);
        dest.writeString(username);
        dest.writeByteArray(head_img);
        dest.writeString(lable);
        dest.writeString(content);
        dest.writeString(create_time);
        dest.writeInt(reply);
        dest.writeInt(praise_num);
        dest.writeByte((byte) (concern ? 1 : 0));
        dest.writeByte((byte) (praise ? 1 : 0));
    }
}
