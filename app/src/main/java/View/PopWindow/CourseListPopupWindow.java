package View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lu.mydemo.R;
import com.lu.mydemo.WeekCourseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Config.ColorManager;
import Utils.Course.MySubject;

/**
 * 创建时间: 2019/09/30 11:20 <br>
 * 作者: luyajun002 <br>
 * 描述: 周课程页面，点击的格子包含大于一节课
 */
public class CourseListPopupWindow extends PopupWindow {

    private View mMenuView;

    private ListView courseList_lv;

    public Animation mExitAnim;//退出动画
    public Animation mEnterAnim;//进入动画

    private Activity context;

    public CourseListPopupWindow(final Activity context, List<MySubject> list, final AdapterView.OnItemClickListener listener, int height, int width) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_course_list, null);

        courseList_lv = mMenuView.findViewById(R.id.pop_window_course_list_layout_course_list_view);
        courseList_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WeekCourseActivity.setSelectedCourseName(((TextView) view.findViewById(R.id.list_item_course_daily_title)).getText().toString().split(" - ")[0]);
                listener.onItemClick(parent, view, position, id);
                dismiss();
            }
        });

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
        drawable.setColors(new int[]{context.getResources().getColor(R.color.color_no_color), context.getResources().getColor(R.color.color_no_color), context.getResources().getColor(R.color.color_no_color), ColorManager.getPopwindow_background_color(), context.getResources().getColor(R.color.color_grayBackground)});
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        this.setBackgroundDrawable(drawable);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_window_course_list_layout).getTop();
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
        HashMap<String, MySubject> courseSet = new HashMap<>();
        for(MySubject subject : list){
            courseSet.put(subject.getName(), subject);
        }
        List<Map<String, Object>> dataList = new ArrayList<>();
        for(Map.Entry<String, MySubject> entry : courseSet.entrySet()){
            Map<String, Object> map = new HashMap<>();
            map.put("title", entry.getKey() + " - " + entry.getValue().getTeacher());
            map.put("context", entry.getValue().getRoom());
            dataList.add(map);
        }
        courseList_lv.setAdapter(new SimpleAdapter(context, dataList, R.layout.list_item_course_daily, new String[]{"title", "context"}, new int[]{R.id.list_item_course_daily_title, R.id.list_item_course_daily_context}));

    }

    private void changeTheme(){
        Log.i("Theme", "Change theme.");
        mMenuView.findViewById(R.id.pop_window_course_list_layout_title).setBackground(ColorManager.getTopRadiusBackground());
        mMenuView.findViewById(R.id.pop_window_course_list_layout_course_list_view).setBackground(ColorManager.getMainBackground());
    }

}
