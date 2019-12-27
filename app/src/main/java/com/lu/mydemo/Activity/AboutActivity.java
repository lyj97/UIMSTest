package com.lu.mydemo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.lu.mydemo.Notification.AlertCenter;

import net.sf.json.JSONObject;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.Config.Version;
import com.lu.mydemo.R;
import com.lu.mydemo.Utils.Rom.EMUIUtils;
import com.lu.mydemo.Utils.Rom.MIUIUtils;
import com.lu.mydemo.View.PopWindow.InternetInformationPopupWindow;

public class AboutActivity extends BaseActivity {

    private TextView application_icon_text_view;
    private TextView application_version_name;
    private TextView link_to_uimstest;
    private TextView link_to_github;
    private TextView link_to_group;
    private TextView link_to_qq;

    private TextView navigation_back;

    private TextView change_color_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        application_icon_text_view = findViewById(R.id.activity_about_application_icon);
        application_version_name = findViewById(R.id.activity_about_application_version_name);
        link_to_uimstest = findViewById(R.id.activity_about_link_to_uimstest);
        link_to_github = findViewById(R.id.activity_about_link_to_github);
        link_to_group = findViewById(R.id.activity_about_link_to_group);
        link_to_qq = findViewById(R.id.activity_about_link_to_qq);

        navigation_back = findViewById(R.id.activity_about_navigation_back_text);

        change_color_text = findViewById(R.id.activity_about_change_color);

        changeTheme();

        if(Version.isIsBeta()) application_version_name.setText("Ver " + Version.getVersionName() + " (BETA)");
        else application_version_name.setText("Ver " + Version.getVersionName());

//        application_icon_text_view.setOnClickListener(new com.lu.mydemo.View.OnClickListener() {
//            @Override
//            public void onClick(com.lu.mydemo.View v) {
//                JSONObject object = new JSONObject();
//                object.put("title", "关于UIMSTest");
//                object.put("information", "为了简单&好用，我一直在努力.\n\n" +
//                        "我的生日是19年3月14日.\n\n" +
//                        "最初，主人作为后台开发者，把已经写好的教务接口调用提供给有需要的同学.\n" +
//                        "这一天，主人创建了一个新工程，用来测试接口调用是否成功，取名“MyDemo”，这就是我呀.\n" +
//                        "为了测试，主人给了我[成绩查询]功能.\n" +
//                        "一直住在主人的Phone中的我，第一次跨过纵横交错的线缆，来到了某小仙女的Phone中；她微笑着对我点点头，在输入框中写下“长得还不错”，按下了发送键.\n\n" +
//                        "这也许是我最开心的一天了吧.\n\n" +
//                        "经历了两周的闭关修炼，我已不再是那时的样子，功能也正一天天变多，却一直保留着对你的思念.\n\n" +
//                        "19年4月20日，我们第一次见面.\n" +
//                        "初来乍到，我的功能很少，只能帮你查一下成绩；几天后，我能查看当日课程啦，这一天，主人帮我做了推广，我来到了更多人的Phone中，有了好多新家；很快，五一假期前，因为课程调整，主人在凌晨三点给我添加了新的功能，让我更懂你今天真正要上的课；随着校内通知在假期后恢复更新，我能帮你看校内通知，也能记住你需要的通知啦；转眼间，属于我们之间的第一个学期已经结束，学期的最后，我可以帮你记下即将到来的考试，见证这一学期的收获；暑假来啦，在校外，我可以帮你查成绩，也可以帮你记下常用的网站了哦~\n\n" +
//                        "一路走来，也许你每天都会看看我，也许我只是静静的看着忙碌的你，不曾发出一点声响.\n" +
//                        "但这又如何，我会一直陪着你，你也会见证我的成长，不是吗？");
//                object.put("link_text", "");
//                object.put("link", "");
//                showInformation(AboutActivity.this, object);
//            }
//        });
        application_icon_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertCenter.showAlert(AboutActivity.this, "感谢使用UIMSTest" ,
                        "可以点击页面中的❤鼓励一下我哦~\n\n" +
                        "如遇问题，请点击“和开发者聊聊；”.\n" +
                        "如果喜欢我，点击“加入内测”，让我变得更好吧~");
            }
        });

        String link_CoolApk = "<a href=\'https://www.coolapk.com/apk/com.lu.mydemo\'>❤</a>";
        String link_MIUI = "<a href=\'http://app.mi.com/details?id=com.lu.mydemo\'>❤</a>";
        String link_EMUI = "<a href=\'https://appstore.huawei.com/app/C100937407\'>❤</a>";

        CharSequence charSequence_link;

        if(MIUIUtils.isMIUI()){
            charSequence_link = Html.fromHtml(link_MIUI);
            Log.i("AboutActivity", "Rom:\t MIUI");
        }
        else if(EMUIUtils.isEMUI()){
            charSequence_link = Html.fromHtml(link_EMUI);
            Log.i("AboutActivity", "Rom:\t EMUI");
        }
        else {
            charSequence_link = Html.fromHtml(link_CoolApk);
            Log.i("AboutActivity", "Rom:\t not MIUI or EMUI");
        }

        link_to_uimstest.setText(charSequence_link);
        link_to_uimstest.setMovementMethod(LinkMovementMethod.getInstance());
        link_to_uimstest.setAutoLinkMask(0);
        link_to_uimstest.setLinksClickable(true);

        CharSequence charSequence_link_to_github = Html.fromHtml("<a href=\'https://github.com/lyj97/UIMSTest\'>\uD83D\uDC40开源项目(Github)</a>");
        link_to_github.setText(charSequence_link_to_github);
        link_to_github.setMovementMethod(LinkMovementMethod.getInstance());
        link_to_github.setAutoLinkMask(0);
        link_to_github.setLinksClickable(true);

        CharSequence charSequence_link_to_group = Html.fromHtml("<a href=\'http://qm.qq.com/cgi-bin/qm/qr?k=SN-JdqTXpVKfvRJm4LgXkSM6yARpXhKY#\'>\uD83D\uDE0E意见反馈、参与开发/内测、Bug反馈请点此处</a>");
        link_to_group.setText(charSequence_link_to_group);
        link_to_group.setMovementMethod(LinkMovementMethod.getInstance());
        link_to_group.setAutoLinkMask(0);
        link_to_group.setLinksClickable(true);

