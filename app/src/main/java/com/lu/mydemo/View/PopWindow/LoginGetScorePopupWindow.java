package com.lu.mydemo.View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lu.mydemo.Activity.MainActivity;
import com.lu.mydemo.Activity.ScoreActivity;
import com.lu.mydemo.CJCX.CJCX;
import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.UIMS.UIMS_New;
import com.lu.mydemo.Utils.Score.ScoreConfig;
import com.lu.mydemo.Utils.StudentVPN.VPNClient;
import com.lu.mydemo.Utils.Thread.MyThreadController;
import com.tapadoo.alerter.Alerter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;

public class LoginGetScorePopupWindow extends PopupWindow {

    private View mMenuView;

    private EditText user;
    private EditText password;

    private View configLayout;
    private View vpnLayout;

    private CheckBox checkBox_UIMS;
    private CheckBox checkBox_CJCX;
    private CheckBox checkBox_VPN;

    private TextView useVPNTv;

    private Button commitButton;
    private Button cancelButton;
    private TextView deleteSavedText;

    private Activity context;

    private String userStr;
    private String passwordStr;

    private SharedPreferences sp;
    private UIMS uims;

    public static boolean loginSuccess = false;

    public LoginGetScorePopupWindow(final ScoreActivity context, int height, int width){
        this(context, height, width, null);
    }

    public LoginGetScorePopupWindow(final ScoreActivity context, int height, int width, final VPNClient vpnClient) {
        super(context);
        this.context = context;
        context.setPopUpWindow(this);
        sp = MainActivity.context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);//共用LoginActivity账户
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_login_get_score, null);

        user = mMenuView.findViewById(R.id.pop_window_login_get_score_id);
        password = mMenuView.findViewById(R.id.pop_window_login_get_score_password);

        configLayout = mMenuView.findViewById(R.id.pop_window_login_get_score_config_layout);
        vpnLayout = mMenuView.findViewById(R.id.pop_window_login_get_score_VPN_layout);

        checkBox_UIMS = mMenuView.findViewById(R.id.pop_window_login_get_score_UIMS_check_box);
        checkBox_CJCX = mMenuView.findViewById(R.id.pop_window_login_get_score_CJCX_check_box);
        checkBox_VPN = mMenuView.findViewById(R.id.pop_window_login_get_score_VPN_check_box);

        useVPNTv = mMenuView.findViewById(R.id.pop_window_login_get_score_use_vpn);

        commitButton = mMenuView.findViewById(R.id.pop_window_login_get_score_commit_button);
        cancelButton = mMenuView.findViewById(R.id.pop_window_login_get_score_cancel_button);
        deleteSavedText = mMenuView.findViewById(R.id.pop_window_login_get_score_delete_saved_text);

        changeTheme();

        user.setText(sp.getString("USER",""));
        password.setText(sp.getString("PASSWORD",""));

        checkBox_UIMS.setChecked(ScoreConfig.isIsUIMSEnable());
        checkBox_CJCX.setChecked(ScoreConfig.isIsCJCXEnable());

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.pop_window_login_get_score_CJCX_check_box : {
                        //需要通知CJCX, CJCX允许了
                        CJCX.setCJCXEnable(context.getApplicationContext(), buttonView.isChecked());
                        break;
                    }
                    case R.id.pop_window_login_get_score_UIMS_check_box : {
                        ScoreConfig.setUIMSEnable(context.getApplicationContext(), buttonView.isChecked());
                        break;
                    }
                }
            }
        };

        checkBox_UIMS.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox_CJCX.setOnCheckedChangeListener(onCheckedChangeListener);

        useVPNTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginVPNPopupWindow window = new LoginVPNPopupWindow(context, context.findViewById(R.id.activity_scrolling_layout).getHeight(), context.findViewById(R.id.activity_scrolling_layout).getWidth());
                window.setFocusable(true);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                context.dismissShowNewPopupWindow(window);
