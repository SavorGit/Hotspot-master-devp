package com.savor.savorphone.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.core.Session;

public class Validator {
	
	static{
//		System.loadLibrary("lashou_validators");
		System.loadLibrary("tuanche_validators");
	}

	
	public static String stid = "";

	public static boolean checkEmail(String email) {
		Pattern pattern = Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static String timeFormat(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA);
		TimeZone timezone = TimeZone.getTimeZone("GMT+08:00");
		sdf.setTimeZone(timezone);
		String date = sdf.format(Double.parseDouble(time + "000")); // 将日期时间格式化
		return date;
	}

	public static String timeFormat1(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA);
		TimeZone timezone = TimeZone.getTimeZone("GMT+08:00");
		sdf.setTimeZone(timezone);
		String date = sdf.format(Double.parseDouble(time + "000")); // 将日期时间格式化
		return date;
	}

	public static String timeFormat2(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.CHINA);
		TimeZone timezone = TimeZone.getTimeZone("GMT+08:00");
		sdf.setTimeZone(timezone);
		String date = sdf.format(Double.parseDouble(time)); // 将日期时间格式化
		return date;
	}

	public static String restTime(String timeStr) {
		int time = Integer.parseInt(timeStr);
		String t;
		int day = 24 * 60 * 60;
		int hour = 60 * 60;
		int minute = 60;
		int d, h, m, s;
		d = time / day;
		h = (time - day * d) / hour;
		m = (time - (day * d + hour * h)) / minute;
		s = time - ((d * day) + (h * hour) + (m * minute));
		if (d == 0) {
			t = (h > 9 ? "" + h : "0" + h) + "时" + (m > 9 ? "" + m : "0" + m) + "分" + (s > 9 ? "" + s : "0" + s) + "秒";
		} else if (d == 0 && h == 0) {
			// t = h + "时" + m + "分" + s + "秒";
			t = (h > 9 ? "" + h : "0" + h) + "时" + (m > 9 ? "" + m : "0" + m) + "分" + (s > 9 ? "" + s : "0" + s) + "秒";
		} else if (d == 0 && h == 0 && m == 0) {
			// t = h + "时" + m + "分" + s + "秒";
			t = (h > 9 ? "" + h : "0" + h) + "时" + (m > 9 ? "" + m : "0" + m) + "分" + (s > 9 ? "" + s : "0" + s) + "秒";
		} else if (d >= 3) {
			t = "剩余3天以上";
		} else {
			// t = d + "天" + h + "时" + m + "分";// +s
			t = d + "天" + (h > 9 ? "" + h : "0" + h) + "时" + (m > 9 ? "" + m : "0" + m) + "分";
		}
		return t;
	}

	public static boolean codeFormat(String code, String i) {
		String regEx;
		if ("1".equals(i)) {// 纯数字格式
			regEx = "^[0-9]+$";
		} else {// 字母数字格式
			regEx = "^[0-9]*[A-Za-z]{1,}[0-9]{1,}[A-Za-z0-9]*$";
		}
		return code.matches(regEx);
	}

	public static boolean complateTime(Object lastTime, Object nowTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String last = sdf.format(lastTime);
		String now = sdf.format(nowTime);
		String[] lastArray = last.split("-");
		String[] nowArray = now.split("-");
		if (Integer.parseInt(lastArray[0]) < Integer.parseInt(nowArray[0])) {
			return true;
		} else if (Integer.parseInt(lastArray[0]) == Integer.parseInt(nowArray[0]) && Integer.parseInt(lastArray[1]) < Integer.parseInt(nowArray[1])) {
			return true;
		} else if (Integer.parseInt(lastArray[0]) == Integer.parseInt(nowArray[0]) && Integer.parseInt(lastArray[1]) == Integer.parseInt(nowArray[1])
				&& Integer.parseInt(lastArray[2]) < Integer.parseInt(nowArray[2])) {
			return true;
		} else {
			return false;
		}
	}
	
//	/**
//	 * 根据规则生成请求参数 sign 值
//	 * @param mapParams 请求参数的键值对
//	 * @param paramJson 请求参数json串
//	 * @param time 时间
//	 * @return
//	 */
//	public static String getSignStr(HashMap<String, Object> mapParams, String paramJson, String time)
//	{
//		List<String> keys = new ArrayList<String>();
//		for(Map.Entry<String, Object> entry : mapParams.entrySet())
//		{
//			String key = entry.getKey();
//			keys.add(key);
//
//		}
//		Collections.sort(keys);
//		StringBuffer signBuffer = new StringBuffer();
//		for (String key : keys)
//		{
//			signBuffer.append(key + "=");
//			Object val=mapParams.get(key);
//			if(val==null){
//				val="";
//			}
//			signBuffer.append(val + "|");
//		}
//		String sign = signBuffer.substring(0, signBuffer.length()-1);
//		String clientId = Session.get(EtagoClientApplication.getContext()).getDeviceid();
//		if (TextUtils.isEmpty(clientId)) {
//			clientId = STIDUtil.getDeviceId(EtagoClientApplication.getContext());
//		}
//		LogUtils.i("Validator+sign before-->" + sign);
//		sign = signNativeMethod(sign, time, clientId);//调用本地方法获取sign
//		LogUtils.i("Validator+sign after-->" + sign);
//		return sign;
//	}
//
//
//
//
//	//----本地方法------
//
//
//	private native static String signNativeMethod(String sign, String time, String clientId);
//
//	/**
//	 * 安全码的签名
//	 * @param txt
//	 * @return
//	 */
//	public native static String getSafeSign(String txt);
//
//	/**
//	 * 支付密码加密方式的 解密方法
//	 * @param txt
//	 * @return
//	 */
//	public native static String decodeSafeSign(String txt);
}
