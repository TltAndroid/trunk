package com.sirc.tlt.ui.fragment;

import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sirc.tlt.MyApplication;
import com.sirc.tlt.R;

/**
 * Created by Hooliganiam on 17/5/18.
 */

public class GuideFragment extends Fragment {

    private View view;
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.fragment_guide, null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        imageView = (ImageView) view.findViewById(R.id.guide_image);
        imageView.setBackgroundResource(R.drawable.img_guide_1);
        return view;
    }

//    public static GuideFragment newInstance(@DrawableRes int imageDrawable){
//            GuideFragment fragment = new GuideFragment();
//            View view = LayoutInflater.from(MyApplication.getContext()).inflate(
//                R.layout.fragment_guide, null);
//            ImageView image  = (ImageView) view.findViewById(R.id.guide_image);
//            image.setBackgroundResource(imageDrawable);
//            return fragment;
//    }
}
