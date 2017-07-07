package com.sirc.tlt.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.renderscript.Type;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.toutiao.NewsCommentAdapter;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.model.AskAndAnswerModel;
import com.sirc.tlt.model.toutiao.CommentItem;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.activity.toutiao.NewsDetailActivity;
import com.sirc.tlt.ui.view.CustomProgressDialog;
import com.sirc.tlt.ui.view.KeyMapDailog;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DateUtils;
import com.sirc.tlt.utils.ImageUtil;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class QuestionDetailAcitivity extends BaseActivity implements Serializable ,BaseQuickAdapter.RequestLoadMoreListener{

    private AskAndAnswerModel askAndAnswerModel;

    //问题的model数据
    private TextView tv_questions_type,tv_question,tv_question_content,tv_question_person_name,
            tv_question_release_time,tv_question_praise_count,tv_question_comment_count,
            tv_question_concern,tv_all_answer,tv_like_count;
    private ImageView img_question_head_img,action_new_love;
    boolean concern = false;

    private RecyclerView mRecyclerView;

    KeyMapDailog dialog;

    int q_id;
    private NewsCommentAdapter mAdapter;
    private List<CommentItem> mData;
    private int currPage;

    int reply_num = 0;
    Dialog load_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_question_detail_acitivity);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏

        ButterKnife.bind(this);

        TemplateTitle title = (TemplateTitle) findViewById(R.id.question_title);
        title.setTitleText(getResources().getString(R.string.question_detail));
        title.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        currPage = 1;
        getData(currPage, false,false);
    }

    private void initView(){

        tv_questions_type = (TextView) findViewById(R.id.tv_questions_type);
        tv_question = (TextView) findViewById(R.id.tv_question);
        tv_question_content = (TextView) findViewById(R.id.tv_question_content);
        tv_question_person_name = (TextView) findViewById(R.id.tv_question_person_name);
        tv_question_release_time = (TextView) findViewById(R.id.tv_question_release_time);
        tv_question_praise_count = (TextView) findViewById(R.id.tv_question_praise_count);
        tv_question_comment_count = (TextView) findViewById(R.id.tv_question_comment_count);
        tv_question_concern = (TextView) findViewById(R.id.tv_question_concern);
        img_question_head_img = (ImageView) findViewById(R.id.img_question_head_img);

        tv_like_count = (TextView) findViewById(R.id.tv_like_count);
        action_new_love = (ImageView) findViewById(R.id.action_new_love);


        Intent intent = this.getIntent();
        if (null != intent) {
            Bundle b = intent.getExtras();
            askAndAnswerModel = b.getParcelable("question");
            tv_questions_type.setText(askAndAnswerModel.getLable());
            tv_question.setText(askAndAnswerModel.getTitle());
            tv_question_content.setText(askAndAnswerModel.getContent());
            tv_question_comment_count.setText(askAndAnswerModel.getReply()+"人评论");
            tv_like_count.setText(askAndAnswerModel.getPraise_num()+"");
            tv_question_person_name.setText(askAndAnswerModel.getUsername());
            tv_question_release_time.setText(DateUtils.friendlyTime(askAndAnswerModel.getCreate_time()));

            q_id = askAndAnswerModel.getQ_id();
            reply_num = askAndAnswerModel.getReply();

            if (askAndAnswerModel.isConcern()){
                action_new_love.setImageResource(R.drawable.new_love_tabbar_selected);
                concern = true;
            }else{
                action_new_love.setImageResource(R.drawable.new_love_tabbar);
            }
            if (askAndAnswerModel.getHead_img() == null){
                img_question_head_img.setImageResource(R.drawable.head_me);
            }else{
                byte [] bis=askAndAnswerModel.getHead_img();
                Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
                img_question_head_img.setImageBitmap(bitmap);
                bitmap.recycle();
            }

            action_new_love.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (concern){
                        showLoadDialog();
                        OkHttpUtils.post()
                                .url(Config.URL_COLLECT)
                                .addParams("cmd","d")
                                .addParams("user_id","123")
                                .addParams("type",Config.QUESTION)
                                .addParams("id",askAndAnswerModel.getQ_id()+"")
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int i) {
                                        Toast.makeText(mContext,
                                                mContext.getString(R.string.net_error),
                                                Toast.LENGTH_SHORT).show();
                                        load_dialog.cancel();
                                    }

                                    @Override
                                    public void onResponse(String response, int i) {
                                        load_dialog.cancel();
                                        Result result = JSON.parseObject(response, Result.class);
                                        if (result.getErr_code() == 0) {
                                            Toast.makeText(QuestionDetailAcitivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                                            action_new_love.setImageResource(R.drawable.new_love_tabbar);
                                            concern = false;
                                        } else {
                                            Toast.makeText(QuestionDetailAcitivity.this, "取消收藏失败！", Toast.LENGTH_SHORT).show();
                                            action_new_love.setImageResource(R.drawable.new_love_tabbar_selected);
                                        }
                                    }
                                });

                    }else{
                        showLoadDialog();
                        OkHttpUtils.post()
                                .url(Config.URL_COLLECT)
                                .addParams("cmd","w")
                                .addParams("user_id","123")
                                .addParams("type",Config.QUESTION)
                                .addParams("q_id",askAndAnswerModel.getQ_id()+"")
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int i) {
                                        Toast.makeText(mContext,
                                                mContext.getString(R.string.net_error),
                                                Toast.LENGTH_SHORT).show();
                                        load_dialog.cancel();
                                    }

                                    @Override
                                    public void onResponse(String response, int i) {
                                        load_dialog.cancel();
                                        Result result = JSON.parseObject(response, Result.class);
                                        if (result.getErr_code() == 0) {
                                            Toast.makeText(QuestionDetailAcitivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                                            action_new_love.setImageResource(R.drawable.new_love_tabbar_selected);
                                            concern = true;
                                        } else {
                                            Toast.makeText(QuestionDetailAcitivity.this, "收藏失败！", Toast.LENGTH_SHORT).show();
                                            action_new_love.setImageResource(R.drawable.new_love_tabbar);
                                        }
                                    }
                                });
                    }
                }
            });

            if (askAndAnswerModel.isPraise()){
                ImageUtil.setDrawbleLef(R.drawable.comment_like_icon_pressed
                        ,tv_like_count);
            }else {
                ImageUtil.setDrawbleLef(R.drawable.comment_like_icon
                        ,tv_like_count);
            }

            tv_question_comment_count.setVisibility(View.GONE);
            tv_question_praise_count.setVisibility(View.GONE);
            tv_question_concern.setVisibility(View.GONE);
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mAdapter = new NewsCommentAdapter(null,Config.QCOMMENT);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);

        //分享
        ImageView action_new_share = (ImageView) findViewById(R.id.action_new_share);
        action_new_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.showShare(QuestionDetailAcitivity.this,
                        askAndAnswerModel.getTitle(),
                        "", "这则新闻很不错哦！", "");
            }
        });

        }

    }

    //从后台获取评论列表
    public void getData(int page, final boolean loadMore, final boolean afterWriteComment) {
        OkHttpUtils.get().url(Config.URL_QUESTION_COMMENT)
                .addParams("cmd", "r")
                .addParams("s", String.valueOf(Config.PAGE_SIZE))
                .addParams("p", String.valueOf(page))
                .addParams("q_id", String.valueOf(q_id))
                .addParams("user_id","123")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(QuestionDetailAcitivity.this, "服务器开小差啦！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<CommentItem> data = JSON.parseArray(response, CommentItem.class);
                        if (!loadMore) {
                            mData = data;
                            for (CommentItem commentItem : data){
                                Log.i("后台获取评论列表", commentItem.getId()+"");
                            }
                            mAdapter.setNewData(mData);
                            LinearLayoutManager manager = new LinearLayoutManager(QuestionDetailAcitivity.this);
                            mRecyclerView.setLayoutManager(manager);
                            mRecyclerView.setAdapter(mAdapter);
                            //上拉加载更多数据
                            mAdapter.setOnLoadMoreListener(QuestionDetailAcitivity.this, mRecyclerView);
                        } else {
                            int dataSize = data.size();
                            if (dataSize > 0) {
                                mAdapter.addData(data);
                                mAdapter.loadMoreComplete();
                            } else {
                                mAdapter.loadMoreEnd();
                                currPage--;
                            }
                        }
                        if (afterWriteComment) {
                            mRecyclerView.scrollToPosition(0);
                        }
                    }
                });
    }

    @OnClick(R.id.tv_reply)
    public void reply() {
        dialog = new KeyMapDailog(String.valueOf(getResources().getText(R.string.say_something)), new KeyMapDailog.SendBackListener() {
            @Override
            public void sendBack(final String inputText) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hideProgressdialog();
                        if (!TextUtils.isEmpty(inputText))
                            OkHttpUtils
                                    .post()
                                    .url(Config.URL_QUESTION_COMMENT)
                                    .addParams("cmd", "w")
                                    .addParams("q_id", String.valueOf(q_id))
                                    .addParams("author", "guest")
                                    .addParams("content", inputText)
                                    .addParams("user_id","123")
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            Toast.makeText(QuestionDetailAcitivity.this, "服务器开小差啦！", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            Result result = JSON.parseObject(response, Result.class);
                                            if (result.getErr_code() == 0) {
                                                Toast.makeText(QuestionDetailAcitivity.this, "评论发表成功！", Toast.LENGTH_SHORT).show();
                                                dialog.getInputDlg().setText("");
                                                currPage = 1;
                                                getData(currPage, false, true);
                                            } else {
                                                Toast.makeText(QuestionDetailAcitivity.this, "评论发表失败！", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        dialog.dismiss();
                    }
                }, 2000);
            }
        });
        dialog.show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onLoadMoreRequested() {
        currPage++;
        getData(currPage, true,false);
    }

    private void showLoadDialog(){
        load_dialog = CustomProgressDialog.createCustomDialog(QuestionDetailAcitivity.this,
                "正在提交,请稍后...");
        load_dialog.setCanceledOnTouchOutside(false);
        load_dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isVisible()){
            dialog.dismiss();
        }
        if (load_dialog != null && load_dialog.isShowing()){
            load_dialog.dismiss();
        }
    }
}
