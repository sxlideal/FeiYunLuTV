package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface;


import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support.Result;

/**
 * 
 *<p>View层在业务层中的监听器，对应处理复杂业务</p><br/>
 *<p>拥有多种状态的回调，对应复杂业务，继承自{@link IListener}</p>
 * @since 1.0.0
 * @author bigpie
 */
public interface ILoadListener {

    public void onPrepare();

    public void onError(Result result);

    public void onComplete(IDao.ResultType resultType);

    public void onCancel();
    
    public void onProgress(long progress, long len);
    
    
   

}
