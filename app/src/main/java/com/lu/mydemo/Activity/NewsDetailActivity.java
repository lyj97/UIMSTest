package com.lu.mydemo.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.OA.NewsClient;
import com.lu.mydemo.R;
import com.tapadoo.alerter.Alerter;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.ToolFor2045_Site.InformationUploader;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.Utils.Thread.MyThreadController;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsDetailActivity extends BaseActivity {

    private TextView detailTitle;
    private TextView detailDepartment;
    private TextView detailTime;
    private TextView detailLink;

    private LinearLayout textSizeChangeLayout;
    private TextView hideText;
    private TextView textSizeEditText;
    private TextView confirmText;

    private TextView navigation_back;

    private String textSize;

    private WebView webView;

    private Bundle bundle;

    private String newsDetail;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("NewsConfig", MODE_PRIVATE);
        setContentView(R.layout.activity_news_detail);

        detailTitle = findViewById(R.id.activity_news_detail_title);
        detailDepartment = findViewById(R.id.activity_news_detail_department);
        detailTime = findViewById(R.id.activity_news_detail_time);
        detailLink = findViewById(R.id.activity_news_detail_link);

        textSizeChangeLayout = findViewById(R.id.activity_news_detail_text_size_change_layout);
        hideText = findViewById(R.id.activity_news_detail_hide_text);
        textSizeEditText = findViewById(R.id.activity_news_detail_text_size_edit_text);
        confirmText = findViewById(R.id.activity_news_detail_text_size_confirm_text);

        navigation_back = findViewById(R.id.activity_news_detail_navigation_back_text);

        changeTheme();

        textSizeChangeLayout.setVisibility(View.GONE);
        textSize = sp.getString("TextSize", "33");
        textSizeEditText.setText(textSize);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize = textSizeEditText.getText().toString();
                getSucceed();
                sp.edit().putString("TextSize", textSize).apply();
            }
        });
        hideText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textSizeChangeLayout.getVisibility() == View.VISIBLE){
                    textSizeChangeLayout.setVisibility(View.GONE);
                    hideText.setBackground(getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp));
                }
                else{
                    textSizeChangeLayout.setVisibility(View.VISIBLE);
                    hideText.setBackground(getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
                }
            }
        });
        boolean isFromOa = false;
        boolean isFromServer = false;
        bundle = getIntent().getBundleExtra("bundle");
        if(bundle != null) {
            detailTitle.setText(bundle.getString("title"));
            detailDepartment.setText(bundle.getString("department"));
            detailTime.setText(bundle.getString("time"));
//        detailLink.setText(Html.fromHtml("<a href=\'" + bundle.getString("abs_link") + "\'>浏览器打开</a>"));
//        detailLink.setMovementMethod(LinkMovementMethod.getInstance());
            detailLink.setText("浏览器打开");
            detailLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(NewsDetailActivity.this, WebViewActivity.class);
                    Bundle web_bundle = new Bundle();
                    web_bundle.putString("link", bundle.getString("abs_link"));
                    intent1.putExtra("bundle", web_bundle);
                    startActivity(intent1);
                }
            });

            isFromOa = bundle.getBoolean("isLoadedFromOA");
            isFromServer = bundle.getBoolean("isLoadedFromServer");
        }

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
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                newsDetail = getNewsDetail();
////                Log.i("NewsDetail", newsDetail);
//                getSucceed(newsDetail);
//            }
//        }).start();
//        webView.loadUrl(bundle.getString("abs_link"));

        if(isFromOa) {
            getNewsDetailFromOA();
        }
        else {
            getNewsDetailFromServer();
        }
        AlertCenter.showLoading(this, "加载中...");
    }

    public void getSucceed(){
        String regEx_style = "style[\\s\\S]*?=[\\s\\S]*?\"[\\s\\S]*?\""; //定义style的正则表达式
        Pattern p_style = Pattern
                .compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(newsDetail);
        final String result = m_style.replaceAll(""); // 过滤style标签
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String mimeType = "text/html";
                String enCoding = "utf-8";
                WebSettings settings = webView.getSettings();
                settings.setUseWideViewPort(true);
                settings.setLoadWithOverviewMode(true);
                settings.setSupportZoom(true);
                settings.setBuiltInZoomControls(true);
                settings.setDisplayZoomControls(false);
                webView.loadDataWithBaseURL(null, "<div style=\"font-size:" + textSize + "; margin:20\"" + result + "</div>", mimeType, enCoding, null);
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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.networkResponse().headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.contains("height:auto!important;width:640px;")) {
//                    Log.e("detail", line);
                    line = line.replace("height:auto!important;width:640px;", "");
                }
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("GetNewsDetail[entity]", "entity:\t" + entityStringBuilder.toString());
//            showResponse("Login[entity]:\t" + entityStringBuilder.toString());

            JSONObject object = JSONObject.fromObject(entityStringBuilder.toString());
            return object.getJSONObject("resultValue").getString("content");
        }
        catch (Exception e){
            e.printStackTrace();
            return "获取失败";
        }
    }

    public void getNewsDetailFromOA(){
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                try {
                    newsDetail = NewsClient.getNewsDetailFromOA(bundle.getString("abs_link"));
                    getSucceed();
                    uploadNewsDetail();
                }
                catch (Exception e){
                    e.printStackTrace();
                    AlertCenter.showErrorAlertWithReportButton(NewsDetailActivity.this, e.getMessage(), e, UIMS.getUser());
                }
            }
        });
    }

    public void getNewsDetailFromServer(){
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                try {
                    String link = bundle.getString("abs_link");
                    if(!TextUtils.isEmpty(link)){
                        newsDetail = NewsClient.getNewsDetailFromServer(link);
                        getSucceed();
                        uploadNewsDetail();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    AlertCenter.showErrorAlertWithReportButton(NewsDetailActivity.this, e.getMessage(), e, UIMS.getUser());
                }
            }
        });
    }

    public void uploadNewsDetail(){
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                try {
                    InformationUploader.uploadOAInformation(bundle.getString("title"), bundle.getString("department"), bundle.getString("abs_link"), newsDetail, bundle.getString("time"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void changeTheme(){
        super.changeTheme();
        findViewById(R.id.activity_news_detail).setBackground(ColorManager.getMainBackground_full());
    }

}
