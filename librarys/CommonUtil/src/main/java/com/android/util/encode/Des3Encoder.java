package com.android.util.encode;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.text.TextUtils;

/**
 * 3DES加密
 * 
 * @note 3DES的加密密钥长度要求是24个字节
 * 
 */
public class Des3Encoder {

	// 向量
	private final static String IV = "01234567";
	// 加解密统一使用的编码方式
	private final static String ENCODING = "utf-8";

	public static void main(String[] args) throws Exception {

		test("中国BB车险");

	}

	private static void test(String src) throws Exception {
		String KEY = "bbchexiabbchexiabbchexia";
		byte[] encryptBytes = encrypt(KEY, src.getBytes(ENCODING));
		System.out.println("加密后的字节数=" + encryptBytes.length);
		byte[] decryptBytes = decrypt(KEY, encryptBytes);
		System.out.println("解密后的字节数=" + decryptBytes.length);
		System.out.println(new String(decryptBytes, ENCODING));
	}

	/**
	 * 加密
	 * 
	 * @param key
	 *            密钥
	 * @param srcBytes
	 *            原始数据
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(String key, byte[] srcBytes) throws Exception {
		if (TextUtils.isEmpty(key) || null == srcBytes || srcBytes.length == 0) {
			return srcBytes;
		}
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);

		Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(IV.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
		byte[] encryptData = cipher.doFinal(srcBytes);

		return encryptData;
	}

	/**
	 * 解密
	 * 
	 * @param key
	 *            解密密钥
	 * @param encodedBytes
	 *            加密后的字节
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(String key, byte[] encodedBytes)
			throws Exception {
		if (TextUtils.isEmpty(key) || null == encodedBytes
				|| encodedBytes.length == 0) {
			return encodedBytes;
		}

		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(IV.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

		byte[] decryptData = cipher.doFinal(encodedBytes);

		return decryptData;
	}
}
