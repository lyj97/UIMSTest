package com.lu.mydemo.Activity.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.lu.mydemo.Utils.Score.ScoreInf;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "Hello world from section: " + input;
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public static final String UPDATE_SCORE_FLAG = "UPDATE_SCORE_FLAG";
    public static final String UPDATE_YKT_INF = "UPDATE_YKT_INF";

    private MutableLiveData<String> mFlag = new MutableLiveData<>();
    private LiveData<String> mData = Transformations.map(mFlag, new Function<String, String>() {
        @Override
        public String apply(String input) {
            switch (input){
                case UPDATE_SCORE_FLAG : {
                    return UPDATE_SCORE_FLAG;
                }
                case UPDATE_YKT_INF : {
                    return UPDATE_YKT_INF;
                }
            }
            return null;
        }
    });

    public void updateScore(){
        mFlag.setValue(UPDATE_SCORE_FLAG);
    }

    public void updateYKTInformation(){
        mFlag.setValue(UPDATE_YKT_INF);
    }

    public LiveData<String> getData(){
        return mData;
    }

}