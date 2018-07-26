package com.android.baosteel.lan.basebusiness.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 * 
 */
public class MD5 {
	public static String getMD5(String strSource) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(strSource.getBytes());
			byte[] m = md5.digest();
			return ByteAction.toHex(m).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}
}
