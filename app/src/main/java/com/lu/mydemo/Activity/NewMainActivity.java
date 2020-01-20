package com.lu.mydemo.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.lu.mydemo.Activity.ui.main.SectionsPagerAdapter;
import com.lu.mydemo.R;
import com.lu.mydemo.UIMSTool.CourseJSONTransfer;
import com.lu.mydemo.Utils.Course.MySubject;
import com.lu.mydemo.Utils.Thread.MyThreadController;
import com.lu.mydemo.Utils.Time.TeachTimeTools;

import java.util.ArrayList;
import java.util.List;

public class NewMainActivity extends BaseActivity {

    ProgressBar progressBar;

    boolean progressBarShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //Loading
        progressBar = findViewById(R.id.progress_bar);
        progressBarShowing = (progressBar.getVisibility() == View.VISIBLE);

        RecyclerView courseListRv = findViewById(R.id.course_list_rv);
        initCourseListRv(courseListRv);

        //底部悬浮Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progressBarShowing) {
                    showToast("因为还没做完╯︿╰");
                    dismissLoading();
                }
                else {
                    showToast("虽然这是更新按钮，但并不会更新数据\n" +
                            "(￣y▽,￣)╭ ");
                    showLoading();
                }
            }
        });
    }

    private void initCourseListRv(final RecyclerView recyclerView) {
        showLoading();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int now_week = (int) TeachTimeTools.now_week;
                int day_of_week = TeachTimeTools.day_of_week;

                if (CourseJSONTransfer.courseList == null || CourseJSONTransfer.courseList.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CourseAdapter adapter = new CourseAdapter(NewMainActivity.this);
                            recyclerView.setLayoutManager(new LinearLayoutManager(NewMainActivity.this));
                            recyclerView.setAdapter(adapter);
                            dismissLoading();
                        }
                    });
                } else {
                    final List<MySubject> list = new ArrayList<>();
                    for (MySubject subject : CourseJSONTransfer.courseList) {
                        if (subject.getDay() == day_of_week && subject.getWeekList().contains(now_week)) {
                            list.add(subject);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CourseAdapter adapter = new CourseAdapter(NewMainActivity.this, list);
                            recyclerView.setLayoutManager(new LinearLayoutManager(NewMainActivity.this));
                            recyclerView.setAdapter(adapter);
                            dismissLoading();
                        }
                    });

                }
            }
        };
        MyThreadController.commit(runnable);
    }

    public void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        progressBarShowing = (progressBar.getVisibility() == View.VISIBLE);
    }

    public void dismissLoading(){
        progressBar.setVisibility(View.GONE);
        progressBarShowing = (progressBar.getVisibility() == View.VISIBLE);
    }

    public void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewMainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{

        private Context context;
        private List<MySubject> courseList;

        CourseAdapter(Context context){
            this.context = context;
        }

        CourseAdapter(Context context, List<MySubject> list){
            this.context = context;
            this.courseList = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_today_course_new, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            //数据绑定，事件绑定
            if(courseList == null){
                //列表为空，设置提示信息
                holder.courseIndexTv.setVisibility(View.GONE);
                holder.courseNameTv.setText(getText(R.string.no_information));
                holder.courseDescTv.setVisibility(View.GONE);
                return;
            }
            if(courseList.size() == 0){
                //没有课 提示信息
                holder.courseIndexTv.setVisibility(View.GONE);
                holder.courseNameTv.setText(getText(R.string.no_course));
                holder.courseDescTv.setVisibility(View.GONE);
                return;
            }
            String indexStr;
            if(courseList.get(position).getStep() == 1){
                indexStr = courseList.get(position).getStart() + "节";
            }
            else {
                indexStr = courseList.get(position).getStart() + "-" + (courseList.get(position).getStart() + courseList.get(position).getStep() - 1) + "节";
            }
            holder.courseIndexTv.setText(indexStr);
            holder.courseNameTv.setText(courseList.get(position).getName());
            holder.courseDescTv.setText(courseList.get(position).getRoom());
        }

        @Override
        public int getItemCount() {
            if(courseList == null || courseList.size() == 0){
                return 1;
            }
            return courseList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView courseIndexTv;
            TextView courseNameTv;
            TextView courseDescTv;

            ViewHolder(View itemView) {
                super(itemView);
                courseIndexTv = itemView.findViewById(R.id.course_index_tv);
                courseNameTv = itemView.findViewById(R.id.course_title_tv);
                courseDescTv = itemView.findViewById(R.id.course_desc_tv);
            }
        }
    }

}