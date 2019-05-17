package com.lu.mydemo;

/**
 * 文档
 * https://www.yuque.com/zhuangfei/timetableview/
 * GitHub
 * https://github.com/zfman/TimetableView
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import java.util.List;

import Config.ColorManager;
import Utils.Course.MySubject;
import Utils.Course.SubjectRepertory;

public class WeekCourseActivity extends AppCompatActivity implements View.OnClickListener {

    //控件
    TimetableView mTimetableView;
    WeekView mWeekView;

    LinearLayout layout;
    TextView titleTextView;
    List<MySubject> mySubjects;

    //记录切换的周次，不一定是当前周
    int target = -1;
    AlertDialog alertDialog;

    int now_week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_course);

        Bundle bundle =  getIntent().getBundleExtra("bundle");
        target = (int) bundle.getLong("now_week");
        now_week = target;

        titleTextView = findViewById(R.id.week_course_title);
        layout = findViewById(R.id.weekCourseLayout);
        layout.setOnClickListener(this);

        changeTheme();

        initTimetableView();

        requestData();
    }

    /**
     * 2秒后刷新界面，模拟网络请求
     */
    private void requestData() {
//        showLoading("加载中，请稍候...");
        handler.sendEmptyMessage(0x123);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(alertDialog!=null) alertDialog.hide();
            mySubjects = SubjectRepertory.loadDefaultSubjects();

            mWeekView.source(mySubjects).showView();
            mTimetableView.source(mySubjects).showView();
            Alerter.hide();
        }
    };
    /**
     * 初始化课程控件
     */
    private void initTimetableView() {
        //获取控件
        mWeekView = findViewById(R.id.id_weekview);
        mTimetableView = findViewById(R.id.id_timetableView);

        //设置周次选择属性
        mWeekView.curWeek((int)LoginActivity.now_week)
                .itemCount(LoginActivity.weeks > 0 ? LoginActivity.weeks : 20)
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        mTimetableView.changeWeekOnly(week);
                    }
                })
                .callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        onWeekLeftLayoutClicked();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.curWeek((int)LoginActivity.now_week)
                /**
                 * 最大课程数
                 */
                .maxSlideItem(11)
                /**
                 * 取消旗标布局
                 */
                .isShowFlaglayout(false)
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
                .alpha(0.2f, 0.1f, 0.6f)
//                .curTerm("大三下学期")
//                .callback(new ISchedule.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View v, List<Schedule> scheduleList) {
//                        display(scheduleList);
//                    }
//                })
//                .callback(new ISchedule.OnItemLongClickListener() {
//                    @Override
//                    public void onLongClick(View v, int day, int start) {
//                        Toast.makeText(WeekCourseActivity.this,
//                                "长按:周" + day  + ",第" + start + "节",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        titleTextView.setText("第" + curWeek + "周");
                    }
                })
                .showView();
    }

    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
    @Override
    protected void onStart() {
        super.onStart();
        mTimetableView.onDateBuildListener()
                .onHighLight();
    }

    /**
     * 周次选择布局的左侧被点击时回调
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
        final String items[] = new String[20];
        int itemCount = mWeekView.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        target = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target = i;
                    }
                });
        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (target != -1) {
                    mWeekView.curWeek(target + 1).updateView();
                    mTimetableView.changeWeekForce(target + 1);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 显示内容
     *
     * @param beans
     */
    protected void display(List<Schedule> beans) {
        String str = "";
        for (Schedule bean : beans) {
            str += bean.getName() + ","+bean.getWeekList().toString()+","+bean.getStart()+","+bean.getStep()+"\n";
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.weekCourseLayout:
                //如果周次选择已经显示了，那么将它隐藏，更新课程、日期
                //否则，显示
                if (mWeekView.isShowing()) hideWeekView();
                else showWeekView();
                break;
        }
    }

    /**
     * 隐藏周次选择，此时需要将课表的日期恢复到本周并将课表切换到当前周
     */
    public void hideWeekView(){
        target = now_week;
        mWeekView.isShow(false);
        titleTextView.setTextColor(getResources().getColor(R.color.app_white));
        int cur = mTimetableView.curWeek();
        mTimetableView.onDateBuildListener()
                .onUpdateDate(cur, cur);
        mTimetableView.changeWeekOnly(cur);
    }

    public void showWeekView(){
        mWeekView.isShow(true);
        titleTextView.setTextColor(getResources().getColor(R.color.color_dark_text));
    }

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        findViewById(R.id.weekCourseLayout).setBackground(ColorManager.getMainBackground_full());
    }

    public void showAlert(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(WeekCourseActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showLoading(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(WeekCourseActivity.this)
                        .setText(message)
                        .enableProgress(true)
                        .setProgressColorRes(R.color.color_alerter_progress_bar)
                        .setDuration(10000)
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showAlert(final String title, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(WeekCourseActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

//    @Override
//    public void finish() {
//        super.finish();
////        overridePendingTransition(R.anim.down_in, R.anim.down_out);
//    }
}