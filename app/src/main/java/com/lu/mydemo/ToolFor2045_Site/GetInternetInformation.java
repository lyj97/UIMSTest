package com.lu.mydemo.ToolFor2045_Site;

import com.lu.mydemo.Config.Version;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.Utils.Common.Address;

import net.sf.json.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    public JSONObject getVersionInformation() {
        try {
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/GetVersionInformation")
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }

            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getRecommendChange() {
        try {
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/GetRecommendChange")
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }

            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getTestItems() {
        try {
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/GetTestFunctionItems")
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }

            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getWebPagesItems(){
        try {
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/common/website/list")
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
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
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/GetNewsList?page=" + page)
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
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

    public JSONObject getNewNewsList(int page, String searchStr) {
        try {
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
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
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
            FormBody formBody = new FormBody.Builder()
                    .add("id", id)
                    .build();
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/common/oa/detail")
                    .post(formBody)
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
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
            Request request = new Request.Builder()
                    .url(Address.myHostAddress + "/UIMSTest/SearchNews?queryStr=" + queryStr)
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }

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
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/common/oa/pageNumber")
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }
            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取服务器下发的更改VPN首页指向链接的js代码
     * @return JSCodeJSON
     */
    public JSONObject getRemoteJSCode(String inputLink){
        try {
            FormBody formBody = new FormBody.Builder()
                    .add("link", inputLink)
                    .build();
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/LinkJSCode/getCode")
                    .post(formBody)
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }
            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取服务器下发的Config数据
     * @return JSCodeJSON
     */
    public JSONObject getRemoteConfig(){
        try {
            FormBody formBody = new FormBody.Builder()
                    .add("user", UIMS.getUser() == null ? "" : UIMS.getUser())
                    .add("version", String.valueOf(Version.getVersionCode()))
                    .build();
            Request request = new Request.Builder()
                    .url(Address.myHostAddress_HUAWEI + "/api/config/getConfigJSON")
                    .post(formBody)
                    .build();
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }
            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
