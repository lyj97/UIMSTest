package com.lu.mydemo.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.tapadoo.alerter.Alerter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.Utils.News.News;
import com.lu.mydemo.Utils.Thread.MyThreadController;

public class NewsSavedActivity extends BaseActivity {

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

    private TextView navigation_back;

    private static JSONObject tempJsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_saved);

        swipeRecyclerView = findViewById(R.id.activity_news_saved_SwipeRecyclerView);
        myAdapter = createAdapter();

        navigation_back = findViewById(R.id.activity_news_saved_navigation_back_text);

        changeTheme();

        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {

                if(TextUtils.isEmpty((String) dataList.get(adapterPosition).get("department")) || TextUtils.isEmpty((String)  dataList.get(adapterPosition).get("time"))){
                    AlertCenter.showAlert(NewsSavedActivity.this, (String) dataList.get(adapterPosition).get("title"));
                    return;
                }

//                Intent intent = new Intent(NewsSavedActivity.this, NewsDetailActivity.class);
//
//                Bundle bundle = new Bundle();
//                bundle.putString("title", (String) dataList.get(adapterPosition).get("title"));
//                bundle.putString("department", (String) dataList.get(adapterPosition).get("department"));
//                bundle.putString("time", "收藏于：" + dataList.get(adapterPosition).get("time").toString().substring(2));
//                bundle.putString("link", (String) dataList.get(adapterPosition).get("link"));
//                bundle.putString("abs_link", (String) dataList.get(adapterPosition).get("abs_link"));
//                bundle.putBoolean("flagTop", (boolean) dataList.get(adapterPosition).get("flagTop"));
//
//                intent.putExtra("bundle", bundle);
//                startActivity(intent);

                Intent intent1 = new Intent(NewsSavedActivity.this, WebViewActivity.class);
                Bundle web_bundle = new Bundle();
                web_bundle.putString("link", (String) dataList.get(adapterPosition).get("abs_link"));
                intent1.putExtra("bundle", web_bundle);
                startActivity(intent1);

            }
        });

        swipeRecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem collectItem = new SwipeMenuItem(NewsSavedActivity.this);
                collectItem.setImage(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
                collectItem.setText("删除");
                collectItem.setHeight(ViewGroup.MarginLayoutParams.MATCH_PARENT);
                collectItem.setBackground(getResources().getDrawable(R.drawable.shape_collect_swap_menu_background));

                rightMenu.addMenuItem(collectItem);

                collectItem = new SwipeMenuItem(NewsSavedActivity.this);
                collectItem.setImage(getResources().getDrawable(R.drawable.ic_vertical_align_top_black_24dp));
                collectItem.setText("置顶");
                collectItem.setHeight(ViewGroup.MarginLayoutParams.MATCH_PARENT);
                collectItem.setBackground(getResources().getDrawable(R.drawable.shape_collect_swap_menu_background));

                rightMenu.addMenuItem(collectItem);
            }
        });

        swipeRecyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                menuBridge.closeMenu();

                Log.i("NewsSavedActivity", "menuBridge.getPosition:\t" + menuBridge.getPosition());

                if(menuBridge.getPosition() == 1) {
                    News.flagTop(adapterPosition);
                    flushList();
                }
                else if(menuBridge.getPosition() == 0) {

                    Alerter.create(NewsSavedActivity.this)
                            .setText("已删除通知【" + dataList.get(adapterPosition).get("title") + "】\n\n" +
                                    "如需撤销，请点击此处.")
                            .enableSwipeToDismiss()
                            .setProgressColorRes(R.color.color_alerter_progress_bar)
                            .setDuration(6000)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    News.add(tempJsonObject, getApplicationContext(), true);
                                    flushList();
                                    Alerter.hide();
                                }
                            })
                            .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                            .show();
                    tempJsonObject = new JSONObject();
                    tempJsonObject.put("title", dataList.get(adapterPosition).get("title"));
                    tempJsonObject.put("department", dataList.get(adapterPosition).get("department"));
                    tempJsonObject.put("time", dataList.get(adapterPosition).get("time"));
                    tempJsonObject.put("link", dataList.get(adapterPosition).get("link"));
                    tempJsonObject.put("abs_link", dataList.get(adapterPosition).get("abs_link"));
                    tempJsonObject.put("flagTop", dataList.get(adapterPosition).get("flagTop"));
                    News.delete(adapterPosition);
                    flushList();
                    NewsActivity.setFlush();

                }

            }
        });

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        flushList();
        if(dataList.size() == 0){
            AlertCenter.showAlert(this, "暂无收藏", "在“校内通知”页面收藏重要的通知吧.\n\n" +
                    "Tips:\t 校内通知界面，左滑收藏.");
        }
    }

    private void loadFailed() {
        AlertCenter.showAlert(this, "加载失败，请稍后重试.");
        Map<String, Object> map = new HashMap<>();
        map.put("title", "加载失败，请稍后重试...");
        map.put("department", "");
        map.put("time", "");
        map.put("link", "");
        map.put("abs_link", "");
        map.put("flagTop", false);
        dataList.add(map);
    }

    private void loadSucceed(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.hide();

                swipeRecyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged(dataList);

            }
        });
    }

    private void flushList(){
        dataList = News.getSavedNewsList(getApplicationContext());
        swipeRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);
    }

    private void getNewsList(){
        AlertCenter.showLoading(this, "加载中，请稍候...");
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {

                dataList = News.getSavedNewsList(getApplicationContext());
                loadSucceed();

            }
        });
    }

    protected MainAdapter createAdapter() {
        return new NewsSavedActivity.NewsSavedListAdapter(this);
    }

    class NewsSavedListAdapter extends MainAdapter{

        private List<Map<String,Object>> mDataList;

        public NewsSavedListAdapter(Context context){
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
            return new NewsSavedActivity.NewsSavedListAdapter.ViewHolder(getInflater().inflate(R.layout.list_item_news, parent, false));
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

            @Override
            public void setData(String title, String department, String time, boolean flagTop) {

                tvTitle.setTextColor(ColorManager.getNews_normal_text_color());

                if(flagTop){
                    tvTitle.setTextColor(ColorManager.getNews_notice_text_color());
                    this.tvTitle.setText("[收藏置顶] " + title);
                }
                else {
                    tvTitle.setTextColor(ColorManager.getPrimaryColor());
                    this.tvTitle.setText(title);
                }

                this.tvDepartment.setText(department);
                this.tvTime.setText("收藏时间:\t" + time);

                if(department.length() == 0 && time.length() == 0){
                    tvTitle.setGravity(Gravity.CENTER);
                }

            }
        }

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

        findViewById(R.id.activity_news_saved).setBackground(ColorManager.getMainBackground_full());
    }

}
