package com.sirc.tlt.ui.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sirc.tlt.Broadcast.RemindReceiver;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.Service.RemindTimeService;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.NotificateUtil;
import com.sirc.tlt.utils.SetTranslucentStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static android.icu.text.DateTimePatternGenerator.DAY;
import static android.icu.text.DateTimePatternGenerator.MINUTE;

public class SignInActivity extends BaseActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();

    private ToggleButton sign_in_remind;
    private RelativeLayout rl_set_remind_time;

    private TextView tv_current_time;

    int mHour, mMinute;

    long systemTime;
    final int TIME_DIALOG = 1;

    private Button btn_sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);
        setContentView(R.layout.activity_sign_in);


        sign_in_remind = (ToggleButton) findViewById(R.id.sign_in_remind);
        rl_set_remind_time = (RelativeLayout) findViewById(R.id.rl_set_remind_time);

        tv_current_time = (TextView) findViewById(R.id.tv_current_time);

        rl_set_remind_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG);
                if (sign_in_remind != null) {
                    sign_in_remind.setChecked(false);
                }
            }
        });

        if (Config.is_open_sign_remind){
            sign_in_remind.setChecked(true);
        }

        NotificateUtil.reset();

        tv_current_time.setText(CommonUtil.getIsRemindTime(SignInActivity.this));

        sign_in_remind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(SignInActivity.this, RemindTimeService.class);
                if (isChecked) {
                    if (tv_current_time != null && !TextUtils.isEmpty(tv_current_time.getText())) {
                        //开启关闭Service
                        Config.is_open_sign_remind = true;
                       // startService(intent);
                        setReminder(true,mHour,mMinute);
//                        NotificateUtil.showIntentActivityNotify("测试"
//                        ,"点击");
                    } else {
                        Toast.makeText(SignInActivity.this, "请先设置提醒时间", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Config.is_open_sign_remind = false;
                     //  stopService(intent);
                        setReminder(false,0,0);
                }
            }
        });

        btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG:
                Calendar ca = Calendar.getInstance();
                mHour = ca.get(Calendar.HOUR_OF_DAY);
                mMinute = ca.get(Calendar.MINUTE);
                return new TimePickerDialog(this, mtimeListener, mHour, mMinute, false);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
//        tv_current_time.setText(new StringBuffer()
//                .append(mHour).append(":").append(mMinute).append(" "));
        tv_current_time.setText(CommonUtil.getIsRemindTime(SignInActivity.this));
    }

    private TimePickerDialog.OnTimeSetListener mtimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            CommonUtil.saveIsRemindTime(SignInActivity.this, new StringBuffer()
                    .append(mHour).append(":").append(mMinute).append(":00").toString());
            display();
        }
    };

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

}
