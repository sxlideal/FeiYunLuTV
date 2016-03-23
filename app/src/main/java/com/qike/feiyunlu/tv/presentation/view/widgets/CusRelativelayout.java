package com.qike.feiyunlu.tv.presentation.view.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by cherish on 2016/3/21.
 */
public class CusRelativelayout extends RelativeLayout {
    public CusRelativelayout(Context context) {
        super(context);
    }

    public CusRelativelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusRelativelayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CusRelativelayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

/**
 * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
 */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);


//        measureChildren(widthMeasureSpec, heightMeasureSpec);


//        measureChild(v,heightMeasureSpec,heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
