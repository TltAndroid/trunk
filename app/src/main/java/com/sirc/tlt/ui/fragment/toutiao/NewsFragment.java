package com.sirc.tlt.ui.fragment.toutiao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.ui.activity.nativeContact.ActivityNativtContact;
import com.sirc.tlt.ui.activity.toutiao.ChannelActivity;
import com.sirc.tlt.ui.fragment.LazyFragment;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.DensityUtil;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.weyye.library.colortrackview.ColorTrackTabLayout;
import okhttp3.Call;

public class NewsFragment extends LazyFragment {

    private View view;

    static final int REQUEST_CHANNEL = 1;
    public static final String SELECTED_TAB_TITLE = "selectedTabTitle";

    @BindView(R.id.tab)
    ColorTrackTabLayout mTab;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.icon_category)
    ImageView iconCategory;
    private List<String> mTitles;
    private String type;
    private boolean isPrepared;
    private boolean isFirst;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.activity_toutiao, null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        type = "tt";
        ButterKnife.bind(getActivity(),view);

        isPrepared = true;
        isFirst = true;
        lazyLoad();
        return view;
    }

    protected void initTab() {

        mTab = (ColorTrackTabLayout) view.findViewById(R.id.tab);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        iconCategory = (ImageView) view.findViewById(R.id.icon_category);

        mTitles = new ArrayList<>();
        OkHttpUtils.post()
                .url(Config.URL_TT_LABS)
                .addParams("type",type)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.showShortToast(getActivity(),
                                getString(R.string.net_error));
                    }

                    @Override
                    public void onResponse(String s, int m) {
                        JSONArray json = JSON.parseArray(s);
                        if (json.size()>0){
                            for (int j = 0;j<json.size();j++){
                                mTitles.add(json.get(j)+"");
                                final List<Fragment> fragments = new ArrayList<>();
                                for (int i = 0; i < mTitles.size(); i++) {
                                    ToutiaoListFragment fragment = new ToutiaoListFragment();
                                    fragment.setType(type);
                                    fragment.setLabel(mTitles.get(i));
                                    //fragment.setLabel("社会");
                                    fragments.add(fragment);
                                }

                                mViewPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
                                    @Override
                                    public Fragment getItem(int position) {
                                        return fragments.get(position);
                                    }

                                    @Override
                                    public int getCount() {
                                        return mTitles.size();
                                    }

                                    @Override
                                    public CharSequence getPageTitle(int position) {
                                        return mTitles.get(position);
                                    }
                                });
                                mTab.setTabPaddingLeftAndRight(DensityUtil.dip2px(getActivity(), 10), DensityUtil.dip2px(getActivity(), 10));
                                //隐藏指示器
                                mTab.setSelectedTabIndicatorHeight(0);
                                mTab.setupWithViewPager(mViewPager);
                                mTab.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //设置最小宽度，使其可以在滑动一部分距离
                                        ViewGroup slidingTabStrip = (ViewGroup) mTab.getChildAt(0);
                                        slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth() + iconCategory.getMeasuredWidth());
                                    }
                                });
                                mViewPager.setOffscreenPageLimit(mTitles.size());
                            }
                        }
                    }
                });

    }

    @OnClick(R.id.icon_category)
    public void openChannelUI() {
        Intent intent = new Intent(getActivity(), ChannelActivity.class);
        intent.putExtra(SELECTED_TAB_TITLE, mTitles.get(mTab.getSelectedTabPosition()));
        startActivityForResult(intent, REQUEST_CHANNEL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANNEL) {
            String selectedTabTitle = data.getStringExtra(SELECTED_TAB_TITLE);
            int i = mTitles.indexOf(selectedTabTitle);
            mTab.getTabAt(i).select();
        }
    }

    public String getType(){
        return type;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
        if (isFirst){
            TemplateTitle title = (TemplateTitle) view.findViewById(R.id.template_title);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_back);
            imageView.setVisibility(View.GONE);
            title.setTitleText(getString(R.string.tlt_tt));
            initTab();
        }
        isFirst = false;
    }
}
