package com.qike.feiyunlu.tv.presentation.model.room;

import com.qike.feiyunlu.tv.module.network.BazaarGetDao;
import com.qike.feiyunlu.tv.module.network.Paths;
import com.qike.feiyunlu.tv.presentation.model.BaseCallbackBiz;
import com.qike.feiyunlu.tv.presentation.model.dto.RoomDto;
import com.qike.feiyunlu.tv.presentation.model.dto.RoomSettingDto;
import com.qike.feiyunlu.tv.presentation.presenter.BaseCallbackPresenter;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.BaseLoadListener;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.IDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support.Result;

/**
 * Created by cherish on 2016/3/9.
 */
public class RoomBiz {

    private BazaarGetDao<RoomDto> mGetInfoDto;

    private BazaarGetDao<RoomDto> mOpenRoomDto;

    private BazaarGetDao<String> mCloseRoomDto;

    public RoomBiz(){

    }


    public void getRoomInfo(String userid,String verifycode, final BaseCallbackPresenter callbackPresenter){

        mGetInfoDto = new BazaarGetDao<RoomDto>( Paths.BASEPATH + Paths.ROOM_URL, RoomDto.class, BazaarGetDao.ARRAY_DATA_CHUNK);

        mGetInfoDto.setNoCache();

        mGetInfoDto.registerListener(new BaseLoadListener() {

            @Override
            public void onError(Result result) {
                super.onError(result);
                if (callbackPresenter != null) {
                    callbackPresenter.onErrer(result.getCode(), result.getErrmsg());
                }
            }

            @Override
            public void onComplete(IDao.ResultType resultType) {
                super.onComplete(resultType);
                if (callbackPresenter != null) {
                    callbackPresenter.callbackResult(mGetInfoDto.getmData());
                }
            }
        });
        mGetInfoDto.putParams(Paths.ACTION_PARAM, Paths.ROOM_ACT_GETINFO);
        mGetInfoDto.putParams(Paths.PARAM_USER_ID,userid);
        mGetInfoDto.putParams(Paths.PARAM_USER_VERIFY,verifycode);

        mGetInfoDto.asyncDoAPI();

    }



    public void getOpenRoom(String userid,String verifycode, final BaseCallbackPresenter callbackPresenter){

        mOpenRoomDto = new BazaarGetDao<RoomDto>( Paths.BASEPATH + Paths.ROOM_URL, RoomDto.class, BazaarGetDao.ARRAY_DATA_CHUNK);

        mOpenRoomDto.setNoCache();

        mOpenRoomDto.registerListener(new BaseLoadListener() {

            @Override
            public void onError(Result result) {
                super.onError(result);
                if (callbackPresenter != null) {
                    callbackPresenter.onErrer(result.getCode(), result.getErrmsg());
                }
            }

            @Override
            public void onComplete(IDao.ResultType resultType) {
                super.onComplete(resultType);
                if (callbackPresenter != null) {

                    callbackPresenter.callbackResult(mOpenRoomDto.getmData());
                }
            }
        });
        mOpenRoomDto.putParams(Paths.ACTION_PARAM, Paths.ROOM_ACT_OPEN);
        mOpenRoomDto.putParams(Paths.PARAM_USER_ID,userid);
        mOpenRoomDto.putParams(Paths.PARAM_USER_VERIFY,verifycode);

        mOpenRoomDto.asyncDoAPI();

    }



    public void getCloseRoom(String userid,String verifycode, final BaseCallbackPresenter callbackPresenter){

        mCloseRoomDto = new BazaarGetDao<String>( Paths.BASEPATH + Paths.ROOM_URL, String.class, BazaarGetDao.ARRAY_DATA_CHUNK);

        mCloseRoomDto.setNoCache();

        mCloseRoomDto.registerListener(new BaseLoadListener() {

            @Override
            public void onError(Result result) {
                super.onError(result);
                if (callbackPresenter != null) {
                    callbackPresenter.onErrer(result.getCode(), result.getErrmsg());
                }
            }

            @Override
            public void onComplete(IDao.ResultType resultType) {
                super.onComplete(resultType);
                if (callbackPresenter != null) {
                    callbackPresenter.callbackResult(mCloseRoomDto.getmData());
                }
            }
        });
        mCloseRoomDto.putParams(Paths.ACTION_PARAM, Paths.ROOM_ACT_CLOSE);
        mCloseRoomDto.putParams(Paths.PARAM_USER_ID, userid);
        mCloseRoomDto.putParams(Paths.PARAM_USER_VERIFY, verifycode);

        mCloseRoomDto.asyncDoAPI();

    }


    private BazaarGetDao<RoomSettingDto> roomSettingDto;
    public void setRoomSetting(  String userid,String verifycode,String roomName, String gameName,String intro ,final BaseCallbackBiz callbackBiz){

        roomSettingDto = new BazaarGetDao<RoomSettingDto>(Paths.BASEPATH+Paths.CHANGE_ROOM,RoomSettingDto.class,BazaarGetDao.ARRAY_DATA_CHUNK);

        roomSettingDto.setNoCache();

        roomSettingDto.putParams("room_user_id", userid);
        roomSettingDto.putParams("user_verify",verifycode);
        roomSettingDto.putParams("name",roomName);
        roomSettingDto.putParams("game_name",gameName);
        roomSettingDto.putParams("intro",intro);
        roomSettingDto.registerListener(new BaseLoadListener() {

            @Override
            public void onError(Result result) {
                super.onError(result);
                if (callbackBiz != null) {
                    callbackBiz.errerResult(result.getCode(), result.getErrmsg());
                }
            }

            @Override
            public void onComplete(IDao.ResultType resultType) {
                super.onComplete(resultType);
                if (callbackBiz != null) {
                    callbackBiz.dataResult(roomSettingDto.getmData());
                }
            }
        });

    }


}
