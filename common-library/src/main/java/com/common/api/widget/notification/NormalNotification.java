package com.common.api.widget.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


/**
 * 普通通知
 * @author hezd
 *
 */
public class NormalNotification{
	// 上下文对象
	private Context mContext;
	// 通知标题
	private String mTitle;
	// 通知内容
	private String mContent;
	// 要跳转的activity的class
	private Class<? extends Activity> mActivityClass;
	// icon图标资源id
	private int mIconId;
	// 通知id,用于更新通知
	private int mNotificationId;
	// 状态栏显示的文字
	private String mTiker;
	// Activity启动模式
	private int mStartMode;
	private Bundle mBundle;
	
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
	 * 构造函数
	 * @param context 上下文对象
	 * @param tiker 状态栏显示的文字
	 * @param iconId icon图标资源id
	 * @param title 通知标题
	 * @param content 通知内容
	 * @param notificationId 通知id，用于更新通知
	 * @param activityCalss 要跳转的activity的class
	 * @param startMode activity启动模式,-1表示不设置。
	 * @param bundle 打开页面上时携带的数据，可以为null
	 */
	public NormalNotification(Context context,String tiker,int iconId ,String title,String content,int notificationId,int startMode,Class<? extends Activity> activityCalss,Bundle bundle) {
		this.mContext = context;
		this.mIconId = iconId;
		this.mTitle = title;
		this.mContent = content;
		this.mActivityClass = activityCalss;
		this.mTiker = tiker;
		this.mNotificationId = notificationId;
		this.mStartMode = startMode;
		this.mBundle = bundle;
	}
	
	public void show() {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(mContext)
		        .setSmallIcon(mIconId)
		        .setContentTitle(mTitle)
		        .setContentText(mContent);
		mBuilder.setAutoCancel(true);
		mBuilder.setTicker(mTiker);
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(mContext, mActivityClass);
		// set startMode
		if(mStartMode!=-1)
			resultIntent.setFlags(mStartMode);
		if(mBundle!=null)
			resultIntent.putExtras(mBundle);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(mActivityClass);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mNotificationId, mBuilder.build());
	}

}
