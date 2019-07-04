package View.Control;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.mydemo.R;

public class FABBehavior extends FloatingActionButton.Behavior {
    private boolean visible = true;
    TextView mainTextArea;
    private int distance = 0;
    String TAG = FABBehavior.class.getSimpleName();

    public FABBehavior() {
    }

    public FABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (mainTextArea == null)
            mainTextArea = coordinatorLayout.findViewById(R.id.activity_main_ScoreStatistics);

        Log.i(TAG, "onNestedPreScroll");
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                       FloatingActionButton child, View directTargetChild, View target,
                                       int nestedScrollAxes) {

        Log.i(TAG, "onStartNestedScroll ");
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild,
                target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                               FloatingActionButton child, View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i(TAG, "onNestedScroll dxConsumed=" + dxConsumed + ",dyConsumed=" + dyConsumed);
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 20 && visible) {
            //隐藏动画
            visible = false;
            onHide(child);
            distance = 0;
        } else if (dyConsumed < -20 && !visible) {
            //显示动画
            visible = true;
            onShow(child);
            distance = 0;
        }
        if (visible && dyConsumed > 0 || (!visible && dyConsumed < 0)) {
            distance += dyUnconsumed;
        }

    }

    public void onHide(FloatingActionButton fab) {

        mainTextArea.animate().translationY(-mainTextArea.getHeight()).setInterpolator(new AccelerateInterpolator(3));
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

        fab.animate().translationY(fab.getHeight() + ((CoordinatorLayout.LayoutParams) layoutParams).bottomMargin).setInterpolator(new AccelerateInterpolator(3));
        //gxw- ViewCompat.animate(fab).scaleX(0f).scaleY(0f).start();
    }

    public void onShow(FloatingActionButton fab) {

        mainTextArea.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fab.getLayoutParams();
        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        //gxw+-ViewCompat.animate(fab).scaleX(1f).scaleY(1f).start();
    }

}