package com.lu.mydemo.Utils.String;

import android.text.TextUtils;

/**
 * 创建时间: 2019/12/02 20:11 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class FormatCheck {

    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

}
