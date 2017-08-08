package com.savor.savorphone.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.DateUtil;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.bean.BaseProResponse;
import com.savor.savorphone.bean.LocalVideoProPesponse;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.QuerySeekResponse;
import com.savor.savorphone.bean.RotateProResponse;
import com.savor.savorphone.bean.SeekRequest;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.IHotspotSenseView;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.utils.ConstantsWhat;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;

import java.util.HashMap;

import static com.savor.savorphone.activity.HotspotMainActivity.SCAN_QR;
import static com.savor.savorphone.activity.LinkTvActivity.EXRA_TV_BOX;
import static com.savor.savorphone.activity.LinkTvActivity.EXTRA_TV_INFO;

/**
 * 本地视频详情页
 *
 * @author savor
 */
public class LocalVideoProAcitvity extends BaseProActivity implements
        OnClickListener, IBindTvView, IHotspotSenseView {

    private static final int UPDATE_SEEK = 10;
    private static final int UPDATE_CURRENT = 11;
    private static final int PLAY_OVER = 12;
    private static final int PLAY_ERROR = 13;
//    private static final int QUERY_SEEK = 14;
    private static final int CANCEL_CHECK_WIFI = 15;
    private static final int CHECK_WIFI_LINKED = 16;
    public static final int RESULT_CODE_SCREEN = 1000;

    private SavorApplication mApp;
    private Context mContext;

    private boolean play_over = false;
    private boolean isMute = false;
    private boolean isPlaying = true;
    /**查询进度任务是否执行*/
//    private boolean alive = true;
    /**是否正在执行播放/暂停操作*/
    private boolean isNotifying;
    private int heartTime = 0;
    private long mStartTime;
    private MediaInfo mModelVideo;
    private TextView mCurrent;
    private SeekBar mSeekBar;
    private ImageView to_back;


    //播放、暂定按钮
    private TextView mPlayButton;
    //音量—
    private ImageButton vol_down;
    //音量+
    private ImageButton vol_up;
    //退出
    private TextView finish;

    //遥控器中心 禁音状态 布局、按钮
    private RelativeLayout play_type_la;
    private ImageButton mute;

    //未绑定提示布局
    private RelativeLayout  bind_type_layout;
    //绑定按钮
    private TextView tv_link;
    /**正在投屏*/
    private boolean mProjecting = true;
    /**
     * 投屏时遇到别人正在投屏，传1代表确认抢投，默认传0
     */
    private int force=0;
    private CommonDialog dialog;
    /**
     * PLAY_ERROR时有时候会在onSaveInstanceState后执行,在PLAY_ERROR中有onBackpressed方法,
     * 该方法在onSaveInstanceState后再执行会抛出异常： java.lang.IllegalStateException: Can
     * not perform this action after onSaveInstanceState
     **/
    private boolean onSaveInstanceStateOver = false;
//    private boolean mQueryEnable = true;
    private android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CANCEL_CHECK_WIFI:
                    mSession.setTvBoxUrl(null);
                    mHandler.removeMessages(CHECK_WIFI_LINKED);
                    break;
                case CHECK_WIFI_LINKED:
                    if(!mSession.isBindTv()) {
                        TvBoxInfo tvboxInfo = mSession.getTvboxInfo();
                        if(tvboxInfo!=null) {
                            checkWifiLinked(tvboxInfo);
                        }
                    }else {
                        mHandler.removeMessages(CANCEL_CHECK_WIFI);
                    }
                    break;
                case UPDATE_CURRENT:
                    mCurrent.setText(msg.obj + "");
                    break;
                case UPDATE_SEEK:
                    int pos = Integer.parseInt(msg.obj + "");
                    LogUtils.d("SeekTo = " + pos / 1000);
                    if (pos > -1) {
                        mSeekBar.setProgress(pos / 1000);
                    }
                    break;
                case ConstantsWhat.NULL:
                    ShowMessage.showToastSavor(mContext, getString(R.string.bad_wifi));
                    break;
                case ConstantsWhat.PLAY_FAILED:
                    // PlayProResponseVo playResponse = (PlayProResponseVo) msg.obj;
                    // ShowMessage.showToastSavor(mContext, playResponse.getInfo());
                    break;
                case PLAY_OVER:
                    LogUtils.d("PLAY_OVER");
                    mProjecting = false;
                    mSeekBar.setProgress(0);
                    mPlayButton.setBackgroundResource(R.drawable.play_selector);
                    finish.setText("投屏");
                    play_over = true;
                    isPlaying = false;
                    stopQuerySeek();
                    setPlayButton(isPlaying);
                    Intent intent = new Intent("play_over");
                    sendBroadcast(intent);
                    break;
                case PLAY_ERROR:
                    stopQuerySeek();
                    break;
            }
        }
    };
    private CountDownTimer mCountDownTimer;
    private TextView mTotalTimeTv;
    private TextView mVideoNameTv;
    /**来源*/
    private String mExtraFrom;
    private CommonDialog hotsDialog;
    private CommonDialog mChangeWifiDiallog;
    private LinkDialog mQrcodeDialog;
    private String projectId;


