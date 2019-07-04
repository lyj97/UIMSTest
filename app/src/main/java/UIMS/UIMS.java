package UIMS;

import android.util.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
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
    static String adcId;
    static String department;
    static String school;
    static String year;
    String nickName;
    long loginCounter;

    static String term_id;
    static String termName;
    static int studCnt;
    static JSONObject currentUserInfoJSON;
    static JSONObject courseJSON;
    static JSONObject courseTypeJSON;
    static JSONObject courseSelectTypeJSON;
    static JSONObject scoreJSON;
    static JSONObject scoreStatisticsJSON;
    static JSONObject studentJSON;
    static JSONObject informationJSON;
    static JSONObject termJSON;//学期
    static JSONObject courseHistoryJSON;//处理历史选课记录用
    static JSONObject teachingTermJSON;//TeachingTerm，与当前学期、教学周相关
    static HashMap<String, String> termId_termName = new HashMap<>();//学期ID-NAME
    static HashMap<String, JSONObject> id_scorePercent = new HashMap<>();//asID-scorePercent
    static HashMap<String, String> courseTypeId_courseType = new HashMap<>();
    static HashMap<String, String> courseSelectTypeId_courseSelectTypeName = new HashMap<>();//选课类型ID_选课类型名称
    static HashMap<String, String> courseId_courseTypeId = new HashMap<>();
    static HashMap<String, JSONObject> noScoreCourseId_course = new HashMap<>();

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
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
//    MultipartBody.Builder builder;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public UIMS(String user, String pass){
        this.user = user;
        this.pass = pass;
    }

    public boolean connectToUIMS() {//连接教务，获取第一个JSON
        try {
//            builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .build();
//                    Log.i("okhttp_request", request.toString());
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.networkResponse().headers()));
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

    public boolean login(){//登录
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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.headers()));

            try{
                String str = response.headers().get("Location");
                if(str.contains("loginError")){
                    return false;
                }
            } catch (Exception e){
//                e.printStackTrace();
            }

            String str = response.headers().get("Set-Cookie");
            str = str.split(";")[0];
            jssionID2 = str.split("=")[1];

//                    showResponse("jssionID2:\t" + jssionID2);

//                    Toast.makeText(context, "JSSIONID2:\t" + jssionID2, Toast.LENGTH_SHORT).show();

