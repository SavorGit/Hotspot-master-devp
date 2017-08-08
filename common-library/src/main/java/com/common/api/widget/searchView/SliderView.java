package com.common.api.widget.searchView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.common.api.R;
import com.common.api.utils.DensityUtil;

/**
 * 字母导航条
 * @author hezd
 *
 */
public class SliderView extends View {
	private OnItemClickListener mOnItemClickListener;
	public static String[] b = { "#", "$","*","A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
			"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y", "Z" };
	private RectF rectFs[] = new RectF[b.length];
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;
	private PopupWindow mPopupWindow;
	private TextView mPopupText;
	private int mCharHeight = 15;
	private boolean isShowPop = true;
	
	private boolean blockPopForScroll;
	
	private float yPos;//字母的偏移量
	private int verticalSpace;
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			dismissPopup();
			return true;
		}
	});

	public SliderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mCharHeight = context.getResources().getDimensionPixelSize(R.dimen.blade_view_text_size);
	}

	public SliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCharHeight = context.getResources().getDimensionPixelSize(R.dimen.blade_view_text_size);
	}

	public SliderView(Context context) {
		super(context);
		mCharHeight = context.getResources().getDimensionPixelSize(R.dimen.blade_view_text_size);
	}

	@Override
	protected void onDetachedFromWindow() {
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;
		super.onDetachedFromWindow();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		if (showBkg) {
//			canvas.drawColor(Color.parseColor("#4d000000"));
//		} else {
//			canvas.drawColor(Color.parseColor("#00000000"));
//		}
		canvas.drawColor(Color.TRANSPARENT);
		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / b.length;
		int fontHeight = getFontHeight(mCharHeight);
//		int verticalSpace = 0;
		if(fontHeight*b.length<height){
			int surplusHeight = height - fontHeight*b.length;
			verticalSpace = (int) Math.ceil(surplusHeight*1.0/(b.length+1));
			int otherSpace = (int) Math.ceil((height-fontHeight*29)*1.0/(b.length+1));
			if(verticalSpace>otherSpace){
				verticalSpace = otherSpace;
			}
					
		}
		yPos = (height -verticalSpace*(b.length+1)-fontHeight*b.length)/2;
		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.GRAY);
//			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(mCharHeight);
			paint.setFakeBoldText(true);
			paint.setAntiAlias(true);
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
			}
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
//			float yPos = (height -verticalSpace*(b.length+1)-fontHeight*b.length)/2;
			float fontY = yPos+(verticalSpace+fontHeight)*i+verticalSpace;
			canvas.drawText(b[i], xPos, fontY, paint);
			if(rectFs!=null && rectFs.length>=b.length){
				rectFs[i] = new RectF(xPos, fontY-(verticalSpace+1)/2, xPos+paint.measureText(b[i]), fontY+fontHeight+(verticalSpace+1)/2);
			}
			paint.reset();
		}

	}
