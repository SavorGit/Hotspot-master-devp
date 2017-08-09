package com.savor.savorphone.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.tedcoder.wkvideoplayer.model.Video;
import com.android.tedcoder.wkvideoplayer.model.VideoUrl;
import com.android.tedcoder.wkvideoplayer.util.TimeUtils;
import com.android.tedcoder.wkvideoplayer.view.MediaController;
import com.android.tedcoder.wkvideoplayer.view.SuperVideoPlayer;
import com.android.tedcoder.wkvideoplayer.view.SuperVideoView;
import com.bumptech.glide.Glide;
import com.common.api.utils.AppUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.adapter.RecommendListAdapter;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.ScreenOrientationUtil;
import com.savor.savorphone.utils.ShareManager;
import com.savor.savorphone.utils.UmengContact;
import com.savor.savorphone.widget.CustomWebView;
import com.savor.savorphone.widget.MyWebView;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.ProgressBarView.ProgressBarViewClickListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 非餐厅环境视频详情界面（未连接电视，阅读）
 * Created by hezd on 2016/12/14.
 */

public class VideoPlayVODNotHotelActivity extends BasePlayActivity implements View.OnClickListener,ProgressBarViewClickListener,CopyCallBack {
    private static final java.lang.String TAG = VideoPlayVODNotHotelActivity.class.getSimpleName();
    private final int TIME_GONE = 0;
    private final int TIME_FORWARD = 1;
    private final int TIME_BACKWARD = 2;
    /**查询视频播放进度*/
    private static final int QUERY_SEEK = 0x1;
    private LinearLayout headLayout;
    private ImageView iv_left;
    private ImageView iv_right;
    private ImageView toleft_iv_right;
    private ScrollView scrollviewLayout;
    private CustomWebView mCustomWebView;
    private LinearLayout shareLayout;
    private ImageView shareWeixinIV;
    private ImageView shareFriendsIV;
    private ImageView shareQqIV;
    private ImageView shareWeiboIV;
    private LinearLayout recommendLayuot;
    private ListView recommend_listview;
    private RecommendListAdapter recommendListAdapter=null;
    private List<CommonListItem> list = new ArrayList<>();
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    private View mGestureView;
    private TextView mTvUpdateTime;
    private View mFinishLayer;
    private SuperVideoPlayer mSuperVideoPlayer;
    private GestureDetector gestureDetector;
    private AudioManager mAudioManager;
    private float mStartY;
    private float mStartX;
    private long mStartTime;
    private HandlerThread mHanderThread;
    private Handler mCutHandler;
    private int currentSeconds;

    private Video video;
    private int currentMsec;
    private TextView mReplayBtn;
    private TextView mShareBtn;
    private RelativeLayout mPlayerLayout;
    private String projectId;
    private String UUID;
    private String custom_volume;
    private int volume;
    private String category_id="0";

    private CommonListItem videoItem;
    /**是否收藏,1:已收藏,2:未收藏**/
    private String collected="-1";

    private ProgressBarView mProgressLayout;
    private ShareManager mShareManager;
    private ShareManager.CustomShareListener mShareListener;

