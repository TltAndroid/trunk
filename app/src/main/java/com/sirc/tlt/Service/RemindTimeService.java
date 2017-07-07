package com.sirc.tlt.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sirc.tlt.Broadcast.RemindReceiver;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.ui.activity.SignInActivity;
import com.sirc.tlt.utils.CommonUtil;

import java.util.Calendar;
import java.util.TimeZone;

import static android.icu.text.DateTimePatternGenerator.DAY;

/**
 * Created by Hooliganiam on 17/5/6.
 */

public class RemindTimeService extends Service {

    private static final String TAG = "RemindTimeService";
    int START_STICKY = 1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        String time = CommonUtil.getIsRemindTime(MyApplication.getContext());
//        if (!TextUtils.isEmpty(time)) {
//            String temp[] = time.split(":");
//            Log.d(TAG, temp.toString());
//            setReminder(true, Integer.valueOf(temp[0]), Integer.valueOf(temp[1]));
//            Log.d(TAG,temp[0]+":"+temp[1]);
//            Toast.makeText(MyApplication.getContext(), "服务开启", Toast.LENGTH_SHORT).show();
//        }
//        Log.d(TAG,START_STICKY+"'");
//        return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Set the alarm
     *
     * @param b whether enable the Alarm clock or not
     */
    private void setReminder(boolean b, int hour, int minute) {

        // get the AlarmManager instance
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // create a PendingIntent that will perform a broadcast
        PendingIntent pi = PendingIntent.getBroadcast(MyApplication.getContext(), 0, new Intent(this, RemindReceiver.class), 0);

        if (b) {
            //得到日历实例，主要是为了下面的获取时间
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(System.currentTimeMillis());

            //获取当前毫秒值
            long systemTime = System.currentTimeMillis();

            //是设置日历的时间，主要是让日历的年月日和当前同步
            mCalendar.setTimeInMillis(System.currentTimeMillis());
            // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
            mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            //设置在几点提醒
            mCalendar.set(Calendar.HOUR_OF_DAY, hour);
            //设置在几分提醒
            mCalendar.set(Calendar.MINUTE, minute);
            //下面这两个看字面意思也知道
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);
            //获取上面设置时间的毫秒值
            long selectTime = mCalendar.getTimeInMillis();

            // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
            if (systemTime > selectTime) {
                mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            // just use current time as the Alarm time.
            // schedule an alarm
            am.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), DAY, pi);
//            am.set(AlarmManager.RTC_WAKEUP, systemTime, pi);
        } else {
            // cancel current alarm
            am.cancel(pi);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //在Service结束后关闭AlarmManager
 //       setReminder(false, 0, 0);

    }
}
