package com.lu.mydemo.View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lu.mydemo.R;

import net.sf.json.JSONObject;

import com.lu.mydemo.Config.ColorManager;

public class InternetInformationPopupWindow extends PopupWindow {

    private View mMenuView;

    private TextView information_title;
    private TextView information_main;
    private TextView information_link;

    private TextView confirm_text;

    public Animation mExitAnim;//退出动画
    public Animation mEnterAnim;//进入动画

    private Activity context;

    /**
     *
     * @param context
     * @param jsonObject
     *      {
     *          "title":""
     *          "information":""
     *          "link_text":""
     *          "link":""
     *      }
     * @param height
     * @param width
     */
    public InternetInformationPopupWindow(final Activity context, JSONObject jsonObject, int height, int width){
        this(context, jsonObject.getString("title"), jsonObject.getString("information"), jsonObject.getString("link_text"), jsonObject.getString("link"), height, width);
    }

    public InternetInformationPopupWindow(final Activity context, String title, String information, String link_text, String link, int height, int width) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_internet_information, null);
        information_title = mMenuView.findViewById(R.id.internet_information_pop_window_title_text);
        information_main = mMenuView.findViewById(R.id.internet_information_pop_window_main_information_text);
        information_link = mMenuView.findViewById(R.id.internet_information_pop_window_link_text);
        confirm_text = mMenuView.findViewById(R.id.internet_information_pop_window_confirm_text);

        changeTheme();

        title = title.replace("\\n", "\n");
        information_title.setText(title);
        information = information.replace("\\n", "\n");
        information_main.setText(information);

        if(link.length() > 0 && link_text.length() > 0) {
            CharSequence charSequence = Html.fromHtml("<a href=\'" + link + "\'>" + link_text + "</a>");
            information_link.setText(charSequence);
            information_link.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else{
            information_link.setMaxHeight(0);
        }

        //取消按钮
        confirm_text.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
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

                int height = mMenuView.findViewById(R.id.internet_information_pop_window_main_layout).getTop();
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

    private void changeTheme(){
        mMenuView.findViewById(R.id.internet_information_pop_window_main_layout).setBackground(ColorManager.getMainBackground_with_top_redius());
        confirm_text.setTextColor(ColorManager.getPrimaryColor());
    }

}
