package com.lu.mydemo.ToolFor2045_Site;

import android.util.Log;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 创建时间: 2019/11/13 12:03 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class InformationTest {

    public static final String HOST_ADDRESS = "http://10.33.77.184:8199";

    static OkHttpClient httpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) { }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    return new ArrayList<>();
                }
            })
//            .addInterceptor(new LoggingInterceptor())
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    public static String uploadInformation(JSONObject term, JSONObject courseType, JSONObject courseSelectType, JSONObject teachingTerm, JSONObject currentUserInfo,
                                           JSONObject score, JSONObject course, JSONObject student, JSONObject scoreStatistics, JSONObject courseHistory) throws Exception{
        FormBody formBody = new FormBody.Builder()
                .add("term", term == null ? "" : term.toString())
                .add("courseType", courseType == null ? "" : courseType.toString())
                .add("courseSelectType", courseSelectType == null ? "" : courseSelectType.toString())
                .add("teachingTerm", teachingTerm == null ? "" : teachingTerm.toString())
                .add("currentUserInfo", currentUserInfo == null ? "" : currentUserInfo.toString())
                .add("score", score == null ? "" : score.toString())
                .add("course", course == null ? "" : course.toString())
                .add("student", student == null ? "" : student.toString())
                .add("scoreStatistics", scoreStatistics == null ? "" : scoreStatistics.toString())
                .add("courseHistory", courseHistory == null ? "" : courseHistory.toString())
                .build();
        Request request = new Request.Builder()
                .url(HOST_ADDRESS + "/api/test/datatest")
                .post(formBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
        StringBuilder entityStringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            entityStringBuilder.append(line).append("\n");
        }
        Log.i("InformationUpload", "Response:" + entityStringBuilder);
        return entityStringBuilder.toString();
    }
}
