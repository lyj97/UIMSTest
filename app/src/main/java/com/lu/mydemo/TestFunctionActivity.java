package com.lu.mydemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import App.BaseActivity;
import Config.ColorManager;
import ToolFor2045_Site.GetInternetInformation;

public class TestFunctionActivity extends BaseActivity {

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

    private TextView navigation_back;
    private TextView page_title;

    private GetInternetInformation loadClient = new GetInternetInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_pages);

        swipeRecyclerView = findViewById(R.id.activity_test_function_SwipeRecyclerView);
        myAdapter = createAdapter();

        navigation_back = findViewById(R.id.activity_test_function_navigation_back_text);
        page_title = findViewById(R.id.activity_test_function_title_text);

        changeTheme();

        page_title.setText("测试功能");
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
                        }
                    });

                } catch (Exception e){
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
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ColorManager.getNoCloor());
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);

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

                tvTitle.setTextColor(ColorManager.getNews_normal_text_color());
                tvTitle.setText(title);
                tvDepartment.setText(description);
                tvTime.setText("");

            }
        }

    }
}
