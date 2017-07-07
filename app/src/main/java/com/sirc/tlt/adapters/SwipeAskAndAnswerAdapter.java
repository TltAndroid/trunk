package com.sirc.tlt.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.AskAndAnswerAdapter;
import com.sirc.tlt.listener.OnItemClickListener;
import com.sirc.tlt.model.AskAndAnswerModel;
import com.sirc.tlt.model.MainDataModel;
import com.sirc.tlt.model.RechargePhoneModel;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.activity.CollectionActivity;
import com.sirc.tlt.ui.activity.QuestionDetailAcitivity;
import com.sirc.tlt.ui.view.CustomProgressDialog;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.ImageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * Created by Hooliganiam on 17/4/24.
 */

public class SwipeAskAndAnswerAdapter extends BaseQuickAdapter<AskAndAnswerModel, BaseViewHolder>
 implements View.OnClickListener{


    private List<AskAndAnswerModel> mDatas = new ArrayList<>();
    private Dialog dialog;
    private Activity activity;

    public SwipeAskAndAnswerAdapter(List<AskAndAnswerModel> list, Activity activity) {
        super(R.layout.swipe_item_ask_answer, list);
        this.mDatas = list;
        this.activity = activity;
        Log.d("adapter", mDatas.size() + "");
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final AskAndAnswerModel askAndAnswerModel) {

        baseViewHolder.setText(R.id.tv_questions_type, askAndAnswerModel.getLable());
        baseViewHolder.setText(R.id.tv_question, askAndAnswerModel.getTitle());
        baseViewHolder.setText(R.id.tv_question_content, askAndAnswerModel.getContent());
        baseViewHolder.setText(R.id.tv_like_count,
                askAndAnswerModel.getPraise_num()+"");
        baseViewHolder.setText(R.id.tv_question_comment_count,
                askAndAnswerModel.getReply() + "人评论");
        baseViewHolder.setText(R.id.tv_question_person_name,
                askAndAnswerModel.getUsername());
        baseViewHolder.setText(R.id.tv_question_release_time,
                askAndAnswerModel.getCreate_time());
        Glide.with(MyApplication.getContext()).load(R.drawable.head_me)
                .into((ImageView) baseViewHolder.getView(R.id.img_question_head_img));

//        if (askAndAnswerModel.isConcern()) {
//            baseViewHolder.setText(R.id.tv_question_concern,R.string.already_concerned);
//        } else {
//            baseViewHolder.setText(R.id.tv_question_concern,R.string.concern_question);
//        }
//        baseViewHolder.setOnClickListener(R.id.tv_question_concern, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (askAndAnswerModel.isConcern()) {
//                    baseViewHolder.setText(R.id.tv_question_concern,R.string.concern_question);
//                    askAndAnswerModel.setConcern(false);
//                } else {
//                    askAndAnswerModel.setConcern(true);
//                    baseViewHolder.setText(R.id.tv_question_concern,R.string.already_concerned);
//                }
//            }
//        });

        baseViewHolder.setOnClickListener(R.id.item_tv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = CustomProgressDialog.createCustomDialog(MyApplication.getContext()
                        , "正在提交,请稍后...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Log.d("adapter", "list大小:" + mDatas.size());
                OkHttpUtils.post()
                        .url(Config.URL_COLLECT)
                        .addParams("cmd", "d")
                        .addParams("user_id", "123")
                        .addParams("id", askAndAnswerModel.getQ_id() + "")
                        .addParams("type", Config.QUESTION)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                                Toast.makeText(MyApplication.getContext(),
                                        mContext.getString(R.string.net_error),
                                        Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }

                            @Override
                            public void onResponse(String s, int i) {
                                Result result = JSON.parseObject(s, Result.class);
                                if (result.getErr_code() == 0) {
                                    Toast.makeText(MyApplication.getContext(),
                                            "删除成功", Toast.LENGTH_SHORT).show();
                                    int pos = baseViewHolder.getAdapterPosition();
                                    Log.d("adapter", "pos:" + pos);
                                    mDatas.remove(pos);
                                    notifyDataSetChanged();
                                    //       notifyItemRemoved(pos);
                                } else {
                                    Toast.makeText(MyApplication.getContext(),
                                            "删除失败", Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();
                            }

                        });
                baseViewHolder.getConvertView().setClickable(true);
            }
        });

        if (TextUtils.isEmpty(askAndAnswerModel.getContent())) {
            baseViewHolder.setVisible(R.id.tv_question_content, false);
        }
        baseViewHolder.setVisible(R.id.tv_question_concern, false);

        final TextView textView = baseViewHolder.getView(R.id.tv_like_count);

        if (askAndAnswerModel.isPraise()) {
            ImageUtil.setDrawbleLef(R.drawable.comment_like_icon_pressed
                    , textView);
        } else {
            ImageUtil.setDrawbleLef(R.drawable.comment_like_icon
                    , textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpUtils.post()
                            .url(Config.URL_PRAISE)
                            .addParams("user_id", "123")
                            .addParams("q_id", String.valueOf(askAndAnswerModel.getQ_id()))
                            .addParams("belong", "问题")
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int i) {
                                    Toast.makeText(MyApplication.getContext(),
                                            mContext.getString(R.string.net_error),
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int i) {
                                    Result result = JSON.parseObject(response, Result.class);
                                    if (result.getErr_code() == 0) {
                                        askAndAnswerModel.setPraise(true);
                                        ImageUtil.setDrawbleLef(R.drawable.comment_like_icon_pressed
                                                , textView);
                                    } else {
                                        Toast.makeText(mContext,
                                                mContext.getString(R.string.net_error),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
        SwipeLayout swipeLayout = baseViewHolder.getView(R.id.swipe);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

//        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
//            @Override
//            public void onStartOpen(SwipeLayout swipeLayout) {
//
//            }
//
//            @Override
//            public void onOpen(SwipeLayout swipeLayout) {
//                //when the BottomView totally show.
//                swipeLayout.getSurfaceView().setClickable(false);
//            }
//
//            @Override
//            public void onStartClose(SwipeLayout swipeLayout) {
//
//            }
//
//            @Override
//            public void onClose(SwipeLayout swipeLayout) {
//                //when the SurfaceView totally cover the BottomView.
//            }
//
//            @Override
//            public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {
//                //you are swiping.
//                swipeLayout.getSurfaceView().setClickable(false);
//            }
//
//            @Override
//            public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {
//                //when user's hand released.
//            }
//        });
        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity,
                        QuestionDetailAcitivity.class);
                askAndAnswerModel.setHead_img(ImageUtil.ImageToByte(R.drawable.head_me));
                Bundle b = new Bundle();
                b.putParcelable("question", askAndAnswerModel);
                intent.putExtras(b);
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

}




