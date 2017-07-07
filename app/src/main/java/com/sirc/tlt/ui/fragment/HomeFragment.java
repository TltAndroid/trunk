package com.sirc.tlt.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.mob.tools.utils.Data;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.adapters.ActionsAdapter;
import com.sirc.tlt.adapters.RechargePhoneAdapter;
import com.sirc.tlt.adapters.SwipeAskAndAnswerAdapter;
import com.sirc.tlt.adapters.TlwServiceTeamAdapter;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.GuessFancyModel;
import com.sirc.tlt.model.MainDataModel;
import com.sirc.tlt.model.RollViewModel;
import com.sirc.tlt.model.TlwServiceModel;
import com.sirc.tlt.model.TlwServiceTeamModel;
import com.sirc.tlt.ui.activity.CollectionActivity;
import com.sirc.tlt.ui.activity.LoginActivity;
import com.sirc.tlt.ui.activity.MainActivity;
import com.sirc.tlt.ui.activity.MyWalletActivity;
import com.sirc.tlt.ui.activity.PermissionsActivity;
import com.sirc.tlt.ui.activity.ShowQRCodeActivity;
import com.sirc.tlt.ui.activity.WebViewActivity;
import com.sirc.tlt.ui.activity.toutiao.NewsDetailActivity;
import com.sirc.tlt.ui.activity.toutiao.ToutiaoActivity;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DateUtils;
import com.sirc.tlt.utils.DensityUtil;
import com.sirc.tlt.utils.ImageUtil;
import com.sirc.tlt.utils.PermissionsChecker;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.sirc.tlt.utils.SpaceItemDecoration;
import com.sirc.tlt.zxing.android.CaptureActivity;
import com.sunfusheng.marqueeview.MarqueeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 主界面
 * Created by Hooliganiam on 17/4/19.
 */
public class HomeFragment extends LazyFragment implements BaseQuickAdapter.RequestLoadMoreListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    Unbinder unbinder;
    private View view;
    private RollPagerView mRollViewPager;


    private LinearLayout liner_add_activity; //猜你喜欢布局

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;

    private MarqueeView marqueeView;


    private String result;


    private List<RollViewModel> rollViewModelList;

    private List<GuessFancyModel> marqueeList;

    private List<TlwServiceModel> tlwServiceModelList;

    private List<GuessFancyModel> guessFancyModelList;

    private List<GuessFancyModel> ToutiaoList;

    private List<GuessFancyModel> ActionsList;

    private int[] liner_id = new int[]{
            R.id.liner_tlt_service_identify_card, R.id.liner_tlt_service_rent_car,R.id.liner_tlt_service_entry_card,R.id.liner_tlt_service_boat_ticket,
            R.id.liner_tlt_service_phone_card, R.id.liner_tlt_service_flow_card,
            R.id.liner_tlt_service_rent_mifi,
            R.id.liner_tlt_service_legwork

    };

    private RecyclerView recyclerView_toutiao;
    private ActionsAdapter weekendAdapter;

    private boolean isPrepared;
    private boolean isFirst;

    private RecyclerView recyclerView_actions;


    //台陆通服务分组模块
    private RecyclerView recyclerView_service;
    private List<TlwServiceTeamModel> serviceTeamList;


    static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_SMS
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.CALL_PHONE
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.fragment_home_page, null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        isPrepared = true;
        isFirst = true;
        lazyLoad();
        return view;


    }


    public void initRollView() {
        mRollViewPager = (RollPagerView) view.findViewById(R.id.roll_pager_view);

//        mRollViewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                mRollViewPager.getHeight()+CommonUtil.getStateBarHeight(getActivity())));
        //设置播放时间间隔
        mRollViewPager.setPlayDelay(2000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(500);
        //设置适配器
        mRollViewPager.setAdapter(new TestNormalAdapter());

        //设置指示器（顺序依次）
        //自定义指示器图片
        //设置圆点指示器颜色
        //设置文字指示器
        //隐藏指示器
        //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
        mRollViewPager.setHintView(new ColorPointHintView(getActivity(), Color.YELLOW, Color.WHITE));
        //mRollViewPager.setHintView(new TextHintView(this));
        //mRollViewPager.setHintView(null);
        mRollViewPager.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                if (rollViewModelList != null && rollViewModelList.size() > 0) {
//                    startWebActivity(rollViewModelList.get(position).getTitle(),
//                            rollViewModelList.get(position).getUrl());
//                }

            }
        });
    }


    public void initMarqueeView() {
        marqueeView = (MarqueeView) view.findViewById(R.id.marqueeView);
        final List<String> info = new ArrayList<>();
        for (GuessFancyModel guessFancyModel : marqueeList) {
            String date = DateUtils.getStringYear(
                    DateUtils.getStringToDate(guessFancyModel.getPost_time()));
            info.add(guessFancyModel.getTitle()+"    "+date);
        }
        marqueeView.startWithList(info);

        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                startWebActivity(marqueeList.get(position).getTitle(),
                        marqueeList.get(position).getUrl());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        unbinder.unbind();
//        Toast.makeText(getActivity(), "onDestroyView", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void lazyLoad() {

        if (!isPrepared || !isVisible) {
            return;
        }
        if (isFirst) {
            unbinder = ButterKnife.bind(this, view);
            String url = Config.URL_MAIN;
            Log.d(TAG,url);
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {

                            ToastUtil.showShortToast(getActivity(),getString(R.string.net_error));
                        }

                        @Override
                        public void onResponse(String s, int j) {
                            if (!TextUtils.isEmpty(s)) {
                                result = s;
                                Log.d(TAG,result);
                                JSONArray json = JSON.parseArray(s);

                                rollViewModelList = JSON.parseObject(
                                        json.getString(0), new TypeReference<List<RollViewModel>>() {
                                        });

                                tlwServiceModelList = JSON.parseObject(json.getString(1), new TypeReference<List<TlwServiceModel>>() {
                                });
                                marqueeList = JSON.parseObject(json.getString(2), new TypeReference<List<GuessFancyModel>>() {
                                });
                                ToutiaoList = JSON.parseObject(json.getString(3), new TypeReference<List<GuessFancyModel>>() {
                                });
                                ActionsList = JSON.parseObject(json.getString(4), new TypeReference<List<GuessFancyModel>>() {
                                });
                                guessFancyModelList = JSON.parseObject(json.getString(5), new TypeReference<List<GuessFancyModel>>() {
                                });

                                Log.d(TAG, result);

                                //动态加载轮播图
                                initRollView();
                                //动态初始化台陆通服务
                                initTlwService();
                                //动态加载跑马灯
                                initMarqueeView();
                                //初始化 猜你喜欢
                                initGuessFancy();

                                //初始化好康头条
                                initRecycleView();

                                //初始化逗阵秩陶
                                initRecycleViewActions();
                            }
                        }

                    });
