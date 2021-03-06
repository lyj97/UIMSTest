package com.lu.mydemo.View.ViewPager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.lu.mydemo.Utils.Course.MySubject;
import com.lu.mydemo.View.MyView.MyCourseDetailView;

public class MyViewPagerAdapter extends PagerAdapter {
    private List<MySubject> list;
    private Context context;

    public MyViewPagerAdapter(Context context, List list, View view) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MyCourseDetailView view = new MyCourseDetailView(context);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
