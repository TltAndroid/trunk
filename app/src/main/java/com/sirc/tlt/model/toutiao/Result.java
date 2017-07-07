package com.sirc.tlt.model.toutiao;

/**
 * Created by Administrator on 2017/5/13.
 */

public class Result {
    private int err_code;
    private String err_msg;
    private String result; //话费充值结果
    private String mobile;// 用户手机号
    private String fyAccountId;//飞语账号id
    private String fyAccountPwd;//飞羽账号密码
    private String district;//返回的区号


    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setFyAccountId(String fyAccountId) {
        this.fyAccountId = fyAccountId;
    }

    public void setFyAccountPwd(String fyAccountPwd) {
        this.fyAccountPwd = fyAccountPwd;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFyAccountId() {
        return fyAccountId;
    }

    public String getFyAccountPwd() {
        return fyAccountPwd;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
