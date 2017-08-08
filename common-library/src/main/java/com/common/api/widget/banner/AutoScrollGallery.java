package com.common.api.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class AutoScrollGallery extends Gallery {
	
	private static final int MESSAGE_WHAT_EMPTY = 0x01;
	/**轮播时间间隔*/
	private static int SPACE = 5000;

	// 图片数组的大小
	private int length;

	public AutoScrollGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoScrollGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setImagesLength(int length) {
		this.length = length;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (getCount() > 1) {
			int kEvent;
			if (isScrollingLeft(e1, e2)) {
				kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
			} else {
				kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
			}
			onKeyDown(kEvent, null);

			if (this.getSelectedItemPosition() == 0) {// 实现后退功能
				this.setSelection(length);
			}
		}
		return false;

	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mOnTouch = true;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mOnTouch = false;
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	public void setSPACE(int sPACE) {
		SPACE = sPACE;
	}

	/**
	 * 开始自动滚动
	 */
	public void startAutoScroll() {
		isAutoScroll = true;
		if (thread != null && !thread.isAlive() && getCount() > 1) {
			thread.start();
		}
	}
	
	/**
	 * 是否可以自动滚动
	 * @return
	 */
	public boolean isAutoScroll(){
		return this.isAutoScroll;
	}
	
	/**
	 * 取消自动滚动
	 */
	public void cancleAutoScroll(){
		setAutoScroll(false);
	}
	
	public void setAutoScroll(boolean autoScroll){
		this.isAutoScroll = autoScroll;
	}
	
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_WHAT_EMPTY:
				int postion = getSelectedItemPosition();
				if (postion >= getCount() - 1) {
					onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				} else {
					onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				}
				break;
			default:
				break;
			}
		};
	};

	private boolean mOnTouch = false;
	private boolean isAutoScroll = false;
	private Thread thread = new Thread(new Runnable() {
		@Override
		public void run() {
			int count = 0;
			while (isAutoScroll) {
				count = 0;
				while (count < 5) {
					count++;
					try {
						Thread.sleep(SPACE/5);
					} catch (InterruptedException e) {
					}
					if (mOnTouch) {
						count = 0;
					}
				}
				handler.sendEmptyMessage(MESSAGE_WHAT_EMPTY);
			}
		}
	});
	
	public void onDestroy() {
		isAutoScroll = true;
		thread = null;
	}
	
	

}
