package com.lu.mydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import CJCX.CJCX;
import Config.ColorManager;
import Config.Version;
import ToolFor2045_Site.GetInternetInformation;
import UIMS.UIMS;
import UIMSTool.ClassSetConvert;
import UIMSTool.CourseJSONTransfer;
import Utils.Course.CourseScheduleChange;
import Utils.Score.ScoreInf;
import View.PopWindow.*;

public class MainActivity extends Activity {

    private LinearLayout activity_login;

    private TextView timeInformation;
    private TextView termInformation;
    private ListView courseList;

    private Button load_internet_inf_button;
    private Button get_save_button;
    private Button getNoneScoreCourseButton;
    private Button getNewsButton;
    private static SharedPreferences sp;
    private static final int PASSWORD_MIWEN = 0x81;

    private TextView enterWeekCourseTextView;

    private TextView about_text_view;

    private TextView test;

    private ScrollView login_main_view;
    private TextView linearLayoutView_down_text;

    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction ;

    private boolean isMainShow = true;
    private static boolean isInternetInformationShowed;
    private static boolean isCourseNeedReload = false;
    private static boolean reLoadTodayCourse = true;
    private boolean listHaveHeadFoot = false;

    public static long now_week;//当前周
    public static int weeks;//共多少周
    private int day_of_week;//今天周几
    public static final int MAX_VOCATION_WEEK_NUMBER = 9;//设置学期最大教学周数，用于时间正确性判断

    private int clickCount = 0;
    private TextView UIMSTest;

    public static UIMS uims;
//    public static boolean isLoginIn = false;
    public static boolean isLocalValueLoaded = false;
    public static Context context;

    String user;
    String pass;

    public LoginPopWindow popWindow;
    public LoginGetCourseSchedulePopupWindow courseSchedulePopupWindow;

    private static boolean acceptTestFun = false;

    private Handler myViewHandler;

//    private String theme = "blue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //找到相应的布局及控件
        setContentView(R.layout.activity_login);

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        context = this;
        timeInformation = findViewById(R.id.time_information);
        termInformation = findViewById(R.id.term_information);
        courseList = findViewById(R.id.course_list);
        UIMSTest = findViewById(R.id.UIMSTest);
        activity_login = findViewById(R.id.activity_login);
        load_internet_inf_button = findViewById(R.id.load_internet_information_button);
        get_save_button = findViewById(R.id.get_saved_button);
        getNoneScoreCourseButton = findViewById(R.id.load_none_score_course_information_button);
        getNewsButton = findViewById(R.id.get_news_button);

        enterWeekCourseTextView = findViewById(R.id.enterWeekCourseTextView);

        about_text_view = findViewById(R.id.login_goto_about_text_view);

        login_main_view = findViewById(R.id.login_main_view);
        linearLayoutView_down_text = findViewById(R.id.LinearLayoutView_down_text);

        //TODO TEST
        test = findViewById(R.id.login_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLocalValueLoaded){
                    startActivity(new Intent(MainActivity.this, PingjiaoActivity.class));
                }
                else{
                    showAlert("还没有已经保存的信息哦，点击\"更新信息\"再试试吧(*^_^*).");
                }
            }
        });

        /**
         * 改变主题颜色
         */
        loadColorConfig();
        changeTheme();

        isCourseNeedReload = sp.getBoolean("isCourseNeedReload", false);

        isMainShow = sp.getBoolean("isMainShow", isMainShow);

        mShowAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(500);

        myViewHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0 :{
                        if(!isMainShow) login_main_view.setVisibility(View.GONE);
                    }
                }
            }
        };

        if(!(timeInformation.getText().length() > 0)) timeInformation.setText("时间(首次查询后刷新)");

        if(!isMainShow){
            hideMainView();
        }

//        if(!isLoginIn) {
//            activity_login.requestLayout();
//        }
//        else{
//            loginSuccess();
//        }

        // i love you.
        if(sp.getBoolean("showEgg", true)){
            UIMSTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCount++;
                    if(clickCount == 7 && sp.getBoolean("showEgg", true)){
                        showAlert("", "I LOVE YOU. (❤ ω ❤)");
                        sp.edit().putBoolean("showEgg", false).apply();
                    }
                }
            });
        }

//        if(isLocalInformationAvailable()) loadLocalInformation(false);

        load_internet_inf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * TODO TEST 登录逻辑
                 */
                LoginPopWindow window = new LoginPopWindow(MainActivity.this, findViewById(R.id.activity_login).getHeight(), findViewById(R.id.activity_login).getWidth());
                window.setFocusable(true);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.showAtLocation(MainActivity.this.findViewById(R.id.activity_login), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                popWindow = window;

            }
        });

        get_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_save_button.setEnabled(false);
                get_save_button.setText("数据转换中，请稍候...");

                //TODO TEST 成绩查询不验证数据直接进入
