package com.lu.mydemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import android.widget.SimpleAdapter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.tapadoo.alerter.Alerter;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import CJCX.CJCX;
import Config.ColorManager;
import UIMS.UIMS;
import Utils.Score.ScoreConfig;
import Utils.Score.ScoreInf;
import View.PopWindow.LoginGetScorePopupWindow;

public class ScoreActivity extends AppCompatActivity
{

//    TextView scoreStatisticsTextViewControl;
//    TextView centerTitle;
    static SharedPreferences sp;

    LinearLayout scoreStatisticsLayout;
    TextView first_score;
    TextView first_gpa;
    TextView best_score;
    TextView best_gpa;
    TextView first_bixiu_score;
    TextView first_bixiu_gpa;
    TextView first_bixiu_with_addition_score;
    TextView first_bixiu_with_addition_gpa;

    TextView backTextView;
//    TextView settingText;

    FloatingActionButton fab;

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private static List<Map<String, Object>> dataList;

    private static PieData[] pieDatas;
    private static boolean[] showScorePercentFlags;

//    private TextView navigation_back;

    static HashMap<Integer, String> index_id = new HashMap<>();
    static HashMap<String, String> courseTypeID_courseType = new HashMap<>();

    private static int requiredScoreSum = 0;
    private static double requiredGPASum = 0;
    private static double requiredCreditSum = 0;

    private static int required_custom_ScoreSum = 0;
    private static double required_custom_GPASum = 0;
    private static double required_custom_CreditSum = 0;

    private static boolean bixiu_select = true;
    private static boolean xuanxiu_select = true;
    private static boolean xianxuan_select = true;
    private static boolean xiaoxuanxiu_select = false;
    private static boolean PE_select = false;
    private static boolean chongxiu_select = false;

    private static boolean reloadData = false;

    public static Context context;

//    private boolean isShow = true;
//    private SharedPreferences sp;

    public LoginGetScorePopupWindow getScorePopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        sp = getApplicationContext().getSharedPreferences("ScoreSelectInfo", Context.MODE_PRIVATE);
        context = getApplicationContext();

        bixiu_select = sp.getBoolean("bixiu_select", true);
        xuanxiu_select = sp.getBoolean("xuanxiu_select", true);
        xianxuan_select = sp.getBoolean("xianxuan_select", true);
        xiaoxuanxiu_select = sp.getBoolean("xiaoxuanxiu_select", false);
        PE_select = sp.getBoolean("PE_select", false);
        chongxiu_select = sp.getBoolean("chongxiu_select", false);

//        sp = this.getSharedPreferences("ScoreStatistics", Context.MODE_PRIVATE);
//        isShow = sp.getBoolean("show", true);

//        scoreStatisticsTextViewControl = findViewById(R.id.activity_main_textView_ScoreStatisticsControl);
//        scoreStatisticsTitleTextView = findViewById(R.id.textView_ScoreStatisticsTitle);
        backTextView = findViewById(R.id.activity_scrolling_layout_back_text);
//        settingText = findViewById(R.id.activity_scrolling_layout_setting_text);

        fab = findViewById(R.id.activity_scrolling_fab);

        scoreStatisticsLayout = findViewById(R.id.activity_main_ScoreStatistics);
        first_score = findViewById(R.id.activity_main_ScoreStatistics_first_score);
        first_gpa = findViewById(R.id.activity_main_ScoreStatistics_first_gpa);
        best_score = findViewById(R.id.activity_main_ScoreStatistics_best_score);
        best_gpa = findViewById(R.id.activity_main_ScoreStatistics_best_gpa);
        first_bixiu_score = findViewById(R.id.activity_main_ScoreStatistics_first_bixiu_score);
        first_bixiu_gpa = findViewById(R.id.activity_main_ScoreStatistics_first_bixiu_gpa);
        first_bixiu_with_addition_score = findViewById(R.id.activity_main_ScoreStatistics_first_bixiu_with_addition_score);
        first_bixiu_with_addition_gpa = findViewById(R.id.activity_main_ScoreStatistics_first_bixiu_with_addition_gpa);

        swipeRecyclerView = findViewById(R.id.activity_main_recycler_view);

//        navigation_back = findViewById(R.id.activity_main_navigation_back_text);

        changeTheme();

        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //考虑加载性能优化
        swipeRecyclerView.setHasFixedSize(true);//设置子布局（每项）高度相等，避免每次计算高度
        swipeRecyclerView.setItemViewCacheSize(20);
        swipeRecyclerView.setDrawingCacheEnabled(true);
        swipeRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        swipeRecyclerView.setOnItemClickListener(new com.yanzhenjie.recyclerview.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
//                showPercent(index_id.get(adapterPosition), ((String) dataList.get(adapterPosition).get("context1")).contains("是"));
                //TODO TEST Chart测试
                showScorePercentFlags[adapterPosition] = !showScorePercentFlags[adapterPosition];
                setData(index_id.get(adapterPosition), ((String) dataList.get(adapterPosition).get("context1")).contains("是"), adapterPosition);
                myAdapter.notifyItemChanged(adapterPosition);
            }
        });
        swipeRecyclerView.setAdapter(createAdapter());

        scoreStatisticsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertCenter.showAlert(ScoreActivity.this, "关于\"平均成绩(绩点)\"", "数据由本地计算得出.计算方法：课程首次成绩(绩点)的加权平均数;(权值为科目学分.)\n本数据仅供参考，不代表教务成绩统计结果，请知悉.\n\n" +
                        "仅必修：只包含必修课程首次成绩统计；\n" +
                        "首次成绩：自定义统计项.");
            }
        });

