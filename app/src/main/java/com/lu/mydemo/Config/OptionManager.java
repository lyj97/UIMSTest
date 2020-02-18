package com.lu.mydemo.Config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 创建时间: 2020/02/18 11:03 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class OptionManager {

    private static boolean has_init = false;

    //成绩统计选项
    private static boolean bixiu_select = true;
    private static boolean xuanxiu_select = true;
    private static boolean xianxuan_select = true;
    private static boolean xiaoxuanxiu_select = false;
    private static boolean PE_select = false;
    private static boolean chongxiu_select = false;

    //课程选项
    private static boolean show_not_current_week_course = true;

    public static void init(Context context){
        SharedPreferences sp = context.getSharedPreferences("MyCustomOption", Context.MODE_PRIVATE);
        if(sp != null){
            bixiu_select = sp.getBoolean("bixiu_select", true);
            xuanxiu_select = sp.getBoolean("xuanxiu_select", true);
            xianxuan_select = sp.getBoolean("xianxuan_select", true);
            xiaoxuanxiu_select = sp.getBoolean("xiaoxuanxiu_select", false);
            PE_select = sp.getBoolean("PE_select", false);
            chongxiu_select = sp.getBoolean("chongxiu_select", false);

            show_not_current_week_course = sp.getBoolean("show_not_current_week_course", true);

            has_init = true;
        }
    }

    public static void saveConfig(Context context){
        SharedPreferences sp = context.getSharedPreferences("MyCustomOption", Context.MODE_PRIVATE);
        if(sp != null){
            sp.edit().putBoolean("bixiu_select", bixiu_select).apply();
            sp.edit().putBoolean("xuanxiu_select", xuanxiu_select).apply();
            sp.edit().putBoolean("xianxuan_select", xianxuan_select).apply();
            sp.edit().putBoolean("xiaoxuanxiu_select", xiaoxuanxiu_select).apply();
            sp.edit().putBoolean("PE_select", PE_select).apply();
            sp.edit().putBoolean("chongxiu_select", chongxiu_select).apply();

            sp.edit().putBoolean("show_not_current_week_course", show_not_current_week_course).apply();
        }
    }

    public static boolean has_init() {
        return has_init;
    }

    public static boolean isBixiu_select() {
        return bixiu_select;
    }

    public static void setBixiu_select(Context context, boolean bixiu_select) {
        OptionManager.bixiu_select = bixiu_select;
        saveConfig(context);
    }

    public static boolean isXuanxiu_select() {
        return xuanxiu_select;
    }

    public static void setXuanxiu_select(Context context, boolean xuanxiu_select) {
        OptionManager.xuanxiu_select = xuanxiu_select;
        saveConfig(context);
    }

    public static boolean isXianxuan_select() {
        return xianxuan_select;
    }

    public static void setXianxuan_select(Context context, boolean xianxuan_select) {
        OptionManager.xianxuan_select = xianxuan_select;
        saveConfig(context);
    }

    public static boolean isXiaoxuanxiu_select() {
        return xiaoxuanxiu_select;
    }

    public static void setXiaoxuanxiu_select(Context context, boolean xiaoxuanxiu_select) {
        OptionManager.xiaoxuanxiu_select = xiaoxuanxiu_select;
        saveConfig(context);
    }

    public static boolean isPE_select() {
        return PE_select;
    }

    public static void setPE_select(Context context, boolean PE_select) {
        OptionManager.PE_select = PE_select;
        saveConfig(context);
    }

    public static boolean isChongxiu_select() {
        return chongxiu_select;
    }

    public static void setChongxiu_select(Context context, boolean chongxiu_select) {
        OptionManager.chongxiu_select = chongxiu_select;
        saveConfig(context);
    }

    public static boolean isShow_not_current_week_course() {
        return show_not_current_week_course;
    }

    public static void setShow_not_current_week_course(Context context, boolean show_not_current_week_course) {
        OptionManager.show_not_current_week_course = show_not_current_week_course;
        saveConfig(context);
    }
}
