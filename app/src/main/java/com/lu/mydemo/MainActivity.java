package com.lu.mydemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import android.widget.SimpleAdapter;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.lu.mydemo.R;
import com.tapadoo.alerter.Alerter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import UIMS.UIMS;

public class MainActivity extends Activity
{

    TextView scoreStatisticsTextView;
    BarChart barChart;

    ArrayList<String> xValues = new ArrayList<>();
    ArrayList<BarEntry> yValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreStatisticsTextView = findViewById(R.id.textView_ScoreStatistics);

        List<Map<String, Object>> datalist = getScoreList();
        ListView lv = (ListView) findViewById(R.id.lv);

        lv.setAdapter(new SimpleAdapter(this, datalist, R.layout.list_item, new String[]{"title", "context"}, new int[]{R.id.title, R.id.context}));
        lv.setOnItemClickListener(new OnItemClickListener() {
            //list点击事件
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                // TODO: Implement this method
                Toast.makeText(MainActivity.this, "TEMP", Toast.LENGTH_SHORT).show();
//                Alerter.create(MainActivity.this)
//                        .setTitle("Title")
//                        .setText("■\n" +
//                                "■■■\n" +
//                                "■■■■\n" +
//                                "■■\n" +
//                                "■■■■■\n")
//                        .enableSwipeToDismiss()
//                        .show();
            }


        });

    }

    private List<Map<String,Object>> getScoreList(){

        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map;

//        dataList.add(getScoreStatistics());
        getScoreStatistics();

        JSONObject scoreJSON = UIMS.getScoreJSON();

        JSONArray scores = scoreJSON.getJSONArray("value");

        try {

            for (int i = 0; i < scores.size(); i++) {
                map = new HashMap<>();

                JSONObject temp = scores.getJSONObject(i);
                JSONObject teachingTerm = temp.getJSONObject("teachingTerm");
                JSONObject course = temp.getJSONObject("course");
                String courName = course.getString("courName");
                String termName = teachingTerm.getString("termName");
                String courScore = temp.getString("score");
                String isReselect = (temp.getString("isReselect").contains("N")) ? "否" : "是";

                map.put("title",courName);
                map.put("context","score:" + courScore + "  " +
                        "Reselect:" + isReselect + "  " +
                        termName);

                dataList.add(map);

            }

            return dataList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private Map<String,Object> getScoreStatistics() {

        JSONObject scoreStatistics = UIMS.getScoreStatisticsJSON();

        String avgScoreBest = scoreStatistics.getJSONArray("value").getJSONObject(0).getString("avgScoreBest");
        String avgScoreFirst = scoreStatistics.getJSONArray("value").getJSONObject(0).getString("avgScoreFirst");
        String gpaFirst = scoreStatistics.getJSONArray("value").getJSONObject(0).getString("gpaFirst");
        String gpaBest = scoreStatistics.getJSONArray("value").getJSONObject(0).getString("gpaBest");

        Map<String, Object> map = new HashMap<>();
//        map.put("title","绩点统计");
//        map.put("context","【首次成绩】：平均成绩:" + avgScoreFirst + "  " + "平均绩点:" + gpaFirst + "  \n" +
//                "【最好成绩】：平均成绩:" + avgScoreBest + "  " + "平均绩点:" + gpaBest);

        String title = "绩点统计";
        String value = "【首次成绩】\n平均成绩:" + avgScoreFirst + "  " + "平均绩点:" + gpaFirst + "  \n" +
                "【最好成绩】\n平均成绩:" + avgScoreBest + "  " + "平均绩点:" + gpaBest;
        scoreStatisticsTextView.setText(title + "\n" + value);

        return map;

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {

            return;

        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0); // 计算子项View 的宽高

            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        // listView.getDividerHeight()获取子项间分隔符占用的高度

        // params.height最后得到整个ListView完整显示需要的高度

        listView.setLayoutParams(params);

    }

}