package com.lu.mydemo.Activity.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.lu.mydemo.Utils.Fragement.FragmentLabels;

/**
 * 创建时间: 2019/12/25 16:22 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class YKTFragment extends Fragment {

    private FragmentLabels label = FragmentLabels.YKT_GRAGNEMT;

    private static YKTFragment instance;

    private PageViewModel pageViewModel;

    private Context context;

    public static YKTFragment newInstance(Context context){
        if(instance == null){
            instance = new YKTFragment();
            instance.context = context;
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        pageViewModel.updateYKTInformation();
    }

}
