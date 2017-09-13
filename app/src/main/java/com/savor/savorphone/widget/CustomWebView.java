package com.savor.savorphone.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.savor.savorphone.R;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity.UpdateProgressListener;
import com.savor.savorphone.utils.ConstantValues;

import java.util.HashMap;

/**
 * 自定义webview加载网页
 * Created by wmm on 16/11/21.
 */
public class CustomWebView extends FrameLayout implements MyWebView.OnScrollBottomListener {
    private MyWebView mWebView;
    private ProgressBar mProgressBar;
    private UpdateProgressListener updateProgressListener;
    private boolean isLoadError;

    public CustomWebView(Context context) {
        this(context, null);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_webview, this, true);
        mWebView = (MyWebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mWebView.setWebViewClient(new MyClient());
//        mWebView.setWebChromeClient(new MyWebChromeClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setBlockNetworkImage(true);
        mWebView.setOnScrollBottomListener(this);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    /**
     * 滚动监听
     */
    public MyWebView.OnScrollBottomListener mListener;

    public void setOnScrollBottomListener(MyWebView.OnScrollBottomListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onScrollBottom() {
        if(mListener!=null) {
            mListener.onScrollBottom();
        }
    }

    public void resumeTimers() {
        mWebView.resumeTimers();
    }

//    private class MyWebChromeClient extends WebChromeClient {
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
//
////            if(newProgress >= 80&&!isLoadError &&AppUtils.isNetworkAvailable(getContext())){
////                if (mProgressBar.getVisibility() == VISIBLE){
////
////                }
////                if (updateProgressListener!=null){
////                    updateProgressListener.loadFinish();
////                    LogUtils.d(ConstantValues.LOG_WEBVIEWmProgressBar.setVisibility(GONE);_PREFIX+" 加载超过80%");
////                }
////            }
//        }
//
//    }

    public void goBack(){
        mWebView.goBack();
    }

    public boolean canGoBack(){
        return mWebView.canGoBack();
    }
    private class MyClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mWebView.getSettings().setBlockNetworkImage(false);
//            mProgressBar.setVisibility(VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (updateProgressListener!=null&&!isLoadError){
                updateProgressListener.loadFinish();
                mProgressBar.setVisibility(GONE);
//                LogUtils.d(ConstantValues.LOG_WEBVIEW_PREFIX+" 加载超过80%");
            }
            LogUtils.d(ConstantValues.LOG_WEBVIEW_PREFIX+" 加载完成");
        }



//        @Override
//        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
//            super.doUpdateVisitedHistory(view, url, isReload);
//            if (needClearHistory) {
//                needClearHistory = false;
//                mWebView.clearHistory();//清除历史记录
//           }
//        }

//        @Override
//        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//            super.onReceivedHttpError(view, request, errorResponse);
//            if (updateProgressListener!=null){
//                updateProgressListener.loadHttpError();
//            }
//        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            LogUtils.d("savor:webview "+error.getDescription());
            isLoadError = true;
            if (updateProgressListener!=null){
                updateProgressListener.loadHttpError();
            }
        }


    }


    public void loadUrl(String url, HashMap<String, String> headMap) {
        if (!TextUtils.isEmpty(url)) {
            if (headMap != null) {
                mWebView.loadUrl(url, headMap);
            } else {
                mWebView.loadUrl(url);
            }
        }
    }

    public String getUrl(){
        return mWebView.getUrl();
    }

    public void  clearHistory(){
        mWebView.clearCache(false);
        mWebView.clearHistory();
      //  mWebView.destroy();
    }
    public void loadUrl(String url, HashMap<String, String> headMap , UpdateProgressListener updateAnimListener) {
        isLoadError = false;
        if (!TextUtils.isEmpty(url)) {
            if (headMap != null) {
                mWebView.loadUrl(url, headMap);
            } else {
                mWebView.loadUrl(url);
            }
        }
        this.updateProgressListener = updateAnimListener;
    }



    public void onDestroy() {
        if(mWebView!=null) {
            mWebView.pauseTimers();
            mWebView.clearCache(true);
            mWebView.destroyDrawingCache();
            mWebView.destroy();
            mWebView = null;
        }
    }

}
