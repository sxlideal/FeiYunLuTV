package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.inter;

/**
 * Created by cherish on 2016/2/22.
 */
public interface FloatWindow {

    public void show();

    public void close();

    public boolean isShowing();

    public void initLocation(int left, int top);

    public void setParams(Object... objects);

    public void setOnConnectMoveListener(IMoveListener moveListener);


}
