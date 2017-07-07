package com.sirc.tlt.adapters;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.model.GuessFancyModel;
import com.sirc.tlt.model.RollViewModel;
import com.sirc.tlt.utils.DensityUtil;

import java.util.List;

/**
 * Created by Hooliganiam on 17/5/11.
 */

public class ActionsAdapter extends BaseQuickAdapter<GuessFancyModel, BaseViewHolder>
{

    private List<GuessFancyModel> mData;

    public ActionsAdapter(int layoutResId, List<GuessFancyModel> data) {
        super(layoutResId, data);
        this.mData = data;
        Log.e("WeekendAdapter",mData.size()+"");
    }
    @Override
    protected void convert(BaseViewHolder holder, GuessFancyModel model) {
        Log.e("WeekendAdapter","adapter");
        int width = DensityUtil.dip2px(MyApplication.getContext(),120);
        int heigth = DensityUtil.dip2px(MyApplication.getContext(),120);
        holder.setText(R.id.tv_weekend_action,model.getTitle());
        Glide.with(MyApplication.getContext())
                .load(model.getThumbnail().get(0))
 //               .override(width,heigth)
 //               .fitCenter()
                .into((ImageView) holder.getView(R.id.img_weekend_action));
    }
}