//    private void sendQuerySeekMessage() {
//        mHandler.removeMessages(QUERY_SEEK);
//        QueryRequestVo queryBean = new QueryRequestVo();
//        queryBean.setFunction(ConstantsWhat.FunctionsIds.QUERY);
//        queryBean.setWhat("pos@" + mSession.getSessionID());
//        Message queryMsg = Message.obtain();
//        queryMsg.what = QUERY_SEEK;
//        queryMsg.obj = queryBean;
//        mHandler.sendMessageDelayed(queryMsg,1000);
//    }

    private ProjectionService mProjectionService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mProjectionService = ((ProjectionService.ProjectionBinder) service).getService();
            mProjectionService.querySeek();
            mProjectionService.setOnProgressChangeListener(new ProjectionService.OnProgressChangeListener() {
                @Override
                public void progressChange(QuerySeekResponse progressInfo) {
                    int result = progressInfo.getResult();
                    if(result == 0) {
                        updateSeek(progressInfo);
                    }else  {
                        queryFailed(progressInfo);
                    }
                }
            });
            mProjectionService.setOnplayOverListener(new ProjectionService.OnPlayOverListener() {
                @Override
                public void onPlayOver(int result) {
                    mProjecting = false;
                    stopProjection();
                }
            });
            mProjectionService.setOnProjectionErrorListener(new ProjectionService.OnProjectionErrorListener() {
                @Override
                public void onProjectoinErrorListener(AppApi.Action method, Object obj) {
                    onError(method,obj);
                }
            });
