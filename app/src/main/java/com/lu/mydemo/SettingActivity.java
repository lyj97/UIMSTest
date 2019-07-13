package com.lu.mydemo;

import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;

import net.sf.json.JSONObject;

import java.util.ArrayList;

import CJCX.CJCX;
import Config.ColorManager;
import UIMS.UIMS;

public class SettingActivity extends AppCompatActivity {

    private TextView back_text;

    private Spinner spinner;
    private ArrayList<String> termList;
    private boolean hasTerm = false;

    private CheckBox check_box_xuanxiu;
    private CheckBox check_box_xianxuan;
    private CheckBox check_box_PE;
    private CheckBox check_box_xiaoxuanxiu;
    private TextView text_xuanxiu;
    private TextView text_xianxuan;
    private TextView text_PE;
    private TextView text_xiaoxuanxiu;

    private CheckBox check_box_cjcx_enable;
    private TextView text_cjcx_enable;

    private CheckBox check_box_test;
    private TextView text_test;

    private TextView text_view_blue;
    private TextView text_view_pink;
    private TextView text_view_green;

//    private TextView testText;

    private String theme = ColorManager.getThemeName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        back_text = findViewById(R.id.activity_setting_navigation_back_text);

        spinner = findViewById(R.id.activity_setting_term_spinner);

        check_box_xuanxiu = findViewById(R.id.activity_setting_score_xuanxiu_checkbox);
        check_box_xianxuan = findViewById(R.id.activity_setting_score_xianxuan_checkbox);
        check_box_PE = findViewById(R.id.activity_setting_score_PE_checkbox);
        check_box_xiaoxuanxiu = findViewById(R.id.activity_setting_score_xiaoxuanxiu_checkbox);

        text_xuanxiu = findViewById(R.id.activity_setting_score_xuanxiu_text);
        text_xianxuan = findViewById(R.id.activity_setting_score_xianxuan_text);
        text_PE = findViewById(R.id.activity_setting_score_PE_text);
        text_xiaoxuanxiu = findViewById(R.id.activity_setting_score_xiaoxuanxiu_text);

        check_box_cjcx_enable = findViewById(R.id.activity_setting_score_cjcx_checkbox);
        text_cjcx_enable = findViewById(R.id.activity_setting_score_cjcx_text);

        check_box_test = findViewById(R.id.activity_setting_test_enable_checkbox);
        text_test = findViewById(R.id.activity_setting_test_enable_text);

        text_view_blue = findViewById(R.id.activity_setting_color_blue_text);
        text_view_pink = findViewById(R.id.activity_setting_color_pink_text);
        text_view_green = findViewById(R.id.activity_setting_color_green_text);

//        testText = findViewById(R.id.activity_setting_test_text);

        changeTheme();

        setSpinnerItems();

        loadScoreSelect();

