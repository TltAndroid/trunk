package com.sirc.tlt.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.ui.fragment.GuideFragment;
import com.sirc.tlt.ui.fragment.GuideFragment2;
import com.sirc.tlt.ui.fragment.GuideFragment3;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.PermissionsChecker;

import java.util.List;

public class IntroActivity extends AppIntro {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 3;
    private static final int PERMISSIONS_REQUEST_READ_SMS = 4;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 5;
    private GuideFragment fragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        fragment = new GuideFragment();
        GuideFragment2 guide2 = new GuideFragment2();
        GuideFragment3 guide3 = new GuideFragment3();

        if (guide3.btn_enter != null){
            guide3.btn_enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

// Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
//        addSlide(AppIntroFragment.newInstance("", "", R.drawable.img_guide_1,
//                Color.WHITE));
//        addSlide(AppIntroFragment.newInstance("", "", R.drawable.img_guide_2,
//                Color.WHITE));
//        addSlide(AppIntroFragment.newInstance("", "", R.drawable.img_guide_3,
//                Color.WHITE));

        addSlide(fragment);
        addSlide(guide2);
        addSlide(guide3);
        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#1296db"));
        setSeparatorColor(getResources().getColor(R.color.transparent));


        setIndicatorColor(Color.YELLOW,getResources().getColor(R.color.background_blue));
        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(false);
//        doneButton.performClick();

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);

    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
//        Toast.makeText(this, "onSkipPressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
//        // Do something when users tap on Done button.
//          if (!CommonUtil.getIsLogin(IntroActivity.this)){
//              Intent intent = new Intent(IntroActivity.this,
//                      LoginActivity.class);
//              startActivity(intent);
//          }
          Intent intent = new Intent(IntroActivity.this,MainActivity.class);
          startActivity(intent);
          finish();
 //       Toast.makeText(this, "onDonePressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
//        Toast.makeText(this, "onSlideChanged", Toast.LENGTH_SHORT).show();
    }


    public void enterHome(){
        doneButton.performClick();
    }

}