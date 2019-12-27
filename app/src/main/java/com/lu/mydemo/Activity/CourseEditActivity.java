package com.lu.mydemo.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.lu.mydemo.R;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.UIMSTool.CourseJSONTransfer;
import com.lu.mydemo.Utils.Course.MySubject;

public class CourseEditActivity extends BaseActivity {

    private TextView courseName_tv;

    private TextView editCourseWeek_tv;
    private TextView editCourseTime_tv;
    private EditText editCourseTeacher_tv;
    private EditText editCoursePlace_tv;

    private TextView otherTimeHint_tv;

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;

    private TextView back_tv;

    public List<MySubject> dataList;

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int adapterPosition) {
            fillView(dataList.get(adapterPosition).getDb_id());
        }
    };

//    public com.lu.mydemo.View.OnClickListener onClickListener = new com.lu.mydemo.View.OnClickListener() {
//        @Override
//        public void onClick(com.lu.mydemo.View v) {
//            Toast.makeText(CourseEditActivity.this, v.getId() + "", Toast.LENGTH_SHORT).show();
//            switch (v.getId()) {
//                case R.id.activity_course_edit_detail_course_week_layout: {
//                    Toast.makeText(CourseEditActivity.this, "week_layout!", Toast.LENGTH_SHORT).show();
//                }
//                case R.id.activity_course_edit_detail_course_week_layout_text_view: {
//                    Toast.makeText(CourseEditActivity.this, "week!", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                case R.id.activity_course_edit_detail_course_time_layout: {
//                    Toast.makeText(CourseEditActivity.this, "time_layout!", Toast.LENGTH_SHORT).show();
//                }
//                case R.id.activity_course_edit_detail_course_time_layout_text_view: {
//                    Toast.makeText(CourseEditActivity.this, "time!", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                case R.id.activity_course_edit_detail_course_teacher_layout: {
//                    Toast.makeText(CourseEditActivity.this, "teacher_layout!", Toast.LENGTH_SHORT).show();
//                }
//                case R.id.activity_course_edit_detail_course_teacher_layout_text_view: {
//                    Toast.makeText(CourseEditActivity.this, "teacher!", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                case R.id.activity_course_edit_detail_course_place_layout: {
//                    Toast.makeText(CourseEditActivity.this, "place_layout!", Toast.LENGTH_SHORT).show();
//                }
//                case R.id.activity_course_edit_detail_course_place_layout_text_view: {
//                    Toast.makeText(CourseEditActivity.this, "place!", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                default: {
//                    Toast.makeText(CourseEditActivity.this, "ID:" + v.getId(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        editCourseWeek_tv = findViewById(R.id.activity_course_edit_detail_course_week_layout_text_view);
        editCourseTime_tv = findViewById(R.id.activity_course_edit_detail_course_time_layout_text_view);
        editCourseTeacher_tv = findViewById(R.id.activity_course_edit_detail_course_teacher_layout_text_view);
        editCoursePlace_tv = findViewById(R.id.activity_course_edit_detail_course_place_layout_text_view);

        otherTimeHint_tv = findViewById(R.id.activity_course_edit_other_time_hint);

        courseName_tv = findViewById(R.id.activity_course_edit_detail_course_name_text_view);
        swipeRecyclerView = findViewById(R.id.activity_course_edit_detail_layout);

        back_tv = findViewById(R.id.activity_course_edit_navigation_back_text);

        initView();
        fillView();
    }

    public void initView(){
        changeTheme();
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        findViewById(R.id.activity_course_edit_detail_course_week_layout).setOnClickListener(onClickListener);
//        findViewById(R.id.activity_course_edit_detail_course_time_layout).setOnClickListener(onClickListener);
//        findViewById(R.id.activity_course_edit_detail_course_teacher_layout).setOnClickListener(onClickListener);
//        findViewById(R.id.activity_course_edit_detail_course_place_layout).setOnClickListener(onClickListener);
    }

    public void fillView(int courseDbId){
        dataList = getCourseList(courseName_tv.getText().toString(), courseDbId);
        myAdapter = createAdapter();
        swipeRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);
    }

    public void fillView(){
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(bundle != null) {
            String courseName = bundle.getString("courseName");
            int courseDbId = bundle.getInt("courseDbId");
            courseName_tv.setText(courseName);
            dataList = getCourseList(courseName, courseDbId);
        }
        myAdapter = createAdapter();
        swipeRecyclerView.setOnItemClickListener(onItemClickListener);
        swipeRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);
    }

    public void fillEditCourseView(MySubject subject){
        editCourseWeek_tv.setText(subject.getWeekRange());
        editCourseTime_tv.setText(subject.getStart() + "~" + (subject.getStart() + subject.getStep() - 1) + "节");
        editCourseTeacher_tv.setText(subject.getTeacher());
        editCoursePlace_tv.setText(subject.getRoom());
    }

    public ArrayList<MySubject> getCourseList(String courseName, int nowEditId){
//        Log.i("CourseEditActivity", "NowEditId:" + nowEditId);
//        Log.i("CourseEditActivity", "CourseList:" + CourseJSONTransfer.courseList);
        ArrayList<MySubject> dataList = new ArrayList<>();
        for(MySubject subject : CourseJSONTransfer.courseList){
            if(subject.getName().equals(courseName)){
                if(subject.getDb_id() == nowEditId) fillEditCourseView(subject);
                else dataList.add(subject);
            }
        }
        if(dataList.size() > 0)
            Collections.sort(dataList, MySubject.timeComparater);
        else {
            otherTimeHint_tv.setText("本课程暂无其他时间段");
            swipeRecyclerView.setVisibility(View.INVISIBLE);
        }
//        Log.i("CourseEditActivity", "OtherTimeList:" + dataList);
        return dataList;
    }

    public MainAdapter createAdapter() {
        return new CourseTimeListAdapter(this);
    }

    public static class CourseTimeListAdapter extends MainAdapter {

        private List<MySubject> mDataList;

        public CourseTimeListAdapter(Context context){
            super(context);
        }

        public void notifyDataSetChanged(List dataList) {
            this.mDataList = (List<MySubject>)dataList;
            super.notifyDataSetChanged(mDataList);
        }

        @Override
        public int getItemCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        @NonNull
        @Override
        public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CourseEditActivity.CourseTimeListAdapter.ViewHolder(getInflater().inflate(R.layout.card_course_detail_edit, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
            holder.setDate(mDataList.get(position));
        }

        class ViewHolder extends MainAdapter.ViewHolder {

            TextView week_tv;
            TextView time_tv;
            TextView teacher_tv;
            TextView place_tv;

            public ViewHolder(View itemView) {
                super(itemView);
                week_tv = itemView.findViewById(R.id.course_detail_edit_week_layout_text_view);
                time_tv = itemView.findViewById(R.id.course_detail_edit_time_layout_text_view);
                teacher_tv = itemView.findViewById(R.id.course_detail_edit_teacher_layout_text_view);
                place_tv = itemView.findViewById(R.id.course_detail_edit_place_layout_text_view);
            }

            @Override
            public void setDate(MySubject subject){
                week_tv.setText(subject.getWeekRange());
                time_tv.setText(subject.getStart() + "~" + (subject.getStart() + subject.getStep() - 1) + "节");
                teacher_tv.setText(subject.getTeacher());
                place_tv.setText(subject.getRoom());
            }
        }

    }

    private void changeTheme(){
        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ColorManager.getNoCloor());
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);

        findViewById(R.id.activity_course_edit_layout).setBackground(ColorManager.getMainBackground_full());
        findViewById(R.id.activity_course_edit_detail_course_name_background_view).setBackgroundColor(ColorManager.getPrimaryColor());
    }
}
