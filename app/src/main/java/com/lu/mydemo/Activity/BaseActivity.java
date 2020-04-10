package com.lu.mydemo.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.lu.mydemo.Config.OptionManager;
import com.lu.mydemo.Utils.Screen.ScreenHeightHelper;

/**
 * 创建时间: 2019/10/10 11:20 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!OptionManager.has_init()){
            OptionManager.init(getApplicationContext());
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            changeTheme();
        }
    }

    public void changeTheme(){
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);
        //导航栏沉浸
        //TODO popup window显示时，改变导航栏颜色
        if(OptionManager.isTransparent_navigation_bar()){
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //(done) 动态padding
            if(ScreenHeightHelper.checkDeviceHasNavigationBar(this)){
                View rootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
                rootView.setPadding(rootView.getPaddingLeft(), rootView.getPaddingTop(),
                        rootView.getPaddingRight(), ScreenHeightHelper.getNavigationBar(this));
            }
        }
    }

}
