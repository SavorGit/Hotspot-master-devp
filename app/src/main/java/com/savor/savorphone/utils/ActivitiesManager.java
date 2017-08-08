package com.savor.savorphone.utils;

import android.app.Activity;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.ModelPic;
import com.savor.savorphone.bean.VideoInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * activity堆栈管理
 * 
 * 
 */
public class ActivitiesManager {

	private static final String TAG = "ActivitiesManager";
	// activity栈
	private static Stack<Activity> mActivityStack;
	private static ActivitiesManager mActivitiesManager;
	private static Class mProjectionActivity;
	private List<MediaInfo> mModelPicList;
	private VideoInfo mVideoInfo;
	private int picPosition;


	private ActivitiesManager() {

	}

	public static ActivitiesManager getInstance() {
		if (null == mActivitiesManager) {
			mActivitiesManager = new ActivitiesManager();
			if (null == mActivityStack) {
				mActivityStack = new Stack<Activity>();
			}
		}
		return mActivitiesManager;
	}

	public int stackSize() {
		return mActivityStack.size();
	}

	/**
	 * 获取当前activity
	 * 
	 * @return 当前activity
	 */
	public Activity getCurrentActivity() {
		Activity activity = null;

		try {
			activity = mActivityStack.lastElement();
		} catch (Exception e) {
			return null;
		}

		return activity;
	}

	/**
	 * 出栈
	 * 暂时没有用到 2014-06-05
	 */
	public void popActivity() {
		Activity activity = mActivityStack.lastElement();
		if (null != activity) {
			LogUtils.i(TAG+"popActivity-->" + activity.getClass().getSimpleName());
			activity.finish();
			mActivityStack.remove(activity);
			activity = null;
		}
	}

	/**
	 * 出栈
	 * 
	 * @param activity
	 */
	public void popActivity(Activity activity) {
		if (null != activity) {
			LogUtils.i("popActivity-->" + activity.getClass().getSimpleName());
//			activity.finish();
			mActivityStack.remove(activity);
			activity = null;
		}
	}

	/**
	 * activity入栈
	 * 
	 * @param activity
	 */
	public void pushActivity(Activity activity) {
//		if(activity instanceof MainActivity) {
//			popAllActivities();
//		}
		mActivityStack.add(activity);
		LogUtils.i("pushActivity-->" + activity.getClass().getSimpleName());
	}

	/**
	 * 所有activity出栈
	 */
	public void popAllActivities() {
		while (!mActivityStack.isEmpty()) {
			Activity activity = getCurrentActivity();
			if (null == activity) {
				break;
			}
			activity.finish();
			popActivity(activity);
		}
	}

	/**
	 * 将上层Activity出栈，直到指定的Activity
	 */
	public void popUntilSpecialActivity(Class<?> cls) {
		while (!mActivityStack.isEmpty()) {
			Activity activity = getCurrentActivity();
			if (null == activity || activity.getClass().equals(cls)) {
				break;
			}
			activity.finish();
			popActivity(activity);
		}
	}
	
	/**
	 * 指定的activity出栈
	 */
	public void popSpecialActivity(Class<?> cls) {
//		for (Activity activity : mActivityStack) {
//			if (activity.getClass().equals(cls)) {
//				activity.finish();
//				mActivityStack.remove(activity);
//				activity = null;
//			}
//		}
		try {
			Iterator<Activity> iterator = mActivityStack.iterator();
			Activity activity = null;
			while (iterator.hasNext()) {
				activity = iterator.next();
				if (activity.getClass().equals(cls)) {
					activity.finish();
					iterator.remove();
					activity = null;
				}
			}
		} catch (Exception e) {
			
		}
	}

	/**
	 * 遍历目前栈中的activity
	 */
	public void peekActivity() {
		for (Activity activity : mActivityStack) {
			if (null == activity) {
				break;
			}
			LogUtils.i("peekActivity()-->"
					+ activity.getClass().getSimpleName());
		}
	}
	
	public boolean contains(Class<? extends Activity> clazz) {
		try {
			Iterator<Activity> iterator = mActivityStack.iterator();
			Activity activity = null;
			while (iterator.hasNext()) {
				activity = iterator.next();
				if (activity.getClass().equals(clazz)) {
					return true;
				}
			}
		} catch (Exception e) {

		}

		return false;
	}
	
	/**
	 * 调用此方法前请务必确认activity在堆栈中，否则将死循环
	 * @param cls
	 * @return
	 */
	public Activity getSpecialActivity(Class<?> cls) {
		for (Activity activity:mActivityStack) {
			if (cls == activity.getClass()) {
				return activity;
			}
		}
		return null;
	}
	
	/**
	 * 跳转到首页并指定某个tab
	 * @param context
	 */
	public static void jumpToMainActivity(Activity context) {
//		if (ActivitiesManager.getInstance().contains(MainActivity.class)) {
//			ActivitiesManager.getInstance().popUntilSpecialActivity(MainActivity.class);
//
////			MainActivity activity = (MainActivity) ActivitiesManager.getInstance().getCurrentActivity();
////			if(activity!=null) {
////				activity.changeFragment(index);
////			}
//		} else {
//			Intent intent = new Intent(context, MainActivity.class);
//			context.startActivity(intent);
//
////			Session.get(context).setLastCheckedId(index);
//		}
//
//		context.finish();
	}
	
}
