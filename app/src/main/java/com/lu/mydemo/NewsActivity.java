package com.lu.mydemo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lu.mydemo.Notification.AlertCenter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Config.ColorManager;
import ToolFor2045_Site.GetInternetInformation;
import Utils.News.News;

public class NewsActivity extends AppCompatActivity {

    private TextView myCollectionTextView;
    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

//    private SearchView searchView;

    private TextView navigation_back;

    private GetInternetInformation loadClient = new GetInternetInformation();

    private int currentPage = 1;

    public static Context context;

    private static JSONObject tempJsonObject;

    public static boolean needFlush = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        myCollectionTextView = findViewById(R.id.activity_news_my_collection);
        swipeRecyclerView = findViewById(R.id.activity_news_SwipeRecyclerView);
        myAdapter = createAdapter();

//        searchView = findViewById(R.id.activity_news_search_view);

        navigation_back = findViewById(R.id.activity_news_navigation_back_text);

        changeTheme();

        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {

                if(((String) dataList.get(adapterPosition).get("department")).length() == 0 && ((String) dataList.get(adapterPosition).get("time")).length() == 0){
                    AlertCenter.showAlert(NewsActivity.this, (String) dataList.get(adapterPosition).get("title"));
                    return;
                }

                Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title", (String) dataList.get(adapterPosition).get("title"));
                bundle.putString("department", (String) dataList.get(adapterPosition).get("department"));
                bundle.putString("time", (String) dataList.get(adapterPosition).get("time"));
                bundle.putString("link", (String) dataList.get(adapterPosition).get("link"));
                bundle.putString("abs_link", (String) dataList.get(adapterPosition).get("abs_link"));
                bundle.putBoolean("flagTop", (boolean) dataList.get(adapterPosition).get("flagTop"));

                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

        swipeRecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                if(dataList.get(position).get("department") == null || dataList.get(position).get("time") == null || !(((String) dataList.get(position).get("department")).length() > 0) || !(((String) dataList.get(position).get("time")).length() > 0)){
                    return;
                }
                if(News.contains((String) dataList.get(position).get("title") ,getApplicationContext())){
                    SwipeMenuItem collectItem = new SwipeMenuItem(NewsActivity.this);
                    collectItem.setImage(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                    collectItem.setText("取消收藏");
                    collectItem.setHeight(ViewGroup.MarginLayoutParams.MATCH_PARENT);
                    collectItem.setBackground(getResources().getDrawable(R.drawable.shape_collect_swap_menu_background));

                    rightMenu.addMenuItem(collectItem);
                }
                else {
                    SwipeMenuItem collectItem = new SwipeMenuItem(NewsActivity.this);
                    collectItem.setImage(getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
                    collectItem.setText("收藏");
                    collectItem.setHeight(ViewGroup.MarginLayoutParams.MATCH_PARENT);
                    collectItem.setBackground(getResources().getDrawable(R.drawable.shape_collect_swap_menu_background));

                    rightMenu.addMenuItem(collectItem);
                }
            }
        });

        swipeRecyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {

                menuBridge.closeMenu();

                if(!News.contains((String) dataList.get(adapterPosition).get("title"), getApplicationContext())) {
                    tempJsonObject = new JSONObject();
                    tempJsonObject.put("title", dataList.get(adapterPosition).get("title"));
                    tempJsonObject.put("department", dataList.get(adapterPosition).get("department"));
                    tempJsonObject.put("time", dataList.get(adapterPosition).get("time"));
                    tempJsonObject.put("link", dataList.get(adapterPosition).get("link"));
                    tempJsonObject.put("abs_link", dataList.get(adapterPosition).get("abs_link"));
                    tempJsonObject.put("flagTop", dataList.get(adapterPosition).get("flagTop"));
                    News.add(tempJsonObject, getApplicationContext());

//                    showAlert("收藏成功", (String) dataList.get(adapterPosition).get("title"));
                    //只刷新数据变化的条目
                    myAdapter.notifyItemChanged(adapterPosition);
                }
                else{
                    if(!News.delete((String) dataList.get(adapterPosition).get("title"))){
                        AlertCenter.showWarningAlert(NewsActivity.this, "删除收藏失败！");
                    }
                    else{
                        //只刷新数据变化的条目
                        myAdapter.notifyItemChanged(adapterPosition);
                    }
                }

            }
        });

        myCollectionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this, NewsSavedActivity.class);
                startActivity(intent);
            }
        });

