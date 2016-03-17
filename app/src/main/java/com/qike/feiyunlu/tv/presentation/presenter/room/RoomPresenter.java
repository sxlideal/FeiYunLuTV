package com.qike.feiyunlu.tv.presentation.presenter.room;

import android.content.Context;

import com.qike.feiyunlu.tv.library.util.PreferencesUtils;
import com.qike.feiyunlu.tv.module.network.DLResultData;
import com.qike.feiyunlu.tv.presentation.model.BaseCallbackBiz;
import com.qike.feiyunlu.tv.presentation.model.dto.RoomDto;
import com.qike.feiyunlu.tv.presentation.model.room.RoomBiz;
import com.qike.feiyunlu.tv.presentation.presenter.BaseCallbackPresenter;
import com.qike.feiyunlu.tv.presentation.view.AnnounceActivity;

/**
 * Created by cherish on 2016/3/14.
 */
public class RoomPresenter {

    private RoomBiz mBiz;


    public RoomPresenter(){
        mBiz = new RoomBiz();
    }


    public void openRoom( String userid, String verifycode, BaseCallbackPresenter callbackPresenter){
        mBiz.getOpenRoom(userid,verifycode,callbackPresenter);
    }

    public void closeRoom( String userid, String verifycode, BaseCallbackPresenter callbackPresenter){
        mBiz.getCloseRoom(userid, verifycode, callbackPresenter);
    }

    public void getInfoRoom( String userid, String verifycode, BaseCallbackPresenter callbackPresenter){
        mBiz.getRoomInfo(userid, verifycode, callbackPresenter);
    }

    public void getRoomUrl(final String userid,final String verifycode, final BaseCallbackPresenter callbackPresenter){

        mBiz.getOpenRoom(userid, verifycode, new BaseCallbackPresenter() {
            @Override
            public boolean callbackResult(Object obj) {

                if (obj != null && obj instanceof DLResultData) {

                    final DLResultData dlResultData = (DLResultData) obj;
                    if (dlResultData.getCode() == 200) {
                        RoomDto dto = (RoomDto) dlResultData.getData();
                        callbackPresenter.callbackResult(dto.getPublish_url());
                    } else {
                        closeRoom(userid, verifycode, new BaseCallbackPresenter() {
                            @Override
                            public boolean callbackResult(Object obj) {

                                if (obj != null && obj instanceof DLResultData) {

                                    openRoom(userid, verifycode, new BaseCallbackPresenter() {
                                        @Override
                                        public boolean callbackResult(Object obj) {
                                            DLResultData dlResultData = (DLResultData) obj;
                                            RoomDto dto = (RoomDto) dlResultData.getData();
                                            callbackPresenter.callbackResult(dto.getPublish_url());
                                            return false;
                                        }

                                        @Override
                                        public void onErrer(int code, String msg) {
                                        }
                                    });
                                }
                                return false;
                            }

                            @Override
                            public void onErrer(int code, String msg) {
                                callbackPresenter.onErrer(code, msg);
                            }
                        });
                    }
                }
                return false;
            }

            @Override
            public void onErrer(int code, String msg) {
                callbackPresenter.onErrer(code, msg);
            }
        });

    }


    public void setRoomSetting(Context context, String userid,String verifycode,String roomName, String gameName,final BaseCallbackPresenter callback){



        String intro = PreferencesUtils.loadPrefString(context, AnnounceActivity.ANNOUNCE,"");
        mBiz.setRoomSetting(userid, verifycode, roomName, gameName, intro, new BaseCallbackBiz() {
            @Override
            public void dataResult(Object obj) {
                callback.callbackResult(obj);
            }

            @Override
            public void errerResult(int code, String msg) {
                callback.onErrer(code, msg);
            }
        });

    }

    public void setRoomIntro( String userid,String verifycode,String roomName, String gameName,String intro ,final BaseCallbackPresenter callback){


            mBiz.setRoomSetting(userid, verifycode, roomName, gameName, intro, new BaseCallbackBiz() {
                @Override
                public void dataResult(Object obj) {
                    callback.callbackResult(obj);
                }

                @Override
                public void errerResult(int code, String msg) {
                    callback.onErrer(code, msg);
                }
            });

    }


}
