package com.lu.mydemo.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.tapadoo.alerter.Alerter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.ToolFor2045_Site.GetInternetInformation;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.Utils.Course.CourseScheduleChange;
import com.lu.mydemo.Utils.Thread.MyThreadController;
import com.lu.mydemo.View.PopWindow.*;

public class CourseScheduleChangeActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

    private Switch recommend_switch;
    private TextView doubt_text_view;
    private Button add_button;
    private AddCourseScheduleChangePopupWindow popupWindow;
    private RecommendCourseScheduleChangePopWindow recommendPopWindow;

    private TextView navigation_back;

    private Spinner spinner;
    private ArrayList<String> termList;

    public static HashMap<String, String> termName_TermId = new HashMap<>();
    public static HashMap<String, String> termId_termName = new HashMap<>();

    private static boolean isRecommendShowed = false;
    private static boolean isRecommendAllowed;

    private static JSONObject tempJsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_schedele_change);

        sharedPreferences = this.getSharedPreferences("CourseScheduleChange", Context.MODE_PRIVATE);

        swipeRecyclerView = findViewById(R.id.course_schedule_change_SwipeRecyclerView);

        recommend_switch = findViewById(R.id.course_schedule_change_recommend_switch);
        doubt_text_view = findViewById(R.id.course_schedule_change_recommend_doubt);
        add_button = findViewById(R.id.course_schedule_change_add_button);

        navigation_back = findViewById(R.id.course_schedule_change_navigation_back_text);

        spinner = findViewById(R.id.course_schedule_change_term_spinner);

        changeTheme();

        setSpinnerItems();

        isRecommendAllowed = sharedPreferences.getBoolean("isRecommendAllowed", false);

        if (isRecommendAllowed) recommend_switch.setChecked(true);
        else recommend_switch.setChecked(false);

        dataList = getChangeList();

        myAdapter = createAdapter();
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem collectItem = new SwipeMenuItem(CourseScheduleChangeActivity.this);
                collectItem.setImage(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
                collectItem.setText("删除");
                collectItem.setHeight(ViewGroup.MarginLayoutParams.MATCH_PARENT);
                collectItem.setBackground(getResources().getDrawable(R.drawable.shape_collect_swap_menu_background));

                rightMenu.addMenuItem(collectItem);
            }
        });
        swipeRecyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {

                menuBridge.closeMenu();

                MainActivity.setReLoadTodayCourse(true);
                if (CourseScheduleChange.delete(((String) dataList.get(adapterPosition).get("context1")).substring(0, 10))) {
                    Alerter.create(CourseScheduleChangeActivity.this)
                            .setText("已删除【" + dataList.get(adapterPosition).get("context1") + "】课程调整\n\n" +
                                    "如需撤销，请点击此处.")
                            .enableSwipeToDismiss()
                            .setProgressColorRes(R.color.color_alerter_progress_bar)
                            .setDuration(6000)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CourseScheduleChange.add(tempJsonObject);
                                    flushList();
                                    Alerter.hide();
                                }
                            })
                            .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                            .show();
                    tempJsonObject = new JSONObject();
                    tempJsonObject.put("title", dataList.get(adapterPosition).get("title"));
                    tempJsonObject.put("previousTime", ((String) dataList.get(adapterPosition).get("context1")).substring(0, 10));
                    tempJsonObject.put("changeTime", ((String) dataList.get(adapterPosition).get("context2")).length() > 0 ? ((String) dataList.get(adapterPosition).get("context2")).substring(0, 10) : "0000-00-00");
                    dataList.remove(adapterPosition);
                    myAdapter.notifyItemRemoved(adapterPosition);
                }
            }
        });

        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                if (dataList.get(adapterPosition).get("context2") == null || ((String) dataList.get(adapterPosition).get("context2")).length() == 0)
                    AlertCenter.showAlert(CourseScheduleChangeActivity.this, "", dataList.get(adapterPosition).get("context1") + "放假.\n请横向滑动来删除条目.");
                else
                    AlertCenter.showAlert(CourseScheduleChangeActivity.this, "", dataList.get(adapterPosition).get("context1") + " 上 " + dataList.get(adapterPosition).get("context2") + "的课.\n请横向滑动来删除条目.");
            }
        });
        swipeRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //实例化SelectPicPopupWindow
                popupWindow = new AddCourseScheduleChangePopupWindow(CourseScheduleChangeActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(CourseSchedeleChangeActivity.this, "确认添加", Toast.LENGTH_SHORT).show();
                        CourseScheduleChange.add(popupWindow.getTitle(), popupWindow.getPre_time(), popupWindow.getChange_time());
                        flushList();
                    }
                }, findViewById(R.id.course_schedule_change_add_layout).getHeight(), findViewById(R.id.course_schedule_change_add_layout).getWidth());
                popupWindow.setFocusable(true);
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //添加pop窗口关闭事件
                popupWindow.setOnDismissListener(new poponDismissListener());
