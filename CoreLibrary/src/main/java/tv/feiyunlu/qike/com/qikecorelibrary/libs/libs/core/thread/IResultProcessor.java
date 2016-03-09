package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread;

import java.util.HashMap;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;


/**
 * 
 *<p>task的doInBackGround回调中调用</p><br/>
 *<p>回调结果的异步处理，解决byte数组解析耗时问题</p>
 * @since 1.0.0
 * @author bigpie
 */
public interface IResultProcessor {

    public void onDoInBackgroundProcess(HttpActionProxy action, HashMap<String, String> headers);
}
