package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http;

/**
 * Created by zhulingjun on 14-3-19.
 */
public interface HttpStack {
    /**
     *
     * @param request 请求对象，封装url、method、params参数
     * @return Response对象，封装result、error参数
     * @throws java.io.IOException
     */
    public <T> Response<T> performRequest(Request<T> request) throws Exception;
}
