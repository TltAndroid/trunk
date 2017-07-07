package com.sirc.tlt.ui.fragment;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feiyucloud.sdk.FYClient;
import com.feiyucloud.sdk.FYClientListener;
import com.feiyucloud.sdk.FYError;
import com.feiyucloud.sdk.FYUploadLogListener;
import com.feiyucloud.sdk.FyOnlineStateCallback;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.feiyucloud.InCallActivity;
import com.sirc.tlt.feiyucloud.util.Dump;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.feiyucloud.util.Utils;
import com.sirc.tlt.permission.PermissionRequest;
import com.sirc.tlt.ui.activity.LoginActivity;
import com.sirc.tlt.ui.activity.MainActivity;
import com.sirc.tlt.ui.activity.MyWalletActivity;
import com.sirc.tlt.ui.activity.nativeContact.ActivityNativtContact;
import com.sirc.tlt.ui.view.ActionSheetDialog;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

/**
 * Created by Hooliganiam on 17/4/19.
 */
public class FreePhoneFragment extends LazyFragment implements View.OnClickListener {

    private static final String TAG = FreePhoneFragment.class.getSimpleName();

    private View view;

    private EditText editText_phone;
    private ImageButton ibtn_num_1, ibtn_num_2, ibtn_num_3, ibtn_num_4,
            ibtn_num_5, ibtn_num_6, ibtn_num_7, ibtn_num_8, ibtn_num_9, ibtn_num_0,
            ibtn_num_left, ibtn_num_right, ibtn_contact, ibtn_take_phone, ibtn_num_delete;
    private StringBuffer stringBuffer;

    //拨打电话 条例描述
    private TextView tv_phone_intruction,tv_choose_area;

    //分钟数
    private TextView tv_show_phone_mins;

    private Button btn_for_sign;
    private boolean isPrepared;
    private boolean isFirst;

    //是否呼叫
    private boolean canCall;
    //是否拨打大陆号码
    private boolean isLandPhone;
    //申请权限
    private PermissionRequest permissionRequest;
    private String[] permissions = {Manifest.permission.READ_PHONE_STATE};

    private AppOpsManager appOpsManager;
    private static final int REQUEST_CODE_SETTING = 300;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.fragment_free_phone, null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        canCall = false;
        isLandPhone = true;
        isPrepared = true;
        isFirst = true;
        lazyLoad();

        return view;
    }

    private void initView() {
        if (!FYClient.isConnected()){
            permissionRequest = new PermissionRequest(getActivity(),
                    new PermissionRequest.PermissionCallback() {
                        @Override
                        public void onSuccessful() {
                            CommonUtil.initFy(getActivity());
                            FYClient.addListener(mClientListener);
                        }

                        @Override
                        public void onFailure(@NonNull List<String> deniedPermissions) {
                            //部分手机会出现申请了权限，但是仍然返回申请失败的结果
                            if (!AndPermission.hasPermission(getActivity(),deniedPermissions)){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    appOpsManager = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
                                    int checkResult = appOpsManager.checkOpNoThrow(
                                            AppOpsManager.OPSTR_FINE_LOCATION, Binder.getCallingUid(),
                                            getActivity().getPackageName());
                                    if(checkResult == AppOpsManager.MODE_ALLOWED){
                                        CommonUtil.initFy(getActivity());
                                        FYClient.addListener(mClientListener);
                                    }else if(checkResult == AppOpsManager.MODE_IGNORED){
                                        // TODO: 只需要依此方法判断退出就可以了，这时是没有权限的。
                                        ToastUtil.showShortToast(getActivity(),"权限申请失败"+deniedPermissions);
                                        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
                                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), deniedPermissions)) {
                                            // 第一种：用默认的提示语。
                                            AndPermission.defaultSettingDialog(getActivity(), REQUEST_CODE_SETTING).show();
                                        }
                                    }
                                }

                            }

                        }

                    });
            permissionRequest.request(permissions,getActivity());

        }

        stringBuffer = new StringBuffer("");
        editText_phone = (EditText) view.findViewById(R.id.edit_phone_num);
        editText_phone.setFocusable(false);
        editText_phone.setClickable(false);

        ibtn_num_0 = (ImageButton) view.findViewById(R.id.ibtn_num_0);
        ibtn_num_1 = (ImageButton) view.findViewById(R.id.ibtn_num_1);
        ibtn_num_2 = (ImageButton) view.findViewById(R.id.ibtn_num_2);
        ibtn_num_3 = (ImageButton) view.findViewById(R.id.ibtn_num_3);
        ibtn_num_4 = (ImageButton) view.findViewById(R.id.ibtn_num_4);
        ibtn_num_5 = (ImageButton) view.findViewById(R.id.ibtn_num_5);
        ibtn_num_6 = (ImageButton) view.findViewById(R.id.ibtn_num_6);
        ibtn_num_7 = (ImageButton) view.findViewById(R.id.ibtn_num_7);
        ibtn_num_8 = (ImageButton) view.findViewById(R.id.ibtn_num_8);
        ibtn_num_9 = (ImageButton) view.findViewById(R.id.ibtn_num_9);
        ibtn_num_left = (ImageButton) view.findViewById(R.id.ibtn_num_left);
        ibtn_num_right = (ImageButton) view.findViewById(R.id.ibtn_num_right);
