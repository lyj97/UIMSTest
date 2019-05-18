package Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.lu.mydemo.R;

public class ColorManager {

    /**
     * primaryColor
     * 全局渐变背景
     * 获取本地顶信息按钮颜色
     * 获取网络信息按钮（渐变）颜色
     * 获取网络信息按钮（渐变）颜色(disable)
     * 首页（Login）课程列表文字颜色
     * 顶部悬浮通知背景
     * 下拉列表背景颜色
     * 通知——置顶文字颜色
     * 通知——收藏文字颜色
     * 通知——普通文字颜色
     *...
     */

    private static SharedPreferences sp;
    private static Context context;

    private static int primaryColor;
    private static int primaryDarkColor;
    private static int topAlertBackgroundColor;
    private static int news_notice_text_color;
    private static int news_collected_text_color;
    private static int news_normal_text_color;
    private static int popwindow_background_color;
    private static Drawable mainBackground;
    private static Drawable mainBackground_full;
    private static Drawable localInformationButtonBackground;
    private static Drawable internetInformationButtonBackground;
    private static Drawable internetInformationButtonBackground_full;
    private static Drawable internetInformationButtonBackground_disable;
    private static Drawable internetInformationButtonBackground_disable_full;
    private static Drawable spinnerBackground;

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
        themeName = sp.getString("theme", "blue");

        switch (themeName){
            case "blue" : {
                primaryColor = context.getColor(R.color.colorPrimary);
                primaryDarkColor = context.getColor(R.color.colorPrimaryDark);
                topAlertBackgroundColor = context.getColor(R.color.color_alerter_background);

                news_normal_text_color = context.getColor(R.color.colorPrimary);
                news_notice_text_color = context.getColor(R.color.color_notice_text);
                news_collected_text_color = context.getColor(R.color.color_collected_news_text);

                popwindow_background_color = context.getColor(R.color.color_popWindowBackground);

                mainBackground = context.getDrawable(R.drawable.background_login);
                mainBackground_full = context.getDrawable(R.drawable.background_login_full);
                localInformationButtonBackground = context.getDrawable(R.drawable.button_local_background);
                internetInformationButtonBackground = context.getDrawable(R.drawable.button_internet_background);
                internetInformationButtonBackground_full = context.getDrawable(R.drawable.button_internet_background_full);
                internetInformationButtonBackground_disable = context.getDrawable(R.drawable.button_internet_disable_background);
                internetInformationButtonBackground_disable_full = context.getDrawable(R.drawable.button_internet_disable_background_full);
                spinnerBackground = context.getDrawable(R.drawable.spinner_background);
                break;
            }
            case "pink" : {
                primaryColor = context.getColor(R.color.colorPrimary_Pink);
                primaryDarkColor = context.getColor(R.color.colorPrimaryDark_Pink);
                topAlertBackgroundColor = context.getColor(R.color.color_alerter_background_pink);

                news_normal_text_color = context.getColor(R.color.colorPrimary_Pink);
                news_notice_text_color = context.getColor(R.color.color_notice_text_pink);
                news_collected_text_color = context.getColor(R.color.color_collected_news_text_pink);

                popwindow_background_color = context.getColor(R.color.color_popWindowBackground_pink);

                mainBackground = context.getDrawable(R.drawable.background_login_pink);
                mainBackground_full = context.getDrawable(R.drawable.background_login_pink_full);
                localInformationButtonBackground = context.getDrawable(R.drawable.button_local_background_pink);
                internetInformationButtonBackground = context.getDrawable(R.drawable.button_internet_background_pink);
                internetInformationButtonBackground_full = context.getDrawable(R.drawable.button_internet_background_pink_full);
                internetInformationButtonBackground_disable = context.getDrawable(R.drawable.button_internet_disable_background_pink);
                internetInformationButtonBackground_disable_full = context.getDrawable(R.drawable.button_internet_disable_background_pink_full);
                spinnerBackground = context.getDrawable(R.drawable.spinner_background_pink);
                break;
            }
            case "green" : {
                primaryColor = context.getColor(R.color.colorPrimary_Green);
                primaryDarkColor = context.getColor(R.color.colorPrimaryDark_Green);
                topAlertBackgroundColor = context.getColor(R.color.color_alerter_background_Green);

                news_normal_text_color = context.getColor(R.color.colorPrimary_Green);
                news_notice_text_color = context.getColor(R.color.color_notice_text_Green);
                news_collected_text_color = context.getColor(R.color.color_collected_news_text_Green);

                popwindow_background_color = context.getColor(R.color.color_popWindowBackground_Green);

                mainBackground = context.getDrawable(R.drawable.background_login_green);
                mainBackground_full = context.getDrawable(R.drawable.background_login_green_full);
                localInformationButtonBackground = context.getDrawable(R.drawable.button_local_background_green);
                internetInformationButtonBackground = context.getDrawable(R.drawable.button_internet_background_green);
                internetInformationButtonBackground_full = context.getDrawable(R.drawable.button_internet_background_green_full);
                internetInformationButtonBackground_disable = context.getDrawable(R.drawable.button_internet_disable_background_green);
                internetInformationButtonBackground_disable_full = context.getDrawable(R.drawable.button_internet_disable_background_green_full);
                spinnerBackground = context.getDrawable(R.drawable.spinner_background_green);
                break;
            }
            default:{
                primaryColor = context.getColor(R.color.colorPrimary);
                primaryDarkColor = context.getColor(R.color.colorPrimaryDark);
                topAlertBackgroundColor = context.getColor(R.color.color_alerter_background);

                news_normal_text_color = context.getColor(R.color.colorPrimary);
                news_notice_text_color = context.getColor(R.color.color_notice_text);
                news_collected_text_color = context.getColor(R.color.color_collected_news_text);

                popwindow_background_color = context.getColor(R.color.color_popWindowBackground);

                mainBackground = context.getDrawable(R.drawable.background_login);
                mainBackground_full = context.getDrawable(R.drawable.background_login_full);
                localInformationButtonBackground = context.getDrawable(R.drawable.button_local_background);
                internetInformationButtonBackground = context.getDrawable(R.drawable.button_internet_background);
                internetInformationButtonBackground_full = context.getDrawable(R.drawable.button_internet_background_full);
                internetInformationButtonBackground_disable = context.getDrawable(R.drawable.button_internet_disable_background);
                internetInformationButtonBackground_disable_full = context.getDrawable(R.drawable.button_internet_disable_background_full);
                spinnerBackground = context.getDrawable(R.drawable.spinner_background);
            }
        }
    }

    public static int getPrimaryColor() {
        return primaryColor;
    }

    public static int getPrimaryDarkColor() {
        return primaryDarkColor;
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

    public static Drawable getInternetInformationButtonBackground_disable_full() {
        return internetInformationButtonBackground_disable_full;
    }

    public static int getTopAlertBackgroundColor() {
        return topAlertBackgroundColor;
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

    public static Drawable getSpinnerBackground() {
        return spinnerBackground;
    }

    public static String getThemeName() {
        return themeName;
    }

    public static void saveTheme(String theme){
        sp.edit().putString("theme", theme).apply();
        loadConfig();
    }
}