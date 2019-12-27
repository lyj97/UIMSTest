package com.lu.mydemo.View.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lu.mydemo.Activity.MainActivity;
import com.lu.mydemo.Activity.NoneScoreCourseActivity;
import com.lu.mydemo.Notification.AlertCenter;
import com.lu.mydemo.R;
import com.lu.mydemo.sample.adapter.BaseAdapter;
import com.lu.mydemo.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.lu.mydemo.CJCX.CJCX;
import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.UIMS.UIMS;
import com.lu.mydemo.Utils.Exam.ExamSchedule;
import com.lu.mydemo.Utils.Thread.MyThreadController;
import com.lu.mydemo.View.PopWindow.AddCourseExamPopupWindow;
import com.lu.mydemo.View.PopWindow.LoginGetSelectCoursePopWindow;

import static com.lu.mydemo.View.Fragment.ExamFragment.getTimeDistance;

public class NoneScoreCourseFragment extends Fragment {

    private LinearLayout myFragmentLayout;
    private NoneScoreCourseActivity context;

    SharedPreferences sharedPreferences;

    Spinner spinner;
    private ArrayList<String> termList;

    Button searchButton;
    Button deleteSavedCourseInformationButton;
//    ListView listView;

    private AddCourseExamPopupWindow popupWindow;

    private SwipeRecyclerView recyclerView;
    private BaseAdapter myAdapter;
    private List<HashMap<String, Object>> dataList;

    LinearLayout dataArea;

    int delete_local_course_information_button_layout_hight;

    public HashMap<String, String> termName_TermId = new HashMap<>();
    public HashMap<String, String> termId_termName = new HashMap<>();

    public LoginGetSelectCoursePopWindow popWindow;

    public static boolean flush = false;
    public static boolean isCourseListShowing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myFragmentLayout = (LinearLayout) inflater.inflate(R.layout.fragment_none_score_course, container, false);
        return myFragmentLayout;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        flush = false;
        context = ((NoneScoreCourseActivity) getActivity()).getContext();

        sharedPreferences = context.getSharedPreferences("CourseHistory", Context.MODE_PRIVATE);
        dataArea = myFragmentLayout.findViewById(R.id.nono_score_course_data_area);
        spinner = myFragmentLayout.findViewById(R.id.get_none_score_course_term_spinner);
        searchButton = myFragmentLayout.findViewById(R.id.get_none_score_course_button);
        deleteSavedCourseInformationButton = myFragmentLayout.findViewById(R.id.delete_saved_course_information_button);
//        listView = myFragmentLayout.findViewById(R.id.get_none_score_course_list_view);
        recyclerView = myFragmentLayout.findViewById(R.id.get_none_score_course_SwipeRecyclerView);

        changeTheme();

        delete_local_course_information_button_layout_hight = deleteSavedCourseInformationButton.getLayoutParams().height;

        deleteSavedCourseInformationButton.setVisibility(View.INVISIBLE);
        deleteSavedCourseInformationButton.getLayoutParams().height = 0;
        deleteSavedCourseInformationButton.requestLayout();

        setSpinnerItems();

        searchButton.setOnClickListener(new View.OnClickListener() {
            String termName = "";
            String termId = "";

            @Override
            public void onClick(View v) {
                try{
                    termName = spinner.getSelectedItem().toString();
                    termId = termName_TermId.get(termName);

                    if(termName == null || termId == null || !(termName.length() > 0) || !(termId.length() > 0)){
//                        AlertCenter.showWarningAlert(context, "数据出错");
                        List<Exception> exceptions = UIMS.getExceptions();
                        exceptions.add(0, new IllegalArgumentException("[TermName]=" + termName + "[TermId]=" + termId));
                        AlertCenter.showErrorAlertWithReportButton(context, "数据错误!", exceptions, UIMS.getUser());
                        return;
                    }

                    /**
                     * TODO TEST
                     */

                    if(sharedPreferences.contains("CourseHistory_" + termId)){
                        AlertCenter.showLoading(context, "由本地加载【" + termName + "】数据中，请稍候...");
                        UIMS.setCourseHistoryJSON(JSONObject.fromObject(sharedPreferences.getString("CourseHistory_" + termId, "")));
                        UIMS.dealNoScoreCourseWithCJCXScore(CJCX.getId_JSON().keySet());
                        getNoneScoreCourseSuccess();
                        return;
                    }

                    AlertCenter.showAlert(context,"本地暂未缓存【" + termName + "】数据，请连接校园网获取数据.");
                    LoginGetSelectCoursePopWindow window = new LoginGetSelectCoursePopWindow(context, termName, myFragmentLayout.findViewById(R.id.none_score_course_layout).getHeight(), myFragmentLayout.findViewById(R.id.none_score_course_layout).getWidth());
                    window.setFocusable(true);
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    window.showAtLocation(myFragmentLayout.findViewById(R.id.none_score_course_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                    popWindow = window;

                    Log.i("TermName", termName);
                } catch(Exception e){
                    e.printStackTrace();
                    getInformationFailed();
                }
//                if(termName != null && termName.length() > 0) getNoneScoreCourse(termName_TermId.get(termName));
//                else getInformationFailed();
            }
        });

        myAdapter = createAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem collectItem = new SwipeMenuItem(context);
                collectItem.setImage(getResources().getDrawable(R.drawable.ic_access_time_black_24dp));
                collectItem.setText("设置考试时间");
                collectItem.setHeight(ViewGroup.MarginLayoutParams.MATCH_PARENT);
                collectItem.setBackground(getResources().getDrawable(R.drawable.shape_collect_swap_menu_background));

                rightMenu.addMenuItem(collectItem);
            }
        });

        MyAddExamListener addExamListener = new MyAddExamListener();
        recyclerView.setOnItemMenuClickListener(addExamListener);
        recyclerView.setOnItemClickListener(addExamListener);

        recyclerView.setAdapter(myAdapter);

        deleteSavedCourseInformationButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sharedPreferences.edit().remove("CourseHistory_" + termName_TermId.get(spinner.getSelectedItem().toString())).apply();
                showResponse("已删除【" + spinner.getSelectedItem().toString() + "】数据.");
