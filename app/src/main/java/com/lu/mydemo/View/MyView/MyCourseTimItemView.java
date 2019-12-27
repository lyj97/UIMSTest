package com.lu.mydemo.View.MyView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.lu.mydemo.R;

/**
 * 创建时间: 2019/10/15 14:31 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class MyCourseTimItemView extends LinearLayout {

    public View rootView;

    public int backColor;
    public int textColor;

    public MyCourseTimItemView(Context context) {
        super(context);
        textColor = Color.WHITE;
        backColor = Color.LTGRAY;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.card_course_detail_edit, this);
    }

}
