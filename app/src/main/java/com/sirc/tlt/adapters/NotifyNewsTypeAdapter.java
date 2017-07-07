package com.sirc.tlt.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.R;
import com.sirc.tlt.model.NotifyNewsModel;
import com.sirc.tlt.model.NotifyNewsType;
import com.sirc.tlt.ui.activity.NotifyNewsDetailActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hooliganiam on 17/6/3.
 */

public class NotifyNewsTypeAdapter extends BaseQuickAdapter<NotifyNewsType, BaseViewHolder>
        {
            Activity activity;
    public NotifyNewsTypeAdapter(List<NotifyNewsType> list,Activity activity) {
        super(R.layout.item_notify_news_type,list);
        this.activity = activity;
        Log.i("123321len",list.size()+"");
    }


            @Override
            protected void convert(BaseViewHolder holder, final NotifyNewsType notifyNewsType) {
                holder.setText(R.id.tv_type,notifyNewsType.getType_name());
                if (TextUtils.equals(notifyNewsType.getType_name(),"订单消息")) holder.setImageResource(R.id.img_news_type,R.drawable.img_news_order);
                if (TextUtils.equals(notifyNewsType.getType_name(),"互动消息")) holder.setImageResource(R.id.img_news_type,R.drawable.img_news_interact);
                if (TextUtils.equals(notifyNewsType.getType_name(),"通知消息")) holder.setImageResource(R.id.img_news_type,R.drawable.img_news_notify);

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity,NotifyNewsDetailActivity.class);
                        intent.putExtra("type",notifyNewsType.getType_name());
                        activity.startActivity(intent);
                    }
                });
            }
        }