//        navigation_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        //TODO TEST 成绩统计隐藏测试
//        scoreStatisticsTextViewControl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeScoreStatisticsState(!isShow);
//            }
//        });
//        changeScoreStatisticsState(isShow);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO flush score
//                Toast.makeText(ScoreActivity.this, "FLUSH!", Toast.LENGTH_SHORT).show();
                LoginGetScorePopupWindow window = new LoginGetScorePopupWindow(ScoreActivity.this, findViewById(R.id.activity_scrolling_layout).getHeight(), findViewById(R.id.activity_scrolling_layout).getWidth());
                window.setFocusable(true);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.showAtLocation(ScoreActivity.this.findViewById(R.id.activity_scrolling_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                getScorePopupWindow = window;
            }
        });

//        settingText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ScoreActivity.this, SettingActivity.class));
//            }
//        });

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(showScorePercentFlags != null && showScorePercentFlags.length > 0){
            for(int i=0; i<showScorePercentFlags.length; i++){
                showScorePercentFlags[i] = false;
            }
        }

        loadScoreList();
//        long finishTime = System.currentTimeMillis();
//        Log.i("Time", (finishTime - (long) getIntent().getBundleExtra("bundle").get("startTime")) / 1000 + "");

//        //TODO TEST 数据加载测试【数据与视图层拆分，实现懒加载】
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ScoreInf.loadScoreList();
//                Log.i("TEST:ScoreInf", ScoreInf.getDataList().toString());
//            }
//        }).start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(reloadData){
            reloadScoreList();
        }
        else {
            myAdapter = createAdapter();
            swipeRecyclerView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged(dataList);
        }
//        reloadScoreList();
        getScoreStatistics();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){

        }
    }

    public void loadScoreList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(ScoreConfig.isIsCJCXEnable()){
                    loadCJCXScore();
                }
                getScoreList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("ScoreListSize", "Size:\t" + dataList.size());
                        getScoreStatistics();
                        myAdapter = createAdapter();
                        swipeRecyclerView.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged(dataList);
                        Alerter.hide();
                    }
                });
