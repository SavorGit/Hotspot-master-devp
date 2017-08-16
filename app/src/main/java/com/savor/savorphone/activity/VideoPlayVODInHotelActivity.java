package com.savor.savorphone.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.AppUtils;
import com.common.api.utils.DateUtil;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity.UpdateProgressListener;
import com.savor.savorphone.adapter.RecommendListAdapter;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.BaseProResponse;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.QuerySeekResponse;
import com.savor.savorphone.bean.SeekRequest;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.StopProResponseVo;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.bean.VodProResponse;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.interfaces.OnQueryListener;
import com.savor.savorphone.interfaces.OnStopListener;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.ConstantsWhat;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.IntentUtil;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.ShareManager;
import com.savor.savorphone.utils.UmengContact;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.CustomWebView;
import com.savor.savorphone.widget.LinkDialog;
import com.savor.savorphone.widget.MyWebView;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.ProgressBarView.ProgressBarViewClickListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 餐厅环境视频点播页（带有退出投频按钮，声音控制，暂停继续播放）
 *
 * @author savor
 */
public class VideoPlayVODInHotelActivity extends BasePlayActivity implements
        OnClickListener, OnStopListener,OnQueryListener,ProgressBarViewClickListener,CopyCallBack, AdapterView.OnItemClickListener {

    private static final int UPDATE_SEEK = 10;
    private static final int UPDATE_CURRENT = 11;
    private static final int PLAY_OVER = 12;
    private static final int PLAY_ERROR = 13;
    public static final int FORCE_MSG = 104;
    private static final int ERROR_MSG = 105;
    private SavorApplication mApp;

    /**是否正在播放*/
    private boolean isPlaying = true;
    /**是否正在投屏*/
    private boolean isProjecting = true;

    private LinearLayout title_layout;
    private ImageView iv_left;
    private ImageView toleft_iv_right;
    private ImageView iv_right;
    private SeekBar mSeekBar;
    private ImageView picIV;
    // private Button mPlayButton;
    private TextView mCurrentTimeTV;
    private TextView totalTimeTV;
    private CustomWebView mWebView;
    private ImageView shareWeixinIV;
    private ImageView shareFriendsIV;
    private ImageView shareQqIV;
    private ImageView shareWeiboIV;
    private LinearLayout recommendLayuot;
    private LinearLayout share_layout;
    private boolean play_over = false;
    private CommonListItem mVodItem;
    private ShareManager mShareManager;
    private long mStartTime;
    //播放、暂定按钮
    private ImageButton mPlayButton;
    //音量—
    private ImageButton vol_down;

    //音量+
    private ImageButton vol_up;
    //遥控器中心 禁音状态 布局、按钮
    private ImageButton mute;

    private boolean isMute = false;
    private TextView signOutTV;

    private String UUID;
    private String custom_volume;
    private int volume;
    /**
     * 遇到抢投传1，代表确认，默认传0
     */
    private int force=0;
    private CommonDialog dialog;
    /**是否收藏,1:已收藏,2:未收藏**/
    private String collected="0";
    private ShareManager.CustomShareListener mShareListener;
    /**
     * PLAY_ERROR时有时候会在onSaveInstanceState后执行,在PLAY_ERROR中有onBackpressed方法,
     * 该方法在onSaveInstanceState后再执行会抛出异常： java.lang.IllegalStateException: Can
     * not perform this action after onSaveInstanceState
     **/
    private boolean onSaveInstanceStateOver = false;
    private HashMap<String, String> hashMap = new HashMap<String, String>();

    private android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SEEK:
                    int pos = Integer.parseInt(msg.obj + "");
                    LogUtils.d( "SeekTo = " + pos / 1000);
                    if (pos > -1) {
                        mSeekBar.setProgress(pos / 1000);
                    }
                    break;
                case UPDATE_CURRENT:
                    mCurrentTimeTV.setText(msg.obj + "");
                    break;
                case ConstantsWhat.PLAY_FAILED:
                    // PlayProResponseVo playResponse = (PlayProResponseVo) msg.obj;
                    // UIUtils.showToastSavor(mContext, playResponse.getInfo());
                    // isPlaying = false;
                    break;
                case ConstantsWhat.NULL:
                    ShowMessage.showToastSavor(VideoPlayVODInHotelActivity.this, getString(R.string.bad_wifi));
                    break;
                case PLAY_OVER:
                    stopQuerySeek();
//                    mHandler.removeMessages(QUERY_SEEK);
                    mSeekBar.setProgress(0);
                    mPlayButton.setBackgroundResource(R.drawable.d_zanting);
                    play_over = true;
                    isPlaying = false;
                    isProjecting = false;
                    signOutTV.setVisibility(View.GONE);
                    finish();
                    break;
                case PLAY_ERROR:
//                    mHandler.removeMessages(QUERY_SEEK);
                    stopQuerySeek();
                    mSeekBar.setProgress(0);
                    mPlayButton.setBackgroundResource(R.drawable.play_new_selector);
                    signOutTV.setVisibility(View.GONE);
                    play_over = true;
                    isPlaying = false;
                    break;
                case FORCE_MSG:
                    String user = (String) msg.obj;
                    showConfirm(user);
                    break;
                case ERROR_MSG:
                    String hint = (String) msg.obj;
                    showToast(hint);
                    break;
            }
        }

        ;
    };

    /**是否正在执行开始暂停操作*/
    private boolean isNotifying;
    /**当前收藏状态*/
    private boolean mCurrentCollectStatus;
    private int mVodType;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mProjectionService = ((ProjectionService.ProjectionBinder) service).getService();
            if(ProjectionManager.getInstance().getProjectionActivity()==SlidesActivity.class) {
                mProjectionService.stopSlide();
            }
            mProjectionService.querySeek();
            mProjectionService.setOnProgressChangeListener(new ProjectionService.OnProgressChangeListener() {
                @Override
                public void progressChange(QuerySeekResponse progressInfo) {
                    int result = progressInfo.getResult();
                    if(result == 0) {
                        if(signOutTV!=null)
                            signOutTV.setVisibility(View.VISIBLE);
                        updateSeek(progressInfo);
                    }else  if(result == 1||result == -1){
                        if(signOutTV!=null)
                            signOutTV.setVisibility(View.GONE);
                        queryFailed(progressInfo);
                        mProjectionService.stopQuerySeek();
                    }
                }
            });
            mProjectionService.setOnProjectionErrorListener(new ProjectionService.OnProjectionErrorListener() {
                @Override
                public void onProjectoinErrorListener(AppApi.Action method, Object obj) {
                    onError(method,obj);
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private ProjectionService mProjectionService;
    private String projectionId;
    private LinkDialog mToScreenDialog;
    /**是否已经打开视频详情页*/
    private boolean isOpenDetailActivity;
    private Context context;

    private ProgressBarView mProgressLayout;
    /**是否是手动点击收藏，只有手动点击才需要提示*/
    private boolean isClickCollection = false;
    private ListView recommend_listview;
    private RecommendListAdapter recommendListAdapter;
    private List<CommonListItem> list = new ArrayList<>();
    private LinkDialog mProDialog;
    private ScrollView mContentSlv;
    private TextView mMoreVideoBtn;

    protected void onSaveInstanceState(Bundle outState) {
        LogUtils.e("onSaveInstanceState");
        onSaveInstanceStateOver = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_video_vod_in_hotel);
        mApp = (SavorApplication) getApplicationContext();
        handleIntent();
        ProjectionManager.getInstance().setVideoTVProjection(VideoPlayVODInHotelActivity.class,mVodItem,isPlaying);
        initShare();
        getViews();
        setViews();
        setListeners();
        getDataFromServer();
        // 开始查询进度
        bindProService();
        getTvRecommendList();
    }

    private void getTvRecommendList() {
        int artId = 0;
        int sortNum = 0;
        try {
            artId = Integer.valueOf(mVodItem.getArtid());
            sortNum = Integer.valueOf(mVodItem.getSort_num());
        }catch (Exception e){}
        AppApi.getTvRecommendList(this,this,artId,sortNum);
    }

    private void bindProService() {
        Intent intent = new Intent(this, ProjectionService.class);
        intent.putExtra(ProjectionService.EXTRA_TYPE,ProjectionService.TYPE_VOD_VIDEO);
        bindService(intent,mServiceConn, Context.BIND_AUTO_CREATE);
        ProjectionManager.getInstance().setSlideStatus(false);
    }

    private void getDataFromServer(){
        if (mVodItem!=null){
            isClickCollection = false;
            AppApi.isCollection(mContext,this,mVodItem.getArtid());
        }
    }

    public static void startOnDemandActivity(Activity context, CommonListItem vodBean,boolean isPlaying) {
        Intent vodIntent = new Intent(context, VideoPlayVODInHotelActivity.class);
        vodIntent.putExtra("voditem", vodBean);
        vodIntent.putExtra("isPlaying", isPlaying);
        context.startActivity(vodIntent);
    }
    private void querySeek() {
        //在打开此页面之前已经请求了机顶盒投屏播放，请求机顶盒播放进度
        if(mProjectionService!=null) {
            mProjectionService.querySeek();
        }

    }

    public void getViews() {
        mMoreVideoBtn = (TextView) findViewById(R.id.tv_more_video);
        mContentSlv = (ScrollView) findViewById(R.id.slv_content);
        title_layout = (LinearLayout) findViewById(R.id.title_layout);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        toleft_iv_right = (ImageView) findViewById(R.id.toleft_iv_right);
        iv_right = (ImageView) findViewById(R.id.iv_right);

        picIV = (ImageView) findViewById(R.id.pic);
        signOutTV = (TextView) findViewById(R.id.sign_out);
        mCurrentTimeTV = (TextView) findViewById(R.id.current_time);
        totalTimeTV = (TextView) findViewById(R.id.total_time);
        share_layout = (LinearLayout) findViewById(R.id.share_layout);
        mWebView = (CustomWebView) findViewById(R.id.webview_custom);
        shareWeixinIV = (ImageView) findViewById(R.id.share_weixin);
        shareFriendsIV = (ImageView) findViewById(R.id.share_friends);
        shareQqIV = (ImageView) findViewById(R.id.share_qq);
        shareWeiboIV = (ImageView) findViewById(R.id.share_weibo);
        recommendLayuot = (LinearLayout) findViewById(R.id.recommend_layout);
        recommend_listview = (ListView) findViewById(R.id.recommend_listview);
        mProgressLayout = (ProgressBarView) findViewById(R.id.pbv_loading);

        mPlayButton = (ImageButton) findViewById(R.id.play);
        vol_down = (ImageButton) findViewById(R.id.down);
        vol_up = (ImageButton) findViewById(R.id.up);
        mute = (ImageButton) findViewById(R.id.mute);
    }

    public void setViews() {
        mShareListener = new ShareManager.CustomShareListener(mContext);
        title_layout.setBackgroundColor(getResources().getColor(R.color.transparent));
        iv_right.setVisibility(View.VISIBLE);
        toleft_iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.drawable.fenxiang3x);
        Glide.with(this).load(mVodItem.getImageURL()).centerCrop().into(picIV);

        recommendLayuot.setVisibility(View.GONE);
        recommendListAdapter = new RecommendListAdapter(mContext,list);
        recommend_listview.setAdapter(recommendListAdapter);

        isMute = mVodItem.isMute();
        setVolType(isMute);
        totalTimeTV.setText(DateUtil.formatSecondsTimeCh(String.valueOf(mVodItem.getDuration())));

        String contentURL = mVodItem.getContentURL();
        Uri contentUri = Uri.parse(contentURL);
        String pure = contentUri.getQueryParameter("pure");
        if(mVodItem.getType() == 4||"1".equals(pure)) {
            share_layout.setVisibility(View.GONE);
        }else {
            share_layout.setVisibility(View.VISIBLE);
        }

        mWebView.loadUrl(ConstantValues.addH5Params(mVodItem.getContentURL()), null, new UpdateProgressListener() {
            @Override
            public void loadFinish() {
//                mProgressLayout.loadSuccess();
            }

            @Override
            public void loadHttpError() {
//                mProgressLayout.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mProgressLayout.loadFailure();
//                    }
//                });
            }
        });
        hashMap.put(UmengContact.contentId, mVodItem.getArtid() + "");
        mCurrentCollectStatus = mSession.containsUrl(mVodItem.getContentURL());

        String url = mVodItem.getImageURL();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this).load(url).centerCrop().into(picIV);
        }
        if(!isPlaying) {
            mPlayButton.setBackgroundResource(R.drawable.play_new_selector);
        }
    }

    public void setListeners() {
        mMoreVideoBtn.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        toleft_iv_right.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        vol_down.setOnClickListener(this);
        vol_up.setOnClickListener(this);
        mute.setOnClickListener(this);
        signOutTV.setOnClickListener(this);
        mWebView.setOnScrollBottomListener(new MyWebView.OnScrollBottomListener() {
            @Override
            public void onScrollBottom() {
                RecordUtils.onEvent(VideoPlayVODInHotelActivity.this,getString(R.string.bunch_planting_page_article));
                writeAppLog("complete");
            }
        });
        shareWeixinIV.setOnClickListener(this);
        shareFriendsIV.setOnClickListener(this);
        shareQqIV.setOnClickListener(this);
        shareWeiboIV.setOnClickListener(this);
        mProgressLayout.setProgressBarViewClickListener(this);
        recommend_listview.setOnItemClickListener(this);

    }

    private void initShare() {
        mShareManager = ShareManager.getInstance();
        initSeekBar();
    }

    private void handleIntent() {
        mVodItem = (CommonListItem) getIntent().getSerializableExtra("voditem");
        mVodType = getIntent().getIntExtra("vodType",0);
        isPlaying = getIntent().getBooleanExtra("isPlaying",true);
        projectionId = getIntent().getStringExtra("projectionId");
        projectionId = ProjectionManager.getInstance().getProjectId();
        initCustom_volume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onSaveInstanceStateOver = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        querySeek();
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void initSeekBar() {
        LogUtils.d( "视频总时长：" + Integer.parseInt(String.valueOf(mVodItem.getDuration())));
        mSeekBar = (SeekBar) findViewById(R.id.sb_seek);
        mSeekBar.setMax(Integer.parseInt(String.valueOf(mVodItem.getDuration())));
        mSeekBar.setProgress(0);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                RecordUtils.onEvent(VideoPlayVODInHotelActivity.this,getString(R.string.bunch_planting_page_drag_progress));
                querySeek();

                SeekRequest seekRequest = new SeekRequest();
                seekRequest.setAbsolutepos(seekBar.getProgress()==0?1 : seekBar.getProgress());
                AppApi.notifyTvBoxSeekChange(VideoPlayVODInHotelActivity.this,mSession.getTVBoxUrl(), seekRequest,projectionId,VideoPlayVODInHotelActivity.this);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopQuerySeek();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当进度条改变时，更新播放时间
                Message obtain = Message.obtain();
                obtain.what = UPDATE_CURRENT;
                obtain.obj = DateUtil.formatSecondsTimeCh(progress + "");
                mHandler.sendMessage(obtain);
            }
        });
    }

    private void stopQuerySeek() {
        if(mProjectionService!=null) {
            mProjectionService.stopQuerySeek();
        }
    }

    @Override
    public void onClick(View view) {
        BaseProReqeust baseProReqeust = new BaseProReqeust();
        baseProReqeust.setVodType(mVodType);
        baseProReqeust.setAssetname(mVodItem.getName());
        switch (view.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.iv_right:
                shareMethod();
                break;
            case R.id.toleft_iv_right:
                // 如果已经收藏，取消收藏,否则添加收藏。添加友盟统计
                toleft_iv_right.setOnClickListener(null);
                collect();
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
            case R.id.play:
                LogUtils.e("play_over = " + play_over);

                if (play_over) {
                    // 点击播放按钮请求机顶盒投屏
                    force = 0;
                    AppApi.vodProection(this,mSession.getTVBoxUrl(), baseProReqeust,force,this);
                } else {
                    if (isPlaying) {    // 暂停
                        if(isNotifying) {
                           // showToast("正在通知暂停...");
                            return;
                        }
//                            alive = false;
                        isNotifying = true;
                        // 停止查询进度
//                        mHandler.removeMessages(QUERY_SEEK);
                        mProjectionService.stopQuerySeek();
                        RecordUtils.onEvent(this,getString(R.string.bunch_planting_page_pause_button));
                        // 请求机顶盒暂停播放
                        AppApi.notifyTvBoxPause(this,mSession.getTVBoxUrl(),projectionId,this);
//                            new PlayAsyncTask(this, VideoVodTVActivity.this).execute(0);
                    } else {    // 开始
                        if(isNotifying) {
                           // showToast("正在通知播放...");
                            return;
                        }
                        isNotifying = true;

                        // 请求机顶盒开始播放
                        AppApi.notifyTvBoxReplay(this,mSession.getTVBoxUrl(),projectionId,this);
                    }
                }
                break;
            case R.id.down:
                RecordUtils.onEvent(this,getString(R.string.bunch_planting_page_vol_down));
                AppApi.notifyTvBoxVolDown(this,mSession.getTVBoxUrl(),projectionId,this);
                if (isMute) {
                    AppApi.notifyTvBoxVolOn(this,mSession.getTVBoxUrl(),projectionId,this);
                }
                break;
            case R.id.up:
                RecordUtils.onEvent(this,getString(R.string.bunch_planting_page_vol_up));
                AppApi.notifyTvBoxVolUp(this,mSession.getTVBoxUrl(),projectionId,this);
                if (isMute) {
                    AppApi.notifyTvBoxVolOn(this,mSession.getTVBoxUrl(),projectionId,this);
                }
                break;
            case R.id.mute:
                if (isMute) {//禁音状态，开启非禁音
                    AppApi.notifyTvBoxVolOn(this,mSession.getTVBoxUrl(),projectionId,this);
                }else {//非禁音状态，开启禁音
                    RecordUtils.onEvent(this,getString(R.string.bunch_planting_page_vol_mute));
                    AppApi.notifyTvBoxVolOff(this,mSession.getTVBoxUrl(),projectionId,this);
                }
                break;

            case R.id.sign_out:
                RecordUtils.onEvent(this,getString(R.string.bunch_planting_page_exit_screen));
                stopQuerySeek();
                String projectId = ProjectionManager.getInstance().getProjectId();
                AppApi.notifyTvBoxStop(this,mSession.getTVBoxUrl(),projectId,this);
                showToScreenDialog("退出投屏...");
                break;
            case R.id.tv_more_video:
                Intent intent = new Intent(this,VodListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

        }
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

    private void share(SHARE_MEDIA platform){
        mShareManager.setShortcutShare();
        UMWeb umWeb = new UMWeb(ConstantValues.addH5ShareParams(mVodItem.getContentURL()));
        umWeb.setThumb(new UMImage(mContext,R.drawable.ic_launcher));
        umWeb.setTitle(mVodItem.getTitle());
        umWeb.setDescription("投屏神器，进入饭局的才是热点");
        new ShareAction(mContext)
                .withText("投屏神器，进入饭局的才是热点"+ConstantValues.addH5ShareParams(mVodItem.getContentURL()))
                .withMedia(umWeb)
                .setPlatform(platform)
                .setCallback(mShareListener)
                .share();

    }

    /**
     * 分享方法
     */
    private void shareMethod() {
        RecordUtils.onEvent(this,getString(R.string.details_page_share));
        if (!AppUtils.isNetworkAvailable(this)) {
            ShowMessage.showToastSavor(this, getString(R.string.bad_wifi));
            return;
        }
        mProjectionService.stopQuerySeek();
        ShareManager shareManager = ShareManager.getInstance();
        shareManager.setCategory_id("0");
        shareManager.setContent_id(mVodItem.getArtid()+"");
        String title = "小热点| "+mVodItem.getTitle() ;
        String text = "小热点| "+mVodItem.getTitle();
        mShareManager.share(this,text,title,mVodItem.getImageURL(),ConstantValues.addH5ShareParams(mVodItem.getContentURL()),this);
    }

    /**
     * 收藏方法
     */
    private void collect() {
        if ("1".equals(collected)){
            AppApi.handleCollection(mContext,this,mVodItem.getArtid(),"0");
        }else{
            AppApi.handleCollection(mContext,this,mVodItem.getArtid(),"1");
        }
    }

    @Override
    public void onBackPressed() {
        RecordUtils.onEvent(this,getString(R.string.bunch_planting_page_back));
        if(isProjecting) {
            LogUtils.d("savor:pro 正在投屏保存投屏信息"+mVodItem);
            ProjectionManager.getInstance().setVideoTVProjection(VideoPlayVODInHotelActivity.class,mVodItem,isPlaying);
        }else {
            LogUtils.d("savor:pro 非投屏状态清除背景投屏信息");
            ProjectionManager.getInstance().resetProjection();
        }
        setResult(HotspotMainActivity.FROM_APP_BACK);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStartAndResume(this,this);
        RecordUtils.onPageStart(this,getString(R.string.bunch_planting_page_share));
        if (mShareManager != null) {
            mShareManager.CloseDialog ();
        }
        mStartTime = System.currentTimeMillis();
        mWebView.resumeTimers();
        UUID = mStartTime +"";
        writeAppLog("start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        int duration = (int) (System.currentTimeMillis() - mStartTime) / 1000;
        RecordUtils.onPageEndAndPause(this,this);
        writeAppLog("end");
//        MobclickAgent.onEventValue(this, UmengContact.PLAY_CAST, hashMap, duration);
//        MobclickAgent.onPause(this);
//        mHandler.removeMessages(QUERY_SEEK);
//        if(projectionService!=null)
//            projectionService.stopQuerySeek();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {
        super.onNetworkFailed(method);
        switch (method) {
            case GET_QUERY_SEEK_JSON:

                break;
            case GET_NOTIFY_PAUSE_JSON:
                isNotifying = false;
                break;
            case POST_NOTIFY_REPLAY_JSON:
                isNotifying = false;
                break;

        }
    }

    @Override
    protected void onDestroy() {
        UMShareAPI.get(this).release();
        mWebView.onDestroy();
        mWebView.destroyDrawingCache();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mServiceConn!=null){
            unbindService(mServiceConn);
        }
        super.onDestroy();
    }

    @Override
    public void updateSeek(QuerySeekResponse posBySessionIdResponseVo) {
        LogUtils.d( "posBySessionIdResponseVo = " + posBySessionIdResponseVo.getResult());
        // 更新进度
        Message msg = Message.obtain();
        msg.what = UPDATE_SEEK;
        msg.obj = posBySessionIdResponseVo.getPos();
        mHandler.sendMessage(msg);
    }


    @Override
    public void stopSuccess() {
    }

    @Override
    public void resultNull() {
        mHandler.sendEmptyMessage(ConstantsWhat.NULL);
    }

    public void playFailed(BaseProResponse playResponseVo) {
        if ("视频无法播放".equals(playResponseVo.getInfo())) {
            isPlaying = !isPlaying;
        }
        Message msg = Message.obtain();
        msg.what = ConstantsWhat.PLAY_FAILED;
        msg.obj = playResponseVo;
        mHandler.sendMessage(msg);
    }

    public void playSuccess(BaseProResponse playResponseVo) {
        isPlaying = !isPlaying;
        LogUtils.v("isPlaying = " + isPlaying);
        if (isPlaying) {
            mPlayButton.setBackgroundResource(R.drawable.pause_new_selector);
           // finish_btn.setVisibility(View.VISIBLE);
        } else {
            mPlayButton.setBackgroundResource(R.drawable.play_new_selector);
           // finish_btn.setVisibility(View.GONE);
        }
    }


    @Override
    public void queryFailed(QuerySeekResponse bySessionIdResponseVo) {
        LogUtils.d("queryFailed:" + bySessionIdResponseVo.toString());
        // 播放完成或异常跳转到视频详情页
        ProjectionManager.getInstance().resetProjection();
        if (bySessionIdResponseVo.getResult() == -1) {
            // 播放异常
            mHandler.sendEmptyMessage(PLAY_OVER);
        } else if (bySessionIdResponseVo.getResult() == 1) {
            // 播放完成
            signOutTV.setVisibility(View.GONE);
            mHandler.sendEmptyMessage(PLAY_OVER);
        }

    }

    @Override
    public void stopFailed(StopProResponseVo responseVo) {
    }


    public void prepareSuccess(VodProResponse prepareResponseVo) {
        isPlaying = true;
        signOutTV.setVisibility(View.VISIBLE);
        play_over = false;
        mPlayButton.setBackgroundResource(R.drawable.d_bofang);
    }


    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_TV_RECOMMEND_JSON:
                if(obj instanceof List<?>) {
                    List<CommonListItem> listRecommend = (List<CommonListItem>) obj;
                    if(listRecommend!=null&&listRecommend.size()>0) {
                        recommendLayuot.setVisibility(View.VISIBLE);
                        recommendListAdapter.setData(listRecommend);
                        recommend_listview.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mContentSlv.scrollTo(0,0);
                            }
                        },100);
                    }else {
                        recommendLayuot.setVisibility(View.GONE);
                    }
                }
                break;
            case POST_NOTIFY_TVBOX_STOP_JSON:
                dismissScreenDialog();
                isPlaying = false;
                mHandler.sendEmptyMessage(PLAY_OVER);
                ProjectionManager.getInstance().resetProjection();
                finish();
                break;
            case GET_QUERY_SEEK_JSON:
                if(obj instanceof QuerySeekResponse) {
                    QuerySeekResponse queryPosBySessionIdResponseVo = (QuerySeekResponse) obj;
                    int result = queryPosBySessionIdResponseVo.getResult();
                    if(result == 0) {
                        updateSeek(queryPosBySessionIdResponseVo);
                    }else  {
                        queryFailed(queryPosBySessionIdResponseVo);
                    }
                }
                break;
            case POST_NOTIFY_TVBOX_SEEK_JSON:
                // do noting
                break;
            case GET_VOD_PRO_JSON:
                isProjecting = true;
                if(obj instanceof VodProResponse) {
                    VodProResponse response = (VodProResponse) obj;
                    projectionId = response.getProjectId();
                    ProjectionManager.getInstance().setProjectId(projectionId);
                    prepareSuccess(response);
                    ProjectionManager.getInstance().setVideoTVProjection(VideoPlayVODInHotelActivity.class,mVodItem,isPlaying);
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(SavorApplication.getInstance(), notification);
                    r.play();
                    if(mProjectionService!=null)
                        mProjectionService.querySeek();
                    // 解决当未投屏成功就点击返回键时 不能成功保存背景投屏状态
                    Activity specialActivity = ActivitiesManager.getInstance().getSpecialActivity(HotspotMainActivity.class);
                    if(specialActivity instanceof HotspotMainActivity) {
                        HotspotMainActivity mainActivity = (HotspotMainActivity) specialActivity;
                        mainActivity.refreshLinkStatus();
                    }
                }
                break;
            case GET_NOTIFY_PAUSE_JSON:
                isNotifying = false;
                if(obj instanceof BaseProResponse) {
                    BaseProResponse playResponseVo = (BaseProResponse) obj;
                    playSuccess(playResponseVo);
                }
                break;
            case POST_NOTIFY_REPLAY_JSON:
                isNotifying = false;
                if(obj instanceof BaseProResponse) {
                    BaseProResponse playResponseVo = (BaseProResponse) obj;
                    playSuccess(playResponseVo);
                    querySeek();
                }
                break;
            case POST_NOTIFY_VOL_UP_JSON:
                break;
            case POST_NOTIFY_VOL_DOWN_JSON:
                break;
            case POST_NOTIFY_VOL_ON_JSON:
                isMute = !isMute;
                setVolType(isMute);
                break;
            case POST_NOTIFY_VOL_OFF_JSON:
                isMute = !isMute;
                setVolType(isMute);
                break;
            case POST_EXIT_STATISTICS_JSON:
                isNotifying = false;
                isProjecting = false;
                signOutTV.setVisibility(View.GONE);
                mHandler.sendEmptyMessage(PLAY_OVER);
                break;
            case GET_IS_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    collected = str;
                    if ("1".equals(collected)){
                        toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                        if(isClickCollection) {
                            ShowMessage.showToast(VideoPlayVODInHotelActivity.this,"收藏成功");
                        }
                    }else{
                        toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                        if(isClickCollection) {
                            ShowMessage.showToast(VideoPlayVODInHotelActivity.this,"取消收藏");
                        }
                    }
                    isClickCollection = true;
                }
                break;
            case GET_ADD_MY_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("success".equals(str)){
                        if ("0".equals(collected)){
                            collected = "1";
                            toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                        }else{
                            collected = "0";
                            toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                        }
                    }
                    toleft_iv_right.setOnClickListener(VideoPlayVODInHotelActivity.this);
                }
                break;
        }
    }

    //控制禁音按钮禁音、非禁音样式
    private void setVolType(boolean isMute){
        //禁音
        if (isMute) {
            mute.setBackgroundResource(R.drawable.mute_off_new_selector);
        }else {//非禁音
            mute.setBackgroundResource(R.drawable.laba_dianbo_selector);
        }
        mVodItem.setMute(isMute);
    }

    private void showToScreenDialog(String content) {
        mToScreenDialog = new LinkDialog(this,content);
        mToScreenDialog.show();
    }

    private void dismissScreenDialog() {
        if(mToScreenDialog != null) {
            mToScreenDialog.dismiss();
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
//        super.onError(method, obj);
        dismissScreenDialog();
        switch (method) {
            case POST_TV_RECOMMEND_JSON:
                recommendLayuot.setVisibility(View.GONE);
                break;
            case GET_VOD_PRO_JSON:
                if(obj == AppApi.ERROR_TIMEOUT) {
                    showToast(getString(R.string.network_error));
                }
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage responseErrorMessage = (ResponseErrorMessage) obj;
                    String msg = responseErrorMessage.getMessage();
                    if (responseErrorMessage.getCode()==4){
                        showConfirm(msg);
                    }else{
                        if (!TextUtils.isEmpty(msg)){
                            showToast(msg);
                        }
                    }

                }
                break;
            case GET_QUERY_SEEK_JSON:
//                mHandler.sendEmptyMessage(PLAY_ERROR);
                break;
            case GET_NOTIFY_PAUSE_JSON:
                isNotifying = false;
                showToast("操作失败");
                break;
            case POST_NOTIFY_REPLAY_JSON:
                isNotifying = false;
                showToast("操作失败");
                break;
            case POST_SEEK_CHANGE_JSON:
                querySeek();
                break;
            case POST_NOTIFY_VOL_ON_JSON:

                break;
        }
    }

    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     * @param msg
     */
    private void showConfirm(String msg){
        final String content = "当前"+msg+"正在投屏,是否继续投屏?";
        dialog = new CommonDialog(this, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> params = new HashMap<>();
                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
                        params.put("type","vod");
                        RecordUtils.onEvent(VideoPlayVODInHotelActivity.this,getString(R.string.to_screen_competition_hint),params);

                        BaseProReqeust baseProReqeust = new BaseProReqeust();
                        baseProReqeust.setVodType(mVodType);
                        baseProReqeust.setAssetname(mVodItem.getName());
                        force = 1;
                        AppApi.vodProection(context,mSession.getTVBoxUrl(), baseProReqeust,force,VideoPlayVODInHotelActivity.this);
                        dialog.cancel();
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","vod");
                RecordUtils.onEvent(VideoPlayVODInHotelActivity.this,getString(R.string.to_screen_competition_hint),params);

                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }

    private void writeAppLog(String action) {
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
        bean.setType("content");
        bean.setContent_id(mVodItem.getArtid()+"");
        bean.setCategory_id("");
        bean.setMobile_id(STIDUtil.getDeviceId(this));
        bean.setMedia_id(mVodItem.getMediaId());
        bean.setOs_type("andriod");
        bean.setCustom_volume(custom_volume);
        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(this,bean,logFilePath);
    }

    private void initCustom_volume(){
        if(mVodItem == null)
            return;
        volume = mVodItem.getType();
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
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {

    }

    @Override
    public void loadFailure() {
        setViews();
        getDataFromServer();
    }

    @Override
    public void copyLink() {
        ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(mVodItem.getContentURL());
        ShowMessage.showToast(mContext,"复制完毕");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommonListItem item = (CommonListItem) parent.getItemAtPosition(position);
        startDemandItemPro(item);
    }

    /**
     * 点播投屏
     */
    private void startDemandItemPro(final CommonListItem currentItem) {
        // 当点击连接电视按钮时，要清除掉mCurrentProItem对象，否则绑定成功后会自动进行投屏
        if(currentItem!=null) {
            BaseProReqeust baseProReqeust = new BaseProReqeust();
            baseProReqeust.setVodType(1);
            baseProReqeust.setAssetname(currentItem.getName());

            // 请求机顶盒投屏，如果成功跳转到播放页面，失败弹出提示接口返回错误信息
            showProLoadingDialog();
            AppApi.vodProection(this, mSession.getTVBoxUrl(), baseProReqeust, force, new ApiRequestListener() {
                @Override
                public void onSuccess(AppApi.Action method, Object obj) {
                    switch (method) {
                        case GET_VOD_PRO_JSON:
                            dismissProLoadingDialog();
                            // 保存会话id
                            if(obj instanceof BaseProResponse) {
                                VodProResponse response = (VodProResponse) obj;
                                String projectId = response.getProjectId();
                                ProjectionManager.getInstance().setProjectId(projectId);
                                ProjectionManager.getInstance().setVideoTVProjection(VideoPlayVODInHotelActivity.class,currentItem,true);
                                boolean isPlaying = ProjectionManager.getInstance().getVodPlayStatus();
                                Intent vodIntent = new Intent(context, VideoPlayVODInHotelActivity.class);
                                vodIntent.putExtra("voditem", currentItem);
                                vodIntent.putExtra("isPlaying", isPlaying);
                                vodIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(vodIntent);
                            }
                            break;
                    }
                }

                @Override
                public void onError(AppApi.Action method, Object obj) {
                    switch (method) {
                        case GET_VOD_PRO_JSON:
                            dismissProLoadingDialog();
                            if(obj instanceof ResponseErrorMessage) {
                                ResponseErrorMessage message = (ResponseErrorMessage) obj;
                                int code = message.getCode();
                                Message msg = Message.obtain();
                                if (code == 4) {
//                        mCurrentMediaInfo.setMobileUser(message.getMessage());
                                    msg.what = FORCE_MSG;
                                    msg.obj = message.getMessage();
                                    mHandler.sendMessage(msg);
                                } else {
                                    msg.what = ERROR_MSG;
                                    msg.obj = message.getMessage();
                                    mHandler.sendMessage(msg);
                                }
                                break;
                            }
                            break;
                    }
                }

                @Override
                public void onNetworkFailed(AppApi.Action method) {

                }
            });
        }
    }

    /**
     * 展示请求投屏弹窗
     * */
    private void showProLoadingDialog() {
        if(mProDialog==null) {
            mProDialog = new LinkDialog(this,"请求投屏...");
        }
        mProDialog.show();
    }

    private void dismissProLoadingDialog() {
        if(mProDialog!=null) {
            mProDialog.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
        ProjectionManager.getInstance().setVideoTVProjection(VideoPlayVODInHotelActivity.class,mVodItem,isPlaying);
        initShare();
        setViews();
        setListeners();
        getDataFromServer();
        getTvRecommendList();
    }
}