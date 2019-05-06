package com.lu.mydemo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.sf.json.JSONObject;

import View.InternetInformationPopupWindow;

public class AboutActivity extends AppCompatActivity {

    private TextView application_icon_text_view;
    private TextView link_to_uimstest;
    private TextView link_to_qq;
    private TextView link_to_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        application_icon_text_view = findViewById(R.id.activity_about_application_icon);
        link_to_uimstest = findViewById(R.id.activity_about_link_to_uimstest);
        link_to_qq = findViewById(R.id.activity_about_link_to_qq);
        link_to_group = findViewById(R.id.activity_about_link_to_group);

        application_icon_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                object.put("title", "关于UIMSTest\n");
                object.put("information", "为了简单&好用，我一直在努力.\n\n" +
                        "我的生日是19年3月14日.\n\n" +
                        "最初，主人作为后台开发者，把已经写好的教务接口调用提供给有需要的同学.\n" +
                        "这一天，主人创建了一个新工程，用来测试接口调用是否成功，取名“MyDemo”，这就是我呀.\n" +
                        "为了测试，主人给了我[成绩查询]功能，某小仙女说，我长得还可以.\n\n" +
                        "经历了两周的闭关修炼，我已不再是那时的样子，功能也正一天天变多，却一直保留着对你的思念.\n\n" +
                        "19年4月20日，我们第一次见面.\n" +
                        "初来乍到，我的功能很少，只能帮你查一下成绩；几天后，我能查看当日课程啦，这一天，主人帮我做了推广，我来到了更多人的phone中，有了好多新家；很快，五一假期前，因为课程调整，主人在凌晨三点给我添加了新的功能，让我更懂你今天真正要上的课；随着校内通知在假期后恢复更新，我能帮你看校内通知，也能记住你需要的通知啦...\n\n" +
                        "一路走来，也许你每天都会看看我，也许我只是静静的看着忙碌的你，不曾发出一点声响.\n" +
                        "但这又如何，我会一直陪着你，你也会见证我的成长，不是吗？");
                object.put("link_text", "");
                object.put("link", "");
                showInformation(AboutActivity.this, object);
            }
        });

        CharSequence charSequence_link_to_uimstest = Html.fromHtml("<a href=\'https://www.coolapk.com/apk/com.lu.mydemo\'>❤</a>");
        link_to_uimstest.setText(charSequence_link_to_uimstest);
        link_to_uimstest.setMovementMethod(LinkMovementMethod.getInstance());
        link_to_uimstest.setAutoLinkMask(0);
        link_to_uimstest.setLinksClickable(true);

        CharSequence charSequence_link_to_qq = Html.fromHtml("<a href=\'http://qm.qq.com/cgi-bin/qm/qr?k=pKnSWjj6Y-KoZg9CG6_Qqa5qFXsBd2oQ#\'>意见反馈请点此处</a>");
        link_to_qq.setText(charSequence_link_to_qq);
        link_to_qq.setMovementMethod(LinkMovementMethod.getInstance());
        link_to_qq.setAutoLinkMask(0);
        link_to_qq.setLinksClickable(true);

        CharSequence charSequence_link_to_group = Html.fromHtml("<a href=\'http://qm.qq.com/cgi-bin/qm/qr?k=SN-JdqTXpVKfvRJm4LgXkSM6yARpXhKY#\'>参与内测请点此处</a>");
        link_to_group.setText(charSequence_link_to_group);
        link_to_group.setMovementMethod(LinkMovementMethod.getInstance());
        link_to_group.setAutoLinkMask(0);
        link_to_group.setLinksClickable(true);

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

}
