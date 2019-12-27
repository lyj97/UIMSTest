package com.lu.mydemo.Activity.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lu.mydemo.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

//    @StringRes
//    private static final int[] TAB_TITLES = new int[]{
//            R.string.tab_text_1,
//            R.string.tab_text_2,
//            R.string.tab_text_3};
    @StringRes
    private static final int[] TAB_TITLES = new int[]{
//            R.string.tab_text_1,
//            R.string.tab_text_2,
            R.string.tab_text_3,
            R.string.tab_text_4};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        switch (position) {
//            case 0 :{
//
//            }
//            case 1 : {
//                return PlaceholderFragment.newInstance(position + 1);
//            }
//            case 2 : {
//                return AllFunctionFragment.newInstance(mContext);
//            }
//            default:{
//                return PlaceholderFragment.newInstance(0);
//            }
//        }
        switch (position) {
            case 0 :{
                return AllFunctionFragment.newInstance(mContext);
            }
            case 1 :{
                return ScoreFragment.newInstance(mContext);
            }
            default:{
                return PlaceholderFragment.newInstance(0);
            }
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}