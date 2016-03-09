package com.qike.feiyunlu.tv.library.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 *<p>TODO(判断邮箱 手机 qq号是否合法)</p><br/>
 *<p>TODO (类的详细的功能描述)</p>
 * @since 1.0.0
 * @author cherish
 */
public class RegExpUtil {

	/**
	 * 
	 *<p>TODO(判断手机号码格式)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author cherish
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile){     
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");     
        Matcher m = p.matcher(mobile);     
        return m.matches();     
    } 
	
	
	public static boolean isCheckCode( String checkCode ){
		
		Pattern p = Pattern.compile("^\\d{3,8}$");
		Matcher m = p.matcher(checkCode);
		return m.matches();
		
	}
	
	/**
	 * 
	 *<p>TODO(判断email格式)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author cherish
	 * @param email
	 * @return
	 */
    public static boolean isEmail(String email){     
     String str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);     
        Matcher m = p.matcher(email);     
        return m.matches();     
    } 
    
	/**
	 * 
	 *<p>TODO(判断qq号格式)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author cherish
	 * @param email
	 * @return
	 */
    public static boolean isQQ(String email){     
        String str="^[1-9]\\d{4,10}$";
           Pattern p = Pattern.compile(str);     
           Matcher m = p.matcher(email);     
           return m.matches();     
       } 
    
    
}
