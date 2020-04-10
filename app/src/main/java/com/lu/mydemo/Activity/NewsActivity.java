package com.lu.mydemo.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.OA.NewsClient;
import com.lu.mydemo.R;
import com.lu.mydemo.Utils.Time.TimeZoneTransform;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.ToolFor2045_Site.GetInternetInformation;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.Utils.News.News;
import com.lu.mydemo.Utils.Thread.MyThreadController;

public class NewsActivity extends BaseActivity {

    private TextView myCollectionTextView;
    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

//    private SearchView searchView;

    private LinearLayout openLinkLayout;
    private TextView hideText;

    private TextView flagOA;
    private TextView flagServer;

    private EditText newsLinkText;
    private Button openLinkButton;

    private TextView navigation_back;

    private GetInternetInformation loadClient = new GetInternetInformation();

    private int currentPage = 1;

    public int remoteNeedPage = 0;

    public static Context context;

    private static JSONObject tempJsonObject;

    public static boolean needFlush = true;
    public static boolean triedOA = false;
    public static boolean isLoadedFromOA = false;

    public static boolean triedServer = false;
    public static boolean isLoadedFromServer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        myCollectionTextView = findViewById(R.id.activity_news_my_collection);
        swipeRecyclerView = findViewById(R.id.activity_news_SwipeRecyclerView);
        myAdapter = createAdapter();

//        searchView = findViewById(R.id.activity_news_search_view);

        openLinkLayout = findViewById(R.id.activity_news_open_link_layout);
        hideText = findViewById(R.id.activity_news_hide_text);

        flagOA = findViewById(R.id.activity_news_flag_oa);
        flagServer = findViewById(R.id.activity_news_flag_server);

        newsLinkText = findViewById(R.id.activity_news_open_link_layout_url);
        openLinkButton = findViewById(R.id.activity_news_open_link_layout_commit_button);

        navigation_back = findViewById(R.id.activity_news_navigation_back_text);