//            projectionService.setOnProjectionSuccessListener(new ProjectionService.OnProjectoinSuccessListener() {
//                @Override
//                public void onProjectionSuccess(AppApi.Action method, Object response) {
//                    onSuccess(method,response);
//                }
//            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    protected void onSaveInstanceState(Bundle outState) {
        onSaveInstanceStateOver = true;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mApp = (SavorApplication) getApplicationContext();
        mContext = this;
        setContentView(R.layout.activity_video_toscreen);
        handleIntent();
        if(isPlaying) {
            ProjectionManager.getInstance().setVideoLocalProjection(LocalVideoProAcitvity.class,mModelVideo,isPlaying);
        }
        getViews();
        setViews();
        setListeners();
        initSeekBar();
        // 开始查询进度
        bindProjectionService();
        initPresenter();
    }


    private void bindProjectionService() {
        Intent intent = new Intent(this, ProjectionService.class);
        bindService(intent,mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public static void startLocalVideoProActivity(Activity activity, MediaInfo videoInfo, boolean isPlaying, String projectId) {
        Intent intent = new Intent(activity,LocalVideoProAcitvity.class);
        intent.putExtra("ModelVideo",videoInfo);
        intent.putExtra("extra_from","video_detail");
        intent.putExtra("isPlaying",isPlaying);
        intent.putExtra("projectId",projectId);
        activity.startActivityForResult(intent,0);
    }

    private void handleIntent() {
        mModelVideo = (MediaInfo) getIntent().getSerializableExtra("ModelVideo");
        mExtraFrom = getIntent().getStringExtra("extra_from");
        isPlaying = getIntent().getBooleanExtra("isPlaying",true);
        projectId = getIntent().getStringExtra("projectId");
    }

    public void getViews() {
        mPlayButton = (TextView) findViewById(R.id.play);
        vol_down = (ImageButton) findViewById(R.id.vol_down);
        vol_up = (ImageButton) findViewById(R.id.vol_up);
        finish = (TextView) findViewById(R.id.finish);
        mTotalTimeTv = (TextView) findViewById(R.id.total);
        mVideoNameTv = (TextView) findViewById(R.id.video_name);
        mCurrent = (TextView) findViewById(R.id.current);
        play_type_la = (RelativeLayout) findViewById(R.id.play_type_la);
        mute = (ImageButton) findViewById(R.id.mute);
        bind_type_layout = (RelativeLayout) findViewById(R.id.bind_type_layout);
        to_back = (ImageView) findViewById(R.id.to_back);
        tv_link = (TextView) findViewById(R.id.tv_link);
     //   mRound = (ImageView) findViewById(R.id.round);
    }

    @Override
    public void setViews() {
        mTotalTimeTv.setText(DateUtil.formatSecondsTimeCh((mModelVideo.getAssetlength() / 1000) + ""));
        mVideoNameTv.setText(mModelVideo.getAssetname());
        mProjecting = mSession.isBindTv();
        if (mSession.isBindTv()) {
            bind_type_layout.setVisibility(View.GONE);
        }else {
            isPlaying = false;
            bind_type_layout.setVisibility(View.VISIBLE);
            if(hotsDialog==null) {
                hotsDialog = new CommonDialog(this, getString(R.string.click_link_tv), new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(getString(R.string.video_to_screen_link_tv),"link");
                        RecordUtils.onEvent(LocalVideoProAcitvity.this,getString(R.string.video_to_screen_link_tv),hashMap);
                        mBindTvPresenter.bindTv();
                    }
                }, new CommonDialog.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(getString(R.string.video_to_screen_link_tv),"cancel");
                        RecordUtils.onEvent(LocalVideoProAcitvity.this,getString(R.string.video_to_screen_link_tv),hashMap);
                    }
                },getString(R.string.link_tv));
            }
            hotsDialog.show();
        }
        if(!isPlaying) {
            mPlayButton.setBackgroundResource(R.drawable.play_selector);
            finish.setText("投屏");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EXTRA_TV_INFO){
            if(data!=null) {
                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
                mBindTvPresenter.handleBindCodeResult(boxInfo);
            }
        }else  if (resultCode == SCAN_QR) {
            if(data!=null) {
                String scanResult = data.getStringExtra("scan_result");
                mBindTvPresenter.handleQrcodeResult(scanResult);
                LogUtils.d("扫描结果：" + scanResult);
            }
//            showToast(scanResult);
        }
    }


    @Override
    public void setListeners() {
        mute.setOnClickListener(this);
        play_type_la.setOnClickListener(this);
        vol_down.setOnClickListener(this);
        vol_up.setOnClickListener(this);
        finish.setOnClickListener(this);
        to_back.setOnClickListener(this);
        tv_link.setOnClickListener(this);
    }

    /**
     * 动画
     */
