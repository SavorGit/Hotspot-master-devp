package com.common.api.utils;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

public class TimerUtils {

	/**
	 * 在指定时间执行一次intent
	 * 
	 * @param context
	 * @param intent
	 * @param when
	 *            指定时间
	 * @return
	 */
	public static AlarmManager getAlarmInstance(Context context, Intent intent,
			Date when) {
		long delay = when.getTime() - System.currentTimeMillis();
		return getAlarmInstance(context, intent, delay, -1);
	}

	/**
	 * 在指定延迟时间后执行一次intent
	 * 
	 * @param context
	 * @param intent
	 * @param delay
	 *            延迟时间
	 * @return
	 */
	public static AlarmManager getAlarmInstance(Context context, Intent intent,
			long delay) {
		return getAlarmInstance(context, intent, delay, -1);
	}

	/**
	 * 在指定时间开始每间隔一段时间执行
	 * 
	 * @param context
	 * @param intent
	 * @param when
	 *            指定时间
	 * @param intervalMillis
	 *            间隔时间
	 * @return
	 */

	public static AlarmManager getAlarmInstance(Context context, Intent intent,
			Date when, long intervalMillis) {
		long delay = when.getTime() - System.currentTimeMillis();
		return getAlarmInstance(context, intent, delay, intervalMillis);
	}

	/**
	 * 在指定延迟时间后每间隔一段时间执行
	 * 
	 * @param context
	 * @param intent
	 * @param delay
	 *            延迟时间
	 * @param intervalMillis
	 *            间隔时间
	 * @return
	 */
	public static AlarmManager getAlarmInstance(Context context, Intent intent,
			long delay, long intervalMillis) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
		if (intervalMillis > 0) {
			manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay,
					intervalMillis, pendingIntent);
		} else {
			manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay,
					pendingIntent);
		}
		return manager;
	}

	/**
	 * 间隔指定时间重复执行，需要手动cancel
	 * 
	 * @param runnable
	 *            执行事件
	 * @param period
	 *            间隔时间
	 * @return
	 */
	public static CountDownTimer getCountDownInstance(long period,
			final Runnable runnable) {
		return new CountDownTimer(Long.MAX_VALUE, period) {

			@Override
			public void onTick(long millisUntilFinished) {
				runnable.run();
			}

			@Override
			public void onFinish() {
			}
		}.start();
	}

	/**
	 * 倒计时，不需要手动开启和结束，仅中途取消需要手动cancel
	 * 
	 * @param millisInFuture
	 *            总时间
	 * @param countDownInterval
	 *            间隔时间
	 * @param timerListener
	 *            响应回调
	 * @return
	 */
	public static CountDownTimer getCountDownInstance(long millisInFuture,
			long countDownInterval, final TimerListener timerListener) {
		if (timerListener == null)
			return null;
		return new CountDownTimer(millisInFuture, countDownInterval) {

			@Override
			public void onTick(long millisUntilFinished) {
				timerListener.onTimerUpdate(millisUntilFinished);
			}

			@Override
			public void onFinish() {
				timerListener.onTimerStop();
			}
		}.start();
	}

	public interface TimerListener {
		/**
		 * 倒计时过程
		 * 
		 * @param millisUntilFinished
		 *            剩余时间
		 */
		public void onTimerUpdate(long millisUntilFinished);

		/**
		 * 倒计时结束
		 */
		public void onTimerStop();
	}

}
