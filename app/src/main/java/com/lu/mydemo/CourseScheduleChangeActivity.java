package com.lu.mydemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.tapadoo.alerter.Alerter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ToolFor2045_Site.GetInternetInformation;
import Utils.Course.CourseScheduleChange;
import View.*;

public class CourseScheduleChangeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

    private Switch recommend_switch;
    private TextView doubt_text_view;
    private Button add_button;
    private AddCourseScheduleChangePopupWindow popupWindow;
    private RecommendCourseScheduleChangePopWindow recommendPopWindow;

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

        isRecommendAllowed = sharedPreferences.getBoolean("isRecommendAllowed", false);

        if(isRecommendAllowed) recommend_switch.setChecked(true);
        else recommend_switch.setChecked(false);

        dataList = getChangeList();

        myAdapter = createAdapter();
        OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {

            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                return false;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                // 此方法在Item在侧滑删除时被调用。
                // 从数据源移除该Item对应的数据，并刷新Adapter。
                int position = srcHolder.getAdapterPosition();

                if(CourseScheduleChange.delete(((String) dataList.get(position).get("context1")).substring(0, 10))){
                    Alerter.create(CourseScheduleChangeActivity.this)
                            .setText("已删除【" + dataList.get(position).get("context1") + "】课程调整\n\n" +
                                    "如需撤销，请点击此处.")
                            .enableSwipeToDismiss()
                            .setProgressColorRes(R.color.color_21)
                            .setDuration(6000)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CourseScheduleChange.add(tempJsonObject);
                                    flushList();
                                    Alerter.hide();
                                }
                            })
                            .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                            .show();
                    tempJsonObject = new JSONObject();
                    tempJsonObject.put("title", dataList.get(position).get("title"));
                    tempJsonObject.put("previousTime", ((String) dataList.get(position).get("context1")).substring(0, 10));
                    tempJsonObject.put("changeTime", ((String) dataList.get(position).get("context2")).length() > 0 ? ((String) dataList.get(position).get("context2")).substring(0, 10) : "0000-00-00");
                    dataList.remove(position);
                    myAdapter.notifyItemRemoved(position);
                }

            }
        };
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setItemViewSwipeEnabled(true);
        swipeRecyclerView.setOnItemMoveListener(mItemMoveListener);
        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                if (dataList.get(adapterPosition).get("context2") == null || ((String) dataList.get(adapterPosition).get("context2")).length() == 0)
                    showAlert("", dataList.get(adapterPosition).get("context1") + "放假.\n请横向滑动来删除条目.");
                else
                    showAlert("", dataList.get(adapterPosition).get("context1") + " 上 " + dataList.get(adapterPosition).get("context2") + "的课.\n请横向滑动来删除条目.");
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
                }, findViewById(R.id.course_schedule_change_add_layout).getHeight(),  findViewById(R.id.course_schedule_change_add_layout).getWidth());
                popupWindow.setFocusable(true);
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //添加pop窗口关闭事件
                popupWindow.setOnDismissListener(new poponDismissListener());
//                popupWindow.setAnimationStyle(R.style.popwin_anim_style);
                //显示窗口
                popupWindow.showAtLocation(CourseScheduleChangeActivity.this.findViewById(R.id.course_schedule_change_add_layout), Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });

        doubt_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert("关于\"推荐\"", "课程调整推荐由本软件作者手动维护，为你提供便捷的课程调整添加方式.\n\n" +
                        "本功能需要联网，但在网络请求中不会向外发送任何数据，你的数据安全不受影响.\n\n" +
                        "若不同意以上说明，请不要开启本功能.(本功能默认关闭)");
            }
        });

        recommend_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(recommend_switch.isChecked()){
                    sharedPreferences.edit().putBoolean("isRecommendAllowed", true).apply();
                    if(!isRecommendShowed) getRecommend();
                }
                else {
                    sharedPreferences.edit().putBoolean("isRecommendAllowed", false).apply();
                }
            }
        });

        if(!isRecommendAllowed || isRecommendShowed) return;
        else {
            getRecommend();
        }
    }

    private void getRecommend(){
        final GetInternetInformation change = new GetInternetInformation();
        isRecommendShowed = true;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final JSONObject object = change.getRecommendChange();
                    if (object == null) {
                        Log.i("GetRecommendCourseScheduleChange", "Object is NULL.");
                        return;
                    }
                    JSONArray array = object.getJSONArray("value");
                    if(array.size() == 0 || CourseScheduleChange.containsDate(LoginActivity.context, array)) {
                        Log.i("GetRecommendCourseScheduleChange", "Contains all in array.");
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
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flushList(){
        dataList = getChangeList();
        swipeRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);
    }

    private List<Map<String, Object>> getChangeList(){

        HashMap<String, JSONObject> changeList = CourseScheduleChange.getPreviousTime_changeTime(LoginActivity.context);

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

            dataList.sort(new Comparator<Map<String, Object>>() {
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

    protected MainAdapter createAdapter() {
        return new MainAdapter(this);
    }

    public void showAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(CourseScheduleChangeActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                        .show();
            }
        });
    }

    public void showLoading(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(CourseScheduleChangeActivity.this)
                        .setText(message)
                        .enableProgress(true)
                        .setProgressColorRes(R.color.color_21)
//                        .setIcon(R.drawable.ic_autorenew_black_24dp)
                        .setDuration(10000)
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                        .show();
            }
        });
    }

    public void showAlert(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(CourseScheduleChangeActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                        .show();
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
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }
}
