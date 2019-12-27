package com.lu.mydemo.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.lu.mydemo.R;

public class ColorManager {

    /**
     * primaryColor
     * 顶部圆角背景
     * 全局渐变背景
     * 获取本地顶信息按钮颜色
     * 获取网络信息按钮（渐变）颜色
     * 获取网络信息按钮（渐变）颜色(disable)
     * 首页（Login）课程列表文字颜色
     * 顶部悬浮通知背景
     * 顶部悬浮通知背景（警告）
     * 顶部悬浮通知背景（错误）
     * 下拉列表背景颜色
     * 通知——置顶文字颜色
     * 通知——收藏文字颜色
     * 通知——普通文字颜色
     *...
     */

    private static SharedPreferences sp;
    private static Context context;

    private static int noCloor;
    private static int primaryColor;
    private static int primaryDarkColor;
    private static int topAlertBackgroundColor;
    private static int topAlertBackgroundColor_warning;
    private static int topAlertBackgroundColor_error;
    private static int news_notice_text_color;
    private static int news_collected_text_color;
    private static int news_normal_text_color;
    private static int popwindow_background_color;
    private static int datePickerDialogTheme;
    private static Drawable topRadiusBackground;
    private static Drawable mainBackground;
    private static Drawable mainBackground_full;
    private static Drawable mainBackground_with_top_redius;
    private static Drawable localInformationButtonBackground;
    private static Drawable internetInformationButtonBackground;
    private static Drawable internetInformationButtonBackground_full;
    private static Drawable internetInformationButtonBackground_disable;
    private static Drawable internetInformationButtonBackground_disable_full;
    private static Drawable spinnerBackground;
    private static Drawable spinnerBackground_full;

    private static String themeName;

    public static void loadColorConfig(Context context){
        ColorManager.context = context;
        sp = context.getSharedPreferences("ColorConfig", Context.MODE_PRIVATE);

        loadConfig();
    }

    public static void loadConfig(Context applicationContext, Context activityContext){
        context = activityContext;
        loadConfig();
        context = applicationContext;
    }

