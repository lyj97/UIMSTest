package com.lu.mydemo.View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.lu.mydemo.R;
import com.lu.mydemo.Activity.WebViewActivity;

import com.lu.mydemo.Config.ColorManager;

public class DownloadFileConfirmPopupWindow extends PopupWindow {

    private View mMenuView;

    private EditText filename;
    private EditText extension;

    private Button cancel_button;
    private Button commit_button;

    public Animation mExitAnim;//退出动画
    public Animation mEnterAnim;//进入动画

    private Activity context;

    public DownloadFileConfirmPopupWindow(final WebViewActivity context, String filename, String extension, int height, int width) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_download_file, null);

        this.filename = mMenuView.findViewById(R.id.pop_window_download_file_filename);
        this.extension = mMenuView.findViewById(R.id.pop_window_download_file_extension);

        this.cancel_button = mMenuView.findViewById(R.id.pop_window_download_file_cancel_button);
        this.commit_button = mMenuView.findViewById(R.id.pop_window_download_file_commit_button);

        changeTheme();

        this.filename.setText(filename);
        this.extension.setText(extension);

        //确认按钮
        commit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                context.setFileName(DownloadFileConfirmPopupWindow.this.filename.getText().toString() + DownloadFileConfirmPopupWindow.this.extension.getText().toString());
                context.commitDownload();
                //销毁弹出框
                dismiss();
            }
        });

        //取消按钮
        cancel_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                context.cancelDownload();
                //销毁弹出框
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

                int height = mMenuView.findViewById(R.id.pop_window_download_file_pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()== MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    private void changeTheme(){
        mMenuView.findViewById(R.id.pop_window_download_file_pop_layout).setBackground(ColorManager.getMainBackground_with_top_redius());
        commit_button.setBackground(ColorManager.getInternetInformationButtonBackground());
    }

}
