package com.sirc.tlt.feiyucloud.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DeveloperAccount implements Serializable {
    private static final long serialVersionUID = 25234469954231057L;
    private String appId;
    private String appToken;
    private String spMobile;
    private String spId;
    private String spEmail;
    private ArrayList<FeiyuAccount> accounts = new ArrayList<FeiyuAccount>();

    public DeveloperAccount() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public ArrayList<FeiyuAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<FeiyuAccount> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(FeiyuAccount account) {
        accounts.add(account);
    }

    public String getSpMobile() {
        return spMobile;
    }

    public void setSpMobile(String spMobile) {
        this.spMobile = spMobile;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getSpEmail() {
        return spEmail;
    }

    public void setSpEmail(String spEmail) {
        this.spEmail = spEmail;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("appId='" + appId + '\'');
        sb.append(" appToken='" + appToken + '\'');
        sb.append(" spMobile='" + spMobile + '\'');
        sb.append(" spId='" + spId + '\'');
        sb.append(" spEmail='" + spEmail + '\'');
        sb.append(" accounts='");
        int size = accounts.size();
        for (int i = 0; i < size; i++) {
            sb.append(accounts.get(i));
            if (i + 1 < size) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