//            Log.d("JSSIONID2", jssionID2);

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
        return getCurrentUserInfo(true);
    }

    public boolean getCurrentUserInfo(boolean setTermID){//获取用户信息（不包含重要个人信息）
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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
            Response response = httpClient.newCall(request).execute();
//            Log.i("OKHttp_Request", String.format("Received response for %s %n%s", response.request().url(), response.headers()));
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            // 利用从HttpEntity中得到的String生成JsonObject
//            Log.i("Login[entity]", "entity:\t" + entityStringBuilder.toString());
//                    showResponse("Login[entity]:\t" + entityStringBuilder.toString());
            JSONObject object = JSONObject.fromObject(entityStringBuilder.toString());

            if (object == null) {
//                Log.e("Login", "object IS NULL!");
                return false;
            }

            currentUserInfoJSON = object;
            nickName = object.getString("nickName");
            loginCounter = object.getLong("loginCounter");
            JSONObject defRes = (JSONObject) object.get("defRes");
            student_id = defRes.getString("personId");
            adcId = defRes.getString("adcId");
            department = defRes.getString("department");
            school = defRes.getString("school");
            if(setTermID) {
                term_id = defRes.getString("term_l");
                return getTeachingTerm();
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getUserInformation(){//个人信息（包含重要个人信息）
        try {
            JSONObject params = new JSONObject();
            params.put("studId",student_id);

            JSONObject request_json = new JSONObject();
            request_json.put("tag","student@stu_infor_table");
            request_json.put("params",params);

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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            JSONObject temp = JSONObject.fromObject(entityStringBuilder.toString());

            String schoolName = "";//学院
            String deptName = "";//专业
            String admissionYear = "";//入学时间
            String name = "";

            try {

                if (temp != null) {
//                    Log.i("GetUserInformation", temp.toString());
                } else {
//                    Log.e("GetUserInformation", "Object IS NULL!");
                }

                JSONObject value = (JSONObject)temp.get("value");
                JSONObject person = null;
                JSONObject department = null;
                JSONObject school = null;
                try{
                    person = value.getJSONObject("person");
                    department = value.getJSONObject("department");
                    school = value.getJSONObject("school");

                    schoolName = school.getString("schoolName");//学院
                    deptName = department.getString("deptName");//专业
                    admissionYear = value.getString("admissionYear");//入学时间
                    name = person.getString("name");

                }
                catch (Exception e1){
//                e1.printStackTrace();
                    throw new RuntimeException("ERROR！");
                }

            }
            catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("ERROR！");
            }

            informationJSON = JSONObject.fromObject(
                    "{\"college\":\"" + schoolName + "\"," +
                            "\"major\":\"" + deptName + "\"," +
                            "\"grade\":\"" + admissionYear + "\"," +
                            "\"name\":\"" + name + "\"}");

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * branch	byId
     * params	{…}
         * termId	???
     * tag	search@teachingTerm
     * @return
     */
    public boolean getTeachingTerm(){//学期周数
        try {
            JSONObject params = new JSONObject();
            params.put("termId",term_id);

            JSONObject request_json = new JSONObject();
            request_json.put("tag","search@teachingTerm");
            request_json.put("branch","byId");
            request_json.put("params",params);

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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            /**
             * id	termId
             * status	0
             * value	[
                     * {
                         * termName	2018-2019第2学期
                         * startDate	2019-03-04T00:00:00
                         * termSeq	2
                         * examDate	2019-07-01T00:00:00
                         * activeStage	1230
                         * year	2018-2019
                         * vacationDate	2019-07-08T00:00:00
                         * weeks	18
                         * termId	136
                         * egrade	2018
                     * }
             *          ]
             * resName	teachingTerm
             * msg
             */
            teachingTermJSON = JSONObject.fromObject(entityStringBuilder.toString());

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getCourseSchedule(){//课表（当前学期直接调用，以前的请求会设置当前term_id）
        try {
            getCourseSchedule(term_id);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getCourseSchedule(String termID){//课程表【课程解析在UIMSTool中的CourseJSONTransfer】
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
                    .header("Referer", Address.hostAddress + "/ntms/index.do")
                    .post(requestBody)
                    .build();
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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

    /**
     * branch	default
     * params	{…}
     * studId	???
     * termId	???
     * tag	termScore@inqueryTermScore
     * @param termID
     * @return
     */
    public boolean getCourseHistory(String termID){//历史选课
        try {
            JSONObject params = new JSONObject();
            params.put("termId", termID);
            params.put("studId", student_id);

            JSONObject request_json = new JSONObject();
            request_json.put("tag", "termScore@inqueryTermScore");
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
                    .header("Referer", Address.hostAddress + "/ntms/index.do")
                    .post(requestBody)
                    .build();
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            courseHistoryJSON = JSONObject.fromObject(entityStringBuilder.toString());
//            Log.i("CourseHistoryJSON", courseHistoryJSON.toString());
            try {
                dealCourseHistoryWithScore();
            } catch (Exception e){
                e.printStackTrace();;
                return false;
            }
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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            return getCourseType();
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * branch	default
     * params	{}
     * tag	search@teachingTerm
     * @return
     */
    public boolean getTermArray(){//学期ID
        try {

            JSONObject request_json = new JSONObject();
            request_json.put("branch","default");
            request_json.put("params","{}");
            request_json.put("tag","search@teachingTerm");

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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            termJSON = JSONObject.fromObject(entityStringBuilder.toString());
//            Log.i("TermJSON", termJSON.toString());
            dealTermArray();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    public boolean getCourseType(){//课程类型
        try {
            JSONObject request_json1 = new JSONObject();
            request_json1.put("id",4160);

            JSONObject request_json = new JSONObject();
            request_json.put("branch","byId");
            request_json.put("params",request_json1);
            request_json.put("tag","getAllById@sysDict");

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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            courseTypeJSON = JSONObject.fromObject(entityStringBuilder.toString());
            dealCourseType();
            return getCourseSelectType();
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * branch	byCategory
     * params	{…}
     *      cg	SELECT_TYPE
     * tag	sysDict
     * @return
     */
    public boolean getCourseSelectType(){//选课类型
        try {
            JSONObject request_json1 = new JSONObject();
            request_json1.put("cg","SELECT_TYPE");

            JSONObject request_json = new JSONObject();
            request_json.put("branch","byCategory");
            request_json.put("params",request_json1);
            request_json.put("tag","sysDict");

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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            courseSelectTypeJSON = JSONObject.fromObject(entityStringBuilder.toString());
            dealCourseSelectType();
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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            setStudentJSON(studentJSON);

            scoreJSON = addScorePercent(scoreJSON);
            dealScorePercent();
            studCnt = studentJSON.getJSONObject("adminClass").getInt("studCnt");

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public JSONObject getScorePercent(String asID){//成绩
        try {

            if(id_scorePercent.containsKey(asID)){
                Log.w("getScorePercent", "Ignored :\t" + asID);
                return id_scorePercent.get(asID);
            }

            JSONObject request_json = new JSONObject();

            request_json.put("asId",asID);

            RequestBody requestBody = RequestBody.create(JSON, request_json.toString());

            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/score/course-score-stat.do")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie3)
                    .header("Host", Address.host)
                    .header("Origin", Address.hostAddress)
                    .header("Content-Type", "application/json")
                    .header("Referer", Address.hostAddress + "/ntms/index.do")
                    .post(requestBody)
                    .build();
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            return JSONObject.fromObject(entityStringBuilder.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject addScorePercent(JSONObject score){
        JSONArray scores = score.getJSONArray("value");
        int i = 0;
        JSONObject percentJSON;
        while (true) {
            try {
                JSONObject temp = scores.getJSONObject(i);
                JSONObject temp1 = temp.getJSONObject("teachingTerm");
                JSONObject temp2 = temp.getJSONObject("course");

                String asId = temp.getString("asId");
                percentJSON = getScorePercent(asId);
                percentJSON.put("courName", temp2.getString("courName"));
                temp.put("percent", percentJSON);

                temp.remove("student");

                i++;
            } catch (Exception e) {
                break;
            }
        }
        return score;
    }

    /**
     * branch	default
     * params	{…}
     *      adcId	6049
     *      deptId	4
     *      egrade	2016
     *      schId	101
     * tag	student_sch_dept
     * @return
     */
    public HashSet<String> getClassStudentName(){//本班学生

        System.out.println("" +
                "adcId\t" + adcId + "\n" +
                "deptId\t" + department + "\n" +
                "egrade\t" + year + "\n" +
                "schId\t" + school);

        //TODO TEST
//        if(year == null) year = "2016";

        if(adcId== null || department== null || year== null || school== null) {
            throw new NegativeArraySizeException("Error! Get names failed!" +
                    1 + ":\t" + adcId + "\n" +
                    2 + ":\t" + department + "\n" +
                    3 + ":\t" + year + "\n" +
                    4 + ":\t" + school);
//            return null;
        }

        try {
            JSONObject request_json1 = new JSONObject();
            request_json1.put("adcId",adcId);
            request_json1.put("deptId",department);
            request_json1.put("egrade",year);
            request_json1.put("schId",school);

            JSONObject request_json = new JSONObject();
            request_json.put("branch","default");
            request_json.put("params",request_json1);
            request_json.put("tag","student_sch_dept");

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
//            Log.i("OKHttp_Request", String.format("Sending request %s %n%s", request.url(), request.headers()));
//            Log.i("okhttp_request_body", request.body().toString());
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
            JSONObject object = JSONObject.fromObject(entityStringBuilder.toString());
            HashSet<String> names = new HashSet<>();

            System.out.println(object);
            try {//输出内容

                JSONArray value = (JSONArray) object.get("value");

                JSONObject temp;
                String name;
                String studNo;
                try {
                    for (int i = 0; i < value.size(); i++) {
                        temp = value.getJSONObject(i);
                        name = temp.getString("name");
                        names.add(name);

//                        studNo = temp.getString("studNo");
//                        System.out.println(name + "\t" + studNo);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

//                System.out.println(evalItemIds);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return names;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * searchterm_header:
     * Cookie:loginPage=userLogin.jsp; alu=教学号; JSESSIONID=？？？
     * Referer: http://uims.jlu.edu.cn/ntms/index.do
     * @return termid
     *
     * 参数
     * branch	self
     * params	{…}
     *      blank	Y
     * tag	student@evalItem
     *
     */
    public ArrayList<String> post_pingjiao_information() {

        try {

//        System.out.println("post_pingjiao_information");

//        System.out.println("url:\t" + url);
//        System.out.println("alu:\t" + alu);
//        System.out.println("jssionid:\t" + jssionid);
//        System.out.println("student_id:\t" + student_id);
//        System.out.println("term_id:\t" + term_id);

            JSONObject request_json1 = new JSONObject();
            request_json1.put("blank", "Y");

            JSONObject request_json = new JSONObject();
            request_json.put("tag", "student@evalItem");
            request_json.put("branch", "self");
            request_json.put("params", request_json1);

            RequestBody requestBody = RequestBody.create(JSON, request_json.toString());

            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/service/res.do")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie3)
                    .header("Host", Address.host)
                    .header("Pragma", "no-cache")
                    .header("Content-Type", "application/json")
                    .header("Connection", "keep-alive")
                    .header("Referer", Address.hostAddress + "/ntms/index.do")
                    .post(requestBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }

            JSONObject object = JSONObject.fromObject(entityStringBuilder.toString());

//            System.out.println(object);

            ArrayList<String> evalItemIds = new ArrayList<>();

            try {//输出内容

                JSONArray value = (JSONArray) object.get("value");
                JSONObject course = null;
                JSONObject course_inf = null;

                try {
                    for (int i = 0; i < value.size(); i++) {
                        course = (JSONObject) value.get(i);
                        course_inf = (JSONObject) course.get("target");
                        evalItemIds.add((String) course.get("evalItemId"));

//                        System.out.println("NUMBER" + (i + 1));
//                        System.out.println("id;\t" + course.get("evalItemId"));
//                        System.out.println("name;\t" + course_inf.get("name"));

//                        course_inf = (JSONObject) course.get("targetClar");

//                        System.out.println("course;\t" + course_inf.get("notes") + "\n");

                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

//                System.out.println(evalItemIds);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return evalItemIds;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * searchterm_header:
     * Cookie:loginPage=userLogin.jsp; alu=教学号; JSESSIONID=？？？
     * Referer: http://uims.jlu.edu.cn/ntms/page/eval/eval_detail_120.html?eitem=？？？
     * @return termid
     *
     * 参数
     * branch	self
     * params	{…}
     *      blank	Y
     * tag	student@evalItem
     *
     * {"tag":"teachClassStud@schedule","branch":"default","params":{"termId":135,"studId":266662}}
     */
    public JSONObject post_pingjiao(String evalItemId) {

        try {
//            System.out.println("post_pingjiao");

//        System.out.println("url:\t" + url);
//        System.out.println("alu:\t" + alu);
//        System.out.println("jssionid:\t" + jssionid);
//        System.out.println("student_id:\t" + student_id);
//        System.out.println("term_id:\t" + term_id);

            HashMap<String, String> map = new HashMap();


//        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//        }
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

            JSONObject request_json1 = new JSONObject();
            request_json1.put("evalItemId", evalItemId);

//        JSONObject request_json = new JSONObject();
//        request_json.put("tag","get@EvalItem");
//        request_json.put("params",request_json1);

            RequestBody requestBody = RequestBody.create(JSON, request_json1.toString());

            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/action/eval/fetch-eval-item.do")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie3)
                    .header("Host", Address.host)
                    .header("Pragma", "no-cache")
                    .header("Content-Type", "application/json")
                    .header("Connection", "keep-alive")
                    .header("Referer", Address.hostAddress + "/ntms/page/eval/eval_detail_120.html?eitem=" + evalItemId)
                    .post(requestBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }

            JSONObject object = JSONObject.fromObject(entityStringBuilder.toString());
            System.out.println(object);

            try {

                JSONObject value = ((JSONArray) object.get("items")).getJSONObject(0);
//                JSONObject evalActTime = null;
                JSONObject targetClar = null;

                try {
//                    evalActTime = (JSONObject) value.get("evalActTime");
                    targetClar = (JSONObject) value.get("targetClar");

                    System.out.println("INF");
                    System.out.println("puzzle;\t" + value.get("puzzle"));
                    System.out.println("person;\t" + targetClar.get("person"));
                    System.out.println("notes;\t" + targetClar.get("name") + "\n");

//                    return value.getString("puzzle");
                    return value;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

//            System.out.println(evalItemIds);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * searchterm_header:
     * Cookie:loginPage=userLogin.jsp; alu=教学号; JSESSIONID=？？？
     * Referer: http://uims.jlu.edu.cn/ntms/page/eval/eval_detail_120.html?eitem=？？？
     * @return termid
     *
     * 参数
     * {
     *      "guidelineId":120,
     *      "evalItemId":"5086141",
     *      "answers":{
     *          "prob11":"A",
     *          "prob12":"A",
     *          "prob13":"N",
     *
     *          prob13	Y
     *          prob13y	A
     *
     *          prob14":"A",
     *          "prob15":"A",
     *          "prob21":"A",
     *          "prob22":"A",
     *          "prob23":"A",
     *          "prob31":"A",
     *          "prob32":"A",
     *          "prob33":"A",
     *          "prob41":"A",
     *          "prob42":"A",
     *          "prob43":"A",
     *          "prob51":"A",
     *          "prob52":"A",
     *          "sat6":"A",
     *          "mulsel71":"K",
     *          "advice72":"",
     *          "prob73":"Y"
     *      },
     *      "clicks":{
     *          "_boot_":0,
     *          "prob11":940480,
     *          "prob12":941785,
     *          "prob13":943263,
     *          "prob14":944599,
     *          "prob15":946087,
     *          "prob21":951274,
     *          "prob22":953240,
     *          "prob23":954562,
     *          "prob31":956457,
     *          "prob32":957899,
     *          "prob33":959297,
     *          "prob41":961440,
     *          "prob42":963751,
     *          "prob43":965425,
     *          "prob51":968037,
     *          "prob52":969919,
     *          "sat6":972365,
     *          "mulsel71":973853,
     *          "prob73":975496
     *      }
     *  }
     */
    public boolean post_pingjiao_tijiao(String evalItemId, String puzzle_answer) {

        try {

            System.out.println("post_pingjiao_tijiao");

//        System.out.println("url:\t" + url);
//        System.out.println("alu:\t" + alu);
//        System.out.println("jssionid:\t" + jssionid);
//        System.out.println("student_id:\t" + student_id);
//        System.out.println("term_id:\t" + term_id);

            JSONObject j_clicks = new JSONObject();
            j_clicks.put("_boot_", 0);
            j_clicks.put("prob11", 940480);
            j_clicks.put("prob12", 941785);
            j_clicks.put("prob13", 943263);
            j_clicks.put("prob14", 944599);
            j_clicks.put("prob15", 946087);
            j_clicks.put("prob21", 951274);
            j_clicks.put("prob22", 953240);
            j_clicks.put("prob23", 954562);
            j_clicks.put("prob31", 956457);
            j_clicks.put("prob32", 957899);
            j_clicks.put("prob33", 959297);
            j_clicks.put("prob41", 961440);
            j_clicks.put("prob42", 963751);
            j_clicks.put("prob43", 965425);
            j_clicks.put("prob51", 968037);
            j_clicks.put("prob52", 969919);
            j_clicks.put("sat6", 972365);
            j_clicks.put("mulsel71", 973853);
            j_clicks.put("prob73", 975496);

            JSONObject j_answers = new JSONObject();
            j_answers.put("advice72", "");
            j_answers.put("mulsel71", "K");
            j_answers.put("prob11", "A");
            j_answers.put("prob12", "A");
            j_answers.put("prob13", "N");
            j_answers.put("prob14", "A");
            j_answers.put("prob15", "A");
            j_answers.put("prob21", "A");
            j_answers.put("prob22", "A");
            j_answers.put("prob23", "A");
            j_answers.put("prob31", "A");
            j_answers.put("prob32", "A");
            j_answers.put("prob33", "A");
            j_answers.put("prob41", "A");
            j_answers.put("prob42", "A");
            j_answers.put("prob43", "A");
            j_answers.put("prob51", "A");
            j_answers.put("prob52", "A");
            j_answers.put("prob73", "Y");
            j_answers.put("sat6", "A");
            j_answers.put("puzzle_answer", puzzle_answer);

            JSONObject request_json = new JSONObject();
            request_json.put("evalItemId", evalItemId);
            request_json.put("guidelineId", 120);
//        request_json.put("clicks",j_clicks);
            request_json.put("answers", j_answers);

            RequestBody requestBody = RequestBody.create(JSON, request_json.toString());

            Request request = new Request.Builder()
                    .url(Address.hostAddress + "/ntms/action/eval/eval-with-answer.do")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                    .header("Cookie", cookie3)
                    .header("Host", Address.host)
                    .header("Pragma", "no-cache")
                    .header("Content-Type", "application/json")
                    .header("Connection", "keep-alive")
                    .header("Referer", Address.hostAddress + "/ntms/page/eval/eval_detail_120.html?eitem=" + evalItemId)
                    .post(requestBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), "UTF-8"), 8 * 1024);
            StringBuilder entityStringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }

            JSONObject object = JSONObject.fromObject(entityStringBuilder.toString());

            try {
                if (object != null) {
                    System.out.println(object.toString());
                } else {
                    System.out.println("object IS NULL!");
                }

                JSONObject value = (JSONObject) object;

                try {
                    System.out.println("INF");
                    System.out.println("count:\t" + value.get("count"));
                    System.out.println("errno:\t" + value.get("errno"));
                    System.out.println("msg:\t" + value.get("msg"));
                    System.out.println("status:\t" + value.get("status") + "\n");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                if (value.get("status").equals(0)) {
                    System.out.println("评教成功！\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void dealScorePercent(){
        id_scorePercent = new HashMap<>();
        courseId_courseTypeId = new HashMap<>();
        JSONArray scores = scoreJSON.getJSONArray("value");
        int i = 0;
        while (true) {
            try {
                JSONObject temp = scores.getJSONObject(i);

                String type5 = temp.getString("type5");
                String asId = temp.getString("asId");

                id_scorePercent.put(asId, temp.getJSONObject("percent"));
                courseId_courseTypeId.put(asId, type5);

                i++;
            } catch (Exception e) {
                break;
            }
        }
    }

    public static void dealCourseType(){
        courseTypeId_courseType = new HashMap<>();
        JSONArray value = courseTypeJSON.getJSONArray("value");
        JSONObject temp;
        for(int i=0; i<value.size(); i++){
            temp = value.getJSONObject(i);
            courseTypeId_courseType.put(temp.getString("dictId"), temp.getString("name"));
        }
    }

    public static void dealCourseSelectType(){
        courseSelectTypeId_courseSelectTypeName = new HashMap<>();
        JSONArray value = courseSelectTypeJSON.getJSONArray("value");
        JSONObject temp;
        for(int i=0; i<value.size(); i++){
            temp = value.getJSONObject(i);
            courseSelectTypeId_courseSelectTypeName.put(temp.getString("dictId"), temp.getString("name"));
        }
    }

    public static void dealCourseHistoryWithScore(){//历史选课与成绩关联
        noScoreCourseId_course = new HashMap<>();
        JSONArray value = courseHistoryJSON.getJSONArray("value");
        JSONObject tempCourse;
        String lsrId;
        for(int i=0; i<value.size(); i++){
            tempCourse = value.getJSONObject(i);
            lsrId = tempCourse.getString("lsrId");
            if(!id_scorePercent.containsKey(lsrId)) noScoreCourseId_course.put(lsrId, tempCourse);
        }
//        Log.i("NSC.size", noScoreCourseId_course.size() + "");
    }

    public static void dealTermArray(){
        termId_termName = new HashMap<>();
        JSONArray value = termJSON.getJSONArray("value");
        JSONObject temp;
        for(int i=0; i<value.size(); i++){
            temp = value.getJSONObject(i);
            termId_termName.put(temp.getString("termId"), temp.getString("termName"));
//            Log.i("Term " + i, temp.toString());
        }
        termName = termId_termName.get(term_id);
    }

    public static JSONObject getTermJSON(String term_id){
        if(term_id == null){
            Log.e("Get term failed:", "term_id is NULL");
            return null;
        }
        Log.i("Get term:", term_id);
        JSONArray value = termJSON.getJSONArray("value");
        JSONObject temp;
        for(int i=0; i<value.size(); i++){
            temp = value.getJSONObject(i);
            Log.i("Term "+ i, temp.toString());
            if(temp.getString("termId").equals(term_id) || temp.getString("termName").equals(term_id)){
                return temp;
            }
        }
        Log.e("Get term failed:", term_id);
        return null;
    }

    public static JSONObject getScorePercentJSON(String asID){
        return id_scorePercent.get(asID);
    }

    public static String getCourseTypeId(String asID){
        return courseId_courseTypeId.get(asID);
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

    public static String getTermName() {
        return termName;
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

    public static void setScoreJSON(JSONObject scoreJSON) {
        UIMS.scoreJSON = scoreJSON;
        dealScorePercent();
    }

    public static void setStudentJSON(JSONObject studentJSON) {
        UIMS.studentJSON = studentJSON;
        JSONObject adminClass = studentJSON.getJSONObject("adminClass");
        studCnt = adminClass.getInt("studCnt");
        adcId = adminClass.getString("adcId");
        year = studentJSON.getString("egrade");
    }

    public static void setScoreStatisticsJSON(JSONObject scoreStatisticsJSON) {
        UIMS.scoreStatisticsJSON = scoreStatisticsJSON;
    }

    public static int getStudCnt() {
        return studCnt;
    }

    public static HashMap<String, String> getCourseTypeId_courseType() {
        return courseTypeId_courseType;
    }

    public static JSONObject getCourseTypeJSON() {
        return courseTypeJSON;
    }

    public static void setCourseTypeJSON(JSONObject courseTypeJSON) {
        UIMS.courseTypeJSON = courseTypeJSON;
        dealCourseType();
    }

    public static JSONObject getCourseSelectTypeJSON() {
        return courseSelectTypeJSON;
    }

    public static void setCourseSelectTypeJSON(JSONObject courseSelectTypeJSON) {
        UIMS.courseSelectTypeJSON = courseSelectTypeJSON;
        dealCourseSelectType();
    }

    public static HashMap<String, String> getCourseSelectTypeId_courseSelectTypeName() {
        return courseSelectTypeId_courseSelectTypeName;
    }

    public static void setCourseSelectTypeId_courseSelectTypeName(HashMap<String, String> courseSelectTypeId_courseSlectTypeName) {
        UIMS.courseSelectTypeId_courseSelectTypeName = courseSelectTypeId_courseSlectTypeName;
    }

    public static JSONObject getStudentJSON() {
        return studentJSON;
    }

    public static JSONObject getInformationJSON() {
        return informationJSON;
    }

    public static JSONObject getTeachingTermJSON() {
        return teachingTermJSON;
    }

    public static void setTeachingTerm(JSONObject teachingTerm) {
        UIMS.teachingTermJSON = teachingTerm;
        JSONObject value;
        try{
            value = teachingTermJSON.getJSONArray("value").getJSONObject(0);
        }catch (JSONException e){
            value = teachingTermJSON;
        }
        termName = value.getString("termName");
        term_id = value.getString("termId");
    }

    public static void setInformationJSON(JSONObject informationJSON) {
        UIMS.informationJSON = informationJSON;
    }

    public static void setCourseJSON(JSONObject courseJSON) {
        UIMS.courseJSON = courseJSON;
    }

    public static JSONObject getCurrentUserInfoJSON() {
        return currentUserInfoJSON;
    }

    public static void setCurrentUserInfoJSON(JSONObject currentUserInfoJSON) {
        UIMS.currentUserInfoJSON = currentUserInfoJSON;
        JSONObject defRes = UIMS.currentUserInfoJSON.getJSONObject("defRes");
        department = defRes.getString("department");
        school = defRes.getString("school");
    }

    public static JSONObject getTermJSON() {
        return termJSON;
    }

    public static void setTermJSON(JSONObject termJSON) {
        UIMS.termJSON = termJSON;
        dealTermArray();
    }

    public static JSONObject getCourseHistoryJSON() {
        return courseHistoryJSON;
    }

    public static void setCourseHistoryJSON(JSONObject courseHistoryJSON) {
        UIMS.courseHistoryJSON = courseHistoryJSON;
        dealCourseHistoryWithScore();
    }

    public static HashMap<String, String> getTermId_termName() {
        return termId_termName;
    }

    public static void setTermId_termName(HashMap<String, String> termId_termName) {
        UIMS.termId_termName = termId_termName;
    }

    public static HashMap<String, JSONObject> getId_scorePercent() {
        return id_scorePercent;
    }

    public static void setId_scorePercent(HashMap<String, JSONObject> id_scorePercent) {
        UIMS.id_scorePercent = id_scorePercent;
    }

    public static void setCourseTypeId_courseType(HashMap<String, String> courseTypeId_courseType) {
        UIMS.courseTypeId_courseType = courseTypeId_courseType;
    }

    public static HashMap<String, String> getCourseId_courseTypeId() {
        return courseId_courseTypeId;
    }

    public static void setCourseId_courseTypeId(HashMap<String, String> courseId_courseTypeId) {
        UIMS.courseId_courseTypeId = courseId_courseTypeId;
    }

    public static HashMap<String, JSONObject> getNoScoreCourseId_course() {
        return noScoreCourseId_course;
    }

    public static void setNoScoreCourseId_course(HashMap<String, JSONObject> noScoreCourseId_course) {
        UIMS.noScoreCourseId_course = noScoreCourseId_course;
    }
}