//        searchView.setQueryHint("搜索...");
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                showAlert(query);
//                searchNews(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        currentPage = 1;
        getNewsList();
        needFlush = false;

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(needFlush) {
//            currentPage = 1;
//            getNewsList();
            myAdapter.notifyDataSetChanged();
            needFlush = false;
        }
    }

    private void searchNews(final String queryStr){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject object = loadClient.searchNews(queryStr);
                AlertCenter.showAlert(NewsActivity.this, "Response", object.toString());
            }
        }).start();
    }

    private void loadFailed(final boolean isFirst) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertCenter.showAlert(NewsActivity.this, "加载失败，请稍后重试.");
                Map<String, Object> map = new HashMap<>();
                map.put("title", "加载失败，请稍后重试...");
                map.put("department", "");
                map.put("time", "");
                map.put("link", "");
                map.put("abs_link", "");
                map.put("flagTop", false);
                map.put("is_new", false);
                dataList.add(map);
                swipeRecyclerView.loadMoreError(0, "加载失败，请稍后重试...");
                if (isFirst) {
                    swipeRecyclerView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged(dataList);
                    swipeRecyclerView.loadMoreFinish(false, false);
                }
            }
        });
    }

    private void loadSucceed(final boolean isFirst){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.hide();
                if(isFirst){
                    swipeRecyclerView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged(dataList);
                    swipeRecyclerView.useDefaultLoadMore();
                    swipeRecyclerView.setLoadMoreListener(new SwipeRecyclerView.LoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            getNewsList();
                        }
                    });
                    swipeRecyclerView.loadMoreFinish(false, true);
                }
                else {
                    myAdapter.notifyDataSetChanged();
                    swipeRecyclerView.loadMoreFinish(false, true);
                }
            }
        });
    }

    private void getNewsList(){
        AlertCenter.showLoading(this, "加载中，请稍候...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = false;
                if(currentPage == 1) {
                    dataList = new ArrayList<>();
                    isFirst = true;
                }
                try {
                    JSONObject object = loadClient.getNewsList(currentPage);
                    if(object == null){
                        loadFailed(isFirst);
                        return;
                    }
                    JSONArray array = object.getJSONArray("value");
                    JSONObject item;
                    for(int i=0; i<array.size(); i++){
                        item = array.getJSONObject(i);
                        Map<String,Object> map = new HashMap<>();
                        map.put("title", item.getString("title"));
                        map.put("department", item.getString("dept"));
                        map.put("time", item.getString("time"));
                        map.put("link", item.getString("link"));
                        map.put("abs_link", item.getString("abs_link"));
                        map.put("flagTop", item.getBoolean("flagTop"));
                        map.put("is_new", item.getBoolean("is_new"));
                        dataList.add(map);
                    }

                    loadSucceed(isFirst);

                    currentPage++;

                } catch (Exception e){
                    e.printStackTrace();
                    swipeRecyclerView.loadMoreError(0, "加载失败，请稍后重试...");
                }
            }
        }).start();
    }

    public static void setFlush(){
        needFlush = true;
    }

    protected MainAdapter createAdapter() {
        return new NewsListAdapter(this);
    }

    class NewsListAdapter extends MainAdapter{

        private List<Map<String,Object>> mDataList;

        public NewsListAdapter(Context context){
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
            return new NewsListAdapter.ViewHolder(getInflater().inflate(R.layout.list_item_news, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
            holder.setData((String) mDataList.get(position).get("title"), (String) mDataList.get(position).get("department"), (String) mDataList.get(position).get("time"), (boolean) mDataList.get(position).get("flagTop"));
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

            public void setData(String title, String department, String time, boolean flagTop) {

                if(flagTop){
                    this.tvTitle.setTextColor(ColorManager.getNews_notice_text_color());
                    this.tvTitle.setText("[置顶] " + title);
                }
                else {
                    tvTitle.setTextColor(ColorManager.getNews_normal_text_color());
                    this.tvTitle.setText(title);
                }
                if(News.contains(title, getApplicationContext())){
                    this.tvTitle.setTextColor(ColorManager.getNews_collected_text_color());
                    this.tvTitle.setText(this.tvTitle.getText() + " [已收藏]");
                }

                this.tvDepartment.setText(department);
                this.tvTime.setText(time);

                if(department.length() == 0 && time.length() == 0){
                    tvTitle.setGravity(Gravity.CENTER);
                }

            }
        }

    }

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        findViewById(R.id.activity_news).setBackground(ColorManager.getMainBackground_full());
    }

}
