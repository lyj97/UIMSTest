package com.lu.mydemo.UIMS;

import android.text.TextUtils;

import com.lu.mydemo.Net.HTTPTools;
import com.lu.mydemo.Utils.Common.Address;
import com.lu.mydemo.Utils.StudentVPN.VPNClient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private JSONObject mDefResJSON;
    private JSONObject mTeachingTermJSON;
    private JSONObject mCourseSelectTypeJSON;
    private JSONObject mScoreStatisticsJSON;
    private JSONObject mRecentScoreJSON;
    private int classStudentCount;
    private int personId;
    private int teachingTerm;
    private int department;

    private String cookie1;
    private String cookie3;//j_spring_security_check返回
    private String cookie4;

    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static String mVPNBaseUrl = "https://vpns.jlu.edu.cn/https/77726476706e69737468656265737421e5fe4c8f693a6445300d8db9d6562d";

    private static List<Exception> mExceptionList = new ArrayList<>();

    public UIMS_New(VPNClient vpnClient, String studentId, String password, boolean useStudentVPN) {
        mVPNClient = vpnClient;
        mStudentId = studentId;
        mPassword = password;
        mUseStudentVPN = useStudentVPN;
    }

    /**
     * @return 结果
     */
    public boolean connectToUIMS() {

        if (mUseStudentVPN) {
            if (mVPNClient == null || TextUtils.isEmpty(mVPNClient.getCookie())) {
                return false;
            }
        }

        String url = Address.hostAddress + "/ntms/";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/";
        String headerKey = "Set-Cookie";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        Response response = HTTPTools.getResponse(url, headers);

        if (response == null) {
            return false;
        }
        String headerValue = response.header(headerKey);
        HTTPTools.getOrOrOutResponse(response, true);

        if (mUseStudentVPN) {
            return true;
        }

        if (TextUtils.isEmpty(headerValue)) {
            return false;
        }
        String str = headerValue;
        String jSessionID;
        try {
            str = str.split(";")[0];
            jSessionID = str.split("=")[1];
            cookie1 = "loginPage=userLogin.jsp; alu=" + mStudentId + "; pwdStrength=1; JSESSIONID=" + jSessionID;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return !TextUtils.isEmpty(jSessionID);
    }

    public byte[] getVerifyCode() {
        String url = Address.hostAddress + "/ntms/open/get-captcha-image.do";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/open/get-captcha-image.do?s=" + Math.random();

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        Response response = HTTPTools.getResponse(url, headers);
        if (response == null || response.body() == null) {
            return null;
        }
        try {
            byte[] bytes = Objects.requireNonNull(response.body()).bytes();
            Objects.requireNonNull(response.body()).close();

            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean loginUIMS(String verifyCode) {
        String url = Address.hostAddress + "/ntms/j_spring_security_check";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/j_spring_security_check?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("Content-Type", "application/x-www-form-urlencoded");
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie1);
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        FormBody formBody = new FormBody.Builder()
                .add("username", mStudentId)
                .add("password", GetMD5.getMD5Str("UIMS" + mStudentId + mPassword))
                .add("mousePath", UIMSStaticRes.MouthPathStr)
                .add("vcode", verifyCode)
                .build();

        Response response = HTTPTools.postResponse(url, headers, formBody);

        if (response == null) {
            return false;
        }

        if (mUseStudentVPN) {
            return true;
        }

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

    public boolean getCurrentUserInfo() {
        String url = Address.hostAddress + "/ntms/action/getCurrentUserInfo.do";
        if (mUseStudentVPN)
            url = mVPNBaseUrl + "/ntms/action/getCurrentUserInfo.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        FormBody formBody = new FormBody.Builder().build();

        Response response = HTTPTools.postResponse(url, headers, formBody);

        if (response == null) {
            return false;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        if (mUseStudentVPN) {
            JSONObject responseJSON;
            try {
                responseJSON = JSONObject.fromObject(responseStr);
                assert responseJSON != null;
                mCurrentUserInfoJSON = responseJSON;
                mDefResJSON = responseJSON.getJSONObject("defRes");
                personId = mDefResJSON.getInt("personId");
                department = mDefResJSON.getInt("department");
                teachingTerm = mDefResJSON.getInt("teachingTerm");
                System.out.println("department:" + department);
                return true;
            } catch (Exception e) {
                mExceptionList.add(e);
                e.printStackTrace();
                return false;
            }
        } else {
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
                System.out.println("[Response]:\t" + responseStr);
                return false;
            }
            return !TextUtils.isEmpty(jSessionID2);
        }
    }

    public boolean getTeachingTerm() {
        String url = Address.hostAddress + "/ntms/service/res.do";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject params = new JSONObject();
        params.put("termId", teachingTerm);

        JSONObject request_json = new JSONObject();
        request_json.put("tag", "search@teachingTerm");
        request_json.put("branch", "byId");
        request_json.put("params", params);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return false;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            mTeachingTermJSON = responseJSON;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getClassStudentCount() {
        String url = Address.hostAddress + "/ntms/service/res.do";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject params = new JSONObject();
        params.put("deptId", department);
        params.put("egrade", "20" + mStudentId.substring(2, 4));

        JSONObject request_json = new JSONObject();
        request_json.put("tag", "adminClass");
        request_json.put("branch", "deptGrade");
        request_json.put("params", params);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return false;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            JSONArray value = responseJSON.getJSONArray("value");
            for (Object object : value) {
                JSONObject item = (JSONObject) object;
                String className = item.getString("className");
                if (className.equals(mStudentId.substring(0, 6))) {
                    classStudentCount = item.getInt("studCnt");
                    break;
                }
            }
        } catch (Exception e) {
            mExceptionList.add(e);
            System.out.println("[Response]:\t" + responseStr);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean getCourseSelectType() {
        String url = Address.hostAddress + "/ntms/service/res.do";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject request_json1 = new JSONObject();
        request_json1.put("id", "4160");

        JSONObject request_json = new JSONObject();
        request_json.put("branch", "byId");
        request_json.put("params", request_json1);
        request_json.put("tag", "getAllById@sysDict");

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return false;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            mCourseSelectTypeJSON = responseJSON;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getRecentScore() {
        String url = Address.hostAddress + "/ntms/service/res.do";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject params = new JSONObject();

        JSONObject request_json = new JSONObject();
        request_json.put("tag", "archiveScore@queryCourseScore");
        request_json.put("branch", "latest");
        request_json.put("rowLimit", 150);
        request_json.put("params", params);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return false;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            mRecentScoreJSON = responseJSON;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getHistoryScore() {
        String url = Address.hostAddress + "/ntms/service/res.do";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject params = new JSONObject();
        params.put("studId", personId);

        JSONObject request_json = new JSONObject();
        request_json.put("tag", "archiveScore@queryCourseScore");
        request_json.put("branch", "byYear");
        request_json.put("orderBy", "teachingTerm.termId, course.courName");
        request_json.put("type", "search");
        request_json.put("params", params);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return false;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            mRecentScoreJSON = responseJSON;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getScoreStatistics() {
        String url = Address.hostAddress + "/ntms/service/res.do";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject request_json1 = new JSONObject();
        request_json1.put("studId", personId);

        JSONObject request_json = new JSONObject();
        request_json.put("res", "stat-avg-gpoint");
        request_json.put("params", request_json1);
        request_json.put("type", "query");

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return false;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            mScoreStatisticsJSON = responseJSON;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public JSONObject postScorePercent(String asID) {
        String url = Address.hostAddress + "/ntms/score/course-score-stat.do";
        if (mUseStudentVPN)
            url = mVPNBaseUrl + "/ntms/score/course-score-stat.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject request_json = new JSONObject();
        request_json.put("asId", asID);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return null;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            return responseJSON;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return null;
        }
    }

    public HashSet<String> getClassStudentName() {//本班学生
        String url = Address.hostAddress + "/ntms/service/res.do";
        if (mUseStudentVPN)
            url = mVPNBaseUrl + "/ntms/service/res.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject request_json1 = new JSONObject();
        request_json1.put("adcId", mDefResJSON.getInt("adcId"));
        request_json1.put("deptId", mDefResJSON.getInt("department"));
        request_json1.put("egrade", "20" + mStudentId.substring(2, 4));
        request_json1.put("schId", mDefResJSON.getInt("school"));

        JSONObject request_json = new JSONObject();
        request_json.put("branch", "default");
        request_json.put("params", request_json1);
        request_json.put("tag", "student_sch_dept");

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return null;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            HashSet<String> names = new HashSet<>();

            try {//输出内容
                System.out.println(responseJSON);

                JSONArray value = (JSONArray) responseJSON.get("value");

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
                    mExceptionList.add(e1);
                }

//                System.out.println(evalItemIds);

            } catch (Exception e) {
                e.printStackTrace();
                mExceptionList.add(e);
            }
            return names;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> postPingjiaoInformation() {
        String url = Address.hostAddress + "/ntms/service/res.do";
        if (mUseStudentVPN) url = mVPNBaseUrl + "/ntms/service/res.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject request_json1 = new JSONObject();
        request_json1.put("blank", "Y");

        JSONObject request_json = new JSONObject();
        request_json.put("tag", "student@evalItem");
        request_json.put("branch", "self");
        request_json.put("params", request_json1);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return null;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            ArrayList<String> evalItemIds = new ArrayList<>();

            JSONArray value = (JSONArray) responseJSON.get("value");
            JSONObject course = null;
            JSONObject course_inf = null;

            try {
                for (int i = 0; i < value.size(); i++) {
                    course = (JSONObject) value.get(i);
                    course_inf = (JSONObject) course.get("target");
                    evalItemIds.add(course.get("evalItemId").toString());

//                        System.out.println("NUMBER" + (i + 1));
//                        System.out.println("id;\t" + course.get("evalItemId"));
//                        System.out.println("name;\t" + course_inf.get("name"));

//                        course_inf = (JSONObject) course.get("targetClar");

//                        System.out.println("course;\t" + course_inf.get("notes") + "\n");

                }
            } catch (Exception e1) {
                e1.printStackTrace();
                mExceptionList.add(e1);
            }
            return evalItemIds;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject postPingjiao(String evalItemId) {
        String url = Address.hostAddress + "/ntms/action/eval/fetch-eval-item.do";
        if (mUseStudentVPN)
            url = mVPNBaseUrl + "/ntms/action/eval/fetch-eval-item.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject request_json = new JSONObject();
        request_json.put("evalItemId", evalItemId);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return null;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);
            assert responseJSON != null;
            return responseJSON;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return null;
        }
    }

    public boolean postPingjiaoCommit_2020_1(String evalItemId, String puzzle_answer){
        String url = Address.hostAddress + "/ntms/action/eval/eval-with-answer.do";
        if (mUseStudentVPN)
            url = mVPNBaseUrl + "/ntms/action/eval/eval-with-answer.do?vpn-12-o2-uims.jlu.edu.cn";

        HashMap<String, String> mHeaderList = new HashMap<>();
        mHeaderList.put("User-Agent", UIMSStaticRes.UserAgentStr);
        if (!mUseStudentVPN) {
            mHeaderList.put("Cookie", cookie3);
        }
        if (mUseStudentVPN) {
            mHeaderList.put("Cookie", mVPNClient.getCookie());
        }
        List<Map.Entry<String, String>> headers = new ArrayList<>(mHeaderList.entrySet());

        JSONObject j_answers = JSONObject.fromObject(UIMSStaticRes.pingjiao_answer);
        j_answers.put("puzzle_answer", puzzle_answer);

        JSONObject request_json = new JSONObject();
        request_json.put("evalItemId", evalItemId);
        request_json.put("guidelineId", 150);
//        request_json.put("clicks",j_clicks);
        request_json.put("answers", j_answers);

        RequestBody requestBody = RequestBody.create(request_json.toString(), JSON);

        Response response = HTTPTools.postResponse(url, headers, requestBody);

        if (response == null) {
            return false;
        }

        String responseStr = HTTPTools.getOrOrOutResponse(response, true);
        JSONObject responseJSON;
        try {
            responseJSON = JSONObject.fromObject(responseStr);

            if (responseJSON != null) {
                System.out.println(responseJSON.toString());
            } else {
                System.out.println("object IS NULL!");
                return false;
            }

            try {
                System.out.println("INF");
                System.out.println("count:\t" + responseJSON.get("count"));
                System.out.println("errno:\t" + responseJSON.get("errno"));
                System.out.println("msg:\t" + responseJSON.get("msg"));
                System.out.println("status:\t" + responseJSON.get("status") + "\n");
            } catch (Exception e1) {
                e1.printStackTrace();
                mExceptionList.add(e1);
            }

            if (responseJSON.get("status").toString().equals("0")) {
                System.out.println("评教成功！\n");
                return true;
            }
            return false;
        } catch (Exception e) {
            mExceptionList.add(e);
            e.printStackTrace();
            return false;
        }
    }

    public void setStudentId(String studentId) {
        this.mStudentId = studentId;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getStudentId() {
        return mStudentId;
    }

    public JSONObject getCurrentUserInfoJSON() {
        return mCurrentUserInfoJSON;
    }

    public JSONObject getmTeachingTermJSON() {
        return mTeachingTermJSON;
    }

    public int getClassStudentNumber() {
        return classStudentCount;
    }

    public JSONObject getCourseSelectTypeJSON() {
        return mCourseSelectTypeJSON;
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
