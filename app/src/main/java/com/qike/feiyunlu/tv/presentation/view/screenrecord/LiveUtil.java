package com.qike.feiyunlu.tv.presentation.view.screenrecord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.WindowManager;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.presenter.Service.FloatService;

/**
 * Created by cherish on 2016/3/17.
 */
public class LiveUtil {



    public static LiveScreenDto chooseOrientation( int checkedId,LiveScreenDto mLiveDto){
        if (checkedId == R.id.horizontal_btn) {//横屏
            mLiveDto.setOrientation(0);
            if (mLiveDto.getHeight() > mLiveDto.getWidth()) {
                swap(mLiveDto);
            }

        } else if (checkedId == R.id.portrait_btn) {//竖屏
            mLiveDto.setOrientation(1);
            if (mLiveDto.getHeight() < mLiveDto.getWidth()) {
                swap(mLiveDto);
            }
        }
        return mLiveDto;
    }

    public static LiveScreenDto chooseSharpness(int checkedId, LiveScreenDto mLiveDto, Context context){

        switch (checkedId) {
            case R.id.normal_density:

                mLiveDto.setBitrate(500000);
                break;

            case R.id.high_density:
                mLiveDto.setBitrate(1000000);
                break;

            case R.id.super_density:
                mLiveDto.setBitrate(2000000);

                break;
        }
        /****************************************/
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        float f = ((float) point.y) / ((float) point.x);

        if (mLiveDto.getBitrate() == 1000000) {
            mLiveDto.setHeight(480);
        } else if (mLiveDto.getBitrate() == 2000000) {
            mLiveDto.setHeight(720);
        } else if (mLiveDto.getBitrate() == 500000) {
            mLiveDto.setHeight(360);
        }
        int width = (int) (f * ((float) mLiveDto.getHeight()));
        if (width % 4 != 0) {
            width -= width % 4;
        }
        mLiveDto.setWidth(width);

        if (mLiveDto.getOrientation() == 1 && mLiveDto.getWidth() > mLiveDto.getHeight()) {
            swap(mLiveDto);
        }

    return mLiveDto;


    }

    public static LiveScreenDto swap(LiveScreenDto mLiveDto) {
        int temp = mLiveDto.getHeight();
        mLiveDto.setHeight(mLiveDto.getWidth());
        mLiveDto.setWidth(temp);
        return mLiveDto;
    }


    public static void startRecord(int i,Context context,LiveScreenDto mLiveDto) {

        Intent intent = new Intent(context, FloatService.class);
        intent.putExtra("liveDto", mLiveDto);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startService(intent);
    }

}
