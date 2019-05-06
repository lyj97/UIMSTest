package com.lu.mydemo.sample.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lu.mydemo.R;

import java.util.List;
import java.util.Map;


/**
 * Created by YOLANDA on 2016/7/22.
 */
public class MainAdapter extends BaseAdapter<MainAdapter.ViewHolder> {

    private List<Map<String,Object>> mDataList;

    public MainAdapter(Context context) {
        super(context);
    }

    public void notifyDataSetChanged(List dataList) {
        this.mDataList = (List<Map<String,Object>>)dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.course_schedule_change_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData((String) mDataList.get(position).get("title"), (String) mDataList.get(position).get("context1"), (String) mDataList.get(position).get("context2"));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvContext1;
        TextView split1;
        TextView tvContext2;
        TextView split2;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.course_schedule_change_list_item_title);
            tvContext1 = itemView.findViewById(R.id.course_schedule_change_list_item_context1);
            split1 = itemView.findViewById(R.id.course_schedule_change_list_item_split1);
            tvContext2 = itemView.findViewById(R.id.course_schedule_change_list_item_context2);
            split2 = itemView.findViewById(R.id.course_schedule_change_list_item_split2);
        }

        public void setData(String title, String context1, String context2) {
            try {
                if(context2 == null || context2.equals("")){
                    this.tvTitle.setText(title);
                    this.tvContext1.setText(context1);
                    this.split1.setText("放假");
                    this.tvContext2.setText("");
                    this.tvContext2.setWidth(0);
                    this.split2.setText("");
                    this.split2.setWidth(0);
                }
                else{
                    this.tvTitle.setText(title);
                    this.tvContext1.setText(context1);
                    this.tvContext2.setText(context2);
                }
            } catch (Exception e){
                e.printStackTrace();
                this.tvTitle.setText(title);
                this.tvContext1.setText(context1);
                this.tvContext2.setText(context2);
            }
        }

        public void setData(String title, String department, String time, boolean flagTop){}
    }

}
