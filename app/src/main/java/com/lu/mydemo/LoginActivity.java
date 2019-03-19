package com.lu.mydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import UIMS.UIMS;
import UIMSTool.CourseJSONTransfer;

public class LoginActivity extends Activity {

    private EditText id_login;
    private EditText password_login;
    private TextView bottom_text;
    private ImageView avatar_login;
    private CheckBox rememberpassword_login;
    private CheckBox auto_login;
    private Button button_login;
    private SharedPreferences sp;
    private String idvalue;
    private String passwordvalue;
    private static final int PASSWORD_MIWEN = 0x81;

    String user;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //找到相应的布局及控件
        setContentView(R.layout.activity_login);
        id_login=(EditText) findViewById(R.id.login_id);
        password_login=(EditText) findViewById(R.id.login_password);
        avatar_login=(ImageView) findViewById(R.id.login_avatar);
        rememberpassword_login=(CheckBox) findViewById(R.id.login_rememberpassword);
        auto_login=(CheckBox) findViewById(R.id.login_autologin);
        button_login=(Button) findViewById(R.id.login_button);
        bottom_text = findViewById(R.id.textView);
        bottom_text.setText("");
        button_login.setActivated(true);

        if (sp.getBoolean("ischeck",false)){
            rememberpassword_login.setChecked(true);
            id_login.setText(sp.getString("USER",""));
            //密文密码
            password_login.setInputType(PASSWORD_MIWEN);
            password_login.setText(sp.getString("PASSWORD",""));
            if (sp.getBoolean("auto_ischeck",false)){
                auto_login.setChecked(true);
                button_login.setActivated(false);
                Toast.makeText(LoginActivity.this, "自动登录中，请稍候...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_login.setActivated(false);
                showResponse("登录中，请稍候...");
                bottom_text.setText("登录中，请稍候...");
                id_login.getPaint().setFlags(0);
                idvalue=id_login.getText().toString();
                password_login.getPaint().setFlags(0);
                passwordvalue=password_login.getText().toString();
                if(idvalue.length() == 8 && passwordvalue.length() > 0){
                    sendRequestWithOkHttp();
                }
                else{
                    showResponse("请输入正确的用户名和密码！");
                }
            }
        });

        rememberpassword_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rememberpassword_login.isChecked()){
                    System.out.println("记住密码已选中");
                    sp.edit().putBoolean("ischeck",true).apply();
                }
                else {
                    System.out.println("记住密码没有选中");
                    sp.edit().putBoolean("ischeck",false).apply();
                }
            }
        });

        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (auto_login.isChecked()){
                    System.out.println("自动登录已选中");
                    sp.edit().putBoolean("auto_ischeck",true).apply();
                }else {
                    System.out.println("自动登录没有选中");
                    sp.edit().putBoolean("auto_ischeck",false).apply();
                }
            }
        });
    }

    private void loginSuccess(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        button_login.setActivated(true);
        bottom_text.setText("");
//        finish();
    }

    private void sendRequestWithOkHttp() {
        //开启线程发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (rememberpassword_login.isChecked()){
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("USER",idvalue);
                        editor.putString("PASSWORD",passwordvalue);
                        editor.apply();
                    }

                    user = idvalue;
                    pass = passwordvalue;

                    UIMS uims = new UIMS(user, pass);
                    if(uims.connectToUIMS()) {
                        if (uims.login()) {
                            if (uims.getCurrentUserInfo()) {
                                showResponse("欢迎您, " + uims.getNickName() + " .");
                                showResponse("您是UIMS系统第 " + uims.getLoginCounter() + " 位访问者.");
//                                if(uims.getSelectedCourse()) {
//                                    if (CourseJSONTransfer.transfer(uims.getCourseJSON())) {
//                                        showResponse("您的选课信息如下:");
//                                        for (int i = 0; i < CourseJSONTransfer.courses.length; i++) {
//                                            showResponse(CourseJSONTransfer.courses[i].toString());
//                                        }
//                                    }
//                                }
                                if(uims.getScoreStatistics()){
                                    showResponse("查询成绩统计成功！");
                                }
                                if(uims.getRecentScore()){
                                    showResponse("查询成绩成功！");
                                }
                                Log.i("Login", "Succeed!");
                                loginSuccess();
                                return;
                            }
                        }
                    }

                    showResponse("Login failed.");
                    Log.i("Login", "failed!");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void showResponse(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(string.contains("failed")) {
                    bottom_text.setText("登陆失败，请稍后重试.");
                    button_login.setActivated(true);
                }
                Toast.makeText(LoginActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
