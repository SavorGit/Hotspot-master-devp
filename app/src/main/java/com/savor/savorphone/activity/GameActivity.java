package com.savor.savorphone.activity;

import android.animation.Animator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.bean.Award;
import com.savor.savorphone.bean.GameResult;
import com.savor.savorphone.bean.SmashEgg;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.bean.VodProResponse;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.receiver.NetworkConnectChangedReceiver;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.LotteryFileUtil;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.utils.WifiUtil;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.savor.savorphone.activity.LinkTvActivity.EXTRA_TV_INFO;

/**
 * Created by bushlee on 2017/5/9.
 */

public class GameActivity extends BaseActivity implements View.OnClickListener,ApiRequestListener{
    public static final int SCAN_QR = 11;
    private Context mContext;
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private VodProResponse getEggbean;
    private GameResult gameResult;
    private String projectId;
    private String resultInfo;
    private String resultTitle;
    private String url;
    private static final int SENSOR_SHAKE = 106;
    private static final int EXIT_PRO = 0x10;
    private long mSeconds = 4;
    private Timer mTimer;
    private TimerTask mTask;
    private ImageView record;
    private ImageView share;
    private ImageView mFirstEggIv;
    private ImageView mSecondEggIv;
    private ImageView mThirdEggIv;
    private RelativeLayout time_la;
    private TextView time;
    private Context context;
    private TextView tv_center;
    private ImageView iv_left;
    private View line;
    private RelativeLayout shake_la;//摇一摇浮层
    private ImageView hammer;//锤子
    private ImageView close_shake_la;
    private RelativeLayout result_la;//抽奖结果浮层
    private TextView  result_title;
    private TextView  user_name;
    private ImageView  result_info;
    private TextView  result_log;
    private ImageView close_result;
    private RelativeLayout result_log_la;
    private TextView  result_time;
    private CommonDialog hotsDialog;
    /**是否没配置了奖项*/
    private boolean ischecknullAward = true;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SENSOR_SHAKE:
                    exitGameDelayed();
                    playHammerAnimation();
                    break;
                case EXIT_PRO:
                    finish();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    mNetWorkSettingsDialog = new CommonDialog(GameActivity.this,"请链接包间wifi","连接后可查看当前酒楼是否有优惠活动"
                            , new CommonDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            Intent intent = new Intent();
                            intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                            startActivity(intent);
                            finish();
                        }
                    }, new CommonDialog.OnCancelListener() {
                        @Override
                        public void onCancel() {
                            finish();
                        }
                    },"去连接");
                    mNetWorkSettingsDialog.show();
                    break;
            }
        }
    };
    private ImageView mWaterLoggingIv;
    private LinkDialog mToScreenDialog;
    private TextView mResetLottoryTv;
    /**
     * 当投屏时遇到大屏正在投屏中，抢投传1，代表确认抢投，默认传0
     */
    private int force=0;
    private CommonDialog mNetWorkSettingsDialog;
    private NetworkConnectChangedReceiver mChangedReceiver;

    /**播放锤子动画*/
    private void playHammerAnimation() {
        hammer.clearAnimation();
        SavorAnimUtil.hammerHit(hammer, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(mediaPlayer!=null) {
                    mediaPlayer.setLooping(true);
                }
                playWaterLoggingAnimation();
                playHit();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hitEgg();
                isShake = false;
                if(mediaPlayer!=null) {
                    mediaPlayer.setLooping(false);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**水渍动画*/
    private void playWaterLoggingAnimation() {
        mWaterLoggingIv.clearAnimation();
        SavorAnimUtil.waterLoggingAlpha(mWaterLoggingIv, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mWaterLoggingIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mWaterLoggingIv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private MediaPlayer mediaPlayer;
    /**是否是选蛋模式*/
    private boolean isChooseMode = true;
    /**是否是砸蛋模式*/
    private boolean isHintMode;
    private TextView mLottoryNumTv;
    private CommonDialog dialog;
    private ProjectionService mProjectionService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mProjectionService = ((ProjectionService.ProjectionBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);
        RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_start_game));
        RecordUtils.onEvent(GameActivity.this,getString(R.string.home_game_click));
        mContext = this;
        initPresenter();
        checkAward();
        getViews();
        setViews();
        setListeners();
        playBGM();
        playEggAnimation();
        bindProService();
        initNetWorkReceiver();
    }

    private void initNetWorkReceiver() {
        if(mChangedReceiver==null)
            mChangedReceiver = new NetworkConnectChangedReceiver(mHandler);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mChangedReceiver, filter);
    }

    private void bindProService() {
        Intent intent = new Intent(this, ProjectionService.class);
        intent.putExtra(ProjectionService.EXTRA_TYPE,ProjectionService.TYPE_VOD_NOMARL);
        bindService(intent,mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private void playEggAnimation() {

        SavorAnimUtil.shake(mFirstEggIv, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SavorAnimUtil.shake(mSecondEggIv, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        SavorAnimUtil.shake(mThirdEggIv, new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(isChooseMode) {
                                    playEggAnimation();
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void playBGM() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        AssetFileDescriptor file = getResources().openRawResourceFd(
                R.raw.game_bgm);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            //音量控制,初始化定义
//            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
////            int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2;
//            //当前音量
//            int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//            am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,0);
//            mediaPlayer.setVolume(currentVolume,currentVolume);
            mediaPlayer.start();

        } catch (IOException e) {
            mediaPlayer = null;
        }
    }

    private void playHit() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        AssetFileDescriptor file = getResources().openRawResourceFd(
                R.raw.hammer_hit);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            mediaPlayer = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isHintMode) {
            setRegisterListener();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(isChooseMode) {
            playBGM();
        }

        if(!mSession.isBindTv()) {
            TvBoxInfo tvBoxInfo = mSession.getTvboxInfo();
            if(tvBoxInfo!=null) {
                LogUtils.d(ConstantValues.LOG_CHECKWIFI_PREFIX+" 有缓存机顶盒信息 检查是否已连接指定wifi");
                checkWifiLinked(tvBoxInfo);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterListener();
        if(mediaPlayer!=null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceConn!=null) {
            unbindService(mServiceConn);
        }
        resetEggAnimation();
        unRegisterListener();
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        notifyBoxStop();
        if(mHandler!=null) {
            mHandler.removeMessages(WifiManager.WIFI_STATE_ENABLED);
            mHandler.removeCallbacksAndMessages(null);
        }
        mContext.unregisterReceiver(mChangedReceiver);
    }

    private void notifyBoxStop() {
        AppApi.notifyTvBoxStop(mContext,mSession.getTVBoxUrl(),projectId,this);
    }

    private void geteggAwardRecord() {
        AppApi.geteggAwardRecord(mContext,this);
    }
    @Override
    public void getViews() {
//        mResetLottoryTv = (TextView) findViewById(R.id.tv_reset);
        mWaterLoggingIv = (ImageView) findViewById(R.id.iv_waterlogging);
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        share = (ImageView) findViewById(R.id.share);
        record = (ImageView) findViewById(R.id.record);
        mFirstEggIv = (ImageView) findViewById(R.id.iv_egg_first);
        mSecondEggIv = (ImageView) findViewById(R.id.iv_egg_second);
        mThirdEggIv = (ImageView) findViewById(R.id.iv_egg_third);
        time_la = (RelativeLayout) findViewById(R.id.time_la);
        time = (TextView) findViewById(R.id.time);
        line = (View) findViewById(R.id.line);
        shake_la = (RelativeLayout) findViewById(R.id.shake_la);
        hammer = (ImageView) findViewById(R.id.hammer);
        close_shake_la = (ImageView) findViewById(R.id.close_shake_la);
        mLottoryNumTv = (TextView) findViewById(R.id.num);
        result_la = (RelativeLayout) findViewById(R.id.result_la);
        result_title = (TextView) findViewById(R.id.result_title);
        user_name = (TextView) findViewById(R.id.user_name);
        result_info = (ImageView) findViewById(R.id.result_info);
        result_log = (TextView) findViewById(R.id.result_log);
        result_time = (TextView) findViewById(R.id.result_time);
        result_log_la = (RelativeLayout) findViewById(R.id.result_log_la);
        close_result = (ImageView) findViewById(R.id.close_result);
    }

    @Override
    public void setViews() {
        tv_center.setText("砸蛋游戏");
        line.setVisibility(View.GONE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        int lottoryNum = LotteryFileUtil.getInstance().getLottoryNum();
        mLottoryNumTv.setText("您还有"+lottoryNum+"次机会");
        if(mSession.getHotelid()<=0|| !WifiUtil.checkWifiState(this)) {
            if(mNetWorkSettingsDialog==null) {
                mNetWorkSettingsDialog = new CommonDialog(GameActivity.this,"请链接包间wifi","连接后可查看当前酒楼是否有优惠活动"
                        , new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        Intent intent = new Intent();
                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                        startActivity(intent);
                        finish();
                    }
                }, new CommonDialog.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        finish();
                    }
                },"去连接");
            }
            if(!mNetWorkSettingsDialog.isShowing()) {
                mNetWorkSettingsDialog.show();
            }
        }
    }

    @Override
    public void setListeners() {
        result_la.setOnClickListener(this);
//        mResetLottoryTv.setOnClickListener(this);
        share.setOnClickListener(this);
        record.setOnClickListener(this);
        mFirstEggIv.setOnClickListener(this);
        mSecondEggIv.setOnClickListener(this);
        mThirdEggIv.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        close_result.setOnClickListener(this);
        close_shake_la.setOnClickListener(this);
        time_la.setOnClickListener(this);
        shake_la.setOnClickListener(this);
    }

    private void checkAward(){
//        File lottoryFile = getLottoryFile();
//        if(!lottoryFile.exists()) {//本地没缓存，请求接口
//
            String hotelId = mSession.getHotelid()+"";
            if (!TextUtils.isEmpty(hotelId)) {
                AppApi.smashEgg(this,hotelId,this);
            }
//
////            LogUtils.d("savor:lottory 当天没有过抽奖记录，保存剩余抽奖次数");
//
//
//
//        }else {
//            LogUtils.d("savor:lottory 已经有缓存抽奖次数，以本地为准");
//        }
    }

    private void setAward( SmashEgg eggInfo){
        if (eggInfo != null) {
            Award award = eggInfo.getAward();
            if (award != null) {
                ischecknullAward = false;
                int random = 5;
            try {
                random = Integer.valueOf(award.getLottery_num());
            }catch (Exception e){

            }
               if(!LotteryFileUtil.getInstance().isCacheLottory()) {
                   LotteryFileUtil.getInstance().saveLottoryNum(award.getLottery_num()+"");
                   LotteryFileUtil.getInstance().createRadomLottoryNum(random);
                   mLottoryNumTv.setText("您还有"+award.getLottery_num()+"次机会");
               }

            }else {
                ischecknullAward = true;
               // ShowMessage.showToast();
            }
        }else {
            ischecknullAward = true;
        }

    }
    @NonNull
    private File getLottoryFile() {
        String lottoryFileDir = SavorApplication.getInstance().getLottoryNumDir();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        String format = simpleDateFormat.format(date);
        return new File(lottoryFileDir+File.separator+format);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_reset:
//                LotteryFileUtil.getInstance().saveLottoryNum("5");
//                LotteryFileUtil.getInstance().saveLottoryCount("1");
//                mLottoryNumTv.setText("您还有5次机会");
//                break;
            case R.id.share://分享
                RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_recommend));
                toRecommend();
                break;
            case R.id.record://中奖纪录
                RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_recommend));
                if (!TextUtils.isEmpty(url)) {
                    toRecord();
                }else {
                    geteggAwardRecord();
                }

                break;
            case R.id.iv_egg_first:
            case R.id.iv_egg_second:
            case R.id.iv_egg_third:
                if(!AppUtils.isWifiNetwork(this)) {
                    showChangeWifiDialog();
                    return;
                }
                if(AppUtils.isFastDoubleClick(1)) {
                    return;
                }
                force = 0;
                RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_choose));
                checkBind();