//                Toast.makeText(context, "该功能尚未完成，敬请期待！", Toast.LENGTH_SHORT).show();
            }
        });

        if(vpnClient != null){
            checkBox_VPN.setChecked(true);
            configLayout.setVisibility(View.GONE);
            useVPNTv.setVisibility(View.GONE);
            vpnLayout.setVisibility(View.VISIBLE);
            checkBox_VPN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    configLayout.setVisibility(View.VISIBLE);
                    useVPNTv.setVisibility(View.VISIBLE);
                    vpnLayout.setVisibility(View.GONE);
                }
            });
        }

        commitButton.setText("更新成绩");

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getText().length() != 8 || !(password.getText().length() > 0)) {
                    AlertCenter.showWarningAlert(context, "用户名或密码不符合规则", "请输入正确的用户名和密码！");
                    if (user.getText().length() != 8) user.setError("请输入8位教学号");
                    if (!(password.getText().length() > 0)) password.setError("请输入密码");
                    return;
                }
                AlertCenter.showLoading(context, "登录中，请稍候...");
                dealing("登录中，请稍候...");
                MyThreadController.commit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            userStr = user.getText().toString();
                            passwordStr = password.getText().toString();

                            sp.edit().putString("USER", userStr).apply();
                            sp.edit().putString("PASSWORD", passwordStr).apply();

                            if(checkBox_VPN.isChecked() && vpnClient != null && !TextUtils.isEmpty(vpnClient.getCookie())){
                                UIMS_New uims_new = new UIMS_New(vpnClient, userStr, passwordStr, true);
                                UIMS.setUser(userStr);
                                AlertCenter.showLoading(context, "正在连接到UIMS教务系统(使用学生VPN)...");
                                if(!uims_new.connectToUIMS()){
                                    AlertCenter.showWarningAlert(context, "未能连接到UIMS教务系统，请检查您的网络连接或稍后重试(也可能是学生VPN的问题).");
                                    dealFinish("重新登录");
                                    return;
                                }
                                AlertCenter.showLoading(context, "正在登录(使用学生VPN)...");
                                if(!uims_new.loginUIMS()){
                                    AlertCenter.showWarningAlert(context, "登录UIMS教务系统失败，请检查您的用户名/密码后重试.");
                                    dealFinish("重新登录");
                                    return;
                                }
                                if(!uims_new.getCurrentUserInfo()){
                                    AlertCenter.showWarningAlert(context, "获取用户信息失败，请检查您的用户名/密码后重试.");
                                    dealFinish("重新登录");
                                    return;
                                }
                                dealCurrentUserInfoJSON(uims_new);
                                UIMS.setCurrentUserInfoJSON(uims_new.getCurrentUserInfoJSON());
                                if(!uims_new.getTeachingTerm() || !uims_new.getCourseSelectType() || !uims_new.getScoreStatistics() || !uims_new.getRecentScore()){
                                    AlertCenter.showErrorAlertWithReportButton(context, "获取成绩失败，请稍后重试.", UIMS_New.getExceptionList(), UIMS.getUser());
                                    dealFinish("更新成绩");
                                    return;
                                }
                                UIMS.setTeachingTerm(uims_new.getmTeachingTermJSON());
                                UIMS.setCourseTypeJSON(uims_new.getCourseSelectTypeJSON());
                                UIMS.setCourseSelectTypeJSON(uims_new.getCourseSelectTypeJSON());
                                UIMS.setScoreStatisticsJSON(uims_new.getScoreStatisticsJSON());
                                dealScoreJSON(uims_new.getRecentScoreJSON(), uims_new);
                                MainActivity.saveVPNData();
                                AlertCenter.showAlert(context, "成绩刷新成功！");
                                context.reloadScoreList();
                                context.dismissPopupWindow();
                                return;
                            }

                            uims = new UIMS(userStr, passwordStr);
                            if(ScoreConfig.isIsUIMSEnable()){
                                AlertCenter.showLoading(context, "正在连接到UIMS教务系统...");
                                if (uims.connectToUIMS()) {
                                    AlertCenter.showLoading(context, "正在登录...");
                                    if (uims.login()) {
                                        if (uims.getCurrentUserInfo(false)) {
                                            uims.getScoreStatistics();
                                            uims.getRecentScore();
                                            MainActivity.saveScoreJSON();
                                            AlertCenter.showAlert(context, "成绩刷新成功！");
                                            context.reloadScoreList();
                                            context.dismissPopupWindow();
                                        }
                                        else{
//                                            AlertCenter.showWarningAlert(context, "获取信息失败！");
                                            AlertCenter.showErrorAlertWithReportButton(context, "获取信息出错，请稍后重试...", UIMS.getExceptions(), userStr);
                                            dealFinish("重新登录");
                                            return;
                                        }

                                    } else {
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Alerter.hide();
                                                AlertCenter.showWarningAlert(context, "", "登录失败，请检查用户名和密码是否正确.\n\n" +
                                                        "教务账号：\t您的教学号\n" +
                                                        "教务密码：\t默认密码为身份证号后六位");

                                                dealFinish("重新登录");
                                                return;
                                            }
                                        });
                                    }
                                } else {
                                    if(ScoreConfig.isIsCJCXEnable()) {
                                        if(ScoreConfig.isIsUIMSEnable())
                                            AlertCenter.showLoading(context, "连接UIMS失败！\n\n" +
                                                    "正在尝试校外(com.lu.mydemo.CJCX)查询，请稍候...");
                                        CJCX cjcx = new CJCX(uims.getUser(), uims.getPass());
                                        if (cjcx.login()) {
                                            if(UIMS.getTermId_termName() == null || ! (UIMS.getTermId_termName().size() > 0)){
                                                if(cjcx.getTeachingTerm()){
                                                    ScoreActivity.saveCJCXTerm();
                                                }
                                            }
                                            if (cjcx.getScore()) {
                                                AlertCenter.showAlert(context, "CJCX查询成功！\n" +
                                                        "本次查询更新了 " + cjcx.getUpdate_count() + " 条成绩信息.");
                                                ScoreActivity.saveCJCXScore();
                                                context.reloadScoreList();
                                                context.dismissPopupWindow();
                                                return;
                                            }
                                        }
                                    }
                                    try {
                                        MainActivity.saveScoreJSON();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    context.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Alerter.hide();
                                            AlertCenter.showErrorAlert(context, "", "登录失败，请检查是否连接校园网！\n\n" +
                                                    "您可以连接JLU.NET或JLU.TEST;\n" +
                                                    "若您未开通校园网，可以考虑连接JLU.PC，此时无需登录到网络，完成“信息更新”后即可断开，切回流量.\n\n" +
                                                    "若您在校外，请在设置中勾选\"启用校外查询(com.lu.mydemo.CJCX)\"\n" +
                                                    "如有问题，请在\"关于\"页反馈.");
                                            dealFinish("重新登录");
                                            return;
                                        }
                                    });
                                }
                            }
                            else if(ScoreConfig.isIsCJCXEnable()){
                                CJCX cjcx = new CJCX(UIMS.getUser(), UIMS.getPass());
                                if (cjcx.login()) {
                                    if(UIMS.getTermId_termName() == null || ! (UIMS.getTermId_termName().size() > 0)){
                                        if(cjcx.getTeachingTerm()){
                                            ScoreActivity.saveCJCXTerm();
                                        }
                                    }
                                    if (cjcx.getScore()) {
                                        AlertCenter.showAlert(context, "CJCX查询成功！\n" +
                                                "本次查询更新了 " + cjcx.getUpdate_count() + " 条成绩信息.");
                                        ScoreActivity.saveCJCXScore();
                                        context.reloadScoreList();
                                        context.dismissPopupWindow();
                                        return;
                                    }
                                    else {
                                        AlertCenter.showErrorAlert(context, "查询失败，请稍后重试.");
                                        dealFinish("更新成绩");
                                    }
                                }
                                else {
                                    AlertCenter.showErrorAlert(context, "登陆失败，请检查用户名/密码是否正确或稍后重试.");
                                    dealFinish("更新成绩");
                                }
                            }
                            else {
                                AlertCenter.showErrorAlert(context, "", "哼！\n" +
                                        "都不选，能查到才怪哦\n" +
                                        "  ￣へ￣  ");
                                dealFinish("更新成绩");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
//                            AlertCenter.showWarningAlert(context, "Error", e.getMessage());
                            AlertCenter.showErrorAlertWithReportButton(context, "抱歉,出现错误.", e, UIMS.getUser());
                        }
                    }
                });
            }
        });

        //取消按钮
        cancelButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });

        deleteSavedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().remove("USER").apply();
                sp.edit().remove("PASSWORD").apply();
                AlertCenter.showAlert(context, "已删除账号信息.");
                dismiss();
            }
        });

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(width);
        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(height);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationActivity);
        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(ColorManager.getPopwindow_background_color());
        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setColors(ColorManager.getPopupWindowBackground());
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        this.setBackgroundDrawable(drawable);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_window_login_get_score_pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    private void dealing(final String commitButtonText){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commitButton.setText(commitButtonText);
                commitButton.setEnabled(false);
                commitButton.setBackground(ColorManager.getInternetInformationButtonBackground_disable_full());
            }
        });
    }

    private void dealFinish(final String commitButtonText){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commitButton.setText(commitButtonText);
                commitButton.setEnabled(true);
                commitButton.setBackground(ColorManager.getInternetInformationButtonBackground_full());
            }
        });
    }

    private void dealCurrentUserInfoJSON(UIMS_New uims_new){
        JSONObject studentJSON = new JSONObject();
        JSONObject defRes;
        try {
            defRes = uims_new.getCurrentUserInfoJSON().getJSONObject("defRes");
            uims_new.getClassStudentCount();
            defRes.put("studCnt", uims_new.getClassStudentNumber());
            studentJSON.put("adminClass", defRes);
            studentJSON.put("egrade", uims_new.getStudentId().substring(2, 4));
            UIMS.setStudentJSON(studentJSON);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dealScoreJSON(JSONObject scoreJSON, UIMS_New uims_new){
        HashMap<String, JSONObject> id_scorePercent = UIMS.getId_scorePercent();

        JSONArray scores;
        try{
            scores = scoreJSON.getJSONArray("value");
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        int i = 0;
        net.sf.json.JSONObject percentJSON;
        while (true) {
            try {
                net.sf.json.JSONObject temp = scores.getJSONObject(i);
                net.sf.json.JSONObject temp2 = temp.getJSONObject("course");

                String asId = temp.getString("asId");
                if (id_scorePercent.containsKey(asId)) {
                    percentJSON = id_scorePercent.get(asId);
                }
                else {
                    percentJSON = uims_new.getScorePercent(asId);
                }
                assert percentJSON != null;
                percentJSON.put("courName", temp2.getString("courName"));
                temp.put("percent", percentJSON);

                temp.remove("student");

                i++;
            } catch (Exception e) {
                break;
            }
        }
        UIMS.setScoreJSON(scoreJSON);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getWindow().peekDecorView();
        if(view != null && inputMethodManager != null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void changeTheme(){
        mMenuView.findViewById(R.id.pop_window_login_get_score_pop_layout).setBackground(ColorManager.getMainBackground_with_top_redius());
        commitButton.setBackground(ColorManager.getInternetInformationButtonBackground_full());
        user.setBackground(ColorManager.getSpinnerBackground_full());
        password.setBackground(ColorManager.getSpinnerBackground_full());
    }

}