//                CJCX.loadCJCXJSON(getApplicationContext());
//                ScoreActivity.context = getApplicationContext();
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                startActivity(intent);

//                if(isLocalInformationAvailable()){
////                    showLoading("数据转换中，请稍候...");
//                    loadLocalInformation(true);
//                }
//                else{
//                    showAlert("还没有已经保存的信息哦，点击\"更新信息\"再试试吧(*^_^*).");
//                    get_save_button.setEnabled(true);
//                    get_save_button.setText("成绩查询");
//                }
            }
        });

        getNoneScoreCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                myTestFunction();
                if(isLocalValueLoaded){
                    Intent intent = new Intent(MainActivity.this,NoneScoreCourseActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.up_in, R.anim.up_out);
                }
                else{
                    showAlert("还没有已经保存的信息哦，点击\"更新信息\"再试试吧(*^_^*).");
                }
//                overridePendingTransition(R.anim.up_in, R.anim.up_out);
            }
        });

        getNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                startActivity(intent);
            }
        });

        enterWeekCourseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLocalValueLoaded){
                    Intent intent = new Intent(MainActivity.this,WeekCourseActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong("now_week", now_week);
                    bundle.putLong("weeks", weeks);

                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.up_in, R.anim.up_out);
                }
                else{
                    showAlert("还没有已经保存的信息哦，点击\"更新信息\"再试试吧(*^_^*).");
                }
            }
        });

        about_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutView_down_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMainShow){
                    hideMainView();
                }
                else {
                    showMainView();
                }
            }
        });

        timeInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CourseScheduleChangeActivity.class);
                startActivity(intent);
            }
        });

        termInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CourseScheduleChangeActivity.class);
                startActivity(intent);
            }
        });

        isInternetInformationShowed = sp.getBoolean("isInternetInformationShowed", false);
        Log.i("GetInternetInformation", "isInternetInformationShowed:\t" + isInternetInformationShowed);
        if(!isInternetInformationShowed) getInternetInformation();

        acceptTestFun = sp.getBoolean("acceptTestFun", false);
        if(!acceptTestFun) {
            hideTestFun();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Alerter.hide();

        ColorManager.loadConfig(getApplicationContext(), this);
        changeTheme();

        // TODO TEST 忽略本地数据加载
//        Log.e("TEST", "Local information ignored for TEST!");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isLocalValueLoaded && isLocalInformationAvailable())
                    loadLocalInformation(false);
                else loadCourseInformation();
                CJCX.loadCJCXJSON(getApplicationContext());
                CJCX.loadCJCXTermJSON(getApplicationContext());
                ScoreActivity.context = getApplicationContext();
                ScoreInf.loadScoreList();
//                Log.i("TEST:ScoreInf", ScoreInf.getDataList().toString());
            }
        }).start();

        if(isLocalValueLoaded && isCourseNeedReload){
            showWarningAlertWithCancel_OKButton("需要刷新课程信息", "当前学期已经改变，请刷新本地课程信息。");
        }

        if(reLoadTodayCourse) loadCourseInformation();

        if(!acceptTestFun) hideTestFun();

    }

    @Override
    protected void onStop() {
        super.onStop();
        reLoadTodayCourse = true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!hasFocus) {
            get_save_button.setText("成绩查询");
            get_save_button.setEnabled(true);
        }
    }

    protected void hideTestFun(){
        test.setVisibility(View.GONE);
    }

    public void hideMainView(){
        login_main_view.startAnimation(mHiddenAction);
        login_main_view.setVisibility(View.INVISIBLE);
        linearLayoutView_down_text.setText("⇧显示下方区域");
        isMainShow = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    myViewHandler.sendEmptyMessage(0);//刷新页面
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        sp.edit().putBoolean("isMainShow", isMainShow).apply();
    }

    public void showMainView(){
        login_main_view.startAnimation(mShowAction);
        login_main_view.setVisibility(View.VISIBLE);
        linearLayoutView_down_text.setText("⇩隐藏下方区域");
        isMainShow = true;
        sp.edit().putBoolean("isMainShow", isMainShow).apply();
    }

    public void loginSuccess() {
        sp.edit().putString("CurrentUserInfoJSON", UIMS.getCurrentUserInfoJSON().toString()).apply();
        sp.edit().putString("TermJSON", UIMS.getTermJSON().toString()).apply();
        sp.edit().putString("TeachingTermJSON", UIMS.getTeachingTermJSON().toString()).apply();

        final JSONObject informationJSON = UIMS.getInformationJSON();
        final JSONObject teachingTermJSON = UIMS.getTeachingTermJSON();

//        isLoginIn = true;

        try {
            JSONObject value;
            try{
                value = teachingTermJSON.getJSONArray("value").getJSONObject(0);
            }catch (JSONException e){
                value = teachingTermJSON;
            }
            weeks = value.getInt("weeks");
            loadTime();
            getCourseSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reLogin(){
        reLogin(false);
    }

    public void reLogin(final boolean showNotice){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.hide();
                if(showNotice) showAlert("如需登录其他账号，请删除本应用数据，以避免出现数据错误.\n\n" +
                        "删除方法：设置->应用->UIMSTest->存储->清除数据." );
//                isLoginIn = false;

                getNoneScoreCourseButton.requestLayout();
                activity_login.requestLayout();

            }
        });
    }

    public void loadSuccess(){
        sp.edit().putString("ScoreJSON", UIMS.getScoreJSON().toString()).apply();
        sp.edit().putString("CourseJSON", UIMS.getCourseJSON().toString()).apply();
        sp.edit().putString("StudentJSON", UIMS.getStudentJSON().toString()).apply();
        sp.edit().putString("CourseTypeJSON", UIMS.getCourseTypeJSON().toString()).apply();
        sp.edit().putString("CourseSelectTypeJSON", UIMS.getCourseSelectTypeJSON().toString()).apply();
        sp.edit().putString("ScoreStatisticsJSON", UIMS.getScoreStatisticsJSON().toString()).apply();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAlert("信息刷新成功", "以后大部分功能就不需要校园网啦!\n" +
                        "祝使用愉快呀！");
            }
        });

        loadLocalInformationSuccess(null);
        getCourseSuccess();
    }

    public void loadCourseInformation(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                loadTime();
                getCourseSuccess();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CourseJSONTransfer.transferCourseList(UIMS.getCourseJSON(), true);
                    }
                }).start();
            }
        }).start();

    }

    private void loadLocalInformation(final boolean show) {

        if (!isLocalValueLoaded) {
            showLoading("正在加载本地数据...");

            UIMS.setCurrentUserInfoJSON(JSONObject.fromObject(sp.getString("CurrentUserInfoJSON", "")));
            UIMS.setScoreJSON(JSONObject.fromObject(sp.getString("ScoreJSON", "")));
            UIMS.setStudentJSON(JSONObject.fromObject(sp.getString("StudentJSON", "")));
            UIMS.setCourseTypeJSON(JSONObject.fromObject(sp.getString("CourseTypeJSON", "")));
            UIMS.setCourseSelectTypeJSON(JSONObject.fromObject(sp.getString("CourseSelectTypeJSON", "")));
            UIMS.setScoreStatisticsJSON(JSONObject.fromObject(sp.getString("ScoreStatisticsJSON", "")));
            UIMS.setTermJSON(JSONObject.fromObject(sp.getString("TermJSON", "")));
            UIMS.setTeachingTerm(JSONObject.fromObject(sp.getString("TeachingTermJSON", "")));
            UIMS.setCourseJSON(JSONObject.fromObject(sp.getString("CourseJSON", "")));

            loadCourseInformation();
            CJCX.loadCJCXJSON(getApplicationContext());
            CJCX.loadCJCXTermJSON(getApplicationContext());
            ScoreActivity.context = getApplicationContext();
//            ScoreActivity.getScoreList();

            ScoreInf.loadScoreList();
//            Log.i("TEST:ScoreInf", ScoreInf.getDataList().toString());

        }

        final JSONObject teachingTermJSON = UIMS.getTeachingTermJSON();

        try {
            JSONObject value;
            try {
                value = teachingTermJSON.getJSONArray("value").getJSONObject(0);
            } catch (JSONException e) {
                value = teachingTermJSON;
            }
            weeks = value.getInt("weeks");
            loadTime();
//                    getCourseSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (show) {
//                                long startTime = System.currentTimeMillis();
                        Bundle bundle = new Bundle();
//                                bundle.putLong("startTime", startTime);
                        Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
//                                intent.putExtra("bundle", bundle);
                        startActivity(intent);
//                            overridePendingTransition(R.anim.up_in, R.anim.up_out);
                    }
                } catch (Exception e) {
                    showWarningAlert(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        loadLocalInformationSuccess(null);

    }

    private void loadLocalInformationSuccess(final String get_save_button_text){
        isLocalValueLoaded = true;
        if(isCourseNeedReload){
            showWarningAlertWithCancel_OKButton("需要刷新课程信息", "当前学期已经改变，请刷新本地课程信息。");
        }
        if(get_save_button_text != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Alerter.hide();
                    get_save_button.setText(get_save_button_text);
                }
            });
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Alerter.hide();
                }
            });
        }
    }

    private void getCourseSuccess(){
        try {
            if(!reLoadTodayCourse) return;
            final List<Map<String, Object>> datalist = getCourseList();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("Login", "CourseListSize:\t" + datalist.size());
                    courseList.setAdapter(new noCourseBetterAdapter(context, datalist, R.layout.today_course_list_item, new String[]{"index", "title", "context1"}, new int[]{R.id.get_none_score_course_title_index, R.id.get_none_score_course_title, R.id.get_none_score_course_context1}));
                    if(!listHaveHeadFoot) {
                        courseList.addHeaderView(new ViewStub(context));
                        courseList.addFooterView(new ViewStub(context));
                        listHaveHeadFoot = true;
                    }
                }
            });
            reLoadTodayCourse = false;
        } catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    courseList.setAdapter(new noCourseBetterAdapter(context, getCourseListNotice("暂无课程信息\n请点击\"更新信息\"登录并刷新本地数据."), R.layout.today_course_list_item, new String[]{"index", "title", "context1"}, new int[]{R.id.get_none_score_course_title_index, R.id.get_none_score_course_title, R.id.get_none_score_course_context1}));
                    if(!listHaveHeadFoot) {
                        courseList.addHeaderView(new ViewStub(context));
                        courseList.addFooterView(new ViewStub(context));
                        listHaveHeadFoot = true;
                    }
                    Log.i("GetCourse", "设置提示信息");
                }
            });
        }
    }

    private void loadTime(){
        try {
            JSONObject teachingTermJSON = UIMS.getTeachingTermJSON();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject value;
            try{
                value = teachingTermJSON.getJSONArray("value").getJSONObject(0);
            }catch (JSONException e){
                value = teachingTermJSON;
            }
            final String now_time = df.format(new Date());
            final String termName = value.getString("termName");
            long startDate = df.parse(value.getString("startDate").split("T")[0]).getTime();
//            long vacationDate = df.parse(value.getString("vacationDate").split("T")[0]).getTime();
            long now = df.parse(now_time).getTime();

            Locale.setDefault(Locale.CHINA);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            final String[] dayOfWeekName = new String[]{"","星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
            day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (day_of_week <= 0)
                day_of_week = 7;

            Log.i("loadTime", "day_of_week:\t" + day_of_week);

            now_week = (now - startDate) / (1000 * 3600 * 24 * 7) + 1;
//            weeks = (int) (vacationDate - startDate / (1000 * 3600 * 24 * 7));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeInformation.setTextColor(Color.BLACK);
                    termInformation.setTextColor(Color.BLACK);
                    timeInformation.setText(now_time + " " + dayOfWeekName[day_of_week]);
                    if(now_week < 0 || now_week > MAX_VOCATION_WEEK_NUMBER + weeks){
                        termInformation.setText(termName + "\n 当前学期可能有误");
                        termInformation.setTextColor(Color.RED);
                    }
                    else {
                        if (now_week <= weeks)
                            termInformation.setText(termName + "\n 第 " + now_week + " 周(共 " + weeks + " 周)");
                        else termInformation.setText(termName + "\n本学期已结束,假期快乐!");
                    }
                }
            });

        } catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeInformation.setText("");
                }
            });
        }
    }

    private void loadInformation() {
        //开启线程发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    showLoading("查询中...");
//                    showLoading("查询成绩统计...");
                    if (uims.getScoreStatistics()) {
//                        showResponse("查询成绩统计成功！");
//                        showLoading("查询成绩...");
                        if (uims.getRecentScore()) {
//                            showResponse("查询成绩成功！");
//                            showAlert("", "查询成绩成功！");
                            Log.i("Login", "getRecentScoreSucceed!");
//                            loadSuccess();
                        }
                        else{
                            showResponse("Login failed!");
                            reLogin();
                            return;
                        }
//                        showLoading("查询课程中...");
                        if(uims.getCourseSchedule()){
//                            showResponse("查询课程成功！");
//                            showAlert("", "查询课程成功！");
                            Log.i("Login", "getCourseScheduleSucceed!");
                            loadSuccess();
                        }
                        else{
                            showResponse("Login failed!");
                            reLogin();
                            return;
                        }
                    }
                    else{
                        showResponse("Login failed!");
                        reLogin();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showResponse("Login failed!");
                    reLogin();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            load_internet_inf_button.setText("更新信息");
                            load_internet_inf_button.setEnabled(true);
                            load_internet_inf_button.setBackground(getResources().getDrawable(R.drawable.button_internet_background));
                        }
                    });
                }
            }
        }).start();
    }

    private boolean isLocalInformationAvailable(){
        return (sp.contains("CurrentUserInfoJSON") && sp.contains("ScoreJSON") && sp.contains("CourseJSON") && sp.contains("ScoreStatisticsJSON") && sp.contains("StudentJSON") && sp.contains("CourseTypeJSON") && sp.contains("CourseSelectTypeJSON") && sp.contains("TermJSON")  && sp.contains("TeachingTermJSON"));
    }

    private List<Map<String, Object>> getCourseListNotice(String str){
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("index", "");
        map.put("title", str);
        map.put("context1", "");
        dataList.add(map);
        return dataList;
    }

    private List<Map<String, Object>> getCourseList() throws Exception{

        Log.i("GetCourse", "教学周:\t" + now_week);
        Log.i("GetCourse", "星期:\t" + day_of_week);

        if(day_of_week == 0 || now_week == 0) throw new IllegalAccessException("教学周或星期为0（未初始化）！");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String now_time = df.format(new Date());

        if(CourseScheduleChange.containsDate(this, now_time)){
            return getCourseList(CourseScheduleChange.getDate(this, now_time));
        }

        ClassSetConvert classSetConvert = new ClassSetConvert();
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map;

        JSONObject allCourseJSON = UIMS.getCourseJSON();

        JSONArray json_courses = allCourseJSON.getJSONArray("value");

        Log.i("GetCourse", "课程数量:\t" + json_courses.size());

        JSONObject teachClassMaster;

        JSONArray lessonSchedules;
        JSONArray lessonTeachers;
        JSONObject teacher;
        String teacherName;
        JSONObject lessonSegment;
        String courName;

        JSONObject timeBlock;
        int classSet;
        int dayOfWeek;
        int beginWeek;
        int endWeek;
        int[] start_end;
        String weekOddEven = "";
        JSONObject classroom;
        String classroomName;

        try {

            for(int i=0; i<json_courses.size(); i++){

                teachClassMaster = json_courses.getJSONObject(i).getJSONObject("teachClassMaster");

                lessonSegment = teachClassMaster.getJSONObject("lessonSegment");
                lessonSchedules = teachClassMaster.getJSONArray("lessonSchedules");
                lessonTeachers = teachClassMaster.getJSONArray("lessonTeachers");

                courName = lessonSegment.getString("fullName");

                teacherName = lessonTeachers.getJSONObject(0).getJSONObject("teacher").getString("name");

                for (int j = 0; j < lessonSchedules.size(); j++) {

                    map = new HashMap<>();

                    timeBlock = lessonSchedules.getJSONObject(j).getJSONObject("timeBlock");
                    classroom = lessonSchedules.getJSONObject(j).getJSONObject("classroom");
                    classroomName = classroom.getString("fullName");

                    classSet = timeBlock.getInt("classSet");
                    dayOfWeek = timeBlock.getInt("dayOfWeek");
                    beginWeek = timeBlock.getInt("beginWeek");
                    endWeek = timeBlock.getInt("endWeek");

                    if(!(beginWeek <= now_week && now_week <= endWeek)) continue;
                    if(dayOfWeek != day_of_week) continue;

                    try {
                        weekOddEven = timeBlock.getString("weekOddEven");
                    } catch (Exception e) {
//                    e.printStackTrace();
                    }

                    switch (weekOddEven.toUpperCase()){
                        case "":{
                            break;
                        }
                        case "E":{
                            //双周
                            if(now_week % 2 != 0) continue;
                            break;
                        }
                        case "O":{
                            //单周
                            if(now_week % 2 == 0) continue;
                            break;
                        }
                    }

                    start_end = classSetConvert.mathStartEnd(classSet);

                    map.put("index", start_end[0] + " - " + start_end[1] + "节");
                    map.put("title", courName);
                    map.put("context1", classroomName);
//                    map.put("type", selectType);

                    dataList.add(map);

                    weekOddEven = "";

                }
            }

            Log.i("GetCourse", "今日课程数量:\t" + dataList.size());

            if(dataList.size() == 0){
                map = new HashMap<>();

                map.put("index", "");
                map.put("title", "今天没课呀~");
                map.put("context1", "");
//                map.put("type", "");

                dataList.add(map);
            }
            else{
                Collections.sort(dataList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        return ((String)o1.get("index")).compareTo((String)o2.get("index"));
                    }
                });
            }

            return dataList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Map<String, Object>> getCourseList(final String date) throws Exception{

        if(date.equals("0000-00-00")) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String now_time = df.format(new Date());
            final String[] dayOfWeekName = new String[]{"","星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeInformation.setTextColor(getResources().getColor(R.color.app_red));
                    timeInformation.setText(now_time.substring(2) + " " + dayOfWeekName[day_of_week] + " (第 " + now_week + " 周)\n" +
                            "今天放假啦");
                }
            });

            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("index", "");
            map.put("title", "今天放假啦o(*￣▽￣*)o");
            map.put("context1", "");
