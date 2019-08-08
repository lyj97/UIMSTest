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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lu.mydemo.MainActivity;
import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;
import com.tapadoo.alerter.Alerter;

import Config.ColorManager;
import UIMS.UIMS;

public class LoginPopWindow extends PopupWindow {

    private View mMenuView;

    private EditText user;
    private EditText password;

    private Button commitButton;
    private Button cancelButton;
    private TextView deleteSavedText;

    private Activity context;

    private String userStr;
    private String passwordStr;

    private SharedPreferences sp;
    private UIMS uims;

    public static boolean loginSuccess = false;

    public LoginPopWindow(final MainActivity context, int height, int width) {
        super(context);
        this.context = context;
        sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_login, null);

        user = mMenuView.findViewById(R.id.pop_window_login_id);
        password = mMenuView.findViewById(R.id.pop_window_login_password);

        commitButton = mMenuView.findViewById(R.id.pop_window_login_commit_button);
        cancelButton = mMenuView.findViewById(R.id.pop_window_login_cancel_button);
        deleteSavedText = mMenuView.findViewById(R.id.pop_window_login_delete_saved_text);

        changeTheme();

        user.setText(sp.getString("USER",""));
        password.setText(sp.getString("PASSWORD",""));

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

                            if (userStr == null || passwordStr == null || userStr.length() != 8 || !(passwordStr.length() > 0)) {
                                sp.edit().remove("USER").apply();
                                sp.edit().remove("PASSWORD").apply();
                                AlertCenter.showWarningAlert(context, "获取已保存账号出错，请输入正确的用户名和密码后重新登录.");
                                return;
                            }

                            uims = new UIMS(userStr, passwordStr);
                            AlertCenter.showLoading(context, "正在连接到UIMS教务系统...");
                            if (uims.connectToUIMS()) {
                                AlertCenter.showLoading(context, "正在登录...");
                                if (uims.login()) {
//                            showLoading("正在加载用户信息...");
                                    if (uims.getCurrentUserInfo()) {
//                                showAlert("", "欢迎您, " + uims.getNickName() + " ." + "\n" +
//                                        "您是UIMS系统第 " + uims.getLoginCounter() + " 位访问者.");
                                        if (uims.getUserInformation())
                                            if (uims.getTermArray()) {
                                                context.loginSuccess();

                                                context.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dealing("查询中，请稍候...");
                                                    }
                                                });

                                                AlertCenter.showLoading(context, "查询中...");
//                    showLoading("查询成绩统计...");
                                                if (uims.getScoreStatistics()) {
//                        showResponse("查询成绩统计成功！");
//                        showLoading("查询成绩...");
                                                    if (uims.getRecentScore()) {
//                            showResponse("查询成绩成功！");
//                            showAlert("", "查询成绩成功！");
                                                        Log.i("Login", "getRecentScoreSucceed!");
//                            loadSuccess();
                                                    }
                                                    else{
                                                        AlertCenter.showWarningAlert(context, "获取信息出错，请稍后重试...");
                                                        dealFinish("登录并更新信息");
                                                        return;
                                                    }
//                        showLoading("查询课程中...");
                                                    if(uims.getCourseSchedule()) {
//                            showResponse("查询课程成功！");
//                            showAlert("", "查询课程成功！");
                                                        Log.i("Login", "getCourseScheduleSucceed!");

                                                        dealFinish("登录并更新信息");

                                                        context.loadSuccess();
                                                        context.dismissPopWindow();
                                                    }
                                                    else{
                                                        AlertCenter.showWarningAlert(context, "获取信息出错，请稍后重试...");
                                                        dealFinish("登录并更新信息");
                                                        context.reLogin();
                                                        return;
                                                    }
                                                }
                                                else{
                                                    AlertCenter.showWarningAlert(context, "获取信息出错，请稍后重试...");
                                                    dealFinish("登录并更新信息");
                                                    context.reLogin();
                                                    return;
                                                }

                                            }
                                            else {
                                                AlertCenter.showWarningAlert(context, "获取信息出错，请稍后重试...");
                                                dealFinish("登录并更新信息");
                                            }
                                        else {
                                            AlertCenter.showWarningAlert(context, "获取信息出错，请稍后重试...");
                                            dealFinish("登录并更新信息");
                                        }
                                    } else {
                                        AlertCenter.showWarningAlert(context, "获取信息出错，请稍后重试...");
                                        dealFinish("登录并更新信息");
                                    }
                                } else {
//                                showResponse("Login failed!");
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
//                            showResponse("Login failed!");
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alerter.hide();
                                        AlertCenter.showWarningAlert(context, "", "登录失败，请检查是否连接校园网！\n\n" +
                                                "您可以连接JLU.NET或JLU.TEST;\n" +
                                                "若您未开通校园网，可以考虑连接JLU.PC，此时无需登录到网络，完成“信息更新”后即可断开，切回流量。");
                                        dealFinish("重新登录");
                                        return;
                                    }
                                });
                            }
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
//        this.setHeight(1500);
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

                int height = mMenuView.findViewById(R.id.pop_window_login_pop_layout).getTop();
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
        mMenuView.findViewById(R.id.pop_window_login_pop_layout_title).setBackgroundColor(ColorManager.getPrimaryColor());
        mMenuView.findViewById(R.id.pop_window_login_pop_layout_main_information).setBackground(ColorManager.getMainBackground());
        commitButton.setBackground(ColorManager.getInternetInformationButtonBackground_full());
        user.setBackground(ColorManager.getSpinnerBackground_full());
        password.setBackground(ColorManager.getSpinnerBackground_full());
    }

}
