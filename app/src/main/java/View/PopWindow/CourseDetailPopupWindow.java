package View.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lu.mydemo.CourseEditActivity;
import com.lu.mydemo.R;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Config.ColorManager;
import UIMSTool.CourseJSONTransfer;
import Utils.Course.MySubject;
import View.Control.CourseWeeklyControl;

public class CourseDetailPopupWindow extends PopupWindow {

    private View mMenuView;

    private TextView title_tv;

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;

    public List<MySubject> dataList;

    public Animation mExitAnim;//退出动画
    public Animation mEnterAnim;//进入动画

    private Activity context;

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int adapterPosition) {
            Intent intent = new Intent(context, CourseEditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("courseName", title_tv.getText().toString());
            bundle.putInt("courseDbId", dataList.get(adapterPosition).getDb_id());
            intent.putExtra("bundle", bundle);
            context.startActivity(intent);
            dismiss();
        }
    };

    public CourseDetailPopupWindow(final Activity context, List<MySubject> list, int height, int width) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.view_course_detail, null);

        title_tv = mMenuView.findViewById(R.id.pop_window_course_detail_layout_title);

        swipeRecyclerView = mMenuView.findViewById(R.id.pop_window_course_detail_item_layout);

        Collections.sort(list, MySubject.timeComparater);

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

        dataList = getCourseList(subject.getName());
        myAdapter = createAdapter();
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        swipeRecyclerView.setOnItemClickListener(onItemClickListener);
        swipeRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);

    }

    public ArrayList<MySubject> getCourseList(String courseName){
        ArrayList<MySubject> dataList = new ArrayList<>();
        for(MySubject subject : CourseJSONTransfer.courseList){
            if(subject.getName().equals(courseName)){
                dataList.add(subject);
            }
        }
        Collections.sort(dataList, MySubject.timeComparater);
        return dataList;
    }

    public MainAdapter createAdapter() {
        return new CourseEditActivity.CourseTimeListAdapter(context);
    }

    private void changeTheme(){
        Log.i("Theme", "Change theme.");
//        mMenuView.findViewById(R.id.pop_window_course_detail_layout_title).setBackground(ColorManager.getTopRadiusBackground());
        mMenuView.findViewById(R.id.pop_window_course_detail_top_back).setBackground(ColorManager.getTopRadiusBackground());
        mMenuView.findViewById(R.id.pop_window_course_detail_layout).setBackground(ColorManager.getMainBackground());
    }

}
