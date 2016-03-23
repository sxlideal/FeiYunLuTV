package com.qike.feiyunlu.tv.presentation.view.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.joooonho.SelectableRoundedImageView;

/**
 * Created by cherish on 2016/3/22.
 */
public class CusImageView extends SelectableRoundedImageView {


    public CusImageView(Context context) {
        super(context);
    }

    public CusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

//        widthMeasureSpec = MeasureSpec.makeMeasureSpec( sizeHeight , MeasureSpec.UNSPECIFIED  );
//        measure(heightMeasureSpec,heightMeasureSpec);
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizeHeight,sizeHeight);


    }



}