    public ProgressBarView allProgressLayuot;
    private boolean isPlaying;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_vod_not_hotel);
        handleIntent();

        ScreenOrientationUtil.getInstance().start(this);
        getViews();
        setViews();
        setListeners();
        contentIsOnline();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
        contentIsOnline();
    }

    public static void startVODVideoActivity(Activity context, CommonListItem vodBean) {
        Intent vodIntent = new Intent(context, VideoPlayVODNotHotelActivity.class);
        vodIntent.putExtra("voditem", vodBean);
        context.startActivity(vodIntent);
    }
    private void handleIntent() {
        videoItem = (CommonListItem) getIntent().getSerializableExtra("voditem");
        category_id = videoItem.getCategoryId();
        initCustom_volume();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id",category_id);
        RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page),hashMap);
        HashMap<String,String> hashMap1 = new HashMap<>();
        hashMap1.put("time",mStartTime+"");
        RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_begin_reading),hashMap1);
    }

    private void initCustom_volume(){
       volume = videoItem.getType();
        switch (volume){
            case 0://文本
                custom_volume = "text";
                break;
            case 1://图文
                custom_volume = "pictext";
                break;
            case 2://图集
                custom_volume = "pic";
                break;
            case 3://视频
                custom_volume = "video";
            case 4://视频
                custom_volume = "video";
                break;
        }

    }
    @Override
    public void getViews() {
        headLayout = (LinearLayout) findViewById(R.id.head_layout);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        toleft_iv_right = (ImageView) findViewById(R.id.toleft_iv_right);
        mPlayerLayout = (RelativeLayout) findViewById(R.id.layout_player);
        mReplayBtn = (TextView) findViewById(R.id.replay);
        mShareBtn = (TextView) findViewById(R.id.share_finish);
        scrollviewLayout = (ScrollView) findViewById(R.id.scrollview_layout);
        mCustomWebView = (CustomWebView) findViewById(R.id.webview_custom);
        shareLayout = (LinearLayout) findViewById(R.id.share_layout);
        shareWeixinIV = (ImageView) findViewById(R.id.share_weixin);
        shareFriendsIV = (ImageView) findViewById(R.id.share_friends);
        shareQqIV = (ImageView) findViewById(R.id.share_qq);
        shareWeiboIV = (ImageView) findViewById(R.id.share_weibo);
        recommendLayuot = (LinearLayout) findViewById(R.id.recommend_layout);
        recommend_listview = (ListView) findViewById(R.id.recommend_listview);
        mProgressLayout = (ProgressBarView) findViewById(R.id.pbv_loading);

        hashMap.put(UmengContact.contentId, videoItem.getArtid() + "");
        allProgressLayuot = (ProgressBarView) findViewById(R.id.all_pbv_loading);
        setupVideoView();

    }
    private void setupVideoView() {
        //开始手势
        mGestureView = findViewById(R.id.gesture);
        //播放结束，回放，转发
        mTvUpdateTime = (TextView) findViewById(R.id.tv_update_time);
        mFinishLayer = findViewById(R.id.finish_layer);
        //播放器view
        mSuperVideoPlayer = (SuperVideoPlayer) findViewById(R.id.video_player_item);
        mSuperVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);
        //手势识别器
        gestureDetector = new GestureDetector(this, new OnVideoGestureListener());
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 是否是第一次播放
        if (mSession.isFristPlay()) {
            mGestureView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setViews() {
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.drawable.fenxiang3x);
        toleft_iv_right.setVisibility(View.VISIBLE);

        mShareManager = ShareManager.getInstance();
        mShareListener = new ShareManager.CustomShareListener(mContext);
        initVideo();
        if( 4==videoItem.getType()) {
            shareLayout.setVisibility(View.GONE);
        }else{
            shareLayout.setVisibility(View.VISIBLE);
        }
        recommendListAdapter = new RecommendListAdapter(mContext,list);
        recommend_listview.setAdapter(recommendListAdapter);
    }

    private void initVideo() {
        if(mSuperVideoPlayer==null) {
            return;
        }
        mSuperVideoPlayer.setCovorVisible();
        mSuperVideoPlayer.requestFocus();
        mSuperVideoPlayer.setVisibility(View.VISIBLE);
        mSuperVideoPlayer.setAutoVisibleView(headLayout);
        mSuperVideoPlayer.setCollectView(toleft_iv_right);
        mSuperVideoPlayer.setShareView(iv_right);
        mSuperVideoPlayer.setAutoHideController(true);
        mSuperVideoPlayer.getSuperVideoView().setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                display();
                mSuperVideoPlayer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSuperVideoPlayer.alwaysShowController();
                    }
                },1000);
                mSuperVideoPlayer.dismissProgress();
                ShowMessage.showToast(VideoPlayVODNotHotelActivity.this,"由于网络问题，播放失败");
                return true;
            }
        });
        String videoURL = videoItem.getVideoURL();
        video = new Video();
        video.setVideoName(videoItem.getTitle());
        VideoUrl videoUrl1 = new VideoUrl();
        videoUrl1.setFormatName("超清");
        videoUrl1.setFormatUrl(videoURL + ".f30.mp4");
        VideoUrl videoUrl2 = new VideoUrl();
        videoUrl2.setFormatName("高清");
        videoUrl2.setFormatUrl(videoURL + ".f20.mp4");
        VideoUrl videoUrl3 = new VideoUrl();
        videoUrl3.setFormatName("标清");
        videoUrl3.setFormatUrl(videoURL + ".f10.mp4");

        ArrayList<VideoUrl> list = new ArrayList<>();
        list.add(videoUrl1);
        list.add(videoUrl2);
        list.add(videoUrl3);

        video.setVideoUrl(list);
    }

    @Override
    public void copyLink() {
        ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(videoItem.getContentURL());
        ShowMessage.showToast(mContext,"复制完毕");
    }


    public interface UpdateProgressListener{
        void loadFinish();
        void loadHttpError();
    }



    @Override
    public void setListeners() {
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        toleft_iv_right.setOnClickListener(this);

        mGestureView.setOnClickListener(this);
//        mCollectBtn.setOnClickListener(this);
        mReplayBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
        mSuperVideoPlayer.setOnPlayBtnClickListener(new MediaController.OnPlayBtnClickListener() {
            @Override
            public void onPlayBtnClick() {
                if(mSuperVideoPlayer.getPlayState() == MediaController.PlayState.PAUSE) {
                    writeAppLog("start",true);
                    RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page_play_video));
                }else {
                    RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page_pause_button));
                    writeAppLog("end",true);
                }
            }
        });
        mSuperVideoPlayer.setOnSeekBarStopListener(new SuperVideoPlayer.OnSeekBarStopListener() {
            @Override
            public void onSeekBarStop() {
                RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page_drag_progress));
            }
        });
        mSuperVideoPlayer.setOnFormatChangeListener(new MediaController.OnChangeFormatListener() {
            @Override
            public void onChangeFormat(int position) {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                if(position == 0) {
                    hashMap.put(getString(R.string.details_page_mediation_clarity),"sd");
                    RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page_mediation_clarity),hashMap);
                }else if(position == 1) {
                    hashMap.put(getString(R.string.details_page_mediation_clarity),"hd");
                    RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page_mediation_clarity),hashMap);
                }else if(position == 2) {
                    hashMap.put(getString(R.string.details_page_mediation_clarity),"ld");
                    RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page_mediation_clarity),hashMap);
                }
            }
        });

        mCustomWebView.setOnScrollBottomListener(new MyWebView.OnScrollBottomListener() {
            @Override
            public void onScrollBottom() {
                RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page_article));
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("time",System.currentTimeMillis()+"");
                RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_end_reading),hashMap);
                writeAppLog("complete",false);
            }
        });
        shareWeixinIV.setOnClickListener(this);
        shareFriendsIV.setOnClickListener(this);
        shareQqIV.setOnClickListener(this);
        shareWeiboIV.setOnClickListener(this);
        recommend_listview.setOnItemClickListener(recommendItemClickListener);
        mProgressLayout.setProgressBarViewClickListener(this);
        allProgressLayuot.setProgressBarViewClickListener(new ProgressBarViewClickListener() {
            @Override
            public void loadDataEmpty() {

            }

            @Override
            public void loadFailureNoNet() {

            }

            @Override
            public void loadFailure() {
                contentIsOnline();
            }
        });
    }

    /**
     * 获取文章是否在线接口
     */
    private void contentIsOnline(){
        allProgressLayuot.startLoading();
        AppApi.getContentIsOnline(this,this,videoItem.getArtid());
    }

    /**获取推荐列表数据**/
    private void getDataFromServer(){
        AppApi.isCollection(mContext,this,videoItem.getArtid());
        AppApi.getRecommendInfo(mContext,videoItem.getArtid(),this);
    }

    OnItemClickListener recommendItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            mSuperVideoPlayer.pausePlay(true);
            mSuperVideoPlayer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CommonListItem item = (CommonListItem) parent.getItemAtPosition(position);
                    Intent intent = new Intent(mContext,VideoPlayVODNotHotelActivity.class);
                    intent.putExtra("voditem",item);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            },500);

        }
    };


    /**
     * 播放回调
     */
    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onSwitchPageType() {    //切换屏幕
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
//                mRightLandscape.setVisibility(View.GONE);
//                mCutScreenBtn.setVisibility(View.GONE);
            } else {    //切换为横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mSuperVideoPlayer.setPageType(MediaController.PageType.EXPAND);
//                mRightLandscape.setVisibility(View.VISIBLE);
//                mCutScreenBtn.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPlayFinish() {    //播放结束
            Log.d("video","onPlayFinish");
            mFinishLayer.setVisibility(View.VISIBLE);
            headLayout.setVisibility(View.VISIBLE);
            mSuperVideoPlayer.dismissProgress();
            mSuperVideoPlayer.hideController();
            mSuperVideoPlayer.setCovorVisible();
            mSuperVideoPlayer.setBottomProgressBarGone();
           writeAppLog("end",true);
        }

        @Override
        public void onUpdateTime(int timeShow, int playTime, int allTime) {    //更新时间
            Log.d("video","onUpdateTime");
            mFinishLayer.setVisibility(View.GONE);
            if (timeShow == TIME_FORWARD) {
                mTvUpdateTime.setVisibility(View.VISIBLE);
                mTvUpdateTime.setText(">> " + TimeUtils.getPlayTime(playTime, allTime));
            } else if (timeShow == TIME_BACKWARD) {
                mTvUpdateTime.setVisibility(View.VISIBLE);
                mTvUpdateTime.setText("<< " + TimeUtils.getPlayTime(playTime, allTime));
            } else {
                mTvUpdateTime.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {

    }

    @Override
    public void loadFailure() {
       loadWebView();
    }

    /**
     * 手势监听
     */
    private class OnVideoGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {    // 单击
            // 单击显示/隐藏控制栏
//
//            int requestedOrientation = getRequestedOrientation();
            mSuperVideoPlayer.showOrHideController();
            if(ScreenOrientationUtil.getInstance().isPortrait()) {
//                if(mSuperVideoPlayer.getSuperVideoView().isPlaying()) {
//                    mSuperVideoPlayer.showOrHideController();
//                }else {
//                    mSuperVideoPlayer.pausePlay(true);
//                }
                headLayout.setVisibility(View.VISIBLE);

//                mTvTitle.setVisibility(View.GONE);
//                mDescTv.setText(videoItem.getTitle());
            }else {
//                mTvTitle.setText(videoItem.getTitle());
                boolean show = mSuperVideoPlayer.showOrHideController();
                if (show) {  //显示
                    Animation animation = AnimationUtils.loadAnimation(VideoPlayVODNotHotelActivity.this, R.anim.anim_enter_from_top);
                    animation.setAnimationListener(new AnimationImp() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            super.onAnimationEnd(animation);
                            headLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    headLayout.startAnimation(animation);
                } else {    //隐藏
                    Animation animation = AnimationUtils.loadAnimation(VideoPlayVODNotHotelActivity.this, R.anim.anim_exit_from_top);
                    animation.setAnimationListener(new AnimationImp() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            super.onAnimationEnd(animation);
                            headLayout.setVisibility(View.GONE);
                        }
                    });
                    headLayout.startAnimation(animation);
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {     // 双击
            if(mSuperVideoPlayer.getSuperVideoView().isPlaying()) {
                RecordUtils.onEvent(VideoPlayVODNotHotelActivity.this,getString(R.string.details_page_pause_touch));
            }
            // 双击暂停/播放视频
            mSuperVideoPlayer.playTurn();
            // 控制栏显示/隐藏状态
            boolean visible = mSuperVideoPlayer.getMediaControllerVisibility();
            if (visible) {
                headLayout.setVisibility(View.VISIBLE);
            } else {
                headLayout.setVisibility(View.GONE);
            }
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {    // 长按
            super.onLongPress(e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //获取手指按下的位置
                    mStartX = event.getX();
                    mStartY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 获取手指当前位置
                    float moveX = event.getX();
                    float moveY = event.getY();
                    // 计算手指移动距离
                    float offsetX = moveX - mStartX;
                    float offsetY = moveY - mStartY;
                    // 计算手指划过屏幕的百分比
                    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                    float scale = screenWidth*1.0f/screenHeight;
                    float movePercentX = Math.abs(offsetX) / screenWidth;
                    float movePercentY = Math.abs(offsetY) / screenHeight;
                    LogUtils.e("movePercentX=" + movePercentX + ",movePercentX=" + movePercentX);
                    // 在屏幕上横向滑动时修改视频播放进度，纵向滑动时修改系统音量
                    float offset = Math.abs(offsetX) - Math.abs(offsetY);
                    if (offset > 100) {
                        if (offsetX > 0) {  //快进
                            //修改播放进度
                            RecordUtils.onEvent(this,getString(R.string.details_page_sliding_progress));
                            mSuperVideoPlayer.updateSeekTime(TIME_FORWARD, movePercentX / 100);
                            mSuperVideoPlayer.updateSeekProgress(TIME_FORWARD, movePercentX / 100);
                        } else if (offsetX < 0) {  //快退
                            RecordUtils.onEvent(this,getString(R.string.details_page_sliding_progress));
                            mSuperVideoPlayer.updateSeekTime(TIME_BACKWARD, movePercentX / 100);
                            mSuperVideoPlayer.updateSeekProgress(TIME_BACKWARD, movePercentX / 100);
                        } else {
                            mSuperVideoPlayer.updateSeekTime(TIME_GONE, movePercentX / 100);
                            mSuperVideoPlayer.updateSeekProgress(TIME_GONE, movePercentX / 100);
                        }
                    } else if (offset < -100*scale) {
                        // 修改音量
                        if (offsetY > 0 && movePercentY > 0.3) {  //减小音量
                            RecordUtils.onEvent(this,getString(R.string.details_page_mediation_volume));
                            lowerVoice();
                        } else if (offsetY < 0 && movePercentY > 0.3) {   //增加音量
                            RecordUtils.onEvent(this,getString(R.string.details_page_mediation_volume));
                            raiseVoice();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //获取手指按下的位置
                    mStartX = event.getX();
                    mStartY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 获取手指当前位置
                    float moveX = event.getX();
                    float moveY = event.getY();
                    // 计算手指移动距离
                    float offsetX = moveX - mStartX;
                    float offsetY = moveY - mStartY;
                    // 计算手指划过屏幕的百分比
                    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                    float scale = screenWidth*1.0f/screenHeight;
                    float movePercentX = Math.abs(offsetX) / screenWidth;
                    float movePercentY = Math.abs(offsetY) / screenHeight;
                    LogUtils.e("movePercentX=" + movePercentX + ",movePercentX=" + movePercentX);
                    // 在屏幕上横向滑动时修改视频播放进度，纵向滑动时修改系统音量
                    float offset = Math.abs(offsetX) - Math.abs(offsetY);
                    int range = DensityUtil.dip2px(mContext, 200);
                    if (offset > 100&&moveY>0&&moveY<range) {
                        if (offsetX > 0) {  //快进
                            //修改播放进度
                            RecordUtils.onEvent(this,getString(R.string.details_page_sliding_progress));
                            mSuperVideoPlayer.updateSeekTime(TIME_FORWARD, movePercentX / 100);
                            mSuperVideoPlayer.updateSeekProgress(TIME_FORWARD, movePercentX / 100);
                        } else if (offsetX < 0) {  //快退
                            RecordUtils.onEvent(this,getString(R.string.details_page_sliding_progress));
                            mSuperVideoPlayer.updateSeekTime(TIME_BACKWARD, movePercentX / 100);
                            mSuperVideoPlayer.updateSeekProgress(TIME_BACKWARD, movePercentX / 100);
                        } else {
                            mSuperVideoPlayer.updateSeekTime(TIME_GONE, movePercentX / 100);
                            mSuperVideoPlayer.updateSeekProgress(TIME_GONE, movePercentX / 100);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 增加音量
     */
    private void raiseVoice() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
    }

    /**
     * 减少音量
     */
    private void lowerVoice() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
    }

    private class AnimationImp implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left: //返回
                onBackPressed();
                break;
            case R.id.iv_right: //分享
                shareMethod();
                break;
            case R.id.toleft_iv_right: //收藏
                toleft_iv_right.setOnClickListener(null);
                collect();
                break;
            case R.id.gesture:  //手势提示
                mGestureView.setVisibility(View.GONE);

                mSuperVideoPlayer.setVisibility(View.VISIBLE);
                mSuperVideoPlayer.setAutoHideController(true);
                mSession.setFirstPlay(false);
                break;

            case R.id.replay:   //重播
                mSuperVideoPlayer.playTurn();
                mFinishLayer.setVisibility(View.GONE);
                mSuperVideoPlayer.setBottomProgressBarVisible();
                writeAppLog("start",true);
                break;
            case R.id.share_finish:
                shareMethod();
                break;
            case R.id.share_weixin:
                share(SHARE_MEDIA.WEIXIN);
                writeAppLogPv("weixin");
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.menu_recommend_share_weixin),"success");
                RecordUtils.onEvent(mContext,"menu_recommend_share_weixin",hashMap);
                break;
            case R.id.share_friends:
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                writeAppLogPv("weixin_circle");
                HashMap<String,String> hashMap1 = new HashMap<>();
                hashMap1.put(getString(R.string.menu_recommend_share_weixin_friends),"success");
                RecordUtils.onEvent(mContext,"menu_recommend_share_weixin_friends",hashMap1);
                break;
            case R.id.share_qq:
                share(SHARE_MEDIA.QQ);
                writeAppLogPv("qq");
                HashMap<String,String> hashMap2 = new HashMap<>();
                hashMap2.put(getString(R.string.menu_recommend_share_qq),"success");
                RecordUtils.onEvent(mContext,"menu_recommend_share_qq",hashMap2);
                break;
            case R.id.share_weibo:
                share(SHARE_MEDIA.SINA);
                writeAppLogPv("weibo");
                HashMap<String,String> hashMap3 = new HashMap<>();
                hashMap3.put(getString(R.string.menu_recommend_sina),"success");
                RecordUtils.onEvent(mContext,"menu_recommend_sina",hashMap3);
        }
    }

    private void share(SHARE_MEDIA platform){
        mShareManager.setShortcutShare();
        UMWeb umWeb = new UMWeb(ConstantValues.addH5ShareParams(videoItem.getContentURL()));
        umWeb.setThumb(new UMImage(mContext,R.drawable.ic_launcher));
        umWeb.setTitle(videoItem.getTitle());
        umWeb.setDescription("投屏神器，进入饭局的才是热点");
        new ShareAction(mContext)
                .withText("投屏神器，进入饭局的才是热点"+ConstantValues.addH5ShareParams(videoItem.getContentURL()))
                .withMedia(umWeb)
                .setPlatform(platform)
                .setCallback(mShareListener)
                .share();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void writeAppLogPv(String custom_volume) {
        AliLogBean bean = new AliLogBean();
        bean.setUUID(UUID);
        int hotelid = mSession.getHotelid();
        int roomid = mSession.getRoomid();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        if(smallPlatInfoBySSDP!=null&&smallPlatInfoBySSDP.getHotelId()>0) {
            hotelid = smallPlatInfoBySSDP.getHotelId();
        }else if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getHotelId())) {
            try {
                hotelid = Integer.valueOf(tvBoxSSDPInfo.getHotelId());
            }catch (Exception e){}
        }else if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getHotelId())) {
            try {
                hotelid = Integer.valueOf(smallPlatformByGetIp.getHotelId());
            }catch (Exception e){}
        }
        bean.setHotel_id(hotelid>0?String.valueOf(hotelid):"");
        bean.setRoom_id(roomid>0?String.valueOf(roomid):"");
        bean.setTime(System.currentTimeMillis()+"");
        bean.setAction("share");
        bean.setType("content");
        bean.setContent_id("");
        bean.setCategory_id("");
        bean.setMobile_id(STIDUtil.getDeviceId(mContext));
        bean.setMedia_id("");
        bean.setOs_type("andriod");
        bean.setCustom_volume(custom_volume);

        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(this,bean,logFilePath);
    }

    @Override
    public void onBackPressed() {
        RecordUtils.onEvent(this,getString(R.string.details_page_back));
        if (mSuperVideoPlayer.mCurrPageType == MediaController.PageType.EXPAND ) {
            mSuperVideoPlayer.mMediaControl.onPageTurn();
        }else{
            finish();
        }
    }

    private void shareMethod() {
        RecordUtils.onEvent(this,getString(R.string.details_page_share));
        if (!AppUtils.isNetworkAvailable(this)) {
            ShowMessage.showToastSavor(this, getString(R.string.bad_wifi));
            return;
        }
        //暂停，记录播放位置
        currentSeconds = mSuperVideoPlayer.getCurrentSeconds();
        String title = "小热点| "+videoItem.getTitle();
        String text = "小热点| "+videoItem.getTitle();
        String imageURL = videoItem.getImageURL();
        String contentURL = videoItem.getContentURL();
        ShareManager shareManager = ShareManager.getInstance();
        shareManager.setCategory_id(category_id);
        shareManager.setContent_id(videoItem.getArtid());
        shareManager.share(this, text, title, imageURL, ConstantValues.addH5ShareParams(videoItem.getContentURL()), this, new ShareBoardlistener() {
            @Override
            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                isPlaying = true;
                if(mFinishLayer.getVisibility()!=View.VISIBLE) {
                    mSuperVideoPlayer.pausePlay(true);
                }
            }
        });
    }

    private void collect() {
        if ("1".equals(collected)){
            AppApi.handleCollection(mContext,this,videoItem.getArtid(),"0");
        }else{
            AppApi.handleCollection(mContext,this,videoItem.getArtid(),"1");
        }
    }

    /***
     * 旋转屏幕之后回调
     *
     * @param newConfig newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null == mSuperVideoPlayer) return;
        RecordUtils.onEvent(this,R.string.details_page_rotating_screen);
        /***
         * 根据屏幕方向重新设置播放器的大小
         */
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RecordUtils.onEvent(this,getString(R.string.details_page_full_screen));
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();
            float height = DensityUtil.getWidthInPx(this);
            float width = DensityUtil.getHeightInPx(this);
            mSuperVideoPlayer.getLayoutParams().height = (int) width;
            mSuperVideoPlayer.getLayoutParams().width = (int) height;
            mSuperVideoPlayer.setPageType(MediaController.PageType.EXPAND);
            ViewGroup.LayoutParams layerParams = mFinishLayer.getLayoutParams();
            layerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layerParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            mRightLandscape.setVisibility(View.VISIBLE);
//            mTvTitle.setVisibility(View.VISIBLE);
//            mTvTitle.setText(videoItem.getTitle());
            if(mSuperVideoPlayer.getSuperVideoView().isPlaying()) {
                headLayout.setVisibility(View.GONE);
            }else{
                headLayout.setVisibility(View.VISIBLE);
            }
            mSuperVideoPlayer.refreshPlayBtnState(MediaController.ORITATION_LANDSCAPE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtil.getWidthInPx(this);
            float height = DensityUtil.dip2px(this, 200.f);
            ViewGroup.LayoutParams layerParams = mFinishLayer.getLayoutParams();
            layerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mFinishLayer.getLayoutParams().height = (DensityUtil.dip2px(this,200f));
            mSuperVideoPlayer.getLayoutParams().height = (int) height;
            mSuperVideoPlayer.getLayoutParams().width = (int) width;
            mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
//            mRightLandscape.setVisibility(View.GONE);
//            mTvTitle.setVisibility(View.GONE);
            headLayout.setVisibility(View.VISIBLE);
            mSuperVideoPlayer.refreshPlayBtnState(MediaController.ORITATION_PORTAIT);
        }
    }

    @Override
    protected void onDestroy() {
        if(mSuperVideoPlayer!=null) {
            mSuperVideoPlayer.stopUpdateTimer();
            SuperVideoView superVideoView = mSuperVideoPlayer.getSuperVideoView();
            superVideoView.suspend();
//            superVideoView.r
            mSuperVideoPlayer = null;
        }

        if(mCustomWebView!=null) {
            mCustomWebView.onDestroy();
        }

        if(mCutHandler!=null) {
            mCutHandler.removeCallbacksAndMessages(null);
            mCutHandler = null;
        }
        if(mHanderThread!=null){
            mHanderThread.quit();
            mHanderThread = null;
        }
        System.gc();
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(new ContextWrapper(newBase)
        {
            @Override
            public Object getSystemService(String name)
            {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStartAndResume(this,this);
        mStartTime = System.currentTimeMillis();
        mCustomWebView.resumeTimers();
        if (mShareManager != null) {
            mShareManager.CloseDialog ();
        }
        mStartTime = System.currentTimeMillis();
        UUID = mStartTime +"";
        writeAppLog("start",false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this,this);
        int duration = (int) (System.currentTimeMillis() - mStartTime) / 1000;

        //行为统计
        MobclickAgent.onEventValue(this, UmengContact.READ, hashMap, duration);
        MobclickAgent.onPause(this);
        currentMsec = mSuperVideoPlayer.getCurrentMsec();
        isPlaying = mSuperVideoPlayer.getSuperVideoView().isPlaying();
        if(mFinishLayer.getVisibility()!=View.VISIBLE) {
            mSuperVideoPlayer.pausePlay(true);
        }
        writeAppLog("end",false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        mSuperVideoPlayer.startPlayVideo(currentSeconds);
        // 判断如果为绑定并且当前ssid与缓存机顶盒ssid相同提示绑定成功
        if(!mSession.isBindTv()) {
            TvBoxInfo tvBoxInfo = mSession.getTvboxInfo();
            if(tvBoxInfo!=null) {
                checkWifiLinked(tvBoxInfo);
            }
        }
        if(video!=null&&mFinishLayer.getVisibility()!=View.VISIBLE) {
            mSuperVideoPlayer.loadMultipleVideo(video,0,currentMsec);
            if(!isPlaying) {
                mSuperVideoPlayer.pausePlay(true);
            }
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_CONTENT_IS_ONLINE_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("success".equals(str)){
                        allProgressLayuot.loadSuccess();
                        ScreenOrientationUtil.getInstance().start(this);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                        iv_right.setVisibility(View.VISIBLE);
                        toleft_iv_right.setVisibility(View.VISIBLE);
                        headLayout.setBackgroundResource(R.drawable.ico_player_title);
                        display();

                    }
                }
                break;
            case GET_IS_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    collected = str;
                    if ("1".equals(collected)){
                        toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                    }else{
                        toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                    }
                }
                break;
            case GET_ADD_MY_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("success".equals(str)){
                        if ("0".equals(collected)){
                            collected = "1";
                            toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                            ShowMessage.showToast(VideoPlayVODNotHotelActivity.this,"收藏成功");
                        }else{
                            collected = "0";
                            toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                            ShowMessage.showToast(VideoPlayVODNotHotelActivity.this,"取消收藏");
                        }
                    }
                    toleft_iv_right.setOnClickListener(VideoPlayVODNotHotelActivity.this);
                }
                break;
            case POST_RECOMMEND_LIST_JSON:
                if(obj instanceof List<?>){
                    List<CommonListItem> listRecommend = (List<CommonListItem>)obj;
                    if (recommendListAdapter!=null){
                        if (listRecommend!=null&&!listRecommend.isEmpty()){
                            recommendLayuot.setVisibility(View.VISIBLE);
                            recommendListAdapter.setData(listRecommend);
                        }else{
                            recommendLayuot.setVisibility(View.GONE);
                        }
                    }
                }
                break;
        }
    }


    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method){
            case POST_CONTENT_IS_ONLINE_JSON:
                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    if (errorMessage.getCode()==19002){
                        allProgressLayuot.loadFailure("该内容找不到了~","",R.drawable.kong_wenzhang);
                        iv_right.setVisibility(View.GONE);
                        toleft_iv_right.setVisibility(View.GONE);
                    }else{
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        ScreenOrientationUtil.getInstance().stop();
                        iv_right.setVisibility(View.GONE);
                        toleft_iv_right.setVisibility(View.GONE);
                        allProgressLayuot.loadFailure();
                    }
                }
                break;
            case POST_RECOMMEND_LIST_JSON:
                recommendLayuot.setVisibility(View.GONE);
                break;
        }
    }


    private void writeAppLog(String action,boolean isplay) {
        AliLogBean bean = new AliLogBean();
        bean.setUUID(UUID);
        int hotelid = mSession.getHotelid();
        int roomid = mSession.getRoomid();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        if(smallPlatInfoBySSDP!=null&&smallPlatInfoBySSDP.getHotelId()>0) {
            hotelid = smallPlatInfoBySSDP.getHotelId();
        }else if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getHotelId())) {
            try {
                hotelid = Integer.valueOf(tvBoxSSDPInfo.getHotelId());
            }catch (Exception e){}
        }else if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getHotelId())) {
            try {
                hotelid = Integer.valueOf(smallPlatformByGetIp.getHotelId());
            }catch (Exception e){}
        }
        bean.setHotel_id(hotelid>0?String.valueOf(hotelid):"");
        bean.setRoom_id(roomid>0?String.valueOf(roomid):"");
        bean.setTime(System.currentTimeMillis()+"");
        bean.setAction(action);

        bean.setContent_id(videoItem.getArtid()+"");
        bean.setCategory_id(category_id);
        bean.setMobile_id(STIDUtil.getDeviceId(this));

        bean.setOs_type("andriod");

        if (isplay) {
            bean.setType("video");
            bean.setCustom_volume("mp4");
            bean.setMedia_id(videoItem.getMediaId());
        }else {
            bean.setType("content");
            bean.setCustom_volume(custom_volume);
            bean.setMedia_id("");
        }
        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(this,bean,logFilePath);
    }

    /**
     * 判断该文章是否下线，如果没有下线，才去渲染整个页面
     * */
    private void display(){

        headLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
        setViews();
        //开始初始化视频相关
        ImageView coverImageView = mSuperVideoPlayer.getCoverImageView();

        if(TextUtils.isEmpty(videoItem.getVideoURL())) {
            mSuperVideoPlayer.setCovorInvisoble();
            mSuperVideoPlayer.setmPlayFinished(true);
            mSuperVideoPlayer.stopUpdateTimer();
            mGestureView.setBackgroundColor(View.GONE);
        }else {
            Glide.with(this).load(videoItem.getImageURL()).centerCrop().into(coverImageView);
            mSuperVideoPlayer.loadMultipleVideo(video);
            mSuperVideoPlayer.pausePlay(true);
        }
        //开始初始化文章内H5内容
        loadWebView();
        //开始加载是否收藏和推荐
        getDataFromServer();
    }

    private void loadWebView() {
        String url = videoItem.getContentURL();
        if (!TextUtils.isEmpty(url)&& AppUtils.isNetworkAvailable(this)) {
            mProgressLayout.startLoading();
            mCustomWebView.loadUrl(ConstantValues.addH5Params(url), null, new UpdateProgressListener() {
                @Override
                public void loadFinish() {
                    writeAppLog("start",false);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    toleft_iv_right.setVisibility(View.VISIBLE);
                    iv_right.setVisibility(View.VISIBLE);
                    scrollviewLayout.scrollTo(0,0);
                    mProgressLayout.loadSuccess();
                }

                @Override
                public void loadHttpError() {

                    mProgressLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            toleft_iv_right.setVisibility(View.GONE);
                            iv_right.setVisibility(View.GONE);
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            mProgressLayout.loadFailure();
                        }
                    });
                }
            });
        }
    }

}