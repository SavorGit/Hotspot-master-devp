package com.savor.savorphone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 *自定义scrollView.解决activity中onTouchEvent不执行
 * Created by bichao on 2017/7/13.
 */

public class MyScrollView extends ScrollView {
    public MyScrollView(Context context)
    {
        super(context);

    }

    public MyScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)   //这个方法如果返回 true 的话 两个手指移动，启动一个按下的手指的移动不能被传播出去。
    {
        super.onInterceptTouchEvent(event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)//这个方法如果 true 则整个Activity 的 onTouchEvent() 不会被系统回调
    {
        super.onTouchEvent(event);
        return false;
    }
}
