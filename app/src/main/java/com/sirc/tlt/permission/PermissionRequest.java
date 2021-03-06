/*
 * Copyright © Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sirc.tlt.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

/**
 *
 */
public class PermissionRequest {

    private Context mContext;
    private PermissionCallback mCallback;

    public PermissionRequest(Context context, PermissionCallback callback) {
        this.mContext = context;
        this.mCallback = callback;

    }

    public void request(String[] permission, final Fragment fragment) {

        AndPermission.with(fragment)
                .requestCode(110)
                .permission(
                        permission
                )
                .callback(this)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(fragment.getActivity(), rationale)
                                .show();
                    }
                })
                .start();
    }

    public void request(String[] permission, final Activity activity) {
        Log.i("权限申请",permission.toString()+"");
        AndPermission.with(activity)
                .requestCode(110)
                .permission(
                        permission
                )
                .callback(this)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(activity, rationale)
                                .show();
                    }
                })
                .start();
    }

    public void request(String[] permission) {
        AndPermission.with(mContext)
                .requestCode(110)
                .permission(
                        permission
                )
                .callback(this)
                .start();
    }

    @PermissionYes(110)
    public void yes(List<String> permissions) {
        this.mCallback.onSuccessful();
    }

    @PermissionNo(110)
    public void no(List<String> permissions) {
        this.mCallback.onFailure(permissions);
    }

    public interface PermissionCallback {
        void onSuccessful();

        void onFailure(List<String> permissions);
    }

}
