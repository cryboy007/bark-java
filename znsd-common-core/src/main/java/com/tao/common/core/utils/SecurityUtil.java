package com.tao.common.core.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.tao.common.core.common.exception.ExceptionWapper;

public class SecurityUtil {
	/**
	 * MD5加密，并按指定编码返回字符串
	 * 
	 * @param input
	 * @param encoding
	 * @return
	 */
	public final static String getEncryptMD5(String input, String encoding) {
		byte[] bytes = getEncryptMD5Bytes(input);
		return new String(bytes, Charset.forName(encoding));
	}

	/**
	 * MD5加密，并按BASE64编码返回字符串
	 * 
	 * @param input
	 * @return
	 */
	public final static String getEncryptMD5(String input) {
		byte[] bytes = getEncryptMD5Bytes(input);
		return encryptBASE64(bytes);
	}

	/**
	 * MD5加密，并按16进制编码返回字符串
	 * 
	 * @param input
	 * @return
	 */
	public final static String getEncryptMD5HexEncoding(String input) {
		byte[] bytes = getEncryptMD5Bytes(input);

		// char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
		// '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		//
		// final int len = bytes.length;
		// final char[] out = new char[len << 1];
		// for (int i = 0, j = 0; i < len; i++) {
		// out[j++] = hexDigits[(0xF0 & bytes[i]) >>> 4];
		// out[j++] = hexDigits[0x0F & bytes[i]];
		// }
		//
		// return new String(out);

		return byte2hex(bytes);
	}

	public static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}

	/**
	 * MD5加密
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] getEncryptMD5Bytes(String input) {
		byte[] inputBytes = null;
		try {
			inputBytes = input.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw ExceptionWapper.createBapException(e);
		}

		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw ExceptionWapper.createBapException(e);
		}

		messageDigest.update(inputBytes);
		return messageDigest.digest();
	}

	private static String encryptBASE64(byte[] key) {
		return Base64.encode(key);
	}

}