//        liner_tlt_service_phone_card = (LinearLayout) view.findViewById(R.id.liner_tlt_service_phone_card);
//        liner_tlt_service_phone_card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), WebViewActivity.class);
//                intent.putExtra("title", "电话卡");
//                intent.putExtra("url", "http://120.24.242.176:8080/tlt-toutiao/tt.jsp?id=112");
//                startActivity(intent);
//            }
//        });
            //动态加载活动标签
            getLoadActionsType();
            TemplateTitle title = (TemplateTitle) view.findViewById(R.id.HomeTitle);
            title.setMoreImgAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ShowQRCodeActivity.class);
                    startActivity(intent);
                }
            });
//            ImageView img_swipe = (ImageView) view.findViewById(R.id.img_swipe);
//            img_swipe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), ShowQRCodeActivity.class);
//                    startActivity(intent);
//                }
//            });
            isFirst = false;
        }
    }


    private class TestNormalAdapter extends StaticPagerAdapter {

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            Glide.with(getActivity())
                    .load(rollViewModelList.get(position).getImg())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.img_loading_pic)
                    .error(R.drawable.img_loading_pic)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
//                            Toast.makeText(getActivity(), "异常=="+e+"===="
//                                    +"内容==="+s, Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"异常=="+e+"===="
                                    +"内容==="+s+"====="+b);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                    //        Toast.makeText(getActivity(), "onResourceReady=="+s, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    })
                    .into(view)
            ;

            view.setAdjustViewBounds(true);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        @Override
        public int getCount() {
            if (rollViewModelList != null && rollViewModelList.size()>0){
                return rollViewModelList.size();
            }else{
                return  0;
            }
        }
    }

    /**
     * 动态加载活动标签
     */
    private void getLoadActionsType() {
        OkHttpUtils.post()
                .url(Config.URL_TT_LABS)
                .addParams("type","dz")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.showShortToast(getActivity(),getString(R.string.net_error));
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        JSONArray json = JSON.parseArray(s);
                        if (json.size()>0){
                            liner_add_activity = (LinearLayout) view.findViewById(R.id.liner_add_activity);
                            for (int j = 0; j < json.size(); j++) {
                                TextView textView = new TextView(getActivity());
                                LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1
                                );
                                tvParams.gravity = Gravity.CENTER_VERTICAL;
                                tvParams.setMargins(10, 0, 0, 0);
                                textView.setGravity(Gravity.CENTER);
                                textView.setText(json.get(j).toString());
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                textView.setTextColor(getResources().getColor(R.color.text_gray));
                                textView.setBackgroundResource(R.drawable.actions_text_background);
                                textView.setPadding(3,3,3,3);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                              Intent intent = new Intent(getActivity(), LoginActivity.class);
//                              startActivity(intent);
                                    }
                                });
                                liner_add_activity.addView(textView, tvParams);
                            }
                        }
                    }
                });
    }

    /**
     * 初始化猜你喜欢的内容
     */
    private void initGuessFancy() {

        int title_id = 10, time_id = 50, reply_id = 100, resource_id = 150;
        LinearLayout liner_guess_fancy = (LinearLayout) view.findViewById(R.id.liner_guess_fancy);
        for (final GuessFancyModel guessFancyModel : guessFancyModelList) {

            LinearLayout liner = new LinearLayout(getActivity());
            LinearLayout.LayoutParams liner_params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 200
            );
            liner.setOrientation(LinearLayout.HORIZONTAL);

            //编写图片布局
            ImageView image = new ImageView(getActivity());
            LinearLayout.LayoutParams image_params = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1
            );
            image.setPadding(5, 5, 5, 5);
            image_params.setMargins(10, 10, 10, 10);

            Glide.with(getActivity())
                    .load(guessFancyModel.getThumbnail().get(0))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(image);
            image.setAdjustViewBounds(true);
            image.setScaleType(ImageView.ScaleType.FIT_XY);

            liner.addView(image, image_params);

            //编写动态文字布局
            RelativeLayout rl = new RelativeLayout(getActivity());

            //头条标题
            TextView title = new TextView(getActivity());
            RelativeLayout.LayoutParams title_params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            title_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            title_params.setMargins(10, 10, 0, 10);
            title.setId(Integer.valueOf(title_id));
            title.setGravity(Gravity.START);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            title.setTextColor(getResources().getColor(R.color.text_black));
            title.setText(guessFancyModel.getTitle());
            rl.addView(title, title_params);

            //头条来源
            TextView resource = new TextView(getActivity());
            RelativeLayout.LayoutParams resource_params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            resource_params.setMargins(10, 10, 10, 10);

            resource_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            resource_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            resource.setGravity(Gravity.CENTER);

            resource.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            resource.setTextColor(getResources().getColor(R.color.text_gray));
            resource.setText(guessFancyModel.getOrigin());
            resource.setId(Integer.valueOf(resource_id));
            rl.addView(resource, resource_params);

            //举办时间
            TextView time = new TextView(getActivity());
            RelativeLayout.LayoutParams time_params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            time_params.setMargins(10, 10, 10, 10);
            time_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            time_params.addRule(RelativeLayout.RIGHT_OF, resource_id);

            time.setGravity(Gravity.CENTER);
            time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            time.setTextColor(getResources().getColor(R.color.text_gray));
            time.setText(DateUtils.friendlyTime(guessFancyModel.getPost_time()));
            time.setId(Integer.valueOf(time_id));
            rl.addView(time, time_params);


