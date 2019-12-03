package View.View;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * 创建时间: 2019/11/04 14:15 <br>
 * 作者: luyajun002 <br>
 * 描述:长按条目添加遮罩操作帮助类
 */
public class ItemLongClickMaskHelper {
    private FrameLayout mRootFrameLayout;
    private ItemMaskLayout mMaskItemLayout;
    private Context mContext;
    private ScaleAnimation anim;
    private String productId;
    public ItemLongClickMaskHelper(final Context context){
        this.mContext = context;
        mMaskItemLayout = new ItemMaskLayout(mContext);
        anim = new ScaleAnimation(
                0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        );
        anim.setDuration(300);
        mMaskItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissItemMaskLayout();
            }
        });

        mMaskItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dismissItemMaskLayout();
                return true;
            }
        });

        mMaskItemLayout.setMaskItemClickListener(new ItemMaskLayout.ItemMaskClickListener() {
            @Override
            public void delete() {
//                Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ItemLongClickMaskHelper setRootFrameLayout(FrameLayout frameLayout){
        return setRootFrameLayout(frameLayout, null);
    }

    public ItemLongClickMaskHelper setRootFrameLayout(FrameLayout frameLayout, String fundId){
        if (mRootFrameLayout != null){
            mRootFrameLayout.removeView(mMaskItemLayout);
        }
        mRootFrameLayout = frameLayout;
        this.productId = fundId;
        mRootFrameLayout.addView(mMaskItemLayout);
        mMaskItemLayout.startAnimation(anim);
        return this;
    }

    public ItemLongClickMaskHelper setMaskItemListener(ItemMaskLayout.ItemMaskClickListener listener){
        this.mMaskItemLayout.setMaskItemClickListener(listener);
        return this;
    }

    /**
     * 遮罩消失
     */
    public void dismissItemMaskLayout(){
        if (mRootFrameLayout != null){
            mRootFrameLayout.removeView(mMaskItemLayout);
        }
    }
}