//                popupWindow.setAnimationStyle(R.style.popwin_anim_style);
                //显示窗口
                popupWindow.showAtLocation(CourseScheduleChangeActivity.this.findViewById(R.id.course_schedule_change_add_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });

        doubt_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertCenter.showAlert(CourseScheduleChangeActivity.this, "关于\"推荐\"", "课程调整推荐由本软件作者手动维护，为你提供便捷的课程调整添加方式.\n\n" +
                        "本功能需要联网，但在网络请求中不会向外发送任何数据，你的数据安全不受影响.\n\n" +
                        "若不同意以上说明，请不要开启本功能.(本功能默认关闭)");
            }
        });

        recommend_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (recommend_switch.isChecked()) {
                    sharedPreferences.edit().putBoolean("isRecommendAllowed", true).apply();
                    if (!isRecommendShowed) getRecommend();
                } else {
                    sharedPreferences.edit().putBoolean("isRecommendAllowed", false).apply();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String term = termList.get(position);
//                Log.i("Term", term);
                if(term != null){
                    if(term.equals(UIMS.getTermName())){
                        Log.i("SetTerm", "Ignored! Term not change.");
                        MainActivity.setIsCourseNeedReload(false);
                        return;
                    }
                    JSONObject termJSON = UIMS.getTermJSON(term);
                    if(termJSON == null){
//                        AlertCenter.showWarningAlert(CourseScheduleChangeActivity.this, "ERROR", "TermJSON is null.");
                        List<Exception> exceptions = UIMS.getExceptions();
                        exceptions.add(new IllegalStateException("TermJSON is null."));
                        AlertCenter.showErrorAlertWithReportButton(CourseScheduleChangeActivity.this, "抱歉,数据出错!", exceptions, UIMS.getUser());
                        return;
                    }
//                    Log.i("TermJSON", termJSON.toString());
                    UIMS.setTeachingTerm(termJSON);
                    MainActivity.saveTeachingTerm();
                    Toast.makeText(CourseScheduleChangeActivity.this, "当前学期已设为：\t" + UIMS.getTermName(), Toast.LENGTH_SHORT).show();
                }
                else{
//                    Toast.makeText(CourseScheduleChangeActivity.this, "TermJSON is NULL!", Toast.LENGTH_SHORT).show();
                    Log.e("TermJSON", "TermJSON is NULL!");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!isRecommendAllowed || isRecommendShowed) return;
        else {
            getRecommend();
        }

//        opening = false;
    }

    private void getRecommend(){
        final GetInternetInformation change = new GetInternetInformation();
        isRecommendShowed = true;
        try {
            MyThreadController.commit(new Runnable() {
                @Override
                public void run() {
                    final JSONObject object = change.getRecommendChange();
                    if (object == null) {
//                        Log.i("GetRecommendCourseScheduleChange", "Object is NULL.");
                        return;
                    }
                    JSONArray array = object.getJSONArray("value");
                    if(array.size() == 0 || CourseScheduleChange.containsDate(MainActivity.context, array)) {
//                        Log.i("GetRecommendCourseScheduleChange", "Contains all in array.");
                        return;
                    }
                    recommendPopWindow = new RecommendCourseScheduleChangePopWindow(CourseScheduleChangeActivity.this, object, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CourseScheduleChange.add(object.getJSONArray("value"));
                                Toast.makeText(CourseScheduleChangeActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                flushList();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, findViewById(R.id.course_schedule_change_add_layout).getHeight(),  findViewById(R.id.course_schedule_change_add_layout).getWidth());
                    recommendPopWindow.prepareData(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recommendPopWindow.showAtLocation(CourseScheduleChangeActivity.this.findViewById(R.id.course_schedule_change_add_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                                }
                            });
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flushList(){
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                dataList = getChangeList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter = createAdapter();
                        swipeRecyclerView.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged(dataList);
                    }
                });
            }
        });

    }

    private List<Map<String, Object>> getChangeList(){

        HashMap<String, JSONObject> changeList = CourseScheduleChange.getPreviousTime_changeTime(MainActivity.context);

        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Locale.setDefault(Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        final String[] dayOfWeekName = new String[]{"","星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
        int day_of_week;

        try {
            Iterator<Map.Entry<String, JSONObject>> iterator = changeList.entrySet().iterator();

            JSONObject temp;
            String timeStr2;
            while(iterator.hasNext()) {
                temp = iterator.next().getValue();
                map = new HashMap<>();

                map.put("title", temp.getString("title"));

                cal.setTime(df.parse(temp.getString("previousTime")));
                day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
                if (day_of_week <= 0)
                    day_of_week = 7;
                map.put("context1", temp.getString("previousTime") + "(" + dayOfWeekName[day_of_week] + ")");

                timeStr2 = temp.getString("changeTime");
                if(timeStr2.equals("0000-00-00")){
                    map.put("context2", "");
                }
                else {
                    cal.setTime(df.parse(timeStr2));
                    day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    if (day_of_week <= 0)
                        day_of_week = 7;
                    map.put("context2", temp.getString("changeTime") + "(" + dayOfWeekName[day_of_week] + ")");
                }

                dataList.add(map);
            }

            Collections.sort(dataList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return ((String)o1.get("context1")).compareTo((String)o2.get("context1"));
                }
            });

            return dataList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setSpinnerItems(){
        if(MainActivity.isLocalValueLoaded){
            termList = getTermArray();
            if(termList == null || termList.size() == 0){
                termList = new ArrayList<>();
                termList.add("暂无学期数据\n请在首页点击“更新信息”按钮");
                spinner.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_spinner, R.id.select_text_item, termList));
            }
            else {
                spinner.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_spinner, R.id.select_text_item, termList));
                spinner.setSelection(termList.indexOf(UIMS.getTermName()));
            }
        }
        else{
            termList = new ArrayList<>();
            termList.add("暂无学期数据\n请在首页点击“更新信息”按钮");
            spinner.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_spinner, R.id.select_text_item, termList));
//            finish();
        }
    }

    public static ArrayList<String> getTermArray(){
        ArrayList<String> terms = new ArrayList<>();
        termId_termName = UIMS.getTermId_termName();
        Iterator<Map.Entry<String, String>> iterator = termId_termName.entrySet().iterator();
        Map.Entry<String, String> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            termName_TermId.put(entry.getValue(), entry.getKey());
            terms.add(entry.getValue());
        }
//        showResponse("termId_termName.size:\t" + termId_termName.size());
//        Log.i("termId_termName.size", "" + termId_termName.size());
        Collections.sort(terms, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        return terms;
    }

    protected MainAdapter createAdapter() {
        return new MainAdapter(this);
    }

    public void changeTheme(){
        super.changeTheme();
        findViewById(R.id.course_schedule_change_add_layout).setBackground(ColorManager.getMainBackground_full());
    }

    public void showResponse(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CourseScheduleChangeActivity.this, string, Toast.LENGTH_SHORT).show();
//                showAlert(string);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     * @author cg
     *
     */
    class poponDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }
}
