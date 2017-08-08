package com.common.api.widget.customTab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.common.api.R;
import com.common.api.exception.BaseException;
import com.common.api.utils.LogUtils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 底部导航
 * 
 * @author dewyze
 * 
 */
public class MyTabWidget extends LinearLayout {

	/**自定义条码索引*/
	private static final int CUSTOM_INDEX = 2;
	private static final String TAG = "MyTabWidget";
//	private int[] mDrawableIds = new int[] { R.drawable.ic_tab_artists,
//			R.drawable.ic_tab_albums, R.drawable.ic_tab_songs,
//			R.drawable.ic_tab_playlists};
	// 存放底部菜单的各个文字CheckedTextView
	private List<CheckedTextView> mCheckedList = new ArrayList<CheckedTextView>();
	// 存放底部菜单每项View
	private List<View> mViewList = new ArrayList<View>();
	// 存放指示点
	private List<TextView> mMessageIndicate = new ArrayList<TextView>();

	// 底部菜单的文字数组
	private CharSequence[] mLabels;
	private ColorStateList mTextColorStateList;
	// 底部图片的数组
	private TypedArray mIconTa;

	private TypedArray mAttrTa;

	public MyTabWidget(Context context, AttributeSet attrs) {
		this(context,attrs, 0);
	}
	public MyTabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TabWidget, defStyle, 0);

		// 读取xml中，各个tab使用的文字
		mLabels = a.getTextArray(R.styleable.TabWidget_bottom_labels);
		//读取xml中底部显示图片
		int resId = a.getResourceId(R.styleable.TabWidget_bottom_icon, 0);
		mIconTa = context.getResources().obtainTypedArray(resId);
		mTextColorStateList = a.getColorStateList(R.styleable.TabWidget_bottom_text_color);
		int attrResId = a.getResourceId(R.styleable.TabWidget_bottom_items_attr, 0);
		if (attrResId > 0) {
			mAttrTa = context.getResources().obtainTypedArray(attrResId);
		}
		if (null == mLabels || mLabels.length <= 0) {
			try {
				throw new BaseException("底部菜单的文字数组未添加...");
			} catch (BaseException e) {
				e.printStackTrace();
			} finally {
				LogUtils.i(MyTabWidget.class.getSimpleName() + " 出错");
			}
			a.recycle();
			return;
		}

		a.recycle();

		// 初始化控件
		init(context);
	}


	public MyTabWidget(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 显示低价购车，重置tab，显示低价购车图标
	 * @param resId 低价购车模式下图片资源id数组
	 */
	public void showLowerPrice(int resId) {
		removeAllViews();
		final List<CharSequence> labels = Arrays.asList(mLabels);
		List<CharSequence> list = new ArrayList<CharSequence>();
		list.addAll(labels);
		list.add(2, "");
		mLabels = new CharSequence[list.size()];
		mLabels = list.toArray(mLabels);
		mIconTa = getContext().getResources().obtainTypedArray(resId);
		init(getContext());
	}

	/**
	 * 初始化控件
	 */
	private void init(final Context context) {
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setBackgroundResource(R.drawable.index_bottom_bar);

		LayoutInflater inflater = LayoutInflater.from(context);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.weight = 1.0f;
		params.gravity = Gravity.CENTER;
		mCheckedList.clear();
		int size = mLabels.length;
		for (int i = 0; i < size; i++) {

			final int index = i;

			// 每个tab对应的layout
			final View view = inflater.inflate(R.layout.tab_item, null);
			final CheckedTextView itemName = (CheckedTextView) view
					.findViewById(R.id.item_name);
			ImageView imageView = (ImageView) view.findViewById(R.id.iv_bg);
			if(i == 2) {
				itemName.setVisibility(GONE);
				imageView.setVisibility(VISIBLE);
				imageView.setImageResource(mIconTa.getResourceId(i, 0));
				this.addView(view,params);
				// 将各个tab的View添加到list
				mViewList.add(view);

			}else {
				itemName.setCompoundDrawablesWithIntrinsicBounds(null, context
						.getResources().getDrawable(mIconTa.getResourceId(i, 0)), null, null);
				itemName.setText(mLabels[i]);
				itemName.setTextColor(mTextColorStateList);

				// 指示点ImageView，如有版本更新需要显示
				final TextView indicateImg = (TextView) view
						.findViewById(R.id.tv_message_indicator);
				this.addView(view, params);

				// 将CheckedTextView添加到list中，便于操作
				mCheckedList.add(itemName);
				// 将指示图片加到list，便于控制显示隐藏
				mMessageIndicate.add(indicateImg);
				// 将各个tab的View添加到list
				mViewList.add(view);
			}

			// CheckedTextView设置索引作为tag，以便后续更改颜色、图片等
			itemName.setTag(index);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// 设置底部图片和文字的显示
					setTabsDisplay(index);

					if (null != mTabListener) {
						// tab项被选中的回调事件
						mTabListener.onTabSelected(index);
					}
				}
			});

			// 初始化 底部菜单选中状态,默认第一个选中
			if (i == 0&&i!=2) {
				itemName.setChecked(true);
			} else if(i!=2){
				itemName.setChecked(false);
			}
		}
	}

	/**
	 * 设置指示点的显示
	 * 
	 * @param context
	 * @param position
	 *            显示位置
	 * @param visible
	 *            是否显示，如果false，则都不显示
	 */
	public void setIndicateDisplay(Context context, int position,
			boolean visible,String count) {
		int size = mMessageIndicate.size();
		if (size <= position) {
			return;
		}
//		TextView message = mMessageIndicate.get(position);
//		message.setText(count);
//		message.setVisibility(visible ? View.VISIBLE : View.GONE);
		View childView = getChildAt(position);
		CheckedTextView itemName = (CheckedTextView) childView.findViewById(R.id.item_name);
		itemName.setText(count);
		childView.invalidate();
	}

	public void setTabIconDisplay(Context context,int positon,int resid) {
		if(positon<0)
			return;
		if(positon==2) {
			View childView = getChildAt(positon);
			ImageView itemName = (ImageView) childView.findViewById(R.id.iv_bg);
			itemName.setImageResource(resid);
			itemName.invalidate();
		}else {
			View childView = getChildAt(positon);
			CheckedTextView itemName = (CheckedTextView) childView.findViewById(R.id.item_name);
			itemName.setCompoundDrawablesWithIntrinsicBounds(null, context
					.getResources().getDrawable(resid), null, null);
			itemName.invalidate();
		}
	}

	/**
	 * 设置底部导航中图片显示状态和字体颜色
	 */
	public void setTabsDisplay(int index) {
		if (mAttrTa != null) {
			int isSpecial = mAttrTa.getInt(index, 0);
			if (isSpecial != 0) {
				return;
			}
		}
		int size = mCheckedList.size();
		for (int i = 0; i < size; i++) {
			CheckedTextView checkedTextView = mCheckedList.get(i);
			if ((Integer) (checkedTextView.getTag()) == index) {
				LogUtils.i(mLabels[index] + " is selected...");
				checkedTextView.setChecked(true);
			} else {
				checkedTextView.setChecked(false);
			}
		}
	}
	/**
	 * 更新底部导航中图片显示状态和字体颜色
	 */
	public void updateTabsDisplay(Context context, int index,List<Drawable> listIcon) {
		int size = mCheckedList.size();
		for (int i = 0; i < size; i++) {
			CheckedTextView checkedTextView = mCheckedList.get(i);
			if ((Integer) (checkedTextView.getTag()) == index) {
				LogUtils.i(mLabels[index] + " is selected...");
				checkedTextView.setChecked(true);
			} else {
				checkedTextView.setChecked(false);
			}
			checkedTextView.setCompoundDrawables(null, listIcon.get(i), null, null);
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpecMode != MeasureSpec.EXACTLY) {
			widthSpecSize = 0;
		}

		if (heightSpecMode != MeasureSpec.EXACTLY) {
			heightSpecSize = 0;
		}

		if (widthSpecMode == MeasureSpec.UNSPECIFIED
				|| heightSpecMode == MeasureSpec.UNSPECIFIED) {
		}

		// 控件的最大高度，就是下边tab的背景最大高度
		int width;
		int height;
		width = Math.max(getMeasuredWidth(), widthSpecSize);
		height = Math.max(getMeasuredHeight(),
				heightSpecSize);
		setMeasuredDimension(width, height);
	}

	// 回调接口，用于获取tab的选中状态
	private OnTabSelectedListener mTabListener;

	public interface OnTabSelectedListener {
		void onTabSelected(int index);
	}

	public void setOnTabSelectedListener(OnTabSelectedListener listener) {
		this.mTabListener = listener;
	}

}