//                dataList = new ArrayList<>();
//                listView.setAdapter(new SimpleAdapter(context, datalist, R.layout.list_item_course, new String[]{"title", "context1"}, new int[]{R.id.get_none_score_course_title, R.id.get_none_score_course_context1}));
                flushList(new ArrayList());
                deleteSavedCourseInformationButton.getLayoutParams().height = 0;
                deleteSavedCourseInformationButton.setVisibility(View.INVISIBLE);
                dataArea.requestLayout();
                isCourseListShowing = false;
                return true;
            }
        });

        deleteSavedCourseInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertCenter.showAlert(context, "您只需刷新【选课发生变化】学期的数据",
                        "一般情况下，您只需要点击首页“更新信息”按钮，即可查看最新的“成绩未发布课程”.\n" +
                                "只有在进行了“选课”/“退补选”后，才需要刷新本学期选课数据.\n" +
                                "如需刷新当前学期数据，请长按“删除本学期缓存数据”.\n");
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flushList(new ArrayList());
                deleteSavedCourseInformationButton.getLayoutParams().height = 0;
                deleteSavedCourseInformationButton.setVisibility(View.INVISIBLE);
                dataArea.requestLayout();
                isCourseListShowing = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("NoneScoreCourseFragment", "isVisibleToUser " + isVisibleToUser);
        Log.i("NoneScoreCourseFragment", "flush " + flush);
        if (isVisibleToUser) {
            // 相当于onResume()方法
            if(flush && isCourseListShowing){
                flushList();
                flush = false;
            }
        } else {
            // 相当于onpause()方法
            flush = false;
        }
    }

    public View getView(){
        return myFragmentLayout;
    }

    private void setSpinnerItems(){
        if(MainActivity.isLocalValueLoaded){
            termList = getTermArray();
            spinner.setAdapter(new ArrayAdapter(context, R.layout.list_item_spinner, R.id.select_text_item, termList));
            spinner.setSelection(termList.indexOf(UIMS.getTermName()));
        }
        else{
            showResponse("本地暂无教学学期数据，且您还未登录，请登录后重试。");
//            finish();
        }
    }

    private void flushList(List list){
        dataList = list;
        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged(dataList);

        ExamFragment.setFlush();
    }

    private void flushList(){
        dataList = getCourseList();
        myAdapter.notifyDataSetChanged(dataList);

        ExamFragment.setFlush();
    }

    private ArrayList<String> getTermArray(){
        ArrayList<String> terms = new ArrayList<>();
        termId_termName = UIMS.getTermId_termName();
        Iterator<Map.Entry<String, String>> iterator = termId_termName.entrySet().iterator();
        Map.Entry<String, String> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            termName_TermId.put(entry.getValue(), entry.getKey());
            terms.add(entry.getValue());
        }
