package com.lu.mydemo.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;
import com.lu.mydemo.UIMS.UIMSTest;
import com.lu.mydemo.UIMS.UIMS_New;
import com.lu.mydemo.Utils.StudentVPN.VPNClient;
import com.lu.mydemo.Utils.Thread.MyThreadController;
import com.tapadoo.alerter.Alerter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.View.PopWindow.LoginPingjiaoPopupWindow;

public class PingjiaoActivity extends BaseActivity {

    boolean isFirstEnter = true;

    TextView backText;
    TextView mainText;
    ScrollView mainTextLayout;
    double Ver = 0.3;

    UIMS uims;
    UIMS_New uims_new;
    boolean isLogin = false;
    boolean useStudentVpn = false;

    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pingjiao);

        backText = findViewById(R.id.activity_pingjiao_navigation_back_text);
        mainText = findViewById(R.id.activity_pingjiao_main_text);
        mainTextLayout = findViewById(R.id.activity_pingjiao_main_text_layout);

        mainText.setText("");

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeTheme();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirstEnter) {
            isFirstEnter = false;
            LoginPingjiaoPopupWindow window = new LoginPingjiaoPopupWindow(this,
                    findViewById(R.id.activity_pingjiao_layout).getHeight(),
                    findViewById(R.id.activity_pingjiao_layout).getWidth(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pingjiao();
                }
            }, "教学质量评价--登录", "一键评教！", null);
            window.showAtLocation(findViewById(R.id.activity_pingjiao_layout),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            setPopUpWindow(window);
            addText("评教测试版本：" + Ver);
        }
    }

    @Override
    public void loginVPNSuccess(VPNClient vpnClient) {
        dismissPopupWindow();
        LoginPingjiaoPopupWindow window = new LoginPingjiaoPopupWindow(this,
                findViewById(R.id.activity_pingjiao_layout).getHeight(),
                findViewById(R.id.activity_pingjiao_layout).getWidth(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pingjiao();
            }
        }, "教学质量评价--登录", "一键评教！", vpnClient);
        window.setFocusable(true);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        showPopupWindow(window);
        Alerter.hide();
    }

    public void addText(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainText.setText(mainText.getText() + "\n" + str);
                mainTextLayout.smoothScrollTo(0, mainText.getBottom());
            }
        });
    }

    public UIMS getUims() {
        return uims;
    }

    public void setUims(UIMS uims) {
        this.uims = uims;
    }

    public UIMS_New getUims_new() {
        return uims_new;
    }

    public void setUims_new(UIMS_New uims_new) {
        this.uims_new = uims_new;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        this.isLogin = login;
    }

    public void setUseStudentVpn(boolean useStudentVpn) {
        this.useStudentVpn = useStudentVpn;
    }

    @Override
    public void setPopUpWindow(PopupWindow popUpWindow) {
        this.popupWindow = popUpWindow;
    }

    public void dismissPopupWindow(){
        if(this.popupWindow == null){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }

    public void showPopupWindow(final PopupWindow window) {
        super.showPopupWindow(window);
        dismissPopupWindow();
        setPopUpWindow(window);
        window.showAtLocation(findViewById(R.id.activity_pingjiao_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void changeTheme() {
        super.changeTheme();
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

    private void pingjiao() {
        AlertCenter.hideAlert(this);
        dismissPopupWindow();
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                if (useStudentVpn) {
                    pingjiao_2020_1();
                } else {
                    pingjiao_old();
                }
            }
        });
    }

    private void pingjiao_old() {

        if (isLogin) {
            ArrayList<String> list = uims.post_pingjiao_information();
            if (list.size() == 0) {
                addText("【您已经完成评教！】");
                return;
            }
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

            try {
                HashSet<String> names = uims.getClassStudentName();
                System.out.println(names);
                System.out.println(list);
                addText("names:");
                addText(names.toString());
                for (int i = 0; i < list.size(); i++) {
                    pingjiao_inf = uims.post_pingjiao(list.get(i));
                    puzzle = pingjiao_inf.getString("puzzle");
                    parts = puzzle.split("_");
                    answer = UIMSTest.getAnswer(parts, names, puzzle.length());
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
                    if (answer.size() == 1) {
                        ans = null;
                        name_chars = answer.get(0).toCharArray();
                        puzzle_chars = puzzle.toCharArray();
                        for (int j = 0; j < name_chars.length; j++) {
                            if (name_chars[j] != puzzle_chars[j])
                                ans = name_chars[j] + "";
                        }
                        Log.w("TestInfo", "Due to TEST, return now!");
                        if (ans != null) {
                            if (uims.post_pingjiao_tijiao(list.get(i), ans)) {
                                addText("【评教成功！】");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                for (int i = 0; i < list.size(); i++) {
                    pingjiao_inf = uims.post_pingjiao(list.get(i));
                    targetClar = pingjiao_inf.getJSONObject("targetClar");
                    addText("person:");
                    addText(targetClar.getString("person"));
                    addText("notes:");
                    addText(targetClar.getString("notes"));
                    if (uims.post_pingjiao_tijiao(list.get(i), "")) {
                        addText("【评教成功！】");
                    }
                }
                addText("【评教可能已经完成，请返回后重新操作以确认！】");
                throw e;
            }

            addText("【评教已完成！】");
        } else {
            addText("未登录！");
        }
    }

    private void pingjiao_2020_1() {
        if (!useStudentVpn || uims_new == null) {
            addText("出现错误，请与开发者联系...");
            return;
        }

        ArrayList<String> list = uims_new.postPingjiaoInformation();
        if (list.size() == 0) {
            addText("【您已经完成评教！】");
            return;
        }
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

        try {
            HashSet<String> names = uims_new.getClassStudentName();
            System.out.println(names);
            System.out.println(list);
            addText("names:");
            addText(names.toString());
            for (int i = 0; i < list.size(); i++) {
                JSONObject object = uims_new.postPingjiao(list.get(i));
                JSONArray array = object.getJSONArray("items");
                pingjiao_inf = array.getJSONObject(0);
                targetClar = pingjiao_inf.getJSONObject("targetClar");
                JSONObject person = targetClar.getJSONObject("person");
                puzzle = pingjiao_inf.getString("puzzle");
                parts = puzzle.split("_");
                answer = UIMSTest.getAnswer(parts, names, puzzle.length());
                System.out.println(answer);
                targetClar = pingjiao_inf.getJSONObject("targetClar");
                addText("person:");
                addText(person.getString("name"));
                addText("notes:");
                addText(targetClar.getString("notes"));
                addText("puzzle:");
                addText(puzzle);
                addText("answer:");
                addText(answer.toString());
                for (int j = 0; j < answer.size(); j++) {
                    addText("所有答案:\t" + answer);
                    ans = null;
                    name_chars = answer.get(j).toCharArray();
                    puzzle_chars = puzzle.toCharArray();
                    for (int k = 0; k < name_chars.length; k++) {
                        if (name_chars[k] != puzzle_chars[k])
                            ans = name_chars[k] + "";
                    }
                    if (ans != null) {
                        addText("尝试的答案:\t" + ans);
                        if (uims_new.postPingjiaoCommit_2020_1(list.get(i), ans)) {
                            addText("【评教成功！】");
                            break;
                        } else {
                            addText("评教失败！");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addText(e.getMessage());
            try {
                for (int i = 0; i < list.size(); i++) {
                    JSONObject object = uims_new.postPingjiao(list.get(i));
                    JSONArray array = object.getJSONArray("items");
                    pingjiao_inf = array.getJSONObject(0);
                    targetClar = pingjiao_inf.getJSONObject("targetClar");
                    JSONObject person = targetClar.getJSONObject("person");
                    addText("person:");
                    addText(person.getString("name"));
                    addText("notes:");
                    addText(targetClar.getString("notes"));
                    if (uims_new.postPingjiaoCommit_2020_1(list.get(i), "")) {
                        addText("【评教成功！】");
                    } else {
                        addText("评教失败！");
                    }
                }
                addText("【评教可能已经完成，请返回后重新操作以确认！】");
            } catch (Exception e1){
                e1.printStackTrace();
                addText(e1.getMessage());
            }
        }

        addText("【评教已完成！】");
    }
}
