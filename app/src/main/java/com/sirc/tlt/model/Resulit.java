package com.sirc.tlt.model;

/**
 *
 * 号码查询类
 * Created by Hooliganiam on 17/6/8.
 */

public class Resulit {

    private String zip;//地区编码
    private String province;
    private String city;
    private String company;
    private String areacode;
    private String card;

    public String getZip() {
        return zip;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAreacode() {
        return areacode;
    }

    public String getCard() {
        return card;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
