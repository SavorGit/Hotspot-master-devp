package com.savor.savorphone.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.OrientationEventListener;

import com.savor.savorphone.R;

/**
 * Created by hezd on 2016/12/23.
 */

public class ScreenOrientationUtil {

    private int mOrientation;
    private OrientationEventListener mOrEventListener;

    private int mOrientation1;
    private OrientationEventListener mOrEventListener1;

    private Activity mActivity;

    private static ScreenOrientationUtil instance = new ScreenOrientationUtil();
    private OnSensorChangeListener mSensorChangeListener;

    public static ScreenOrientationUtil getInstance(){
        return instance;
    }

    public void start(Activity activity){
        this.mActivity = activity;
        if(mOrEventListener == null){
            initListener();
        }
        mOrEventListener.enable();
    }

    public ScreenOrientationUtil listener(OnSensorChangeListener listener) {
            this.mSensorChangeListener = listener;
        return getInstance();
    }


    public void stop(){
        if(mOrEventListener != null){
            mOrEventListener.disable();
        }
        if(mOrEventListener1 != null){
            mOrEventListener1.disable();
        }
    }

    private void initListener(){
        mOrEventListener = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int rotation) {
//                Log.e("test", "屏幕方向 "+rotation);
                if (rotation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                int orientation = convert2Orientation(rotation);
                // 方向没有变化,跳过
                if (orientation == mOrientation) {
                    return;
                }
                RecordUtils.onEvent(mActivity,mActivity.getString(R.string.details_page_rotating_screen));
                mOrientation = orientation;
                if(mOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {

                    mActivity.setRequestedOrientation(mOrientation);
                }

                if(mSensorChangeListener!=null) {
                    switch (mOrientation) {
                        case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
                                mSensorChangeListener.onExpand();
                            break;
                        case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                                mSensorChangeListener.onShink();
                            break;
                    }
                }
            }
        };

        mOrEventListener1 = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int rotation) {
                if (rotation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                int orientation = convert2Orientation(rotation);
                // 方向没有变化,跳过
                if (orientation == mOrientation1) {
                    return;
                }
                mOrientation1 = orientation;
                if(mOrientation1 == mOrientation){
                    mOrEventListener1.disable();
                    mOrEventListener.enable();
                }
            }
        };
    }

    public boolean isPortrait(){
        if(mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || mOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
            return true;
        }
        return false;
    }

    public int getOrientation(){
        return mOrientation;
    }

    public void toggleScreen(){
        mOrEventListener.disable();
        mOrEventListener1.enable();
        int orientation = 0 ;
        if(mOrientation ==  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }else if(mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }else if(mOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }else if(mOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }
        mOrientation = orientation;
        mActivity.setRequestedOrientation(mOrientation);
    }

    private int convert2Orientation(int rotation){
        int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315)) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else if ((rotation > 45) && (rotation <= 135)) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        } else if ((rotation > 135) && (rotation <= 225)) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        } else if ((rotation > 225) && (rotation <= 315)) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        return orientation;
    }

    interface OnSensorChangeListener {
        /**横屏*/
        void onExpand();
        /**竖屏*/
        void onShink();
    }
}
