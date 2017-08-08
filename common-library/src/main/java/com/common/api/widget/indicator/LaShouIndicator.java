package com.common.api.widget.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.RadioGroup;

public class LaShouIndicator extends RadioGroup {

	private Context mContext;

	private int mCount;

	private int mSelectedItem;

	public LaShouIndicator(Context context) {
		super(context);
		init(context);
	}

	public LaShouIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
	}

	public void setCount(int count) {

		this.mCount = count;
		IndicatorRadioButton rb;
		removeAllViews();
		clearCheck();
		LayoutParams params = new LayoutParams(Tools.dpToPx(mContext, 20),
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		for (int i = 0; i < count; i++) {
			rb = new IndicatorRadioButton(mContext);
			rb.setId(i);
			rb.setClickable(false);
			rb.setLayoutParams(params);
			addView(rb);
		}
		check(mSelectedItem);
	}

	public void setSelectedItem(int selectedItem) {
		this.mSelectedItem = selectedItem;
		check(selectedItem);
	}

}
