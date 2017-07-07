package com.sirc.tlt;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.feiyucloud.sdk.FYCall;
import com.feiyucloud.sdk.FYCallListener;
import com.feiyucloud.sdk.FYClient;
import com.feiyucloud.sdk.FYClientListener;
import com.feiyucloud.sdk.FYError;
import com.mob.MobApplication;
import com.mob.MobSDK;
import com.sirc.tlt.feiyucloud.InCallActivity;
import com.sirc.tlt.feiyucloud.util.Dump;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.smssdk.SMSSDK;
import okhttp3.OkHttpClient;


/**
 * 全局Application
 */
public class MyApplication extends Application {

    public  static final String TAG = "MyApplication";
    protected static boolean isShowIncomingCallUI = false;
    private static Context context;
    private List<Activity> ActivityList;//用于存放所有启动的Activity的集合
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //设置网络访问
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        //初始化OKHTTP
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //设置可访问所有https的网站
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();

        OkHttpUtils.initClient(okHttpClient);

// FYClient初始化操作需在主线程
        /**
         * 初始化FYClient
         *
         * @param context 上下文
         * @param channelId 渠道号
         * @param toggleLog log开关
         * @param needEC 是否开启回声校正，默认开启，只执行一次
         */
        FYClient.instance().init(getApplicationContext(), "test", true, true);

        CommonUtil.initFy(MyApplication.getContext());
        /**
         * 添加连接云平台回调
         *
         * @param listener
         */
        FYClient.addListener(mClientListener);
//        FYCall.addListener(mCallListener);

//        MobSDK.init(context, Config.SMS_APPKEY, Config.SMS_AppSecret);

    }

    private FYClientListener mClientListener = new FYClientListener() {

        @Override
        public void onConnectionSuccessful() {

            Dump.d("FYCloud connect successful");
        }

        @Override
        public void onConnectionFailed(FYError fyError) {
            Dump.d("FYCloud connect failed:" + fyError);
            Log.i("FYCloud connect failed",fyError.toString());
        }

    };

    private FYCallListener mCallListener = new FYCallListener() {

        @Override
        public void onIncomingCall(String s) {
            Dump.d("onIncomingCall:" + s);
            Intent intent = new Intent();
            intent.setClass(MyApplication.this, InCallActivity.class);
            intent.putExtra("Flag_Incoming", true);
            intent.putExtra("CallNumber", s);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            isShowIncomingCallUI = true;
        }

        @Override
        public void onOutgoingCall(String s) {
            Dump.d("onOutgoingCall:" + s);
        }

        @Override
        public void onCallRunning(String s) {
            Dump.d("onCallRunning:" + s);
        }

        @Override
        public void onCallAlerting(String s) {
            Dump.d("onCallAlerting:" + s);
        }

        @Override
        public void onCallEnd() {
            isShowIncomingCallUI = false;
        }

        @Override
        public void onCallFailed(FYError feiyuError) {
            Dump.d("onCallFailed " + feiyuError);
            isShowIncomingCallUI = false;
        }

        @Override
        public void onCallbackSuccessful() {
            Dump.d("onCallbackSuccessful");
        }

        @Override
        public void onCallbackFailed(FYError feiyuError) {
            Dump.d("onCallbackFailed " + feiyuError);
        }

        @Override
        public void onDtmfReceived(char dtmf) {
        }
    };

    public static Context getContext() {
        return context;
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!ActivityList.contains(activity)) {
            ActivityList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
//判断当前集合中存在该Activity
        if (ActivityList.contains(activity)) {
            ActivityList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : ActivityList) {
            activity.finish();
        }
    }
}