package com.sirc.tlt.ui.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.feiyucloud.sdk.FYClient;
import com.feiyucloud.sdk.FYClientListener;
import com.feiyucloud.sdk.FYError;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.util.Dump;
import com.sirc.tlt.feiyucloud.util.Encrypt;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.permission.PermissionRequest;
import com.sirc.tlt.ui.fragment.AskAnswerFragment;
import com.sirc.tlt.ui.fragment.FreePhoneFragment;
import com.sirc.tlt.ui.fragment.HomeFragment;
import com.sirc.tlt.ui.fragment.UserProfileFragment;
import com.sirc.tlt.ui.fragment.toutiao.NewsFragment;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.PermissionsChecker;
import com.sirc.tlt.utils.SetTranslucentStatus;
import com.yanzhenjie.permission.AndPermission;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * Created by Hooliganiam on 17/4/19.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager viewPager;
    public FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private HomeFragment homeFragment;
    private AskAnswerFragment askAnswerFragment;
    private FreePhoneFragment freePhoneFragment;
    private UserProfileFragment userProfileFragment;
    private NewsFragment newsFragment;
    private LinearLayout liner_home_page, liner_free_phone, liner_ask_answer, liner_user_profile;
    private ImageView img_home_page, img_free_phone, img_ask_answer, img_user_profile;

    private FragmentManager fragmentManager;

    private TextView tv_home_page, tv_free_phone, tv_ask_answer, tv_user_profile;

    private PermissionRequest permissionRequest;
    private static final int REQUEST_CODE_SETTING = 300;

    private String[] permissions = {Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS,
            Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE};

    private AppOpsManager appOpsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus status = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_main);
        permissionRequest = new PermissionRequest(this, new PermissionRequest.PermissionCallback() {
            @Override
            public void onSuccessful() {
                ToastUtil.showShortToast(MainActivity.this,"权限申请成功");
                init();
            }
            @Override
            public void onFailure(@NonNull List<String> deniedPermissions) {
                Log.i("拥有权限",AndPermission.hasPermission(MainActivity.this,deniedPermissions)+"");
                if (!AndPermission.hasPermission(MainActivity.this,deniedPermissions)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                        int checkResult = appOpsManager.checkOpNoThrow(
                                AppOpsManager.OPSTR_FINE_LOCATION, Binder.getCallingUid(),getPackageName());
                        if(checkResult == AppOpsManager.MODE_ALLOWED){
                            init();
                            Log.e("有权限","有权限");
                        }else if(checkResult == AppOpsManager.MODE_IGNORED){
                            // TODO: 只需要依此方法判断退出就可以了，这时是没有权限的。
                            Toast.makeText(MainActivity.this,"被禁止了",Toast.LENGTH_LONG).show();
                            Log.e("没有权限","被禁止了");
                            init();
                            ToastUtil.showShortToast(MainActivity.this,"权限申请失败"+deniedPermissions);
                            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
                            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                                // 第一种：用默认的提示语。
                                AndPermission.defaultSettingDialog(MainActivity.this, REQUEST_CODE_SETTING).show();
                            }
                        }
                    }

                }

            }

        });
        permissionRequest.request(permissions,this);
    }


    private void init(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        initView();
        FYClientInit();
        // userLogin();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                // super.destroyItem(container, position, object);
            }
        };

        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int currentIndex;

            @Override
            public void onPageSelected(int position) {
                resetTabBtn();
                switch (position) {
                    case 0:
                        img_ask_answer.setImageResource(R.drawable.ask_answer_pressed);
                        tv_ask_answer.setTextColor(getResources().getColor(R.color.tab_text_color_selector));
                        break;

                    case 1:
                        img_home_page.setImageResource(R.drawable.home_page_pressed);
                        tv_home_page.setTextColor(getResources().getColor(R.color.tab_text_color_selector));
                        break;

                    case 2:
                        img_free_phone.setImageResource(R.drawable.free_phone_pressed);
                        tv_free_phone.setTextColor(getResources().getColor(R.color.tab_text_color_selector));
                        break;

                    case 3:
                        img_user_profile.setImageResource(R.drawable.user_profile_pressed);
                        tv_user_profile.setTextColor(getResources().getColor(R.color.tab_text_color_selector));
                        break;
                }

                currentIndex = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        fragmentManager = getSupportFragmentManager();
        setTabSelected(0);
    }

    protected void resetTabBtn() {
        img_home_page.setImageResource(R.drawable.home_page);
        img_free_phone.setImageResource(R.drawable.free_phone);
        img_ask_answer.setImageResource(R.drawable.ask_answer);
        img_user_profile.setImageResource(R.drawable.user_profile);

        tv_home_page.setTextColor(getResources().getColor(R.color.text_black));
        tv_free_phone.setTextColor(getResources().getColor(R.color.text_black));
        tv_ask_answer.setTextColor(getResources().getColor(R.color.text_black));
        tv_user_profile.setTextColor(getResources().getColor(R.color.text_black));


    }

    private void initView() {
        liner_home_page = (LinearLayout) findViewById(R.id.liner_home_page);
        liner_free_phone = (LinearLayout) findViewById(R.id.liner_free_phone);
        liner_ask_answer = (LinearLayout) findViewById(R.id.liner_ask_answer);
        liner_user_profile = (LinearLayout) findViewById(R.id.liner_user_profile);


        liner_home_page.setOnClickListener(this);
        liner_free_phone.setOnClickListener(this);
        liner_ask_answer.setOnClickListener(this);
        liner_user_profile.setOnClickListener(this);


        img_home_page = (ImageView) findViewById(R.id.img_home_page);
        img_free_phone = (ImageView) findViewById(R.id.img_free_phone);
        img_ask_answer = (ImageView) findViewById(R.id.img_ask_answer);
        img_user_profile = (ImageView) findViewById(R.id.img_user_profile);

        tv_home_page = (TextView) findViewById(R.id.tv_home_page);
        tv_free_phone = (TextView) findViewById(R.id.tv_free_phone);
        tv_ask_answer = (TextView) findViewById(R.id.tv_ask_answer);
        tv_user_profile = (TextView) findViewById(R.id.tv_user_profile);

        homeFragment = new HomeFragment();
        askAnswerFragment = new AskAnswerFragment();
        freePhoneFragment = new FreePhoneFragment();
        userProfileFragment = new UserProfileFragment();
        newsFragment = new NewsFragment();

        mFragments.add(newsFragment);
        mFragments.add(homeFragment);
        mFragments.add(freePhoneFragment);
 //       mFragments.add(askAnswerFragment);
        mFragments.add(userProfileFragment);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.liner_ask_answer:
//                setTabSelected(2);
                viewPager.setCurrentItem(0);
                break;
            case R.id.liner_home_page:
//                setTabSelected(0);
//                mAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(1);
                break;
            case R.id.liner_free_phone:
//                setTabSelected(1);
                viewPager.setCurrentItem(2);
                break;
            case R.id.liner_user_profile:
//                setTabSelected(3);
                viewPager.setCurrentItem(3);
                break;
        }
    }

    //设置选项卡选中
    private void setTabSelected(int position) {
        resetTabBtn();
        //开启一个fragment的事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //隐藏所有fragment，避免多个fragment出现在界面上
//        hideFragments(transaction);
        switch (position) {
            case 0:
                if (newsFragment == null) {
                    newsFragment = new NewsFragment();
                    transaction.add(R.id.viewpager, newsFragment);
                } else {
                    transaction.show(askAnswerFragment);
                }
                img_ask_answer.setImageResource(R.drawable.ask_answer_pressed);
                tv_ask_answer.setTextColor(getResources().getColor(R.color.tab_text_color_selector));
                break;

            case 1:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.viewpager, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                img_home_page.setImageResource(R.drawable.home_page_pressed);
                tv_home_page.setTextColor(getResources().getColor(R.color.tab_text_color_selector));
                break;
            case 2:
                if (freePhoneFragment == null) {
                    freePhoneFragment = new FreePhoneFragment();
                    transaction.add(R.id.viewpager, freePhoneFragment);
                } else {
                    transaction.show(freePhoneFragment);
                }
                img_free_phone.setImageResource(R.drawable.free_phone_pressed);
                tv_free_phone.setTextColor(getResources().getColor(R.color.tab_text_color_selector));
                break;
            case 3:
                if (userProfileFragment == null) {
                    userProfileFragment = new UserProfileFragment();
                    transaction.add(R.id.viewpager, userProfileFragment);
                } else {
                    transaction.show(userProfileFragment);
                }
                img_user_profile.setImageResource(R.drawable.user_profile_pressed);
                tv_user_profile.setTextColor(getResources().getColor(R.color.tab_text_color_selector));
                break;
        }

        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    @SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (freePhoneFragment != null) {
            transaction.hide(freePhoneFragment);
        }
        if (newsFragment != null) {
            transaction.hide(newsFragment);
        }
        if (userProfileFragment != null) {
            transaction.hide(userProfileFragment);
        }
    }



    /**
     * 连接云平台
     * appId       应用id
     * appToken    应用token
     * accountId   飞语云账户ID
     * accountPwd  飞语账户密码
     */

    private void FYClientInit() {

//        String appid = "35DFF18CEB4DCA781BDBD8856DEFF247";
//        String apptoken = "4FA98758387131EEFEAEC6C6D5A1B943";
//        String account = "FY35DFF0662Q6";
//        String psw = "ICKBAW";
        //连接
        CommonUtil.initFy(this);

        /**
         * 添加连接云平台回调
         *
         * @param listener
         */
        FYClient.addListener(mClientListener);


    }

    private FYClientListener mClientListener = new FYClientListener() {

        @Override
        public void onConnectionSuccessful() {
            Dump.d("FYCloud connect successful");
            Log.d(TAG, "FYCloud connect successful");
        }

        @Override
        public void onConnectionFailed(FYError fyError) {
            Dump.d("FYCloud connect failed:" + fyError);
            Log.d(TAG, "FYCloud connect failed:" + fyError);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!FYClient.isConnected()) {
            Dump.d("connect FeiyuClient");
            Log.d(TAG, "connect fy");
            CommonUtil.initFy(this);
            FYClient.addListener(mClientListener);

        } else {
            Dump.d("FeiyuClient already connected.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFragments != null) {
            mFragments.clear();
        }
        if (FYClient.isConnected()) {
            Dump.d("disconnect FeiyuClient");
            FYClient.instance().disconnect();
        } else {
            Dump.d("FeiyuClient already disconnected.");
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

}
