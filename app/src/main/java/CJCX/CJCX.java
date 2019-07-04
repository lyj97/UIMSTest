package CJCX;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import Net.HTTP;
import UIMS.Address;
import UIMS.GetMD5;
import okhttp3.FormBody;

public class CJCX {

    String user;
    String pass;
    String md5_pass;
    String cookie;
    String cookie1;//带PHPSESSID
    String PHPSESSID;

    static JSONObject CJCXScoreJSON;
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
                id_JSON.put(temp.getString("lsrId"), temp);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
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
        CJCX cjcx = new CJCX("54160907", "225577");
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

    public static HashMap<String, JSONObject> getId_JSON() {
        return id_JSON;
    }

}
