package UIMS;

import android.content.Context;
import android.util.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UIMS {

    JSONObject jsonObject;
    String cookie1;//有pwdStrength
    String cookie2;
    String cookie3;//j_spring_security_check返回
    String cookie4;
    String student_id;
    String user;
    String pass;
    String jssionID;
    String jssionID2;
    String term_id;
    String adcId;
    String nickName;
    long loginCounter;

    static JSONObject courseJSON;
    static JSONObject scoreJSON;
    static JSONObject scoreStatisticsJSON;
    static JSONObject studentJSON;

    OkHttpClient httpClient = new OkHttpClient.Builder()
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
            .build();
    MultipartBody.Builder builder;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    Context context;

    public UIMS(String user, String pass){
        this.user = user;
        this.pass = pass;
    }

    public boolean connectToUIMS() {
        try {
            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .build();
//                    Log.i("okhttp_request", request.toString());
            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
                    request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
            Log.i("OKHttp_Request", String.format("Received response for %s %n%s",
                    response.request().url(), response.networkResponse().headers()));
            String str = response.headers().get("Set-Cookie");
            str = str.split(";")[0];
            jssionID = str.split("=")[1];
            cookie1 = "loginPage=userLogin.jsp; alu=" + user + "; pwdStrength=1; JSESSIONID=" + jssionID;
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(){
        try {
            FormBody formBody = new FormBody.Builder()
                    .add("j_username", user)
                    .add("j_password", GetMD5.getMD5Str("UIMS" + user + pass))
                    .add("mousePath", "SGwABSAgCeSBgCwSCQDASCgDRSDQDhSDwDzSEQEDSFgEUSGgEkSHgE1SIgFGSJwFWSLwFnSMwF4SOAGJSPAGZSQQGqSRQG6SSQHMSTgHcSUgHtSVgH9SWgIOSYQIeSZgIvSbAJAScQJRSdwJiSewJySgAKDShAKUShgKkSigK0SjQLFSkALWSlgLmSmgL4SnwMISowMZSpgMpSqQM6SrQNLSsQNbStANsStwN9SvAOOSvgOfSxAOvSxwPASywPRSzwPhS1APyS2AQCS3QQTS4QQkS5QQ1S6QRFARwXr")
                    .build();

            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/j_spring_security_check")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie1)
                    .header("Referer", Address.hostAddress + "/ntms/userLogin.jsp?reason=nologin")
                    .post(formBody)
                    .build();
            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
                    request.url(), request.headers()));
            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
            Log.i("OKHttp_Request", String.format("Received response for %s %n%s",
                    response.request().url(), response.headers()));
            String str = response.headers().get("Set-Cookie");
            str = str.split(";")[0];
            jssionID2 = str.split("=")[1];

//                    showResponse("jssionID2:\t" + jssionID2);

//                    Toast.makeText(context, "JSSIONID2:\t" + jssionID2, Toast.LENGTH_SHORT).show();

            Log.d("JSSIONID2", jssionID2);

            cookie3 = "loginPage=userLogin.jsp; alu=" + user + "; pwdStrength=1; JSESSIONID=" + jssionID2;
            cookie4 = "loginPage=userLogin.jsp; alu=" + user + "; JSESSIONID=" + jssionID2;

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getCurrentUserInfo(){
        try{
            FormBody formBody = new FormBody.Builder().build();
            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/action/getCurrentUserInfo.do")
                    .header("Referer", Address.hostAddress + "/ntms/index.do")
                    .header("Connection", "keep-alive")
                    .header("Origin", Address.hostAddress)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie3)
                    .post(formBody)
                    .build();
//                    Log.i("okhttp_request", request.toString());
            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
                    request.url(), request.headers()));
            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
            Log.i("OKHttp_Request", String.format("Received response for %s %n%s",
                    response.request().url(), response.headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
            Log.i("Login[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());
            JSONObject object = JSONObject.fromObject(entityStringBuilder.toString());

            if (object == null) {
                Log.e("Login", "object IS NULL!");
                return false;
            }

            nickName = object.getString("nickName");
            loginCounter = object.getLong("loginCounter");
            JSONObject defRes = (JSONObject) object.get("defRes");
            student_id = defRes.getString("personId");
            term_id = defRes.getString("term_l");
            adcId = defRes.getString("adcId");
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getSelectedCourse(){
        try {
            getSelectedCourse(term_id);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getSelectedCourse(String termID){//选课
        try {
            JSONObject params = new JSONObject();
            params.put("termId", termID);
            params.put("studId", student_id);

            JSONObject request_json = new JSONObject();
            request_json.put("tag", "teachClassStud@schedule");
            request_json.put("branch", "default");
            request_json.put("params", params);

            RequestBody requestBody = RequestBody.create(JSON, request_json.toString());

            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/service/res.do")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie3)
                    .header("Host", Address.host)
                    .header("Origin", Address.hostAddress)
                    .header("Content-Type", "application/json")
                    .header("Referer", Address.hostAddress + "/ntms/userLogin.jsp?reason=nologin")
                    .post(requestBody)
                    .build();
            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
                    request.url(), request.headers()));
            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());
            courseJSON = JSONObject.fromObject(entityStringBuilder.toString());

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getScoreStatistics(){//绩点统计
        try {
            JSONObject request_json1 = new JSONObject();
            request_json1.put("studId",student_id);

            JSONObject request_json = new JSONObject();
            request_json.put("res","stat-avg-gpoint");
            request_json.put("params",request_json1);
            request_json.put("type","query");

            RequestBody requestBody = RequestBody.create(JSON, request_json.toString());

            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/service/res.do")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie3)
                    .header("Host", Address.host)
                    .header("Origin", Address.hostAddress)
                    .header("Content-Type", "application/json")
                    .header("Referer", Address.hostAddress + "/ntms/index.do")
                    .post(requestBody)
                    .build();
            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
                    request.url(), request.headers()));
            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());
            scoreStatisticsJSON = JSONObject.fromObject(entityStringBuilder.toString());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getRecentScore(){//成绩
        try {
            JSONObject request_json = new JSONObject();
            request_json.put("branch","latest");
            request_json.put("params","{}");
            request_json.put("rowLimit","99");
            request_json.put("tag","archiveScore@queryCourseScore");

            RequestBody requestBody = RequestBody.create(JSON, request_json.toString());

            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/service/res.do")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie3)
                    .header("Host", Address.host)
                    .header("Origin", Address.hostAddress)
                    .header("Content-Type", "application/json")
                    .header("Referer", Address.hostAddress + "/ntms/index.do")
                    .post(requestBody)
                    .build();
            Log.i("OKHttp_Request", String.format("Sending request %s %n%s",
                    request.url(), request.headers()));
            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());
            scoreJSON = JSONObject.fromObject(entityStringBuilder.toString());

            studentJSON = scoreJSON.getJSONArray("value").getJSONObject(0).getJSONObject("student");

            scoreJSON = addScorePercent(scoreJSON);

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public JSONObject addScorePercent(JSONObject score){
        JSONArray scores = score.getJSONArray("value");
        int i = 0;
        while (true) {
            try {
                JSONObject temp = scores.getJSONObject(i);
                JSONObject temp1 = temp.getJSONObject("teachingTerm");
                JSONObject temp2 = temp.getJSONObject("course");

//                String asId = temp.getString("asId");

//                temp.put("percent", getScorePercent(asId));

                temp.remove("student");
                temp.remove("xkkh");
                temp.remove("notes");
                temp.remove("planDetail");
                temp1.remove("activeStage");
                temp1.remove("weeks");
                temp1.remove("startDate");
                temp1.remove("vacationDate");
                temp2.remove("type5");
                temp2.remove("englishName");
                temp2.remove("courType3");
                temp2.remove("activeStatus");

                i++;
            } catch (Exception e) {
                break;
            }
        }
        return score;
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getTerm_id() {
        return term_id;
    }

    public String getAdcId() {
        return adcId;
    }

    public String getNickName() {
        return nickName;
    }

    public long getLoginCounter() {
        return loginCounter;
    }

    public static JSONObject getCourseJSON() {
        return courseJSON;
    }

    public static JSONObject getScoreJSON() {
        return scoreJSON;
    }

    public static JSONObject getScoreStatisticsJSON() {
        return scoreStatisticsJSON;
    }

    public static JSONObject getStudentJSON() {
        return studentJSON;
    }
}
