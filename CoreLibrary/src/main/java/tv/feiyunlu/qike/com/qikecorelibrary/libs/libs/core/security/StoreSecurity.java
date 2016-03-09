/*
 * 文件名：StoreSecurity.java
 * 描述：加密的抽象类
 * 版本：v1.0.0
 * 日期：2014-3-21
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.security;


/**
 *<p>加密的抽象类</p><br/>
 *<p>继承此类 重写方法 实现加密功能</p>
 * @since 1.0.0
 * @author cxd
 */
public abstract class StoreSecurity {

	/**
	 * 加密信息
	 * 
	 * @param buff
	 *            待加密数据
	 * @return byte[] 已加密数据
	 */
	public abstract byte[] encrypt(byte[] buff) throws Exception;

	/**
	 * 解密信息
	 * 
	 * @param buff
	 *            待解密数据
	 * @return byte[] 已解密数据
	 */
	public abstract byte[] decrypt(byte[] buff) throws Exception;

}
