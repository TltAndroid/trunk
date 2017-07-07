package com.sirc.tlt.utils;

import android.os.Environment;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 *
 * Created by Hooliganiam on 17/4/27.
 */

public class Config {
    //飞语APP id

    public static final String APPID_FY = "B56BAF1711C67F671A0E2E9C854B8197";
    public static final String APPTOKEN_FY = "555FF2F8E766CF2FECD72895159D2886";

    public static String FyPhone_district = "";

    /*短信sdk 账户密码*/
    public static final String  MOB_APPKEY = "1eb7ebde15ba2";
    public static final String  MOB_AppSecret = "88a5c6a6099ca9a6123ca992b1037283";


    public static final String QCOMMENT = "问题评论";
    public static final String TCOMMENT = "新闻评论";

    public static final int CALLTYPE = 1;
    public static final String KEFU_PHONE = "4008001969";
    // preference key
    static final String KEY_LOGIN_USER = "key_login_user";
    static final String KEY_LOGIN_PWD = "key_login_pwd";
    static final String KEY_IS_SHOW_NUMBER = "key_is_show_number";
    static final String KEY_IS_REMIND_TIME = "key_is_remind_time";
    static final String KEY_IS_LOGIN = "key_is_login";

    //飞语通话的账号密码
    static final String KEY_FyAccountPwd = "key_fyAccountPwd";
    //飞语通话的账号密码
    static final String KEY_FyAccountId = "key_fyAccountId";

    public static  boolean is_open_sign_remind = false;


    public final static String WX_APP_PAY_ID = "wxc0280f8387f30225";


    //服务端访问地址
    /*-----------------------------------------------------------------------------------*/

    //http://120.24.242.176:8080/tlt-toutiao/tt?s=5&p=4&label=社会
    public static final String URL_BASE = "http://120.24.242.176:8080/";
    public static final String URL_TOUTIAO_LIST = URL_BASE + "tlt-toutiao/tt";
    public static final String URL_TOUTIAO_COMMENT = URL_BASE + "tlt-toutiao/tc";

    //主页访问的url
    public static final String URL_MAIN =URL_BASE+"tlt-web/main";

    //二维码url
    public static final String URL_QR_CODE = URL_BASE+"tlt/download/index.html";

    //问答标签url
    public static final String URL_ASK_LABLE = "http://192.168.1.20:8080/"+"tlt-askanswer/lable";
    //有问必答url
    public static final String URL_Questions = "http://192.168.1.20:8080/"+"tlt-askanswer/questions";
    //问题评论url
    public static final String URL_QUESTION_COMMENT = "http://192.168.1.20:8080/"+"tlt-askanswer/ac";
    //点赞
    public static final String URL_PRAISE = "http://192.168.1.20:8080/"+"tlt-askanswer/insert_praise";
    //收藏
    public static final String URL_COLLECT = "http://192.168.1.20:8080/"+"tlt-askanswer/collections";
    //消息通知
    public static final String URL_NOTIFY_NEWS = "http://192.168.1.20:8080/"+"tlt-askanswer/notifynews";

    //话费流量充值地址
    public static final String URL_RECHARGE = "http://192.168.1.20:8080/"+"tlt-recharge/recharge";

    //获取邮箱验证码地址
    public static final String URL_SEND_EMAIL_CODE = URL_BASE+"tlt-auth/reg";

    //用户注册
    public static final String URL_REGISTER=URL_BASE+"tlt-auth/reg";

    //获取阿里支付订单详情的接口
    public static final String URL_ALI_PAY = "http://192.168.1.20:8080/"+"TltPay/alipay";
    //获取微信支付的订单信息
    public static final String URL_WX_PAY = "http://192.168.1.20:8080/"+"TltPay/wxpay";

    //登陆接口
    public static final String URL_USER_LOGIN = URL_BASE+"tlt-auth/login";

    //获取主页好康头条标签
    public static final String URL_TT_LABS = URL_BASE+"tlt-toutiao/label";

    //
    /*-----------------------------------------------------------------------------------*/



    //拍照保存地址
    public static final String cameraPath = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "/"+"tlt"
            + File.separator
            ;
    // 拍照保存的图片临时地址
    public static final String TEMP_IMAGE_FILE_LOCATION = cameraPath
            + "_temp.jpg";
    // 剪裁完后的图片保存地址
    public static final  String IMAGE_FILE_LOCATION = cameraPath
            + "_finish.jpg";

    //支付宝支付
    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_AUTH_FLAG = 2;

    //每页展示的数据数
    public static final int PAGE_SIZE = 10;

    //问答
    public static final String QUESTION = "问答";
    //头条
    public static final String TTNEWS = "头条";

    //充值本机号码
    public static final String NATIVE_PHONE = "话费";
    //充值本机流量
    public static final String NATIVE_FLOW = "流量";

    //阿里支付
    public static final String ALI_PAY = "支付宝支付";
    //微信支付
    public static final String WX_PAY = "微信支付";

    //网络电话分钟数:
    public static int NET_PHONE_LEFT_MINS = 30;


}
