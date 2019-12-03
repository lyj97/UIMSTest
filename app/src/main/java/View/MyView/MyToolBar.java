package View.MyView;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.mydemo.R;

/**
 * 创建时间: 2019/12/02 11:47 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class MyToolBar {

    final private Activity context;

    private TextView mainTitleTv;
    private TextView subTitleTv;
    private ImageView leftIconIv;
    private ImageView rightIconIv;

    public MyToolBar(Activity context){
        this.context = context;
        initView();
    }

    public void initView(){
        mainTitleTv = context.findViewById(R.id.tool_bar_main_title);
        subTitleTv = context.findViewById(R.id.tool_bar_sub_title);
        leftIconIv = context.findViewById(R.id.tool_bar_back);
        rightIconIv = context.findViewById(R.id.tool_bar_right_icon);
        initDefault();
    }

    public void initDefault(){
        mainTitleTv.setText(context.getText(R.string.app_name));
        subTitleTv.setText(context.getText(R.string.app_name));
        rightIconIv.setVisibility(View.INVISIBLE);
        leftIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
    }

    public void setMainTitle(String title){
        mainTitleTv.setText(title);
    }

    public void setSubTitle(String title){
        subTitleTv.setText(title);
    }

    public void setLeftOnClickListener(View.OnClickListener onClickListener){
        leftIconIv.setOnClickListener(onClickListener);
    }

    public void setRightOnClickListener(View.OnClickListener onClickListener){
        setRightIconVisible(true);
        rightIconIv.setOnClickListener(onClickListener);
    }

    public void setLeftIconVisible(boolean visible){
        leftIconIv.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setRightIconVisible(boolean visible){
        rightIconIv.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setLeftIcon(Drawable drawable){
        leftIconIv.setImageDrawable(drawable);
    }

    public void setRightIcon(Drawable drawable){
        rightIconIv.setImageDrawable(drawable);
    }

    public TextView getMainTitleTv() {
        return mainTitleTv;
    }

    public TextView getSubTitleTv() {
        return subTitleTv;
    }

    public ImageView getLeftIconIv() {
        return leftIconIv;
    }

    public ImageView getRightIconIv() {
        return rightIconIv;
    }
}