        changeTheme();

        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {

                if(isLoadedFromServer){

                }

                if(TextUtils.isEmpty((String) dataList.get(adapterPosition).get("department")) || TextUtils.isEmpty((String)  dataList.get(adapterPosition).get("time"))){
                    AlertCenter.showAlert(NewsActivity.this, (String) dataList.get(adapterPosition).get("title"));
                    return;
                }

                //校内通知已经不能校外访问 停用NewsDetailActivity
                Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title", (String) dataList.get(adapterPosition).get("title"));
                bundle.putString("department", (String) dataList.get(adapterPosition).get("department"));
                bundle.putString("time", (String) dataList.get(adapterPosition).get("time"));
                bundle.putString("link", (String) dataList.get(adapterPosition).get("link"));
                bundle.putString("abs_link", (String) dataList.get(adapterPosition).get("abs_link"));
                bundle.putBoolean("flagTop", (boolean) dataList.get(adapterPosition).get("flagTop"));

                bundle.putBoolean("isLoadedFromOA", isLoadedFromOA);
                bundle.putBoolean("isLoadedFromServer", isLoadedFromServer);

                intent.putExtra("bundle", bundle);
                startActivity(intent);

//                Intent intent1 = new Intent(NewsActivity.this, WebViewActivity.class);
//                Bundle web_bundle = new Bundle();
//                web_bundle.putString("link", (String) dataList.get(adapterPosition).get("abs_link"));
//                intent1.putExtra("bundle", web_bundle);
//                startActivity(intent1);
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
                    try{
                        News.delete((String) dataList.get(adapterPosition).get("title"));
                        //只刷新数据变化的条目
                        myAdapter.notifyItemChanged(adapterPosition);
                    }
                    catch (Exception e){
//                        AlertCenter.showWarningAlert(NewsActivity.this, "删除收藏失败！");
                        AlertCenter.showErrorAlertWithReportButton(NewsActivity.this, "删除收藏失败!", e, UIMS.getUser());
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

        openLinkLayout.setVisibility(View.GONE);
        hideText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(openLinkLayout.getVisibility() == View.VISIBLE){
                    openLinkLayout.setVisibility(View.GONE);
                    hideText.setBackground(getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp));
                }
                else{
                    openLinkLayout.setVisibility(View.VISIBLE);
                    hideText.setBackground(getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
                }
            }
        });

        openLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String news_url = newsLinkText.getText().toString();
                if(news_url.contains("oa.jlu.edu.cn")) {

                    String[] strings = news_url.split("oa.jlu.edu.cn");
                    Log.i("NewsActivity", "URLArr:\t" + Arrays.asList(strings));

                    if (strings.length != 2) {
                        AlertCenter.showErrorAlert(NewsActivity.this, "错误：" + strings.length + "\n" +
                                Arrays.asList(strings));
                    }
                    else {

                        Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("title", "");
                        bundle.putString("department", "");
                        bundle.putString("time", "");
                        bundle.putString("link", strings[1]);
                        bundle.putString("abs_link", "");
                        bundle.putBoolean("flagTop", false);

                        intent.putExtra("bundle", bundle);
                        startActivity(intent);

                    }
                }
                else {
                    AlertCenter.showErrorAlert(NewsActivity.this, "只能打开oa链接\n（如：http://oa.jlu.edu.cn/defaultroot/...）");
                }
            }
        });

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
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                JSONObject object = loadClient.searchNews(queryStr);
                AlertCenter.showAlert(NewsActivity.this, "Response", object.toString());
            }
        });
    }

    private void loadFailed(final boolean isFirst) {
        if(!triedOA){
            getNewsListFromOA();
            return;
        }
        else {
            flagOA.setBackground(new ColorDrawable(Color.RED));
        }
        if(!triedServer){
            getNewsListFromServer();
            return;
        }
        else {
            flagServer.setBackground(new ColorDrawable(Color.RED));
        }
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
//        Log.i("NewsActivity", "IsFirst:" + isFirst);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.hide();
                if(isLoadedFromOA){
                    flagOA.setBackground(new ColorDrawable(Color.GREEN));
                }
                if(isLoadedFromServer){
                    flagServer.setBackground(new ColorDrawable(Color.GREEN));
                }
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
                Log.i("NewsActivity", "News list size:" + dataList.size());
            }
        });
    }

    private void getNewsList(){
        if(isLoadedFromOA){
            getNewsListFromOA();
            return;
        }
        if(isLoadedFromServer){
            getNewsListFromServer();
            return;
        }
        AlertCenter.showLoading(this, "加载中，请稍候...");
        MyThreadController.commit(new Runnable() {
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
        });
    }

    private void getNewsListFromOA(){
        AlertCenter.showLoading(this, "由OA加载中，请稍候...");
        triedOA = true;
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = false;
                if(currentPage == 1) {
                    dataList = new ArrayList<>();
                    isFirst = true;
                }
                JSONObject object = NewsClient.getNewsList(currentPage);
                if(object != null) {
                    try {
                        JSONArray array = object.getJSONArray("value");
                        JSONObject item;
                        for (int i = 0; i < array.size(); i++) {
                            item = array.getJSONObject(i);
                            Map<String, Object> map = new HashMap<>();
                            map.put("title", item.getString("title"));
                            map.put("department", item.getString("department"));
                            map.put("time", item.getString("time"));
                            map.put("link", item.getString("link"));
                            map.put("abs_link", item.getString("abs_link"));
                            map.put("flagTop", item.getBoolean("flagTop"));
                            map.put("is_new", item.getBoolean("is_new"));
                            dataList.add(map);
                        }

                        currentPage++;
                        isLoadedFromOA = true;

                        loadSucceed(isFirst);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        swipeRecyclerView.loadMoreError(0, "加载失败，请稍后重试...");
                        loadFailed(isFirst);
                    }
                }
            }
        });
    }

    private void getNewsListFromServer(){
        AlertCenter.showLoading(this, "由Server加载中，请稍候...");
        triedServer = true;
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        final SimpleDateFormat format_server = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT);
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = false;
                if(currentPage == 1) {
                    dataList = new ArrayList<>();
                    isFirst = true;
                }
                JSONObject object = NewsClient.getNewsListFromServer(currentPage, "");
                if(object != null) {
                    try {
                        JSONArray array = object.getJSONArray("data");
                        JSONObject item;
                        for (int i = 0; i < array.size(); i++) {
                            item = array.getJSONObject(i);
                            Map<String, Object> map = new HashMap<>();
                            map.put("title", item.getString("title"));
                            map.put("department", item.getString("department"));
                            String publishTimeStr = item.getString("publish_time").replaceAll("T", " ");
                            if(!TextUtils.isEmpty(publishTimeStr)) {
                                map.put("time", TimeZoneTransform.gmt2Gmt_8Str(format_server.parse(publishTimeStr), format));
                            }
                            map.put("link", item.getString("link"));
                            map.put("abs_link", item.getString("link"));
                            map.put("flagTop", false);
                            map.put("is_new", false);
                            dataList.add(map);
                        }

                        currentPage++;
                        isLoadedFromServer = true;

                        loadSucceed(isFirst);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        swipeRecyclerView.loadMoreError(0, "加载失败，请稍后重试...");
                        loadFailed(isFirst);
                    }
                }
            }
        });
    }

    public static void setFlush(){
        needFlush = true;
    }

    public int getRemoteNeedPage() {
        return remoteNeedPage;
    }

    public void setRemoteNeedPage(int remoteNeedPage) {
        this.remoteNeedPage = remoteNeedPage;
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

    public void changeTheme(){
        super.changeTheme();
        findViewById(R.id.activity_news).setBackground(ColorManager.getMainBackground_full());
    }

}
