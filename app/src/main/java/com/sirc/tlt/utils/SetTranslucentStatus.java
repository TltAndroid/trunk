package com.sirc.tlt.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sirc.tlt.R;

/**
 * Created by Hooliganiam on 17/4/21.
 */
public class SetTranslucentStatus {

    private Activity mActivity;

    public SetTranslucentStatus(Activity activity) {
        this.mActivity = activity;
        setTranslucentStatus();
    }

    /**
     * 为xml 的根布局添加android:fitsSystemWindows=”true” 属性<br/>
     */
    public void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(R.color.title_background);
        //Apply the specified drawable or color resource to the system navigation bar.
        //给导航栏设置资源
        tintManager.setNavigationBarTintResource(R.color.title_background);
        tintManager.setStatusBarTintResource(R.color.title_background);// 通知栏所需颜色
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = mActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
