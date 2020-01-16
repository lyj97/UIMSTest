package com.lu.mydemo.ToolFor2045_Site;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.lu.mydemo.Utils.Common.Address;

import org.jetbrains.annotations.NotNull;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static net.sf.json.JSONObject.fromObject;

public class GetInternetInformation {

    private OkHttpClient httpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) { }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                    return new ArrayList<>();
                }
            })
//            .addInterceptor(new LoggingInterceptor())
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    private MultipartBody.Builder builder;

    public JSONObject getVersionInformation() {
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/GetVersionInformation")
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
//                    request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s",
//                    response.request().url(), response.networkResponse().headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("2045_getVersionInformation[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());

            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getRecommendChange() {
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/GetRecommendChange")
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
//                    request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s",
//                    response.request().url(), response.networkResponse().headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("2045_getRecommendChange[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());

            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getTestItems() {
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/GetTestFunctionItems")
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
//                    request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s",
//                    response.request().url(), response.networkResponse().headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("2045_getTestFunctionItems[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());

            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getWebPagesItems(){
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/common/website/list")
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getNewsList(int page) {
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/GetNewsList?page=" + page)
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
//                    request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s",
//                    response.request().url(), response.networkResponse().headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("2045_getNewsList[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());

            try {
                return JSONObject.fromObject(entityStringBuilder.toString());

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getNewNewsList(int page, String searchStr) {
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            FormBody formBody = new FormBody.Builder()
                    .add("page", String.valueOf(page))
                    .add("searchName", searchStr == null ? "" : searchStr)
                    .build();
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/common/oa/list")
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
            try {
                return JSONObject.fromObject(entityStringBuilder.toString());

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getNewsDetail(String id){
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            FormBody formBody = new FormBody.Builder()
                    .add("id", id)
                    .build();
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/common/oa/detail")
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
            try {
                return JSONObject.fromObject(entityStringBuilder.toString());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject searchNews(String queryStr) {
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/SearchNews?queryStr=" + queryStr)
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.networkResponse().headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("2045_searchNews[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());

            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取服务端OA列表返回页数
     * @return
     * {
     *     status:0
     *     message:""
     *     data:{
     *         page:0
     *     }
     * }
     */
    public JSONObject getRemoteNewsPage(){
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/common/oa/pageNumber")
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.networkResponse().headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("2045_searchNews[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());
            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