//        showResponse("termId_termName.size:\t" + termId_termName.size());
        Log.i("termId_termName.size", "" + termId_termName.size());
        Collections.sort(terms, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        return terms;
    }

    private void getNoneScoreCourse(final String termID){
        getNoneScoreCourse(termID, null);
    }

    public void getNoneScoreCourse(final String termID, UIMS myUims){

        final List<Map<String, Object>> datalist = getListHint("查询中...");
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flushList(new ArrayList());
                deleteSavedCourseInformationButton.getLayoutParams().height = 0;
                deleteSavedCourseInformationButton.setVisibility(View.INVISIBLE);
                dataArea.requestLayout();
            }
        });

        final UIMS uims = (myUims == null ? MainActivity.uims : myUims);
        if(uims == null){
            AlertCenter.showAlert(context, "本地暂无【" + termId_termName.get(termID) + "】数据，且您还未登录，请登录后重试。");
            getNoneScoreCourseFailed();
            return;
        }
        AlertCenter.showLoading(context, "加载【" + termId_termName.get(termID) + "】数据中，请稍候...");
        MyThreadController.commit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (uims.getCourseHistory(termID)) {
                        sharedPreferences.edit().putString("CourseHistory_" + termID, UIMS.getCourseHistoryJSON().toString()).apply();
                        getNoneScoreCourseSuccess();
                    } else {
                        getInformationFailed();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    showResponse("加载数据失败！");
                    ((NoneScoreCourseActivity) context).finish();
                }
            }
        });
    }

    private void getNoneScoreCourseSuccess(){
        isCourseListShowing = true;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertCenter.hideAlert(context);
                deleteSavedCourseInformationButton.getLayoutParams().height = delete_local_course_information_button_layout_hight;
                deleteSavedCourseInformationButton.setVisibility(View.VISIBLE);
                flushList(getCourseList());
                dataArea.requestLayout();
            }
        });
    }

    public void getNoneScoreCourseFailed() {
        isCourseListShowing = false;
        flushList(new ArrayList());
    }

    private void getInformationFailed(){
        isCourseListShowing = false;
//        showResponse("Get Information failed!");
        showResponse("获取信息失败！请尝试点击\"更新信息\"按钮.");
        AlertCenter.hideAlert(context);

        flushList(new ArrayList());
//        finish();
    }

    private List<HashMap<String, Object>> getCourseList(){
        List<HashMap<String,Object>> dataList = new ArrayList<>();
        HashMap<String,Object> map;

        HashMap<String, JSONObject> courseID_courseJSON = UIMS.getNoScoreCourseId_course();
        HashMap<String, String> courseSelectTypeId_courseSelectTypeName = UIMS.getCourseSelectTypeId_courseSelectTypeName();
        JSONObject courseJSON;
        JSONObject courseInfo;
//        JSONObject teachingTerm;
        JSONObject apl;

        String courseName;
//        String termName;
        String selectType;
        String credit;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Locale.setDefault(Locale.CHINA);

        try {

            Iterator<Map.Entry<String, JSONObject>> iterator = courseID_courseJSON.entrySet().iterator();
            Map.Entry<String, JSONObject> entry;
            while (iterator.hasNext()){
                map = new HashMap<>();

                entry = iterator.next();
                courseJSON = entry.getValue();
//                Log.i("Course", courseJSON.toString());
                courseInfo = courseJSON.getJSONObject("lesson").getJSONObject("courseInfo");
//                teachingTerm = courseJSON.getJSONObject("teachingTerm");
                apl = courseJSON.getJSONObject("apl");

                courseName = courseInfo.getString("courName");
//                termName = teachingTerm.getString("termName");
                selectType = apl.getString("selectType");
                try {
                    credit = apl.getString("credit");
                } catch (Exception e){
                    try {
                        credit = apl.getJSONObject("planDetail").getString("credit");
                    }catch (Exception e1){
                        e1.printStackTrace();
                        credit = "--";
                        AlertCenter.showWarningAlert(context, "Error", e1.getMessage());
                    }
//                    e.printStackTrace();
                }


                map.put("title", courseName + "（" + courseSelectTypeId_courseSelectTypeName.get(selectType) + "）");
                map.put("context1", "学分：" + credit);
                map.put("type", selectType);

                if(ExamSchedule.containsTitle(courseName)){

                    String time_str = ExamSchedule.getExamTime(courseName);
                    Date exam_date = df.parse(time_str);
                    String time_left_str;

                    int day_distance = getTimeDistance(new Date(), exam_date);

                    if(day_distance == 0){
                        time_left_str = " 今天考试啦，祝顺利~";
                    }
                    else if(day_distance > 0){
                        time_left_str = "【距考试 " + day_distance + " 天】";
                    }
                    else{
                        time_left_str = "(考试已结束)";
                    }

//                    map.put("context1", map.get("context1") + "  考试时间" + time_str + time_left_str);
                    map.put("context1", map.get("context1") + "    " + time_left_str);
                }

                dataList.add(map);
            }

            if(dataList.size() == 0){
                map = new HashMap<>();

                map.put("title", "本学期暂无成绩未发布的课程~");
                map.put("context1", "");
                map.put("type", "");

                dataList.add(map);
            }

            //按类别排序
            Collections.sort(dataList, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                    return ((String) o1.get("type")).compareTo((String) o2.get("type"));
                }
            });

            return dataList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Map<String, Object>> getListHint(String str){
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map;

        try {
            map = new HashMap<>();

            map.put("title", str);
            map.put("context1", "");
            map.put("type", "");

            dataList.add(map);

            return dataList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadSuccess(){
        SharedPreferences sp = MainActivity.context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        sp.edit().putString("ScoreJSON", UIMS.getScoreJSON().toString()).apply();
        sp.edit().putString("StudentJSON", UIMS.getStudentJSON().toString()).apply();
        sp.edit().putString("CourseTypeJSON", UIMS.getCourseTypeJSON().toString()).apply();
        sp.edit().putString("CourseSelectTypeJSON", UIMS.getCourseSelectTypeJSON().toString()).apply();
        sp.edit().putString("ScoreStatisticsJSON", UIMS.getScoreStatisticsJSON().toString()).apply();
        sp.edit().putString("TermJSON", UIMS.getTermJSON().toString()).apply();

    }

    private void setListviewAdapter(final ListView listView, final ListAdapter adapter){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                listView.requestLayout();
            }
        });
    }

    public void dismissPopWindow(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popWindow.dismiss();
            }
        });
    }

    public static void setFlush(){
        flush = true;
    }

    protected MainAdapter createAdapter() {
        return new NoneScoreCourseAdapter(context);
    }

    class NoneScoreCourseAdapter extends MainAdapter {

        private List<Map<String,Object>> mDataList;

        public NoneScoreCourseAdapter(Context context){
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
            return new NoneScoreCourseAdapter.ViewHolder(getInflater().inflate(R.layout.list_item_course, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
            holder.setData((String) mDataList.get(position).get("title"), (String) mDataList.get(position).get("context1"), (String) mDataList.get(position).get("type"));
        }

        class ViewHolder extends MainAdapter.ViewHolder {

            TextView tvTitle;
            TextView tvContext;

            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.get_none_score_course_title);
                tvContext = itemView.findViewById(R.id.get_none_score_course_context1);
            }

            public void setData(String title, String context1, String type) {

                tvTitle.setTextColor(ColorManager.getNews_normal_text_color());

                if(type.equals("3060")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_bixiu));
                }else if(type.equals("3061")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_xuanxiu));
                }else if(type.equals("3062")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_xianxuan));
                }else if(type.equals("3065")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_xiaoxuanxiu));
                }else if(type.equals("3064")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_tiyu));
                }else if(type.equals("3066")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_kuazhuanye));
                }else if(type.equals("3067")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_tongguozaixiu));
                }else if(type.equals("3068")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_buxiu));
                }else if(type.equals("3099")){
                    tvTitle.setTextColor(getResources().getColor(R.color.course_bangdinggongxuan));
                }

                this.tvTitle.setText(title);
                this.tvContext.setText(context1);

            }
        }

    }

    class MyAddExamListener implements OnItemMenuClickListener, OnItemClickListener {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
            menuBridge.closeMenu();

//            Log.i("NewsSavedActivity", "menuBridge.getPosition:\t" + menuBridge.getPosition());

            openPopWindow(adapterPosition);
        }

        @Override
        public void onItemClick(View view, int adapterPosition) {
            openPopWindow(adapterPosition);
        }

        public void openPopWindow(int adapterPosition){
            popupWindow = new AddCourseExamPopupWindow(context, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "已添加【" + popupWindow.getTitle() + "】考试", Toast.LENGTH_SHORT).show();
                    ExamSchedule.add(popupWindow.getTitle(), popupWindow.getExam_date() + " " + popupWindow.getExam_time(), popupWindow.getExam_place());
                    flushList();
                    ExamFragment.setFlush();
                }
            }, context.findViewById(R.id.course_and_exam_layout).getHeight(), context.findViewById(R.id.course_and_exam_layout).getWidth(), ((String) dataList.get(adapterPosition).get("title")).split("（")[0]);
            popupWindow.setFocusable(true);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //添加pop窗口关闭事件
            popupWindow.setOnDismissListener(new poponDismissListener());
//                popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            //显示窗口
            popupWindow.showAtLocation(context.findViewById(R.id.course_and_exam_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        }
    }

    private void changeTheme(){
//        Window window = context.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ColorManager.getPrimaryColor());

//        myFragmentLayout.findViewById(R.id.none_score_course_layout).setBackground(ColorManager.getMainBackground_full());
        searchButton.setBackground(ColorManager.getInternetInformationButtonBackground());
        spinner.setBackground(ColorManager.getSpinnerBackground());
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     * @author cg
     *
     */
    class poponDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }

    public void showResponse(final String string){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
//                showAlert(string);
            }
        });
    }

}
