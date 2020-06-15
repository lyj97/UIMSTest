package com.lu.mydemo.View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lu.mydemo.Activity.BaseActivity;
import com.lu.mydemo.Activity.ScoreActivity;
import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.Utils.StudentVPN.VPNClient;
import com.lu.mydemo.Utils.Thread.MyThreadController;

/**
 * 创建时间: 2020/01/16 17:14 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class LoginVPNPopupWindow extends PopupWindow {

    private View mMenuView;

    private TextView mTitleTv;

    private EditText user;
    private EditText password;

    private Button commitButton;
    private Button cancelButton;
    private TextView deleteSavedText;

    private Activity context;
    private LoginVPNListener mLoginListener;

    public LoginVPNPopupWindow(final BaseActivity context, int height, int width, LoginVPNListener listener) {
        super(context);
        this.context = context;
        this.mLoginListener = listener;
        context.setPopUpWindow(this);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        mMenuView = inflater.inflate(R.layout.pop_window_login, null);

        mTitleTv = mMenuView.findViewById(R.id.pop_window_login_pop_layout_title);

        user = mMenuView.findViewById(R.id.pop_window_login_id);
        password = mMenuView.findViewById(R.id.pop_window_login_password);

        commitButton = mMenuView.findViewById(R.id.pop_window_login_commit_button);
        cancelButton = mMenuView.findViewById(R.id.pop_window_login_cancel_button);
        deleteSavedText = mMenuView.findViewById(R.id.pop_window_login_delete_saved_text);

        changeTheme();

        VPNClient vpnClient = VPNClient.getInstance(context.getApplicationContext());
        if(vpnClient != null) {
            try {
                user.setText(vpnClient.getUser());
                password.setText(vpnClient.getPass());
            } catch (Exception e) {
                e.printStackTrace();
                AlertCenter.showErrorAlertWithReportButton(context, "抱歉，出现问题.", e, UIMS.getUser());
            }
        }
        else {
            AlertCenter.showErrorAlert(context, "抱歉，出现问题.");
            return;
        }

        String userHintStr = "学生邮箱前缀";
        String passHintStr = "学生邮箱密码";

        final String commitText = "登录学生VPN";

        mTitleTv.setText(commitText);

        user.setInputType(InputType.TYPE_CLASS_TEXT);
        user.setHint(userHintStr);
        password.setHint(passHintStr);

        commitButton.setText(commitText);

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThreadController.commit(new Runnable() {
                    @Override
                    public void run() {
                        dealing("登录中...");
                        VPNClient.setUserData(context.getApplicationContext(), user.getText().toString(), password.getText().toString());
                        VPNClient vpnClient = VPNClient.getInstance(context);
                        try {
                            assert vpnClient != null;
                            int result = vpnClient.startLogin();
                            switch (result){
                                case 0:{
                                    dealFinish(commitText);
                                    AlertCenter.showAlert(context, "登录学生VPN成功.");
                                    loginVPNSuccess(vpnClient);
                                    break;
                                }
                                case -1:{
                                    AlertCenter.showWarningAlert(context, "账号/密码不符合规则，请检查！");
                                    dealFinish(commitText);
                                    break;
                                }
                                case -2:{
                                    AlertCenter.showWarningAlert(context, "登录失败，请检查您的网络连接后重试.");
                                    dealFinish(commitText);
                                    break;
                                }
                                case -3:{

                                }
                                case -4:{
                                    AlertCenter.showErrorAlert(context, "登录失败，请检查您的用户名或密码是否正确.");
                                    dealFinish(commitText);
                                    break;
                                }
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            AlertCenter.showErrorAlertWithReportButton(context, "抱歉，出现问题.", e, UIMS.getUser());
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
                VPNClient.setUserData(context.getApplicationContext(), "", "");
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

    private void loginVPNSuccess(final VPNClient vpnClient){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoginListener.loginVPNSuccess(vpnClient);
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
        if(view != null && inputMethodManager != null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void changeTheme(){
        mMenuView.findViewById(R.id.pop_window_login_pop_layout).setBackground(ColorManager.getMainBackground_with_top_redius());
        commitButton.setBackground(ColorManager.getInternetInformationButtonBackground_full());
        user.setBackground(ColorManager.getSpinnerBackground_full());
        password.setBackground(ColorManager.getSpinnerBackground_full());
    }

    public interface LoginVPNListener{
        void loginVPNSuccess(final VPNClient vpnClient);
    }

}
