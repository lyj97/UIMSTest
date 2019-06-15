package com.lu.mydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import Config.ColorManager;
import UIMS.UIMS;
import View.PopWindow.LoginGetCourseSchedulePopupWindow;
import View.PopWindow.LoginPingjiaoPopupWindow;

import static UIMS.UIMSTest.getAnswer;

public class PingjiaoActivity extends AppCompatActivity {

    TextView backText;
    TextView mainText;

    UIMS uims;
    boolean isLogin = false;

    LoginPingjiaoPopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pingjiao);

        backText = findViewById(R.id.activity_pingjiao_navigation_back_text);
        mainText = findViewById(R.id.activity_pingjiao_main_text);

        mainText.setText("");

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeTheme();

        showWarningAlertWithCancel_OKButton("评教", "需要评教吗？\n" +
                "评价方式：全部好评，没有建议。");
    }

    public void addText(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainText.setText(mainText.getText() + "\n" + str);
            }
        });
    }

    public UIMS getUims() {
        return uims;
    }

    public void setUims(UIMS uims) {
        this.uims = uims;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public void dismissPingjiaoPopWindow(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        });
    }

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        findViewById(R.id.activity_pingjiao_layout).setBackground(ColorManager.getMainBackground_full());
    }

    public void showResponse(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.hide();
                Toast.makeText(PingjiaoActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLoading(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(PingjiaoActivity.this)
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

    public void showAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(PingjiaoActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showAlert(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(PingjiaoActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showWarningAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(PingjiaoActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .show();
            }
        });
    }

    public void showWarningAlert(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(PingjiaoActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .show();
            }
        });
    }

    public void showWarningAlertWithCancel_OKButton(final String title, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(PingjiaoActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .addButton("取消", R.style.AlertButton, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Alerter.hide();
                            }
                        })
                        .addButton("评教！", R.style.AlertButton, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoginPingjiaoPopupWindow window = new LoginPingjiaoPopupWindow(PingjiaoActivity.this, findViewById(R.id.activity_pingjiao_layout).getHeight(), findViewById(R.id.activity_pingjiao_layout).getWidth(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(isLogin){
                                            ArrayList<String> list = uims.post_pingjiao_information();
                                            if(list.size() == 0){
                                                addText("【您已经完成评教！】");
                                                return;
                                            }
                                            HashSet<String> names = uims.getClassStudentName();
                                            System.out.println(names);
                                            System.out.println(list);
                                            addText("names:");
                                            addText(names.toString());
                                            addText("list:");
                                            addText(list.toString());

                                            JSONObject pingjiao_inf;
                                            JSONObject targetClar;
                                            ArrayList<String> answer;
                                            String puzzle;
                                            String[] parts;
                                            char[] name_chars;
                                            char[] puzzle_chars;
                                            String ans;

                                            for(int i=0; i<list.size(); i++){
                                                pingjiao_inf = uims.post_pingjiao(list.get(i));
                                                puzzle = pingjiao_inf.getString("puzzle");
                                                parts = puzzle.split("_");
                                                answer = getAnswer(parts, names, puzzle.length());
                                                System.out.println(answer);
                                                targetClar = pingjiao_inf.getJSONObject("targetClar");
                                                addText("person:");
                                                addText(targetClar.getString("person"));
                                                addText("notes:");
                                                addText(targetClar.getString("notes"));
                                                addText("puzzle:");
                                                addText(puzzle);
                                                addText("answer:");
                                                addText(answer.toString());
                                                if(answer.size() == 1) {
                                                    ans = null;
                                                    name_chars = answer.get(0).toCharArray();
                                                    puzzle_chars = puzzle.toCharArray();
                                                    for (int j = 0; j < name_chars.length; j++) {
                                                        if (name_chars[j] != puzzle_chars[j])
                                                            ans = name_chars[j] + "";
                                                    }
                                                    Log.w("TestInfo", "Due to TEST, return now!");
                                                    if (ans != null){
                                                        if(uims.post_pingjiao_tijiao(list.get(i), ans)){
                                                            addText("【评教成功！】");
                                                        }
                                                    }
                                                }
                                            }

                                            addText("【评教已完成！】");
                                        }
                                        else{
                                            addText("未登录！");
                                        }
                                    }
                                }, "教学质量评价--登录", "一键评教！");
                                window.setFocusable(true);
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                window.showAtLocation(PingjiaoActivity.this.findViewById(R.id.activity_pingjiao_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                                popupWindow = window;
                                Alerter.hide();
                            }
                        })
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .enableSwipeToDismiss()
                        .setDuration(Integer.MAX_VALUE)
                        .show();
            }
        });
    }
}