package com.lu.mydemo;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lu.mydemo.Notification.AlertCenter;

import java.util.HashMap;

import Config.ColorManager;
import View.PopWindow.DownloadFileConfirmPopupWindow;

public class WebViewActivity extends BaseActivity {

    private TextView titleTextView;
    private TextView backTextView;
    private TextView closeTextView;

    private ProgressBar loadingProgressBar;
    private WebView webView;

    private Bundle bundle;

    private boolean hasTryed = false;
    private boolean isGranted = true;

    private HashMap<String, String> myHeaders;

    private DownloadCompleteReceiver receiver;

    //下载文件 临时信息
    private DownloadManager.Request request;
    private String fileName;

    private PopupWindow popWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        titleTextView = findViewById(R.id.activity_web_view_title);
        backTextView = findViewById(R.id.activity_web_view_navigation_back_text);
        closeTextView = findViewById(R.id.activity_web_view_navigation_close_text);

        loadingProgressBar = findViewById(R.id.activity_web_view_loading_progress_bar);
        webView = findViewById(R.id.activity_web_view_web_view);

        changeTheme();

        closeTextView.setVisibility(View.GONE);
        closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                new Thread(){
                    public void run() {
                        try{
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        }
                        catch (Exception e) {
                            Log.e("Exception when goBack", e.toString());
                        }
                    }
                }.start();
            }
        });

        WebSettings settings = webView.getSettings();

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);

        String link = bundle.getString("link");
        Log.i("WebView", "link:\t" + link);
        if(link == null){
            AlertCenter.showErrorAlert(WebViewActivity.this, "ERROR!", "Link is NULL!");
        }
        else if(link.contains("oa.jlu.edu.cn")){
            myHeaders = new HashMap<>();
            myHeaders.put("x-forwarded-for", "49.140.97.100");
            webView.loadUrl(link, myHeaders);
        }
        else {
            webView.loadUrl(link);
        }

        webView.setWebViewClient(new MyWebViewClient());

        webView.setWebChromeClient(new MyWebChromeClient());

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                checkFilePermission();

                try {

                    // TODO: 处理下载事件
                    // 指定下载地址
                    request = new DownloadManager.Request(Uri.parse(url));
                    // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
                    request.allowScanningByMediaScanner();
                    // 设置通知的显示类型，下载进行时和完成后显示通知
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
                    // 设置通知栏的描述
//        request.setDescription("This is description");
                    // 允许在计费流量下下载
                    request.setAllowedOverMetered(false);
                    // 允许该记录在下载管理界面可见
                    request.setVisibleInDownloadsUi(true);
                    // 允许漫游时下载
                    request.setAllowedOverRoaming(true);
                    // 允许下载的网路类型
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                    // 设置下载文件保存的路径和文件名
                    fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                    Log.d("fileName:", fileName);

                    if(fileName.contains("�")) {
                        AlertCenter.showWarningAlert(WebViewActivity.this, "So Sorry", "抱歉，文件名可能乱码，请手动更改文件名后确认.");
                    }

                    loadingProgressBar.setVisibility(View.GONE);
                    //下载确认对话框
                    DownloadFileConfirmPopupWindow window = new DownloadFileConfirmPopupWindow(WebViewActivity.this, fileName.substring(0, fileName.lastIndexOf(".")), fileName.substring(fileName.lastIndexOf(".")), findViewById(R.id.activity_web_view).getHeight(), findViewById(R.id.activity_web_view).getWidth());
                    window.setFocusable(true);
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    window.showAtLocation(WebViewActivity.this.findViewById(R.id.activity_web_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                    popWindow = window;

//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
////        另外可选一下方法，自定义下载路径
////        request.setDestinationUri()
////        request.setDestinationInExternalFilesDir()
//                    final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                    // 添加一个下载任务
//                    long downloadId = downloadManager.enqueue(request);
//                    Log.d("downloadId:", downloadId + "");

                } catch (Exception e) {
                    if(e.getMessage().toLowerCase().contains("no permission to write to")){
                        AlertCenter.showErrorAlert(WebViewActivity.this, "ERROR!", "下载文件需要您同意“读写手机存储”权限，请同意后重试！");
                        WebViewActivity.this.requestPermissions(
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                        .ACCESS_FINE_LOCATION,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                102);
                    }
                    else AlertCenter.showErrorAlert(WebViewActivity.this, "Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        //注册下载监听
        receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public void commitDownload(){
        try {
            Log.d("fileName:", fileName);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
            final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            // 添加一个下载任务
            long downloadId = downloadManager.enqueue(request);
            Log.d("downloadId:", downloadId + "");
            this.popWindow.dismiss();
            this.popWindow = null;
        } catch (Exception e) {
            if(e.getMessage().toLowerCase().contains("no permission to write to")){
                AlertCenter.showErrorAlert(WebViewActivity.this, "ERROR!", "下载文件需要您同意“读写手机存储”权限，请同意后重试！");
                WebViewActivity.this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }
            else AlertCenter.showErrorAlert(WebViewActivity.this, "Error", e.getMessage());
            e.printStackTrace();
        }
    }

    public void cancelDownload(){
        this.request = null;
        this.fileName = null;
        this.popWindow.dismiss();
        this.popWindow = null;
    }

    public void checkFilePermission() {
        isGranted = true;
        if (WebViewActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //如果没有写sd卡权限
            isGranted = false;
        }
        if (WebViewActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            isGranted = false;
        }
        Log.i("File","isGranted == "+isGranted);
        if (!isGranted) {
            WebViewActivity.this.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                            .ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    102);
        }
    }

    private class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive. intent:", intent != null ? intent.toUri(0) : null);
            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    Log.d("downloadId:{}", downloadId + "");
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                    Log.d("getMimeTypeForDownloadedFile:{}", type);
                    if (TextUtils.isEmpty(type)) {
                        type = "*/*";
                    }
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    Log.d("UriForDownloadedFile:", uri + "");
                    if (uri != null) {
                        Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
                        handlerIntent.setDataAndType(uri, type);
                        context.startActivity(handlerIntent);
                    }
                }
            }
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override  //WebView代表是当前的WebView
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //表示在当前的WebView继续打开网页
            if(request.getUrl().toString().contains("oa.jlu.edu.cn")) view.loadUrl(request.getUrl().toString(), myHeaders);
            else view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            Log.d("WebView","开始访问网页");
//            AlertCenter.showLoading(WebViewActivity.this, "加载中...");
            loadingProgressBar.setVisibility(View.VISIBLE);
            if(webView.canGoBack()) closeTextView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            Log.d("WebView","访问网页结束");
            AlertCenter.hideAlert(WebViewActivity.this);
            loadingProgressBar.setVisibility(View.GONE);
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override //监听加载进度
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            loadingProgressBar.setProgress(newProgress);
        }
        @Override//接受网页标题
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //把当前的Title设置到Activity的title上显示
            setTitle(title);
        }
    }

    public void setTitle(final String title){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText(title);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按返回键，此时WebView网页可以后退
        if (keyCode==KeyEvent.KEYCODE_BACK&&webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeTheme(){
        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ColorManager.getNoCloor());
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);

        findViewById(R.id.activity_web_view).setBackground(ColorManager.getMainBackground_full());
    }
}