//        ibtn_contact = (ImageButton) view.findViewById(R.id.ibtn_contact);
        ibtn_take_phone = (ImageButton) view.findViewById(R.id.ibtn_take_phone);
        ibtn_num_delete = (ImageButton) view.findViewById(R.id.ibtn_num_delete);

        tv_phone_intruction = (TextView) view.findViewById(R.id.tv_phone_intruction);
        tv_show_phone_mins = (TextView) view.findViewById(R.id.tv_show_phone_mins);

        tv_choose_area = (TextView) view.findViewById(R.id.tv_choose_area);
        tv_choose_area.setOnClickListener(this);

        btn_for_sign = (Button) view.findViewById(R.id.btn_for_sign);

        ibtn_num_0.setOnClickListener(this);
        ibtn_num_1.setOnClickListener(this);
        ibtn_num_2.setOnClickListener(this);
        ibtn_num_3.setOnClickListener(this);
        ibtn_num_4.setOnClickListener(this);
        ibtn_num_5.setOnClickListener(this);
        ibtn_num_6.setOnClickListener(this);
        ibtn_num_7.setOnClickListener(this);
        ibtn_num_8.setOnClickListener(this);
        ibtn_num_9.setOnClickListener(this);
        ibtn_num_left.setOnClickListener(this);
        ibtn_num_right.setOnClickListener(this);
