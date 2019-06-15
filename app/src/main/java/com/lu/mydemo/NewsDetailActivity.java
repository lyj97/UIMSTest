package com.lu.mydemo;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import Config.ColorManager;
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

    private TextView navigation_back;

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

        navigation_back = findViewById(R.id.activity_news_detail_navigation_back_text);

        changeTheme();

        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        detailTitle.setText(bundle.getString("title"));
        detailDepartment.setText(bundle.getString("department"));
        detailTime.setText(bundle.getString("time"));
        detailLink.setText(Html.fromHtml("<a href=\'" + bundle.getString("abs_link") + "\'>浏览器打开</a>"));
        detailLink.setMovementMethod(LinkMovementMethod.getInstance());

        CharSequence text  =  detailLink.getText();
        if (text instanceof Spannable){

            int  end  =  text.length();
            Spannable sp  =  (Spannable)detailLink.getText();
            URLSpan[] urls = sp.getSpans( 0 , end, URLSpan.class );

            SpannableStringBuilder style = new  SpannableStringBuilder(text);
            style.clearSpans(); // should clear old spans
            for (URLSpan url : urls){
                URLSpan myURLSpan=   new  URLSpan(url.getURL());
                style.setSpan(myURLSpan,sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(Color.WHITE), sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//设置前景色为白色
            }
            detailLink.setText(style);
        }

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        findViewById(R.id.activity_news_detail).setBackground(ColorManager.getMainBackground_full());
    }

    public void showLoading(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NewsDetailActivity.this)
                        .setText(message)
                        .enableProgress(true)
                        .setDismissable(false)
                        .setProgressColorRes(R.color.color_alerter_progress_bar)
                        .setDuration(Integer.MAX_VALUE)
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

}
