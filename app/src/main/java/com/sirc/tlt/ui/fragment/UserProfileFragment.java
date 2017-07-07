package com.sirc.tlt.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.model.ALiPayResult;
import com.sirc.tlt.ui.activity.CollectionActivity;
import com.sirc.tlt.ui.activity.LoginActivity;
import com.sirc.tlt.ui.activity.MoreSettingActivity;
import com.sirc.tlt.ui.activity.MyWalletActivity;
import com.sirc.tlt.ui.activity.NotifyNewsActivity;
import com.sirc.tlt.ui.activity.OrderActivity;
import com.sirc.tlt.ui.activity.PermissionsActivity;
import com.sirc.tlt.ui.activity.SettingsActivity;
import com.sirc.tlt.ui.activity.SignInActivity;
import com.sirc.tlt.ui.activity.SuggestActivity;
import com.sirc.tlt.ui.view.ActionSheetDialog;
import com.sirc.tlt.ui.view.CircleImageView;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.FileUtils;
import com.sirc.tlt.utils.PermissionsChecker;
import com.sirc.tlt.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.badgeview.BGABadgeImageView;
import cn.bingoogolapple.badgeview.BGABadgeTextView;
import okhttp3.Call;

import static android.app.Activity.RESULT_OK;
import static com.sirc.tlt.utils.Config.SDK_AUTH_FLAG;
import static com.sirc.tlt.utils.Config.SDK_PAY_FLAG;

/**
 * 个人中心界面
 * Created by Hooliganiam on 17/4/19.
 */
public class UserProfileFragment extends LazyFragment implements View.OnClickListener {


    private static final String TAG = UserProfileFragment.class.getSimpleName();
    private View view;
    private TextView tv_my_collect, tv_settings, tv_suggestions, tv_more_settings, tv_my_wallet
            ,tv_user_profile_name,tv_commoninfo;
    private CircleImageView user_profile_head_image;
    private RelativeLayout rl_user_profile;

    private static final int REQUEST_PICK_IMAGE = 1; //相册选取
    private static final int REQUEST_CAPTURE = 2;  //拍照
    private static final int REQUEST_PICTURE_CUT = 3;  //剪裁图片
    private static final int REQUEST_PERMISSION = 4;  //权限请求
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Uri imageUri;//原图保存地址
    private boolean isClickCamera;
    private String imagePath;

    private boolean isPrepared;
    private boolean isFirst;

//    //订单状态RelativeLayout
//    private RelativeLayout rl_order_wait_to_pay,rl_order_wait_to_comment,rl_order_already_finished
//            ,rl_order_refund;
    //订单状态textview
    private BGABadgeTextView tv_order_wait_to_pay,tv_order_wait_to_comment,tv_order_already_finished
            ,tv_order_refund;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.fragment_user_profile, null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        mPermissionsChecker = new PermissionsChecker(getActivity());
        isPrepared = true;
        isFirst = true;
        lazyLoad();