        loadTestSelect();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(hasTerm) {
                    String term = termList.get(position);
                    Log.i("Term", term);
                    if(term != null){
                        if(term.equals(UIMS.getTermName())){
                            Log.i("SetTerm", "Ignored! Term not change.");
                            MainActivity.setIsCourseNeedReload(false);
                            return;
                        }
                        JSONObject termJSON = UIMS.getTermJSON(term);
                        if(termJSON == null){
                            showWarningAlert("ERROR", "TermJSON is null.");
                            return;
                        }
                        Log.i("TermJSON", termJSON.toString());
                        UIMS.setTeachingTerm(termJSON);
                        MainActivity.saveTeachingTerm();
                        Toast.makeText(SettingActivity.this, "当前学期已设为：\t" + UIMS.getTermName(), Toast.LENGTH_SHORT).show();
                    } else {
//                    Toast.makeText(CourseScheduleChangeActivity.this, "TermJSON is NULL!", Toast.LENGTH_SHORT).show();
                        Log.e("TermJSON", "TermJSON is NULL!");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(SettingActivity.this, "OnCheckedChange!", Toast.LENGTH_SHORT).show();
                ScoreActivity.setReLoadSocreList(true);
                switch (buttonView.getId()){
                    case R.id.activity_setting_score_xuanxiu_checkbox : {
                        ScoreActivity.setXuanxiu_select(check_box_xuanxiu.isChecked());
                        break;
                    }
                    case R.id.activity_setting_score_xianxuan_checkbox : {
                        ScoreActivity.setXianxuan_select(check_box_xianxuan.isChecked());
                        break;
                    }
                    case R.id.activity_setting_score_xiaoxuanxiu_checkbox : {
                        ScoreActivity.setXiaoxuanxiu_select(check_box_xiaoxuanxiu.isChecked());
                        break;
                    }
                    case R.id.activity_setting_score_PE_checkbox : {
                        ScoreActivity.setPE_select(check_box_PE.isChecked());
                        break;
                    }
                    case R.id.activity_setting_score_cjcx_checkbox : {
                        CJCX.setCJCXEnable(getApplicationContext(), check_box_cjcx_enable.isChecked());
                        break;
                    }
                    case R.id.activity_setting_test_enable_checkbox : {
                        MainActivity.setAcceptTestFun(check_box_test.isChecked());
                        break;
                    }
                }
            }
        };

//        View.OnClickListener check_onclick = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(SettingActivity.this, "OnClick!", Toast.LENGTH_SHORT).show();
//                switch (v.getId()) {
//                    case R.id.activity_setting_score_xuanxiu_checkbox: {
//                        ScoreActivity.setXuanxiu_select(check_box_xuanxiu.isChecked());
//                        break;
//                    }
//                    case R.id.activity_setting_score_xianxuan_checkbox: {
//                        ScoreActivity.setXianxuan_select(check_box_xianxuan.isChecked());
//                        break;
//                    }
//                    case R.id.activity_setting_score_xiaoxuanxiu_checkbox: {
//                        ScoreActivity.setXiaoxuanxiu_select(check_box_xiaoxuanxiu.isChecked());
//                        break;
//                    }
//                    case R.id.activity_setting_score_PE_checkbox: {
//                        ScoreActivity.setPE_select(check_box_PE.isChecked());
//                        break;
//                    }
//                }
//            }
//        };

//        check_box_xuanxiu.setOnClickListener(check_onclick);
//        check_box_xianxuan.setOnClickListener(check_onclick);
//        check_box_xiaoxuanxiu.setOnClickListener(check_onclick);
//        check_box_PE.setOnClickListener(check_onclick);

        check_box_xuanxiu.setOnCheckedChangeListener(onCheckedChangeListener);
        check_box_xianxuan.setOnCheckedChangeListener(onCheckedChangeListener);
        check_box_xiaoxuanxiu.setOnCheckedChangeListener(onCheckedChangeListener);
        check_box_PE.setOnCheckedChangeListener(onCheckedChangeListener);
        check_box_cjcx_enable.setOnCheckedChangeListener(onCheckedChangeListener);
        check_box_test.setOnCheckedChangeListener(onCheckedChangeListener);

        View.OnClickListener text_onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.activity_setting_score_xuanxiu_text:{
                        check_box_xuanxiu.setChecked(! check_box_xuanxiu.isChecked());
                        break;
                    }
                    case R.id.activity_setting_score_xianxuan_text:{
                        check_box_xianxuan.setChecked(! check_box_xianxuan.isChecked());
                        break;
                    }
                    case R.id.activity_setting_score_PE_text:{
                        check_box_PE.setChecked(! check_box_PE.isChecked());
                        break;
                    }
                    case R.id.activity_setting_score_xiaoxuanxiu_text:{
                        check_box_xiaoxuanxiu.setChecked(! check_box_xiaoxuanxiu.isChecked());
                        break;
                    }
                    case R.id.activity_setting_score_cjcx_text:{
                        check_box_cjcx_enable.setChecked(! check_box_cjcx_enable.isChecked());
                        break;
                    }
                    case R.id.activity_setting_test_enable_text:{
                        check_box_test.setChecked(! check_box_test.isChecked());
                        break;
                    }
                }
            }
        };

        text_xuanxiu.setOnClickListener(text_onClick);
        text_xianxuan.setOnClickListener(text_onClick);
        text_PE.setOnClickListener(text_onClick);
        text_xiaoxuanxiu.setOnClickListener(text_onClick);
        text_cjcx_enable.setOnClickListener(text_onClick);
        text_test.setOnClickListener(text_onClick);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.activity_setting_color_blue_text : {
                        if(!theme.equals("blue")){
                            theme = "blue";
                            ColorManager.saveTheme(theme);
                            changeTheme();
                        }
                        break;
                    }
                    case R.id.activity_setting_color_pink_text : {
                        if(!theme.equals("pink")){
                            theme = "pink";
                            ColorManager.saveTheme(theme);
                            changeTheme();
                        }
                        break;
                    }
                    case R.id.activity_setting_color_green_text : {
                        if(!theme.equals("green")){
                            theme = "green";
                            ColorManager.saveTheme(theme);
                            changeTheme();
                        }
                        break;
                    }
                }
            }
        };

        text_view_blue.setOnClickListener(onClickListener);
        text_view_pink.setOnClickListener(onClickListener);
        text_view_green.setOnClickListener(onClickListener);

        back_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        testText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        CJCX cjcx = new CJCX("54160907", "225577");
