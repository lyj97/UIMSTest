package View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lu.mydemo.R;

import java.util.List;

import Config.ColorManager;
import Utils.Course.MySubject;
import View.Control.CourseWeeklyControl;

public class CourseDetailPopupWindow extends PopupWindow {

    private View mMenuView;

    private TextView title_tv;
    private TextView coursePlace_tv;
    private TextView teacher_tv;
//    private TextView week_tv;
    private TextView time_tv;

    public Animation mExitAnim;//退出动画
    public Animation mEnterAnim;//进入动画

    private Activity context;

    public CourseDetailPopupWindow(final Activity context, List list, int height, int width) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.view_course_detail, null);

        title_tv = mMenuView.findViewById(R.id.pop_window_course_detail_layout_title);
        coursePlace_tv = mMenuView.findViewById(R.id.pop_window_course_detail_information_course_place);
        teacher_tv = mMenuView.findViewById(R.id.pop_window_course_detail_information_teacher);
//        week_tv = mMenuView.findViewById(R.id.pop_window_course_detail_information_week);
        time_tv = mMenuView.findViewById(R.id.pop_window_course_detail_information_time);

        CourseWeeklyControl weekly = new CourseWeeklyControl(mMenuView.findViewById(R.id.pop_window_course_detail_time), context);
        weekly.init(list);

        changeTheme();

        initView(list);

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

                int height = mMenuView.findViewById(R.id.pop_window_course_detail_layout).getTop();
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

    public void initView(List<MySubject> list){
        if(list == null || list.size() < 1) return;
        MySubject subject = list.get(0);
        title_tv.setText(subject.getName());
        coursePlace_tv.setText(subject.getRoom());
        teacher_tv.setText(subject.getTeacher());
//        week_tv.setText(subject.getWeekRange());
        time_tv.setText(subject.getWeekRange() + " " + subject.getStepRange());

        for(int i=1; i<list.size(); i++){
//            week_tv.setText(week_tv.getText() + "\n" + list.get(i).getWeekRange());
            time_tv.setText(time_tv.getText() + "\n" + list.get(i).getWeekRange() + " " + list.get(i).getStepRange());
        }

    }

    private void changeTheme(){
        Log.i("Theme", "Change theme.");
//        mMenuView.findViewById(R.id.pop_window_course_detail_layout_title).setBackground(ColorManager.getTopRadiusBackground());
        mMenuView.findViewById(R.id.pop_window_course_detail_layout).setBackground(ColorManager.getMainBackground());
    }

}
