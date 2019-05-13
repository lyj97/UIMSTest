package com.lu.mydemo;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import PopWindow.LoginGetSelectCoursePopWindow;
import PopWindow.LoginPopWindow;
import UIMS.UIMS;

public class NoneScoreCourseActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    Spinner spinner;

    Button searchButton;
    Button deleteSavedCourseInformationButton;
    ListView listView;

    LinearLayout dataArea;

    int delete_local_course_information_button_layout_hight;

    public HashMap<String, String> termName_TermId = new HashMap<>();
    public HashMap<String, String> termId_termName = new HashMap<>();

    public LoginGetSelectCoursePopWindow popWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        setContentView(R.layout.activity_none_score_course);

        sharedPreferences = this.getSharedPreferences("CourseHistory", Context.MODE_PRIVATE);
        dataArea = findViewById(R.id.nono_score_course_data_area);
        spinner = findViewById(R.id.get_none_score_course_term_spinner);
        searchButton = findViewById(R.id.get_none_score_course_button);
        deleteSavedCourseInformationButton = findViewById(R.id.delete_saved_course_information_button);
        listView = findViewById(R.id.get_none_score_course_list_view);

        delete_local_course_information_button_layout_hight = deleteSavedCourseInformationButton.getLayoutParams().height;

        deleteSavedCourseInformationButton.setVisibility(View.INVISIBLE);
        deleteSavedCourseInformationButton.getLayoutParams().height = 0;
        deleteSavedCourseInformationButton.requestLayout();

        setSpinnerItems();

        searchButton.setOnClickListener(new View.OnClickListener() {
            String termName = "";
            String termId = "";

            @Override
            public void onClick(View v) {
                try{
                    termName = spinner.getSelectedItem().toString();
                    termId = termName_TermId.get(termName);

                    if(termName == null || termId == null || !(termName.length() > 0) || !(termId.length() > 0)){
                        showWarningAlert("数据出错");
                        return;
                    }

                    /**
                     * TODO TEST
                     */

                    if(sharedPreferences.contains("CourseHistory_" + termId)){
                        showLoading("由本地加载【" + termName + "】数据中，请稍候...");
                        UIMS.setCourseHistoryJSON(JSONObject.fromObject(sharedPreferences.getString("CourseHistory_" + termId, "")));
                        getNoneScoreCourseSuccess();
                        return;
                    }

                    showAlert("本地暂未缓存【" + termName + "】数据，请连接校园网获取数据.");
                    LoginGetSelectCoursePopWindow window = new LoginGetSelectCoursePopWindow(NoneScoreCourseActivity.this, termName, findViewById(R.id.none_score_course_layout).getHeight(), findViewById(R.id.none_score_course_layout).getWidth());
                    window.setFocusable(true);
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    window.showAtLocation(NoneScoreCourseActivity.this.findViewById(R.id.none_score_course_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                    popWindow = window;

                    Log.i("TermName", termName);
                } catch(Exception e){
                    e.printStackTrace();
                    getInformationFailed();
                }
//                if(termName != null && termName.length() > 0) getNoneScoreCourse(termName_TermId.get(termName));
//                else getInformationFailed();
            }
        });

        deleteSavedCourseInformationButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sharedPreferences.edit().remove("CourseHistory_" + termName_TermId.get(spinner.getSelectedItem().toString())).apply();
                showResponse("已删除【" + spinner.getSelectedItem().toString() + "】数据.");
                List<Map<String, Object>> datalist = new ArrayList<>();
                listView.setAdapter(new SimpleAdapter(context, datalist, R.layout.course_list_ietm, new String[]{"title", "context1"}, new int[]{R.id.get_none_score_course_title, R.id.get_none_score_course_context1}));
                deleteSavedCourseInformationButton.getLayoutParams().height = 0;
                deleteSavedCourseInformationButton.setVisibility(View.INVISIBLE);
                dataArea.requestLayout();
                return true;
            }
        });

        deleteSavedCourseInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert("您只需刷新【选课发生变化】学期的数据",
                        "一般情况下，您只需要点击首页“更新信息”按钮，即可查看最新的“成绩未发布课程”.\n" +
                                "只有在进行了“选课”/“退补选”后，才需要刷新本学期选课数据.\n" +
                                "如需刷新当前学期数据，请长按“删除本学期缓存数据”.\n");
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listView.setAdapter(null);
                deleteSavedCourseInformationButton.getLayoutParams().height = 0;
                deleteSavedCourseInformationButton.setVisibility(View.INVISIBLE);
                dataArea.requestLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setSpinnerItems(){
        if(LoginActivity.isLocalValueLoaded){
            spinner.setAdapter(new ArrayAdapter(this, R.layout.select_item, R.id.select_text_item, getTermArray()));
        }
//        else if(LoginActivity.isLoginIn && LoginActivity.uims != null && UIMS.getId_scorePercent().size() > 0) {
//            spinner.setAdapter(new ArrayAdapter(this, R.layout.select_item, R.id.select_text_item, getTermArray()));
//        }
//        else if(LoginActivity.isLoginIn && LoginActivity.uims != null){
//            final UIMS uims = LoginActivity.uims;
//            final Context context = this;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Alerter.create(NoneScoreCourseActivity.this)
//                            .setText("本地暂无教学学期数据\n检测到您已登录，如需从教务获取所需数据，请点击此消息。")
//                            .enableSwipeToDismiss()
//                            .enableProgress(true)
//                            .setProgressColorRes(R.color.color_21)
//                            .setDuration(5000)
//                            .setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            try {
//                                                showLoading("查询中，请稍候...");
//                                                if (uims.getTermArray()) {
////                                                    showResponse("查询学期列表成功！");
//                                                } else {
//                                                    showResponse("Login failed!");
//                                                    ((NoneScoreCourseActivity) context).finish();
//                                                    return;
//                                                }
//                                                if (!(UIMS.getId_scorePercent().size() > 0) &&uims.getScoreStatistics() && uims.getRecentScore()) {
////                                                    showResponse("查询成绩成功！");
//                                                }
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                                showResponse("Login failed!");
//                                                ((NoneScoreCourseActivity) context).finish();
//                                            } finally {
//                                                if (!(UIMS.getId_scorePercent().size() > 0)){
//                                                    showResponse("查询所需数据失败！");
//                                                    ((NoneScoreCourseActivity) context).finish();
//                                                }
//                                                else {
//                                                    loadSuccess();
//                                                    ((NoneScoreCourseActivity) context).finish();
//                                                    Intent intent = new Intent(LoginActivity.context, NoneScoreCourseActivity.class);
//                                                    startActivity(intent);
////                                                    overridePendingTransition(R.anim.up_in, R.anim.up_out);
//                                                }
//                                            }
//                                        }
//                                    }).start();
//                                }
//                            })
//                            .setOnHideListener(new OnHideAlertListener() {
//                                @Override
//                                public void onHide() {
//                                    if(!(UIMS.getId_scorePercent().size() > 0)){
//                                        getInformationFailed();
//                                    }
//                                }
//                            })
//                            .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
//                            .show();
//                }
//            });
//        }
        else{
            showResponse("本地暂无教学学期数据，且您还未登录，请登录后重试。");
            finish();
        }
    }

    private ArrayList<String> getTermArray(){
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
        Log.i("termId_termName.size", "" + termId_termName.size());
        terms.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        return terms;
    }

    private void getNoneScoreCourse(final String termID){
        getNoneScoreCourse(termID, null);
    }

    public void getNoneScoreCourse(final String termID, UIMS myUims){

        final List<Map<String, Object>> datalist = getListHint("查询中...");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new SimpleAdapter(NoneScoreCourseActivity.this, datalist, R.layout.course_list_ietm, new String[]{"title", "context1"}, new int[]{R.id.get_none_score_course_title, R.id.get_none_score_course_context1}));
                deleteSavedCourseInformationButton.getLayoutParams().height = 0;
                deleteSavedCourseInformationButton.setVisibility(View.INVISIBLE);
                dataArea.requestLayout();
            }
        });

        final UIMS uims = (myUims == null ? LoginActivity.uims : myUims);
        if(uims == null){
            showAlert("本地暂无【" + termId_termName.get(termID) + "】数据，且您还未登录，请登录后重试。");
            getNoneScoreCourseFailed();
            return;
        }
        showLoading("加载【" + termId_termName.get(termID) + "】数据中，请稍候...");
        final Context context = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (uims.getCourseHistory(termID)) {
                        sharedPreferences.edit().putString("CourseHistory_" + termID, UIMS.getCourseHistoryJSON().toString()).apply();
                        getNoneScoreCourseSuccess();
                    } else {
                        getInformationFailed();
                    }
                } catch (Exception e){
                    showResponse("加载数据失败！");
                    ((NoneScoreCourseActivity) context).finish();
                }
            }
        }).start();
    }

    private void getNoneScoreCourseSuccess(){
        Alerter.hide();
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deleteSavedCourseInformationButton.getLayoutParams().height = delete_local_course_information_button_layout_hight;
                deleteSavedCourseInformationButton.setVisibility(View.VISIBLE);
                List<Map<String, Object>> datalist = getCourseList();
                listView.setAdapter(new colorAdapter(context, datalist, R.layout.course_list_ietm, new String[]{"title", "context1"}, new int[]{R.id.get_none_score_course_title, R.id.get_none_score_course_context1}));
                dataArea.requestLayout();
            }
        });
    }

    public void getNoneScoreCourseFailed(){
        final List<Map<String, Object>> datalist = new ArrayList<>();
        setListviewAdapter(listView, new SimpleAdapter(NoneScoreCourseActivity.this, datalist, R.layout.course_list_ietm, new String[]{"title", "context1"}, new int[]{R.id.get_none_score_course_title, R.id.get_none_score_course_context1}));
    }

    private void getInformationFailed(){
//        showResponse("Get Information failed!");
        showResponse("获取信息失败！请尝试点击\"更新信息\"按钮.");
        Alerter.hide();

        List<Map<String, Object>> datalist = new ArrayList<>();
        setListviewAdapter(listView, new SimpleAdapter(NoneScoreCourseActivity.this, datalist, R.layout.course_list_ietm, new String[]{"title", "context1"}, new int[]{R.id.get_none_score_course_title, R.id.get_none_score_course_context1}));

        finish();
    }

    private List<Map<String, Object>> getCourseList(){
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map;

        HashMap<String, JSONObject> courseID_courseJSON = UIMS.getNoScoreCourseId_course();
        HashMap<String, String> courseSelectTypeId_courseSelectTypeName = UIMS.getCourseSelectTypeId_courseSlectTypeName();
        JSONObject courseJSON;
        JSONObject courseInfo;
//        JSONObject teachingTerm;
        JSONObject apl;

        String courseName;
//        String termName;
        String selectType;
        String credit;

        try {

            Iterator<Map.Entry<String, JSONObject>> iterator = courseID_courseJSON.entrySet().iterator();
            Map.Entry<String, JSONObject> entry;
            while (iterator.hasNext()){
                map = new HashMap<>();

                entry = iterator.next();
                courseJSON = entry.getValue();
                courseInfo = courseJSON.getJSONObject("lesson").getJSONObject("courseInfo");
//                teachingTerm = courseJSON.getJSONObject("teachingTerm");
                apl = courseJSON.getJSONObject("apl");

                courseName = courseInfo.getString("courName");
//                termName = teachingTerm.getString("termName");
                selectType = apl.getString("selectType");
                credit = apl.getString("credit");

                map.put("title", courseName + "(" + courseSelectTypeId_courseSelectTypeName.get(selectType) + ")");
                map.put("context1", "学分：" + credit);
                map.put("type", selectType);

                dataList.add(map);
            }

            if(dataList.size() == 0){
                map = new HashMap<>();

                map.put("title", "本学期暂无成绩未发布的课程~");
                map.put("context1", "");
                map.put("type", "");

                dataList.add(map);
            }

            return dataList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Map<String, Object>> getListHint(String str){
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map;

        try {
            map = new HashMap<>();

            map.put("title", str);
            map.put("context1", "");
            map.put("type", "");

            dataList.add(map);

            return dataList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadSuccess(){
        SharedPreferences sp = LoginActivity.context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        sp.edit().putString("ScoreJSON", UIMS.getScoreJSON().toString()).apply();
        sp.edit().putString("StudentJSON", UIMS.getStudentJSON().toString()).apply();
        sp.edit().putString("CourseTypeJSON", UIMS.getCourseTypeJSON().toString()).apply();
        sp.edit().putString("CourseSelectTypeJSON", UIMS.getCourseSelectTypeJSON().toString()).apply();
        sp.edit().putString("ScoreStatisticsJSON", UIMS.getScoreStatisticsJSON().toString()).apply();
        sp.edit().putString("TermJSON", UIMS.getTermJSON().toString()).apply();

    }

    private void setListviewAdapter(final ListView listView, final ListAdapter adapter){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                listView.requestLayout();
            }
        });
    }

    public void dismissPopWindow(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popWindow.dismiss();
            }
        });
    }

    class colorAdapter extends SimpleAdapter {
        List<? extends Map<String, ?>> mdata;

        public colorAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                            int[] to) {
            super(context, data, resource, from, to);
            this.mdata = data;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LinearLayout.inflate(getBaseContext(), R.layout.course_list_ietm, null);
            }//这个TextView是R.layout.list_item里面的，修改这个字体的颜色
            TextView textView = (TextView) convertView.findViewById(R.id.get_none_score_course_title);
            //获取每次进来时 mData里面存的值  若果相同则变颜色
            //根据Key值取出装入的数据，然后进行比较
            String ss=(String)mdata.get(position).get("type");
            if(ss.equals("3060")){
                textView.setTextColor(getResources().getColor(R.color.course_bixiu));
            }else if(ss.equals("3061")){
                textView.setTextColor(getResources().getColor(R.color.course_xuanxiu));
            }else if(ss.equals("3062")){
                textView.setTextColor(getResources().getColor(R.color.course_xianxuan));
            }else if(ss.equals("3065")){
                textView.setTextColor(getResources().getColor(R.color.course_xiaoxuanxiu));
            }else if(ss.equals("3064")){
                textView.setTextColor(getResources().getColor(R.color.course_tiyu));
            }else if(ss.equals("3066")){
                textView.setTextColor(getResources().getColor(R.color.course_kuazhuanye));
            }else if(ss.equals("3067")){
                textView.setTextColor(getResources().getColor(R.color.course_tongguozaixiu));
            }else if(ss.equals("3068")){
                textView.setTextColor(getResources().getColor(R.color.course_buxiu));
            }else if(ss.equals("3099")){
                textView.setTextColor(getResources().getColor(R.color.course_bangdinggongxuan));
            }
            //Log.i("TAG", Integer.toString(position));
            //Log.i("TAG", (String) mData.get(position).get("text"));
            return super.getView(position, convertView, parent);
        }
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
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
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
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
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
                        .setProgressColorRes(R.color.color_21)
                        .setDuration(10000)
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                        .show();
            }
        });
    }
}
