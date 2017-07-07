package com.sirc.tlt.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.R;
import com.sirc.tlt.model.AALableModel;
import com.sirc.tlt.model.RechargePhoneModel;

import java.util.List;

/**
 * Created by Hooliganiam on 17/5/8.
 */

public class ChooseTypeAdapter extends BaseQuickAdapter<AALableModel, BaseViewHolder> {

    private List<AALableModel> list;



    public ChooseTypeAdapter(int layoutResId, List<AALableModel> data) {
        super(layoutResId, data);
        this.list = data;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, AALableModel model) {

        baseViewHolder.setText(R.id.tv_item_type,model.getLable());
        baseViewHolder.setChecked(R.id.tv_item_type,true);
    }


}
