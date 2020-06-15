package com.lu.mydemo.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.ToolFor2045_Site.GetInternetInformation;
import com.lu.mydemo.Utils.Thread.MyThreadController;

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
                        startActivity(new Intent(TestFunctionActivity.this, PingjiaoActivity.class));
                        break;
                    }
                    case 1:{
                        startActivity(new Intent(TestFunctionActivity.this, NewMainActivity.class));
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
        MyThreadController.commit(new Runnable() {
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

                    // NewMainActivity
//                    map = new HashMap<>();
//                    map.put("title","新主页");
//                    map.put("description", "邀请你参加新版主页设计，快来测试群说说你的想法吧~");
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
        });
    }

    protected MainAdapter createAdapter() {
        return new TestFunctionListAdapter(this);
    }

    public void changeTheme(){
        super.changeTheme();
        findViewById(R.id.activity_test_function).setBackground(ColorManager.getMainBackground_full());
    }

    public static class TestFunctionListAdapter extends MainAdapter {

        private List<Map<String,Object>> mDataList;

        TestFunctionListAdapter(Context context){
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
            return new ViewHolder(getInflater().inflate(R.layout.list_item_news, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
            holder.setData((String) mDataList.get(position).get("title"), (String) mDataList.get(position).get("description"));
        }

        public static class ViewHolder extends MainAdapter.ViewHolder {

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
