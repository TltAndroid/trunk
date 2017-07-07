package com.sirc.tlt.model;

import java.util.List;

/**
 *
 * 台陆通服务分组model
 * Created by Hooliganiam on 17/6/12.
 */

public class TlwServiceTeamModel {
    private String head; //头标题

    private List<TlwServiceModel> serviceModels; //对应的服务集合

    public String getHead() {
        return head;
    }

    public List<TlwServiceModel> getServiceModels() {
        return serviceModels;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setServiceModels(List<TlwServiceModel> serviceModels) {
        this.serviceModels = serviceModels;
    }
}