//            //举办城市
//            TextView city = new TextView(getActivity());
//            RelativeLayout.LayoutParams city_params = new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            city_params.setMargins(10, 10, 10, 10);
//            //在time的右边
//            city_params.addRule(RelativeLayout.RIGHT_OF, time_id);
//            city_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//
//            city.setGravity(Gravity.CENTER);
//            city.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//            city.setTextColor(getResources().getColor(R.color.text_gray));
//            //     city.setText("在" + guessFancyModel1.getCity() + "举办");
//            rl.addView(city, city_params);

            //跟帖数
            TextView reply = new TextView(getActivity());
            RelativeLayout.LayoutParams reply_params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            reply_params.setMargins(10, 10, 10, 10);
            reply_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            reply_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            reply.setPadding(5, 5, 5, 5);
            reply.setGravity(Gravity.CENTER);
            reply.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            reply.setTextColor(getResources().getColor(R.color.text_gray));
//            reply.setBackgroundResource(R.drawable.text_view_border);
            reply.setId(Integer.valueOf(reply_id));
            int num = guessFancyModel.getReply();
//            float d;
            DecimalFormat df = new DecimalFormat("0.0");
            if (num > 10000) {
//                d = num/10000;
                reply.setText(df.format((float) num / 10000) + "万跟帖");
            } else {
                reply.setText(num + "跟帖");
            }
            rl.addView(reply, reply_params);

            LinearLayout.LayoutParams rl_params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 3
            );
            rl_params.setMargins(10, 10, 10, 10);
            //添加动态RelativeLayout布局
            liner.addView(rl, rl_params);

            //添加动态linerLayout布局
            liner_guess_fancy.addView(liner, liner_params);

            TextView line = new TextView(getActivity());
            LinearLayout.LayoutParams line_params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1
            );
            line_params.setMargins(0, 3, 0, 0);
            line.setBackgroundColor(getResources().getColor(R.color.simple_gray));
            liner_guess_fancy.addView(line, line_params);

            liner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
             //       startWebActivity("好康头条", guessFancyModel.getUrl());
                    startToutiaoActivity(guessFancyModel.getTitle(),
                            guessFancyModel.getUrl(),
                            guessFancyModel.getId());
                }
            });

            title_id++;
            time_id++;
            reply_id++;
            resource_id++;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN) {
            if (data != null) {

                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);

                Toast.makeText(getActivity(), "解码结果" + content, Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            ToastUtil.showShortToast(getActivity(),"该功能可能无法使用,请到设置中开启应用呢权限");
        }
    }


    /**
     * 初始化台陆通服务数据
     */
    private void initTlwService() {
//        if (tlwServiceModelList != null && tlwServiceModelList.size() ==
//                liner_id.length) {
//            for (int i = 0; i < tlwServiceModelList.size(); i++) {
//                setTlwServiceClick(liner_id[i], tlwServiceModelList.get(i).getName(),
//                        tlwServiceModelList.get(i).getUrl()
//                );
//            }
//        }

        if (tlwServiceModelList != null && tlwServiceModelList.size() != 0){
        String[] arr = new String[]{
                "证照","通讯","交通","生活服务"
        };
        String[] service = new String[]{
                    "特价酒店","公交地铁","神州专车","周边游"
            };
            String[] traffic = new String[]{
                    "海峡/丽娜号","小三通","神州租车","动车票"
            };
        serviceTeamList = new ArrayList<>();
        recyclerView_service = (RecyclerView) view.findViewById(R.id.recyclerView_service);
        for (int i = 0;i < arr.length;i++){
            List<TlwServiceModel> list = new ArrayList<>();
            TlwServiceTeamModel model = new TlwServiceTeamModel();
            model.setHead(arr[i]);
            if (i==0){
                list.add(tlwServiceModelList.get(0));
                list.add(tlwServiceModelList.get(2));
                list.add(tlwServiceModelList.get(1));
                list.add(tlwServiceModelList.get(7));
            }else if (i==1){
                list.add(tlwServiceModelList.get(4));
                list.add(tlwServiceModelList.get(5));
                list.add(tlwServiceModelList.get(6));
                TlwServiceModel model1 = new TlwServiceModel();
                model1.setName("话费充值");
                model1.setUrl("");
                list.add(model1);
            }else if (i==2){
                for (int j = 0;j<traffic.length;j++){
                    TlwServiceModel model1 = new TlwServiceModel();
                    model1.setName(traffic[j]);
                    if (j==0){
                        model1.setUrl(tlwServiceModelList.get(3).getUrl());
                    }else  model1.setUrl("");
                    list.add(model1);
                }
            }else if (i==3){
                for (int j = 0;j<service.length;j++){
                    TlwServiceModel model1 = new TlwServiceModel();
                    model1.setName(service[j]);
                    model1.setUrl("");
                    list.add(model1);
                }
//                list.add(tlwServiceModelList.get(3));
//                list.add(tlwServiceModelList.get(5));
//                list.add(tlwServiceModelList.get(6));
//                list.add(tlwServiceModelList.get(7));
            }
            model.setServiceModels(list);
            serviceTeamList.add(model);
        }

        TlwServiceTeamAdapter adapter = new TlwServiceTeamAdapter(R.layout.item_tlw_service,
                serviceTeamList,getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_service.setLayoutManager(layoutManager);

        recyclerView_service.setAdapter(adapter);
        }
    }


    /**
     * 初始化头条recycleview
     */
    private void initRecycleView() {
        if (ToutiaoList != null && ToutiaoList.size() != 0){
        if (ToutiaoList.size() > 3) {
            List<GuessFancyModel> temp = new ArrayList<>();
            temp.addAll(ToutiaoList);
            ToutiaoList.clear();
            for (int i = 0; i < 3; i++) {
                ToutiaoList.add(temp.get(i));
            }
        }
        Log.d(TAG, ToutiaoList.size() + "");
        recyclerView_toutiao = (RecyclerView) view.findViewById(R.id.recyclerView_toutiao);
        weekendAdapter = new ActionsAdapter(R.layout.item_weekend_actions, ToutiaoList);

        GridLayoutManager mgr = new GridLayoutManager(getActivity(), 3);
        recyclerView_toutiao.setLayoutManager(mgr);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView_actions.setLayoutManager(linearLayoutManager);// 布局管理器。
        weekendAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);

        int spacingInPixels = DensityUtil.dip2px(getActivity(),5);
        recyclerView_toutiao.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        recyclerView_toutiao.setAdapter(weekendAdapter);


            weekendAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
//                    startWebActivity(ToutiaoList.get(i).getTitle()
//                            ,ToutiaoList.get(i).getUrl());
                    startToutiaoActivity(ToutiaoList.get(i).getTitle()
                            ,ToutiaoList.get(i).getUrl(),
                            ToutiaoList.get(i).getId());
                }
            });

        }
        OkHttpUtils.post()
                .url(Config.URL_TT_LABS)
                .addParams("type","tt")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.showShortToast(getActivity(),getString(R.string.net_error));
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        JSONArray json = JSON.parseArray(s);
                        Log.i(TAG,"json lab"+json.toJSONString());
                        if (json.size()>0){
                            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.liner_tt_lab);
                            for (int j= 0;j<json.size();j++){
                                TextView textView = new TextView(getActivity());
                                LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1
                                );
                                tvParams.gravity = Gravity.CENTER_VERTICAL;
                                tvParams.setMargins(10, 0, 0, 0);
                                textView.setGravity(Gravity.CENTER);
                                textView.setText(json.get(j).toString());
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                textView.setTextColor(getResources().getColor(R.color.text_gray));
                                textView.setPadding(3,3,3,3);
                                textView.setBackgroundResource(R.drawable.text_view_border);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                linearLayout.addView(textView,tvParams);
                            }
                        }
                    }
                });

    }


    /**
     * 初始化活动recycleview
     */
    private void initRecycleViewActions() {
        if (ActionsList != null && ActionsList.size() >0){
        if (ActionsList.size() > 3) {
            List<GuessFancyModel> temp = new ArrayList<>();
            temp.addAll(ActionsList);
            ActionsList.clear();
            for (int i = 0; i < 3; i++) {
                ActionsList.add(temp.get(i));
            }
        }
        Log.d(TAG, ActionsList.size() + "actions");
        recyclerView_actions = (RecyclerView) view.findViewById(R.id.recyclerView_actions);
        ActionsAdapter adapter = new ActionsAdapter(R.layout.item_weekend_actions, ActionsList);

        GridLayoutManager mgr = new GridLayoutManager(getActivity(), 3);
        recyclerView_actions.setLayoutManager(mgr);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView_actions.setLayoutManager(linearLayoutManager);// 布局管理器。
            adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);

        int spacingInPixels = DensityUtil.dip2px(getActivity(),5);
        recyclerView_actions.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        recyclerView_actions.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                startToutiaoActivity(ActionsList.get(i).getTitle()
                        ,ActionsList.get(i).getUrl(),
                        ActionsList.get(i).getId());
            }
        });

        }
    }


    @Override
    public void onLoadMoreRequested() {

    }


    /**
     * 设置台陆通服务的点击事件
     *
     * @param id
     */
    private void setTlwServiceClick(int id, final String title, final String url) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(id);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWebActivity(title, url);
            }
        });
    }


    /**
     * 打开通用webactivity
     *
     * @param title
     * @param url
     */
    public void startWebActivity(String title, String url) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    public void startToutiaoActivity(String title,String url,int tt_id ){
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("id", tt_id);
        startActivity(intent);
    }

    //打开好康头条的界面
    @OnClick(R.id.tv_toutiao_more)
    public void openToutiao(View view) {
        Intent intent = new Intent(getActivity(), ToutiaoActivity.class);
        intent.putExtra("type","tt");
        startActivity(intent);
    }

    //打开更多活动
    @OnClick(R.id.tv_action_more)
    public void openAciton(View view) {
        Intent intent = new Intent(getActivity(), ToutiaoActivity.class);
        intent.putExtra("type","dz");
        startActivity(intent);
//        ToastUtil.showShortToast(getActivity(),"正在开发中...");
    }

    //打开更多活动
    @OnClick(R.id.tv_guess_fancy_more)
    public void openFancy(View view) {
        Intent intent = new Intent(getActivity(), ToutiaoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst && isVisible){
            initRollView();
        }
    }
}
