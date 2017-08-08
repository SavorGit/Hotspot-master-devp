package com.common.api.widget.pulltorefresh.library;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

/**
 * header或footer的加载布局
 * @author hezd
 *
 */
public interface ILoadingLayout {

	/**
	 * 设置最近更新文本内容，当下拉刷新的时候。
	 * 
	 * @param label 文本
	 */
	public void setLastUpdatedLabel(CharSequence label);

	/**
	 * 设置加载布局中使用的图标，类似调用setLoadingDrawable(drawable, Mode.BOTH)方法
	 * 
	 * @param drawable - 要显示的图标
	 */
	public void setLoadingDrawable(Drawable drawable);

	/**
	 * 设置控件下拉时显示的文本内容
	 * 
	 * @param pullLabel - 下拉提示文本
	 */
	public void setPullLabel(CharSequence pullLabel);

	/**
	 * 设置控件刷新时提示文本标签
	 * 
	 * @param refreshingLabel - 刷新文本标签
	 */
	public void setRefreshingLabel(CharSequence refreshingLabel);

	/**
	 * 设置松手释放时显示的文本标签
	 * 
	 * @param releaseLabel - 显示文本标签 
	 */
	public void setReleaseLabel(CharSequence releaseLabel);

	/**
	 * 设置文字显示样式
	 */
	public void setTextTypeface(Typeface tf);

}
