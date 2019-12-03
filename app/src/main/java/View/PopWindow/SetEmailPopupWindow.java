package View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;

import Config.ColorManager;
import ToolFor2045_Site.ExceptionReporter;
import Utils.Rom.EMUIUtils;
import Utils.Rom.MIUIUtils;
import Utils.String.FormatCheck;

/**
 * 创建时间: 2019/12/02 17:58 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class SetEmailPopupWindow extends PopupWindow {

    private View mMenuView;

    private EditText email_edit_text_view;
    private View email_layout;
    private Button commit_button;

    private Activity context;

    private SetEmailPopupWindow.OnDismissListener mOnDismissListener;

    public SetEmailPopupWindow(final Activity context, SetEmailPopupWindow.OnDismissListener onDismissListener, int height, int width){
        super(context);
        this.context = context;
        this.mOnDismissListener = onDismissListener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_set_email, null);
        email_edit_text_view = mMenuView.findViewById(R.id.set_email_pop_window_email);
        email_layout = mMenuView.findViewById(R.id.set_email_pop_window_email_layout);
        commit_button = mMenuView.findViewById(R.id.set_email_pop_window_commit_button);

        if(MIUIUtils.isMIUI()){
            email_edit_text_view.setVisibility(View.GONE);
            email_layout.setVisibility(View.VISIBLE);

            final EditText part_1_edit_text = email_layout.findViewById(R.id.set_email_pop_window_email_part_1);
            final EditText part_2_edit_text = email_layout.findViewById(R.id.set_email_pop_window_email_part_2);

            String[] mail_parts = ExceptionReporter.USER_MAIL.split("@");
            if(mail_parts.length >= 2){
                part_1_edit_text.setText(mail_parts[0]);
                part_2_edit_text.setText(mail_parts[1]);
            }

            commit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(part_1_edit_text.getText()) || TextUtils.isEmpty(part_2_edit_text.getText()) || ! FormatCheck.isEmail(part_1_edit_text.getText() + "@" + part_2_edit_text.getText())){
                        AlertCenter.showWarningAlert(context, "请输入正确的Email地址");
                    }
                    else  {
                        ExceptionReporter.setUserMail(part_1_edit_text.getText() + "@" + part_2_edit_text.getText());
                        dismiss();
                    }
                }
            });
        }
        else {
            email_edit_text_view.setText(ExceptionReporter.USER_MAIL);
            commit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FormatCheck.isEmail(email_edit_text_view.getText().toString())) {
                        ExceptionReporter.setUserMail(email_edit_text_view.getText().toString());
                        dismiss();
                    } else {
                        email_edit_text_view.setError("请输入正确的Email地址");
                        AlertCenter.showWarningAlert(context, "请输入正确的Email地址");
                    }
                }
            });
        }

        changeTheme();

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

                int height = mMenuView.findViewById(R.id.set_email_pop_window_main_layout).getTop();
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

    @Override
    public void dismiss() {
        if(mOnDismissListener != null){
            mOnDismissListener.onDismiss();
        }
        super.dismiss();
    }

    private void changeTheme(){
        mMenuView.findViewById(R.id.set_email_pop_window_main_layout).setBackground(ColorManager.getMainBackground_with_top_redius());
        email_edit_text_view.setBackground(ColorManager.getSpinnerBackground_full());
        commit_button.setBackground(ColorManager.getInternetInformationButtonBackground_full());
    }

    public interface OnDismissListener{
        void onDismiss();
    }

}
