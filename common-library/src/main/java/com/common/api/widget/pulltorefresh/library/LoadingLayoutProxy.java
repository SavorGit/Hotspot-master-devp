package com.common.api.widget.pulltorefresh.library;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.common.api.widget.pulltorefresh.library.internal.LoadingLayout;

import java.util.HashSet;


/**
 * loadingLayout加载布局的代理对象
 * @author hezd
 *
 */
public class LoadingLayoutProxy implements ILoadingLayout {
	// 加载布局Set集合
	private final HashSet<LoadingLayout> mLoadingLayouts;

	LoadingLayoutProxy() {
		mLoadingLayouts = new HashSet<LoadingLayout>();
	}

	/**
	 * 允许你去添加一个额外的加载布局实例到这个代理。
	 * 这只有在你需要保留你自己实例并且要他们被包含在任何
	 * PullToRefreshBase#createLoadingLayoutProxy(boolean, boolean)
	 * createLoadingLayoutProxy(...)} 被调用的时候才需要。
	 * 
	 * 
	 * @param layout - 被包含的加载布局
	 */
	public void addLayout(LoadingLayout layout) {
		if (null != layout) {
			mLoadingLayouts.add(layout);
		}
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setLastUpdatedLabel(label);
		}
	}

	@Override
	public void setLoadingDrawable(Drawable drawable) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setLoadingDrawable(drawable);
		}
	}

	@Override
	public void setRefreshingLabel(CharSequence refreshingLabel) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setRefreshingLabel(refreshingLabel);
		}
	}

	@Override
	public void setPullLabel(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setPullLabel(label);
		}
	}

	@Override
	public void setReleaseLabel(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setReleaseLabel(label);
		}
	}

	public void setTextTypeface(Typeface tf) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setTextTypeface(tf);
		}
	}
}
