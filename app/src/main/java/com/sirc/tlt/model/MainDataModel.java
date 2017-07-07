package com.sirc.tlt.model;

import java.util.List;

/**
 *
 * 获取主页数据的模型
 * Created by Hooliganiam on 17/5/7.
 */

public class MainDataModel {


    private List<RollViewModel> rollViewModelList; //轮播list数据
    private List<TlwServiceModel> tlwServiceModelList;//台陆通服务list数据
    private List<GuessFancyModel> toutiaoList;//好看头条跑马灯list数据
    private List<GuessFancyModel> guessFancyModelList;//猜您喜欢list数据


    public void setRollViewModelList(List<RollViewModel> rollViewModelList) {
        this.rollViewModelList = rollViewModelList;
    }

    public void setTlwServiceModelList(List<TlwServiceModel> tlwServiceModelList) {
        this.tlwServiceModelList = tlwServiceModelList;
    }

    public void setToutiaoList(List<GuessFancyModel> toutiaoList) {
        this.toutiaoList = toutiaoList;
    }

    public void setGuessFancyModelList(List<GuessFancyModel> guessFancyModelList) {
        this.guessFancyModelList = guessFancyModelList;
    }


    public List<RollViewModel> getRollViewModelList() {
        return rollViewModelList;
    }

    public List<TlwServiceModel> getTlwServiceModelList() {
        return tlwServiceModelList;
    }

    public List<GuessFancyModel> getToutiaoList() {
        return toutiaoList;
    }

    public List<GuessFancyModel> getGuessFancyModelList() {
        return guessFancyModelList;
    }
}
