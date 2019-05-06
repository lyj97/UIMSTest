package Utils.News;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class News {

    private static SharedPreferences sp;//context使用ApplicationContext

    /**
     * newsJSON
     {
     *     updateTime:"yyyy-MM-dd"
     *     value[
     *          {
     *              title:"",
     *              department:"",
     *              time:"", //收藏时间
     *              link:""
     *              abs_link:""
     *              flagTop:"" //警告：此时应标记本地置顶操作
     *          },...
     *     ]
     * }
     */
    private static JSONObject newsJSON;
    private static List<Map<String, Object>> savedNewsList;
    private static HashMap<String, Integer> newsTitle_listIndex;

    public static List<Map<String, Object>> getSavedNewsList(Context context){
        if(sp == null) sp = context.getSharedPreferences("CourseScheduleChange", Context.MODE_PRIVATE);
        if(newsJSON == null) newsJSON = JSONObject.fromObject(sp.getString("SavedNewsList", "{}"));

        if(savedNewsList == null){
            savedNewsList = new ArrayList<>();
            newsTitle_listIndex = new HashMap<>();
            try {
                JSONArray array = newsJSON.getJSONArray("value");
                for (int i = 0; i < array.size(); i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    JSONObject aNewsItem = array.getJSONObject(i);
                    map.put("title", aNewsItem.getString("title"));
                    map.put("department", aNewsItem.getString("department"));
                    map.put("time", aNewsItem.getString("time"));
                    map.put("link", aNewsItem.getString("link"));
                    map.put("abs_link", aNewsItem.getString("abs_link"));
                    map.put("flagTop", aNewsItem.getBoolean("flagTop"));//警告：此时应标记本地置顶操作
                    savedNewsList.add(map);
                    newsTitle_listIndex.put(aNewsItem.getString("title"), i);
                }
            } catch (Exception e){
                e.printStackTrace();
                return savedNewsList;
            }

        }

        return savedNewsList;

    }

    public static boolean contains(String title, Context context){
        if(sp == null || newsJSON == null) getSavedNewsList(context);
        return newsTitle_listIndex.containsKey(title);
    }

    public static void add(JSONObject newsItem, Context context){
        add(newsItem, context, false);
    }

    public static void add(JSONObject newsItem, Context context, boolean usePriTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(sp == null || newsJSON == null) getSavedNewsList(context);
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", newsItem.getString("title"));
        map.put("department", newsItem.getString("department"));
        if(usePriTime) map.put("time", newsItem.getString("time"));
        else map.put("time", simpleDateFormat.format(new Date()));
        map.put("link", newsItem.getString("link"));
        map.put("abs_link", newsItem.getString("abs_link"));
        map.put("flagTop", false);//警告：此时应标记本地置顶操作
        savedNewsList.add(map);
        newsTitle_listIndex.put(newsItem.getString("title"), savedNewsList.size() - 1);
        try {
            save();
            flushNewsList();
        }
        catch (Exception e){
            e.printStackTrace();
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
    }

    public static boolean delete(String title){
        try{
            delete(newsTitle_listIndex.get(title));
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(int index){
        savedNewsList.remove(index);
        try {
            save();
            flushNewsList();
        }
        catch (Exception e){
            e.printStackTrace();
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
        return true;
    }

    public static boolean flagTop(int index){

        try {
            Map<String, Object> temp = savedNewsList.get(index);
            for(int i=index; i>0; i--){
                savedNewsList.set(i, savedNewsList.get(i - 1));
            }
            temp.put("flagTop", true);
            savedNewsList.set(0, temp);
            try {
                save();
                flushNewsList();
            }
            catch (Exception e){
                e.printStackTrace();
            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }).start();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public static void save(){
        Iterator<Map<String, Object>> iterator = savedNewsList.iterator();
        JSONArray array = new JSONArray();
        while (iterator.hasNext()){
            Map map = iterator.next();
            JSONObject object = new JSONObject();
            object.put("title", map.get("title"));
            object.put("department", map.get("department"));
            object.put("time", map.get("time"));
            object.put("link", map.get("link"));
            object.put("abs_link", map.get("abs_link"));
            object.put("flagTop", map.get("flagTop"));
            array.add(object);
        }
        JSONObject object = new JSONObject();
        object.put("updateTime", "");
        object.put("value", array);
        sp.edit().putString("SavedNewsList", object.toString()).apply();
    }

    public static void flushNewsList() throws Exception{

        if(sp == null || newsJSON == null) throw new IllegalAccessException("未获取本地存储列表情况下尝试刷新");

        newsJSON = JSONObject.fromObject(sp.getString("SavedNewsList", ""));
        savedNewsList = new ArrayList<>();
        newsTitle_listIndex = new HashMap<>();
        JSONArray array = newsJSON.getJSONArray("value");
        for(int i=0; i<array.size(); i++){
            HashMap<String, Object> map = new HashMap<>();
            JSONObject aNewsItem = array.getJSONObject(i);
            map.put("title", aNewsItem.getString("title"));
            map.put("department", aNewsItem.getString("department"));
            map.put("time", aNewsItem.getString("time"));
            map.put("link", aNewsItem.getString("link"));
            map.put("abs_link", aNewsItem.getString("abs_link"));
            map.put("flagTop", aNewsItem.getBoolean("flagTop"));//警告：此时应标记本地置顶操作
            savedNewsList.add(map);
            newsTitle_listIndex.put(aNewsItem.getString("title"), i);
        }
    }

}
