package com.lu.mydemo.Activity.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lu.mydemo.R;
import com.lu.mydemo.Utils.Fragement.FragmentLabels;
import com.lu.mydemo.Utils.Score.ScoreInf;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;
import java.util.Map;

/**
 * 创建时间: 2019/12/24 10:26 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class ScoreFragment extends Fragment {

    private FragmentLabels label = FragmentLabels.SCORE_FRAGMENT;

    private static ScoreFragment instance;

    private PageViewModel pageViewModel;

    private Context context;

    public static ScoreFragment newInstance(Context context){
        if(instance == null){
            instance = new ScoreFragment();
            instance.context = context;
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        pageViewModel.updateScore();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_score, container, false);
        final SwipeRecyclerView recyclerView = root.findViewById(R.id.score_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //考虑加载性能优化
        recyclerView.setHasFixedSize(true);//设置子布局（每项）高度相等，避免每次计算高度
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        final MainAdapter adapter = createAdapter();
        recyclerView.setAdapter(adapter);
        pageViewModel.getData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String str) {
                if(PageViewModel.UPDATE_SCORE_FLAG.equals(str)) {
                    adapter.notifyDataSetChanged(ScoreInf.getDataList());
                }
            }
        });
        return root;
    }

    protected MainAdapter createAdapter() {
        return new ScoreListAdapter(context);
    }

    class ScoreListAdapter extends MainAdapter{

        private List<Map<String,Object>> mDataList;

        public ScoreListAdapter(Context context){
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
            return new ScoreListAdapter.ViewHolder(getInflater().inflate(R.layout.list_item_score_in_main, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
            holder.setData((String) mDataList.get(position).get("title"),
                    (String) mDataList.get(position).get("context1"),
                    (String) mDataList.get(position).get("context2"),
                    (String) mDataList.get(position).get("context3"),
                    (Double) mDataList.get(position).get("context4"),
                    (String) mDataList.get(position).get("context5"),
                    (String) mDataList.get(position).get("type"),
                    position);
        }

        class ViewHolder extends MainAdapter.ViewHolder {

            TextView courseNameTv;
            TextView courseMarkTv;
            TextView publishTimeTv;
            TextView creditTv;
            TextView gpaTv;

            public ViewHolder(View itemView) {
                super(itemView);
                courseNameTv = itemView.findViewById(R.id.score_course_name);
                courseMarkTv = itemView.findViewById(R.id.score_mark);
                publishTimeTv = itemView.findViewById(R.id.score_publish_time);
                creditTv = itemView.findViewById(R.id.score_credit);
                gpaTv = itemView.findViewById(R.id.score_gpa);
            }

            public void setData(String title, String context1, final String context2, String context3,
                                Double context4, String context5, String type, int position) {
                courseNameTv.setText(title);
                courseMarkTv.setText(context2);
                publishTimeTv.setText(context3);
                String creditStr = "学分: " + context4;
                creditTv.setText(creditStr);
                String gpaStr = "绩点: " + context5;
                gpaTv.setText(gpaStr);
            }
        }

    }

}
