package com.sirc.tlt.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.AskAndAnswerModel;
import com.sirc.tlt.model.TlwServiceModel;
import com.sirc.tlt.model.TlwServiceTeamModel;
import com.sirc.tlt.ui.activity.MyWalletActivity;
import com.sirc.tlt.ui.activity.RechargePhoneActivity;
import com.sirc.tlt.ui.activity.WebViewActivity;

import java.util.List;

/**
 * Created by Hooliganiam on 17/6/12.
 */

public class TlwServiceTeamAdapter extends BaseQuickAdapter<TlwServiceTeamModel, BaseViewHolder>
  {
    Context context = MyApplication.getContext();

    private Activity activity;
    public TlwServiceTeamAdapter(int layoutResId, List<TlwServiceTeamModel> data, Activity activity) {
        super(layoutResId, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder holder, TlwServiceTeamModel model) {

         if (TextUtils.equals(context.getString(R.string.tlw_service_card),model.getHead())){
             setBackGround(holder,R.color.tlw_service_card);
             setHeadDrawble(holder,R.drawable.img_service_card);
         }else if (TextUtils.equals(context.getString(R.string.tlw_service_communication),model.getHead())){
             setBackGround(holder,R.color.tlw_service_communication);
             setHeadDrawble(holder,R.drawable.img_service_communicate);
         }else if (TextUtils.equals(context.getString(R.string.tlw_service_traffic),model.getHead())){
                 setBackGround(holder,R.color.tlw_service_traffic);
             setHeadDrawble(holder,R.drawable.img_service_traffic);
         }else if (TextUtils.equals(context.getString(R.string.tlw_service_life),model.getHead())){
             setBackGround(holder,R.color.tlw_service_life);
             setHeadDrawble(holder,R.drawable.img_service_life);
         }
         List<TlwServiceModel> list = model.getServiceModels();
         if (TextUtils.equals("租车",list.get(2).getName())){
             list.get(2).setName("临时驾照");
             list.get(2).setUrl("");
         }
        if (TextUtils.equals("帮您跑腿",list.get(3).getName())){
            list.get(3).setName("营业执照");
        }

         holder.setText(R.id.tv_service_head,model.getHead())
                 .setText(R.id.tv_item_1,list.get(0).getName())
                 .setText(R.id.tv_item_2,list.get(1).getName())
                 .setText(R.id.tv_item_3,list.get(2).getName())
                 .setText(R.id.tv_item_4,list.get(3).getName());

        setOnClick(holder,R.id.tv_item_1,list.get(0));
        setOnClick(holder,R.id.tv_item_2,list.get(1));
        setOnClick(holder,R.id.tv_item_3,list.get(2));
        setOnClick(holder,R.id.tv_item_4,list.get(3));

    }

    private void setBackGround(BaseViewHolder holder,int colorId){
        holder.getConvertView().setBackgroundColor(context.getResources()
                .getColor(colorId));
    }

    private void  setHeadDrawble(BaseViewHolder holder,int drawbleId){
        TextView textView = holder.getView(R.id.tv_service_head);
        Drawable drawable= activity.getResources().getDrawable(drawbleId);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null,drawable,null,null);
    }

    private void setOnClick(final BaseViewHolder holder, int id, final TlwServiceModel model){
            holder.setOnClickListener(id, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.equals("话费充值",model.getName())){
                        Intent intent = new Intent(activity,RechargePhoneActivity.class);
                        activity.startActivity(intent);
                    }else if (TextUtils.isEmpty(model.getUrl())){
                        ToastUtil.showShortToast(activity,"正在开发中...");
                    }else {
                        Intent intent = new Intent(activity, WebViewActivity.class);
                        intent.putExtra("title", model.getName());
                        intent.putExtra("url", model.getUrl());
                        activity.startActivity(intent);
                    }

                }
            });
    }
}
