package View.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lu.mydemo.R;

public class MyCourseDetailView extends View {

    public int backColor;
    public int textColor;

    //定义画笔
    Paint p = new Paint();

    RectF back_view = new RectF(50, 300, 800, 600);// 设置个新的长方形

    public MyCourseDetailView(Context context) {
        super(context);

        textColor = Color.WHITE;
        backColor = Color.LTGRAY;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画圆角矩形
        p.setStyle(Paint.Style.FILL);//充满
        p.setColor(backColor);
        p.setAntiAlias(true);// 设置画笔的锯齿效果
        canvas.drawRoundRect(back_view, 20, 15, p);//第二个参数是x半径，第三个参数是y半径

        p.setColor(textColor);
        p.setAntiAlias(false);// 设置画笔的锯齿效果
        p.setTextSize(33);
        canvas.drawText("课程", 80, 300, p);
        canvas.drawText("教师", 80, 350, p);
        canvas.drawText("地点", 80, 400, p);
        canvas.drawText("时间", 80, 450, p);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // get calculate mode of width and height
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // get recommend width and height
//        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

//        if (modeWidth == MeasureSpec.UNSPECIFIED) { // this view used in scrollView or listview or recyclerView
//            int wrap_width = 100 + getPaddingLeft() + getPaddingRight();
//            sizeWidth = wrap_width;
//            modeWidth = MeasureSpec.EXACTLY;
//        }
//
//        if (modeHeight == MeasureSpec.UNSPECIFIED) { // this view used in scrollView or listview or recyclerView
//            int wrap_height = 100 + getPaddingTop() + getPaddingBottom();
//            sizeHeight = wrap_height;
//            modeHeight = MeasureSpec.EXACTLY;
//        }
//
//        if (modeWidth == MeasureSpec.AT_MOST) { // wrap_content
//            int wrap_width = 100 + getPaddingLeft() + getPaddingRight();
//            sizeWidth = Math.min(wrap_width,sizeWidth);
//            modeWidth = MeasureSpec.EXACTLY;
//        }
//
//        if (modeHeight == MeasureSpec.AT_MOST) { // wrap_content
//            int wrap_height = 100 + getPaddingTop() + getPaddingBottom();
//            sizeHeight = Math.min(wrap_height,sizeHeight);
//            modeHeight = MeasureSpec.EXACTLY;
//        }

//        widthMeasureSpec = MeasureSpec.makeMeasureSpec(sizeWidth, modeWidth);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(sizeHeight, modeHeight);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(750, modeWidth);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(300, modeHeight);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

}