//	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
////		if (showBkg) {
////			canvas.drawColor(Color.parseColor("#4d000000"));
////		} else {
////			canvas.drawColor(Color.parseColor("#00000000"));
////		}
//		canvas.drawColor(Color.TRANSPARENT);
//		int height = getHeight();
//		int width = getWidth();
//		int singleHeight = height / b.length;
//		for (int i = 0; i < b.length; i++) {
//			paint.setColor(Color.GRAY);
////			paint.setTypeface(Typeface.DEFAULT_BOLD);
//			paint.setTextSize(mCharHeight);
//			paint.setFakeBoldText(true);
//			paint.setAntiAlias(true);
//			if (i == choose) {
//				paint.setColor(Color.parseColor("#3399ff"));
//			}
//			float xPos = width / 2 - paint.measureText(b[i]) / 2;
//			float yPos = singleHeight * i + singleHeight;
//			canvas.drawText(b[i], xPos, yPos, paint);
//			paint.reset();
//		}
//		
//	}

	@Override
	protected Parcelable onSaveInstanceState() {
		dismissPopup();
		return super.onSaveInstanceState();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		int c = -1;
		c = (int) ((y-yPos)/(verticalSpace+getFontHeight(mCharHeight)));
//		for(int i=0;i<b.length;i++){
//			if(rectFs[i].contains(event.getX(), event.getY())){
//				c = i;
//				break;
//			}
//		}
//		final int c = (int) (y / getHeight() * b.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			blockPopForScroll = true;
			showBkg = true;
			if (oldChoose != c) {
				if (c >=0 && c < b.length) {
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			blockPopForScroll = false;
			showBkg = false;
			choose = -1;
			dismissPopup();
			invalidate();
			break;
		}
		return true;
	}
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		final int action = event.getAction();
//		final float y = event.getY();
//		final int oldChoose = choose;
//		final int c = (int) (y / getHeight() * b.length);
//		
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			blockPopForScroll = true;
//			showBkg = true;
//			if (oldChoose != c) {
//				if (c >=0 && c < b.length) {
//					performItemClicked(c);
//					choose = c;
//					invalidate();
//				}
//			}
//			
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if (oldChoose != c) {
//				if (c >= 0 && c < b.length) {
//					performItemClicked(c);
//					choose = c;
//					invalidate();
//				}
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			blockPopForScroll = false;
//			showBkg = false;
//			choose = -1;
//			dismissPopup();
//			invalidate();
//			break;
//		}
//		return true;
//	}
	
	

//	public void showPopup(int item) {
//		if(!isShowPop )
//			return;
//		String text = b[item];
//		if (mPopupWindow == null) {
//			mPopupText = new TextView(getContext());
//			mPopupText
//					.setBackgroundResource(R.color.pop_text_color);
//			android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
//			mPopupText.setLayoutParams(layoutParams);
//			mPopupText.setTextColor(getContext().getResources().getColor(R.color.white));
//			mPopupText.setTextSize(25);
//			mPopupText.setGravity(Gravity.CENTER_HORIZONTAL
//					| Gravity.CENTER_VERTICAL);
//			mPopupWindow = new PopupWindow(mPopupText,
//					DensityUtil.dpToPx(getContext(), 40), DensityUtil.dpToPx(getContext(), 40));
//			mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//		}
//		int width = (int)mPopupText.getPaint().measureText(text);
//		mPopupWindow.setWidth(width+50);
//
////		String text = "";
////		if (item == 0) {
////			text = "#";
////		} else {
////			text = Character.toString((char) ('A' + item - 1));
////		}
//		mPopupText.setText(text);
//		if (mPopupWindow.isShowing()) {
//			mPopupWindow.update();
//		} else {
//			mPopupWindow.showAtLocation(getRootView(),
//					Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
//		}
//	}
	public void showPopup(String text) {
		if(!isShowPop || blockPopForScroll) 
			return;
		if (mPopupWindow == null) {
			mPopupText = new TextView(getContext());
			mPopupText
					.setBackgroundResource(R.color.pop_text_color);
			android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
			mPopupText.setLayoutParams(layoutParams);
			mPopupText.setTextColor(getContext().getResources().getColor(R.color.white));
			mPopupText.setTextSize(25);
			mPopupText.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);
			mPopupWindow = new PopupWindow(mPopupText,
					DensityUtil.dpToPx(getContext(), 40), DensityUtil.dpToPx(getContext(), 40));
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
		int width = (int)mPopupText.getPaint().measureText(text);
		mPopupWindow.setWidth(width+50);

//		String text = "";
//		if (item == 0) {
//			text = "#";
//		} else {
//			text = Character.toString((char) ('A' + item - 1));
//		}
		mPopupText.setText(text);
		if (mPopupWindow.isShowing()) {
			mPopupWindow.update();
		} else {
			mPopupWindow.showAtLocation(getRootView(),
					Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
		
		mHandler.removeMessages(1);
		mHandler.sendEmptyMessageDelayed(1, 1000);
	}

	public void dismissPopup() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	private void performItemClicked(int item) {
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(b[item]);
//			showPopup(item);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(String s);
	}
	
	/**
	 * 改变导航标签显示内容
	 * @param labels
	 */
	public void setDataChanged(String[] labels) {
		b = labels;
		rectFs = new RectF[b.length];
		invalidate();
	}

	public boolean isShowPop() {
		return isShowPop;
	}

	public void setShowPop(boolean isShowPop) {
		this.isShowPop = isShowPop;
	}
	
	/**
	 * 获取字体的高度
	 * @param fontSize
	 * @return
	 */
	private int getFontHeight(float fontSize){  
	     Paint paint = new Paint();  
	     paint.setTextSize(fontSize);  
	     FontMetrics fm = paint.getFontMetrics();  
	    return (int) Math.ceil(fm.descent - fm.top) + 2;  
	}
	
}
