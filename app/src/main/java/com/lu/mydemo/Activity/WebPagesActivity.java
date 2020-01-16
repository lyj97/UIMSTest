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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.ToolFor2045_Site.GetInternetInformation;
import com.lu.mydemo.Utils.Thread.MyThreadController;

public class WebPagesActivity extends BaseActivity {

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

    private TextView navigation_back;

    private GetInternetInformation loadClient = new GetInternetInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_pages);

        swipeRecyclerView = findViewById(R.id.activity_test_function_SwipeRecyclerView);
        myAdapter = createAdapter();

        navigation_back = findViewById(R.id.activity_test_function_navigation_back_text);

        changeTheme();

        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                String link = (String) dataList.get(adapterPosition).get("link");
                if (link == null || !link.startsWith("http")) {
                    AlertCenter.showWarningAlert(WebPagesActivity.this, "ERROR", "link:\t" + link);
                    return;
                }
                Intent intent = new Intent(WebPagesActivity.this, WebViewActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("link", link);

                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getRemoteItems();

    }

    private void getRemoteItems(){
        AlertCenter.showLoading(WebPagesActivity.this, "加载中...");
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                try{
//                    dataList = new ArrayList<>();
//                    JSONObject object = loadClient.getTestItems();
//                    JSONArray array = object.getJSONArray("value");
//                    for(Object item : array){
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("title", ((JSONObject) item).getString("title"));
//                        map.put("description", ((JSONObject) item).getString("description"));
//                        map.put("link", ((JSONObject) item).getString("link"));
//                        dataList.add(map);
//                    }

                    initPageList();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRecyclerView.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged(dataList);
                            AlertCenter.hideAlert(WebPagesActivity.this);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void initPageList() {
        dataList = new ArrayList<>();
        JSONObject object = loadClient.getWebPagesItems();
        JSONArray array = object.getJSONArray("data");
        for (Object item : array) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", ((JSONObject) item).getString("title"));
            map.put("description", ((JSONObject) item).getString("description"));
            map.put("link", ((JSONObject) item).getString("link"));
            dataList.add(map);
        }
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
