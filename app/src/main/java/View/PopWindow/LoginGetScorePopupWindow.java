package View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lu.mydemo.MainActivity;
import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.ScoreActivity;
import com.lu.mydemo.R;
import com.tapadoo.alerter.Alerter;

import CJCX.CJCX;
import Config.ColorManager;
import UIMS.UIMS;
import Utils.Score.ScoreConfig;

public class LoginGetScorePopupWindow extends PopupWindow {

    private View mMenuView;

    private EditText user;
    private EditText password;

    private CheckBox checkBox_UIMS;
    private CheckBox checkBox_CJCX;

    private Button commitButton;
    private Button cancelButton;
    private TextView deleteSavedText;

    private Activity context;

    private String userStr;
    private String passwordStr;

    private SharedPreferences sp;
    private UIMS uims;

    public static boolean loginSuccess = false;

    public LoginGetScorePopupWindow(final ScoreActivity context, int height, int width) {
        super(context);
        this.context = context;
        sp = MainActivity.context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);//共用LoginActivity账户
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_login_get_score, null);

        user = mMenuView.findViewById(R.id.pop_window_login_get_score_id);
        password = mMenuView.findViewById(R.id.pop_window_login_get_score_password);

        checkBox_UIMS = mMenuView.findViewById(R.id.pop_window_login_get_score_UIMS_check_box);
        checkBox_CJCX = mMenuView.findViewById(R.id.pop_window_login_get_score_CJCX_check_box);

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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            userStr = user.getText().toString();
                            passwordStr = password.getText().toString();

                            sp.edit().putString("USER", userStr).apply();
                            sp.edit().putString("PASSWORD", passwordStr).apply();

                            Log.i("LoginPop", "USER:\t" + userStr);
                            Log.i("LoginPop", "PASS:\t" + passwordStr);

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
                                            context.dismissGetScorePopWindow();
                                        }
                                        else{
                                            AlertCenter.showWarningAlert(context, "获取信息失败！");
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
                                                    "正在尝试校外(CJCX)查询，请稍候...");
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
                                                context.dismissGetScorePopWindow();
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
                                                    "若您在校外，请在设置中勾选\"启用校外查询(CJCX)\"\n" +
                                                    "如有问题，请在\"关于\"页反馈.");
                                            dealFinish("重新登录");
                                            return;
                                        }
                                    });
                                }
                            }
                            else if(ScoreConfig.isIsCJCXEnable()){
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
                                        context.dismissGetScorePopWindow();
                                        return;
                                    }
                                }
                            }
                            else {
                                AlertCenter.showErrorAlert(context, "", "哼！\n" +
                                        "都不选，能查到才怪哦\n" +
                                        "  ￣へ￣  ");
                                dealFinish("更新成绩");
                            }
//                            context.showLoading("正在连接到UIMS教务系统...");
//                            if (uims.connectToUIMS()) {
//                                context.showLoading("正在登录...");
//                                if (uims.login()) {
//                                    if (uims.getCurrentUserInfo(false)) {
//                                        uims.getScoreStatistics();
//                                        uims.getRecentScore();
//                                        if(ScoreConfig.isIsCJCXEnable()) {
//                                            CJCX cjcx = new CJCX(uims.getUser(), uims.getPass());
//                                            if (cjcx.login()) {
//                                                if (cjcx.getScore()) {
//                                                    context.showAlert("CJCX查询成功！");
//                                                    ScoreActivity.saveCJCXScore();
//                                                }
//                                            }
//                                        }
//                                        MainActivity.saveScoreJSON();
//                                        context.showAlert("成绩刷新成功！");
//                                        context.reloadScoreList();
//                                        context.dismissGetScorePopWindow();
//                                    }
//                                    else{
//                                        context.showWarningAlert("获取信息失败！");
//                                        dealFinish("重新登录");
//                                        return;
//                                    }
//
//                                } else {
////                                showResponse("Login failed!");
//                                    context.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Alerter.hide();
//                                            context.showWarningAlert("", "登录失败，请检查用户名和密码是否正确.\n\n" +
//                                                    "教务账号：\t您的教学号\n" +
//                                                    "教务密码：\t默认密码为身份证号后六位");
//
//                                            dealFinish("重新登录");
//
//                                            return;
//                                        }
//                                    });
//                                }
//                            } else {
////                            showResponse("Login failed!");
//                                if(ScoreConfig.isIsCJCXEnable()) {
//                                    if(ScoreConfig.isIsUIMSEnable())
//                                        context.showLoading("连接UIMS失败！\n\n" +
//                                            "正在尝试校外(CJCX)查询，请稍候...");
//                                    CJCX cjcx = new CJCX(uims.getUser(), uims.getPass());
//                                    if (cjcx.login()) {
//                                        if(UIMS.getTermId_termName() == null || ! (UIMS.getTermId_termName().size() > 0)){
//                                            if(cjcx.getTeachingTerm()){
//                                                ScoreActivity.saveCJCXTerm();
//                                            }
//                                        }
//                                        if (cjcx.getScore()) {
//                                            context.showAlert("CJCX查询成功！\n" +
//                                                    "本次查询更新了 " + cjcx.getUpdate_count() + " 条成绩信息.");
//                                            ScoreActivity.saveCJCXScore();
//                                            context.reloadScoreList();
//                                            context.dismissGetScorePopWindow();
//                                            return;
//                                        }
//                                    }
//                                }
//                                MainActivity.saveScoreJSON();
//                                context.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Alerter.hide();
//                                        context.showErrorAlert("", "登录失败，请检查是否连接校园网！\n\n" +
//                                                "您可以连接JLU.NET或JLU.TEST;\n" +
//                                                "若您未开通校园网，可以考虑连接JLU.PC，此时无需登录到网络，完成“信息更新”后即可断开，切回流量.\n\n" +
//                                                "若您在校外，请在设置中勾选\"启用校外查询(CJCX)\"\n" +
//                                                "如有问题，请在\"关于\"页反馈.");
//                                        dealFinish("重新登录");
//                                        return;
//                                    }
//                                });
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertCenter.showWarningAlert(context, "Error", e.getMessage());
                        }
                    }
                }).start();
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
//        this.setHeight(1200);
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
        drawable.setColors(new int[]{context.getResources().getColor(R.color.color_no_color), context.getResources().getColor(R.color.color_no_color), ColorManager.getPopwindow_background_color(), context.getResources().getColor(R.color.color_grayBackground)});
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

    @Override
    public void dismiss() {
        super.dismiss();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getWindow().peekDecorView();
        if(view != null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void changeTheme(){
        Log.i("Theme", "Change theme.");
        mMenuView.findViewById(R.id.pop_window_login_get_score_pop_layout_title).setBackgroundColor(ColorManager.getPrimaryColor());
        mMenuView.findViewById(R.id.pop_window_login_get_score_pop_layout_main_information).setBackground(ColorManager.getMainBackground());
        commitButton.setBackground(ColorManager.getInternetInformationButtonBackground_full());
        user.setBackground(ColorManager.getSpinnerBackground_full());
        password.setBackground(ColorManager.getSpinnerBackground_full());
    }

}
