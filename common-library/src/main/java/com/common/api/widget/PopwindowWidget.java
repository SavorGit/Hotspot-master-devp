/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.common.api.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

/**
 * 
 * @ClassName: LoadingWidget
 * @Description: TODO(加载组件)
 * @author sun
 * @date 2014-4-24 下午6:12:42
 * 
 */
public class PopwindowWidget extends PopupWindow {
	private Activity activity;
	
	public PopwindowWidget(Activity activity, View view, OnDismissListener onDismissListener) {
		super(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		this.setOutsideTouchable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xB0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		this.setOnDismissListener(onDismissListener);
		
	}
	
	public PopwindowWidget(Activity activity, View view, OnDismissListener onDismissListener,
			int resId, int resId2) {
		this(activity, view, onDismissListener);
		if (resId != 0) {
			View v = view.findViewById(resId);
			if (v != null)
				v.setOnClickListener(clickListener);
		}
		
		if (resId2 != 0) {
			View v2 = view.findViewById(resId2);
			if (v2 != null)
				v2.setOnClickListener(clickListener);
		}
	}

	public PopwindowWidget(Activity activity, View view,int width,int height,boolean focusable) {
		this.activity=activity;
		// 设置SelectPicPopupWindow的View
		this.setContentView(view);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(width);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(height);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(focusable); 
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
	}
	
	public PopwindowWidget(Activity activity, View view,int width,int height,OnDismissListener onDismissListener) {
		this.activity=activity;
		// 设置SelectPicPopupWindow的View
		this.setContentView(view);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(width);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(height);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setOnDismissListener(onDismissListener);
	}
	
	public PopwindowWidget(Activity activity, View view,int width,int height,boolean focusable,int anim) {
		this.activity=activity;
		// 设置SelectPicPopupWindow的View
		this.setContentView(view);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(width);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(height);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(focusable);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(anim);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
	}
	
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			PopwindowWidget.this.dismiss();
			
		}
	};
	
}