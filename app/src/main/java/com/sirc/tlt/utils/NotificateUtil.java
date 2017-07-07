package com.sirc.tlt.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.ui.activity.SignInActivity;

/**
 * Created by Hooliganiam on 17/5/8.
 */

public class NotificateUtil {

    public static final int pushId=1;

    public static void setNotifycation(String title,String content,boolean isnotify){
        NotificationManager mNotificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyApplication.getContext());
        Intent notificationIntent = new Intent(MyApplication.getContext(), SignInActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setFlags(Notification.FLAG_AUTO_CANCEL);
        notificationIntent.putExtra("isnotify",isnotify);
        PendingIntent pi = PendingIntent.getActivity(MyApplication.getContext(), 0,
                notificationIntent, 0);
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content)
                .setContentIntent(pi) //设置通知栏点击意图
//                .setNumber(++pushNum) //设置通知集合的数量
                .setTicker(title+":"+content) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.logo);//设置通知小ICON
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_AUTO_CANCEL;
        notify.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(pushId, notify);

    }

    /** 显示通知栏点击跳转到指定Activity */
    public static  void showIntentActivityNotify(String title,String content){
        // Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
        NotificationManager mNotificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyApplication.getContext());
        mBuilder.setContentTitle(title)
                .setContentText(content)
     //          .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setTicker(title)//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                .setSmallIcon(R.drawable.img_logo)
                .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
                .setAutoCancel(true);//点击后让通知将消失

        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(MyApplication.getContext(), SignInActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.putExtra("notify",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getContext(), 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(pushId, mBuilder.build());
    }

    public static  void reset(){
        NotificationManager notificationManager = (NotificationManager)MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

}
