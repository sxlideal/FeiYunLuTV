package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface;


import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support.Result;

public class BaseLoadListener implements ILoadListener {

    @Override
    public void onPrepare() {

    }


    @Override
    public void onCancel() {

    }

    @Override
    public void onProgress(long progress, long len) {

    }


    @Override
    public void onError(Result result) {
	
    }


    @Override
    public void onComplete(IDao.ResultType resultType) {
	
    }

}
