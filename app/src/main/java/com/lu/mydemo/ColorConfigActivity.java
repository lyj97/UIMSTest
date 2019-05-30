package com.lu.mydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import Config.ColorManager;

public class ColorConfigActivity extends AppCompatActivity {

    private LinearLayout activity_color_config_layout;

    private TextView text_view_blue;
    private TextView text_view_pink;
    private TextView text_view_green;

    private TextView navigation_back;

    private String theme = ColorManager.getThemeName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_config);

        activity_color_config_layout = findViewById(R.id.activity_color_config_layout);

        text_view_blue = findViewById(R.id.color_config_blue_text);
        text_view_pink = findViewById(R.id.color_config_pink_text);
        text_view_green = findViewById(R.id.color_config_green_text);

        navigation_back = findViewById(R.id.color_config_navigation_back_text);

        changeTheme();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.color_config_blue_text : {
                        if(!theme.equals("blue")){
                            theme = "blue";
                            ColorManager.saveTheme(theme);
                            changeTheme();
                        }
                        break;
                    }
                    case R.id.color_config_pink_text : {
                        if(!theme.equals("pink")){
                            theme = "pink";
                            ColorManager.saveTheme(theme);
                            changeTheme();
                        }
                        break;
                    }
                    case R.id.color_config_green_text : {
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

        navigation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void changeTheme(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ColorManager.getPrimaryColor());

        activity_color_config_layout.setBackground(ColorManager.getMainBackground_full());
    }
}
