package com.lu.mydemo.View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lu.mydemo.Activity.MainActivity;
import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.Activity.PingjiaoActivity;
import com.lu.mydemo.R;
import com.lu.mydemo.ToolFor2045_Site.InformationUploader;
import com.lu.mydemo.UIMS.UIMS_New;
import com.lu.mydemo.Utils.StudentVPN.VPNClient;
import com.tapadoo.alerter.Alerter;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.Utils.Thread.MyThreadController;

import net.sf.json.JSONObject;

public class LoginPingjiaoPopupWindow extends PopupWindow {

    private View mMenuView;

    private View mVerifyCodeLayout;
    private ImageView mVerifyCodeIv;

    private EditText user;
    private EditText password;
    private EditText verifyCode;

    private View vpnLayout;

    private CheckBox checkBox_VPN;

    private TextView useVPNTv;

    private TextView titleTextView;
    private Button commitButton;
    private Button cancelButton;
    private TextView deleteSavedText;

    private Activity context;

    private String userStr;
    private String passwordStr;

    private SharedPreferences sp;
    private UIMS_New uims_new;
    private UIMS uims;

    public static boolean loginSuccess = false;

    public LoginPingjiaoPopupWindow(final PingjiaoActivity context, int height, int width,
                                    final View.OnClickListener listener, String title,
                                    String commit_text, final VPNClient vpnClient) {
        super(context);
        this.context = context;
        sp = MainActivity.context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);//共用LoginActivity账户
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_login, null);

        mVerifyCodeLayout = mMenuView.findViewById(R.id.pop_window_login_verify_code_layout);
        mVerifyCodeIv = mMenuView.findViewById(R.id.pop_window_login_verify_code_iv);

        user = mMenuView.findViewById(R.id.pop_window_login_id);
        password = mMenuView.findViewById(R.id.pop_window_login_password);
        verifyCode = mMenuView.findViewById(R.id.pop_window_login_verify_code_edit_text);

        vpnLayout = mMenuView.findViewById(R.id.pop_window_login_get_score_VPN_layout);

        checkBox_VPN = mMenuView.findViewById(R.id.pop_window_login_get_score_VPN_check_box);

        useVPNTv = mMenuView.findViewById(R.id.pop_window_login_use_student_vpn_login);

        titleTextView = mMenuView.findViewById(R.id.pop_window_login_pop_layout_title);
        commitButton = mMenuView.findViewById(R.id.pop_window_login_commit_button);
        cancelButton = mMenuView.findViewById(R.id.pop_window_login_cancel_button);
        deleteSavedText = mMenuView.findViewById(R.id.pop_window_login_delete_saved_text);

        changeTheme();

        user.setText(sp.getString("USER", ""));
        password.setText(sp.getString("PASSWORD", ""));

        useVPNTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.dismissPopupWindow();
                LoginVPNPopupWindow window = new LoginVPNPopupWindow(context,
                        context.findViewById(R.id.activity_pingjiao_layout).getHeight(),
                        context.findViewById(R.id.activity_pingjiao_layout).getWidth(), context);
                window.setFocusable(true);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                context.showPopupWindow(window);
            }
        });

        if (vpnClient != null) {
            checkBox_VPN.setChecked(true);
            mVerifyCodeLayout.setVisibility(View.VISIBLE);
            useVPNTv.setVisibility(View.GONE);
            vpnLayout.setVisibility(View.VISIBLE);
            checkBox_VPN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    useVPNTv.setVisibility(View.VISIBLE);
                    vpnLayout.setVisibility(View.GONE);
                }
            });
            userStr = user.getText().toString();
            passwordStr = password.getText().toString();
            uims_new = new UIMS_New(vpnClient, userStr, passwordStr, true);
            UIMS.setUser(userStr);
            MyThreadController.commit(new Runnable() {
                @Override
                public void run() {
                    byte[] bytes = uims_new.getVerifyCode();
                    try {
                        String code = InformationUploader.getVerifyCodeStr(bytes);
                        code = JSONObject.fromObject(code).getString("data");
                        setVerifyCode(bytes, code);
                    } catch (Exception e) {
                        e.printStackTrace();
                        setVerifyCode(bytes);
                    }
                }
            });
        } else {
            useVPNTv.setVisibility(View.VISIBLE);
        }

        mVerifyCodeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vpnClient == null) {
                    AlertCenter.showWarningAlert(context, "暂未测试的功能",
                            "校内使用暂时无法测试，如您有任何疑问，请通过【设置 - 右上角“关于” - 联系开发者】与我联系.");
                    return;
                }
                MyThreadController.commit(new Runnable() {
                    @Override
                    public void run() {
                        byte[] bytes = uims_new.getVerifyCode();
                        try {
                            String code = InformationUploader.getVerifyCodeStr(bytes);
                            code = JSONObject.fromObject(code).getString("data");
                            setVerifyCode(bytes, code);
                        } catch (Exception e) {
                            e.printStackTrace();
                            setVerifyCode(bytes);
                        }
                    }
                });
            }
        });

        titleTextView.setText(title);
        commitButton.setText(commit_text);

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getText().length() != 8 || !(password.getText().length() > 0)) {
                    AlertCenter.showWarningAlert(context, "用户名或密码不符合规则", "请输入正确的用户名和密码！");
                    if (user.getText().length() != 8) user.setError("请输入8位教学号");
                    if (!(password.getText().length() > 0)) password.setError("请输入密码");
                    if(TextUtils.isEmpty(verifyCode.getText())) verifyCode.setError("请输入验证码");
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

                            Log.i("LoginPop", "USER:\t" + userStr);
                            Log.i("LoginPop", "PASS:\t" + passwordStr);


                            if (checkBox_VPN.isChecked() && vpnClient != null && !TextUtils.isEmpty(vpnClient.getCookie())) {
                                AlertCenter.showLoading(context, "正在连接到UIMS教务系统(使用学生VPN)...");

                                if (uims_new == null) {
                                    throw new IllegalAccessException("UIMS_NEW is NULL!");
                                }

                                if (!uims_new.connectToUIMS()) {
                                    AlertCenter.showWarningAlert(context, "未能连接到UIMS教务系统，请检查您的网络连接或稍后重试(也可能是学生VPN的问题).");
                                    dealFinish("重新登录");
                                    return;
                                }
                                AlertCenter.showLoading(context, "正在登录(使用学生VPN)...");
                                if (!uims_new.loginUIMS(verifyCode.getText().toString())) {
                                    AlertCenter.showWarningAlert(context, "登录UIMS教务系统失败，请检查您的用户名/密码后重试.");
                                    dealFinish("重新登录");
                                    return;
                                }
                                if (!uims_new.getCurrentUserInfo()) {
                                    AlertCenter.showWarningAlert(context, "获取用户信息失败，请检查您的用户名/密码后重试.");
                                    dealFinish("重新登录");
                                    return;
                                }
                                context.addText("登录成功！");
                                context.setUseStudentVpn(true);
                                context.setUims_new(uims_new);
                                listener.onClick(null);
                                return;
                            } else {
                                uims = new UIMS(userStr, passwordStr);
                                AlertCenter.showLoading(context, "正在连接到UIMS教务系统...");

                                if (uims.connectToUIMS()) {
                                    AlertCenter.showLoading(context, "正在登录...");
                                    if (uims.login()) {
                                        if (uims.getCurrentUserInfo(false)) {
                                            MainActivity.saveScoreJSON();
                                            AlertCenter.showAlert(context, "登录成功！");
                                            context.setLogin(true);
                                            context.setUims(uims);
                                            context.addText("登录成功！");
                                            context.addText("欢迎您, " + uims.getNickName() + " .");
                                            listener.onClick(null);
                                            context.dismissPopupWindow();
                                        } else {
//                                        AlertCenter.showWarningAlert(context, "获取信息失败！");
                                            AlertCenter.showErrorAlertWithReportButton(context, "获取信息出错，请稍后重试...", UIMS.getExceptions(), userStr);
                                            dealFinish("重新登录");
                                            return;
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
                                                    "若您未开通校园网，可以考虑连接JLU.PC，此时无需登录即可使用。");
                                            dealFinish("重新登录");
                                            return;
                                        }
                                    });
                                }
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
        drawable.setColors(ColorManager.getPopupWindowBackground());
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        this.setBackgroundDrawable(drawable);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_window_login_pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    private void setVerifyCode(final byte[] bytes) {
        setVerifyCode(bytes, null);
    }

    private void setVerifyCode(final byte[] bytes, final String code) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(context).load(bytes).into(mVerifyCodeIv);
                if (code.length() == 4) {
                    try {
                        Integer.parseInt(code);
                        verifyCode.setText(code);
                    } catch (Exception e) {
                        e.printStackTrace();
                        verifyCode.setText("");
                    }
                } else {
                    verifyCode.setText("");
                }
            }
        });
    }

    private void dealing(final String commitButtonText) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commitButton.setText(commitButtonText);
                commitButton.setEnabled(false);
                commitButton.setBackground(ColorManager.getInternetInformationButtonBackground_disable_full());
            }
        });
    }

    private void dealFinish(final String commitButtonText) {
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
        if (view != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void changeTheme() {
        mMenuView.findViewById(R.id.pop_window_login_pop_layout).setBackground(ColorManager.getMainBackground_with_top_redius());
        commitButton.setBackground(ColorManager.getInternetInformationButtonBackground_full());
        user.setBackground(ColorManager.getSpinnerBackground_full());
        password.setBackground(ColorManager.getSpinnerBackground_full());
    }

}
