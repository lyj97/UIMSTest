package com.lu.mydemo.ToolFor2045_Site;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.lu.mydemo.Config.Version;
import com.lu.mydemo.Utils.Common.Address;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 创建时间: 2019/10/30 14:44 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class InformationUploader {

    public static final String HOST_ADDRESS = "http://uimstest." + Address.myHost + ":8199";
    //    public static final String HOST_ADDRESS = "http://10.151.150.106:8199";
    public static String USER_MAIL = "";
    public static SharedPreferences sp;
//    public static final String HOST_ADDRESS = "http://" + "10.33.78.213" + ":8199";

    static OkHttpClient httpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    return new ArrayList<>();
                }
            })
//            .addInterceptor(new LoggingInterceptor())
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    public static void initUserInformation(Context context) {
        sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (sp == null) return;
        if (sp.contains("UserEmail")) {
            USER_MAIL = sp.getString("UserEmail", "");
        }
    }

    public static void setUserMail(String mail) {
        USER_MAIL = mail;
        saveUserEmail();
    }

    public static void saveUserEmail() {
        if (sp == null) return;
        sp.edit().putString("UserEmail", USER_MAIL).apply();
    }

    public static String reportException(Exception exception, String userId) throws Exception {
//        Log.i("InformationUploader", "ReportResponse");
        if (exception == null) return "";
        FormBody formBody = new FormBody.Builder()
                .add("version", Version.getVersionName() + "(" + Version.getVersionCode() + ")")
                .add("name", exception.getClass().getName())
                .add("message", exception.getMessage() == null ? "" : exception.getMessage())
                .add("detail", Arrays.asList(exception.getStackTrace()).toString())
                .add("user_id", userId)
                .add("mail", USER_MAIL)
                .build();
        Request request = new Request.Builder()
                .url(HOST_ADDRESS + "/api/exception/upload")
                .post(formBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8), 8 * 1024);
        StringBuilder entityStringBuilder = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            entityStringBuilder.append(line + "\n");
        }
//        Log.i("InformationUploader", "ReportResponse:" + entityStringBuilder);
        return entityStringBuilder.toString();
    }

    public static String reportException(List<Exception> exceptions, String userId) throws Exception {
//        Log.i("InformationUploader", "ReportResponse(List)");
        if (exceptions == null || exceptions.size() == 0) return "";
        FormBody formBody = new FormBody.Builder()
                .add("version", Version.getVersionName() + "(" + Version.getVersionCode() + ")")
                .add("name", exceptions.get(0).getClass().getName())
                .add("message", exceptions.get(0).getMessage() == null ? "" : exceptions.get(0).getMessage())
                .add("detail", getExceptionString(exceptions))
                .add("user_id", userId)
                .add("mail", USER_MAIL)
                .build();
        Request request = new Request.Builder()
                .url(HOST_ADDRESS + "/api/exception/upload")
                .post(formBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8), 8 * 1024);
        StringBuilder entityStringBuilder = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            entityStringBuilder.append(line + "\n");
        }
//        Log.i("InformationUploader", "ReportResponse(List):" + entityStringBuilder);
        return entityStringBuilder.toString();
    }

    public static String getExceptionString(List<Exception> exceptions) {
        StringBuilder builder = new StringBuilder();
        for (Exception exception : exceptions) {
            builder.append(Arrays.asList(exception.getStackTrace())).append(",");
        }
        return builder.toString();
    }

    public static String uploadOAInformation(String title, String department, String link,
                                             String detail, String publishTime) throws Exception {
        if (title == null || link == null || detail == null || publishTime == null) return "";
        FormBody formBody = new FormBody.Builder()
                .add("title", title)
                .add("department", department)
                .add("link", link)
                .add("detail", detail)
                .add("publishTime", publishTime)
                .build();
        Request request = new Request.Builder()
                .url(HOST_ADDRESS + "/api/common/oa/upload")
                .post(formBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8), 8 * 1024);
        StringBuilder entityStringBuilder = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            entityStringBuilder.append(line + "\n");
        }
//        Log.i("InformationUploader", "UploadOAInformation:" + entityStringBuilder);
        return entityStringBuilder.toString();
    }

    public static String getVerifyCodeStr(byte[] bytes) throws Exception {
        //2.创建RequestBody
        RequestBody fileBody = RequestBody.create(bytes);

        //3.构建MultipartBody
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pic", "verifyCode.jpg", fileBody)
                .build();

        Request request = new Request.Builder()
                .url(HOST_ADDRESS + "/api/verifyCode/getCode")
                .post(requestBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8), 8 * 1024);
        StringBuilder entityStringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            entityStringBuilder.append(line + "\n");
        }
        Log.i("InformationUploader", "getVerifyCodeStr:" + entityStringBuilder);
        return entityStringBuilder.toString();
    }

}
