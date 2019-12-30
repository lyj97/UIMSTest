package com.lu.mydemo.Activity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;
import com.lu.mydemo.View.MyView.MyToolBar;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.tapadoo.alerter.Alerter;
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

    private EditText editCourseStartWeek_tv;
    private EditText editCourseEndWeek_tv;
    private CheckBox editCourseSingleWeekCheckBox;
    private CheckBox editCourseDoubleWeekCheckBox;

    private EditText editCourseStartTime_tv;
    private EditText editCourseEndTime_tv;

    private EditText editCourseDay_tv;

    private EditText editCourseTeacher_tv;
    private EditText editCoursePlace_tv;

    private TextView otherTimeHint_tv;

    private FloatingActionButton editCourseSaveButton;
    private Button addNewButton;
    private Button editCourseDeleteButton;

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;

    private MyToolBar toolBar;

    public List<MySubject> dataList;

    public MySubject currentEditSubject;
    public boolean isAddingNew = false;

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int adapterPosition) {
            fillView(dataList.get(adapterPosition).getDb_id());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        toolBar = new MyToolBar(this);

        editCourseStartWeek_tv = findViewById(R.id.activity_course_edit_detail_course_week_start_edit_text);
        editCourseEndWeek_tv = findViewById(R.id.activity_course_edit_detail_course_week_end_edit_text);
        editCourseSingleWeekCheckBox = findViewById(R.id.activity_course_edit_detail_course_week_single_week_check_box);
        editCourseDoubleWeekCheckBox = findViewById(R.id.activity_course_edit_detail_course_week_double_week_check_box);

        editCourseStartTime_tv = findViewById(R.id.activity_course_edit_detail_course_time_start_edit_text);
        editCourseEndTime_tv = findViewById(R.id.activity_course_edit_detail_course_time_end_edit_text);

        editCourseDay_tv = findViewById(R.id.activity_course_edit_detail_course_day_edit_text);

        editCourseTeacher_tv = findViewById(R.id.activity_course_edit_detail_course_teacher_layout_text_view);
        editCoursePlace_tv = findViewById(R.id.activity_course_edit_detail_course_place_layout_text_view);

        otherTimeHint_tv = findViewById(R.id.activity_course_edit_other_time_hint);

        editCourseSaveButton = findViewById(R.id.activity_course_edit_save_button);
        addNewButton = findViewById(R.id.activity_course_edit_add_new_button);
        editCourseDeleteButton = findViewById(R.id.activity_course_edit_delete_button);

        courseName_tv = findViewById(R.id.activity_course_edit_detail_course_name_text_view);
        swipeRecyclerView = findViewById(R.id.activity_course_edit_detail_layout);

        initView();
        fillView(false);
    }

    public void initView(){
        changeTheme();
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolBar.setSubTitle("编辑课程");

        //单双周不同时选中
        editCourseSingleWeekCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //TODO 单双周相关
//                    currentEditSubject.setWeekOddEven();
                    editCourseDoubleWeekCheckBox.setChecked(false);
                }
            }
        });
        editCourseDoubleWeekCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //TODO 单双周相关
