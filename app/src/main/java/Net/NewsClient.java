package Net;

import android.util.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 创建时间: 2019/10/27 11:45 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class NewsClient {

    public static final String OA_NEWS_LIST_LINK = "https://oa.jlu.edu.cn/defaultroot/PortalInformation!jldxList.action?1=1&channelId=179577&startPage=";
    public static final String OA_NEWS_BASE_URI = "https://oa.jlu.edu.cn/defaultroot/";

    public static JSONObject getNewsList(int page){
        JSONObject news_json = new JSONObject();
        JSONArray array = new JSONArray();
        Document document = null;
        try {
            document = Jsoup.connect(OA_NEWS_LIST_LINK + page).get();
        }
        catch (Exception e){
            e.printStackTrace();
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
                    array.add(object);
                }catch (Exception e){
                    e.printStackTrace();
                }
//                System.out.println("Title:" + title_a.text());
//                System.out.println("Department:" + department_a.text());
//                System.out.println("Time:" + time_span.text());
//                System.out.println("Link:" + title_a.attr("href"));
//                System.out.println("AbsLink:" + OA_NEWS_BASE_URI + title_a.attr("href"));
            }
            Log.i("NewsClient", "News list size:" + array.size());
            news_json.put("value", array);
            return news_json;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Hello!");
        getNewsList(1);
    }
}
