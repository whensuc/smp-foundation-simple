package cn.com.dyg.work.utils;

import org.springframework.util.Base64Utils;

import java.security.InvalidKeyException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;




public class AESUtil {
private static final String IV_STRING = "16-Bytes--String";

	public static String encryptAES(String content, String key)
			{
			byte[] encryptedBytes=null;
			try {
				byte[] byteContent = content.getBytes("UTF-8");
				// 注意，为了能与 iOS 统一
				// 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
				byte[] enCodeFormat = key.getBytes();
				SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
				byte[] initParam = IV_STRING.getBytes();
				IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
				// 指定加密的算法、工作模式和填充方式
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
				encryptedBytes = cipher.doFinal(byteContent);
			} catch (Exception  e) {
				e.printStackTrace();
			}
			// 同样对加密后数据进行 base64 编码

			return Base64Utils.encodeToString(encryptedBytes);
		}

	public static String decryptAES(String content, String key)
			 {
			// base64 解码
			byte[] result=null;
			String decstr = null;
			try {
				byte[] encryptedBytes=null;
				encryptedBytes = Base64Utils.decodeFromString(content);
				byte[] enCodeFormat = key.getBytes();
				SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
				byte[] initParam = IV_STRING.getBytes();
				IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
				result = cipher.doFinal(encryptedBytes);
				decstr = new String(result, "UTF-8");
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return decstr;
		}
}
