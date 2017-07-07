package com.sirc.tlt.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.data.DataServer;
import com.sirc.tlt.model.RechargePhoneModel;

import java.util.List;
import java.util.Map;

/**
 * Created by Hooliganiam on 17/5/7.
 */

//public class RechargePhoneAdapter extends RecyclerView.Adapter<RechargePhoneAdapter.ViewHolder> {
//
//
//    private List<RechargePhoneModel> list;
//    private Context mContext;
//
//
//    public RechargePhoneAdapter(Context context,List<RechargePhoneModel> list){
//        mContext = context;
//        this.list = list;
//    }
//
//    //RecyclerView显示的子View
//    //该方法返回是ViewHolder，当有可复用View时，就不再调用
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(mContext).inflate(R.layout.item_recharge_phone, null);
//        return new ViewHolder(v);
//    }
//
//    //将数据绑定到子View，会自动复用View
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.orignal_price.setText(String.valueOf(list.get(position).getOrignal_price()));
//        holder.sale_price.setText(String.valueOf(list.get(position).getSale_price()));
//        holder.on_sale_price.setText(String.valueOf(list.get(position).getOn_sale_price()));
//        if (list.get(position).is_on_sale()){
//            holder.on_sale_price.setVisibility(View.VISIBLE);
//        }else{
//            holder.on_sale_price.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder
//    {
//
//        TextView orignal_price;
//        TextView sale_price;
//        TextView on_sale_price;
//        public ViewHolder(View itemView)
//        {
//            super(itemView);
//            orignal_price = (TextView) itemView.findViewById(R.id.tv_orignal_price);
//            sale_price = (TextView) itemView.findViewById(R.id.tv_sale_price);
//            on_sale_price = (TextView) itemView.findViewById(R.id.tv_on_sale_price);
//        }
//
//    }
//}

public class RechargePhoneAdapter extends BaseQuickAdapter<RechargePhoneModel, BaseViewHolder>
        implements View.OnClickListener {

    private List<RechargePhoneModel> list;


    private String type; //流量还是话费

    public static RechargePhoneModel rechargePhoneModel = null;

    public RechargePhoneAdapter(List<RechargePhoneModel> list,String type) {
        super(R.layout.item_recharge_phone, list);
        this.list = list;
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargePhoneModel item) {
        if (TextUtils.equals(type,"话费")){
            Log.i("话费",item.getAmount()+"");
            helper.setText(R.id.tv_orignal_price, String.valueOf(item.getAmount()+"元"))
                    .setText(R.id.tv_sale_price, "售价:"+String.valueOf(item.getSale_price()+"元"));
        }else if (TextUtils.equals(type,"流量")){
            helper.setVisible(R.id.tv_sale_price, true);
            helper.setText(R.id.tv_orignal_price, String.valueOf(item.getFlow()+"M"))
                    .setText(R.id.tv_sale_price, "售价:"+String.valueOf(item.getSale_price()+"元"));
        }

//        if (item.is_on_sale()) {
//            helper.setVisible(R.id.tv_on_sale_price, true);
//        } else {
//            helper.setVisible(R.id.tv_on_sale_price, false);
//        }
//        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (last_view != null) {
//                    last_view.setBackgroundResource(R.drawable.item_view_border);
//                }
//                last_view = v;
//                v.setBackgroundResource(R.drawable.item_view_border_pressed);
//                rechargePhoneModel = item;
//            }
//        });
    }


    public RechargePhoneModel getChecked(){
        if (rechargePhoneModel != null){
            return rechargePhoneModel;
        }
        return null;
    }


    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.liner_item_recharge) {
//            if (last_view != null) {
//                last_view.setBackgroundResource(R.drawable.item_view_border);
//            }
//            last_view = v;
//            v.setBackgroundResource(R.drawable.item_view_border_pressed);
//        }
    }
}
