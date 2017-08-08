package com.common.api.widget.notification;

import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * 通知处理器 处理自定义事件
 * @author hezhudong
 *
 */
public interface INotificationHandler {
	/**
	 * 处理通知自定义点击事件
	 * @param builder 通知构造器
	 * @param remoteview 自定义通知的remoteview 
	 */
	void handleClick(NotificationCompat.Builder builder,RemoteViews view);
}
