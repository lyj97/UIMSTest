package com.lu.mydemo.Utils.YKT;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 创建时间: 2019/12/25 18:16 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class YKTInformation {

    private static String userName;
    private static String password;
    private static SharedPreferences sp;

    public static void init(Context context){
        sp = context.getSharedPreferences("YKTInf", Context.MODE_PRIVATE);
        userName = sp.getString("user", "");
        password = sp.getString("pass", "");
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        YKTInformation.userName = userName;
        if(sp != null){
            sp.edit().putString("user", userName).apply();
        }
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        YKTInformation.password = password;
        if(sp != null){
            sp.edit().putString("pass", password).apply();
        }
    }
}
