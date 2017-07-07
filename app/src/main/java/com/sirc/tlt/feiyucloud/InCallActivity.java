package com.sirc.tlt.feiyucloud;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.feiyucloud.sdk.FYCall;
import com.feiyucloud.sdk.FYCallListener;
import com.feiyucloud.sdk.FYClient;
import com.feiyucloud.sdk.FYClientListener;
import com.feiyucloud.sdk.FYError;
import com.feiyucloud.sdk.FYUploadLogListener;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.feiyucloud.cfg.SettingConfig;
import com.sirc.tlt.feiyucloud.util.Dump;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.utils.Config;

import java.lang.ref.WeakReference;

public class InCallActivity extends Activity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{
    private static final String TAG = InCallActivity.class.getSimpleName();
    private TextView mTextDisplayName;
    private TextView mTextCallStatus;
    private ToggleButton mToggleMute;
    private ToggleButton mToggleDailpad;
    private ToggleButton mToggleSpeaker;
    private Button mBtnHangup;
    private Button mBtnRefuse;
    private Button mBtnConnect;
    private Chronometer mCallDuration;
    private boolean mIsChronometer;

    private View panelBottomIncoming;
    private View panelBottomCalling;

//    private Dialpad mDialPad;

    private FYCall mFeiyuCall;
    private InternalHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FYCall.addListener(mFeiyuCallListener);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_incall);
        initView();

        mFeiyuCall = FYCall.instance();
        Intent intent = getIntent();
        boolean flagIncoming = intent.getBooleanExtra("Flag_Incoming", false);
        String number = intent.getStringExtra("CallNumber");
        Log.i("拨打的手机号码",number);

//        if (flagIncoming) {
//            // 如果对方拨打电话后立刻挂断，此时电话已经挂断，但该Activity还未完全加载，FYCallListener还未能注册，
//            // 所以收不到OnCallEnd。加标识是否显示来电界面。
////            if (!MyApplication.isShowIncomingCallUI) {
////                Dump.d("isShowIncomingCallUI: false");
////                // 此时电话已经结束，直接finish。
////                finish();
////            } else {
////                Dump.d("isShowIncomingCallUI: true");
////            }
//            panelBottomIncoming.setVisibility(View.VISIBLE);
//            panelBottomCalling.setVisibility(View.GONE);
//        } else {
////            panelBottomIncoming.setVisibility(View.GONE);
////            panelBottomCalling.setVisibility(View.VISIBLE);
//
//            // 显号和录音需要后台开通权限,否则调用无效
////            int showNumber = SettingConfig.getIsShowNumber(this) ? FYCall.SHOW_NUMBER_ON
////                    : FYCall.SHOW_NUMBER_OFF;
//            int calltype = intent.getIntExtra("CallType", -1);
//            if (calltype == 2) {
//                mTextDisplayName.setText(number);
//                mFeiyuCall.callback(number, showNumber, isRecord, null);
//            } else if (calltype == 1) {
//                mFeiyuCall.directCall(number, showNumber, isRecord, null);
//            } else if (calltype == 0) {
//                mFeiyuCall.networkCall(number, showNumber, isRecord, null);
//            }
//        }
////        int showNumber = SettingConfig.getIsShowNumber(this) ? FYCall.SHOW_NUMBER_ON
////                    : FYCall.SHOW_NUMBER_OFF;
        int showNumber = FYCall.SHOW_NUMBER_ON;
        boolean isRecord = false;
        mFeiyuCall.directCall(number, showNumber, isRecord, null);
        mTextDisplayName.setText(number);
        mHandler = new InternalHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 记得移除注册的Listener
        FYCall.removeListener(mFeiyuCallListener);
    }

    private void initView() {
        mTextDisplayName = (TextView) findViewById(R.id.tv_display_name);
        mTextCallStatus = (TextView) findViewById(R.id.tv_call_status);

        mToggleMute = (ToggleButton) findViewById(R.id.toggle_mute);
        mToggleMute.setOnCheckedChangeListener(this);
//        mToggleDailpad = (ToggleButton) findViewById(R.id.toggle_dialpad);
//        mToggleDailpad.setOnCheckedChangeListener(this);
        mToggleSpeaker = (ToggleButton) findViewById(R.id.toggle_speaker);
        mToggleSpeaker.setOnCheckedChangeListener(this);

        mBtnHangup = (Button) findViewById(R.id.btn_hangup);
        mBtnHangup.setOnClickListener(this);
//        mBtnRefuse = (Button) findViewById(R.id.btn_refuse);
//        mBtnRefuse.setOnClickListener(this);
//        mBtnConnect = (Button) findViewById(R.id.btn_connect);
//        mBtnConnect.setOnClickListener(this);
//
//        mDialPad = (Dialpad) findViewById(R.id.dialPad);
//        mDialPad.setOnDialKeyListener(this);
        mCallDuration = (Chronometer) findViewById(R.id.ch_call_duration);

//        panelBottomIncoming = findViewById(R.id.panel_incoming_bottom);
//        panelBottomCalling = findViewById(R.id.panel_call_bottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hangup:
                mTextCallStatus.setText("正在挂断");
                // 延迟1秒挂断，避免主叫拨打后立刻挂断，导致对方来电界面还未完全加载，电话就已经挂断的情况
                mHandler.sendEmptyMessageAtTime(MSG_END_CALL, 1000);
                break;

//            case R.id.btn_refuse:
//                mFeiyuCall.rejectCall();
//                mTextCallStatus.setText("正在挂断");
//                // 延迟1秒挂断，避免主叫拨打后立刻挂断，导致对方来电界面还未完全加载，电话就已经挂断的情况
//                mHandler.sendEmptyMessageAtTime(MSG_END_CALL, 1000);
//                break;
//
//            case R.id.btn_connect:
//                mFeiyuCall.answerCall();
//                break;
        }
    }

