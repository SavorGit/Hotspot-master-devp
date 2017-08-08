package com.savor.savorphone.utils;

import android.app.Activity;
import android.content.Context;

import com.common.api.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;


/**
 * 统计记录工具类
 * 
 */
public class RecordUtils {
	/**
	 * 页面开始
	 * 
	 * @param tag
	 *            记录tag
	 */
	public static void onPageStart(Context context, String tag) {
		try {
			MobclickAgent.onPageStart(tag);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+tag, e);
		}
	}

	/**
	 * 页面结束
	 * 
	 * @param tag
	 *            记录tag
	 */
	public static void onPageEnd(Context context, String tag) {
		try {
			MobclickAgent.onPageEnd(tag);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+tag, e);
		}
	}
	/**
	 * 事件
	 * 
	 * @param context
	 *            页面context
	 * @param eventTag
	 *            事件tag 
	 */
	public static void onEvent(Context context, int eventTag) {
		try {
			MobclickAgent.onEvent(context, context.getString(eventTag));
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+eventTag, e);
		}
	}
	/**
	 * 使用这个方法要慎重----这个只在首页图标点击的时候起作用
	 * @param context
	 * @param eventTag
	 */
	public static void onEvent(Context context, String eventTag) {
		try {
			MobclickAgent.onEvent(context, eventTag);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+eventTag, e);
		}
	}
	
	public static void onEvent(Context context, int eventTag, String eventValue) {
		try {
			MobclickAgent.onEvent(context, context.getString(eventTag), eventValue);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+eventTag, e);
		}
	}


	/**
	 * 事件
	 * 
	 * @param context
	 *            页面context
	 * @param eventTag
	 *            事件tag
	 * @param map
	 *            事件对应键值对
	 */
	public static void onEvent(Context context, String eventTag, String eventLabel,
                               HashMap<String, String> map) {
		try {
			MobclickAgent.onEvent(context, eventTag, map);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+eventTag, e);
		}
	}
	
	/**
	 * 事件
	 * 
	 * @param context
	 *            页面context
	 * @param eventTag
	 *            事件tag
	 * @param map
	 *            事件对应键值对
	 */
	public static void onEvent(Context context, String eventTag,
                               HashMap<String, String> map) {
		try {
			MobclickAgent.onEvent(context, eventTag, map);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+eventTag, e);
		}
	}
	
	public static void onEvent(Context context, int eventTagInt, int eventLabelInt,
                               HashMap<String, String> map) {
		try {
			MobclickAgent.onEvent(context,context.getString(eventTagInt), map);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+context.getString(eventTagInt), e);
		}
	}
	/**
	 * onResume
	 * 
	 *            页面context
	 */
	public static void onResume(Activity activity) {
		try {
			MobclickAgent.onResume(activity);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+activity, e);
		}
	}

	/**
	 * onPause
	 * 
	 *            页面context
	 */
	public static void onPause(Activity activity) {
		try {
			MobclickAgent.onPause(activity);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+activity, e);
		}
	}
	
	/***
	 * onResume
	 * @param activity
	 */
	public static void onPageStartAndResume(Activity activity, Context context){
		try {
			RecordUtils.onPageStart(context,activity.getClass().getSimpleName());
			RecordUtils.onResume(activity);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+activity, e);
		}
	}
	public static void onPageEndAndPause(Activity activity, Context context){
		try {
			RecordUtils.onPageEnd(context,activity.getClass().getSimpleName());
			RecordUtils.onPause(activity);
		} catch (Exception e) {
			LogUtils.d("TalkingData:"+activity, e);
		}
	}

}
