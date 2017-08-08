package com.common.api.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * http://stackoverflow.com/questions/3495890/how-can-i-put-a-listview-into-a-scrollview-without-it-collapsing/3495908#3495908
 * @author XYZ
 * http://stackoverflow.com/users/1179638/xyz
 * 
 * 确保在ScrollView内部的控件可以处理自己的Event，比如ListView，可以实现滑动，或者其它自定义的控件，如缩放效果的实现。
 */
public class VerticalScrollview extends ScrollView{

	private boolean isOpenSwitch  = true;
	
	public VerticalScrollview(Context context) {
        super(context);
    }

     public VerticalScrollview(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public VerticalScrollview(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
        
    public boolean isOpenSwitch() {
		return isOpenSwitch;
	}

	public void setOpenSwitch(boolean isOpenSwitch) {
		this.isOpenSwitch = isOpenSwitch;
	}

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
//                    Log.i("VerticalScrollview", "onInterceptTouchEvent: DOWN super false" );
                    super.onTouchEvent(ev);
                    break;

            case MotionEvent.ACTION_MOVE:
            	if(isOpenSwitch)
                    return false; // redirect MotionEvents to ourself

            case MotionEvent.ACTION_CANCEL:
//                    Log.i("VerticalScrollview", "onInterceptTouchEvent: CANCEL super false" );
                    super.onTouchEvent(ev);
                    break;

            case MotionEvent.ACTION_UP:
//                    Log.i("VerticalScrollview", "onInterceptTouchEvent: UP super false" );
            	if(isOpenSwitch)
                    return false;

//            default: Log.i("VerticalScrollview", "onInterceptTouchEvent: " + action ); break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
//        Log.i("VerticalScrollview", "onTouchEvent. action: " + ev.getAction() );
         return true;
    }
}