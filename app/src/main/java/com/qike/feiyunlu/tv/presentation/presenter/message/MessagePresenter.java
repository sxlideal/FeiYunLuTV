package com.qike.feiyunlu.tv.presentation.presenter.message;

import com.qike.feiyunlu.tv.presentation.model.BaseCallbackBiz;
import com.qike.feiyunlu.tv.presentation.model.message.MessageBiz;
import com.qike.feiyunlu.tv.presentation.presenter.BaseCallbackPresenter;

/**
 * Created by cherish on 2016/3/16.
 */
public class MessagePresenter {

    private MessageBiz mBiz;

    public MessagePresenter(){
        mBiz = new MessageBiz();

    }


    public void getMessage( String userid , final BaseCallbackPresenter callbackPresenter){

        mBiz.getMessage(userid, new BaseCallbackBiz() {
            @Override
            public void dataResult(Object obj) {
                if (callbackPresenter != null ){
                    callbackPresenter.callbackResult(obj);
                }
            }

            @Override
            public void errerResult(int code, String msg) {
                if (callbackPresenter != null){
                    callbackPresenter.onErrer(code,msg);
                }
            }
        });

    }

}
