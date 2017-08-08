/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.common.api.widget.pulltorefresh.library;

import android.annotation.TargetApi;
import android.util.Log;
import android.view.View;

import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.Mode;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.State;

/**
 * 滚动事件帮助类
 * @author hezd
 *
 */
@TargetApi(9)
public final class OverscrollHelper {
	// 打印日志Log
	static final String LOG_TAG = "OverscrollHelper";
	// 默认滚动比例
	static final float DEFAULT_OVERSCROLL_SCALE = 1f;

	/**
	 * 帮助类方法对于滚动事件封装了所有的必须的方法。
	 * 应该仅仅使用在AdapterView上比如listview通过overScrollBy()调用并使用scrollRange = 0。
	 * 这样AdapterView没有一个滚动的范围（也就是getScrollY()不起作用）
	 * 
	 * @param view - 调用此方法的刷新控件.
	 * @param deltaX - X坐标变化值
	 * @param scrollX - 当前X坐标
	 * @param deltaY - Y坐标变化值
	 * @param scrollY - 当前Y坐标
	 */
	public static void overScrollBy(final PullToRefreshBase<?> view, final int deltaX, final int scrollX,
			final int deltaY, final int scrollY, final boolean isTouchEvent) {
		overScrollBy(view, deltaX, scrollX, deltaY, scrollY, 0, isTouchEvent);
	}

	/**
	 * 帮助类方法对于滚动事件封装了所有必须的方法。这个方法适用于控件需要指定一个滚动范围
	 * 并可以正确回滚到边缘。
	 * 
	 * @param view - 调用此方法的刷新控件.
	 * @param deltaX - X坐标变化值
	 * @param scrollX - 当前X坐标
	 * @param deltaY - Y坐标的变化值
	 * @param scrollY - 当前Y坐标
	 * @param scrollRange - 滚动范围
	 */
	public static void overScrollBy(final PullToRefreshBase<?> view, final int deltaX, final int scrollX,
			final int deltaY, final int scrollY, final int scrollRange, final boolean isTouchEvent) {
		overScrollBy(view, deltaX, scrollX, deltaY, scrollY, scrollRange, 0, DEFAULT_OVERSCROLL_SCALE, isTouchEvent);
	}

	/**
	 * 帮助类的方法对于滚动事件封装了所有必须的功能。
	 * 适用于所有版本
	 * 
	 * @param view - 调用此方法的刷新控件.
	 * @param deltaX - X坐标变化值
	 * @param scrollX - 当前X坐标
	 * @param deltaY - Y坐标的变化值
	 * @param scrollY - 当前Y坐标
	 * @param scrollRange - 滚动范围，特别是对于ScrollView
	 * @param fuzzyThreshold - 模糊阈值Threshold for which the values how fuzzy we
	 *            should treat the other values. Needed for WebView as it
	 *            doesn't always scroll back to it's edge. 0 = no fuzziness.
	 * @param scaleFactor - 比例因子对于滚动的总量
	 * @param isTouchEvent - 如果滚动操作是触摸事件通过overScrollBy调用的结果返回true
	 */
	public static void overScrollBy(final PullToRefreshBase<?> view, final int deltaX, final int scrollX,
			final int deltaY, final int scrollY, final int scrollRange, final int fuzzyThreshold,
			final float scaleFactor, final boolean isTouchEvent) {

		final int deltaValue, currentScrollValue, scrollValue;
		switch (view.getPullToRefreshScrollDirection()) {
			case HORIZONTAL:
				deltaValue = deltaX;
				scrollValue = scrollX;
				currentScrollValue = view.getScrollX();
				break;
			case VERTICAL:
			default:
				deltaValue = deltaY;
				scrollValue = scrollY;
				currentScrollValue = view.getScrollY();
				break;
		}

		// Check that OverScroll is enabled and that we're not currently
		// refreshing.
		if (view.isPullToRefreshOverScrollEnabled() && !view.isRefreshing()) {
			final Mode mode = view.getMode();

			// Check that Pull-to-Refresh is enabled, and the event isn't from
			// touch
			if (mode.permitsPullToRefresh() && !isTouchEvent && deltaValue != 0) {
				final int newScrollValue = (deltaValue + scrollValue);

				if (PullToRefreshBase.DEBUG) {
					Log.d(LOG_TAG, "OverScroll. DeltaX: " + deltaX + ", ScrollX: " + scrollX + ", DeltaY: " + deltaY
							+ ", ScrollY: " + scrollY + ", NewY: " + newScrollValue + ", ScrollRange: " + scrollRange
							+ ", CurrentScroll: " + currentScrollValue);
				}

				if (newScrollValue < (0 - fuzzyThreshold)) {
					// Check the mode supports the overscroll direction, and
					// then move scroll
					if (mode.showHeaderLoadingLayout()) {
						// If we're currently at zero, we're about to start
						// overscrolling, so change the state
						if (currentScrollValue == 0) {
							view.setState(State.OVERSCROLLING);
						}

						view.setHeaderScroll((int) (scaleFactor * (currentScrollValue + newScrollValue)));
					}
				} else if (newScrollValue > (scrollRange + fuzzyThreshold)) {
					// Check the mode supports the overscroll direction, and
					// then move scroll
					if (mode.showFooterLoadingLayout()) {
						// If we're currently at zero, we're about to start
						// overscrolling, so change the state
						if (currentScrollValue == 0) {
							view.setState(State.OVERSCROLLING);
						}

						view.setHeaderScroll((int) (scaleFactor * (currentScrollValue + newScrollValue - scrollRange)));
					}
				} else if (Math.abs(newScrollValue) <= fuzzyThreshold
						|| Math.abs(newScrollValue - scrollRange) <= fuzzyThreshold) {
					// Means we've stopped overscrolling, so scroll back to 0
					view.setState(State.RESET);
				}
			} else if (isTouchEvent && State.OVERSCROLLING == view.getState()) {
				// This condition means that we were overscrolling from a fling,
				// but the user has touched the View and is now overscrolling
				// from touch instead. We need to just reset.
				view.setState(State.RESET);
			}
		}
	}
	
	/**
	 * android标准滚动事件是否可用
	 * @param view 当前判断的控件
	 * @return 如果可用返回true
	 */
	static boolean isAndroidOverScrollEnabled(View view) {
		return view.getOverScrollMode() != View.OVER_SCROLL_NEVER;
	}
}
