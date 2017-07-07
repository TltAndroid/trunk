package com.sirc.tlt.model;

/**
 * Created by Hooliganiam on 17/5/7.
 */

public class RechargePhoneModel {


    private int amount; //面额
    private double sale_price; //售价
    private double on_sale_price;//特价
    private boolean is_on_sale;//是否显示特价
    private int isLocal;
    private int flow;//流量面额
    private String type;//号码运营商





    public double getSale_price() {
        return sale_price;
    }

    public double getOn_sale_price() {
        return on_sale_price;
    }

    public boolean is_on_sale() {
        return is_on_sale;
    }


    public void setSale_price(double sale_price) {
        this.sale_price = sale_price;
    }

    public void setOn_sale_price(double on_sale_price) {
        this.on_sale_price = on_sale_price;
    }

    public void setIs_on_sale(boolean is_on_sale) {
        this.is_on_sale = is_on_sale;
    }


    public int getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(int isLocal) {
        this.isLocal = isLocal;
    }

    public int getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }
}