//                final List<Map<String, Object>> datalist = getScoreList();
//                final ListViewForScrollView lv = findViewById(R.id.activity_main_list_view);
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        lv.setAdapter(new colorAdapter(ScoreActivity.this, datalist, R.layout.list_item, new String[]{"title", "context1", "context2", "context3", "context4"}, new int[]{R.id.list_item_title, R.id.list_item_context1, R.id.list_item_context2, R.id.list_item_context3, R.id.list_item_context4}));
//                        lv.addHeaderView(new ViewStub(ScoreActivity.this));
//                        lv.addFooterView(new ViewStub(ScoreActivity.this));
//                        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
//                        lv.setOnItemClickListener(new OnItemClickListener() {
//                            //list点击事件
//                            @Override
//                            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                                // TODO: Implement this method
//                Toast.makeText(ScoreActivity.this, "TEMP", Toast.LENGTH_SHORT).show();
//                Alerter.create(ScoreActivity.this)
//                        .setTitle("Title")
//                        .setText("p1:\t" + p1 + "\n" +
//                                "p2:\t" + p2 + "\n" +
//                                "p3:\t" + p3 + "\n" +
//                                "p4:\t" + p4 + "\n"
//                        )
//                        .enableSwipeToDismiss()
//                        .setBackgroundColorInt(Color.rgb(100,100,100))
//                        .show();
//                Log.i("ScoreActivity:showPercent", index_id.get(p3 - 1) + ((String) datalist.get(p3 - 1).get("context1")).contains("是"));
//                                showPercent(index_id.get(p3 - 1), ((String) datalist.get(p3 - 1).get("context1")).contains("是"));
//                            }
//                        });
//                        Alerter.hide();
//                    }
//                });
            }
        }).start();
    }

    public void reloadScoreList(){
        reloadData = true;
        ScoreInf.setScoreListLoaded(false);
        ScoreInf.loadScoreList();
        getScoreList();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter = createAdapter();
                swipeRecyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged(dataList);
                getScoreStatistics();
            }
        });
    }

    public static void getScoreList(){

        if(!reloadData && dataList != null && dataList.size() > 0) return;

        if(ScoreInf.isScoreListLoaded()){
            dataList = ScoreInf.getDataList();
            pieDatas = new PieData[dataList.size()];
            showScorePercentFlags = new boolean[dataList.size()];
            for(int i=0; i<dataList.size(); i++){
                showScorePercentFlags[i] = false;
            }
            Log.i("GetScore", "Loaded score list from ScoreInf.");
            return;
        }
        else {
            Log.e("ScoreList", "Not loaded!");
        }

        reloadData = false;

        courseTypeID_courseType = UIMS.getCourseTypeId_courseType();
        dataList = new ArrayList<>();
        Map<String,Object> map;

        try {

            JSONObject scoreJSON = UIMS.getScoreJSON();

            JSONArray scores = scoreJSON.getJSONArray("value");

            double adviceCredit;

            for (int i = 0; i < scores.size(); i++) {
                map = new HashMap<>();

                JSONObject temp = scores.getJSONObject(i);
                JSONObject teachingTerm = temp.getJSONObject("teachingTerm");
                JSONObject course = temp.getJSONObject("course");
                String asId = temp.getString("asId");
                String courName = course.getString("courName");
                String termName = teachingTerm.getString("termName");
                String courScore = temp.getString("score");
                int scoreNum = temp.getInt("scoreNum");
                String isReselect = (temp.getString("isReselect").contains("Y")) ? "是" : "否";
                String gPoint = temp.getString("gpoint");
                String dateScore = temp.getString("dateScore");
                String type5 = temp.getString("type5");
                adviceCredit = course.getDouble("adviceCredit");
                dateScore = dateScore.replaceAll("T", "  ");

                map.put("asId", asId);
                index_id.put(i, asId);
                map.put("title", courName + "(" + courseTypeID_courseType.get(type5) + ")");
//                map.put("context1","成绩:" + courScore + "  \t  " +
//                        "重修:" + isReselect);
//                map.put("context2",
//                        termName);
                map.put("context1", termName + "   \t   " +
                        "重修?  " + isReselect);
                map.put("context2",
                        courScore);
                map.put("context3",
                        "发布时间： " + dateScore);
                map.put("context4",
                        adviceCredit);
                map.put("context5",
                        gPoint);
                map.put("type", type5);

                if (!chongxiu_select) {//排除所有重修
                    if (isReselect.equals("否") ) {//排除所有重修

//                    if (type5.equals("4161") || type5.equals("4162") || type5.equals("4164") ) {//体育/限选/选修（除校选修）
                        if (xuanxiu_select && type5.equals("4161") ){//选修
                            required_custom_ScoreSum += scoreNum * adviceCredit;
                            required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                            required_custom_CreditSum += adviceCredit;
                        }
                        if (xianxuan_select && type5.equals("4162") ) {//限选
                            required_custom_ScoreSum += scoreNum * adviceCredit;
                            required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                            required_custom_CreditSum += adviceCredit;
                        }
                        if (xiaoxuanxiu_select && type5.equals("4163") ) {//校选修
                            required_custom_ScoreSum += scoreNum * adviceCredit;
                            required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                            required_custom_CreditSum += adviceCredit;
                        }
                        if (PE_select && type5.equals("4164") ) {//体育
                            required_custom_ScoreSum += scoreNum * adviceCredit;
                            required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                            required_custom_CreditSum += adviceCredit;
                        }
                        if (type5.equals("4160") ) {//仅必修
                            if(bixiu_select) {
                                required_custom_ScoreSum += scoreNum * adviceCredit;
                                required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                                required_custom_CreditSum += adviceCredit;
                            }
                            requiredScoreSum += scoreNum * adviceCredit;
                            requiredGPASum += Double.parseDouble(gPoint) * adviceCredit;
                            requiredCreditSum += adviceCredit;
                        }

                    }
                }
                else{
                    if (type5.equals("4160") ) {//仅必修
                        if(bixiu_select) {
                            required_custom_ScoreSum += scoreNum * adviceCredit;
                            required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                            required_custom_CreditSum += adviceCredit;
                        }
                        if (isReselect.equals("否") ) {//排除重修
                            requiredScoreSum += scoreNum * adviceCredit;
                            requiredGPASum += Double.parseDouble(gPoint) * adviceCredit;
                            requiredCreditSum += adviceCredit;
                        }
                    }
                    if (xuanxiu_select && type5.equals("4161") ){//选修
                        required_custom_ScoreSum += scoreNum * adviceCredit;
                        required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                        required_custom_CreditSum += adviceCredit;
                    }
                    if (xianxuan_select && type5.equals("4162") ) {//限选
                        required_custom_ScoreSum += scoreNum * adviceCredit;
                        required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                        required_custom_CreditSum += adviceCredit;
                    }
                    if (xiaoxuanxiu_select && type5.equals("4163") ) {//校选修
                        required_custom_ScoreSum += scoreNum * adviceCredit;
                        required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                        required_custom_CreditSum += adviceCredit;
                    }
                    if (PE_select && type5.equals("4164") ) {//体育
                        required_custom_ScoreSum += scoreNum * adviceCredit;
                        required_custom_GPASum += Double.parseDouble(gPoint) * adviceCredit;
                        required_custom_CreditSum += adviceCredit;
                    }
                }

                dataList.add(map);

            }

            HashMap<String, org.json.JSONObject> id_JSON = CJCX.getId_JSON();
            if(!ScoreConfig.isIsCJCXEnable() || id_JSON == null) {
                Log.i("GetScoreList", "Ignored CJCX!");
                Log.i("GetScoreList", "Finished! Size:\t" + dataList.size());
                return;
            }
            else{
                loadCJCXScore();
                id_JSON = CJCX.getId_JSON();
                if(id_JSON == null) {
                    Log.i("GetScoreList", "Ignored CJCX!  Load Failed! No date!");
                    return;
                }
            }
            org.json.JSONObject object;
            for(String id : id_JSON.keySet()){
                if(!index_id.containsValue(id)) {
                    try {
                        object = id_JSON.get(id);
                        map = new HashMap<>();
                        map.put("asId", object.getString("lsrId"));
                        map.put("title", object.getString("kcmc"));
                        try {
                            map.put("context1", UIMS.getTermId_termName().get(object.getString("termId")) + "   \t   " +
                                    "重修?  " + ((object.getString("isReselect").toUpperCase().equals("N")) ? "否" : "是"));
                        }
                        catch (Exception e){
                            map.put("context1", UIMS.getTermId_termName().get(object.getString("termId")) + "   \t   " +
                                    "重修?  " + "否");
                        }
                        map.put("context2",
                                object.getString("cj"));
                        map.put("context3",
                                "发布时间： " + "--");
                        map.put("context4",
                                object.getDouble("credit"));
                        map.put("context5",
                                object.getString("gpoint"));
                        map.put("type", "");
                        dataList.add(0, map);
                    }catch (Exception e){
                        Log.w("ErrJSON", id_JSON.get(id).toString());
                        e.printStackTrace();
                    }
                }
            }

            int index = 0;
            for(Map<String,Object> temp : dataList){
                index_id.put(index++, (String) temp.get("asId"));
            }

            Log.i("GetScoreList", "Finished! Size:\t" + dataList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getScoreStatistics() {

        try {

            JSONObject scoreStatistics = UIMS.getScoreStatisticsJSON();

            if(scoreStatistics == null){
                first_score.setText("--");
                first_gpa.setText("--");
                best_score.setText("--");
                best_gpa.setText("--");
                first_bixiu_score.setText("--");
                first_bixiu_gpa.setText("--");
                first_bixiu_with_addition_score.setText("--");
                first_bixiu_with_addition_gpa.setText("--");
                return;
            }

            double avgScoreBest = scoreStatistics.getJSONArray("value").getJSONObject(0).getDouble("avgScoreBest");
            double avgScoreFirst = scoreStatistics.getJSONArray("value").getJSONObject(0).getDouble("avgScoreFirst");
            double gpaFirst = scoreStatistics.getJSONArray("value").getJSONObject(0).getDouble("gpaFirst");
            double gpaBest = scoreStatistics.getJSONArray("value").getJSONObject(0).getDouble("gpaBest");

//            Map<String, Object> map = new HashMap<>();
//        map.put("title","绩点统计");
//        map.put("context","【首次成绩】：平均成绩:" + avgScoreFirst + "  " + "平均绩点:" + gpaFirst + "  \n" +
//                "【最好成绩】：平均成绩:" + avgScoreBest + "  " + "平均绩点:" + gpaBest);

//        String title = "绩点统计";
//        String title = "学分成绩";
//        String value = "【首次成绩】 \t平均成绩:" + String.format("%.2f", avgScoreFirst) + "  " + "平均绩点:" + String.format("%.2f", gpaFirst) + "\n" +
//                "【最好成绩】 \t平均成绩:" + String.format("%.2f", avgScoreBest) + "  " + "平均绩点:" + String.format("%.2f", gpaBest) + "\n" +
//                "必修平均成绩:" + String.format("%.2f", requiredScoreSum / requiredCreditSum) + " \t 必修平均绩点:" + String.format("%.2f", requiredGPASum / requiredCreditSum);
            first_score.setText(String.format("%.2f", avgScoreFirst));
            first_gpa.setText(String.format("%.2f", gpaFirst));
            best_score.setText(String.format("%.2f", avgScoreBest));
            best_gpa.setText(String.format("%.2f", gpaBest));
            first_bixiu_score.setText(String.format("%.2f", requiredScoreSum / requiredCreditSum));
            first_bixiu_gpa.setText(String.format("%.2f", requiredGPASum / requiredCreditSum));
            first_bixiu_with_addition_score.setText(String.format("%.2f", required_custom_ScoreSum / required_custom_CreditSum));
            first_bixiu_with_addition_gpa.setText(String.format("%.2f", required_custom_GPASum / required_custom_CreditSum));
//        scoreStatisticsTitleTextView.setText(title);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showPercent(final String asID, boolean isReSelect){
        final JSONObject percent = UIMS.getScorePercentJSON(asID);

        if(percent == null){
            AlertCenter.showAlert(this, "成绩分布走丢了(っ °Д °;)っ\n\n" +
                    "校外成绩查询(CJCX)无成绩分布哦;\n" +
                    "如在校内，请连接校园网后点击“刷新信息”再试一下吧.");
            return;
        }

        JSONArray items = percent.getJSONArray("items");

        StringBuilder stringBuilder = new StringBuilder();
        JSONObject temp;
        String label;
        double perc;
        int i = 0;
        try{
            while(true) {
                temp = items.getJSONObject(i);
                label = temp.getString("label");
                perc = temp.getDouble("percent");

                if (i != 0) stringBuilder.append('\n');
                stringBuilder.append(label);
//                Log.i("ScoreActivity", "label: " + label + "\tlength: " + label.length());
                if (label.length() == 8) stringBuilder.append(":\t");
                else if (label.length() < 8) stringBuilder.append(":\t\t\t");
                else stringBuilder.append(":\t\t");
                stringBuilder.append(getScorePercentString(perc));
                stringBuilder.append(String.format("%.1f", perc) + " %");
                if (UIMS.getCourseTypeId(asID).equals("4160") && !isReSelect)
                    stringBuilder.append("\t(约 " + String.format("%.0f", UIMS.getStudCnt() * perc * 0.01) + " 人)");
                i++;
            }

        }catch (Exception e) {
//            e.printStackTrace();
            if(UIMS.getCourseTypeId(asID).equals("4160") && ! isReSelect) stringBuilder.append("\n\n人数仅供参考哦(￣▽￣)");
        }
        final String str = stringBuilder.toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(ScoreActivity.this)
                        .setTitle(percent.getString("courName"))
                        .setText(str)
                        .enableSwipeToDismiss()
                        .setDuration(4000)
                        .hideIcon()
//                        .enableProgress(true)
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    private void setData(final String asID, boolean isReSelect, final int position){

        if(pieDatas[position] != null){
            Log.w("PieChartTest", "Ignored generate PieDate because the data is already there!");
            return;
        }

        final JSONObject percent = UIMS.getScorePercentJSON(asID);

        if(percent == null){

            return;
        }

        ArrayList<PieEntry> values = new ArrayList<>();

        JSONArray items = percent.getJSONArray("items");

        JSONObject temp;
        String label;
        double perc;
        int i = 0;
        try{
            while(true) {
                temp = items.getJSONObject(i);
                label = temp.getString("label");
                perc = temp.getDouble("percent");

                if (UIMS.getCourseTypeId(asID).equals("4160") && !isReSelect)
                    values.add(new PieEntry((float) perc, label + "\n(约 " + String.format("%.0f", UIMS.getStudCnt() * perc * 0.01) + " 人)"));
                else
                    values.add(new PieEntry((float) perc, label));
                i++;
            }
        }catch (Exception e) {
//            e.printStackTrace();
        }
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.BLACK);

        pieDatas[position] = data;
    }

    private SpannableString generateCenterSpannableText(String str) {

        SpannableString s = new SpannableString(str);
//        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
//        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        try {
            s.setSpan(new RelativeSizeSpan(1f), str.indexOf("("), str.indexOf(")") + 1, 0);
            s.setSpan(new StyleSpan(Typeface.ITALIC), str.indexOf("("), str.indexOf(")") + 1, 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), str.indexOf("("), str.indexOf(")") + 1, 0);
        }catch (Exception e){
            Log.w("SetChartTextStyle", "Ignored!");
        }
        return s;
    }

    private String getScorePercentString(double percent){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<(int)percent/3; i++){
            stringBuilder.append("|");
        }
        stringBuilder.append("\t\t");
        return stringBuilder.toString();
    }

    public void showResponse(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScoreActivity.this, string, Toast.LENGTH_SHORT).show();
//                showAlert(string);
            }
        });
    }

    public class NestedListView extends ListView  {

        public NestedListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //测量的大小由一个32位的数字表示，前两位表示测量模式，后30位表示大小，这里需要右移两位才能拿到测量的大小
            int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightSpec);
        }

    }

    private ColorStateList getColorStateListTest() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
        int color = ColorManager.getTopAlertBackgroundColor();
        int[] colors = new int[]{color, color, color, ColorManager.getTopAlertBackgroundColor()};
        return new ColorStateList(states, colors);
    }

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        findViewById(R.id.activity_scrolling_layout).setBackground(ColorManager.getMainBackground_full());
        fab.setBackgroundTintList(getColorStateListTest());
