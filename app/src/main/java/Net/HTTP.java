package Net;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTP {

    OkHttpClient httpClient = new OkHttpClient.Builder()
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
    //    MultipartBody.Builder builder;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String sendGet_getHeader_setCookie(String address){
        try {
//            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(address)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.networkResponse().headers()));
            return response.headers().get("Set-Cookie");
//            str = str.split(";")[0];
//            jssionID = str.split("=")[1];
//            cookie1 = "loginPage=userLogin.jsp; alu=" + user + "; pwdStrength=1; JSESSIONID=" + jssionID;
//            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String sendPost_form_getHhader_setCookie(String address, String referer, String cookie, FormBody formBody){
        try {
            Request request = new Request.Builder()
                    .url(address)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie)
                    .header("Referer", referer)
                    .post(formBody)
                    .build();
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.headers()));

            try{
                String str = response.headers().get("Location");
                if(str.contains("loginError")){
                    return null;
                }
            } catch (Exception e){
//                e.printStackTrace();
            }

            return response.headers().get("Set-Cookie");
//            str = str.split(";")[0];
//            jssionID2 = str.split("=")[1];
//
//            cookie3 = "loginPage=userLogin.jsp; alu=" + user + "; pwdStrength=1; JSESSIONID=" + jssionID2;
//            cookie4 = "loginPage=userLogin.jsp; alu=" + user + "; JSESSIONID=" + jssionID2;
//
//            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String sendPost_nothing(String address, String referer, String origin, String cookie){
        try{
            FormBody formBody = new FormBody.Builder().build();
            Request request = new Request.Builder()
                    .url(address)
                    .header("Referer", referer)
                    .header("Connection", "keep-alive")
                    .header("Origin", origin)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie)
                    .post(formBody)
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("Login[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());
            return entityStringBuilder.toString();

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String sendPost_JSON(String address, String referer, String host, String origin, String cookie, JSONObject jsonObject){
        try {
            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(address)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie)
                    .header("Host", host)
                    .header("Origin", origin)
                    .header("Content-Type", "application/json")
                    .header("Referer", referer)
                    .post(requestBody)
                    .build();
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }

            return entityStringBuilder.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String sendPost_JSON(String address, String referer, String host, String cookie, JSONObject jsonObject){
        try {
            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(address)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie)
                    .header("Host", host)
                    .header("Content-Type", "application/json")
                    .header("Referer", referer)
                    .post(requestBody)
                    .build();
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }

            return entityStringBuilder.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
