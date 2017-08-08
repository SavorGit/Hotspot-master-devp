package com.common.api.utils;

public class StringKit {

	/**
	 * byte[]数组转换为16进制的字符串。
	 * @param data
	 *            要转换的字节数组。
	 * @return 转换后的结果。
	 */
	public static final String bytesToHexString(byte[] data) {
		StringBuilder valueHex = new StringBuilder();
		for (int i = 0, tmp; i < data.length; i++) {
			tmp = data[i] & 0xff;
			if (tmp < 16) {
				valueHex.append(0);
			}
			valueHex.append(Integer.toHexString(tmp));
		}
		return valueHex.toString();
	}
	
	/**
	 * 16进制表示的字符串转换为字节数组。
	 * 
	 * @param hexString
	 *            16进制表示的字符串
	 * @return byte[] 字节数组
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		char[] hexChars = hexString.toCharArray();
		int length = hexString.length();
		byte[] d = new byte[length >>> 1];
		for (int n = 0; n < length; n += 2) {
			String item = new String(hexChars, n, 2);
			// 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
			d[n >>> 1] = (byte) Integer.parseInt(item, 16);
		}
		return d;
	}
}
