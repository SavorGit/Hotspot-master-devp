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

import android.view.View;
import android.view.animation.Interpolator;

import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.Mode;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.State;


/**
 * 下拉刷新base实现的接口
 * @author hezd
 *
 * @param <T>
 */
public interface IPullToRefresh<T extends View> {

	/**
	 * 演示下拉刷新的功能给使用者因此他们要注意这里。这对于第一次使用你app的人来说很有用。
	 * 动画仅仅会在刷新控件处于下拉刷新被用户手势触摸触发时发生。
	 * 
	 * @return true - 如果开启了demo模式返回true.
	 */
	public boolean demo();

	/**
	 * 获取刷新控件当前处于的状态 Mode
	 * 
	 * @return 返回当前处于的模式
	 */
	public Mode getCurrentMode();

	/**
	 * 返回触摸事件是否被过滤。如果为true，刷新控件只能在Y坐标变化大于X坐标时响应出没时间。
	 * 这意味着刷新控件将不会干扰水平滚动view使用比如viewpager
	 * 
	 * @return boolean - 如果控件触摸事件被过滤返回true
	 */
	public boolean getFilterTouchEvents();

	/**
	 * 返回一个代理对象，它允许你调用loading布局所有方法。
	 * 你应该仅仅在需要时保留方法返回结果。
	 * 
	 * @return 这个对象将代理所有在loading布局上的调用。
	 */
	public ILoadingLayout getLoadingLayoutProxy();

	/**
	 * 返回一个代理对象，它允许你调用loading布局的所有方法。真正的loadingLayouts将
	 * 受到传递参数的影响。
	 * 
	 *你应该仅仅在需要时保留方法返回结果。
	 * 
	 * @param includeStart - 是否包含头
	 * @param includeEnd - 是否包含尾
	 * @return 这个对象将代理所有LoadingLayouts包含的调用。
	 */
	public ILoadingLayout getLoadingLayoutProxy(boolean includeStart, boolean includeEnd);

	/**
	 * 获取到刷新控件被设置的模式。如果返回Mode.BOTH，你可以使用getCurrentMode()
	 * 去检查空间当前处于那种模式。
	 * 
	 * @return Mode 控件当前处于的模式
	 */
	public Mode getMode();

	/**
	 * 获取到包裹的被刷新的控件。返回的任何view对象都已经被添加到显示中间容器中。
	 * 
	 * @return 刷新控件包裹的被刷新view
	 */
	public T getRefreshableView();

	/**
	 * 被刷新的控件是否应该自动展示当刷新的时候，默认返回true。
	 * 
	 * @return - 如果被刷新view可显示返回true
	 */
	public boolean getShowViewWhileRefreshing();

	/**
	 * @return - 刷新控件当前处于的状态 
	 */
	public State getState();

	/**
	 * 下拉刷新是否可用
	 * 
	 * @return 如果可用返回true 
	 */
	public boolean isPullToRefreshEnabled();

	/**
	 * 是否支持滚动，这不同于android标准滚动（边缘光晕） ,从2.3版本开始支持。
	 *  
	 * @return 如果下拉刷新滚动和android内置滚动都可用返回true
	 */
	public boolean isPullToRefreshOverScrollEnabled();

	/**
	 * 判断刷新控件当前是否处于刷新状态
	 * 
	 * @return 如果正在刷新返回true 
	 */
	public boolean isRefreshing();

	/**
	 * 刷新时是否支持滚动
	 * 
	 * @return 如果刷新时可以滚动返回true.
	 */
	public boolean isScrollingWhileRefreshingEnabled();

	/**
	 * 标记当前刷新完成。将重置UI并且隐藏刷新view（header或footer）
	 */
	public void onRefreshComplete();

	/**
	 * 设置触摸事件是否被过滤。如果设置为true，控件将仅在Y坐标变化
	 * 大于X坐标时响应触摸事件。这意味着控件将不会干扰水平滚动的view
	 * 比如ViewPager，但是会约束手指滑动对view产生滚动效果。
	 * 
	 */
	public void setFilterTouchEvents(boolean filterEvents);

	/**
	 * 设置下拉刷新的模式
	 * 
	 * @param mode - 下拉刷新的模式
	 */
	public void setMode(Mode mode);

	/**
	 * 设置下拉刷新事件监听
	 * 
	 * @param listener - 控件被下拉时注册的监听 
	 */
	public void setOnPullEventListener(OnPullEventListener<T> listener);

	/**
	 * 设置刷新监听 
	 * 如果ptrMode定义为both，应该使用 setOnRefreshListener(OnRefreshListener2<T> listener)
	 *
	 * @param listener - 刷新时注册的监听
	 */
	public void setOnRefreshListener(OnRefreshListener<T> listener);

	/**
	 * 设置刷新监听
	 * 如果ptrMode定义为both，即允许从上而下和从下而上2个方向进行拉动刷新，
	 * 应该使用 public final void setOnRefreshListener(OnRefreshListener2<T> listener)  
	 * 这个函数来设定listener
	 * @param listener - 刷新时注册的监听
	 */
	public void setOnRefreshListener(OnRefreshListener2<T> listener);

	/**
	 * 设置是否支持滚动。这不同于android标准的滚动（边缘光晕）.这个设定仅仅
	 * 会对2.3以上系统产生影响。
	 * 
	 * @param enabled - 如果你想设置滚动可用true
	 */
	public void setPullToRefreshOverScrollEnabled(boolean enabled);

	/**
	 * 设置控件处于刷新状态。界面讲更新来显示正在刷新控件，并且滚动到可显示位置。
	 */
	public void setRefreshing();

	/**
	 * 设置控件处于刷新状态。界面讲更新来显示正在刷新控件，并且滚动到可显示位置。
	 * 
	 * @param doScroll - 如果你想要强制滚动来显示refreshing view
	 */
	public void setRefreshing(boolean doScroll);

	/**
	 * 设置用于滚动动画的动画篡改器，默认为DecelerateInterpolator。
	 * 
	 * @param interpolator - 使用的动画篡改器
	 */
	public void setScrollAnimationInterpolator(Interpolator interpolator);

	/**
	 * 刷新状态时控件是否可以滚动
	 * @param scrollingWhileRefreshingEnabled - 如果想要刷新时滚动设置为true
	 */
	public void setScrollingWhileRefreshingEnabled(boolean scrollingWhileRefreshingEnabled);

	/**
	 * Refreshing view是否应该被自动显示当啥U欣赏时
	 * @param showView
	 */
	public void setShowViewWhileRefreshing(boolean showView);

}