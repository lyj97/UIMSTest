package com.lu.mydemo.Activity.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lu.mydemo.Activity.CourseScheduleChangeActivity;
import com.lu.mydemo.Activity.NewsActivity;
import com.lu.mydemo.Activity.NoneScoreCourseActivity;
import com.lu.mydemo.Activity.ScoreActivity;
import com.lu.mydemo.Activity.SettingActivity;
import com.lu.mydemo.Activity.WeekCourseActivity;
import com.lu.mydemo.R;
import com.lu.mydemo.Utils.Fragement.FragmentLabels;
import com.lu.mydemo.Utils.Time.TeachTimeTools;

/**
 * 创建时间: 2019/12/19 15:35 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class AllFunctionFragment extends Fragment implements View.OnClickListener{

    private FragmentLabels label = FragmentLabels.ALL_FUNCTION_FRAGMENT;

    private static AllFunctionFragment instance;

    private Button newsButton;
    private Button scoreButton;
    private Button courseExamButton;
    private Button courseTimeChangeButton;
    private Button courseWeeklyButton;
    private Button settingButton;

    private Context context;

    public static AllFunctionFragment newInstance(Context context) {
        if(instance == null) {
            instance = new AllFunctionFragment();
            instance.context = context;
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_function, container, false);

        newsButton = root.findViewById(R.id.news_button);
        scoreButton = root.findViewById(R.id.score_button);
        courseExamButton = root.findViewById(R.id.course_and_exam_button);
        courseTimeChangeButton = root.findViewById(R.id.course_time_change_button);
        courseWeeklyButton = root.findViewById(R.id.course_weekly_button);
        settingButton = root.findViewById(R.id.setting_button);

        newsButton.setOnClickListener(this);
        scoreButton.setOnClickListener(this);
        courseExamButton.setOnClickListener(this);
        courseTimeChangeButton.setOnClickListener(this);
        courseWeeklyButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        if(context == null) return;
        Intent intent = null;
        switch (v.getId()){
            case R.id.news_button : {
                intent = new Intent(context, NewsActivity.class);
                break;
            }
            case R.id.score_button : {
                intent = new Intent(context, ScoreActivity.class);
                break;
            }
            case R.id.course_and_exam_button : {
                intent = new Intent(context, NoneScoreCourseActivity.class);
                break;
            }
            case R.id.course_time_change_button : {
                intent = new Intent(context, CourseScheduleChangeActivity.class);
                break;
            }
            case R.id.course_weekly_button : {
                intent = new Intent(context, WeekCourseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("now_week", TeachTimeTools.now_week);
                bundle.putLong("weeks", TeachTimeTools.weeks);
                intent.putExtra("bundle", bundle);
                break;
            }
            case R.id.setting_button : {
                intent = new Intent(context, SettingActivity.class);
                break;
            }
        }
        if(intent != null){
            startActivity(intent);
        }
    }
}