//                setTime();
                break;
            case R.id.iv_left:
                onBackPressed();
                RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_back));
                //game_page_back
                break;
            case R.id.close_result:
                RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_result_finish));
                isChooseMode = true;
                notifyBoxStop();
                unRegisterListener();
                resetEggAnimation();
                playBGM();
                playEggAnimation();
                result_la.setVisibility(View.GONE);
                break;
            case R.id.close_shake_la:
                RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_hammer_back));
                isChooseMode = true;
                notifyBoxStop();
                unRegisterListener();
                playBGM();
                playEggAnimation();
                shake_la.setVisibility(View.GONE);
                break;



        }
    }

    private void resetEggAnimation() {
        mFirstEggIv.clearAnimation();
        mFirstEggIv.post(new Runnable() {
            @Override
            public void run() {
                mSecondEggIv.clearAnimation();
            }
        });
        mSecondEggIv.post(new Runnable() {
            @Override
            public void run() {
                mThirdEggIv.clearAnimation();
            }
        });

    }

    private void stopBGM() {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void dismissScreenDialog() {
        if(mToScreenDialog != null) {
            mToScreenDialog.dismiss();
        }
    }

    private void showToScreenDialog(String content) {
        mToScreenDialog = new LinkDialog(this,content);
        mToScreenDialog.show();
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case GET_EGG_JSON:
                RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_hit));
                dismissScreenDialog();
                if(obj instanceof VodProResponse) {
                    LogUtils.d("savor:lottory 投蛋成功");
                    exitGameDelayed();
                    if(mProjectionService!=null) {
                        mProjectionService.stopQuerySeek();
                        mProjectionService.stopSlide();
                    }
                    ProjectionManager.getInstance().resetProjection();
                    getEggbean = (VodProResponse) obj;
                    projectId = getEggbean.getProjectId();
                    isChooseMode = false;
                    isHintMode = true;
                    resetEggAnimation();
                    stopBGM();
                    setTime();
                }
                break;
            case GET_HIT_EGG_JSON:
                isHintMode = false;
                if(obj instanceof GameResult) {
                    gameResult = (GameResult) obj;
                    //处理砸金蛋摇一摇结果
                    sethitEggResult();
                }
                break;
            case POST_SMASH_EGG_JSON:
                if(obj instanceof SmashEgg) {
                    SmashEgg eggInfo = (SmashEgg) obj;
                    //处理砸金蛋摇一摇结果
                    setAward(eggInfo);
                }
                break;
            case POST_AWARD_RECORD_JSON:
                if(obj instanceof GameResult) {
                    GameResult re = (GameResult) obj;
                    if (re != null) {
                        url = re.getUrl();
                        if (!TextUtils.isEmpty(url)) {
                            toRecord();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void exitGameDelayed() {
        LogUtils.d("savor:game 延时2分钟后退出游戏页面");
        mHandler.removeMessages(EXIT_PRO);
        mHandler.sendEmptyMessageDelayed(EXIT_PRO,1000*60*2);
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
//        super.onError(method, obj);
        switch (method) {
            case GET_EGG_JSON:
                dismissScreenDialog();
                isChooseMode = true;
                isHintMode = false;
                //ResponseErrorMessage
                if(obj instanceof ResponseErrorMessage) {
                    RecordUtils.onEvent(GameActivity.this,getString(R.string.home_game_time_hint));
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String msg = message.getMessage();
                    if(code == 4) {
                        showConfirm(msg);
                    }else {
                        if(TextUtils.isEmpty(msg)) {
                            msg = "投屏失败";
                        }
                        dialog = new CommonDialog(mContext,msg);
                        dialog.show();
                    }
                }

                break;
            case GET_HIT_EGG_JSON:
                isHintMode = false;
                dismissScreenDialog();
                HashMap<String,String> hashMap1 = new HashMap<>();
                hashMap1.put(getString(R.string.game_page_result),"prize_failure");
                RecordUtils.onEvent(GameActivity.this,"game_page_result",hashMap1);

                break;



        }
    }

    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     * @param msg
     */
    private void  showConfirm(String msg){
        String content = "当前"+msg+"正在投屏,是否继续投屏?";
        dialog = new CommonDialog(this, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> params = new HashMap<>();
                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
                        params.put("type","game");
                        RecordUtils.onEvent(GameActivity.this,getString(R.string.to_screen_competition_hint),params);
                        force = 1;
                        getEgg();
                        dialog.cancel();
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","game");
                RecordUtils.onEvent(GameActivity.this,getString(R.string.to_screen_competition_hint),params);
                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }

    //砸蛋
    private void hitEgg(){
        String url = mSession.getTVBoxUrl();
        if (!TextUtils.isEmpty(projectId)&&!TextUtils.isEmpty(url) ) {
            if(!TextUtils.isEmpty(projectId)) {
                AppApi.hitEgg(this,url,projectId,this);
            }
        }else {
            if(!TextUtils.isEmpty(projectId)) {
                resetEggAnimation();
                playEggAnimation();
                playBGM();
            }
//            ShowMessage.showToast(this,"操作失败");
        }
    }

    //投蛋
    private void getEgg(){
        String url = mSession.getTVBoxUrl();
        if (!TextUtils.isEmpty(url) ) {
            int lottoryNum = LotteryFileUtil.getInstance().getLottoryNum();
            int lottoryRandom = LotteryFileUtil.getInstance().getLottoryRandom();
            int lottoryCount = LotteryFileUtil.getInstance().getLottoryCount();

            int hunger = lottoryCount == lottoryRandom?1:0;
            LogUtils.d("savor:lottory 开始投蛋，可抽奖次数为"+lottoryNum+",第"+lottoryRandom+"次中奖，"+"当前是第"+lottoryCount+"次抽奖");
            showToScreenDialog("加载中...");
            AppApi.getEgg(this,url,String.valueOf(hunger),force,this);
        }else {
            resetEggAnimation();
            playEggAnimation();
            playBGM();
//            ShowMessage.showToast(this,"操作失败");
        }
    }

    //处理砸蛋结果
    private void sethitEggResult(){
        if (gameResult != null) {
            int done = gameResult.getDone();
            if (done == 0) {//未砸开 继续砸

            }else if (done == 1) {//砸开了 结束砸蛋流程 显示抽奖结果

                mHandler.removeMessages(EXIT_PRO);
                shake_la.setVisibility(View.GONE);
                unRegisterListener();
                int win = gameResult.getWin();
                if (win == 0) {//未中奖
                    resultTitle =  "很遗憾，没中奖";
                }else if (win == 1) {//中奖了
                    resultTitle =  "恭喜您，中奖了";
                }
                resultInfo = gameResult.getInfo();
                setResultInfo();
                // 上报砸蛋结果
                String boxMac = mSession.getBoxMac();
                if(!TextUtils.isEmpty(boxMac)) {
                    LogUtils.d("savor:lottory 机顶盒mac："+boxMac);
                    AppApi.recordAwardLog(mContext,gameResult.getPrize_id()+"",gameResult.getPrize_time(),mSession.getBoxMac(),this);
                }else {
                    LogUtils.d("savor:lottory 无法获取机顶盒信息");
                }
                // 更新本地剩余抽奖次数
                if(!TextUtils.isEmpty(projectId)) {
                    int lottoryNum = LotteryFileUtil.getInstance().getLottoryNum();
                    if(lottoryNum>0) {
                        lottoryNum--;
                        LogUtils.d("savor:lottory 更新本地缓存可抽奖次数 "+lottoryNum+"次");
                        mLottoryNumTv.setText("您还有"+lottoryNum+"次机会");
                        LotteryFileUtil.getInstance().saveLottoryNum(String.valueOf(lottoryNum));
                    }
                    // 更新本地已经抽奖次数
                    int lottoryCount = LotteryFileUtil.getInstance().getLottoryCount();
                    lottoryCount++;
                    LotteryFileUtil.getInstance().saveLottoryCount(String.valueOf(lottoryCount));
                }
                projectId = null;
            }
        }
    }

    private void setResultInfo(){
        RecordUtils.onEvent(GameActivity.this,getString(R.string.game_page_hit));
        result_la.setVisibility(View.VISIBLE);
        result_title.setText(resultTitle);
       // result_info.setText(resultInfo);
        user_name.setText(Build.MODEL);
        int prize_level = gameResult.getPrize_level();
        switch (prize_level){
            case 0:
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.game_page_result),"prize_z");
                RecordUtils.onEvent(GameActivity.this,"game_page_result",hashMap);
                result_info.setBackgroundResource(R.drawable.xxcy);
                setResultLosingView();
                break;
            case 1:
                HashMap<String,String> hashMap1 = new HashMap<>();
                hashMap1.put(getString(R.string.game_page_result),"prize_a");
                RecordUtils.onEvent(GameActivity.this,"game_page_result",hashMap1);
                result_info.setBackgroundResource(R.drawable.yidj);
                setResultWinView();
                break;
            case 2:
                HashMap<String,String> hashMap2 = new HashMap<>();
                hashMap2.put(getString(R.string.game_page_result),"prize_b");
                RecordUtils.onEvent(GameActivity.this,"game_page_result",hashMap2);
                result_info.setBackgroundResource(R.drawable.erdj);
                setResultWinView();
                break;
            case 3:
                HashMap<String,String> hashMap3 = new HashMap<>();
                hashMap3.put(getString(R.string.game_page_result),"prize_c");
                RecordUtils.onEvent(GameActivity.this,"game_page_result",hashMap3);
                result_info.setBackgroundResource(R.drawable.sadj);
                setResultWinView();
                break;
        }
    }

    private void setResultWinView(){
        result_time.setVisibility(View.VISIBLE);
        String prize_time = gameResult.getPrize_time();
        long time = System.currentTimeMillis();
        try {
            time = Long.valueOf(prize_time);
        }catch (Exception e){}
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = simpleDateFormat.format(date);
        result_time.setText("("+format+")");
        result_log.setVisibility(View.GONE);
        result_log_la.setVisibility(View.VISIBLE);
    }

    private void setResultLosingView(){
        result_time.setVisibility(View.GONE);
        result_log.setVisibility(View.VISIBLE);
        result_log_la.setVisibility(View.GONE);

    }

    private boolean isShake;
    /**
     * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
//            int sensorType = event.sensor.getType();
//            switch (sensorType) {
//                case Sensor.TYPE_ACCELEROMETER:
                    float[] values = event.values;
                    float xValue = values[0];
                    float yValue = values[1];
                    float zValue = values[2];
                    int compareValue = 15;
                    if ((Math.abs(xValue) > compareValue || Math.abs(yValue) > compareValue || Math.abs(zValue) > compareValue)&&!isShake) {
                        isShake = true;
                        vibrator.vibrate(200);
                        Message message = mHandler.obtainMessage();
                        message.what = SENSOR_SHAKE;
                        mHandler.sendMessage(message);
                    }
//                    break;
//            }
//            // 传感器信息改变时执行该方法
//            float[] values = event.values;
//            float x = values[0]; // x轴方向的重力加速度，向右为正
//            float y = values[1]; // y轴方向的重力加速度，向前为正
//            float z = values[2]; // z轴方向的重力加速度，向上为正
//           // Log.i(TAG, "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z);
//            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
//            int medumValue = 19;// 如果不敏感请自行调低该数值,低于10的话就不行了,因为z轴上的加速度本身就已经达到10了
//            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
//                vibrator.vibrate(200);
//                Message msg = new Message();
//                msg.what = SENSOR_SHAKE;
//                mHandler.sendMessage(msg);
//            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    // 注册监听器
    private void setRegisterListener(){
        if (sensorManager != null) {// 注册监听器
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
    }

    // 取消监听器
    private void unRegisterListener(){
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    //分享给好友
    private void toRecommend(){
        Intent intent = new Intent();
        intent.putExtra("flag",6);
        intent.setClass(GameActivity.this,RecommendActivity.class);
        startActivity(intent);
    }

    //查看中奖结果
    private void toRecord(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("type","record");
        intent.putExtra("content",url);
        intent.setClass(GameActivity.this,WebViewActivity.class);
        startActivity(intent);
    }
    /**
     * 倒计时
     */
    private void countTime() {
        mTimer = new Timer();
        mTask = new TimerTask() {

            @Override
            public void run() {
                mSeconds = mSeconds - 1;
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = mSeconds;
                mTimeHandler.sendMessage(msg);
            }
        };
        mTimer.schedule(mTask, 0, 1000);
    }

    /**
     * 倒计时 handler
     */
    private Handler mTimeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:
                    long time = (Long) msg.obj;
                    if (time > 0 ) {
                        getStatus();
                    }else {
                        time_la.setVisibility(View.GONE);
                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                        }
                        if (mTask != null) {
                            mTask.cancel();
                            mTask = null;
                        }
                        //开始砸金蛋 1、弹出砸蛋（锤子浮层）2、手机可以摇
                        shake_la.setVisibility(View.VISIBLE);
                        setRegisterListener();
//                        int lottoryNum = LotteryFileUtil.getInstance().getLottoryNum();
//                        if(lottoryNum==0) {
//                            dialog = new CommonDialog(mContext,"今天的抽奖机会用完了\n明天再来吧！");
//                            dialog.show();
//                            //ShowMessage.showToast(GameActivity.this,"可抽奖次数为0");
//                        }else {
//                            getEgg();
//                        }
                    }

                    break;
            }
        };
    };

    private void getStatus(){
        time.setText(mSeconds+"");
    }

    private void setTime(){
        mSeconds = 4;
        time_la.setVisibility(View.VISIBLE);
        countTime();
    }


    private void checkBind(){
        if (mSession.isBindTv()) {
            int lottoryNum = LotteryFileUtil.getInstance().getLottoryNum();
            if (ischecknullAward) {
                dialog = new CommonDialog(mContext,"当前包间活动未开始");
                dialog.show();
            }else {
                if(lottoryNum==0) {
                    dialog = new CommonDialog(mContext,"今天的抽奖机会用完了\n明天再来吧！");
                    dialog.show();
                    //ShowMessage.showToast(GameActivity.this,"可抽奖次数为0");
                }else {

                    getEgg();
                }
            }
        }else {

            if(hotsDialog==null) {
                hotsDialog = new CommonDialog(this, "请连接电视，即可参与活动", new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        mBindTvPresenter.bindTv();
                    }
                }, new CommonDialog.OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                },getString(R.string.link_tv));
            }
            hotsDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EXTRA_TV_INFO){
            force = 0;
            getEgg();
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
    public void initBindcodeResult() {
//        showToScreenDialog("加载中...");
//        force = 0;
//        getEgg();
    }
}
