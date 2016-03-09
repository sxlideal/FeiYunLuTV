package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface;



/**
 * 
 *<p>数据请求和处理业务接口</p><br/>
 *<p>数据请求方法和注册解绑监听器方法，以及数据请求参数配置方法</p>
 * @since 1.0.0
 * @author bigpie
 */
public interface IDao<T> {

    
    
    public void asyncDoAPI();
    
    public void taskCancel();

    public void needVerifyKey(boolean bool);
    
    public void needDecodeResponse(boolean bool);
    
    public String decodeResponse(byte[] bytes) throws Throwable;
    
    public void setCacheTTL(long ttl);
    
    public void isDontLoadFromCache(boolean bool);
    
    public void setNoCache();
    
    public void registerListener(BaseLoadListener listener);

    public void unRegisterListener(BaseLoadListener listener);
    
    public void setRetryCount(int retryCount);
    
    
    public enum ResultType{
	SUCCESS, EMPTY, OTHERS, 
    }
    
    public enum ResultError{
   	PARSEERROR, DECODEERROR, SERVERERROR, OTHERS
       }
    
}






