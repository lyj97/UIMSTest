package com.lu.mydemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.News.News;

public class NewsSavedActivity extends AppCompatActivity {

    private SwipeRecyclerView swipeRecyclerView;
    private BaseAdapter myAdapter;
    private List<Map<String, Object>> dataList;

    private static JSONObject tempJsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_saved);

        swipeRecyclerView = findViewById(R.id.activity_news_saved_SwipeRecyclerView);
        myAdapter = createAdapter();

        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {

                if(((String) dataList.get(adapterPosition).get("department")).length() == 0 && ((String) dataList.get(adapterPosition).get("time")).length() == 0){
                    showAlert((String) dataList.get(adapterPosition).get("title"));
                    return;
                }

                Intent intent = new Intent(NewsSavedActivity.this, NewsDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title", (String) dataList.get(adapterPosition).get("title"));
                bundle.putString("department", (String) dataList.get(adapterPosition).get("department"));
                bundle.putString("time", "收藏时间：" + dataList.get(adapterPosition).get("time"));
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
                            .setProgressColorRes(R.color.color_21)
                            .setDuration(6000)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    News.add(tempJsonObject, getApplicationContext(), true);
                                    flushList();
                                    Alerter.hide();
                                }
                            })
                            .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        flushList();
        if(dataList.size() == 0){
            showAlert("暂无收藏", "在“校内通知”页面收藏重要的通知吧.\n\n" +
                    "Tips:\t 校内通知界面，左滑收藏.");
        }
    }

    private void loadFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAlert("加载失败，请稍后重试.");
                Map<String, Object> map = new HashMap<>();
                map.put("title", "加载失败，请稍后重试...");
                map.put("department", "");
                map.put("time", "");
                map.put("link", "");
                map.put("abs_link", "");
                map.put("flagTop", false);
                dataList.add(map);
            }
        });
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
        showLoading("加载中，请稍候...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                dataList = News.getSavedNewsList(getApplicationContext());
                loadSucceed();

            }
        }).start();
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
            return new NewsSavedActivity.NewsSavedListAdapter.ViewHolder(getInflater().inflate(R.layout.news_list_item, parent, false));
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
                    tvTitle.setTextColor(getResources().getColor(R.color.color_notice_text));
                    this.tvTitle.setText("[收藏置顶] " + title);
                }
                else {
                    tvTitle.setTextColor(Color.parseColor("#03A9F4"));
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

    public void showAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NewsSavedActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                        .show();
            }
        });
    }

    public void showLoading(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NewsSavedActivity.this)
                        .setText(message)
                        .enableProgress(true)
                        .setProgressColorRes(R.color.color_21)
                        .setDuration(10000)
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                        .show();
            }
        });
    }

    public void showAlert(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(NewsSavedActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_background))
                        .show();
            }
        });
    }

}
