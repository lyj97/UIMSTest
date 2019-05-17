package PopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lu.mydemo.R;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Config.ColorManager;

public class RecommendCourseScheduleChangePopWindow extends PopupWindow {

    private View mMenuView;

    private TextView recommendInformation;
    private ListView course_list;

    private Button commit_button;
    private Button cancel_button;

    private boolean ispreTimeSet = false;
    private boolean ischangeTimeSet = false;

    private Activity context;

    private static JSONObject courseScheduleChangeJSON;

    public RecommendCourseScheduleChangePopWindow(final Activity context, JSONObject jsonObject, final View.OnClickListener commitButtonOnClickListener, int height, int width) {
        super(context);
        this.context = context;
        ispreTimeSet = false;
        ischangeTimeSet = false;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_window_recommend_course_schedule_change, null);
        recommendInformation = mMenuView.findViewById(R.id.recommend_course_schedule_change_information);
        course_list = mMenuView.findViewById(R.id.recommend_course_schedule_change_list);
        commit_button = mMenuView.findViewById(R.id.recommend_course_schedule_change_commit_button);
        cancel_button = mMenuView.findViewById(R.id.recommend_course_schedule_change_cancel_button);

        changeTheme();

        courseScheduleChangeJSON = jsonObject;

        //取消按钮
        cancel_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });

        //设置按钮监听
        commit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitButtonOnClickListener.onClick(commit_button);
                dismiss();
            }
        });

        Locale.setDefault(Locale.CHINA);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(width);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(height);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationActivity);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(ColorManager.getPopwindow_background_color());
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.recommend_course_schedule_change_pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    public void prepareData(final View.OnClickListener onClickListener){
        course_list.setAdapter(new CourseListAdapter(context, getCourseList(), R.layout.course_schedule_change_list_item, new String[]{"title","context1","context2"}, new int[]{R.id.course_schedule_change_list_item_title, R.id.course_schedule_change_list_item_context1, R.id.course_schedule_change_list_item_context2,}));
        recommendInformation.setText(courseScheduleChangeJSON.getString("information"));
        onClickListener.onClick(mMenuView);
    }

    private List<Map<String, Object>> getCourseList(){
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Locale.setDefault(Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        final String[] dayOfWeekName = new String[]{"","星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
        int day_of_week;

        try{
            JSONArray value = courseScheduleChangeJSON.getJSONArray("value");

            JSONObject temp;
            String timeStr2;
            for(int i=0; i<value.size(); i++){
                temp = value.getJSONObject(i);

                map = new HashMap<>();

                map.put("title", temp.getString("title"));

                cal.setTime(df.parse(temp.getString("previousTime")));
                day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
                if (day_of_week <= 0)
                    day_of_week = 7;
                map.put("context1", temp.getString("previousTime") + "(" + dayOfWeekName[day_of_week] + ")");

                timeStr2 = temp.getString("changeTime");
                if(timeStr2.equals("0000-00-00")){
                    map.put("context2", "");
                }
                else {
                    cal.setTime(df.parse(timeStr2));
                    day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    if (day_of_week <= 0)
                        day_of_week = 7;
                    map.put("context2", temp.getString("changeTime") + "(" + dayOfWeekName[day_of_week] + ")");
                }

                dataList.add(map);
            }
            dataList.sort(new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return ((String)o1.get("context1")).compareTo((String)o2.get("context1"));
                }
            });

            return dataList;

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private String getTimeString(int year, int month, int dayOfMonth){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(year);
        stringBuilder.append('-');
        month += 1;
        if(month < 10){
            stringBuilder.append(0);
            stringBuilder.append(month);
        }
        else{
            stringBuilder.append(month);
        }
        stringBuilder.append('-');
        if(dayOfMonth < 10){
            stringBuilder.append(0);
            stringBuilder.append(dayOfMonth);
        }
        else{
            stringBuilder.append(dayOfMonth);
        }
        return stringBuilder.toString();
    }

    class CourseListAdapter extends SimpleAdapter {
        List<? extends Map<String, ?>> mdata;

        public CourseListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                            int[] to) {
            super(context, data, resource, from, to);
            this.mdata = data;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LinearLayout.inflate(mMenuView.getContext(), R.layout.course_schedule_change_list_item, null);
            }//这个TextView是R.layout.list_item里面的，修改这个字体的颜色
            TextView context1 = convertView.findViewById(R.id.course_schedule_change_list_item_context1);
            TextView split1 = convertView.findViewById(R.id.course_schedule_change_list_item_split1);
            TextView context2 = convertView.findViewById(R.id.course_schedule_change_list_item_context2);
            TextView split2 = convertView.findViewById(R.id.course_schedule_change_list_item_split2);

            ((TextView) mMenuView.findViewById(R.id.course_schedule_change_list_item_title)).setTextColor(ColorManager.getPrimaryColor());

            //根据Key值取出装入的数据，然后进行比较
            try {
                String ss = ((String) mdata.get(position).get("context2"));
                if(ss == null || ss.equals("")){
                    split1.setText("放假");
//                    context2.setText("");
//                    context2.setWidth(0);
                    split2.setText("");
//                    split2.setWidth(0);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            //Log.i("TAG", Integer.toString(position));
            //Log.i("TAG", (String) mData.get(position).get("text"));
            return super.getView(position, convertView, parent);
        }
    }

    private void changeTheme(){
        Log.i("Theme", "Change theme.");
        mMenuView.findViewById(R.id.recommend_course_schedule_change_pop_layout_title).setBackgroundColor(ColorManager.getPrimaryColor());
        course_list.setBackground(ColorManager.getMainBackground());
        commit_button.setBackground(ColorManager.getInternetInformationButtonBackground_full());
    }

}
