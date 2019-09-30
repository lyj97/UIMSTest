package com.lu.mydemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.mydemo.Notification.AlertCenter;

import Config.ColorManager;

public class ColorConfigActivity extends AppCompatActivity {

    private LinearLayout activity_color_config_layout;

//    private LinearLayout config_text;
    private TextView chooseImage_tv;
    private TextView deleteImage_tv;

    private TextView text_view_blue;
    private TextView text_view_pink;
    private TextView text_view_green;

    private TextView navigation_back;

    boolean isGranted = false;

    private String theme = ColorManager.getThemeName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_config);

        activity_color_config_layout = findViewById(R.id.activity_color_config_layout);

//        config_text = findViewById(R.id.color_config_text_layout);
        chooseImage_tv = findViewById(R.id.color_config_choose_img);
        deleteImage_tv = findViewById(R.id.color_config_delete_custom_img);

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
                    case R.id.color_config_choose_img : {
                        try {
                            checkFilePermission();
                            Intent intent_gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent_gallery, 1);
                        }catch (Exception e){
                            e.printStackTrace();
                            AlertCenter.showWarningAlert(ColorConfigActivity.this, "自定义背景需要您授予\"访问外部存储空间\"权限，请您授权后重试。");
                            checkFilePermission();
                        }
                    }
                    case R.id.color_config_delete_custom_img : {
                        ColorManager.deleteCustomBg();
                        ColorManager.loadColorConfig(getApplicationContext());
                        changeTheme();
                    }
                }
            }
        };

//        config_text.setOnClickListener(onClickListener);
        chooseImage_tv.setOnClickListener(onClickListener);
        deleteImage_tv.setOnClickListener(onClickListener);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ColorConfigActivity", "ActivityResult:" + data);

        if (requestCode == 1&& resultCode == Activity.RESULT_OK
                && data != null) {
            Uri selectedImage = data.getData();//返回的是uri

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);

            Bitmap bitmap = BitmapFactory.decodeFile(path);

//            Drawable drawable =new BitmapDrawable(bitmap);

            ColorManager.setMainBackground(new BitmapDrawable(getResources(), bitmap));
            ColorManager.saveCustomBg(path);
            changeTheme();
        }

    }

    public void checkFilePermission() {
        isGranted = true;
        if (ColorConfigActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //如果没有写sd卡权限
            isGranted = false;
        }
        if (ColorConfigActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            isGranted = false;
        }
        Log.i("File","isGranted == "+isGranted);
        if (!isGranted) {
            ColorConfigActivity.this.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                            .ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    102);
        }
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