    private static void loadConfig(){
        noCloor = context.getColor(R.color.color_no_color);

        themeName = sp.getString("theme", "blue");

        switch (themeName){
            case "blue" : {
                primaryColor = context.getColor(R.color.colorPrimary);
                primaryDarkColor = context.getColor(R.color.colorPrimaryDark);
                topAlertBackgroundColor = context.getColor(R.color.color_alerter_background);
                topAlertBackgroundColor_warning = context.getColor(R.color.color_alerter_warning_background);
                topAlertBackgroundColor_error = context.getColor(R.color.color_alerter_error_background);

                news_normal_text_color = context.getColor(R.color.colorPrimary);
                news_notice_text_color = context.getColor(R.color.color_notice_text);
                news_collected_text_color = context.getColor(R.color.color_collected_news_text);

                popwindow_background_color = context.getColor(R.color.color_popWindowBackground);
                datePickerDialogTheme = R.style.MyDatePickerDialogTheme;

                topRadiusBackground = context.getDrawable(R.drawable.shape_rectangle_with_top_radius);
                mainBackground = context.getDrawable(R.drawable.background_login);
                mainBackground_full = context.getDrawable(R.drawable.background_login_full);
                mainBackground_with_top_redius = context.getDrawable(R.drawable.background_login_with_top_rectangle);
                localInformationButtonBackground = context.getDrawable(R.drawable.button_local_background);
                internetInformationButtonBackground = context.getDrawable(R.drawable.button_internet_background);
                internetInformationButtonBackground_full = context.getDrawable(R.drawable.button_internet_background_full);
                internetInformationButtonBackground_disable = context.getDrawable(R.drawable.button_internet_disable_background);
                internetInformationButtonBackground_disable_full = context.getDrawable(R.drawable.button_internet_disable_background_full);
                spinnerBackground = context.getDrawable(R.drawable.spinner_background);
                spinnerBackground_full = context.getDrawable(R.drawable.spinner_background_full);
                break;
            }
            case "pink" : {
                primaryColor = context.getColor(R.color.colorPrimary_Pink);
                primaryDarkColor = context.getColor(R.color.colorPrimaryDark_Pink);
                topAlertBackgroundColor = context.getColor(R.color.color_alerter_background_pink);
                topAlertBackgroundColor_warning = context.getColor(R.color.color_alerter_warning_background);
                topAlertBackgroundColor_error = context.getColor(R.color.color_alerter_error_background);

                news_normal_text_color = context.getColor(R.color.colorPrimary_Pink);
                news_notice_text_color = context.getColor(R.color.color_notice_text_pink);
                news_collected_text_color = context.getColor(R.color.color_collected_news_text_pink);

                popwindow_background_color = context.getColor(R.color.color_popWindowBackground_pink);
                datePickerDialogTheme = R.style.MyDatePickerDialogTheme_Pink;

                topRadiusBackground = context.getDrawable(R.drawable.shape_rectangle_with_top_radius_pink);
                mainBackground = context.getDrawable(R.drawable.background_login_pink);
                mainBackground_full = context.getDrawable(R.drawable.background_login_pink_full);
                mainBackground_with_top_redius = context.getDrawable(R.drawable.background_login_pink_with_top_rectangle);
                localInformationButtonBackground = context.getDrawable(R.drawable.button_local_background_pink);
                internetInformationButtonBackground = context.getDrawable(R.drawable.button_internet_background_pink);
                internetInformationButtonBackground_full = context.getDrawable(R.drawable.button_internet_background_pink_full);
                internetInformationButtonBackground_disable = context.getDrawable(R.drawable.button_internet_disable_background_pink);
                internetInformationButtonBackground_disable_full = context.getDrawable(R.drawable.button_internet_disable_background_pink_full);
                spinnerBackground = context.getDrawable(R.drawable.spinner_background_pink);
                spinnerBackground_full = context.getDrawable(R.drawable.spinner_background_pink_full);
                break;
            }
            case "green" : {
                primaryColor = context.getColor(R.color.colorPrimary_Green);
                primaryDarkColor = context.getColor(R.color.colorPrimaryDark_Green);
                topAlertBackgroundColor = context.getColor(R.color.color_alerter_background_Green);
                topAlertBackgroundColor_warning = context.getColor(R.color.color_alerter_warning_background);
                topAlertBackgroundColor_error = context.getColor(R.color.color_alerter_error_background);

                news_normal_text_color = context.getColor(R.color.colorPrimary_Green);
                news_notice_text_color = context.getColor(R.color.color_notice_text_Green);
                news_collected_text_color = context.getColor(R.color.color_collected_news_text_Green);

                popwindow_background_color = context.getColor(R.color.color_popWindowBackground_Green);
                datePickerDialogTheme = R.style.MyDatePickerDialogTheme_Green;

                topRadiusBackground = context.getDrawable(R.drawable.shape_rectangle_with_top_radius_green);
                mainBackground = context.getDrawable(R.drawable.background_login_green);
                mainBackground_full = context.getDrawable(R.drawable.background_login_green_full);
                mainBackground_with_top_redius = context.getDrawable(R.drawable.background_login_green_with_top_rectangle);
                localInformationButtonBackground = context.getDrawable(R.drawable.button_local_background_green);
                internetInformationButtonBackground = context.getDrawable(R.drawable.button_internet_background_green);
                internetInformationButtonBackground_full = context.getDrawable(R.drawable.button_internet_background_green_full);
                internetInformationButtonBackground_disable = context.getDrawable(R.drawable.button_internet_disable_background_green);
                internetInformationButtonBackground_disable_full = context.getDrawable(R.drawable.button_internet_disable_background_green_full);
                spinnerBackground = context.getDrawable(R.drawable.spinner_background_green);
                spinnerBackground_full = context.getDrawable(R.drawable.spinner_background_green_full);
                break;
            }
            default:{
                primaryColor = context.getColor(R.color.colorPrimary);
                primaryDarkColor = context.getColor(R.color.colorPrimaryDark);
                topAlertBackgroundColor = context.getColor(R.color.color_alerter_background);
                topAlertBackgroundColor_warning = context.getColor(R.color.color_alerter_warning_background);
                topAlertBackgroundColor_error = context.getColor(R.color.color_alerter_error_background);

                news_normal_text_color = context.getColor(R.color.colorPrimary);
                news_notice_text_color = context.getColor(R.color.color_notice_text);
                news_collected_text_color = context.getColor(R.color.color_collected_news_text);

                popwindow_background_color = context.getColor(R.color.color_popWindowBackground);

                topRadiusBackground = context.getDrawable(R.drawable.shape_rectangle_with_top_radius);
                mainBackground = context.getDrawable(R.drawable.background_login);
                mainBackground_full = context.getDrawable(R.drawable.background_login_full);
                mainBackground_with_top_redius = context.getDrawable(R.drawable.background_login_with_top_rectangle);
                localInformationButtonBackground = context.getDrawable(R.drawable.button_local_background);
                internetInformationButtonBackground = context.getDrawable(R.drawable.button_internet_background);
                internetInformationButtonBackground_full = context.getDrawable(R.drawable.button_internet_background_full);
                internetInformationButtonBackground_disable = context.getDrawable(R.drawable.button_internet_disable_background);
                internetInformationButtonBackground_disable_full = context.getDrawable(R.drawable.button_internet_disable_background_full);
                spinnerBackground = context.getDrawable(R.drawable.spinner_background);
                spinnerBackground_full = context.getDrawable(R.drawable.spinner_background_full);
            }
        }

        //使用自定义背景
        if(sp.contains("CustomBg")){
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(sp.getString("CustomBg", ""));
                ColorManager.setMainBackground(new BitmapDrawable(context.getResources(), bitmap));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public static int getNoCloor() {
        return noCloor;
    }

    public static int getPrimaryColor() {
        return primaryColor;
    }

    public static int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public static Drawable getTopRadiusBackground() {
        return topRadiusBackground;
    }

    public static Drawable getMainBackground() {
        return mainBackground;
    }

    public static Drawable getLocalInformationButtonBackground() {
        return localInformationButtonBackground;
    }

    public static Drawable getInternetInformationButtonBackground() {
        return internetInformationButtonBackground;
    }

    public static Drawable getInternetInformationButtonBackground_disable() {
        return internetInformationButtonBackground_disable;
    }

    public static Drawable getMainBackground_full() {
        return mainBackground_full;
    }

    public static Drawable getInternetInformationButtonBackground_full() {
        return internetInformationButtonBackground_full;
    }

    public static Drawable getMainBackground_with_top_redius() {
        return mainBackground_with_top_redius;
    }

    public static Drawable getInternetInformationButtonBackground_disable_full() {
        return internetInformationButtonBackground_disable_full;
    }

    public static int getTopAlertBackgroundColor() {
        return topAlertBackgroundColor;
    }

    public static int getTopAlertBackgroundColor_warning() {
        return topAlertBackgroundColor_warning;
    }

    public static int getTopAlertBackgroundColor_error() {
        return topAlertBackgroundColor_error;
    }

    public static int getNews_notice_text_color() {
        return news_notice_text_color;
    }

    public static int getNews_collected_text_color() {
        return news_collected_text_color;
    }

    public static int getNews_normal_text_color() {
        return news_normal_text_color;
    }

    public static int getPopwindow_background_color() {
        return popwindow_background_color;
    }

    public static int getDatePickerDialogTheme() {
        return datePickerDialogTheme;
    }

    public static Drawable getSpinnerBackground() {
        return spinnerBackground;
    }

    public static Drawable getSpinnerBackground_full() {
        return spinnerBackground_full;
    }

    public static int[] getPopupWindowBackground() {
        return new int[]{context.getResources().getColor(R.color.color_no_color), context.getResources().getColor(R.color.color_no_color), context.getResources().getColor(R.color.color_no_color), ColorManager.getPopwindow_background_color()};
    }

    public static String getThemeName() {
        return themeName;
    }

    public static void setMainBackground(Drawable mainBackground) {
//        ColorManager.mainBackground = mainBackground;
        ColorManager.mainBackground_full = mainBackground;
    }

    public static void saveCustomBg(String path){
        sp.edit().putString("CustomBg", path).apply();
    }

    public static void deleteCustomBg(){
        sp.edit().remove("CustomBg").apply();
    }

    public static void saveTheme(String theme){
        sp.edit().putString("theme", theme).apply();
        loadConfig();
    }
}
