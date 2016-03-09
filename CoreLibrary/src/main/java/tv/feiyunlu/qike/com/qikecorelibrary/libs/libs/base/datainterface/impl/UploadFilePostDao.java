package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;


public class UploadFilePostDao extends AbstractPostDao {


    public UploadFilePostDao(String url) {
	super(url);
	
	if (mCacheLoader == null) {
	    mCacheLoader = buildCacheLoader();
	}
    }

    
    
    public void putFilePath(String key, String filePath){
	putParams(key, filePath);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onDoInBackgroundProcess(HttpActionProxy action,HashMap<String, String> headers) {
	
	try {
	    content = new String(action.getBytes(), "utf-8");
        } catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
        }
    }


    public String getItem(){
	if(TextUtils.isEmpty(content)){
	    return "";
	}
	return content;
    }



    @Override
    protected CacheLoad buildCacheLoader() {
	return null;
    }

}
