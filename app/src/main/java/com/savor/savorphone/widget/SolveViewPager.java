package com.savor.savorphone.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.savor.savorphone.interfaces.OnChildMovingListener;
import com.savor.savorphone.interfaces.SetRecommend;

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
    private int  mode = 0;
    private boolean isRight = false;
    private SetRecommend obj;
    public SolveViewPager(Context context) {
        super(context);
    }
    public SolveViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setRecommend(SetRecommend obj){
        this.obj = obj;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float startX1 = 0;
        float endX1;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mode = 1;
                getParent().requestDisallowInterceptTouchEvent(true);// 不要拦截,
                // 这样是为了保证ACTION_MOVE调用
                startX = (int) ev.getRawX();
                startY = (int) ev.getRawY();
                startX1 = ev.getX();
                ev.getY();
                break;
            case MotionEvent.ACTION_UP:
//                startX = (int) ev.getRawX();
//                startY = (int) ev.getRawY();
                int X = startX;
                int Y = startY;
                endX1 = ev.getX();
                ev.getY();
                mode = 0;
                if (isRight == false) {
                     obj.setData(startX,endX1);
                }


                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode += 1;
                break;
            case MotionEvent.ACTION_MOVE:


                int endX = (int) ev.getRawX();
                int endY = (int) ev.getRawY();

                if (Math.abs(endX - startX) > Math.abs(endY - startY)) {// 左右滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                    if (endX > startX) {// 右划
                        isRight = true;
                    } else {// 左划
                        isRight = false;
                    }
                } else {// 上下滑动
//                    if (mode >= 2) {
                        getParent().requestDisallowInterceptTouchEvent(false); //不需要父控件拦截
//                    }else {
//                        getParent().requestDisallowInterceptTouchEvent(true); //需要父控件拦截
//                    }


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
