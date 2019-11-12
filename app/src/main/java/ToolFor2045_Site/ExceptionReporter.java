package ToolFor2045_Site;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import UIMS.Address;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 创建时间: 2019/10/30 14:44 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class ExceptionReporter {

    public static final String HOST_ADDRESS = "http://uimstest." + Address.myHost + ":8199";
//    public static final String HOST_ADDRESS = "http://" + "10.33.78.213" + ":8199";

    static OkHttpClient httpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) { }

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

    public static String reportException(Exception exception, String userId) throws Exception{
        Log.i("ExceptionReporter", "ReportResponse");
        if(exception == null) return "";
        FormBody formBody = new FormBody.Builder()
                .add("name", exception.getClass().getName())
                .add("message", exception.getMessage() == null ? "" : exception.getMessage())
                .add("detail", Arrays.asList(exception.getStackTrace()).toString())
                .add("user_id", userId)
                .build();
        Request request = new Request.Builder()
                .url(HOST_ADDRESS + "/api/exception/upload")
                .post(formBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
        StringBuilder entityStringBuilder = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            entityStringBuilder.append(line + "\n");
        }
        Log.i("ExceptionReporter", "ReportResponse:" + entityStringBuilder);
        return entityStringBuilder.toString();
    }

    public static String reportException(List<Exception> exceptions, String userId) throws Exception{
        Log.i("ExceptionReporter", "ReportResponse(List)");
        if(exceptions == null || exceptions.size() == 0) return "";
        FormBody formBody = new FormBody.Builder()
                .add("name", exceptions.get(0).getClass().getName())
                .add("message", exceptions.get(0).getMessage() == null ? "" : exceptions.get(0).getMessage())
                .add("detail", getExceptionString(exceptions))
                .add("user_id", userId)
                .build();
        Request request = new Request.Builder()
                .url(HOST_ADDRESS + "/api/exception/upload")
                .post(formBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
        StringBuilder entityStringBuilder = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            entityStringBuilder.append(line + "\n");
        }
        Log.i("ExceptionReporter", "ReportResponse(List):" + entityStringBuilder);
        return entityStringBuilder.toString();
    }

    public static String getExceptionString(List<Exception> exceptions){
        StringBuilder builder = new StringBuilder();
        for(Exception exception : exceptions){
            builder.append(Arrays.asList(exception.getStackTrace())).append(",");
        }
        return builder.toString();
    }

}