//                        cjcx.login();
//                        cjcx.getScore();
//                    }
//                }).start();
//            }
//        });

//        opening = false;
    }

    private void setSpinnerItems(){
        if(MainActivity.isLocalValueLoaded){
            termList = CourseScheduleChangeActivity.getTermArray();
            if(!(termList.size() > 0)){
                termList = new ArrayList<>();
                termList.add("暂无学期数据\n请在首页点击“刷新信息”按钮");
                spinner.setAdapter(new ArrayAdapter(this, R.layout.select_item, R.id.select_text_item, termList));
                hasTerm = false;
            }
            else{
                spinner.setAdapter(new ArrayAdapter(this, R.layout.select_item, R.id.select_text_item, termList));
                spinner.setSelection(termList.indexOf(UIMS.getTermName()));
                hasTerm = true;
            }
        }
        else{
            hasTerm = false;
            termList = new ArrayList<>();
            termList.add("暂无学期数据\n请在首页点击“刷新信息”按钮");
            spinner.setAdapter(new ArrayAdapter(this, R.layout.select_item, R.id.select_text_item, termList));
//            finish();
        }
    }

    private void loadScoreSelect() {
        check_box_xuanxiu.setChecked(ScoreActivity.isXuanxiu_select());
        check_box_xianxuan.setChecked(ScoreActivity.isXianxuan_select());
        check_box_xiaoxuanxiu.setChecked(ScoreActivity.isXiaoxuanxiu_select());
        check_box_PE.setChecked(ScoreActivity.isPE_select());
        check_box_cjcx_enable.setChecked(CJCX.isIsCJCXEnable());
    }

    private void loadTestSelect(){
        check_box_test.setChecked(MainActivity.isAcceptTestFun());
    }

    private ColorStateList getColorStateListTest() {
        int[][] states = new int[][]{
//                new int[]{android.R.attr.state_enabled}, // enabled
//                new int[]{-android.R.attr.state_enabled}, // disabled
//                new int[]{android.R.attr.state_checked}, // unchecked
//                new int[]{android.R.attr.state_pressed},  // pressed
                new int[]{android.R.attr.colorAccent}
        };
        int color = ColorManager.getPrimaryColor();
        int[] colors = new int[]{color};
        return new ColorStateList(states, colors);
    }

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        findViewById(R.id.activity_setting).setBackground(ColorManager.getMainBackground_full());

        check_box_xuanxiu.setForegroundTintList(getColorStateListTest());
        check_box_xianxuan.setForegroundTintList(getColorStateListTest());
        check_box_PE.setForegroundTintList(getColorStateListTest());
        check_box_xiaoxuanxiu.setForegroundTintList(getColorStateListTest());
    }

    public void showResponse(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingActivity.this, string, Toast.LENGTH_SHORT).show();
//                showAlert(string);
            }
        });
    }

    public void showLoading(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(SettingActivity.this)
                        .setText(message)
                        .enableProgress(true)
                        .setDismissable(false)
                        .setProgressColorRes(R.color.color_alerter_progress_bar)
                        .setDuration(Integer.MAX_VALUE)
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(SettingActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showAlert(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(SettingActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public void showWarningAlert(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(SettingActivity.this)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .show();
            }
        });
    }

    public void showWarningAlert(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(SettingActivity.this)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(getResources().getColor(R.color.color_alerter_warning_background))
                        .show();
            }
        });
    }
}
