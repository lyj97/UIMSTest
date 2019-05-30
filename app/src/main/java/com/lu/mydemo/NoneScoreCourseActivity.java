package com.lu.mydemo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;

import java.util.Arrays;
import java.util.List;

import Config.ColorManager;
import View.Fragment.ExamFragment;
import View.Fragment.NoneScoreCourseFragment;

public class NoneScoreCourseActivity extends AppCompatActivity {

    private ViewPager courseExamLayout;

    private TextView navigation_back;

    public NoneScoreCourseFragment noneScoreFragment;
    public ExamFragment examFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        setContentView(R.layout.activity_course_and_exam);

        courseExamLayout = findViewById(R.id.course_and_exam_layout_viewpager);

        navigation_back = findViewById(R.id.course_and_exam_layout_navigation_back_text);

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ColorManager.loadConfig(getApplicationContext(), NoneScoreCourseActivity.this);
        changeTheme();

        noneScoreFragment = new NoneScoreCourseFragment();

        examFragment = new ExamFragment();

        courseExamLayout.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), this, Arrays.asList(new String[]{"考试安排", "成绩未发布课程"})));

    }

    public NoneScoreCourseActivity getContext(){
        return this;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private Context mContext;
        private List<String> mData;

        public MyPagerAdapter(FragmentManager manager, Context context , List<String> list) {
            super(manager);
            mContext = context;
            mData = list;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return examFragment;
            }
            if(position == 1){
                return noneScoreFragment;
            }
            return null;
        }

        /**
         * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
         */
        @Override
        public void finishUpdate(ViewGroup container)
        {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mData.get(position);
        }
    }

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        findViewById(R.id.course_and_exam_layout).setBackground(ColorManager.getMainBackground_full());
    }

    public void showResponse(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NoneScoreCourseActivity.this, string, Toast.LENGTH_SHORT).show();
//                showAlert(string);
            }
        });
    }

    public void showAlert(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NoneScoreCourseActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showAlert(final String title, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NoneScoreCourseActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showWarningAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NoneScoreCourseActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .show();
            }
        });
    }

    public void showWarningAlert(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NoneScoreCourseActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .show();
            }
        });
    }

    public void showLoading(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NoneScoreCourseActivity.this)
                        .setText(message)
                        .enableProgress(true)
                        .setProgressColorRes(R.color.color_alerter_progress_bar)
                        .setDuration(10000)
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }
}
