package View.PopWindow;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.icu.util.Calendar;
import android.util.Log;
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
import android.widget.TimePicker;

import com.lu.mydemo.R;

import java.util.Date;
import java.util.Locale;

import Config.ColorManager;
import Utils.Exam.ExamSchedule;

public class AddCourseExamPopupWindow extends PopupWindow {

    private View mMenuView;

    private TextView layout_title;
    private EditText title_text;
    private TextView exam_date_text;
    private TextView exam_time_text;
    private EditText exam_place_text;

    private Button commit_button;
    private Button cancel_button;

    private String title;
    private String exam_date;
    private String exam_time;
    private String exam_place;

    public Animation mExitAnim;//退出动画
    public Animation mEnterAnim;//进入动画

    private boolean ispreTimeSet = false;
    private boolean ischangeTimeSet = false;

    private boolean overWrite = false;

    private Activity context;

    public AddCourseExamPopupWindow(final Activity context, final OnClickListener commitButtonOnClickListener, int height, int width) {
        super(context);
        this.context = context;
        ispreTimeSet = false;
        ischangeTimeSet = false;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_add_exam, null);
        layout_title = mMenuView.findViewById(R.id.add_exam_pop_layout_title);
        title_text = mMenuView.findViewById(R.id.add_exam_add_title_text);
        exam_date_text = mMenuView.findViewById(R.id.add_exam_add_date_text);
        exam_time_text = mMenuView.findViewById(R.id.add_exam_add_time_text);
        exam_place_text = mMenuView.findViewById(R.id.add_exam_add_place_text);
        commit_button = mMenuView.findViewById(R.id.add_exam_commit_button);
        cancel_button = mMenuView.findViewById(R.id.add_exam_cancel_button);

        changeTheme();

//        layout_title.setText("添加考试");
//        title_text.setHint("科目");
//        exam_date_text.setText("日期");
//        exam_time_text.setText("时间");
//        exam_place_text.setText("地点");

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
                title = (title_text.getText() == null) ? "" : title_text.getText().toString();
                exam_place = (exam_place_text.getText() == null) ? "" : exam_place_text.getText().toString();
                if (!ispreTimeSet) {
                    commit_button.setText("请设置日期");
                    commit_button.setBackground(ColorManager.getInternetInformationButtonBackground_disable_full());
                    commit_button.setEnabled(false);
                    return;
                }

                if (!ischangeTimeSet) {
                    commit_button.setText("请设置时间");
                    commit_button.setBackground(ColorManager.getInternetInformationButtonBackground_disable_full());
                    commit_button.setEnabled(false);
                    return;
                }

                if (ExamSchedule.containsTitle(title)) {
                    if (overWrite) {
                        commitButtonOnClickListener.onClick(commit_button);
                        dismiss();
                        return;
                    }
                    commit_button.setText("确认覆盖已有同名考试");
                    commit_button.setBackground(context.getResources().getDrawable(R.drawable.shape_warn));
                    overWrite = true;
                }

                else{
                    commitButtonOnClickListener.onClick(commit_button);
                    dismiss();
                }

            }
        });

        Locale.setDefault(Locale.CHINA);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        exam_date_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, ColorManager.getDatePickerDialogTheme(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        exam_date_text.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        exam_date = getDataString(year, month, dayOfMonth);
                        ispreTimeSet = true;
                        commit_button.setText("确定");
                        commit_button.setBackground(ColorManager.getInternetInformationButtonBackground_full());
                        commit_button.setEnabled(true);
                    }
                }, cal.get(Calendar.YEAR),  cal.get(Calendar.MONTH),  cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        exam_time_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context, ColorManager.getDatePickerDialogTheme(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        exam_time_text.setText(getTimeString(hourOfDay, minute));
                        exam_time = getTimeString(hourOfDay, minute);
                        ischangeTimeSet = true;
                        commit_button.setText("确定");
                        commit_button.setBackground(ColorManager.getInternetInformationButtonBackground_full());
                        commit_button.setEnabled(true);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
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
        drawable.setColors(new int[]{context.getResources().getColor(R.color.color_no_color), context.getResources().getColor(R.color.color_no_color), ColorManager.getPopwindow_background_color(), context.getResources().getColor(R.color.color_grayBackground)});
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        this.setBackgroundDrawable(drawable);
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

    public AddCourseExamPopupWindow(final Activity context, final OnClickListener commitButtonOnClickListener, int height, int width, String courseName){
        this(context, commitButtonOnClickListener, height, width);

        title_text.setText(courseName);
        title_text.setEnabled(false);

    }

    private String getTimeString(int hour, int minute){
        StringBuilder stringBuilder = new StringBuilder();

        if(hour < 10){
            stringBuilder.append(0);
            stringBuilder.append(hour);
        }
        else {
            stringBuilder.append(hour);
        }
        stringBuilder.append(":");
        if(minute < 10){
            stringBuilder.append(0);
            stringBuilder.append(minute);
        }
        else {
            stringBuilder.append(minute);
        }
        return stringBuilder.toString();
    }

    private String getDataString(int year, int month, int dayOfMonth){
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
        overWrite = false;
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getWindow().peekDecorView();
        if(view != null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void changeTheme(){
        Log.i("Theme", "Change theme.");
        mMenuView.findViewById(R.id.add_exam_pop_layout_title).setBackgroundColor(ColorManager.getPrimaryColor());
        mMenuView.findViewById(R.id.add_exam_pop_layout_main_information).setBackground(ColorManager.getMainBackground());
        commit_button.setBackground(ColorManager.getInternetInformationButtonBackground_full());
    }

    public String getTitle() {
        return title;
    }

    public String getExam_place() {
        return exam_place;
    }

    public String getExam_date() {
        return exam_date;
    }

    public String getExam_time() {
        return exam_time;
    }
}
