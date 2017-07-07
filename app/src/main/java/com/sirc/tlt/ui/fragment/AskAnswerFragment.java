package com.sirc.tlt.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.AskAndAnswerAdapter;
import com.sirc.tlt.model.AALableModel;
import com.sirc.tlt.model.AskAndAnswerModel;
import com.sirc.tlt.model.toutiao.Result;
import com.sirc.tlt.ui.activity.CollectionActivity;
import com.sirc.tlt.ui.activity.QuestionDetailAcitivity;
import com.sirc.tlt.ui.activity.ReleaseQuestionActivity;
import com.sirc.tlt.ui.activity.toutiao.NewsDetailActivity;
import com.sirc.tlt.ui.view.CustomProgressDialog;
import com.sirc.tlt.ui.view.DrawableCenterTextView;
import com.sirc.tlt.ui.view.KeyMapDailog;
import com.sirc.tlt.ui.view.SearchEditText;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DensityUtil;
import com.sirc.tlt.utils.ImageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hooliganiam on 17/4/19.
 */
public class AskAnswerFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.RequestLoadMoreListener {
    private static final String TAG = AskAnswerFragment.class.getSimpleName();
    private View view;
    private RecyclerView listview_questions;
    private SearchEditText ask_answer_search;
    private AskAndAnswerAdapter askAndAnswerAdapter;
    private List<AskAndAnswerModel> list;

    private PopupWindow MapMenuPop;

    private DrawableCenterTextView tv_release_question;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int currPage;
    private boolean isPrepared;
    private boolean isFirst;

    KeyMapDailog dialog;
    private Dialog load_dialog;
    private static final int RELEASE_QUESTION = 00001;