        return view;
    }

    private void initView() {
        tv_my_collect = (TextView) view.findViewById(R.id.tv_my_collect);
        tv_my_collect.setOnClickListener(this);

        tv_settings = (TextView) view.findViewById(R.id.tv_settings);
        tv_settings.setOnClickListener(this);

        tv_suggestions = (TextView) view.findViewById(R.id.tv_suggestions);
        tv_suggestions.setOnClickListener(this);

        tv_more_settings = (TextView) view.findViewById(R.id.tv_more_settings);
        tv_more_settings.setOnClickListener(this);

        tv_my_wallet = (TextView) view.findViewById(R.id.tv_my_wallet);
        tv_my_wallet.setOnClickListener(this);

        user_profile_head_image = (CircleImageView) view.findViewById(R.id.user_profile_head_image);
        user_profile_head_image.setOnClickListener(this);

        tv_commoninfo = (TextView) view.findViewById(R.id.tv_commoninfo);
        tv_commoninfo.setOnClickListener(this);


        rl_user_profile = (RelativeLayout) view.findViewById(R.id.rl_user_profile);
        rl_user_profile.setOnClickListener(this);

        tv_user_profile_name = (TextView) view.findViewById(R.id.tv_user_profile_name);
        if (CommonUtil.getIsLogin(getActivity())){
            tv_user_profile_name.setText(CommonUtil.getLoginUser(getActivity()));
            rl_user_profile.setClickable(false);
            user_profile_head_image.setClickable(true);
            if (FileUtils.isFileExit(Config.IMAGE_FILE_LOCATION)){
                Glide.with(getActivity())
                        .load(Config.IMAGE_FILE_LOCATION)
                        .into(user_profile_head_image);
            }else {
                Glide.with(getActivity()).load(R.drawable.head_me).centerCrop()
                        .override(100, 100).into(user_profile_head_image);
            }
        }else {
            tv_user_profile_name.setText(getString(R.string.not_login));
            rl_user_profile.setClickable(true);
            user_profile_head_image.setClickable(false);
            Glide.with(getActivity()).load(R.drawable.head_me).centerCrop()
                    .override(100, 100).into(user_profile_head_image);
        }







//        rl_order_wait_to_pay = (RelativeLayout) view.findViewById(R.id.rl_order_wait_to_pay);
//        rl_order_wait_to_pay.setOnClickListener(this);
//
//        rl_order_wait_to_comment = (RelativeLayout) view.findViewById(R.id.rl_order_wait_to_comment);
//        rl_order_wait_to_comment.setOnClickListener(this);
//
//        rl_order_already_finished = (RelativeLayout) view.findViewById(R.id.rl_order_already_finished);
//        rl_order_already_finished.setOnClickListener(this);
//
//        rl_order_refund = (RelativeLayout) view.findViewById(R.id.rl_order_refund);
//        rl_order_refund.setOnClickListener(this);
//
        tv_order_wait_to_pay = (BGABadgeTextView) view.findViewById(R.id.tv_order_wait_to_pay);

        tv_order_wait_to_comment = (BGABadgeTextView) view.findViewById(R.id.tv_order_wait_to_comment);

        tv_order_already_finished = (BGABadgeTextView) view.findViewById(R.id.tv_order_already_finished);

        tv_order_refund = (BGABadgeTextView) view.findViewById(R.id.tv_order_refund);

        tv_order_wait_to_pay.setOnClickListener(this);
        tv_order_wait_to_comment.setOnClickListener(this);
        tv_order_already_finished.setOnClickListener(this);
        tv_order_refund.setOnClickListener(this);

//        tv_order_wait_to_pay.showTextBadge("3");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_collect:
               navitActivity(true,CollectionActivity.class);
                break;
            case R.id.tv_settings:
                navitActivity(true,SettingsActivity.class);
                break;
            case R.id.tv_suggestions:
                navitActivity(true,SuggestActivity.class);
                break;
            case R.id.tv_more_settings:
                navitActivity(false,MoreSettingActivity.class);
                break;
            case R.id.tv_my_wallet:
                navitActivity(true,MyWalletActivity.class);
                break;
            case R.id.user_profile_head_image:
                getPictureDialog();
                break;
            case R.id.rl_user_profile:
                navitActivity(false,LoginActivity.class);
                break;

            case R.id.tv_commoninfo:
                navitActivity(true,OrderActivity.class);
                break;

            case R.id.tv_order_wait_to_pay:
                ToastUtil.showShortToast(getActivity(),
                        "正在开发中...");
                break;

            case R.id.tv_order_wait_to_comment:
                ToastUtil.showShortToast(getActivity(),
                        "正在开发中...");
                break;

            case R.id.tv_order_already_finished:
                ToastUtil.showShortToast(getActivity(),
                        "正在开发中...");
                break;

            case R.id.tv_order_refund:
                ToastUtil.showShortToast(getActivity(),
                        "正在开发中...");
                break;

        }
    }

    private void getPictureDialog() {
        new ActionSheetDialog(getActivity())
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                getImageFromCamera();
                            }
                        })
                .addSheetItem("从相册中选择", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                getImageFromAlbum();
                            }
                        }).show();
    }

    private void getImageFromCamera() {
            //检查权限(6.0以上做权限判断)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            } else {
                openCamera();
            }
        } else {
            openCamera();
        }
        isClickCamera = true;

    }

    private void getImageFromAlbum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            } else {
                selectFromAlbum();
            }
        } else {
            selectFromAlbum();
        }
        isClickCamera = false;
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
        if (isFirst){
            TemplateTitle title = (TemplateTitle) view.findViewById(R.id.user_profile_title);
            title.setTitleText("个人中心");
            title.setMoreImg(R.drawable.img_notify_news);
            title.setMoreImgAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NotifyNewsActivity.class);
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
        Log.d(TAG,"onResume");
        if (CommonUtil.getIsLogin(getContext())){
            if (user_profile_head_image != null){
                user_profile_head_image.setClickable(true);
            }
            if (rl_user_profile != null){
                rl_user_profile.setClickable(false);
            }
            if (tv_user_profile_name != null){
                tv_user_profile_name.setText(CommonUtil.getLoginUser(getActivity()));
            }

        }
    }

    //跳转activity
    private void navitActivity(boolean needLogin,Class<? extends Activity> activity){
        if (needLogin){
            if (!CommonUtil.getIsLogin(getActivity())){
                CommonUtil.startToLoginActivity(getActivity());
            }else {
                Intent intent = new Intent(getActivity(),activity);
                startActivity(intent);
            }
        }else {
            Intent intent = new Intent(getActivity(),activity);
            startActivity(intent);
        }

    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(getActivity(), REQUEST_PERMISSION,
                PERMISSIONS);
    }

    /**
     * 打开系统相机
     */
    private void openCamera() {

        File file = new FileUtils().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(getActivity(),
                    "com.sirc.tlt.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * 从相册选择
     */
    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    /**
     * 裁剪
     */
    private void cropPhoto() {
        File file = new FileUtils().createCropFile();
        Uri outputUri = Uri.fromFile(file);//缩略图保存地址
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_PICTURE_CUT);
    }


    /**
     * 如果系统是4.4以上的手机就调用handleImageOnKitKat()
     * 4.4之后选取中的图片不再返回真实的Uri了，而是封装过的Uri,所以在4.4以上，就要对这个Uri进行解析
     * @param data
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        if (data != null) {
            imagePath = null;
            imageUri = data.getData();
            if (DocumentsContract.isDocumentUri(getActivity(), imageUri)) {
                //如果是document类型的uri,则通过document id处理
                String docId = DocumentsContract.getDocumentId(imageUri);
                if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
                    String id = docId.split(":")[1];//解析出数字格式的id
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
                //如果是content类型的Uri，则使用普通方式处理
                imagePath = getImagePath(imageUri, null);
            } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
                //如果是file类型的Uri,直接获取图片路径即可
                imagePath = imageUri.getPath();
            }
            cropPhoto();
        }
    }

    /**
     *
     * 4.4以下的版本选用这个方法
     *
     * @param intent
     */
    private void handleImageBeforeKitKat(Intent intent) {
        if (intent != null){
            imageUri = intent.getData();
            imagePath = getImagePath(imageUri, null);
            cropPhoto();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection老获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE://从相册选择
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                } else {
                    handleImageBeforeKitKat(data);
                }
                break;
            case REQUEST_CAPTURE://拍照
                if (resultCode == RESULT_OK) {
                    Log.d(TAG,imageUri+"");
                    cropPhoto();
                }
                break;
            case REQUEST_PICTURE_CUT://裁剪完成
                Bitmap bitmap = null;
                Log.d(TAG,imageUri+"");
                try {
                    if (isClickCamera) {
                        Glide.with(getActivity())
                                .load(imageUri)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(user_profile_head_image);
//                        bitmap = BitmapFactory.decodeStream(getActivity().
//                                getContentResolver().openInputStream(imageUri));
                    } else {
                        Glide.with(getActivity())
                                .load(imagePath)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(user_profile_head_image);
                       // bitmap = BitmapFactory.decodeFile(imagePath);
                    }
                 //   user_profile_head_image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_PERMISSION://权限请求
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    ToastUtil.showShortToast(getActivity(),"该功能可能无法使用,请到设置中开启");
                } else {
                    if (isClickCamera) {
                        openCamera();
                    } else {
                        selectFromAlbum();
                    }
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG,"onSaveInstanceState");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }
}
