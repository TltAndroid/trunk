package com.sirc.tlt.Broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.ui.activity.DialogActivity;
import com.sirc.tlt.ui.activity.SignInActivity;
import com.sirc.tlt.utils.NotificateUtil;

/**
 *
 * 提醒的接收广播
 * Created by Hooliganiam on 17/5/6.
 */

public class RemindReceiver extends BroadcastReceiver{

    private final int pushId=1;

    @Override
    public void onReceive(Context context, Intent intent) {
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClass(context, DialogActivity.class);
//        context.startActivity(intent);
        String title = MyApplication.getContext().getString(R.string.notification_sign_title);
        String content = MyApplication.getContext().getString(R.string.notification_sign_content);
        NotificateUtil.showIntentActivityNotify(title,content);
    }
}
