package com.qike.feiyunlu.tv.presentation.model.room;

import com.qike.feiyunlu.tv.module.network.BazaarGetDao;
import com.qike.feiyunlu.tv.module.network.Paths;
import com.qike.feiyunlu.tv.presentation.model.BaseCallbackBiz;
import com.qike.feiyunlu.tv.presentation.model.dto.RoomDto;
import com.qike.feiyunlu.tv.presentation.presenter.BaseCallbackPresenter;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.BaseLoadListener;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.IDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support.Result;

/**
 * Created by cherish on 2016/3/9.
 */
public class RoomPresenter{

    private BaseCallbackBiz mCallBack;
    private BazaarGetDao<RoomDto> mGetInfoDto;



    public RoomPresenter(){

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
                    callbackPresenter.callbackResult(mGetInfoDto.getData());
                }
            }
        });
        mGetInfoDto.putParams(Paths.ACTION_PARAM, Paths.ROOM_ACT_GETINFO);
        mGetInfoDto.putParams(Paths.PARAM_USER_ID,userid);
        mGetInfoDto.putParams(Paths.PARAM_USER_VERIFY,verifycode);

        mGetInfoDto.asyncDoAPI();

    }



    public void getOpenRoom(String userid,String verifycode, final BaseCallbackPresenter callbackPresenter){

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
                    callbackPresenter.callbackResult(mGetInfoDto.getData());
                }
            }
        });
        mGetInfoDto.putParams(Paths.ACTION_PARAM, Paths.ROOM_ACT_OPEN);
        mGetInfoDto.putParams(Paths.PARAM_USER_ID,userid);
        mGetInfoDto.putParams(Paths.PARAM_USER_VERIFY,verifycode);

        mGetInfoDto.asyncDoAPI();

    }

    private BazaarGetDao<String> mCloseDto;

    public void getCloseRoom(String userid,String verifycode, final BaseCallbackPresenter callbackPresenter){

        mCloseDto = new BazaarGetDao<String>( Paths.BASEPATH + Paths.ROOM_URL, String.class, BazaarGetDao.ARRAY_DATA_CHUNK);

        mCloseDto.setNoCache();

        mCloseDto.registerListener(new BaseLoadListener() {

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
                    callbackPresenter.callbackResult(mCloseDto.getData());
                }
            }
        });
        mCloseDto.putParams(Paths.ACTION_PARAM, Paths.ROOM_ACT_CLOSE);
        mCloseDto.putParams(Paths.PARAM_USER_ID,userid);
        mCloseDto.putParams(Paths.PARAM_USER_VERIFY,verifycode);

        mCloseDto.asyncDoAPI();

    }

}