//    @Override
//    public void onTrigger(char dtmf) {
//        mFeiyuCall.sendDtmf(dtmf);
//    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.toggle_mute:
                mFeiyuCall.setMuteEnabled(mToggleMute.isChecked());
                break;
            //        case R.id.toggle_dialpad:
//                boolean visible = mDialPad.getVisibility() == View.VISIBLE;
//                mDialPad.setVisibility(visible ? View.GONE : View.VISIBLE);
//                mTextDisplayName.setVisibility(visible ? View.VISIBLE : View.GONE);
//                break;

            case R.id.toggle_speaker:
                mFeiyuCall.setSpeakerEnabled(mToggleSpeaker.isChecked());
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private FYCallListener mFeiyuCallListener = new FYCallListener() {

        @Override
        public void onCallFailed(FYError error) {
            StringBuilder sb = new StringBuilder();
            sb.append("[error] ")
                    .append("code:" + error.code)
                    .append(" subCode:" + error.subCode)
                    .append(" msg:" + error.msg);
            Dump.d(sb.toString());
            Log.d(TAG,sb.toString());
            Log.i("feiyu",sb.toString());
            ToastUtil.showShortToast(InCallActivity.this, "拨打失败 " + error);
            FYClient.instance().uploadLog(new FYUploadLogListener() {
                @Override
                public void onFinished(boolean b) {
                    if (b){
                        if (mHandler != null){
                            mHandler.sendEmptyMessageDelayed(MSG_EXIT, 1000);
                        }
                    }
                }
            });



        }

        @Override
        public void onIncomingCall(String s) {
            Dump.d("onIncomingCall:" + s);
            mTextCallStatus.setText("来电");
        }

        @Override
        public void onOutgoingCall(String s) {
            Dump.d("onOutgoingCall:" + s);
            mTextCallStatus.setText("正在呼叫");
        }

        @Override
        public void onCallRunning(String s) {
            Dump.d("onCallRunning:" + s);
            if (!mIsChronometer) {
                mIsChronometer = true;
                mCallDuration.setVisibility(View.VISIBLE);
                mCallDuration.setBase(SystemClock.elapsedRealtime());
                mCallDuration.start();
            }
            mTextCallStatus.setText("");
            panelBottomIncoming.setVisibility(View.GONE);
            panelBottomCalling.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCallEnd() {
            Dump.d("onCallEnd");
            mCallDuration.stop();
            Toast.makeText(InCallActivity.this, "当前通话时间"
                    +mCallDuration.getText(), Toast.LENGTH_SHORT).show();
            mTextCallStatus.setText("通话结束");
            int use_mins = 0;
            String time = mCallDuration.getText()+"";
            if (!TextUtils.isEmpty(time)){
                String[] tem = time.split(":");
                if (!TextUtils.isEmpty(tem[0])){
                    use_mins += Integer.valueOf(tem[0]);
                }
                if (!TextUtils.isEmpty(tem[1])){
                    if (Integer.valueOf(tem[1])!=0){
                        use_mins++;
                    }
                }
            }
            Log.i(TAG,"使用分钟数:"+use_mins);
            Config.NET_PHONE_LEFT_MINS = Config.NET_PHONE_LEFT_MINS-use_mins;
            mHandler.sendEmptyMessageDelayed(MSG_EXIT, 1000);
        }

        @Override
        public void onCallbackSuccessful() {
            Dump.d("onCallbackSuccessful");
            ToastUtil.showShortToast(InCallActivity.this, "回拨成功，请等待来电");
            mHandler.sendEmptyMessageDelayed(MSG_EXIT, 1000);
        }

        @Override
        public void onCallbackFailed(FYError error) {
            Dump.d("onCallbackFailed:" + error);
            ToastUtil.showShortToast(InCallActivity.this, "onCallbackFailed:" + error);
            mHandler.sendEmptyMessageDelayed(MSG_EXIT, 1000);
        }

        @Override
        public void onCallAlerting(String s) {
            Dump.d("onCallAlerting:" + s);
        }

        @Override
        public void onDtmfReceived(char dtmf) {
            ToastUtil.showShortToast(InCallActivity.this, "dtmf: " + dtmf);
        }
    };

    private static final int MSG_EXIT = 1;
    private static final int MSG_END_CALL = 2;

    private static class InternalHandler extends Handler {
        WeakReference<InCallActivity> r;

        public InternalHandler(InCallActivity a) {
            r = new WeakReference<InCallActivity>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EXIT:
                    if (r != null && r.get() != null) {
                        r.get().finish();
                    }
                    break;

                case MSG_END_CALL:
                    FYCall.instance().endCall();
                    sendEmptyMessage(MSG_EXIT);
                    break;
            }
        }
    }

}
