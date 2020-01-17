package com.lu.mydemo.CJCX;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lu.mydemo.Activity.ScoreActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import com.lu.mydemo.Net.HTTP;
import com.lu.mydemo.Utils.Common.Address;
import com.lu.mydemo.UIMS.GetMD5;
import com.lu.mydemo.Utils.Score.ScoreConfig;
import com.lu.mydemo.Utils.Score.ScoreInf;
import com.lu.mydemo.Utils.Thread.MyThreadController;
import okhttp3.FormBody;

public class CJCX {

    static SharedPreferences sp;

    String user;
    String pass;
    String md5_pass;
    String cookie;
    String cookie1;//带PHPSESSID
    String PHPSESSID;

    int update_count = -1;

    static JSONObject CJCXScoreJSON;
    static JSONObject CJCXTermJSON;
    static HashMap<String, String> termId_termName = new HashMap<>();//学期ID-NAME
    static HashMap<String, JSONObject> id_JSON = new HashMap<>();

    HTTP client = new HTTP();

    public CJCX(String user, String pass){
        this.user = user;
        this.pass = pass;
    }

    public boolean login(){
        try{
            md5_pass = GetMD5.getMD5Str("UIMS" + user + pass);
            cookie =  "loginPage=userLogin.jsp; alu=" + user + "; alp=" + md5_pass + "; ald=;";
            FormBody formBody = new FormBody.Builder()
                    .add("j_username",user)
                    .add("j_password", md5_pass)
                    .build();
            String response = client.sendPost_form_getHhader_setCookie(Address.cjcxHostAddress + "/score/action/security_check.php", Address.cjcxHostAddress + "/score/userLogin.php", cookie, formBody);
//            System.out.println(response);
            String str = response.split(";")[0];
            PHPSESSID = str.split("=")[1];
//            System.out.println(PHPSESSID);
            cookie1 = "PHPSESSID=" + PHPSESSID + "; " + cookie;
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getScore(){
        try{
            update_count = 0;
            JSONObject request_json1 = new JSONObject();
            request_json1.put("xh",user);

            JSONObject request_json = new JSONObject();
            request_json.put("params",request_json1);
            request_json.put("tag","lessonSelectResult@oldStudScore");

            String response = client.sendPost_JSON(Address.cjcxHostAddress + "/score/action/service_res.php", Address.cjcxHostAddress + "/score/index.php", Address.cjcxHost, cookie1, request_json);
//            System.out.println(response);
            CJCXScoreJSON = new JSONObject(response);
            JSONArray items = CJCXScoreJSON.getJSONArray("items");
            JSONObject temp;
            for(int i=0; i<items.length(); i++){
                temp = items.getJSONObject(i);
                if(!id_JSON.containsKey(temp.getString("lsrId"))){
                    id_JSON.put(temp.getString("lsrId"), temp);
                    update_count++;
                }

            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 参数
     * orderBy	termId desc
     * res	teachingTerm
     * tag	teachingTerm
     * type	search
     */
    public boolean getTeachingTerm(){
        try{
            JSONObject request_json = new JSONObject();
            request_json.put("orderBy", "termId desc");
            request_json.put("res", "teachingTerm");
            request_json.put("tag", "teachingTerm");
            request_json.put("type", "search");

            String response = client.sendPost_JSON(Address.cjcxHostAddress + "/score/action/service_res.php", Address.cjcxHostAddress + "/score/index.php", Address.cjcxHost, cookie1, request_json);
//            System.out.println(response);
            CJCXTermJSON = new JSONObject(response);
            dealTermArray();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void dealTermArray(){
        try {
            termId_termName = new HashMap<>();
            JSONArray value = CJCXTermJSON.getJSONArray("items");
            JSONObject temp;
            for (int i = 0; i < value.length(); i++) {
                temp = value.getJSONObject(i);
                termId_termName.put(temp.getString("termId"), temp.getString("termName"));
//            Log.i("Term " + i, temp.toString());
            }
            if(! (termId_termName.size() > 0)){
                Log.e("com/lu/mydemo/CJCX", "CJCXTerm load error!(SIZE is 0)");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getUpdate_count() {
        return update_count;
    }

    private static void dealJSON(){
        try{
            JSONArray items = CJCXScoreJSON.getJSONArray("items");
            JSONObject temp;
            for(int i=0; i<items.length(); i++){
                temp = items.getJSONObject(i);
                id_JSON.put(temp.getString("lsrId"), temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void test(String[] args) {
        CJCX cjcx = new CJCX("user", "pass");
        cjcx.login();
        cjcx.getScore();
    }

    public static JSONObject getCJCXScoreJSON() {
        return CJCXScoreJSON;
    }

    public static void setCJCXScoreJSON(JSONObject CJCXScoreJSON) {
        CJCX.CJCXScoreJSON = CJCXScoreJSON;
        dealJSON();
    }

    public static void setCJCXEnable(Context context, boolean enable){
        if(sp == null){
            sp = context.getSharedPreferences("CJCXScore", Context.MODE_PRIVATE);
        }
        try {
            ScoreConfig.setCJCXEnable(context.getApplicationContext(), enable);
            sp.edit().putBoolean("CJCXEnable", ScoreConfig.isIsCJCXEnable()).apply();
            MyThreadController.commit(new Runnable() {
                @Override
                public void run() {
                    ScoreActivity.setReLoadSocreList(true);
                    ScoreInf.loadScoreList();
                    ScoreActivity.getScoreList();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveCJCXJSON(Context context, JSONObject object){
        if(sp == null){
            sp = context.getSharedPreferences("CJCXScore", Context.MODE_PRIVATE);
        }
        try {
            sp.edit().putString("CJCXScore", object.toString()).apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveCJCXJSON(Context context){
        if(sp == null){
            sp = context.getSharedPreferences("CJCXScore", Context.MODE_PRIVATE);
        }
        try {
            if(CJCX.CJCXScoreJSON != null) {
                sp.edit().putString("CJCXScore", CJCX.CJCXScoreJSON.toString()).apply();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadCJCXJSON(Context context){
        if(sp == null){
            sp = context.getSharedPreferences("CJCXScore", Context.MODE_PRIVATE);
        }
        try{
            ScoreConfig.setCJCXEnable(context.getApplicationContext(), sp.getBoolean("CJCXEnable", true));
            if(!ScoreConfig.isIsCJCXEnable()) return;
            // TEST 忽略本地CJCX SCORE存储
//            Log.e("TEST", "Local com.lu.mydemo.CJCX Score ignored for TEST!");
            setCJCXScoreJSON(new JSONObject(sp.getString("CJCXScore", "")));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveCJCXTermJSON(Context context, JSONObject object){
        if(sp == null){
            sp = context.getSharedPreferences("CJCXScore", Context.MODE_PRIVATE);
        }
        try {
            sp.edit().putString("CJCXTermJSON", object.toString()).apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveCJCXTermJSON(Context context){
        if(sp == null){
            sp = context.getSharedPreferences("CJCXScore", Context.MODE_PRIVATE);
        }
        try {
            if(CJCX.CJCXTermJSON != null) {
                sp.edit().putString("CJCXTermJSON", CJCX.CJCXTermJSON.toString()).apply();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadCJCXTermJSON(Context context){
        if(sp == null){
            sp = context.getSharedPreferences("CJCXScore", Context.MODE_PRIVATE);
        }
        try{
            CJCX.CJCXTermJSON = new JSONObject(sp.getString("CJCXTermJSON", ""));
            if(!ScoreConfig.isIsCJCXEnable()) return;
            // TEST 忽略本地CJCX SCORE存储
//            Log.e("TEST", "Local com.lu.mydemo.CJCX Score ignored for TEST!");
            dealTermArray();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static JSONObject getCJCXTermJSON() {
        return CJCXTermJSON;
    }

    public static void setCJCXTermJSON(JSONObject CJCXTermJSON) {
        CJCX.CJCXTermJSON = CJCXTermJSON;
    }

    public static HashMap<String, JSONObject> getId_JSON() {
        if(!ScoreConfig.isIsCJCXEnable()) return new HashMap<>();
        return id_JSON;
    }

    public static HashMap<String, String> getTermId_termName() {
        if(sp != null) loadCJCXTermJSON(null);
        else Log.e("com/lu/mydemo/CJCX", "CJCXTerm noe loaded!");
        return termId_termName;
    }

    public static void setTermId_termName(HashMap<String, String> termId_termName) {
        CJCX.termId_termName = termId_termName;
    }
}