//                    currentEditSubject.setWeekOddEven();
                    editCourseSingleWeekCheckBox.setChecked(false);
                }
            }
        });

        editCourseSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdit();
            }
        });
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentEditSubject != null) {
                    MySubject temp = new MySubject();
                    temp.setName(currentEditSubject.getName());
                    temp.setId(currentEditSubject.getId());
                    temp.setTeacher(currentEditSubject.getTeacher());
                    temp.setRoom(currentEditSubject.getRoom());
                    // fillView 会清空 currentEditSubject
                    fillView(true);
                    currentEditSubject = temp;
                    AlertCenter.showAlert(CourseEditActivity.this, "请在上方输入新增时间段信息，完成后，点击\"保存\"按钮.");
                    cleanEditCourseView();
                    isAddingNew = true;
                }
                else {
                    AlertCenter.showErrorAlert(CourseEditActivity.this, "抱歉，出现错误，请联系开发者.");
                }
            }
        });
        editCourseDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Alerter.create(CourseEditActivity.this)
                                .setTitle("提示")
                                .setText("确定要删除吗？\n删除后不可恢复！")
                                .enableSwipeToDismiss()
                                .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor_warning())
                                .addButton("删除", R.style.AlertButton, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteEdit();
                                    }
                                })
                                .show();
                    }
                });
            }
        });
    }

    public void fillView(int courseDbId){
        dataList = getCourseList(courseName_tv.getText().toString(), courseDbId, false);
        myAdapter = createAdapter();
        swipeRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);
    }

    public void fillView(boolean ignoreEditView){
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(bundle != null) {
            String courseName = bundle.getString("courseName");
            int courseDbId = bundle.getInt("courseDbId");
            courseName_tv.setText(courseName);
            dataList = getCourseList(courseName, courseDbId, ignoreEditView);
        }
        if(swipeRecyclerView.getAdapter() == null) {
            myAdapter = createAdapter();
            swipeRecyclerView.setOnItemClickListener(onItemClickListener);
        }
        swipeRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);
    }

    public void fillEditCourseView(){
        if(currentEditSubject == null){
            Log.e("CourseEditActivity", "CurrentEditSubject is NULL!");
        }
        editCourseStartWeek_tv.setText(String.valueOf(currentEditSubject.getWeekList().get(0)));
        editCourseEndWeek_tv.setText(String.valueOf(currentEditSubject.getWeekList().get(currentEditSubject.getWeekList().size() - 1)));
        if(isSingleWeek(currentEditSubject.getWeekList())){
            setCheckSingleWeek();
        }
        else if(isDoubleWeek(currentEditSubject.getWeekList())){
            setCheckDoubleWeek();
        }
        editCourseStartTime_tv.setText(String.valueOf(currentEditSubject.getStart()));
        editCourseEndTime_tv.setText(String.valueOf(currentEditSubject.getStart() + currentEditSubject.getStep() - 1));
        editCourseDay_tv.setText(String.valueOf(currentEditSubject.getDay()));
        editCourseTeacher_tv.setText(currentEditSubject.getTeacher());
        editCoursePlace_tv.setText(currentEditSubject.getRoom());
    }

    public void cleanEditCourseView(){
        editCourseStartWeek_tv.setText("");
        editCourseEndWeek_tv.setText("");
        editCourseSingleWeekCheckBox.setChecked(false);
        editCourseDoubleWeekCheckBox.setChecked(false);
        editCourseStartTime_tv.setText("");
        editCourseEndTime_tv.setText("");
        editCourseDay_tv.setText("");
        editCourseTeacher_tv.setText(currentEditSubject.getTeacher());
        editCoursePlace_tv.setText(currentEditSubject.getRoom());
    }

    public void saveEdit(){
        int edit_day = Integer.parseInt(editCourseDay_tv.getText().toString());
        if(!(edit_day >= 1 && edit_day <= 7)){
            AlertCenter.showErrorAlert(CourseEditActivity.this, "星期必须为 1~7 之间的值");
            return;
        }
        if(currentEditSubject != null) {
            currentEditSubject.setWeekList(getWeekList());
            currentEditSubject.setStart(Integer.parseInt(editCourseStartTime_tv.getText().toString()));
            currentEditSubject.setStep(Integer.parseInt(editCourseEndTime_tv.getText().toString()) - Integer.parseInt(editCourseStartTime_tv.getText().toString()) + 1);
            currentEditSubject.setDay(edit_day);
            currentEditSubject.setRoom(editCoursePlace_tv.getText().toString());
            currentEditSubject.setTeacher(editCourseTeacher_tv.getText().toString());
        }
        if(isAddingNew){
            //todo 增加时间段
            if(CourseJSONTransfer.insertCourseTime(getApplicationContext(), currentEditSubject)){
                AlertCenter.showAlert(CourseEditActivity.this, "增加成功.");
                CourseJSONTransfer.transferCourseList(getApplicationContext(), null, true);
                fillView(false);
                isAddingNew = false;
            }
            else {
                AlertCenter.showErrorAlert(CourseEditActivity.this, "出现错误，添加失败.");
            }
            return;
        }
        if(currentEditSubject != null){
            if(CourseJSONTransfer.updateCourse(getApplicationContext(), currentEditSubject)){
                AlertCenter.showAlert(CourseEditActivity.this, "编辑成功");
                CourseJSONTransfer.transferCourseList(getApplicationContext(), null, true);
            }
            else {
                AlertCenter.showErrorAlert(CourseEditActivity.this, "编辑失败");
            }
            fillView(false);
        }
    }

    public void deleteEdit(){
        if(currentEditSubject != null) {
            if (CourseJSONTransfer.deleteCourse(getApplicationContext(), currentEditSubject)) {
                AlertCenter.showAlert(CourseEditActivity.this, "编辑成功");
                CourseJSONTransfer.transferCourseList(getApplicationContext(), null, true);
            } else {
                AlertCenter.showErrorAlert(CourseEditActivity.this, "编辑失败");
            }
            fillView(false);
        }
    }

    public boolean isSingleWeek(List<Integer> list){
        if(list == null || list.size() == 1) return false;
        for(int num : list){
            if(num % 2 == 0) return false;
        }
        return true;
    }

    public boolean isDoubleWeek(List<Integer> list){
        if(list == null || list.size() == 1) return false;
        for(int num : list){
            if(num % 2 == 1) return false;
        }
        return true;
    }

    public void setCheckSingleWeek(){
        editCourseSingleWeekCheckBox.setChecked(true);
        editCourseDoubleWeekCheckBox.setChecked(false);
    }

    public void setCheckDoubleWeek(){
        editCourseSingleWeekCheckBox.setChecked(false);
        editCourseDoubleWeekCheckBox.setChecked(true);
    }

    public List<Integer> getWeekList(){
        List<Integer> list = new ArrayList<>();
        int startWeek = Integer.parseInt(editCourseStartWeek_tv.getText().toString());
        int endWeek = Integer.parseInt(editCourseEndWeek_tv.getText().toString());
        if(startWeek == endWeek){
            list.add(startWeek);
            return list;
        }
        else if(startWeek > endWeek){
            return null;
        }
        else {
            for (int i = startWeek; i <= endWeek; i++) {
                if (editCourseSingleWeekCheckBox.isChecked() && i % 2 == 1) {
                    list.add(i);
                    i++;
                } else if (editCourseDoubleWeekCheckBox.isChecked() && i % 2 == 0) {
                    list.add(i);
                    i++;
                }
                else if (!(editCourseSingleWeekCheckBox.isChecked() || editCourseDoubleWeekCheckBox.isChecked())) {
                    list.add(i);
                }
            }
        }
        return list;
    }

    public ArrayList<MySubject> getCourseList(String courseName, int nowEditId, boolean ignoreEditView){
//        Log.i("CourseEditActivity", "NowEditId:" + nowEditId);
//        Log.i("CourseEditActivity", "CourseList:" + CourseJSONTransfer.courseList);
        currentEditSubject = null;
        ArrayList<MySubject> dataList = new ArrayList<>();
        if(ignoreEditView){
            // 忽略当前编辑
            for (MySubject subject : CourseJSONTransfer.courseList) {
                if (subject.getName().equals(courseName)) {
                    dataList.add(subject);
                }
            }
        }
        else {
            for (MySubject subject : CourseJSONTransfer.courseList) {
                if (subject.getName().equals(courseName)) {
                    if (subject.getDb_id() == nowEditId) {
                        currentEditSubject = subject;
                        fillEditCourseView();
                    } else dataList.add(subject);
                }
            }
        }
        if(dataList.size() == 0 && currentEditSubject == null){
            Toast.makeText(CourseEditActivity.this, "当前课程已被删除！", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(dataList.size() > 0 && currentEditSubject == null && !ignoreEditView){
            currentEditSubject = dataList.remove(0);
            fillEditCourseView();
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

    //获取颜色方法
    private ColorStateList getMyFabColorStateList(int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
//        int color = ContextCompat.getColor(CourseEditActivity.this, colorRes);
        int[] colors = new int[]{colorRes, colorRes, colorRes, colorRes};
        return new ColorStateList(states, colors);
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
        findViewById(R.id.activity_course_edit_add_new_button).setBackground(ColorManager.getLocalInformationButtonBackground());
        findViewById(R.id.activity_course_edit_save_button).setBackgroundTintList(getMyFabColorStateList(ColorManager.getPrimaryColor()));
    }
}
