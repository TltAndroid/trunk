package com.sirc.tlt.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.listener.OnItemClickListener;
import com.sirc.tlt.model.AskAndAnswerModel;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DateUtils;
import com.sirc.tlt.utils.ImageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * Created by Hooliganiam on 17/4/24.
 */
public class AskAndAnswerAdapter extends BaseQuickAdapter<AskAndAnswerModel, BaseViewHolder>
        implements View.OnClickListener {

    private List<AskAndAnswerModel> mDatas = new ArrayList<>();

    public AskAndAnswerAdapter(List<AskAndAnswerModel> list) {
        super(R.layout.item_ask_answer, list);
        this.mDatas = list;
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final AskAndAnswerModel askAndAnswerModel) {
        baseViewHolder.setText(R.id.tv_questions_type,askAndAnswerModel.getLable());
        baseViewHolder.setText(R.id.tv_question,askAndAnswerModel.getTitle());
        baseViewHolder.setText(R.id.tv_question_content,askAndAnswerModel.getContent());
        baseViewHolder.setText(R.id.tv_like_count,
                askAndAnswerModel.getPraise_num()+"");
        baseViewHolder.setText(R.id.tv_question_comment_count,
                askAndAnswerModel.getReply() + "人评论");
        baseViewHolder.setText(R.id.tv_question_person_name,
                askAndAnswerModel.getUsername());
        baseViewHolder.setText(R.id.tv_question_release_time,
                DateUtils.friendlyTime(askAndAnswerModel.getCreate_time()));
        Glide.with(MyApplication.getContext()).load(R.drawable.head_me)
                .into((ImageView)baseViewHolder.getView(R.id.img_question_head_img));

        if (askAndAnswerModel.isConcern()) {
            baseViewHolder.setText(R.id.tv_question_concern,R.string.already_concerned);
        } else {
            baseViewHolder.setText(R.id.tv_question_concern,R.string.concern_question);
        }
        baseViewHolder.setOnClickListener(R.id.tv_question_concern, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (askAndAnswerModel.isConcern()) {
                    baseViewHolder.setText(R.id.tv_question_concern,R.string.concern_question);
                    askAndAnswerModel.setConcern(false);
                } else {
                    askAndAnswerModel.setConcern(true);
                    baseViewHolder.setText(R.id.tv_question_concern,R.string.already_concerned);
                }
            }
        });

        baseViewHolder.setOnClickListener(R.id.item_tv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = baseViewHolder.getAdapterPosition();
                mDatas.remove(pos);
                notifyItemRemoved(pos);
            }
        });

        if (TextUtils.isEmpty(askAndAnswerModel.getContent())){
            baseViewHolder.setVisible(R.id.tv_question_content,false);
        }
        baseViewHolder.setVisible(R.id.tv_question_concern,false);

        final TextView textView = baseViewHolder.getView(R.id.tv_like_count);

        if (askAndAnswerModel.isPraise()){
            ImageUtil.setDrawbleLef(R.drawable.comment_like_icon_pressed
                    ,textView);
        }else{
            ImageUtil.setDrawbleLef(R.drawable.comment_like_icon
                    ,textView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpUtils.post()
                            .url(Config.URL_PRAISE)
                            .addParams("user_id","123")
                            .addParams("q_id",String.valueOf(askAndAnswerModel.getQ_id()))
                            .addParams("belong","问题")
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
                                        askAndAnswerModel.setPraise(true);

                                        askAndAnswerModel.setPraise(true);
                                        ImageUtil.setDrawbleLef(R.drawable.comment_like_icon_pressed
                                                ,textView);
                                        textView.setText(askAndAnswerModel.getPraise_num()+1+"");
                                    }else{
                                        Toast.makeText(mContext,
                                                mContext.getString(R.string.net_error),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }



}