//        fab.setColorFilter(ColorManager.getPrimaryColor());
//        fab.setRippleColor(ColorManager.getPrimaryColor());
//        fab.setBackgroundColor(ColorManager.getPrimaryColor());
    }

    public void dismissGetScorePopWindow(){
        try{
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getScorePopupWindow.dismiss();
                getScorePopupWindow = null;
            }
        });
    }

    protected MainAdapter createAdapter() {
        return new ScoreListAdapter(this);
    }

    class ScoreListAdapter extends MainAdapter{

        private List<Map<String,Object>> mDataList;

        public ScoreListAdapter(Context context){
            super(context);
        }

        public void notifyDataSetChanged(List dataList) {
            this.mDataList = (List<Map<String,Object>>)dataList;
            super.notifyDataSetChanged(mDataList);
        }

        @Override
        public int getItemCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        @NonNull
        @Override
        public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ScoreListAdapter.ViewHolder(getInflater().inflate(R.layout.list_item_new, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
            holder.setData((String) mDataList.get(position).get("title"), (String) mDataList.get(position).get("context1"), (String) mDataList.get(position).get("context2"), (String) mDataList.get(position).get("context3"), (Double) mDataList.get(position).get("context4"), (String) mDataList.get(position).get("context5"), (String) mDataList.get(position).get("type"), position);
        }

        class ViewHolder extends MainAdapter.ViewHolder {

            TextView tvTitle;
            TextView tvContext1;
            TextView tvContext2;
            TextView tvContext3;
            TextView tvContext4;
            TextView tvContext5;

            PieChart chart;

            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_item_title);
                tvContext1 = itemView.findViewById(R.id.list_item_context1);
                tvContext2 = itemView.findViewById(R.id.list_item_context2);
                tvContext3 = itemView.findViewById(R.id.list_item_context3);
                tvContext4 = itemView.findViewById(R.id.list_item_context4);
                tvContext5 = itemView.findViewById(R.id.list_item_context5);

                chart = itemView.findViewById(R.id.list_item_chart1);
                chart.setUsePercentValues(true);
                chart.setExtraOffsets(3, 3, 3, 3);

                chart.setDragDecelerationFrictionCoef(0.95f);

                chart.animateY(1400, Easing.EaseInOutQuad);

                Legend l = chart.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.VERTICAL);
                l.setDrawInside(false);
                l.setXEntrySpace(7f);
                l.setYEntrySpace(0f);
                l.setYOffset(0f);

                // entry label styling
                chart.setEntryLabelColor(Color.WHITE);
                chart.setEntryLabelTextSize(12f);
                chart.setDrawEntryLabels(false);
                chart.setNoDataText("成绩分布走丢了(っ °Д °;)っ");
                chart.getDescription().setText("");
            }

            public void setData(String title, String context1, final String context2, String context3, Double context4, String context5, String type, int position) {
                tvTitle.setText(title);
                tvContext1.setText(context1);
                tvContext2.setText(context2);
                tvContext3.setText(context3);
                tvContext4.setText(context4 + "");
                tvContext5.setText(context5);

                switch (type){
                    case "4160" :{
                        tvTitle.setTextColor(getResources().getColor(R.color.course_bixiu));
                        break;
                    }
                    case "4161" :{
                        tvTitle.setTextColor(getResources().getColor(R.color.course_xuanxiu));
                        break;
                    }
                    case "4162" :{
                        tvTitle.setTextColor(getResources().getColor(R.color.course_xianxuan));
                        break;
                    }
                    case "4163" :{
                        tvTitle.setTextColor(getResources().getColor(R.color.course_xiaoxuanxiu));
                        break;
                    }
                    case "4164" :{
                        tvTitle.setTextColor(getResources().getColor(R.color.course_tiyu));
                        break;
                    }
                    default:{
                        tvTitle.setTextColor(ColorManager.getNews_normal_text_color());
                    }
                }

                if(showScorePercentFlags[position]){
                    chart.setCenterText(generateCenterSpannableText(title));

                    chart.setDrawCenterText(true);

                    final PieData data = pieDatas[position];
                    if(data != null) data.setValueFormatter(new PercentFormatter(chart){
                        int index = -1;
                        int part_index;
                        @Override
                        public String getFormattedValue(float value) {
                            try{
                                part_index = (int) Double.parseDouble(context2) / 10;
                            }catch (Exception e){
//                                e.printStackTrace();
                                switch (context2){
                                    case "优秀" :{
                                        part_index = 9;
                                        break;
                                    }
                                    case "良好" :{
                                        part_index = 8;
                                        break;
                                    }
                                    case "中等" :{
                                        part_index = 7;
                                        break;
                                    }
                                    case "及格" :{
                                        part_index = 6;
                                        break;
                                    }
                                    case "不及格" :{
                                        part_index = 5;
                                        break;
                                    }
                                }
                            }
                            index ++;
                            if(index > 5) index %= 5;
//                            Log.e("ScorePercent", "index:" + index + "\tpart_index:" +part_index + "\tvalue:" + value);
                            if(value > 0){
                                switch (part_index){
                                    case 10:{
                                        if(index == 0){
                                            return "👉" + super.getFormattedValue(value);
                                        }
                                        return super.getFormattedValue(value);
                                    }
                                    case 9:{
                                        if(index == 0){
                                            return "👉" + super.getFormattedValue(value);
                                        }
                                        else {
                                            return super.getFormattedValue(value);
                                        }
                                    }
                                    case 8:{
                                        if(index == 1){
                                            return "👉" + super.getFormattedValue(value);
                                        }
                                        else {
                                            return super.getFormattedValue(value);
                                        }
                                    }
                                    case 7:{
                                        if(index == 2){
                                            return "👉" + super.getFormattedValue(value);
                                        }
                                        else {
                                            return super.getFormattedValue(value);
                                        }
                                    }
                                    case 6:{
                                        if(index == 3){
                                            return "👉" + super.getFormattedValue(value);
                                        }
                                        else {
                                            return super.getFormattedValue(value);
                                        }
                                    }
                                    default:{
                                        if(index == 4){
                                            return "👉" + super.getFormattedValue(value);
                                        }
                                        else {
                                            return super.getFormattedValue(value);
                                        }
                                    }
                                }
                            }
                            else return "";
                        }
                    });
                    chart.setData(data);
                    // TODO
//                    chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//                        @Override
//                        public void onValueSelected(Entry e, Highlight h) {
////                            chart.getDescription().setText(e + " \t" + h);
//                            Log.i("PieChartTest", e + " \t" + h);
//                            Log.i("PieChartTest", data.getDataSetByIndex((int) h.getX()).toString());
//                        }
//
//                        @Override
//                        public void onNothingSelected() {
//                            chart.getDescription().setText("");
//                        }
//                    });
                    // undo all highlights
                    chart.highlightValues(null);

                    chart.invalidate();
                    chart.setVisibility(View.VISIBLE);
                }
                else {
                    chart.setVisibility(View.GONE);
                }
            }
        }

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
                convertView = LinearLayout.inflate(getBaseContext(), R.layout.list_item_new, null);
            }//这个TextView是R.layout.list_item里面的，修改这个字体的颜色
            TextView textView = (TextView) convertView.findViewById(R.id.list_item_title);
            //获取每次进来时 mData里面存的值  若果相同则变颜色
            //根据Key值取出装入的数据，然后进行比较
            String ss=(String)mdata.get(position).get("type");
            if(ss.equals("4160")){
                textView.setTextColor(getResources().getColor(R.color.course_bixiu));
            }else if(ss.equals("4161")){
                textView.setTextColor(getResources().getColor(R.color.course_xuanxiu));
            }else if(ss.equals("4162")){
                textView.setTextColor(getResources().getColor(R.color.course_xianxuan));
            }else if(ss.equals("4163")){
                textView.setTextColor(getResources().getColor(R.color.course_xiaoxuanxiu));
            }else if(ss.equals("4164")){
                textView.setTextColor(getResources().getColor(R.color.course_tiyu));
            }
            //Log.i("TAG", Integer.toString(position));
            //Log.i("TAG", (String) mData.get(position).get("text"));
            return super.getView(position, convertView, parent);
        }
    }

    public static void loadScoreSelect(){
        if(sp == null) sp = MainActivity.context.getApplicationContext().getSharedPreferences("ScoreSelectInfo", Context.MODE_PRIVATE);;
        bixiu_select = sp.getBoolean("bixiu_select", true);
        xuanxiu_select = sp.getBoolean("xuanxiu_select", true);
        xianxuan_select = sp.getBoolean("xianxuan_select", true);
        xiaoxuanxiu_select = sp.getBoolean("xiaoxuanxiu_select", false);
        PE_select = sp.getBoolean("PE_select", false);
        chongxiu_select = sp.getBoolean("chongxiu_select", false);
    }

    public static boolean isBixiu_select() {
        return bixiu_select;
    }

    public static void setBixiu_select(boolean bixiu_select) {
        ScoreActivity.bixiu_select = bixiu_select;
        if(sp == null) sp = MainActivity.context.getApplicationContext().getSharedPreferences("ScoreSelectInfo", Context.MODE_PRIVATE);;
        sp.edit().putBoolean("bixiu_select", ScoreActivity.bixiu_select).apply();
        Log.i("ScoreCustom", "setBixiu_select:\t" + bixiu_select);
    }

    public static boolean isXuanxiu_select() {
        return xuanxiu_select;
    }

    public static void setXuanxiu_select(boolean xuanxiu_select) {
        ScoreActivity.xuanxiu_select = xuanxiu_select;
        if(sp == null) sp = MainActivity.context.getApplicationContext().getSharedPreferences("ScoreSelectInfo", Context.MODE_PRIVATE);;
        sp.edit().putBoolean("xuanxiu_select", ScoreActivity.xuanxiu_select).apply();
        Log.i("ScoreCustom", "setXuanxiu_select:\t" + xuanxiu_select);
    }

    public static boolean isXianxuan_select() {
        return xianxuan_select;
    }

    public static void setXianxuan_select(boolean xianxuan_select) {
        ScoreActivity.xianxuan_select = xianxuan_select;
        if(sp == null) sp = MainActivity.context.getApplicationContext().getSharedPreferences("ScoreSelectInfo", Context.MODE_PRIVATE);;
        sp.edit().putBoolean("xianxuan_select", ScoreActivity.xianxuan_select).apply();
        Log.i("ScoreCustom", "setXianxuan_select:\t" + xianxuan_select);
    }

    public static boolean isXiaoxuanxiu_select() {
        return xiaoxuanxiu_select;
    }

    public static void setXiaoxuanxiu_select(boolean xiaoxuanxiu_select) {
        ScoreActivity.xiaoxuanxiu_select = xiaoxuanxiu_select;
        if(sp == null) sp = MainActivity.context.getApplicationContext().getSharedPreferences("ScoreSelectInfo", Context.MODE_PRIVATE);;
        sp.edit().putBoolean("xiaoxuanxiu_select", ScoreActivity.xiaoxuanxiu_select).apply();
        Log.i("ScoreCustom", "setXiaoxuanxiu_select:\t" + xiaoxuanxiu_select);
    }

    public static boolean isPE_select() {
        return PE_select;
    }

    public static void setPE_select(boolean PE_select) {
        ScoreActivity.PE_select = PE_select;
        if(sp == null) sp = MainActivity.context.getApplicationContext().getSharedPreferences("ScoreSelectInfo", Context.MODE_PRIVATE);;
        sp.edit().putBoolean("PE_select", ScoreActivity.PE_select).apply();
        Log.i("ScoreCustom", "setPE_select:\t" + PE_select);
    }

    public static boolean isChongxiu_select() {
        return chongxiu_select;
    }

    public static void setChongxiu_select(boolean chongxiu_select) {
        ScoreActivity.chongxiu_select = chongxiu_select;
        if(sp == null) sp = MainActivity.context.getApplicationContext().getSharedPreferences("ScoreSelectInfo", Context.MODE_PRIVATE);;
        sp.edit().putBoolean("chongxiu_select", ScoreActivity.chongxiu_select).apply();
        Log.i("ScoreCustom", "isChongxiu_select:\t" + chongxiu_select);
    }

    public static boolean isReLoadSocreList() {
        return reloadData;
    }

    public static void setReLoadSocreList(boolean reLoadScoreList) {
        ScoreActivity.reloadData = reLoadScoreList;
        getScoreList();
    }

    public static void saveCJCXScore(){
        if(context != null){
            CJCX.saveCJCXJSON(context);
        }
        else{
            Log.e("CJCXScore", "ScoreActivity.context is NULL! Can't save!");
        }
    }

    public static void loadCJCXScore(){
        if(context != null){
            if(sp != null && sp.contains("CJCXScore")){
                try {
                    CJCX.saveCJCXJSON(context, new org.json.JSONObject(sp.getString("CJCXScore", "")));
                    sp.edit().remove("CJCXScore").apply();
                    Log.i("CJCXScore", "Save pri.");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            CJCX.loadCJCXTermJSON(context);
            CJCX.loadCJCXJSON(context);
        }
        else{
            Log.e("CJCXScore", "ScoreActivity.context is NULL! Can't load!");
        }
    }

    public static void saveCJCXTerm(){
        if(context != null){
            CJCX.saveCJCXTermJSON(context);
        }
        else{
            Log.e("CJCXScore", "ScoreActivity.context is NULL! Can't save!");
        }
    }

    public static void loadCJCXTerm(){
        if(context != null){
            CJCX.loadCJCXTermJSON(context);
        }
        else{
            Log.e("CJCXScore", "ScoreActivity.context is NULL! Can't load!");
        }
    }

    public static void setRequiredScoreSum(int requiredScoreSum) {
        ScoreActivity.requiredScoreSum = requiredScoreSum;
    }

    public static void setRequiredGPASum(double requiredGPASum) {
        ScoreActivity.requiredGPASum = requiredGPASum;
    }

    public static void setRequiredCreditSum(double requiredCreditSum) {
        ScoreActivity.requiredCreditSum = requiredCreditSum;
    }

    public static void setRequired_custom_ScoreSum(int required_custom_ScoreSum) {
        ScoreActivity.required_custom_ScoreSum = required_custom_ScoreSum;
    }

    public static void setRequired_custom_GPASum(double required_custom_GPASum) {
        ScoreActivity.required_custom_GPASum = required_custom_GPASum;
    }

    public static void setRequired_custom_CreditSum(double required_custom_CreditSum) {
        ScoreActivity.required_custom_CreditSum = required_custom_CreditSum;
    }

    public static HashMap<Integer, String> getIndex_id() {
        return index_id;
    }

    public static void setIndex_id(HashMap<Integer, String> index_id) {
        ScoreActivity.index_id = index_id;
    }
}