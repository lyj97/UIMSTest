package com.lu.mydemo.View.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 * 创建时间: 2019/11/04 14:34 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class TouchCallbackRecyclerView extends SwipeRecyclerView {
    public TouchCallbackRecyclerView(Context context) {
        super(context);
    }

    public TouchCallbackRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchCallbackRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public interface ScrollCallback {
        /**
         * 滑动手指抬起事件
         *
         * @param diffY 抬起时相对于按下时的偏移量<br/>大于0：列表往下拉, 小于0： 列表往上拉
         */
        void onTouchUp(float diffY);
    }

    private ScrollCallback mScrollCallback;
    public void setScrollCallback(ScrollCallback callback) {
        this.mScrollCallback = callback;
    }

    private float mDownY, mMovingY, mUpY;
    private boolean isUp = false;

    @SuppressWarnings("deprecation")
    private static final float SLOP = ViewConfiguration.getTouchSlop();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                isUp = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mMovingY = ev.getY();
                isUp = false;
                break;
            case MotionEvent.ACTION_UP:
                mUpY = ev.getY();
                isUp = true;
                break;
        }
        if (isUp && mScrollCallback != null && Math.abs(mUpY - mDownY) > SLOP) {
            mScrollCallback.onTouchUp(mMovingY - mDownY);
        }
        return super.dispatchTouchEvent(ev);
    }
}