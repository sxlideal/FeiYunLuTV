package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.utils;

import java.util.UUID;

public class SessionUtils {

    public static String getSession(){
	return UUID.randomUUID().toString();
    }
}