//        ibtn_contact.setOnClickListener(this);
        ibtn_take_phone.setOnClickListener(this);
        ibtn_num_delete.setOnClickListener(this);

        ibtn_num_0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                stringBuffer.append("+");
                editText_phone.setText(stringBuffer);
                return false;
            }
        });


        editText_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ibtn_num_delete != null) {
                    if (TextUtils.isEmpty(s)) {
                        ibtn_num_delete.setVisibility(View.GONE);
                        ibtn_take_phone.setImageResource(R.drawable.img_phone_record);
                        canCall = false;
                    } else {
                        ibtn_num_delete.setVisibility(View.VISIBLE);
                        ibtn_take_phone.setImageResource(R.drawable.img_take_phone);
                        canCall = true;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //tv_phone_intruction.loadUrl(" file:///android_asset/take_phone_content.html ");
        tv_show_phone_mins.setText(getString(R.string.leave_minute)+Config.NET_PHONE_LEFT_MINS
        +"分钟");

        btn_for_sign.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_num_0:
                stringBuffer.append("0");
                break;
            case R.id.ibtn_num_1:
                stringBuffer.append("1");
                break;
            case R.id.ibtn_num_2:
                stringBuffer.append("2");
                break;
            case R.id.ibtn_num_3:
                stringBuffer.append("3");
                break;
            case R.id.ibtn_num_4:
                stringBuffer.append("4");
                break;
            case R.id.ibtn_num_5:
                stringBuffer.append("5");
                break;
            case R.id.ibtn_num_6:
                stringBuffer.append("6");
                break;
            case R.id.ibtn_num_7:
                stringBuffer.append("7");
                break;
            case R.id.ibtn_num_8:
                stringBuffer.append("8");
                break;
            case R.id.ibtn_num_9:
                stringBuffer.append("9");
                break;
            case R.id.ibtn_num_left:
                stringBuffer.append("*");
                break;
            case R.id.ibtn_num_right:
                stringBuffer.append("#");
                break;
//            case R.id.ibtn_contact:
//
//                break;
            case R.id.ibtn_take_phone:
                if (!canCall){

                }else {
                if (TextUtils.isEmpty(editText_phone.getText())) {
                    Toast.makeText(getActivity(), "您所拨打的号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtil.getIsLogin(getActivity())){
                        String phone = "";
                        if (Config.NET_PHONE_LEFT_MINS > 0){
                            if (isLandPhone){
                                if (!CommonUtil.isChinaPhoneLegal(editText_phone.getText()+"")
                                        || editText_phone.getText().length() != 11){
                                    ToastUtil.showShortToast(getActivity(),"请输入合法的大陆手机号");
                                }else {
                                    phone = editText_phone.getText()+"";
                                    navToCallPhone(phone);
                                }
                            }else {
                                phone = "00886"+editText_phone.getText();
                                if (editText_phone.getText().length() == 9){
                                    navToCallPhone(phone);
                                }else ToastUtil.showShortToast(getActivity(),"请输入合法的台湾地区号码");
                            }
                        }else {
                            ToastUtil.showShortToast(getActivity(),
                                    "分钟数已使用完毕，请充值");
                        }
                  }else{
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra("canBack",true);
                        startActivity(intent);
                    }
                  }
                }
                break;
            case R.id.ibtn_num_delete:
                if (stringBuffer.length() > 0) {
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                break;
            case R.id.btn_for_sign:
                Intent intent = new Intent(getActivity(), MyWalletActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_choose_area:
                chooseArea();
                break;
        }
        editText_phone.setText(stringBuffer.toString());
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
        if (isFirst){
            TemplateTitle title = (TemplateTitle) view.findViewById(R.id.free_phone_title);
            title.setTitleText(getString(R.string.free_phone));
            title.setMoreTextContext("通讯录");
            title.setMoreTextAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ActivityNativtContact.class);
                    startActivity(intent);
                }
            });
            initView();

            Log.d("fragment",TAG);
        }
        isFirst = false;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!FYClient.isConnected()){
            CommonUtil.initFy(getActivity());
            FYClient.addListener(mClientListener);
        }
        if (editText_phone !=null ){
            editText_phone.setText("");
            stringBuffer = new StringBuffer("");

        }
        if (tv_show_phone_mins != null ){
            tv_show_phone_mins.setText(getString(R.string.leave_minute)+Config.NET_PHONE_LEFT_MINS
                    +"分钟");
        }
    }

    private FYClientListener mClientListener = new FYClientListener() {

        @Override
        public void onConnectionSuccessful() {
            Log.i("feiyu connect","successful");
            Dump.d("FYCloud connect successful");

        }

        @Override
        public void onConnectionFailed(FYError fyError) {
            Dump.d("FYCloud connect failed:" + fyError);
            FYClient.instance().uploadLog(new FYUploadLogListener() {
                @Override
                public void onFinished(boolean b) {

                }
            });

            Log.i("feiyu connect failed",fyError.toString());
        }

    };

    @Override
    public void onDestroy() {
        if (FYClient.isConnected()) {
            Dump.d("disconnect FeiyuClient");
            FYClient.instance().disconnect();
        } else {
            Dump.d("FeiyuClient already disconnected.");
        }
        super.onDestroy();
    }


    /**
     * 选择局域拨打
     */
    private void chooseArea(){
            new ActionSheetDialog(getActivity())
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .addSheetItem(getString(R.string.tv_choose_cl), ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    tv_choose_area.setText(R.string.tv_choose_cl);
                                    isLandPhone = true;
                                }
                            })
                    .addSheetItem(getString(R.string.tv_choose_tw), ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    tv_choose_area.setText(R.string.tv_choose_tw);
                                    isLandPhone = false;
                                }
                            }).show();
        }

    /**
     * 跳转拨打电话界面
     *
     */
    private void navToCallPhone(String phone){
        int callType = 1;
        Intent intent = new Intent();
        intent.putExtra("Flag_Incoming", false);
        intent.putExtra("CallNumber", phone);
        intent.putExtra("CallType", callType);
        intent.setClass(getActivity(), InCallActivity.class);
        startActivity(intent);
    }

}
