package com.lu.mydemo.UIMS;

import android.text.TextUtils;

import com.lu.mydemo.Net.HTTPTools;
import com.lu.mydemo.Utils.Common.Address;
import com.lu.mydemo.Utils.StudentVPN.VPNClient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 创建时间: 2020/01/17 09:25 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class UIMS_New {
    private boolean mUseStudentVPN;

    private String mStudentId;
    private String mPassword;

    private VPNClient mVPNClient;

    private JSONObject mCurrentUserInfoJSON;
    private JSONObject mScoreStatisticsJSON;
    private JSONObject mRecentScoreJSON;
    private int personId;
    private int teachingTerm;
    private int department;

    private String cookie1;
    private String cookie3;//j_spring_security_check返回
    private String cookie4;

    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static String mVPNBaseUrl = "https://vpns.jlu.edu.cn/https/77726476706e69737468656265737421e5fe4c8f693a6445300d8db9d6562d";
    private static String mUserAgentStr = "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36";

    private static List<Exception> mExceptionList = new ArrayList<>();

    public UIMS_New(VPNClient vpnClient, String studentId, String password, boolean useStudentVPN){
        mVPNClient = vpnClient;
        mStudentId = studentId;
        mPassword = password;
        mUseStudentVPN = useStudentVPN;
    }

    /**
     *
     * @return 结果
     */
    public boolean connectToUIMS() {

        if(mUseStudentVPN){
            if(mVPNClient == null || TextUtils.isEmpty(mVPNClient.getCookie())){
                return false;
            }
        }

        String url = Address.hostAddress + "/ntms/";
        if(mUseStudentVPN) url =  mVPNBaseUrl + "/ntms/";
        String headerKey = "Set-Cookie";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", mUserAgentStr);
        if(mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        Response response = HTTPTools.getResponse(url, headers);

        assert response != null;
        String headerValue = response.header(headerKey);
        HTTPTools.getOrOrOutResponse(response, true);

        if(mUseStudentVPN){
            return true;
        }

        if(TextUtils.isEmpty(headerValue)){
            return false;
        }
        String str = headerValue;
        String jSessionID;
        try{
            str = str.split(";")[0];
            jSessionID = str.split("=")[1];
            cookie1 = "loginPage=userLogin.jsp; alu=" + mStudentId + "; pwdStrength=1; JSESSIONID=" + jSessionID;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return !TextUtils.isEmpty(jSessionID);
    }

    public boolean loginUIMS(){
        String url = Address.hostAddress + "/ntms/j_spring_security_check";
        if(mUseStudentVPN) url = mVPNBaseUrl + "/ntms/j_spring_security_check";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("Content-Type", "application/x-www-form-urlencoded");
        mHeaderList.put("User-Agent", mUserAgentStr);
        if(mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        if(!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie1);
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        FormBody formBody = new FormBody.Builder()
                .add("j_username", mStudentId)
                .add("j_password", GetMD5.getMD5Str("UIMS" + mStudentId + mPassword))
                .add("mousePath", "YGQABYCQDLYEgDaZHwDqZJgD8aLwENbNwEdbPQEubQwE/bRgFQbSgFhbTgFxbUgGBbWQGSbWwGjbYgGybZgHEbbAHWbcAHlbdgH2bfQIIbgQIXbiAIpbjwI5blQJJbmwJabpAJsbqgJ8brQKMbtQKdbugKtbwAK/bxwLPbywLgb0QLwb1gMAa2gMSa4QMiZ6AMybYAOW")
                .build();

        Response response = HTTPTools.postResponse(url, headers, formBody);

        HTTPTools.getOrOrOutResponse(response, true);
        if(mUseStudentVPN){
            return true;
        }

        if(response == null){
            return false;
        }
        try{
            String str = response.headers().get("Location");
            if(str == null || str.contains("loginError")){
                return false;
            }
        } catch (Exception e){
            mExceptionList.add(e);
            e.printStackTrace();
        }

        String str = response.headers().get("Set-Cookie");
        String jSessionID2;
        if(str != null) {
            str = str.split(";")[0];
            jSessionID2 = str.split("=")[1];
            cookie3 = "loginPage=userLogin.jsp; alu=" + mStudentId + "; pwdStrength=1; JSESSIONID=" + jSessionID2;
            cookie4 = "loginPage=userLogin.jsp; alu=" + mStudentId + "; JSESSIONID=" + jSessionID2;
        }
        else {
            HTTPTools.getOrOrOutResponse(response, true);
            return false;
        }
        return !TextUtils.isEmpty(jSessionID2);
    }

    public boolean getCurrentUserInfo(){
        String url = Address.hostAddress + "/ntms/action/getCurrentUserInfo.do";
        if(mUseStudentVPN) url = mVPNBaseUrl + "/ntms/action/getCurrentUserInfo.do";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", mUserAgentStr);
        if(!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if(mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        FormBody formBody = new FormBody.Builder().build();

        Response response = HTTPTools.postResponse(url, headers, formBody);

        if(response == null){
            return false;
        }
        if(mUseStudentVPN){
            String responseStr = HTTPTools.getOrOrOutResponse(response, true);
            JSONObject responseJSON;
            try{
                responseJSON = JSONObject.fromObject(responseStr);
                assert responseJSON != null;
                mCurrentUserInfoJSON = responseJSON;
                JSONObject defRes = responseJSON.getJSONObject("defRes");
                String nickName = responseJSON.getString("nickName");
                personId = defRes.getInt("personId");
                department = defRes.getInt("department");
                teachingTerm = defRes.getInt("teachingTerm");
                String campus = defRes.getString("campus");
                System.out.println("department:" + department);
                System.out.println("campus:" + campus);
                System.out.println("nickName:" + nickName);
                return true;
            }
            catch (Exception e){
                mExceptionList.add(e);
                e.printStackTrace();
                return false;
            }
        }
        else {
            try {
                String str = response.headers().get("Location");
                if (str == null || str.contains("loginError")) {
                    return false;
                }
            } catch (Exception e) {
                mExceptionList.add(e);
                e.printStackTrace();
            }

            String str = response.headers().get("Set-Cookie");
            String jSessionID2;
            if (str != null) {
                str = str.split(";")[0];
                jSessionID2 = str.split("=")[1];
                cookie3 = "loginPage=userLogin.jsp; alu=" + mStudentId + "; pwdStrength=1; JSESSIONID=" + jSessionID2;
                cookie4 = "loginPage=userLogin.jsp; alu=" + mStudentId + "; JSESSIONID=" + jSessionID2;
            } else {
                HTTPTools.getOrOrOutResponse(response, true);
                return false;
            }
            return !TextUtils.isEmpty(jSessionID2);
        }
    }

    public boolean getTeachingTerm(){
        String url = Address.hostAddress + "/ntms/service/res.do";
        if(mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", mUserAgentStr);
        if(!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if(mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject params = new JSONObject();
        params.put("termId", teachingTerm);//TODO TermId

        JSONObject request_json = new JSONObject();
        request_json.put("tag","search@teachingTerm");
        request_json.put("branch","byId");
        request_json.put("params",params);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        HTTPTools.getOrOrOutResponse(response, true);

        return true;
    }

    public boolean getClassStudentCount(){
        String url = Address.hostAddress + "/ntms/service/res.do";
        if(mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", mUserAgentStr);
        if(!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if(mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject params = new JSONObject();
        params.put("deptId", department);
        params.put("egrade", "20" + mStudentId.substring(2, 4));

        JSONObject request_json = new JSONObject();
        request_json.put("tag","adminClass");
        request_json.put("branch","deptGrade");
        request_json.put("params",params);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try{
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            JSONArray value = responseJSON.getJSONArray("value");
            for(Object object : value){
                JSONObject item = (JSONObject) object;
                String className = item.getString("className");
                if(className.equals(mStudentId.substring(0, 6))){
                    int studCnt = item.getInt("studCnt");
                    System.out.println("班级人数:" + studCnt);
                    break;
                }
            }
        }
        catch (Exception e){
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getRecentScore(){
        String url = Address.hostAddress + "/ntms/service/res.do";
        if(mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", mUserAgentStr);
        if(!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if(mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject params = new JSONObject();

        JSONObject request_json = new JSONObject();
        request_json.put("tag","archiveScore@queryCourseScore");
        request_json.put("branch","latest");
        request_json.put("rowLimit", 150);
        request_json.put("params",params);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            mRecentScoreJSON = responseJSON;
        }
        catch (Exception e){
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getScoreStatistics(){
        String url = Address.hostAddress + "/ntms/service/res.do";
        if(mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", mUserAgentStr);
        if(!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if(mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject request_json1 = new JSONObject();
        request_json1.put("studId",personId);

        JSONObject request_json = new JSONObject();
        request_json.put("res","stat-avg-gpoint");
        request_json.put("params",request_json1);
        request_json.put("type","query");

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            mScoreStatisticsJSON = responseJSON;
        }
        catch (Exception e){
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public JSONObject getScorePercent(String asID){
        String url = Address.hostAddress + "/ntms/score/course-score-stat.do";
        if(mUseStudentVPN) url = mVPNBaseUrl + "/ntms/score/course-score-stat.do";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", mUserAgentStr);
        if(!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if(mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject request_json = new JSONObject();
        request_json.put("asId",asID);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            return responseJSON;
        }
        catch (Exception e){
            mExceptionList.add(e);
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getCurrentUserInfoJSON() {
        return mCurrentUserInfoJSON;
    }

    public JSONObject getScoreStatisticsJSON() {
        return mScoreStatisticsJSON;
    }

    public JSONObject getRecentScoreJSON() {
        return mRecentScoreJSON;
    }

    public static List<Exception> getExceptionList() {
        return mExceptionList;
    }
}
