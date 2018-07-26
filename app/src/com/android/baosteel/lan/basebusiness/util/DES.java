package com.android.baosteel.lan.basebusiness.util;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {

	public static String KEY = "$&%@.!^~";

	private static byte[] iv = { 7, 0, 1, 2, 1, 4, 5, 5 };

	// public static String encryptDES(String encryptString, String encryptKey)
	// throws Exception {
	// IvParameterSpec zeroIv = new IvParameterSpec(iv);
	// SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
	// Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	// cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
	// byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
	// return Base64.encode(encryptedData);
	// }

	public static String decryptDES(String decryptString, String decryptKey) {
		if (TextUtils.isEmpty(decryptString)) {
			return null;
		}
		byte[] byteMi = Base64.decode(decryptString.getBytes(), Base64.DEFAULT);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte decryptedData[] = cipher.doFinal(byteMi);
			return new String(decryptedData);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static String decryptDES(String decryptString) {
		byte[] byteMi = Base64.decode(decryptString.getBytes(), Base64.DEFAULT);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "DES");
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte decryptedData[] = cipher.doFinal(byteMi);
			return new String(decryptedData);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
