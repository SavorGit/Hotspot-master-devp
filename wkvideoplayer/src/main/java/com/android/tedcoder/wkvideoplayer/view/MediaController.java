package com.android.tedcoder.wkvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.tedcoder.wkvideoplayer.R;
import com.android.tedcoder.wkvideoplayer.model.Video;
import com.android.tedcoder.wkvideoplayer.model.VideoUrl;
import com.android.tedcoder.wkvideoplayer.util.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Ted on 2015/8/4.
 * MediaController
 */
public class MediaController extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private ImageView mPlayImg;//播放按钮
    private SeekBar mProgressSeekBar;//播放进度条
    private TextView mTimeTxt;//播放时间
    private ImageView mExpandImg;//最大化播放按钮
    private ImageView mShrinkImg;//缩放播放按钮
    private EasySwitcher mVideoFormatSwitcher;//视频清晰度切换器
    private View mMenuView;
    private View mMenuViewPlaceHolder;

    private MediaControlImpl mMediaControl;
    private TextView mEndtimeTv;

    public static final int ORITATION_PORTAIT = 0x1;
    public static final int ORITATION_LANDSCAPE = 0x2;
    private int mOrietation = ORITATION_PORTAIT;
    private PlayState mPlayState;
    private OnChangeFormatListener mOnChangeFormatListener;
    private OnPlayBtnClickListener mOnplayBtnClickListener;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
        if (isFromUser)
            mMediaControl.onProgressTurn(ProgressState.DOING, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mMediaControl.onProgressTurn(ProgressState.START, 0);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaControl.onProgressTurn(ProgressState.STOP, 0);
    }

    private EasySwitcher.EasySwitcherCallbackImpl mFormatSwitcherCallback = new EasySwitcher.EasySwitcherCallbackImpl() {
        @Override
        public void onSelectItem(int position, String name) {
            if(mOnChangeFormatListener!=null) {
                mOnChangeFormatListener.onChangeFormat(position);
            }
            mMediaControl.onSelectFormat(position);
        }

        @Override
        public void onShowList() {
            mMediaControl.alwaysShowController();
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.pause) {
            if(mOnplayBtnClickListener!=null) {
                mOnplayBtnClickListener.onPlayBtnClick();
            }
            mMediaControl.onPlayTurn();
        } else if (view.getId() == R.id.expand) {
            mMediaControl.onPageTurn();
        } else if (view.getId() == R.id.shrink) {
            mMediaControl.onPageTurn();
        }
    }

    public void initPlayVideo(Video video) {
        ArrayList<String> format = new ArrayList<>();
        for (VideoUrl url : video.getVideoUrl()) {
            format.add(url.getFormatName());
        }
        mVideoFormatSwitcher.initData(format);
    }

    public void closeAllSwitchList() {
        mVideoFormatSwitcher.closeSwitchList();
    }

    /**
     * 初始化精简模式
     */
    public void initTrimmedMode() {
        mMenuView.setVisibility(GONE);
        mMenuViewPlaceHolder.setVisibility(GONE);
        mExpandImg.setVisibility(INVISIBLE);
        mShrinkImg.setVisibility(INVISIBLE);
    }

    /***
     * 强制横屏模式
     */
    public void forceLandscapeMode() {
        mExpandImg.setVisibility(INVISIBLE);
        mShrinkImg.setVisibility(INVISIBLE);
    }


    public void setProgressBar(int progress, int secondProgress) {
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;
        if (secondProgress < 0) secondProgress = 0;
        if (secondProgress > 100) secondProgress = 100;
        mProgressSeekBar.setProgress(progress);
        mProgressSeekBar.setSecondaryProgress(secondProgress);
    }

    public void setPlayState(PlayState playState) {
        this.mPlayState = playState;
        if(mOrietation == ORITATION_PORTAIT) {
            mPlayImg.setImageResource(mPlayState.equals(PlayState.PLAY) ? R.drawable.sp_bofang : R.drawable.sp_zanting);
        }else {
            mPlayImg.setImageResource(mPlayState.equals(PlayState.PLAY) ? R.drawable.sp_bofang : R.drawable.sp_zanting);

        }
    }

    public PlayState getPlayState() {
        return mPlayState;
    }

    public void refreshPlayBtnState(int orietation) {
        this.mOrietation = orietation;
        if(orietation == ORITATION_PORTAIT) {
            mPlayImg.setImageResource(mPlayState.equals(PlayState.PLAY) ? R.drawable.sp_bofang : R.drawable.sp_zanting);
        }else {
            mPlayImg.setImageResource(mPlayState.equals(PlayState.PLAY) ? R.drawable.sp_bofang : R.drawable.sp_zanting);

        }
    }

    public void setPageType(PageType pageType) {
        //EXPAND展开，SHRINK收缩
        mExpandImg.setVisibility(pageType.equals(PageType.EXPAND) ? GONE : VISIBLE);
        mShrinkImg.setVisibility(pageType.equals(PageType.SHRINK) ? GONE : VISIBLE);
        mMenuView.setVisibility(pageType.equals(PageType.SHRINK) ? GONE : VISIBLE);
        mMenuViewPlaceHolder.setVisibility(pageType.equals(PageType.SHRINK) ? GONE : VISIBLE);
    }

    public void setPlayProgressTxt(int nowSecond, int allSecond) {
        String playtime = TimeUtils.formatPlayTime(nowSecond);
        String totaltime = TimeUtils.formatPlayTime(allSecond);
        mTimeTxt.setText(playtime);
        if(allSecond>0)
            mEndtimeTv.setText(totaltime);
    }

    public void setDuration(int endtime) {
        if(endtime>0) {
            String totaltime = TimeUtils.formatPlayTime(endtime*1000);
            mEndtimeTv.setText(totaltime);
        }
    }

    public void playFinish(int allTime) {
        mProgressSeekBar.setProgress(100);
        setPlayProgressTxt(allTime, allTime);
        setPlayState(PlayState.PAUSE);
    }

    public void setMediaControl(MediaControlImpl mediaControl) {
        mMediaControl = mediaControl;
    }

    public MediaController(Context context) {
        super(context);
        initView(context);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.biz_video_media_controller, this);
        mPlayImg = (ImageView) findViewById(R.id.pause);
        mProgressSeekBar = (SeekBar) findViewById(R.id.media_controller_progress);
        mVideoFormatSwitcher = (EasySwitcher) findViewById(R.id.video_format_switcher);
        mTimeTxt = (TextView) findViewById(R.id.time);
        mEndtimeTv = (TextView) findViewById(R.id.tv_endtime);
        mExpandImg = (ImageView) findViewById(R.id.expand);
        mShrinkImg = (ImageView) findViewById(R.id.shrink);
        mMenuView = findViewById(R.id.view_menu);
        mMenuViewPlaceHolder = findViewById(R.id.view_menu_placeholder);
        initData();
    }

    private void initData() {
        mProgressSeekBar.setOnSeekBarChangeListener(this);
        mPlayImg.setOnClickListener(this);
        mShrinkImg.setOnClickListener(this);
        mExpandImg.setOnClickListener(this);
        setPageType(PageType.SHRINK);
        setPlayState(PlayState.PAUSE);
        mVideoFormatSwitcher.setEasySwitcherCallback(mFormatSwitcherCallback);
    }

    /**
     * 播放样式 展开、缩放
     */
    public enum PageType {
        EXPAND, SHRINK
    }

    /**
     * 播放状态 播放 暂停
     */
    public enum PlayState {
        PLAY, PAUSE
    }

    public enum ProgressState {
        START, DOING, STOP
    }

    /**
     * 切换视频清晰度监听
     * @param listener
     */
    public void setOnFormatChangeListener(OnChangeFormatListener listener) {
        this.mOnChangeFormatListener = listener;
    }

    /**
     * 切换视频清晰度
     */
    public interface OnChangeFormatListener {
        void onChangeFormat(int position);
    }

    public void setOnPlayBtnClickListener(OnPlayBtnClickListener listener) {
        this.mOnplayBtnClickListener = listener;
    }

    public interface MediaControlImpl {
        void onPlayTurn();

        void onPageTurn();

        void onProgressTurn(ProgressState state, int progress);

        void onSelectFormat(int position);

        void alwaysShowController();
    }

    public interface OnPlayBtnClickListener {
        void onPlayBtnClick();
    }
}
