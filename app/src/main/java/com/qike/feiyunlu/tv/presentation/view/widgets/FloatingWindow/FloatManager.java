package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow;

import android.content.Context;

import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow.IconFloatWindow;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow.MenuFloatWindow;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow.MsgFloatWindow;


/**
 * Created by cherish on 2016/2/23.
 */
public class FloatManager {

    private static FloatManager INSTANCE;

    private IconFloatWindow mIconWindow;
    private MenuFloatWindow mMenuWindow;
    private MsgFloatWindow mMsgWindow;

    public static FloatManager getINSTANCE( Context context){

        if( INSTANCE == null ){
            synchronized (FloatManager.class){
                if ( INSTANCE == null ){
                    INSTANCE = new FloatManager(context);
                }
            }
        }
        return INSTANCE;

    }

    private FloatManager(Context context){

        mIconWindow = new IconFloatWindow(context);
        mMenuWindow = new MenuFloatWindow(context);
        mMsgWindow = new MsgFloatWindow(context);

    }
    public void openMenuWindow(){
        if( mIconWindow.isShowing()){
            int[] location = mIconWindow.getLocation();
            mMenuWindow.initLocation(location[0],location[1]);
            mMenuWindow.show();
        }
    }

    public void closeMenuWindow(){
        mMenuWindow.close();
    }

    public void openIconAtLoctionWindow(){
        if( mMenuWindow.isShowing()){
            int[] location = mMenuWindow.getLocation();
            mIconWindow.initLocation(location[0],location[1]);
            mIconWindow.show();
        }
    }


    public void openIconWindow(){
        mIconWindow.show();
    }

    public void closeIconWindow(){
        mIconWindow.close();
    }

    public void openMsgWindow(){
        mMsgWindow.show();
    }

    public void closeMsgWindow(){
        mMsgWindow.close();
    }

    public boolean isOpen( ){
        return mMsgWindow.isShowing();
    }

    public void closeAllWindows(){
        mMsgWindow.close();
        mIconWindow.close();
        mMenuWindow.close();
    }

    public void showAllWindows(){
        mMsgWindow.show();
        mIconWindow.show();
        mMenuWindow.show();
    }

    public void destroy(){
        closeAllWindows();
        INSTANCE = null;
    }

    private static boolean isIconShow = false;
    private static boolean isMessShow = false;
    private static boolean isMenuShow = false;
    public void menuActivityCloseShowingWindow(){

        if(mIconWindow.isShowing()){
            isIconShow = true;
            mIconWindow.close();
        }
        if(mMenuWindow.isShowing()){
            isMenuShow = true;
            mMenuWindow.close();
        }
        if(mMsgWindow.isShowing()){
            isMessShow = true;
            mMsgWindow.close();
        }
    }


    public void menuAcitivityOpenWindow(){

        if( isIconShow ){
            isIconShow = false;
            mIconWindow.showAtLastLocation();
        }
        if( isMenuShow ){
            isMenuShow = false;
            mMenuWindow.showAtLastLocation();
        }
        if (isMessShow){
            isMessShow = false;
            mMsgWindow.showAtLastLocation();
        }

    }


}
