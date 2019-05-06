package Utils.Course;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.lu.mydemo.LoginActivity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CourseScheduleChange {

    private static SharedPreferences sp;
    /**
     * courseScheduleChangeJSON
     * {
     *     updateTime:"yyyy-MM-dd"
     *     information:""
     *     value[
     *          {
     *              title:"",
     *              previousTime:"yyyy-MM-dd",
     *              changeTime:"yyyy-MM-dd"
     *          },...
     *     ]
     * }
     */
    private static JSONObject courseScheduleChangeJSON;
    private static HashMap<String, JSONObject> previousTime_changeTime = new HashMap<>();

    public static HashMap<String, JSONObject> getPreviousTime_changeTime(Context context) {

        sp = context.getSharedPreferences("CourseScheduleChange", Context.MODE_PRIVATE);

        if(sp.contains("CourseScheduleChange")){
            try{
                courseScheduleChangeJSON = JSONObject.fromObject(sp.getString("CourseScheduleChange", ""));
                JSONArray value = courseScheduleChangeJSON.getJSONArray("value");

                JSONObject temp;
                for(int i=0; i<value.size(); i++){
                    temp = value.getJSONObject(i);
                    previousTime_changeTime.put(temp.getString("previousTime"), temp);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return previousTime_changeTime;
    }

    public static boolean delete(String previousTime) {

        if(previousTime_changeTime.remove(previousTime) != null){
            Log.i("CourseScheduleChange", "removed one item.");
            return save();
        }
        else {
            Log.i("CourseScheduleChange", "is NULL.");
            return false;
        }

    }

    public static boolean add(JSONObject object){
        try {
            Log.i("CourseScheduleChange", "Added:\t" + object);
            previousTime_changeTime.put(object.getString("previousTime"), object);
            return save();
        } catch (Exception e){
            e.printStackTrace();;
            return false;
        }
    }

    public static boolean add(JSONArray array){
        try {
            JSONObject object;
            for(int i=0; i<array.size(); i++){
                object = array.getJSONObject(i);
                Log.i("CourseScheduleChange", "Added:\t" + object);
                previousTime_changeTime.put(object.getString("previousTime"), object);
            }
            Log.i("CourseScheduleChange", "Add Array: number:\t" + array.size());
            return save();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean add(String title, String previousTime, String changeTime){
        JSONObject object = new JSONObject();
        object.put("title", title);
        object.put("previousTime", previousTime);
        object.put("changeTime", changeTime);
        Log.i("CourseScheduleChange", "Added:\t" + object);
        previousTime_changeTime.put(previousTime, object);
        return save();
    }

    private static boolean save(){

        Log.i("CourseScheduleChange", "Save");

        if(sp != null){
            if(courseScheduleChangeJSON != null){
                try{
                    JSONObject temp;
                    JSONArray array = new JSONArray();

                    Iterator<Map.Entry<String, JSONObject>> iterator = previousTime_changeTime.entrySet().iterator();

                    while (iterator.hasNext()){
                        temp = iterator.next().getValue();
                        Log.i("CourseScheduleChange", "Saved:\t" + temp);
                        array.add(temp);
                    }

                    courseScheduleChangeJSON.put("value", array);

                    sp.edit().putString("CourseScheduleChange", courseScheduleChangeJSON.toString()).apply();

                    LoginActivity.setReLoadTodayCourse(true);
                    reLoad();

                    Log.i("CourseScheduleChange", "SaveSucceed!");
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    Log.i("CourseScheduleChange", "Save Failed 1");
                    return false;
                }
            }
            else{
                try {
                    courseScheduleChangeJSON = new JSONObject();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    courseScheduleChangeJSON.put("updateTime", simpleDateFormat.format(new Date()));

                    JSONArray array = new JSONArray();

                    Iterator<Map.Entry<String, JSONObject>> iterator = previousTime_changeTime.entrySet().iterator();

                    while (iterator.hasNext()) {
                        array.add(iterator.next().getValue());
                    }

                    courseScheduleChangeJSON.put("value", array);

                    sp.edit().putString("CourseScheduleChange", courseScheduleChangeJSON.toString()).apply();

                    LoginActivity.setReLoadTodayCourse(true);
                    reLoad();

                    Log.i("CourseScheduleChange", "SaveSucceed!");
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    Log.i("CourseScheduleChange", "Save Failed 2");
                    return false;
                }
            }
        }

        Log.i("CourseScheduleChange", "Save Failed 3");

        return false;

    }

    public static boolean containsDate(Context context, String date){
        Log.i("CourseScheduleChange", "containsDate:\t" + date);
        showHashMap();
        if(sp == null || courseScheduleChangeJSON == null){
            getPreviousTime_changeTime(context);
        }
        return (previousTime_changeTime.containsKey(date));
    }

    public static boolean containsDate(Context context, JSONArray array){
        for(int i=0; i<array.size(); i++){
            if(! previousTime_changeTime.containsKey(array.getJSONObject(i).getString("previousTime"))) return false;
        }
        return true;
    }

    public static String getDate(Context context, String date){
        if(sp == null || courseScheduleChangeJSON == null){
            getPreviousTime_changeTime(context);
        }
        return (previousTime_changeTime.get(date).getString("changeTime"));
    }

    private static void reLoad(){
        if(sp == null) return;
        if(sp.contains("CourseScheduleChange")){
            try{
                courseScheduleChangeJSON = JSONObject.fromObject(sp.getString("CourseScheduleChange", ""));
                JSONArray value = courseScheduleChangeJSON.getJSONArray("value");

                JSONObject temp;
                previousTime_changeTime = new HashMap<>();
                for(int i=0; i<value.size(); i++){
                    temp = value.getJSONObject(i);
                    previousTime_changeTime.put(temp.getString("previousTime"), temp);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void load(Context context){
        if(sp == null || courseScheduleChangeJSON == null){
            getPreviousTime_changeTime(context);
        }
    }

    public static void test(Context context){
        if(sp == null || courseScheduleChangeJSON == null){
            getPreviousTime_changeTime(context);
        }
        add("五一假期", "2019-04-28", "2019-05-02");
        add("五一假期", "2019-05-05", "2019-05-03");
    }

    private static void showHashMap(){
        Iterator<Map.Entry<String, JSONObject>> iterator = previousTime_changeTime.entrySet().iterator();

        while (iterator.hasNext()) {
            Log.i("CourseScheduleChange", iterator.next().getValue().toString());
        }
    }

}
