package com.lu.mydemo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tapadoo.alerter.Alerter;

import net.sf.json.JSONObject;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import UIMS.Address;
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

public class NewsDetailActivity extends AppCompatActivity {

    private TextView detailTitle;
    private TextView detailDepartment;
    private TextView detailTime;
    private TextView detailLink;

    private WebView webView;

    private Bundle bundle;

    private String newsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        detailTitle = findViewById(R.id.activity_news_detail_title);
        detailDepartment = findViewById(R.id.activity_news_detail_department);
        detailTime = findViewById(R.id.activity_news_detail_time);
        detailLink = findViewById(R.id.activity_news_detail_link);

        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        detailTitle.setText(bundle.getString("title"));
        detailDepartment.setText(bundle.getString("department"));
        detailTime.setText(bundle.getString("time"));
        detailLink.setText(Html.fromHtml("<a href=\'" + bundle.getString("abs_link") + "\'>ⓘ</a>"));
        detailLink.setMovementMethod(LinkMovementMethod.getInstance());

        webView = findViewById(R.id.activity_news_detail_web_view);
        new Thread(new Runnable() {
            @Override
            public void run() {
                newsDetail = getNewsDetail();
                getSucceed(newsDetail);
            }
        }).start();
        showLoading("加载中...");
    }

    public void getSucceed(final String detail){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String mimeType = "text/html";
                String enCoding = "utf-8";
                WebSettings settings = webView.getSettings();
                settings.setSupportZoom(true);
                settings.setBuiltInZoomControls(true);
                settings.setDisplayZoomControls(false);
                webView.loadDataWithBaseURL(null, detail, mimeType, enCoding, null);
                Alerter.hide();
            }
        });
    }

    public String getNewsDetail(){
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
        try {
//            final MediaType MEDIA_TYPE_NORAML_FORM = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
//            RequestBody body = RequestBody.create(MEDIA_TYPE_NORAML_FORM,"link=" + bundle.getString("link"));
            FormBody formBody = new FormBody.Builder()
                    .add("link", bundle.getString("link"))
                    .build();
            Request request = new Request.Builder()
                    .url("http://202.98.18.57:18080/webservice/m/api/getNewsDetail")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Host", "202.98.18.57:18080")
                    .post(formBody)
                    .build();
//                    Log.i("okhttp_request", request.toString());
            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
                    request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
            Log.i("OKHttp_Request", String.format("Received response for %s %n%s",
                    response.request().url(), response.networkResponse().headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
            Log.i("GetNewsDetail[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());

            JSONObject object = JSONObject.fromObject(entityStringBuilder.toString());
            return object.getJSONObject("resultValue").getString("content");
        }
        catch (Exception e){
            e.printStackTrace();
            return "获取失败";
        }
    }

    public void showLoading(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NewsDetailActivity.this)
                        .setText(message)
                        .enableProgress(true)
                        .setProgressColorRes(R.color.color_21)
                        .setDuration(10000)
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                        .show();
            }
        });
    }

}