//            map.put("type", "");
            dataList.add(map);
            return dataList;
        }

        //递归调用【！会引起逻辑错误】
//        if(CourseScheduleChange.containsDate(this, date)){
//            return getCourseList(CourseScheduleChange.getDate(this, date));
//        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long time = df.parse(date).getTime();

        Locale.setDefault(Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        JSONObject teachingTermJSON = UIMS.getTeachingTermJSON();
        JSONObject value;
        try {
            value = teachingTermJSON.getJSONArray("value").getJSONObject(0);
        }catch (Exception e){
            e.printStackTrace();
            value = teachingTermJSON;
        }
        long startTime = df.parse(value.getString("startDate").split("T")[0]).getTime();
        final long temp_week = (time - startTime) / (1000 * 3600 * 24 * 7) + 1;

        cal = Calendar.getInstance();
        cal.setTime(df.parse(date));
        int temp_day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (temp_day_of_week <= 0)
            temp_day_of_week = 7;
        final int temp_day_of_week_1 = temp_day_of_week;

        final String now_time = df.format(new Date());
        final String[] dayOfWeekName = new String[]{"","星期一","星期二","星期三","星期四","星期五","星期六","星期日"};

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeInformation.setTextColor(getResources().getColor(R.color.app_red));
                timeInformation.setText(now_time.substring(2) + " " + dayOfWeekName[day_of_week] + " (第 " + now_week + " 周)\n" +
                        "课程临时调整：\n" +
                        date.substring(2) + " " + dayOfWeekName[temp_day_of_week_1] + " (第 " + temp_week + " 周)");
            }
        });

        return getCourseList(temp_day_of_week_1, (int) temp_week);

    }

    private List<Map<String, Object>> getCourseList(int day_of_week, int now_week) throws Exception{

        Log.i("GetCourse", "教学周:\t" + now_week);
        Log.i("GetCourse", "星期:\t" + day_of_week);

        if(day_of_week == 0 || now_week == 0) throw new IllegalAccessException("教学周或星期为0！");

        ClassSetConvert classSetConvert = new ClassSetConvert();
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map;

        JSONObject allCourseJSON = UIMS.getCourseJSON();

        JSONArray json_courses = allCourseJSON.getJSONArray("value");

        Log.i("GetCourse", "课程数量:\t" + json_courses.size());

        JSONObject teachClassMaster;

        JSONArray lessonSchedules;
        JSONArray lessonTeachers;
        JSONObject teacher;
        String teacherName;
        JSONObject lessonSegment;
        String courName;

        JSONObject timeBlock;
        int classSet;
        int dayOfWeek;
        int beginWeek;
        int endWeek;
        int[] start_end;
        String weekOddEven = "";
        JSONObject classroom;
        String classroomName;

        try {

            for(int i=0; i<json_courses.size(); i++){

                teachClassMaster = json_courses.getJSONObject(i).getJSONObject("teachClassMaster");

                lessonSegment = teachClassMaster.getJSONObject("lessonSegment");
                lessonSchedules = teachClassMaster.getJSONArray("lessonSchedules");
                lessonTeachers = teachClassMaster.getJSONArray("lessonTeachers");

                courName = lessonSegment.getString("fullName");

                teacherName = lessonTeachers.getJSONObject(0).getJSONObject("teacher").getString("name");

                for (int j = 0; j < lessonSchedules.size(); j++) {

                    map = new HashMap<>();

                    timeBlock = lessonSchedules.getJSONObject(j).getJSONObject("timeBlock");
                    classroom = lessonSchedules.getJSONObject(j).getJSONObject("classroom");
                    classroomName = classroom.getString("fullName");

                    classSet = timeBlock.getInt("classSet");
                    dayOfWeek = timeBlock.getInt("dayOfWeek");
                    beginWeek = timeBlock.getInt("beginWeek");
                    endWeek = timeBlock.getInt("endWeek");

                    if(!(beginWeek <= now_week && now_week <= endWeek)) continue;
                    if(dayOfWeek != day_of_week) continue;

                    try {
                        weekOddEven = timeBlock.getString("weekOddEven");
                    } catch (Exception e) {
//                    e.printStackTrace();
                    }

                    switch (weekOddEven.toUpperCase()){
                        case "":{
                            break;
                        }
                        case "E":{
                            //双周
                            if(now_week % 2 != 0) continue;
                            break;
                        }
                        case "O":{
                            //单周
                            if(now_week % 2 == 0) continue;
                            break;
                        }
                    }

                    start_end = classSetConvert.mathStartEnd(classSet);

                    map.put("index", start_end[0] + " - " + start_end[1] + "节");
                    map.put("title", courName);
                    map.put("context1", classroomName);
//                    map.put("type", selectType);

                    dataList.add(map);

                    weekOddEven = "";

                }
            }

            Log.i("GetCourse", "今日课程数量:\t" + dataList.size());

            if(dataList.size() == 0){
                map = new HashMap<>();

                map.put("index", "");
                map.put("title", "今天没课呀o(*￣▽￣*)o");
                map.put("context1", "");
//                map.put("type", "");

                dataList.add(map);
            }
            else{
                Collections.sort(dataList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        return ((String)o1.get("index")).compareTo((String)o2.get("index"));
                    }
                });
            }

            return dataList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getInternetInformation(){
        Log.i("GetInternetInformation", "GetInternetInformation");
        final GetInternetInformation getInf = new GetInternetInformation();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final JSONObject object = getInf.getVersionInformation();

                    if (object == null) {
                        Log.i("GetInternetInformation", "Object is NULL.");
                        return;
                    }

                    try {
                        int internetVersion = object.getInt("VersionCode");
                        Log.i("Version", "" + internetVersion);
                        // TODO Version Control
                        if(internetVersion <= Version.getVersionCode()){
                            return;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            InternetInformationPopupWindow informationPopWindow = new InternetInformationPopupWindow(MainActivity.this, object, findViewById(R.id.activity_login).getHeight(), findViewById(R.id.activity_login).getWidth());
                            informationPopWindow.showAtLocation(MainActivity.this.findViewById(R.id.activity_login), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isInternetInformationShowed = true;
//            sp.edit().putBoolean("isInternetInformationShowed", true).apply();
        }
    }

    private void changeTheme(){
        Log.i("Theme", "Change theme.");
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        activity_login.setBackground(ColorManager.getMainBackground_full());
        getNewsButton.setBackground(ColorManager.getLocalInformationButtonBackground());
        get_save_button.setBackground(ColorManager.getLocalInformationButtonBackground());
        getNoneScoreCourseButton.setBackground(ColorManager.getLocalInformationButtonBackground());
        load_internet_inf_button.setBackground(ColorManager.getInternetInformationButtonBackground_full());
    }

    public static void loadColorConfig(){
        ColorManager.loadColorConfig(context.getApplicationContext());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ColorManager.loacColorCofig(context.getApplicationContext());
//            }
//        }).start();
    }

    private void changeStatusBarTextColor(boolean isBlack) {
        if (isBlack) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//恢复状态栏白色字体
        }
    }

    public static void setReLoadTodayCourse(boolean reLoadTodayCourse) {
        MainActivity.reLoadTodayCourse = reLoadTodayCourse;
    }

    public static void setIsCourseNeedReload(boolean isCourseNeedReload) {
        MainActivity.isCourseNeedReload = isCourseNeedReload;
        sp.edit().putBoolean("isCourseNeedReload", isCourseNeedReload).apply();
    }

    public static void saveTeachingTerm(){
        sp.edit().putString("TeachingTermJSON", UIMS.getTeachingTermJSON().toString()).apply();
        MainActivity.setIsCourseNeedReload(true);
    }

    public static void saveCourseJSON(){
        sp.edit().putString("CourseJSON", UIMS.getCourseJSON().toString()).apply();
    }

    public static void saveScoreJSON(){
        sp.edit().putString("ScoreJSON", UIMS.getScoreJSON().toString()).apply();
        sp.edit().putString("ScoreStatisticsJSON", UIMS.getScoreStatisticsJSON().toString()).apply();
    }

    public static boolean isAcceptTestFun() {
        return acceptTestFun;
    }

    public static void setAcceptTestFun(boolean acceptTestFun) {
        MainActivity.acceptTestFun = acceptTestFun;
        sp.edit().putBoolean("acceptTestFun", MainActivity.acceptTestFun).apply();
    }

    private void myTestFunction(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    user = "54160907";
                    pass = "225577";

                    uims = new UIMS(user, pass);
                    showLoading("正在连接到UIMS教务系统...");
                    if (uims.connectToUIMS()) {
                        showLoading("正在登录...");
                        if (uims.login()) {
                            showLoading("正在加载用户信息...");
                            if (uims.getCurrentUserInfo()) {
                                showAlert("", "欢迎您, " + uims.getNickName() + " ." + "\n" +
                                        "您是UIMS系统第 " + uims.getLoginCounter() + " 位访问者.");
                                if(uims.getUserInformation()) loginSuccess();
                                else{
                                    showResponse("Login failed!");
                                    return;
                                }
                                if(!uims.getTermArray()){
                                    showResponse("Login failed!");
                                    return;
                                }
                                if (uims.getScoreStatistics()) {
//                                    showResponse("查询成绩统计成功！");
                                    showLoading("查询成绩中，请稍侯...");
                                    if (uims.getRecentScore()) {
//                                        showResponse("查询成绩成功！");
                                        Log.i("Login", "getRecentScoreSucceed!");
                                    }
                                    else{
                                        showResponse("Login failed!");
                                        reLogin();
                                        return;
                                    }
                                    if(uims.getTermArray()){
//                                        showResponse("查询学期列表成功！");
//                                        loadSuccess();
                                        Log.i("Login", "getTermArraySucceed!");
                                    }
                                    else{
                                        showResponse("Login failed!");
                                        reLogin();
                                        return;
                                    }
                                    if(uims.getCourseHistory("135")){
                                        showResponse("查询历史选课成功！(term: 135)");
                                        Log.i("Login", "getCourseHistorySucceed!");
                                    }
                                    else{
                                        showResponse("Login failed!");
                                        reLogin();
                                        return;
                                    }
                                    if(uims.getCourseHistory("134")){
                                        showResponse("查询历史选课成功！(term: 134)");
                                        Log.i("Login", "getCourseHistorySucceed!");
                                    }
                                    else{
                                        showResponse("Login failed!");
                                        reLogin();
                                        return;
                                    }
                                    if(uims.getCourseHistory("133")){
                                        showResponse("查询历史选课成功！(term: 133)");
                                        Log.i("Login", "getCourseHistorySucceed!");
                                    }
                                    else{
                                        showResponse("Login failed!");
                                        reLogin();
                                        return;
                                    }
                                }
                                else{
                                    showResponse("Login failed!");
                                    reLogin();
                                }
                            }
                            else{
//                                showResponse("Login failed!");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alerter.hide();
                                        showWarningAlert("", "登录失败，请检查用户名和密码是否正确.\n\n" +
                                                "教务账号：\t您的教学号\n" +
                                                "教务密码：\t默认密码为身份证号后六位");
//                                        button_login.setText("重新登录");
//                                        button_login.setEnabled(true);
//                                        button_login.setBackground(getResources().getDrawable(R.drawable.button_internet_background));
                                        return;
                                    }
                                });
                            }
                        }
                        else{
//                            showResponse("Login failed!");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Alerter.hide();
                                    showWarningAlert("", "登录失败，请检查是否连接校园网！");
//                                    button_login.setText("重新登录");
//                                    button_login.setEnabled(true);
//                                    button_login.setBackground(getResources().getDrawable(R.drawable.button_internet_background));
                                    return;
                                }
                            });
                        }
                    }
                    else{
                        showResponse("Login failed!");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void dismissPopWindow(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popWindow.dismiss();
            }
        });
    }

    public void dismissCourseSchedulePopWindow(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                courseSchedulePopupWindow.dismiss();
            }
        });
    }

    public void showResponse(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.hide();
                if (string.toLowerCase().contains("failed")) {
                    showWarningAlert("", "获取数据失败，请稍后重试.");
//                    button_login.setText("重新登录");
//                    button_login.setEnabled(true);
//                    button_login.setBackground(getResources().getDrawable(R.drawable.button_internet_background));
                    return;
                }
                Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
//        showAlert(string);
            }
        });
    }

    public void showLoading(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(MainActivity.this)
                        .setText(message)
                        .enableProgress(true)
                        .setDismissable(false)
                        .setProgressColorRes(R.color.color_alerter_progress_bar)
                        .setDuration(Integer.MAX_VALUE)
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(MainActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showAlert(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(MainActivity.this)
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
                Alerter.create(MainActivity.this)
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
                Alerter.create(MainActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .show();
            }
        });
    }

    public void showWarningAlertWithCancel_OKButton(final String title, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(MainActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .addButton("取消", R.style.AlertButton, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Alerter.hide();
                            }
                        })
                        .addButton("更新", R.style.AlertButton, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoginGetCourseSchedulePopupWindow window = new LoginGetCourseSchedulePopupWindow(MainActivity.this, UIMS.getTermName(), findViewById(R.id.activity_login).getHeight(), findViewById(R.id.activity_login).getWidth());
                                window.setFocusable(true);
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                window.showAtLocation(MainActivity.this.findViewById(R.id.activity_login), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                                courseSchedulePopupWindow = window;
                                Alerter.hide();
                            }
                        })
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .enableSwipeToDismiss()
                        .setDuration(Integer.MAX_VALUE)
                        .show();
            }
        });
    }

    class noCourseBetterAdapter extends SimpleAdapter {
        List<? extends Map<String, ?>> mdata;

        public noCourseBetterAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                            int[] to) {
            super(context, data, resource, from, to);
            this.mdata = data;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LinearLayout.inflate(getBaseContext(), R.layout.today_course_list_item, null);
            }//这个TextView是R.layout.list_item里面的，修改这个字体的颜色
            TextView textView = (TextView) convertView.findViewById(R.id.get_none_score_course_title_index);
            //获取每次进来时 mData里面存的值  若果相同则变颜色
            //根据Key值取出装入的数据，然后进行比较
            String ss=(String)mdata.get(position).get("index");

            if(ss == null || !(ss.length() > 0)) {
                textView.getLayoutParams().width = 0;
                textView.setWidth(0);
            }

            //Log.i("TAG", Integer.toString(position));
            //Log.i("TAG", (String) mData.get(position).get("text"));
            return super.getView(position, convertView, parent);
        }
    }

}
