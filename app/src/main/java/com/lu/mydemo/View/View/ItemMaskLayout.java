package com.lu.mydemo.View.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.lu.mydemo.R;

/**
 * 创建时间: 2019/11/04 14:04 <br>
 * 作者: luyajun002 <br>
 * 描述:长按条目遮罩界面
 */

public class ItemMaskLayout extends LinearLayout {

    public ItemMaskLayout(Context context) {
        this(context, null);
    }

    public ItemMaskLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemMaskLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.list_item_mask_delete, this, true);
        findViewById(R.id.list_item_mask_delete_layout_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemMaskClickListener != null) {
                    mItemMaskClickListener.delete();
                }
            }
        });
//        findViewById(R.id.tv_collection).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(com.lu.mydemo.View v) {
//                if (mItemMaskClickListener != null) {
//                    mItemMaskClickListener.collection();
//                }
//            }
//        });
    }

    public ItemMaskClickListener mItemMaskClickListener;
    public void setMaskItemClickListener(ItemMaskClickListener listener) {
        this.mItemMaskClickListener = listener;
    }

    //提供遮罩中按钮点击操作接口 自定义
    public interface ItemMaskClickListener {
//        void findTheSame();
//        void collection();
        void delete();
    }
}
