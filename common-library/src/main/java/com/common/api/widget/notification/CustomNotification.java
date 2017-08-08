package com.common.api.widget.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;



/**
 * 自定义通知
 * @author hezd
 *
 */
public class CustomNotification implements INotification{
	// 上下文对象
	private Context mContext;
	// 自定义通知布局id
	private int mLayoutId;
	// 自定义通知处理器
	private INotificationHandler mHandler;
	// 通知id，用于更新通知
	private int mNotificationId;
	// 通知标题
	private String mTitle;
	// 通知内容
	private String mContent;
	// 通知icon资源id
	private int mSmallIconId;
	// 状态栏显示内容 
	private String mTiker;
	
	/**
	 * 构造函数
	 * @param context 上下文对象
	 * @param tiker 状态栏显示内容
	 * @param smallIconId icon图标资源id
	 * @param title 通知标题
	 * @param content 通知内容
	 * @param layoutId 自定义通知布局id
	 * @param handler 自定义通知事件处理器
	 * @param notificationId 通知id用于更新通知
	 */
	public CustomNotification(Context context,String tiker,int smallIconId,String title,String content,int layoutId,INotificationHandler handler,int notificationId) {
		this.mContext = context;
		this.mTiker = tiker;
		this.mTitle = title;
		this.mContent = content;
		this.mSmallIconId = smallIconId;
		this.mLayoutId = layoutId;
		this.mHandler = handler;
		this.mNotificationId = notificationId;
	}
	
	@Override
	public void show() {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(mContext)
		        .setSmallIcon(mSmallIconId)
		        .setContentTitle(mTitle)
		        .setContentText(mContent);
		mBuilder.setAutoCancel(true);
		mBuilder.setTicker(mTiker);
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), mLayoutId);
		mBuilder.setContent(remoteView);
		
		// 处理自定义通知的点击事件
		mHandler.handleClick(mBuilder,remoteView);
		
		NotificationManager mNotificationManager =
			    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mNotificationId, mBuilder.build());
	}

}
