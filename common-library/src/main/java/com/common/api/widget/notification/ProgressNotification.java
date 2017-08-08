package com.common.api.widget.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;


/**
 * 进度条通知
 * @author hezd
 *
 */
public class ProgressNotification implements INotification{
	// 通知管理器
	private NotificationManager mNotifyManager;
	// 上下文对象
	private Context mContext;
	// 通知构造器
	private Builder mBuilder;
	// 状态栏显示内容
	private String mTiker;
	// icon资源id
	private int mSmallIconId;
	// 通知标题
	private String mTitle;
	// 通知内容
	private String mContent;
	// 通知id，用于更新通知
	private int mNotificationId;
	
	// 进度条通知进度的初始值
	private static final int NOTIFICATION_DEFAULT_PROGRESS = 0;
	// 进度条通知进度最大值
	private static final int NOTIFICATION_MAX_PROGRESS = 100;
	
	/**
	 *  构造函数
	 * @param context 上下文对象
	 * @param tiker 状态栏显示内容
	 * @param smallIconId icon资源id
	 * @param title 通知标题
	 * @param content 通知内容
	 * @param notificationId 通知id，用于更新通知
	 */
	public ProgressNotification(Context context,String tiker,int smallIconId,String title,String content,int notificationId) {
		this.mContext = context;
		this.mTiker = tiker;
		this.mTitle = title;
		this.mSmallIconId = smallIconId;
		this.mContent = content;
		this.mNotificationId = notificationId;
	}
	
	@Override
	public void show() {
		mNotifyManager =
		        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setContentTitle(mTitle)
		    .setContentText(mContent)
		    .setSmallIcon(mSmallIconId);
		mBuilder.setTicker(mTiker);
//		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		
		mBuilder.setProgress(NOTIFICATION_MAX_PROGRESS,NOTIFICATION_DEFAULT_PROGRESS , false);
		mNotifyManager.notify(mNotificationId, mBuilder.build());
	}
	
	/**
	 * 更新进度条通知
	 * @param notificationId 通知id
	 * @param progress 进度
	 */
	public void updateProgressNotification(int notificationId,int progress) {
		mBuilder.setProgress(NOTIFICATION_MAX_PROGRESS, progress, false);
		mNotifyManager.notify(notificationId, mBuilder.build());
	}
}
