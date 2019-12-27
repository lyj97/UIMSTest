package com.lu.mydemo.OA;

import com.lu.mydemo.ToolFor2045_Site.GetInternetInformation;
import com.lu.mydemo.ToolFor2045_Site.InformationUploader;
import com.lu.mydemo.Utils.Thread.MyThreadController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ConcurrentHashMap;

public class NewsClient {

    public static final String OA_NEWS_LIST_LINK = "https://oa.jlu.edu.cn/defaultroot/PortalInformation!jldxList.action?1=1&channelId=179577&startPage=";
    public static final String OA_NEWS_BASE_URI = "https://oa.jlu.edu.cn/defaultroot/";

    public static ConcurrentHashMap<Integer, JSONObject> page_newsListMap = new ConcurrentHashMap<>();

    public static boolean hasStarted = false;

    public static JSONObject getNewsList(int page){
        JSONObject news_json = new JSONObject();
        JSONArray array = new JSONArray();
        Document document;
        try {
            document = Jsoup.connect(OA_NEWS_LIST_LINK + page).get();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        Element title_a;
        Element department_a;
        Element time_span;
        if(document != null){
            Element element = document.getElementById("itemContainer");
            JSONObject object;
            for(Element div : element.children()){
                object = new JSONObject();
                title_a = div.select("a").first();
                department_a = div.select("a").get(1);
                time_span = div.select("span").first();
                try {
                    if(title_a.text().contains("[置顶]")){
                        title_a.text(title_a.text().replace("[置顶]", ""));
                        object.put("flagTop", true);
                    }
                    else object.put("flagTop", false);
                    object.put("title", title_a.text());
                    object.put("department", department_a.text());
                    object.put("time", time_span.text());
                    object.put("link", title_a.attr("href"));
                    object.put("abs_link", OA_NEWS_BASE_URI + title_a.attr("href"));
                    object.put("is_new", false);
                    object.put("id", object.getString("link").substring(object.getString("link").indexOf("?id=") + 4, object.getString("link").indexOf("&channelId")));
                    array.add(object);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            news_json.put("value", array);
            page_newsListMap.put(page, news_json);
            return news_json;
        }
        return null;
    }

    public static String getNewsDetailFromOA(String url){
        Document document;
        try {
            document = Jsoup.connect(url).get();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        Elements content_box = document.select("div.content_box");
        if(content_box.size() == 1){
            return content_box.get(0).toString();
        }
        return "";
    }

    public static void initUploadCheck(){
        if(hasStarted) return;
        hasStarted = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JSONObject uploadJSON;
                try{
                    GetInternetInformation client = new GetInternetInformation();
                    uploadJSON = client.getRemoteNewsPage();
                    if(uploadJSON != null){
                        int page = 0;
                        try{
                            page = uploadJSON.getJSONObject("data").getInt("page");
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        if(page > 0){
                            initUploadThread(page);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        MyThreadController.commit(runnable);
    }

    public static void uploadOADetail(String title, String department, String abs_link, String time){
        try {
            InformationUploader.uploadOAInformation(title, department, abs_link, getNewsDetailFromOA(abs_link), time);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void initUploadThread(final int page){
        if(page > 0){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    JSONObject pageJSON;
                    for(int i=0; i<page; i++) {
                        pageJSON = page_newsListMap.get(i);
                        if(pageJSON == null) {
                            try {
                                pageJSON = getNewsList(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(pageJSON == null) return;
                        else {
                            uploadNewsDetail(pageJSON);
                        }

                    }
                }
            };
            MyThreadController.commit(runnable);
        }
    }

    public static void uploadNewsDetail(JSONObject pageJSON){
        JSONArray array = null;
        try{
            array = pageJSON.getJSONArray("value");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(array != null){
            JSONObject temp;
            try {
                for (Object object : array) {
                    temp = (JSONObject) object;
                    uploadOADetail(temp.getString("title"), temp.getString("department"), temp.getString("abs_link"), temp.getString("time"));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
