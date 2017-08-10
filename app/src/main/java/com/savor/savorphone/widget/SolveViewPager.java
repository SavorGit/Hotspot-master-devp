package com.savor.savorphone.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.savor.savorphone.interfaces.OnChildMovingListener;

/**
 * Created by bushlee on 2017/8/8.
 */

public class SolveViewPager extends ViewPager implements OnChildMovingListener {

    int startX;
    int startY;
    boolean upeable = true;    //上滑事件默认需要true
    boolean downeable = true; //下滑事件默认需要true
    boolean leftrighable = false; // 左右滑动事件是否需要父控件拦截    默认不需要 false
    private boolean mChildIsBeingDragged=false; /**  当前子控件是否处理拖动状态  */
    public SolveViewPager(Context context) {
        super(context);
    }
    public SolveViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);// 不要拦截,
                // 这样是为了保证ACTION_MOVE调用
                startX = (int) ev.getRawX();
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:

                int endX = (int) ev.getRawX();
                int endY = (int) ev.getRawY();

                if (Math.abs(endX - startX) > Math.abs(endY - startY)) {// 左右滑动
                    getParent().requestDisallowInterceptTouchEvent(!leftrighable); //不需要父控件拦截
//                    if (endX > startX) {// 右划
//                        if (getCurrentItem() == 0) {// 第一个页面, 需要父控件拦截
//                            getParent().requestDisallowInterceptTouchEvent(!rightable);
//                        }
//                    } else {// 左划
//                        if (getCurrentItem() == getAdapter().getCount() - 1) {// 最后一个页面,
//                            // 需要拦截
//                            getParent().requestDisallowInterceptTouchEvent(!leftable);
//                        }
//                    }
                } else {// 上下滑动
                    getParent().requestDisallowInterceptTouchEvent(true); //需要父控件拦截
                }

                break;
        }

        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent arg0) {
//        if(mChildIsBeingDragged)
//            return false;
//        return super.onInterceptTouchEvent(arg0);
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    @Override
    public void startDrag() {
        mChildIsBeingDragged = true;
    }

    @Override
    public void stopDrag() {
        mChildIsBeingDragged=false;
    }
}
