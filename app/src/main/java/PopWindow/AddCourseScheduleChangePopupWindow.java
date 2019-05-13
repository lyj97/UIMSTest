package PopWindow;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lu.mydemo.R;

import java.util.Date;
import java.util.Locale;

public class AddCourseScheduleChangePopupWindow extends PopupWindow {

    private View mMenuView;

    private EditText title_text;
    private TextView pre_time_text;
    private TextView change_time_text;

    private Button commit_button;
    private Button cancel_button;

    private String title;
    private String pre_time;
    private String change_time;

    public Animation mExitAnim;//退出动画
    public Animation mEnterAnim;//进入动画

    private boolean ispreTimeSet = false;
    private boolean ischangeTimeSet = false;

    private Activity context;

    public AddCourseScheduleChangePopupWindow(final Activity context, final OnClickListener commitButtonOnClickListener, int height, int width) {
        super(context);
        this.context = context;
        ispreTimeSet = false;
        ischangeTimeSet = false;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_add_course_schedule, null);
        title_text = mMenuView.findViewById(R.id.course_schedule_change_add_title_text);
        pre_time_text = mMenuView.findViewById(R.id.course_schedule_change_add_pre_time_text);
        change_time_text = mMenuView.findViewById(R.id.course_schedule_change_add_change_time_text);
        commit_button = mMenuView.findViewById(R.id.course_schedule_change_commit_button);
        cancel_button = mMenuView.findViewById(R.id.course_schedule_change_cancel_button);

        //取消按钮
        cancel_button.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });

        //设置按钮监听
        commit_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ispreTimeSet) {
                    commit_button.setText("请设置开始时间");
                    commit_button.setBackground(context.getResources().getDrawable(R.drawable.shape_disable));
                    commit_button.setEnabled(false);
                    return;
                }
//                    pre_time_text.setError("请输入要改变课程的日期");
//                if(!ischangeTimeSet) change_time_text.setError("请输入改变后的日期");

                if(pre_time_text.getText().equals(change_time_text.getText())){
                    commit_button.setText("开始时间与结束时间不能相同");
                    commit_button.setBackground(context.getResources().getDrawable(R.drawable.shape_disable));
                    commit_button.setEnabled(false);
                    return;
                }

                if(ispreTimeSet) {
                    title = (title_text.getText() == null) ? "" : title_text.getText().toString();
                    if(!ischangeTimeSet) change_time = "0000-00-00";
                    commitButtonOnClickListener.onClick(commit_button);
                    dismiss();
                }
            }
        });

        Locale.setDefault(Locale.CHINA);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        pre_time_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        pre_time_text.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        pre_time = getTimeString(year, month, dayOfMonth);
                        ispreTimeSet = true;
                        commit_button.setText("确定");
                        commit_button.setBackground(context.getResources().getDrawable(R.drawable.login_button_background));
                        commit_button.setEnabled(true);
                    }
                }, cal.get(Calendar.YEAR),  cal.get(Calendar.MONTH),  cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        change_time_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        change_time_text.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        change_time = getTimeString(year, month, dayOfMonth);
                        ischangeTimeSet = true;
                        commit_button.setText("确定");
                        commit_button.setBackground(context.getResources().getDrawable(R.drawable.login_button_background));
                        commit_button.setEnabled(true);
                    }
                }, cal.get(Calendar.YEAR),  cal.get(Calendar.MONTH),  cal.get(Calendar.DAY_OF_MONTH)).show();
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
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.color_popWindowBackground));
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.course_schedule_change_pop_layout).getTop();
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

    private String getTimeString(int year, int month, int dayOfMonth){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(year);
        stringBuilder.append('-');
        month += 1;
        if(month < 10){
            stringBuilder.append(0);
            stringBuilder.append(month);
        }
        else{
            stringBuilder.append(month);
        }
        stringBuilder.append('-');
        if(dayOfMonth < 10){
            stringBuilder.append(0);
            stringBuilder.append(dayOfMonth);
        }
        else{
            stringBuilder.append(dayOfMonth);
        }
        return stringBuilder.toString();
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

    public String getTitle() {
        return title;
    }

    public String getPre_time() {
        return pre_time;
    }

    public String getChange_time() {
        return change_time;
    }
}