//    private void initAnim() {
//        LinearInterpolator lir = new LinearInterpolator();
//        if(mAnimator==null) {
//            mAnimator = ObjectAnimator.ofFloat(mRound, "rotation", 0.0f, 360f);
//        }
//        mAnimator.setInterpolator(lir);
//        mAnimator.setDuration(15000);
//        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        mAnimator.addUpdateListener(updateListener);
//    }


    @Override
    public void onBackPressed() {
        if("video_detail".equals(mExtraFrom)) {
            setResult(RESULT_CODE_SCREEN);
        }
        if(mProjecting) {
            ProjectionManager.getInstance().setVideoLocalProjection(LocalVideoProAcitvity.class,mModelVideo,isPlaying);
        }else {
            ProjectionManager.getInstance().resetProjection();
        }
        super.onBackPressed();
    }

    /**
     * 播放进度条
     */
    private void initSeekBar() {
        mSeekBar = (SeekBar) findViewById(R.id.sb_seek);
        int maxSecond = (int) (mModelVideo.getAssetlength() / 1000);
        mSeekBar.setMax(maxSecond);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    querySeek();
                    int progress = seekBar.getProgress();
                    SeekRequest seekRequest = new SeekRequest();
                    seekRequest.setAbsolutepos(progress);
                    AppApi.notifyTvBoxSeekChange(LocalVideoProAcitvity.this,mSession.getTVBoxUrl(), seekRequest,projectId,LocalVideoProAcitvity.this);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopQuerySeek();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RecordUtils.onEvent(mContext,getString(R.string.video_to_screen_drag_progress));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mute:
                if (isMute) {//禁音状态，开启非禁音
                    AppApi.notifyTvBoxVolOn(this,mSession.getTVBoxUrl(),projectId,this);
                }else {//非禁音状态，开启禁音
                    AppApi.notifyTvBoxVolOff(this,mSession.getTVBoxUrl(),projectId,this);
                }
                break;
            case R.id.vol_down:
                RecordUtils.onEvent(this,getString(R.string.video_to_screen_vol_down));
                AppApi.notifyTvBoxVolDown(this,mSession.getTVBoxUrl(),projectId,this);
                if (isMute) {
                    AppApi.notifyTvBoxVolOn(this,mSession.getTVBoxUrl(),projectId,this);
                }
                break;
            case R.id.vol_up:

                RecordUtils.onEvent(this,getString(R.string.video_to_screen_vol_up));
                AppApi.notifyTvBoxVolUp(this,mSession.getTVBoxUrl(),projectId,this);
                if (isMute) {
                    AppApi.notifyTvBoxVolOn(this,mSession.getTVBoxUrl(),projectId,this);
                }
                break;
            case R.id.play_type_la:
                LogUtils.v( "play_over = " + play_over);
                if(!mSession.isBindTv())
                    return;
                if (play_over) {
//                    mQueryEnable = true;
                    isPlaying = true;
                    if(isPlaying) {
                        ProjectionManager.getInstance().setVideoLocalProjection(LocalVideoProAcitvity.class,mModelVideo,isPlaying);
                    }
                    force = 0;
                    AppApi.localVideoPro(mContext,mSession.getTVBoxUrl(),mModelVideo,force,this);
                } else {
                    if (isPlaying) {// 暂停
                        if(isNotifying) {
                            showToast("正在通知暂停...");
                            return;
                        }
                        isNotifying = true;
                        // 请求机顶盒暂停播放

                        RecordUtils.onEvent(this,getString(R.string.video_to_screen_pause));
                        AppApi.notifyTvBoxPause(this,mSession.getTVBoxUrl(),projectId,this);
                    } else {// 开始
                        if(isNotifying) {
                            showToast("正在通知播放...");
                            return;
                        }
                        isNotifying = true;
                        // 请求机顶盒开始播放

                        RecordUtils.onEvent(this,getString(R.string.video_to_screen_pause));
                        AppApi.notifyTvBoxReplay(this,mSession.getTVBoxUrl(),projectId,this);
                    }
                }
                break;
            case R.id.finish:
                if(mProjecting) {
                    mProjecting = false;
                    RecordUtils.onEvent(this,getString(R.string.video_to_screen_exit_screen));
                    AppApi.notifyTvBoxStop(this,mSession.getTVBoxUrl(),projectId,this);
                    stopQuerySeek();
                }else {
                    if(mSession.isBindTv()) {
                        force = 0;
                        AppApi.localVideoPro(mContext,mSession.getTVBoxUrl(),mModelVideo,force,this);
                    }
                }
                break;
            case R.id.to_back:
                onBackPressed();
                RecordUtils.onEvent(this,getString(R.string.album_toscreen_video_back));
                break;
            case R.id.tv_link:
                if(!AppUtils.isWifiNetwork(this)) {
                    new CommonDialog(this,"请前往手机设置，连接至电视同一WiFi下").show();
                }else{
                    RecordUtils.onEvent(this,R.string.album_toscreen_video_link);
                    mBindTvPresenter.bindTv();
                }
                break;

            default:
                break;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStartTime = System.currentTimeMillis();
        RecordUtils.onPageStart(this, getString(R.string.video_to_screen_play));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        mQueryEnable = true;
//        startUpdateSeek();
        // 判断如果为绑定并且当前ssid与缓存机顶盒ssid相同提示绑定成功
        if(!mSession.isBindTv()) {
            TvBoxInfo tvBoxInfo = mSession.getTvboxInfo();
            if(tvBoxInfo!=null) {
                checkWifiLinked(tvBoxInfo);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this, this);
//        pauseQuerySeek();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.d("onDestroy");
        super.onDestroy();
        if(mServiceConn!=null)
            unbindService(mServiceConn);
    }

    public void updateSeek(QuerySeekResponse posBySessionIdResponseVo) {
        LogUtils.d("updateSeek:" + posBySessionIdResponseVo.toString());
        Message msg = Message.obtain();
        msg.what = UPDATE_SEEK;
        msg.obj = posBySessionIdResponseVo.getPos();
        mHandler.sendMessage(msg);
    }

    public void queryFailed(QuerySeekResponse bySessionIdResponseVo) {
        LogUtils.d("queryFailed:" + bySessionIdResponseVo.toString());
//        if (bySessionIdResponseVo.getResult() == -1||bySessionIdResponseVo.getResult() == 1) {
            stopQuerySeek();
            // 播放完成
            mHandler.sendEmptyMessage(PLAY_OVER);
//            mQueryEnable = false;
//        }
    }


    public void playSuccess(BaseProResponse playResponseVo) {
        LogUtils.v( "isPlaying = " + isPlaying);
        int result = playResponseVo.getResult();
        if(result==-1)
            return;

        setPlayButton(isPlaying);
//        if (isPlaying) {
//           mPlayButton.setBackgroundResource(R.drawable.up_bai);
//            if (updateListener.isPause) {
//                updateListener.play();
//            } else { // 否则就是从头开始播放
//                if (mAnimator == null) {
//                    initAnim();
//                }
//                mAnimator.start();
//            }
//        } else {
//            mPlayButton.setBackgroundResource(R.drawable.play);
//            if (updateListener != null) {
//                updateListener.pause();
//            }
//        }
    }

//    @Override
//    public void prepareFailed(PrepareResponseVo prepareResponseVo) {
//    }
//
//    @Override
//    public void prepareSuccess(PrepareResponseVo prepareResponseVo) {
//        isPlaying = true;
//        play_over = false;
//        LogUtils.d("prepareSuccess  isPlaying= " + isPlaying);
//        setPlayButton(isPlaying);
////        mPlayButton.setBackgroundResource(R.drawable.up_bai);
////        if (updateListener.isPause) {
////            updateListener.play();
////        } else {  // 否则就是从头开始播放
////            if (mAnimator == null) {
////                initAnim();
////            }
////            mAnimator.start();
////        }
//    }

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
            case POST_SEEK_CHANGE_JSON:
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_NOTIFY_TVBOX_STOP_JSON:
                ProjectionManager.getInstance().resetProjection();
                stopProjection();
                break;
            case POST_LOCAL_VIDEO_PRO_JSON:
                finish.setText("退出");
                mProjecting = true;
                isPlaying = true;
                play_over = false;
                setPlayButton(true);
                if(obj instanceof LocalVideoProPesponse) {
                    LocalVideoProPesponse response = (LocalVideoProPesponse) obj;
                    projectId = response.getProjectId();
                    ProjectionManager.getInstance().setProjectId(projectId);
                }
                querySeek();
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

            case GET_NOTIFY_PAUSE_JSON:
                isNotifying = false;
                isPlaying = false;
//                mQueryEnable = false;
                if(obj instanceof BaseProResponse) {
                    BaseProResponse playResponseVo = (BaseProResponse) obj;
                    playSuccess(playResponseVo);
                }
                break;
            case POST_NOTIFY_REPLAY_JSON:
                isNotifying = false;
                isPlaying = true;
//                mQueryEnable = true;
                if(obj instanceof BaseProResponse) {
                    BaseProResponse playResponseVo = (BaseProResponse) obj;
                    playSuccess(playResponseVo);
//                    startUpdateSeek();
                }
                break;
            case POST_SEEK_CHANGE_JSON:
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
            default:
                break;
        }

    }

    private void querySeek() {
        if(mProjectionService!=null) {
            mProjectionService.querySeek();
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
//        super.onError(method, obj);
        switch (method) {
            case GET_QUERY_SEEK_JSON:
                mHandler.sendEmptyMessage(PLAY_ERROR);
                break;
            case GET_NOTIFY_PAUSE_JSON:
                isNotifying = false;
                showToast("暂停异常");
                break;
            case POST_NOTIFY_REPLAY_JSON:
                isNotifying = false;
                showToast("播放异常");
                break;
            case POST_SEEK_CHANGE_JSON:
                querySeek();
                break;
            case POST_LOCAL_VIDEO_PRO_JSON:
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage responseErrorMessage = (ResponseErrorMessage) obj;
                    String msg = responseErrorMessage.getMessage();
                    int code = responseErrorMessage.getCode();
                    if (code==4){
                        showConfirm(msg);
                    }else{
                        if (!TextUtils.isEmpty(msg)){
                            showToast(msg);
                        }
                    }

                }
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
                    params.put("type","video");
                    RecordUtils.onEvent(LocalVideoProAcitvity.this,getString(R.string.to_screen_competition_hint),params);
                    force = 1;
                    AppApi.localVideoPro(mContext,mSession.getTVBoxUrl(),mModelVideo,force,LocalVideoProAcitvity.this);
                    dialog.cancel();
                }
            }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","video");
                RecordUtils.onEvent(LocalVideoProAcitvity.this,getString(R.string.to_screen_competition_hint),params);
                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }

    //控制播放按钮暂停、播放样式
    private void setPlayButton(boolean isPlaying){
        //播放状态，显示暂停图标
        if (isPlaying) {
            mPlayButton.setBackgroundResource(R.drawable.pause_selector);
        }else {//暂停状态，显示播放图标
            mPlayButton.setBackgroundResource(R.drawable.play_selector);
        }
    }

    //控制禁音按钮禁音、非禁音样式
    private void setVolType(boolean isMute){
        //禁音
        if (isMute) {
            //mute.setBackgroundResource(R.drawable.sptplaba_jingyin);
            mute.setImageResource(R.drawable.sptplaba_jingyin);
        }else {//非禁音
           // mute.setBackgroundResource(R.drawable.sptplaba_dianbo);
            mute.setImageResource(R.drawable.sptplaba_dianbo);
        }
    }

    @Override
    public void showChangeWifiDialog() {
        mChangeWifiDiallog = new CommonDialog(this,
                getString(R.string.tv_bind_wifi) + "" + (TextUtils.isEmpty(mSession.getSsid()) ? "与电视相同的wifi" : mSession.getSsid())
                , new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivity(intent);
            }
        }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {

            }
        },"去设置");
        mChangeWifiDiallog.show();
    }

    @Override
    public void readyForCode() {
        if(mQrcodeDialog==null)
            mQrcodeDialog = new LinkDialog(this,getString(R.string.call_qrcode));
        mQrcodeDialog.show();
    }

    @Override
    public void closeQrcodeDialog() {
        if (mQrcodeDialog != null) {
            mQrcodeDialog.dismiss();
            mQrcodeDialog = null;
        }
    }

    @Override
    public void initBindcodeResult() {
        ShowMessage.showToast(this,"连接电视成功");
        ProjectionManager.getInstance().setVideoLocalProjection(LocalVideoProAcitvity.class,mModelVideo,isPlaying);
        bind_type_layout.setVisibility(View.GONE);
        force = 0;
        AppApi.localVideoPro(this,mSession.getTVBoxUrl(),mModelVideo,force,this);
    }

    @Override
    public void startLinkTv() {
        Intent intent = new Intent(this,LinkTvActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,0);
    }

    @Override
    public void showUnLinkDialog() {

    }

    @Override
    public void rotate(RotateProResponse rotateResponse) {

    }

    @Override
    public void reCheckPlatform() {

    }

    @Override
    public void initSenseState() {


    }

    @Override
    public void showProjection(boolean isBind) {

    }

    @Override
    public void hideProjection() {

    }

    @Override
    public void refreshData() {

    }

    @Override
    public void checkSense() {

    }

    public void stopProjection() {
        mHandler.removeMessages(PLAY_OVER);
        mHandler.sendEmptyMessageDelayed(PLAY_OVER,50);
    }
}
