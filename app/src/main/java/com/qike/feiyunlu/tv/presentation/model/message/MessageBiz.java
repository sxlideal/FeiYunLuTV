package com.qike.feiyunlu.tv.presentation.model.message;

import com.qike.feiyunlu.tv.module.network.BazaarGetDao;
import com.qike.feiyunlu.tv.module.network.Paths;
import com.qike.feiyunlu.tv.presentation.model.BaseCallbackBiz;
import com.qike.feiyunlu.tv.presentation.model.dto.User;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.BaseLoadListener;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.IDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support.Result;

/**
 * Created by cherish on 2016/3/16.
 */
public class MessageBiz {

    private BazaarGetDao<User> mDao;

    private BazaarGetDao<String> banDao;

    public MessageBiz(){
        mDao = new BazaarGetDao<User>(Paths.BASEPATH + Paths.SYSTEM_INFO, User.class, BazaarGetDao.ARRAY_DATA );

    }

    public void banUser(){

//        banDao = new BazaarGetDao<String>(Paths.BASEPATH+)

    }




    public void getMessage( String userid , final BaseCallbackBiz callbackBiz){
        mDao.setNoCache();
        mDao.putParams(Paths.PARAM_USER_ID, userid);
        mDao.registerListener(new BaseLoadListener() {

            @Override
            public void onError(Result result) {

                if (callbackBiz != null) {
                    callbackBiz.errerResult(result.getCode(),result.getErrmsg());
                }

            }


            @Override
            public void onComplete(IDao.ResultType resultType) {
                if( callbackBiz != null ){
                    callbackBiz.dataResult(mDao.getmData());
                }
            }
        });

        mDao.asyncDoAPI();


    }


}
