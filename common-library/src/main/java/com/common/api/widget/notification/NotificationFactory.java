package com.common.api.widget.notification;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


/**
 * 通知工厂
 * @author hezd
 *
 */
public class NotificationFactory {
	
	// 取消Activity启动模式Flag
	public static final int NO_FLAG = -1;
	
	/**
	 * 创建普通通知
	 * @param context 上下文对象
	 * @param tiker 状态栏显示的文字
	 * @param iconId icon图标资源id
	 * @param title 通知标题
	 * @param content 通知内容
	 * @param notificationId 通知id，用于更新通知
	 * @param activityCalss 要跳转的activity的class
	 * @param startMode Activity启动模式,-1表示不设置。
	 * @param bundle 页面跳转时携带的数据，可以为null
	 * @return 返回创建的通知
	 */
	public static NormalNotification createNormalNotification(Context context,String tiker,int iconId ,String title,String content,int notificationId,int startMode,Class<? extends Activity> activityCalss,Bundle bundle) {
		return new NormalNotification(context, tiker,iconId, title, content,notificationId,startMode, activityCalss,bundle);
	}
	
	/**
	 * 创建进度条通知
	 * @param context 上下文对象
	 * @param tiker 状态栏显示内容
	 * @param smallIconId icon资源id
	 * @param title 通知标题
	 * @param content 通知内容
	 * @param notificationId 通知id，用于更新通知
	 * 
	 * @return  返回创建的通知
	 */
	public static INotification createProgressNotification(Context context,String tiker,int smallIconId,String title,String content,int notificationId) {
		return new ProgressNotification(context, tiker, smallIconId, title, content, notificationId);
	}
	
	/**
	 * 创建自定义通知
	 * @param context 上下文对象
	 * @param tiker 状态栏显示内容
	 * @param smallIconId icon图标资源id
	 * @param title 通知标题
	 * @param content 通知内容
	 * @param layoutId 自定义通知布局id
	 * @param handler 自定义通知事件处理器
	 * @param notificationId 通知id用于更新通知
	 * 
	 * @return 返回创建的通知
	 */
	public static INotification createCustomNotification(Context context,String tiker,int smallIconId,String title,String content,int layoutId,INotificationHandler handler,int notificationId) {
		return new CustomNotification(context, tiker, smallIconId, title, content, layoutId, handler, notificationId);
	}
}
