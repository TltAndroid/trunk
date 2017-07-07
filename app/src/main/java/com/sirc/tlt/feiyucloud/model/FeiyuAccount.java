package com.sirc.tlt.feiyucloud.model;

import java.io.Serializable;

public class FeiyuAccount implements Serializable {
    private static final long serialVersionUID = 3337623526513695442L;
    private String id;
    private String pwd;
    private String mobilePhone;

    public FeiyuAccount() {
    }

    public FeiyuAccount(String id, String pwd, String mobilePhone) {
        this.id = id;
        this.pwd = pwd;
        this.mobilePhone = mobilePhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Override
    public String toString() {
        return "{" + "id='" + id + '\'' + ", pwd='" + pwd + '\''
                + ", mobilePhone='" + mobilePhone + '\'' + '}';
    }
}
