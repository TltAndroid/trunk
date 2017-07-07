package com.sirc.tlt.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.R;
import com.sirc.tlt.model.NotifyNewsModel;
import com.sirc.tlt.ui.activity.NotifyNewsDetailActivity;

import java.util.List;

/**
 * Created by Hooliganiam on 17/6/3.
 */

public class NotifyNewsAdapter extends BaseQuickAdapter<NotifyNewsModel, BaseViewHolder>
        {
            Activity activity;
    public NotifyNewsAdapter( List<NotifyNewsModel> list,Activity activity) {
        super(R.layout.item_notify_news,list);
        this.activity = activity;
        Log.i("123321de",list.size()+"");
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final NotifyNewsModel model) {

         baseViewHolder.setText(R.id.tv_order_title,model.getTitle());

         baseViewHolder.setText(R.id.tv_notify_time,model.getCreate_time());

         baseViewHolder.setText(R.id.tv_state,model.getState());

         baseViewHolder.setText(R.id.tv_order_content,model.getContent());

    }
}
