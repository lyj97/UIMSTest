package com.lu.mydemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Config.ColorManager;
import ToolFor2045_Site.GetInternetInformation;

public class TestFunctionActivity extends AppCompatActivity {

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

    private TextView navigation_back;

    private GetInternetInformation loadClient = new GetInternetInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_function);

        swipeRecyclerView = findViewById(R.id.activity_test_function_SwipeRecyclerView);
        myAdapter = createAdapter();

        navigation_back = findViewById(R.id.activity_test_function_navigation_back_text);

        changeTheme();

        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                switch (adapterPosition){
                    case 0:{
                        if(MainActivity.isLocalValueLoaded)
                            startActivity(new Intent(TestFunctionActivity.this, PingjiaoActivity.class));
                        else
                            AlertCenter.showErrorAlert(TestFunctionActivity.this, "还没有已经保存的信息，缺少必要信息无法教评哦，点击首页\"更新信息\"再试试吧(*^_^*).");
                        break;
                    }
//                    case 1:{
//                        try{
//                            if(Build.VERSION.SDK_INT > 26) {
//                                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
//
//                                ShortcutInfo shortcut = new ShortcutInfo.Builder(TestFunctionActivity.this, "id1")
//                                        .setShortLabel("News Activity.")
//                                        .setLongLabel("News Activity.")
////                                        .setIcon(Icon.createWithResource(, R.drawable.icon_website))
//                                        .setIntent(new Intent(Intent.ACTION_VIEW, null, TestFunctionActivity.this, NewsActivity.class))
////                                        .setIntent(intent)
////                                        .setActivity(new ComponentName(getPackageName(), TestFunctionActivity.class.getName()))
//                                        .build();
//
//                                shortcutManager.removeAllDynamicShortcuts();
//                                shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
//                            }
//                            else {
//                                AlertCenter.showErrorAlert(TestFunctionActivity.this, "Current API:\t" + Build.VERSION.SDK_INT + "\n" +
//                                        "Needed API:\t26 or higher.");
//                            }
//                        } catch (Exception e){
//                            e.printStackTrace();
//                            AlertCenter.showErrorAlert(TestFunctionActivity.this, e.getMessage());
//                        }
//                        break;
//                    }
                    default:{
                        String link = (String) dataList.get(adapterPosition).get("link");
                        if(link == null || !link.startsWith("http")){
                            AlertCenter.showWarningAlert(TestFunctionActivity.this, "ERROR", "link:\t" + link);
                            return;
                        }
                        Intent intent = new Intent(TestFunctionActivity.this, WebViewActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("link", link);

                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }
                }
            }
        });

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTestFunctionList();

    }

    private void getTestFunctionList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("title","教评");
                    map.put("description", "教学质量评价");
                    dataList.add(map);

//                    //TODO ShortCutTest
//                    map = new HashMap<>();
//                    map.put("title","ShortCutTest");
//                    map.put("description", "ShortCutTest");
//                    dataList.add(map);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRecyclerView.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged(dataList);
                            getRemoteItems();
                        }
                    });

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getRemoteItems(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject object = loadClient.getTestItems();
                    JSONArray array = object.getJSONArray("value");
                    for(Object item : array){
                        Map<String, Object> map = new HashMap<>();
                        map.put("title", ((JSONObject) item).getString("title"));
                        map.put("description", ((JSONObject) item).getString("description"));
                        map.put("link", ((JSONObject) item).getString("link"));
                        dataList.add(map);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected MainAdapter createAdapter() {
        return new TestFunctionListAdapter(this);
    }

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        findViewById(R.id.activity_test_function).setBackground(ColorManager.getMainBackground_full());
    }

    class TestFunctionListAdapter extends MainAdapter {

        private List<Map<String,Object>> mDataList;

        public TestFunctionListAdapter(Context context){
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
            return new TestFunctionListAdapter.ViewHolder(getInflater().inflate(R.layout.list_item_news, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
            holder.setData((String) mDataList.get(position).get("title"), (String) mDataList.get(position).get("description"));
        }

        class ViewHolder extends MainAdapter.ViewHolder {

            TextView tvTitle;
            TextView tvDepartment;
            TextView tvTime;

            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.news_list_item_title);
                tvDepartment = itemView.findViewById(R.id.news_list_item_department);
                tvTime = itemView.findViewById(R.id.news_list_item_time);
            }

            public void setData(String title, String description){

                tvTitle.setText(title);
                tvDepartment.setText(description);
                tvTime.setText("");

            }
        }

    }

}
