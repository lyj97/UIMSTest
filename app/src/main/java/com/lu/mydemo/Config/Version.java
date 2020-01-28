package com.lu.mydemo.Config;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class Version {

    private static int versionMajor = 1;
    private static int versionMinor = 2;
    private static int versionPatch = 18;
    private static boolean isBeta = false;

    public static String getVersionName(){
        return versionMajor + "." + versionMinor + "." + versionPatch;
    }

    public static int getVersionCode(){
        return versionMajor * 10000 + versionMinor * 100 + versionPatch;
    }

    public static boolean isIsBeta() {
        return isBeta;
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