    //排序方式
    private String sort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.fragment_ask_answer, null);
            }
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
            parent.removeView(view);
            }
            isPrepared = true;
            isFirst = true;
        lazyLoad();
        Log.d("fragment",TAG);
            return view;
    }

    private void initView() {
        ask_answer_search = (SearchEditText) view.findViewById(R.id.ask_answer_search);

        ask_answer_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) {
                    //点击搜索要做的操作
                    currPage = 1;
                    setData(currPage, false,ask_answer_search.getText().toString(),"");
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ask_answer_search.getWindowToken(), 0);
                }
                return false;
            }
        });

        tv_release_question = (DrawableCenterTextView) view.findViewById(R.id.tv_release_question);
        tv_release_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReleaseQuestionActivity.class);
                startActivityForResult(intent,RELEASE_QUESTION);
            }
        });

    }


    private void setData(int page, final boolean loadMore,String searchKey,String sort) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_ask_fragment);

        listview_questions = (RecyclerView) view.findViewById(R.id.listview_questions);

        OkHttpUtils.post()
                .url(Config.URL_Questions)
                .addParams("searchKey",searchKey)
                .addParams("cmd","r")
                .addParams("s", String.valueOf(Config.PAGE_SIZE))
                .addParams("p", String.valueOf(page))
                .addParams("user_id","123")
                .addParams("sort",sort)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(),
                                getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        if (load_dialog != null){
                            load_dialog.cancel();
                        }
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        List<AskAndAnswerModel> data = JSON.parseObject(s,new TypeReference<List<AskAndAnswerModel>>() {
                        });
                        if (askAndAnswerAdapter == null) {
                            list = data;
                            askAndAnswerAdapter = new AskAndAnswerAdapter(list);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            listview_questions.setLayoutManager(linearLayoutManager);// 布局管理器。

                            listview_questions.setAdapter(askAndAnswerAdapter);

                            setItemListener();

                            //上拉加载
                            askAndAnswerAdapter.setOnLoadMoreListener(AskAnswerFragment.this,
                                    listview_questions);
                            //下拉刷新
                            mSwipeRefreshLayout.setOnRefreshListener(AskAnswerFragment.this);
                            mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
                        } else {
                            if (loadMore) { //加载更多
                                mSwipeRefreshLayout.setEnabled(false);
                                if (data.size() > 0) {
                                    askAndAnswerAdapter.addData(data);
                                    askAndAnswerAdapter.loadMoreComplete();
                                } else {
                                    askAndAnswerAdapter.loadMoreEnd();
                                    currPage--;
                                }
                                mSwipeRefreshLayout.setEnabled(true);
                            } else {
                                list = data;
                                askAndAnswerAdapter.setEnableLoadMore(false);
                                askAndAnswerAdapter.setNewData(list);
                                mSwipeRefreshLayout.setRefreshing(false);
                                askAndAnswerAdapter.setEnableLoadMore(true);
                                askAndAnswerAdapter.notifyDataSetChanged();
                            }

                        }
                        if (load_dialog != null){
                            load_dialog.cancel();
                        }
                    }

                });

    }

    private void setItemListener() {
        askAndAnswerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),
                        QuestionDetailAcitivity.class);
                list.get(position).setHead_img(ImageUtil.ImageToByte(R.drawable.head_me));
                Bundle b = new Bundle();
                b.putParcelable("question", list.get(position));
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    /**
     * 右上角菜单popwindow
     */
    private void getMenuPopupWindow() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_check_popview, null);
        TextView tv_all_questions = (TextView) view.findViewById(R.id.tv_pop_all_questions);
        TextView tv_hot_questions = (TextView) view.findViewById(R.id.tv_pop_hot_questions);
        TextView tv_lasted_questions = (TextView) view.findViewById(R.id.tv_pop_lasted_questions);
        TextView tv_my_collected_questions = (TextView) view.findViewById(R.id.tv_pop_my_collected_questions);

        tv_all_questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = getString(R.string.all_questions);
                showLoadingDialog();
                setmSwipeRefres();
                if (MapMenuPop != null){
                    MapMenuPop.dismiss();
                }
            }
        });
        tv_hot_questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = getString(R.string.hot_questions);
                if (MapMenuPop != null){
                    MapMenuPop.dismiss();
                }
            }
        });
        tv_lasted_questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = getString(R.string.lasted_questions);
                showLoadingDialog();
                currPage = 1;
                setData(currPage, false,"",getString(R.string.lasted_questions));
                if (MapMenuPop != null){
                    MapMenuPop.dismiss();
                }
            }
        });
        tv_my_collected_questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.getIsLogin(getActivity())){
                    CommonUtil.startToLoginActivity(getActivity());
                }else {
                    Intent intent = new Intent(getActivity(), CollectionActivity.class);
                    startActivity(intent);
                }
                if (MapMenuPop != null){
                    MapMenuPop.dismiss();
                }
            }
        });
        MapMenuPop = new PopupWindow(view,DensityUtil.dip2px(getActivity(),120),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        MapMenuPop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        MapMenuPop.setFocusable(true);
        MapMenuPop.setTouchable(true);
        MapMenuPop.update();

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MapMenuPop != null && MapMenuPop.isShowing()) {
                    MapMenuPop.dismiss();
                    // SetDocFontPopupWindow = null;
                }
                return true;
            }
        });
    }

    /**
     * 刷新监听。
     */
    @Override
    public void onRefresh() {
        setmSwipeRefres();
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMoreRequested() {
        currPage++;
        String str = ask_answer_search.getText().toString();
        if (TextUtils.isEmpty(str)){
            if (!TextUtils.isEmpty(sort)){
                setData(currPage, true,"",sort);
            }
        }else setData(currPage, true,str,"");

    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
        if (isFirst){
            showLoadingDialog();
        TemplateTitle title = (TemplateTitle) view.findViewById(R.id.ask_answer_title);

        title.setTitleText(getResources().getString(R.string.ask_answer));
        final ImageView img_more = (ImageView) title.findViewById(R.id.img_more);
        img_more.setImageResource(R.drawable.img_add);
        title.setMoreImgAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuPopupWindow();
                MapMenuPop.showAsDropDown(img_more, DensityUtil.dip2px(getActivity(),30),
                        DensityUtil.dip2px(getActivity(),10));
            }
        });

        initView();
        currPage = 1;
        setData(currPage, false,"","");
        }
        isFirst = false;
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
                            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (resultCode == RESULT_OK){
                if (requestCode == RELEASE_QUESTION){
                    setmSwipeRefres();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst && isVisible){
            setmSwipeRefres();
        }
    }

    private void setmSwipeRefres(){
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ask_answer_search != null){
                    ask_answer_search.setHint(getString(R.string.search));
                }
                currPage = 1;
                setData(currPage, false,"","");
            }
        }, 2000);
    }

    private void showLoadingDialog(){
        load_dialog = CustomProgressDialog.createCustomDialog(getActivity(),getString(R.string.loading));
        load_dialog.setCanceledOnTouchOutside(false);
        load_dialog.show();
    }
}