//        CharSequence charSequence_link_to_qq = Html.fromHtml("<a href=\'http://wpa.qq.com/msgrd?v=3&uin=1159386449&site=qq&menu=yes\'>\uD83D\uDC27和开发者聊聊</a>");
//        link_to_qq.setText(charSequence_link_to_qq);
//        link_to_qq.setMovementMethod(LinkMovementMethod.getInstance());
//        link_to_qq.setAutoLinkMask(0);
//        link_to_qq.setLinksClickable(true);

        link_to_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=1159386449";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }catch (Exception e){
                    AlertCenter.showAlert(AboutActivity.this, "可能未安装QQ,不能愉快的和开发者聊天了呢╯︿╰");
                }
            }
        });

        setLinkStyle(link_to_uimstest);
        setLinkStyle(link_to_github);
        setLinkStyle(link_to_group);
        setLinkStyle(link_to_qq);

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        change_color_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, ColorConfigActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        changeTheme();
    }

    public void showInformation(final Activity activity, final JSONObject object){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InternetInformationPopupWindow informationPopWindow = new InternetInformationPopupWindow(activity, object, findViewById(R.id.activity_about_layout).getHeight(), findViewById(R.id.activity_about_layout).getWidth());
                informationPopWindow.showAtLocation(activity.findViewById(R.id.activity_about_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });
    }

    public void setLinkStyle(TextView textView){
        CharSequence text  =  textView.getText();
        if (text instanceof Spannable){

            int  end  =  text.length();
            Spannable sp  =  (Spannable)textView.getText();
            URLSpan[] urls = sp.getSpans( 0 , end, URLSpan.class );

            SpannableStringBuilder style = new  SpannableStringBuilder(text);
            style.clearSpans(); // should clear old spans
            for (URLSpan url : urls){
                URLSpan myURLSpan=   new  URLSpan(url.getURL());
                style.setSpan(myURLSpan,sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(Color.GRAY), sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//设置前景色为灰色
            }
            textView.setText(style);
        }
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

        findViewById(R.id.activity_about_layout).setBackground(ColorManager.getMainBackground_full());
    }

}
