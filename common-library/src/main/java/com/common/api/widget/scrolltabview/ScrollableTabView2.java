/*
 * Copyright (C) 2011 Andreas Stuetz <andreas.stuetz@gmail.com>
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

package com.common.api.widget.scrolltabview;

import java.util.ArrayList;
import java.util.List;

import com.common.api.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * I'm using a custom tab view in place of ActionBarTabs entirely for the theme
 * engine.
 */
public class ScrollableTabView2 extends HorizontalScrollView{

    protected static final String TAG = ScrollableTabView2.class.getSimpleName();

    private TabAdapter mAdapter = null;

    private final LinearLayout mContainer;

    private final ArrayList<View> mTabs = new ArrayList<View>();

	private List<String> mTabList;
	
	private int mCurrentItem;

	private onRefreshListener mRefreshListener;

	
	public ScrollableTabView2(Context context) {
        this(context, null);
    }

    public ScrollableTabView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableTabView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        this.setHorizontalScrollBarEnabled(false);
        this.setHorizontalFadingEdgeEnabled(false);

        mContainer = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(params);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);

        this.addView(mContainer);

    }

    public void setAdapter(TabAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void setOnRefreshListener(onRefreshListener refreshListener) {
    	this.mRefreshListener = refreshListener;
    }
    
    public void setTabList(List<String> tabList) {
    	this.mTabList = tabList;
    	initTabs();
    }

    public int getmCurrentItem() {
 		return mCurrentItem;
 	}

 	public void setmCurrentItem(int mCurrentItem) {
 		this.mCurrentItem = mCurrentItem;
 	}
    
    private void initTabs() {

        mContainer.removeAllViews();
        mTabs.clear();

        if (mAdapter == null)
            return;

        for (int i = 0; i < mTabList.size(); i++) {

            final int index = i;
            View tab = mAdapter.getView(i);
            mContainer.addView(tab);
            tab.setFocusable(true);
            mTabs.add(tab);
            
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	mRefreshListener.refreshList(mTabList.get(index));
                	selectTab(index);
                }
            });

        }

        selectTab(getmCurrentItem());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void selectTab(int position) {

        for (int i = 0, pos = 0; i < mContainer.getChildCount(); i ++ , pos++) {
            View tab = mContainer.getChildAt(i);
            tab.setSelected(pos == position);
        }
        
        View selectedTab = mContainer.getChildAt(position);
        final int w = selectedTab.getMeasuredWidth();
        final int l = selectedTab.getLeft();

        final int x = l - this.getWidth() / 2 + w / 2;
        smoothScrollTo(x, this.getScrollY());
    }
    /**
     * 获取被点击的TabView里面所有的View
     * @return
     */
	public ArrayList<View> getTabViews() {
		return mTabs;
	}
    
    
}
