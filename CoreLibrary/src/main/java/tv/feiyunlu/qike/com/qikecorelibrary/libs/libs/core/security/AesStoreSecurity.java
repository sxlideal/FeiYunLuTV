/*
 * 文件名：AesStoreSecurity.java
 * 描述：AES加密
 * 版本：v1.0.0
 * 日期：2014-3-21
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *<p>AES加密</p><br/>
 *<p>AES加密</p>
 * @since 1.0.0
 * @author cxd
 */
public class AesStoreSecurity extends StoreSecurity {

	public byte[] encrypt(byte[] buff) throws Exception {
		IvParameterSpec zeroIv = new IvParameterSpec("0102030405060708".getBytes());
		SecretKeySpec key = new SecretKeySpec("craigdvsevendays".getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(buff);
		return encryptedData;

	}

	public byte[] decrypt(byte[] buff) throws Exception {

		IvParameterSpec zeroIv = new IvParameterSpec("0102030405060708".getBytes());
		SecretKeySpec key = new SecretKeySpec("craigdvsevendays".getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte[] decryptedData = cipher.doFinal(buff);

		return decryptedData;
	}

}
