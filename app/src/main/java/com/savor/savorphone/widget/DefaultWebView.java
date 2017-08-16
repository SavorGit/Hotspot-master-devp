package com.savor.savorphone.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.common.api.utils.AppUtils;
import com.savor.savorphone.R;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity.UpdateProgressListener;

import java.util.HashMap;

/**
 * 自定义webview加载网页
 * Created by wmm on 16/11/21.
 */
public class DefaultWebView extends FrameLayout implements MyWebView.OnScrollBottomListener {
    private MyWebView mWebView;
    private ProgressBar mProgressBar;
    private UpdateProgressListener updateProgressListener;
    private boolean isLoadError;


    public DefaultWebView(Context context) {
        this(context, null);
    }

    public DefaultWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_webview_default, this, true);
        mWebView = (MyWebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setVisibility(VISIBLE);
        mWebView.setWebViewClient(new MyClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
        webSettings.setBlockNetworkImage(true);
        mWebView.setOnScrollBottomListener(this);
//        mWebView.setLayerType(View.La, null);
//        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        mWebView.setVerticalScrollBarEnabled(true);
//        mWebView.setHorizontalScrollBarEnabled(false);
         webSettings.setSupportZoom(false);
//        webSettings.setBlockNetworkImage(true);
         webSettings.setDomStorageEnabled(true);
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    public void goBack(){
        mWebView.goBack();
    }

    public boolean canGoBack(){
        return mWebView.canGoBack();
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

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            if(newProgress >= 80&&!isLoadError &&AppUtils.isNetworkAvailable(getContext())){
                if (mProgressBar.getVisibility() == VISIBLE){
                    mProgressBar.setVisibility(GONE);
                }
                if (updateProgressListener!=null){
                    updateProgressListener.loadFinish();
                }
            }
        }

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
            super.onPageFinished(view, url);
        }

        public boolean shouldOverrideUrlLoading(WebView view,String url){
            // mWebView.loadUrl(url,null,updateProgressListener);
            loadUrl(url,null,updateProgressListener);
            return true;

        }
//        @Override
//        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//            super.onReceivedHttpError(view, request, errorResponse);
//            if (updateProgressListener!=null){
//                updateProgressListener.loadHttpError();
//            }
//        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
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
