package com.savor.savorphone.interfaces;

import android.widget.AbsListView;

/**
 * 记录停止滑动时当前显示条目
 * Created by wmm on 2016/11/30.
 */

public class OnScrollVisibleListener implements AbsListView.OnScrollListener {

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        // 判断是否是停止滚动状态
        if (scrollState == SCROLL_STATE_IDLE) {
            //获取停止时第一个可见item位置
            int firstVisiblePosition = absListView.getFirstVisiblePosition();
            //获取停止时最后一个可见item位置
            int lastVisiblePosition = absListView.getLastVisiblePosition();
            //去除SwipeListView刷新头
            if (firstVisiblePosition == 0 || firstVisiblePosition == lastVisiblePosition) {
                return;
            }
            //添加监听回馈
            if (mListener != null) {
                mListener.onStopVisibleItem(firstVisiblePosition, lastVisiblePosition);
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    //停止滑动时可见item监听器
    private OnStopScrollVisibleListener mListener;

    public void setOnStopScrollVisibleListener(OnStopScrollVisibleListener listener) {
        mListener = listener;
    }

    public interface OnStopScrollVisibleListener {
        void onStopVisibleItem(int firstVisiblePosition, int lastVisiblePosition);
    }
}
