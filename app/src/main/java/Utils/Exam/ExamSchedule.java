package Utils.Exam;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lu.mydemo.LoginActivity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExamSchedule {

    private static SharedPreferences sp;
    /**
     * examScheduleJSON
     * {
     *     updateTime:"yyyy-MM-dd"
     *     information:""
     *     value[
     *          {
     *              title:"",
     *              xkkh:"", //选课课号关联，只区分学期与重修，不区分student
     *              time:"yyyy-MM-dd HH:mm"
     *              flagTop:"boolean"
     *          },...
     *     ]
     * }
     */
    private static JSONObject examScheduleJSON;
    private static List<JSONObject> courseTitle_exam = new ArrayList<>();
    private static HashMap<String, Integer> title_index = new HashMap<>();

    public static List<JSONObject> getCourseTitle_examTime(Context context){

        if(sp != null){
            reLoad();
            return courseTitle_exam;
        }

        sp = context.getSharedPreferences("ExamSchedule", Context.MODE_PRIVATE);

        if(sp.contains("ExamSchedule")){
            try{
                examScheduleJSON = JSONObject.fromObject(sp.getString("ExamSchedule", ""));
                JSONArray value = examScheduleJSON.getJSONArray("value");

                JSONObject temp;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

                for(int i=0; i<value.size(); i++){
                    temp = value.getJSONObject(i);
                    if(!title_index.containsKey(temp.getString("title"))) {
                        courseTitle_exam.add(temp);
                    }
                    else{
                        courseTitle_exam.set(title_index.get(temp.getString("title")), temp);
                    }
                }

                Collections.sort(courseTitle_exam, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        return o1.getString("time").compareTo(o2.getString("time"));
                    }
                });

                title_index = new HashMap<>();
                int i = 0;
                for(JSONObject object : courseTitle_exam){
                    title_index.put(object.getString("title"), i++);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return courseTitle_exam;

    }

    public static boolean delete(int index) {

        JSONObject object = courseTitle_exam.get(index);

        if(object != null){
            courseTitle_exam.remove(index);
            title_index.remove(object.getString("title"));
            Log.i("CourseScheduleChange", "removed one item.");
            return save();
        }
        else {
            Log.i("CourseScheduleChange", "is NULL.");
            return false;
        }

    }

    public static boolean containsTitle(String title){
        return title_index.containsKey(title);
    }

    public static String getExamTime(String title){
        Log.i("ExamSchedule", "getExamTime[title:\t" + title + "\tindex:\t" + title_index.get(title) + "\ttime:\t" + courseTitle_exam.get(title_index.get(title)).getString("time"));
        showHashMap();
        return courseTitle_exam.get(title_index.get(title)).getString("time");
    }

    public static boolean add(JSONObject object){
        try {
            Log.i("ExamSchedule", "Added:\t" + object);
            if(!title_index.containsKey(object.getString("title"))) {
                courseTitle_exam.add(object);
                title_index.put(object.getString("title"), courseTitle_exam.size() - 1);
            }
            else{
                courseTitle_exam.set(title_index.get(object.getString("title")), object);
            }
            return save();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static boolean add(JSONArray array){
        try {
            JSONObject object;
            for(int i=0; i<array.size(); i++){
                object = array.getJSONObject(i);
                Log.i("ExamSchedule", "Added:\t" + object);
                if(!title_index.containsKey(object.getString("title"))) {
                    courseTitle_exam.add(object);
                    title_index.put(object.getString("title"), courseTitle_exam.size() - 1);
                }
                else{
                    courseTitle_exam.set(title_index.get(object.getString("title")), object);
                }
            }
            Log.i("ExamSchedule", "Add Array: number:\t" + array.size());
            return save();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean add(String title, String time){
        return add(title, "", time);
    }

    public static boolean add(String title, String xkkh, String time){
        JSONObject object = new JSONObject();
        object.put("title", title);
        object.put("xkkh", xkkh);
        object.put("time", time);
        object.put("flagTop", false);
        Log.i("ExamSchedule", "Added:\t" + object);
        if(!title_index.containsKey(object.getString("title"))) {
            courseTitle_exam.add(object);
            title_index.put(object.getString("title"), courseTitle_exam.size() - 1);
        }
        else{
            courseTitle_exam.set(title_index.get(object.getString("title")), object);
        }
        return save();
    }

    private static boolean save(){

        Log.i("ExamSchedule", "Save");

        if(sp != null){
            if(examScheduleJSON != null){
                try{

                    JSONObject temp;
                    JSONArray array = new JSONArray();

                    Iterator<JSONObject> iterator = courseTitle_exam.iterator();

                    while (iterator.hasNext()){
                        temp = iterator.next();
                        Log.i("ExamSchedule", "Saved:\t" + temp);
                        array.add(temp);
                    }

                    examScheduleJSON.put("value", array);

                    sp.edit().putString("ExamSchedule", examScheduleJSON.toString()).apply();

                    LoginActivity.setReLoadTodayCourse(true);
                    reLoad();

                    Log.i("ExamSchedule", "SaveSucceed!");
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    Log.i("ExamSchedule", "Save Failed 1");
                    return false;
                }
            }
            else{
                try {
                    examScheduleJSON = new JSONObject();
                    android.icu.text.SimpleDateFormat simpleDateFormat = new android.icu.text.SimpleDateFormat("yyyy-MM-dd");
                    examScheduleJSON.put("updateTime", simpleDateFormat.format(new Date()));

                    JSONArray array = new JSONArray();

                    Iterator<JSONObject> iterator = courseTitle_exam.iterator();

                    while (iterator.hasNext()) {
                        array.add(iterator.next());
                    }

                    examScheduleJSON.put("value", array);

                    sp.edit().putString("ExamSchedule", examScheduleJSON.toString()).apply();

                    LoginActivity.setReLoadTodayCourse(true);
                    reLoad();

                    Log.i("ExamSchedule", "SaveSucceed!");
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    Log.i("ExamSchedule", "Save Failed 2");
                    return false;
                }
            }
        }

        Log.i("ExamSchedule", "Save Failed 3");

        return false;

    }

    private static void reLoad(){
        if(sp == null) return;
        if(sp.contains("ExamSchedule")){
            try{
                examScheduleJSON = JSONObject.fromObject(sp.getString("ExamSchedule", ""));
                JSONArray value = examScheduleJSON.getJSONArray("value");

                JSONObject temp;
                courseTitle_exam = new ArrayList<>();
                for(int i=0; i<value.size(); i++){
                    temp = value.getJSONObject(i);
                    courseTitle_exam.add(temp);
                }

                Collections.sort(courseTitle_exam, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        return o1.getString("time").compareTo(o2.getString("time"));
                    }
                });

                title_index = new HashMap<>();
                int i = 0;
                for(JSONObject object : courseTitle_exam){
                    title_index.put(object.getString("title"), i++);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void showHashMap(){
        Iterator<JSONObject> iterator = courseTitle_exam.iterator();

        while (iterator.hasNext()) {
            Log.i("ExamSchedule", iterator.next().toString());
        }
    }

}
