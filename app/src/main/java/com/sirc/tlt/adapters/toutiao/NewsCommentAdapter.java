package com.sirc.tlt.adapters.toutiao;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.model.toutiao.CommentItem;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.activity.QuestionDetailAcitivity;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DateUtils;
import com.sirc.tlt.utils.ImageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/5/3.
 */

public class NewsCommentAdapter extends BaseQuickAdapter<CommentItem, BaseViewHolder> {

    String type;
    String id_key = "";

    public NewsCommentAdapter(List<CommentItem> data,String type) {
        super(R.layout.item_comment, data);
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, final CommentItem item) {
        if (TextUtils.equals(type,Config.TCOMMENT)){
            id_key = "tc_id";
        }else if (TextUtils.equals(type,Config.QCOMMENT)){
            id_key = "qc_id";
        }

        helper.setText(R.id.tv_user_name, "匿名用户");
        helper.setText(R.id.tv_content, item.getContent());
        helper.setText(R.id.comment_time, DateUtils.friendlyTime(item.getTime()));

        final TextView textView = helper.getView(R.id.tv_like_count);
        textView.setText(item.getPraise_num()+"");

        if (item.isPraise()){
            ImageUtil.setDrawbleLef(R.drawable.comment_like_icon_pressed
                    ,textView);
        }else {
            ImageUtil.setDrawbleLef(R.drawable.comment_like_icon
                    ,textView);
            Log.i("评论",id_key+"-"+item.getId());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!item.isPraise()){
                    OkHttpUtils.post()
                            .url(Config.URL_PRAISE)
                            .addParams("user_id","123")
                            .addParams(id_key,String.valueOf(item.getId()))
                            .addParams("belong",type)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int i) {
                                    Toast.makeText(mContext,
                                            mContext.getString(R.string.net_error),
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int i) {
                                    Result result = JSON.parseObject(response, Result.class);
                                    if (result.getErr_code() == 0){
                                        item.setPraise(true);
                                        ImageUtil.setDrawbleLef(R.drawable.comment_like_icon_pressed
                                        ,textView);
                                        textView.setText(item.getPraise_num()+1+"");

                                    }else{
                                        Toast.makeText(mContext,
                                                mContext.getString(R.string.net_error),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                }
            });
        }
    }
}
