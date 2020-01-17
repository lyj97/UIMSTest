package com.lu.mydemo.Utils.StudentVPN;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lu.mydemo.Net.HTTPTools;
import com.lu.mydemo.Utils.Common.Address;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.Response;

/**
 * 创建时间: 2020/01/16 16:38 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class VPNClient {

    private static String vpnKey = "VPNKey";

    private String mUser;
    private String mPass;

    private String wengine_vpn_ticket_ecit;//CookieId
    private String cookie;
    private String logoutOtherToken;

    private static String baseUrl = Address.vpnAddress;
    private static String userAgentStr = "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36";

    public static VPNClient getInstance(Context context){
        SharedPreferences sp = context.getSharedPreferences(vpnKey, Context.MODE_PRIVATE);
        if(sp != null){
            VPNClient vpnClient = new VPNClient();
            vpnClient.mUser = sp.getString("user", "");
            vpnClient.mPass = sp.getString("pass", "");
            return vpnClient;
        }
        else {
            return null;
        }
    }

    public static boolean setUserData(Context context, String user, String pass){
        SharedPreferences sp = context.getSharedPreferences(vpnKey, Context.MODE_PRIVATE);
        if(sp != null){
            sp.edit().putString("user", user).apply();
            sp.edit().putString("pass", pass).apply();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * @return
     *      0 成功
     *      -1 账号/密码为空
     *      -2 获取Cookie失败
     *      -3 登录失败
     *      -4 确认登录失败
     */
    public int startLogin(){
        if(TextUtils.isEmpty(mUser) || TextUtils.isEmpty(mPass)){
            return -1;
        }
        if(!getLoginCookie()){
            return -2;
        }
        if(!login()){
            return -3;
        }
        int result = getLoginPage();
        if(result == 0){
            if(!TextUtils.isEmpty(logoutOtherToken)){
                if(!confirmLogin()){
                    return -4;
                }
            }
            if(getMainPage()){
                return 0;
            }
            else {
                return -3;
            }
        }
        else return -3;
    }

    public boolean getLoginCookie(){
        String url = baseUrl + "/";
        String headerKey = "set-cookie";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", userAgentStr);
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        String headerValue = HTTPTools.getResponseHeader(url, headers, headerKey);

        if(TextUtils.isEmpty(headerValue)){
            return false;
        }

        try{
            cookie = headerValue.split(";")[0];
            wengine_vpn_ticket_ecit = cookie.split("=")[1];
        }
        catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("wengine_vpn_ticket_ecit:\t" + wengine_vpn_ticket_ecit);

        return !TextUtils.isEmpty(headerValue);
    }

    public boolean login(){
        String url = baseUrl + "/do-login?fromUrl=";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", userAgentStr);
        mHeaderList.put("Cookie", cookie);
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        FormBody formBody = new FormBody.Builder()
                .add("auth_type", "local")
                .add("username", mUser)
                .add("sms_code", mUser)
                .add("password", mPass)
                .build();

        Response response = HTTPTools.postResponse(url, headers, formBody);
        assert response != null;
        return true;
    }

    public boolean confirmLogin(){
        String url = baseUrl + "/do-confirm-login";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", userAgentStr);
        mHeaderList.put("Cookie", cookie);
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        FormBody formBody = new FormBody.Builder()
                .add("username", mUser)
                .add("logoutOtherToken", logoutOtherToken)
                .build();

        Response response = HTTPTools.postResponse(url, headers, formBody);
        assert response != null;
        System.out.println("Response:\t" + response.toString());
        return true;
    }

    public boolean getMainPage(){
        String url = baseUrl + "/";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", userAgentStr);
        mHeaderList.put("Cookie", cookie);
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        Response response = HTTPTools.getResponse(url, headers);
        assert response != null;
        System.out.println("Response:\t" + response.toString());
        return true;
    }

    public int getLoginPage(){
        String url = baseUrl + "/login";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", userAgentStr);
        mHeaderList.put("Cookie", cookie);
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        Response response = HTTPTools.getResponse(url, headers);

        try {
            assert response != null;
            String responseBodyStr = HTTPTools.getOrOrOutResponse(response, false);
            assert responseBodyStr != null;
            String startStr = "var logoutOtherToken";
            String endStr = "if (logoutOtherToken != \"\")";
            int start_index = responseBodyStr.indexOf(startStr);
            int end_index = responseBodyStr.indexOf(endStr);
            if(start_index > 0 && end_index > 0) {
                logoutOtherToken = responseBodyStr.substring(start_index + startStr.length(), end_index);
                logoutOtherToken = logoutOtherToken.split("'")[1];
                System.out.println("logoutOtherToken:\t" + logoutOtherToken);
                return 0;
            }
            else {
                System.out.println("Start:" + start_index);
                System.out.println("End:" + end_index);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public String getUser() {
        return mUser;
    }

    public String getPass() {
        return mPass;
    }

    public String getWengine_vpn_ticket_ecit() {
        return wengine_vpn_ticket_ecit;
    }

    public String getCookie() {
        return cookie;
    }

    public String getLogoutOtherToken() {
        return logoutOtherToken;
    }
}
