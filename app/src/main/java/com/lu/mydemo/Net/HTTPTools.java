package com.lu.mydemo.Net;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 创建时间: 2020/01/14 10:40 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class HTTPTools {

    private static OkHttpClient httpClient;

    static {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(sslContext != null) {
            try {
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }
        // Create an ssl socket factory with our all-trusting manager
        assert sslContext != null;
        final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        httpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) { }

                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        return new ArrayList<>();
                    }
                })
                .followRedirects(false)
                .followSslRedirects(false)
                .connectTimeout(6, TimeUnit.SECONDS)
                .build();
    }

    public static Response getResponse(String url, List<Map.Entry<String, String>> headers){
        try {
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            for(Map.Entry<String, String> entry : headers){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = builder.build();
            Response response = httpClient.newCall(request).execute();
            System.out.println(response);
            return response;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseHeader(String url, List<Map.Entry<String, String>> headers, String headerKey){
        try {
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            for(Map.Entry<String, String> entry : headers){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = builder.build();
            Response response = httpClient.newCall(request).execute();
            System.out.println(response);
            return response.headers().get(headerKey);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseBody(String url, List<Map.Entry<String, String>> headers){
        try {
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            for(Map.Entry<String, String> entry : headers){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = builder.build();
            Response response = httpClient.newCall(request).execute();
            System.out.println(response);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(),
                            StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }
            System.out.println(entityStringBuilder);
            return entityStringBuilder.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Response postResponse(String url, List<Map.Entry<String, String>> headers, RequestBody formBody){
        try {
            Request.Builder builder = new Request.Builder();
            builder.url(url).post(formBody);
            for(Map.Entry<String, String> entry : headers){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = builder.build();
            Response response = httpClient.newCall(request).execute();
            System.out.println(response);
            return response;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String postResponseHeader(String url, List<Map.Entry<String, String>> headers, RequestBody formBody, String headerKey){
        try {
            Request.Builder builder = new Request.Builder();
            builder.url(url).post(formBody);
            for(Map.Entry<String, String> entry : headers){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = builder.build();
            Response response = httpClient.newCall(request).execute();
            System.out.println(response);
            return response.headers().get(headerKey);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String postResponseBody(String url, List<Map.Entry<String, String>> headers, RequestBody formBody){
        try {
            Request.Builder builder = new Request.Builder();
            builder.url(url).post(formBody);
            for(Map.Entry<String, String> entry : headers){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = builder.build();
            Response response = httpClient.newCall(request).execute();
            System.out.println(response);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(),
                            StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }
            System.out.println(entityStringBuilder);
            return entityStringBuilder.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getOrOrOutResponse(Response response, boolean output){
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(response.body()).byteStream(), StandardCharsets.UTF_8), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line).append("\n");
            }
            if(output) {
                System.out.println("------Response Body------");
                System.out.println(entityStringBuilder);
                System.out.println("-------------------------");
            }
            return entityStringBuilder.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
