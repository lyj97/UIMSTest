package com.lu.mydemo.Utils.Fragement;

/**
 * 创建时间: 2019/12/19 15:39 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public enum FragmentLabels {

    ALL_FUNCTION_FRAGMENT(1),
    SCORE_FRAGMENT(2),
    YKT_GRAGNEMT(3);

    FragmentLabels(int code){
        this.code = code;
    }

    int code;

    public int getCode() {
        return code;
    }

}
