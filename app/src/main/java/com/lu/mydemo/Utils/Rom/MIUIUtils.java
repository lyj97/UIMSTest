package com.lu.mydemo.Utils.Rom;

// 检测MIUI
import android.os.Build;

public final class MIUIUtils {

//    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
//    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
//    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static boolean isMIUI() {
//        try {
//            //BuildProperties 是一个工具类，下面会给出代码
//            final BuildProperties prop = BuildProperties.newInstance();
//            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
//                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
//                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
//        } catch (final IOException e) {
//            return false;
//        }
        return Build.MANUFACTURER.toLowerCase().equals("xiaomi");
    }
}