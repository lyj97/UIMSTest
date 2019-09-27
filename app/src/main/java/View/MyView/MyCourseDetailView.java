package View.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.PaintDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.lu.mydemo.R;

public class MyCourseDetailView extends View {

    public View rootView;

    public int backColor;
    public int textColor;

    public LinearLayout myDetailView;


    public MyCourseDetailView(Context context) {
        super(context);
        textColor = Color.WHITE;
        backColor = Color.LTGRAY;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.view_course_detail, null);
    }

    public MyCourseDetailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获取资源文件里面的属性，由于这里只有一个属性值，不用遍历数组，直接通过R文件拿出color值
        //把属性放在资源文件里，方便设置和复用
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyCourseDetailView);
        textColor = array.getColor(R.styleable.MyCourseDetailView_textColor, Color.WHITE);
        backColor = array.getColor(R.styleable.MyCourseDetailView_backColor, Color.LTGRAY);
        array.recycle();
    }

    public void initView(){
        myDetailView = rootView.findViewById(R.id.pop_window_course_detail_layout);
        myDetailView.setBackground(new PaintDrawable(Color.WHITE));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

}
