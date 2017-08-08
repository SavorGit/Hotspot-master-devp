package com.common.api.widget;

import com.common.api.R;
import com.common.api.utils.AppUtils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 数量选择控件
 * 
 * @author dewyze
 * 
 */
public class CustomEditText extends LinearLayout implements
		View.OnClickListener {

	protected static final String TAG = "CustomEditText";
	// 数值输入框
	private EditText mEditText;
	// 加 按钮
	private ImageButton mAddBtn;
	// 减 按钮
	private ImageButton mReduceBtn;
	// 默认初始值
	private String mInitValue = "1";
	// 最小值
	private String mMinValue;
	// 最大值
	private String mMaxValue;
	private Handler mHandler;
	private int positon;
	private String id;
	private int operation = 0;
	public CustomEditText(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.custom_edittext,
				this);
		// 初始化控件
		init();
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	/**用于区分多个对象中的加减选择*/
	public void setHandler(Handler handler,int position) {
		this.mHandler = handler;
		this.positon = position;
	}
	/**区分多个对象，传id*/
	public void setHandler(Handler handler,String id) {
		this.mHandler = handler;
		this.id = id;
	}
	/**
	 * 设置初始值
	 * 
	 * @param value
	 */
	public void setInitValue(String value) {
		if ("0".equals(value)||TextUtils.isEmpty(value)||"null".equals(value)) {
			mMinValue = "0";
		}
		mInitValue = value;
		mEditText.setText(mInitValue);
		// 根据EditText的内容设置按钮的显示
		if (mEditText.getText().toString() != null
				&& !"".equals(mEditText.getText().toString())) {
			int num = Integer.valueOf(mEditText.getText().toString());
			if (num > Integer.parseInt(mMinValue)) {
				mReduceBtn.setBackgroundResource(R.drawable.btn_minus);
			} else {
				mReduceBtn.setBackgroundResource(R.drawable.btn_minus_a);
			}
			if (num >= Integer.parseInt(mMaxValue)) {
				mEditText.setText(mMaxValue);
				mAddBtn.setBackgroundResource(R.drawable.btn_plus_c);
			} else {
				mAddBtn.setBackgroundResource(R.drawable.btn_plus);
			}
		}
	}

	/**
	 * 初始化控件，设置监听
	 */
	public void init() {
		mEditText = (EditText) findViewById(R.id.num_edit);
		mAddBtn = (ImageButton) findViewById(R.id.plus_btn);
		mReduceBtn = (ImageButton) findViewById(R.id.minus_btn);
		mEditText.setText(mInitValue);
		mAddBtn.setOnClickListener(this);
		mReduceBtn.setOnClickListener(this);
		mEditText.addTextChangedListener(mWatcher);
		operation = 0;
	}

	/**
	 * 获取EditText内容
	 */
	public String getEditTextValue() {
		return mEditText.getText().toString();
	}

	/**
	 * 设置最大值
	 * 
	 * @param value
	 *            最大值
	 */
	public void setMaxValue(String value) {
		if ("0".equals(value)||TextUtils.isEmpty(value)||"null".equals(value)) {
			mMaxValue = "" + Integer.MAX_VALUE;
		} else {
			mMaxValue = value;
		}
	}

	/**
	 * 设置最小值
	 * 
	 * @param value
	 *            最小值
	 */
	public void setMinValue(String value) {
		if ("0".equals(value)||TextUtils.isEmpty(value)||"null".equals(value)) {
			mMinValue = "0";
		} else {
			mMinValue = value;
		}
	}

	/**
	 * 获取EditText
	 */
	public EditText getEditText() {
		return mEditText;
	}

	// modify by lee 20130722 for bug 2226 start
	// 由于多选商品的时候，各个EditText的值可以为0，所以做了特别
	private boolean mIsMultiCustomEditText = false;

	public void setIsMultiCustomEditText(boolean isMultiCustomEditText) {
		this.mIsMultiCustomEditText = isMultiCustomEditText;
	}

	// modify by lee 20130722 for bug 2226 end

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.minus_btn) {
//			if(AppUtils.isFastDoubleClick(1)){
//				return;
//			}
			// 处理减操作
			handleMinus();
		} else if (id == R.id.plus_btn) {
//			if(AppUtils.isFastDoubleClick(1)){
//				return;
//			}
			// 处理加操作
			handlePlus();
		}
	}

	/**
	 * 处理减号操作
	 */
	private void handleMinus() {
		if (mEditText.getText().toString() != null
				&& !"".equals(mEditText.getText().toString())) {
			int num = Integer.valueOf(mEditText.getText().toString());

			// modify by lee 20130722 for bug 2226 start
			// 多分类商品是特殊处理,各个分类的商品数量可以为0
			if (mIsMultiCustomEditText) {
				if (num <= 0) {
					mEditText.setText(0 + "");
					mReduceBtn.setClickable(false);
				} else {
					operation = 1;
					num--;
					mAddBtn.setClickable(true);
					mEditText.setText(Integer.toString(num));
				}
				return;
			}
			// modify by lee 20130722 for bug 2226 end

			if (num <= Integer.valueOf(mMinValue)) {
				mEditText.setText(mMinValue);
				mReduceBtn.setClickable(false);
			} else {
				operation = 1;
				num--;
				mAddBtn.setClickable(true);
				mEditText.setText(Integer.toString(num));
			}

			// add by he for bug 2732 start
			if (num > Integer.parseInt(mMinValue)) {
				mReduceBtn.setBackgroundResource(R.drawable.btn_minus);
				mReduceBtn.setClickable(true);
			} else {
				mReduceBtn.setBackgroundResource(R.drawable.btn_minus_a);
				mReduceBtn.setClickable(false);
			}
			if (num >= Integer.parseInt(mMaxValue)) {
				mEditText.setText(mMaxValue);
				mAddBtn.setBackgroundResource(R.drawable.btn_plus_c);
			} else {
				mAddBtn.setBackgroundResource(R.drawable.btn_plus);
			}
			// add by he for bug 2732 end
		} else {
			mAddBtn.setClickable(true);
			mReduceBtn.setClickable(false);
			mEditText.setText(mMinValue);
		}
	}

	/**
	 * 处理加操作
	 */
	private void handlePlus() {
		if (mEditText.getText().toString() != null
				&& !"".equals(mEditText.getText().toString())) {
			int num = Integer.valueOf(mEditText.getText().toString());

			if (num >= Integer.valueOf(mMaxValue)) {
				mEditText.setText(mMaxValue);
				mAddBtn.setClickable(false);
			} else {
				operation = 1;
				num++;
				mReduceBtn.setClickable(true);
				mEditText.setText(Integer.toString(num));
			}

			// add by he for bug 2732 start
			if (num > Integer.parseInt(mMinValue)) {
				mReduceBtn.setBackgroundResource(R.drawable.btn_minus);
			} else {
				mReduceBtn.setBackgroundResource(R.drawable.btn_minus_a);
			}
			if (num >= Integer.parseInt(mMaxValue)) {
				mEditText.setText(mMaxValue);
				mAddBtn.setBackgroundResource(R.drawable.btn_plus_c);
			} else {
				mAddBtn.setBackgroundResource(R.drawable.btn_plus);
			}
			// add by he for bug 2732 end
		} else {
			mAddBtn.setClickable(true);
			mReduceBtn.setClickable(false);
			mEditText.setText(mMinValue);
		}
	}
	/**
	 * 设置是否可用
	 * @param enable
	 * @param enableStr 不可用时提示信息
	 */
	public void setEnable(boolean enable,String enableStr) {
		mReduceBtn.setEnabled(enable);
		mAddBtn.setEnabled(enable);
		mEditText.setEnabled(enable);
		if(enable) {
			mReduceBtn.setBackgroundResource(R.drawable.btn_minus);
			mAddBtn.setBackgroundResource(R.drawable.btn_plus);
		}else {
			if(!TextUtils.isEmpty(enableStr)) {
				mEditText.setText(enableStr);
			}else {
				mEditText.setText("0");
			}
			mReduceBtn.setBackgroundResource(R.drawable.btn_minus_a);
			mAddBtn.setBackgroundResource(R.drawable.btn_plus_c);
		}
	}
	/**
	 * EditText数值变化监听
	 */
	private TextWatcher mWatcher = new TextWatcher() {

		private int num;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// add by he for bug 2732 start
			if (TextUtils.isEmpty(mMinValue)) {
				mMinValue = "0";
			}
			if (TextUtils.isEmpty(mMaxValue)) {
				mMaxValue = ""+Integer.MAX_VALUE;
			}
			if (s != null && !"".equals(s.toString())) {
				if ("已卖光".equals(s.toString())) {
					return;
				}
				num = Integer.parseInt(s + "");

				if (num < Integer.parseInt(mMinValue)) {
					Toast.makeText(getContext(), "最小购买数量为" + mMinValue,
							Toast.LENGTH_SHORT).show();
					mReduceBtn.setClickable(false);
					mReduceBtn.setBackgroundResource(R.drawable.btn_minus_a);
				} else if (num == Integer.parseInt(mMinValue)) {
					mReduceBtn.setBackgroundResource(R.drawable.btn_minus_a);
					mReduceBtn.setClickable(false);
				} else {
					mReduceBtn.setBackgroundResource(R.drawable.btn_minus);
					mReduceBtn.setClickable(true);
				}
				if (num > Integer.parseInt(mMaxValue)) {
					Toast.makeText(getContext(), "购买数量不能超过" + mMaxValue,
							Toast.LENGTH_SHORT).show();
					mEditText.setText(mMaxValue);
					mAddBtn.setBackgroundResource(R.drawable.btn_plus_c);
					mAddBtn.setClickable(false);
				} else {
					mAddBtn.setBackgroundResource(R.drawable.btn_plus);
					mAddBtn.setClickable(true);
				}
			} else {
//				mEditText.setText("0");
				Toast.makeText(getContext(), "请输入购买数量", Toast.LENGTH_SHORT)
						.show();
			}
			// add by he for bug 2732 end
			if (s == null || "".equals(s.toString())) {
				s = mMinValue;
			} else if (Integer.valueOf(s.toString()) <= Integer
					.valueOf(mMinValue)) {
				// modify by lee 20130722 start
				if (mIsMultiCustomEditText) {
					s = s.toString() + "";
				} else {
					s = mMinValue;
				}
				// modify by lee 20130722 end
			} else if (Integer.valueOf(s.toString()) >= Integer
					.valueOf(mMaxValue)) {
				s = mMaxValue;
			}
			num = Integer.parseInt(s + "");
			// modify by lee 20130722 for bug 2226 start
			if (mIsMultiCustomEditText) {
				if (num >= 0) {
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("id", id);
					b.putInt("num", num);
					b.putInt("operation", operation);
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
				return;
			}
			// modify by lee 20130722 for bug 2226 end
			if (num >= 0) {
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("id", id);
				b.putInt("num", num);
				b.putInt("operation", operation);
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			if (s != null && !"".equals(s.toString())) {
				int tempNum = Integer.parseInt(s + "");
				s = String.valueOf(tempNum);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// 去除尾部多余的0
			String text = s.toString();
			if (null != text && !TextUtils.isEmpty(text)) {
				int len = text.length();
				if (len == 2 && text.startsWith("0")) {
					s.delete(0, 1);
				}
				if (len == 3 && text.startsWith("00")) {
					s.delete(0, 2);
				}
				if (len == 4 && text.startsWith("000")) {
					s.delete(0, 3);
				}
				if (len == 5 && text.startsWith("0000")) {
					s.delete(0, 4);
				}
			}
		}
	};

}
