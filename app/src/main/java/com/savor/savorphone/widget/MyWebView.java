package com.savor.savorphone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity;

/**
 * Created by hezd on 2016/12/30.
 */

public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);

        float webcontent = getContentHeight() * getScale();// webview的高度
        float webnow = getHeight() + getScrollY();// 当前webview的高度
//        Log.i("TAG1", "webview.getScrollY()====>>" + getScrollY());
        if (Math.abs(webcontent - webnow) <= 1) {
            // 已经处于底端
            // Log.i("TAG1", "已经处于底端");
            if (mListener!=null) {
                mListener.onScrollBottom();
            }
//            listener.onPageEnd(l, t, oldl, oldt);
        }
    }
//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);
//        //WebView的总高度
//        float webViewContentHeight = getContentHeight() * getScale();
//        //WebView的现高度
//        float webViewCurrentHeight = (getHeight() + getScrollY());
//        //判断WebView是否滚动到底部
//        if (webViewContentHeight - webViewCurrentHeight == 0 && mListener != null) {
//            mListener.onScrollBottom();
//        }
//    }

    /**
     * 滚动监听
     */
    public OnScrollBottomListener mListener;

    public void setOnScrollBottomListener(OnScrollBottomListener listener) {
        this.mListener = listener;
    }



    public interface OnScrollBottomListener {
        void onScrollBottom();
    }
}
