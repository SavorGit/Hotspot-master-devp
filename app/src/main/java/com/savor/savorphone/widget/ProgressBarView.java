package com.savor.savorphone.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savor.savorphone.R;


/**
 * 进度条
 */
public class ProgressBarView extends RelativeLayout implements OnClickListener {

    enum ProgressBarState {
        SUCCESS,
        EMPTY,
        FAILED,
        NO_NET,
    }

    private ProgressBarState mState = ProgressBarState.SUCCESS;

    private ProgressBarViewClickListener mBarViewClickListener;
    private Context mContext;
    private RelativeLayout mBackgroundRl;
    private ImageView mLoadingIv;
    private ProgressBar mProgressBar;
    private ImageView mErrorIv;
    private ImageView mFirstTipsIV;
    private TextView mFirstTipsTv;
    private TextView mSecondTipsTv;
    private RelativeLayout mLoadingRl;
    private AnimationDrawable mAnimationDrawable;


    public ProgressBarView(Context context) {
        this(context, null);
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_progress_bar_view, this);
        if (isInEditMode())
            return;

        initViews();
        setListeners();
    }

    private void initViews() {
        mBackgroundRl = (RelativeLayout) findViewById(R.id.backgroundRL);
//        mLoadingIv = (ImageView) findViewById(R.id.loadingIV);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        mErrorIv = (ImageView) findViewById(R.id.errorIV);
        mFirstTipsIV = (ImageView) findViewById(R.id.tipImageView);
        mFirstTipsTv = (TextView) findViewById(R.id.tipTextView);
        mSecondTipsTv = (TextView) findViewById(R.id.nextActivityButton);
        mLoadingRl = (RelativeLayout) findViewById(R.id.loadingRL);
    }

    private void setListeners() {
        mBackgroundRl.setOnClickListener(this);
    }

    /**
     * 开始加载
     */
    public void startLoading() {
        startLoading(null);
    }
    /**
     * 开始加载
     */
    public void startLoading(String tips) {
//		mLoadingIv.setBackgroundResource(R.drawable.progress_loading);
//        mAnimationDrawable = (AnimationDrawable) mLoadingIv.getBackground();
//        mAnimationDrawable.start();
        mState = ProgressBarState.SUCCESS;
        setVisibility(View.VISIBLE);
        mFirstTipsIV.setVisibility(View.VISIBLE);
//        tips = TextUtils.isEmpty(tips) ? mContext.getResources().getString(R.string.progressbar_loading) : tips;
//        mFirstTipsTv.setText(tips);
        setStatusVisible(true, false, false, false,true);
    }

    /**
     * 加载完毕，数据为空
     */
    public void loadEmpty() {
        loadEmpty(null);
    }

    public void loadEmpty(String firstTips) {
        loadEmpty(firstTips, null);
    }

    /**
     * 加载完毕，数据为空
     *
     * @param firstTips
     * @param secondTips
     */
    public void loadEmpty(String firstTips, String secondTips) {
        loadEmpty(firstTips, secondTips, 0);
    }
    public void loadEmpty(String firstTips, String secondTips, int failImgResId) {
        setVisibility(View.VISIBLE);
        mState = ProgressBarState.EMPTY;
        firstTips = TextUtils.isEmpty(firstTips) ? mContext.getResources().getString(R.string.progressbar_repeatload) : firstTips;
        if (failImgResId == 0) {
            mErrorIv.setImageResource(R.drawable.ico_load_error);
        } else {
            mErrorIv.setImageResource(failImgResId);
        }
        mFirstTipsTv.setText(firstTips);
        if (TextUtils.isEmpty(secondTips)){
            mSecondTipsTv.setVisibility(View.GONE);
        }else{
            mSecondTipsTv.setVisibility(View.VISIBLE);
            mSecondTipsTv.setText(secondTips);
        }
        setStatusVisible(false, true, !TextUtils.isEmpty(firstTips), false,!TextUtils.isEmpty(secondTips));
    }

    /**
     * 没有网络，数据加载失败
     */
    public void loadFailureNoNet() {
        setVisibility(View.VISIBLE);
        mState = ProgressBarState.NO_NET;
        mErrorIv.setImageResource(R.drawable.ico_load_error);
        mFirstTipsTv.setText(mContext.getResources().getString(R.string.progressbar_notnet));
        mSecondTipsTv.setText(mContext.getResources().getString(R.string.progressbar_repeatload));
        setStatusVisible(false, true, true, false,false);
    }

    /**
     * 数据加载失败，可能是数据分析，或者服务器内部异常
     */
    public void loadFailure() {
        loadFailure(null);
    }

    public void loadFailure(String firstTips) {
        loadFailure(firstTips, null);
    }

    public void loadFailure(String firstTips, String secondTips) {
        loadFailure(firstTips, secondTips, 0);
    }
    public void loadFailure(String firstTips, String secondTips, int failImgResId) {
        setVisibility(View.VISIBLE);
        mState = ProgressBarState.FAILED;
        if (TextUtils.isEmpty(firstTips)){
            firstTips = TextUtils.isEmpty(firstTips) ? mContext.getResources().getString(R.string.progressbar_repeatload) : firstTips;
        }
        if (failImgResId == 0) {
            mErrorIv.setImageResource(R.drawable.ico_load_error);
        } else {
            mErrorIv.setImageResource(failImgResId);
        }
        mFirstTipsTv.setText(firstTips);
//        mFirstTipsTv.setTextColor(getResources().getColor(R.color.network_hint));
        if (!TextUtils.isEmpty(secondTips)){
            mSecondTipsTv.setText(secondTips);
        }
        setStatusVisible(false, true, true,!TextUtils.isEmpty(secondTips),false);
    }

    /**
     * 加载成功
     */
    public void loadSuccess() {
        mState = ProgressBarState.SUCCESS;
        setVisibility(View.GONE);
    }

    private void setStatusVisible(boolean showLoadingRL, boolean showErrorIv, boolean showFirstTips,boolean showSecondTips,boolean showFirstIV) {
        mLoadingRl.setVisibility(showLoadingRL ? View.VISIBLE : View.GONE);
        mErrorIv.setVisibility(showErrorIv ? View.VISIBLE : View.GONE);
        mFirstTipsTv.setVisibility(showFirstTips ? View.VISIBLE : View.GONE);
        mSecondTipsTv.setVisibility(showSecondTips ? View.VISIBLE : View.GONE);
        mFirstTipsIV.setVisibility(showFirstIV ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextActivityButton || v.getId() == R.id.backgroundRL) {
            switch (mState) {
                case EMPTY:
                    if (mBarViewClickListener != null) {
                        mBarViewClickListener.loadDataEmpty();
                    }
                    break;
                case NO_NET://没有网络加载失败
                    if (mBarViewClickListener != null) {
                        mBarViewClickListener.loadFailureNoNet();
                    }
                    break;
                case FAILED://非网络加载失败
                    if (mBarViewClickListener != null) {
                        mBarViewClickListener.loadFailure();
                    }
                    break;
            }
        }
    }

    public ProgressBarViewClickListener getBarViewClickListener() {
        return mBarViewClickListener;
    }

    /**
     * 设置按钮的监听事件
     *
     * @param barViewClickListener
     */
    public void setProgressBarViewClickListener(ProgressBarViewClickListener barViewClickListener) {
        this.mBarViewClickListener = barViewClickListener;
    }

    public interface ProgressBarViewClickListener {
        void loadDataEmpty();

        void loadFailureNoNet();

        void loadFailure();
    }

}
