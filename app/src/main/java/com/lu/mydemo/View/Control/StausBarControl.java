package com.lu.mydemo.View.Control;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.lu.mydemo.Notification.AlertCenter;

/**
 * 创建时间: 2019/09/30 15:44 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class StausBarControl {
    public static void setNavigationBarStatusBarTranslucent(Activity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        try {
            ActionBar actionBar = activity.getActionBar();
            actionBar.hide();
        }catch (Exception e){
            AlertCenter.showErrorAlert(activity, e.getMessage());
        }
    }
}
