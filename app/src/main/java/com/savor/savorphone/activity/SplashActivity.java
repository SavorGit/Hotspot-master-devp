package com.savor.savorphone.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.api.bitmap.BitmapCommonUtils;
import com.common.api.utils.AppUtils;
import com.common.api.utils.FileUtils;
import com.common.api.utils.LogUtils;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.StartUpSettingsBean;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.location.LocationService;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.service.LocalJettyService;
import com.savor.savorphone.service.SSDPService;
import com.savor.savorphone.service.UpLoadLogService;
import com.savor.savorphone.service.UploadFirstUseService;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.ImageCacheUtils;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.widget.SplashDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


/**
 * 启动页面
 *
 * @author savor
 *         Update by wmm on 2016/11/20.
 */
public class SplashActivity extends BaseActivity {

    private static final int CLOSE_FIRSTUSE_SERVICE = 0x3;
    private final int FIRST_START = 1;
    private final int SWITCH_HOME = 2;

    //获取权限
    private static final int REQUEST_CODE = 3;
    private static final String TAG = "savor:splash";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLOSE_FIRSTUSE_SERVICE:
                    LogUtils.d("savor:ssdp 超过10秒关闭ssdp服务 并写入open日志");
                    writeStartAppLog();

                    Intent firstUseintent = new Intent(SplashActivity.this,UploadFirstUseService.class);
                    startService(firstUseintent);
                    finish();
                    break;
                case FIRST_START:
                    // 第一次启动显示向导页
                    Intent guideIntent = new Intent(SplashActivity.this, GuideActivity.class);
                    if(("application/pdf").equals(getIntent().getType())) {
                        Uri data = getIntent().getData();
                        guideIntent.setType(getIntent().getType());
                        guideIntent.setData(data);
                    }
                    startActivity(guideIntent);
                    break;
                case SWITCH_HOME:
                    // 启动跳转到首页
                    Intent homeIntent = new Intent(SplashActivity.this, HotspotMainActivity.class);
                    Intent intent = getIntent();
                    if(intent!=null&&("application/pdf").equals(intent.getType())) {
                        Uri data = getIntent().getData();
                        homeIntent.setDataAndType(data,intent.getType());
                    }
                    startActivity(homeIntent);
//                    finish();
                    break;
            }
        }
    };
    private StartUpSettingsBean latestSettingsBean;
    private ImageView mStartUpImageView;
    private long delayedTime = 1500;
    private SurfaceView mSurfaceView;
    private MediaPlayer mp;
    private RelativeLayout mParentLayout;
    private int uploadWaiterDataCount;
    private String waiterData;
    private boolean isJumped;
    private LocationService locationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            if(("application/pdf").equals(getIntent().getType())) {
                Uri data = getIntent().getData();
                Intent intent = new Intent(this,PdfPreviewActivity.class);
                intent.setDataAndType(data,getIntent().getType());
                startActivity(intent);
            }
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        showSplashVideo();
        getViews();
        setViews();
        setListeners();
//        AppApi.postWaiterExtension(this,this,"waiter","101","101");
        startServerDiscoveryService();
        startJettyServer();
        getSmallPlatformUrl();
		uploadLogFile();
        reUploadWaiterData();
//        OpenInstall.getInstall(new AppInstallListener() {
//            @Override
//            public void onInstallFinish(AppData appData, Error error) {
//                if (error == null) {
//                    //获取渠道数据
//                    Log.d("SplashActivity", "channel = " + appData.getChannel());
//                    //获取个性化安装数据
//                    Log.d("SplashActivity", "install = " + appData.getData());
//                    waiterData = appData.getData();
//                    postWaiterToServer(waiterData);
//                } else {
//                    Log.d("SplashActivity", "error : "+error.toString());
//                }
//            }
//        });

        startLocation();
        stopFirstUserServiceDelayed();
//        test();
    }

    private void test() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HotspotMainActivity.class);
                startActivity(intent);
            }
        },500);
    }

    private void startLocation() {
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        if(locationService==null) {
            locationService = new LocationService(this);
            locationService.registerListener(mLocationListener);
        }
        locationService.start();
    }

    private void writeStartAppLog() {
        Session session = Session.get(this);
        if(session.isWriteOpenLog()) {
            LogUtils.d("savor:log 已经写入open日志重启之前不会再次写入");
            return;
        }
        TvBoxSSDPInfo tvBoxSSDPInfo = session.getTvBoxSSDPInfo();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = session.getSmallPlatInfoBySSDP();
        SmallPlatformByGetIp smallPlatformByGetIp = session.getmSmallPlatInfoByIp();
        AliLogBean bean = new AliLogBean();
        if(smallPlatInfoBySSDP!=null) {
            int hotelid = smallPlatInfoBySSDP.getHotelId();
            int roomid = session.getRoomid();
            LogUtils.d("savor:ssdp 获取小平台ssdp信息 写入open日志 hotelid="+hotelid);
            startWrite(bean, hotelid, roomid);
        }else if(tvBoxSSDPInfo!=null) {
            String hotelId = tvBoxSSDPInfo.getHotelId();
            int roomid = session.getRoomid();
            LogUtils.d("savor:ssdp 获取机顶盒ssdp信息 写入open日志 hotelid="+hotelId);
            int hid = 0;
            try {
                hid = Integer.valueOf(hotelId);
            }catch (Exception e){}
            startWrite(bean,hid,roomid);
        }else if(smallPlatformByGetIp!=null) {
            String hotelId = smallPlatformByGetIp.getHotelId();
            int roomid = session.getRoomid();
            LogUtils.d("savor:ssdp 获取GetIp信息 写入open日志 hotelid="+hotelId);
            int hid = 0;
            try {
                hid = Integer.valueOf(hotelId);
            }catch (Exception e){}
            startWrite(bean,hid,roomid);
        }else {
            LogUtils.d("savor:机顶盒ssdp 小平台ssdp getip 都未获取到 写入open日志");
            startWrite(bean,0,0);
        }
    }

    private void startWrite(AliLogBean bean, int hotelid, int roomid) {
        bean.setHotel_id(hotelid>0?String.valueOf(hotelid):"");
        bean.setRoom_id(roomid>0?String.valueOf(roomid):"");
        bean.setUUID(System.currentTimeMillis()+"");
        bean.setTime(System.currentTimeMillis()+"");
        bean.setAction("open");
        bean.setType("app");
        bean.setContent_id("");
        bean.setCategory_id("");
        bean.setMobile_id(STIDUtil.getDeviceId(this));
        bean.setMedia_id("");
        bean.setCustom_volume("");
        bean.setOs_type("andriod");
        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(this,bean, logFilePath, new AliLogFileUtil.OnCompleteListener() {
            @Override
            public void onComplete() {
                Session session = Session.get(SplashActivity.this);
                session.setIsWriteOpenLog(true);
            }
        });
    }
    private void stopFirstUserServiceDelayed() {
        mHandler.sendEmptyMessageDelayed(CLOSE_FIRSTUSE_SERVICE,10*1000);
    }

    private void reUploadWaiterData() {
        // 服务员上传失败的数据重传
        waiterData = mSession.getWaiterData();
        if(!TextUtils.isEmpty(waiterData)) {
            LogUtils.d("savor:waiter 有上传失败服务员推广数据，开始重传");
            postWaiterToServer(waiterData);
        }else {
            LogUtils.d("savor:waiter 无失败的推广数据");
        }
    }

    private void postWaiterToServer(String data){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
            Log.d("SplashActivity", "统计服务员推广参数 : st=" + jsonObject.getString("st")+",hotelid="+jsonObject.getString("hotelid")+",waiterid="+jsonObject.getString("waiterid"));
            AppApi.postWaiterExtension(this,this,jsonObject.getString("st"),jsonObject.getString("hotelid"),jsonObject.getString("waiterid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    /**
     * 规则：
     * 1.删除日志临时文件如果有的话
     * 2.将日志文件压缩为zip保存为临时文件并上传
     *      如果上传成功删除日志文件
     * */
    private void uploadLogFile() {
        Intent intent = new Intent(this, UpLoadLogService.class);
        startService(intent);
    }

    private void showSplashVideo() {
        final SplashDialog splashDialog = new SplashDialog(this);
        splashDialog.setPlayOverListener(new SplashDialog.OnPlayOverListener() {
            @Override
            public void onSplashPlayOver() {
                getStartUpSettings();
                splashDialog.dismiss();
            }
        });
        splashDialog.show();
    }

    /**延时跳转*/
    private void jumpDelayed() {
        if(isJumped)
            return;
        isJumped = true;
        //第一次进入引导页，否则直接进入主页
        if (mSession.isNeedGuide()) {
            mHandler.sendEmptyMessageDelayed(FIRST_START, delayedTime);
        } else {
            mHandler.sendEmptyMessageDelayed(SWITCH_HOME, delayedTime);
        }
    }


    private void getSmallPlatformUrl() {
        //  判断是否获取到小平台地址，如果没有获取到请求云平台（小平台是局域网）获取小平台ip
        if(AppUtils.isWifiNetwork(this)) {
            LogUtils.d("savor:sp 当前wifi不可用不请求getip");
            AppApi.getSmallPlatformIp(this,this);
        }else {
            LogUtils.d("savor:sp 当前wifi状态不可用不请求getip");
        }
    }

    /**
     * 获取启动配置，启动图或启动视频
     */
    private void getStartUpSettings() {
        LogUtils.d(ConstantValues.LOG_PREFIX+"开始跳转...");
//        delayedTime = 0;
//        jumpDelayed();
//        return;
        StartUpSettingsBean startUpSettings = mSession.getStartUpSettings();
        /**
         * 如果有缓存数据，判断缓存文件是否存在如果存在就展示图片或视频，如果不存在展示默认图
         * */
        if(startUpSettings!=null) {
            String url = startUpSettings.getUrl();
            String duration = startUpSettings.getDuration();

            String status = startUpSettings.getStatus();
            String cacheKeyByUrl = ImageCacheUtils.getCacheKeyByUrl(url);
            String cachePath = SavorApplication.getInstance().getSplashCachePath();
            final String cacheFilepath = cachePath+File.separator+cacheKeyByUrl;
            File cacheFile = new File(cacheFilepath);
            if(cacheFile.exists()) {
                if("1".equals(status)) {
                    // 展示图片
                    int[] imageSize = BitmapCommonUtils.getImageSize(cacheFilepath);
                    if(imageSize[0]>0&&imageSize[1]>0) {
                        LogUtils.d(TAG+"加载缓存启动图"+cacheFilepath + ",图片分辨率="+imageSize[0]+"x"+imageSize[1]);
                        showCacheImage(cacheFilepath);
                        try {
                            delayedTime = Integer.valueOf(duration)*1000;
                        }catch (Exception e){}
                        jumpDelayed();
                    }else {
                        LogUtils.d(TAG+"图片宽或高为0，显示默认启动图");
                        delayedTime = 0;
                        jumpDelayed();
                        mSession.setStartUpSettings(null);
                    }
                }else if("2".equals(status)) {
                    // 展示视频
                    LogUtils.d(TAG+"加载缓存启动视频"+cacheFilepath);
                    showVideo(cacheFilepath);
                }
            }else {
                mSession.setStartUpSettings(null);
                // 缓存文件不存在展示默认图
                delayedTime = 0;
                jumpDelayed();
                LogUtils.d(TAG+" 缓存文件不存在--"+cacheFilepath);
            }
        }else {
            //  展示默认图
            LogUtils.d(TAG+"无缓存数据");
            delayedTime = 0;
            jumpDelayed();
        }

        AppApi.getStartUpSettings(this,this);
    }

    private void showVideo(final String cacheFilepath) {
        LogUtils.d("savor:splash 开始播放广告视频");
        mSurfaceView.setVisibility(View.VISIBLE);
        mp = new MediaPlayer();
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mp.setSurface(holder.getSurface());
                    mp.setDataSource(cacheFilepath);
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.prepare();
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.seekTo(0);
                            mp.start();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                ViewGroup.LayoutParams layoutParams = mSurfaceView.getLayoutParams();
                int viewWidth = mSurfaceView.getWidth();
                int viewHeight = mSurfaceView.getHeight();
                if (((double) viewWidth / width) * height > viewHeight) {
                    layoutParams.width = (int) (((double) viewHeight / height) * width);
                    layoutParams.height = viewHeight;
                } else {
                    layoutParams.width = viewWidth;
                    layoutParams.height = (int) (((double) viewWidth / width) * height);
                }
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setOnCompletionListener(null);
                if(mp!=null&&mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }
                delayedTime = 0;
                jumpDelayed();
            }
        });
    }

    private void showCacheImage(String cacheFilepath) {
        mStartUpImageView.setVisibility(View.VISIBLE);

        Glide.with(this).load(cacheFilepath).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mStartUpImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStartAndResume(this,this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this,this);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_STATICS_FIRSTUSE_JSON:
                LogUtils.d("savor:firstUse 首次使用上传成功");
                mSession.setIsUploadFirstuse(true);
                break;
            case DOWNLOAD_START_UP_JSON:
                handleDownLoadFile(obj);
                break;
            case GET_CLIENTSTART_JSON:
                handleStartUpSettings(obj);
                break;
            case GET_SAMLL_PLATFORMURL_JSON:
                // 获取小平台地址
                if(obj instanceof SmallPlatformByGetIp) {
                    SmallPlatformByGetIp smallPlatformByGetIp = (SmallPlatformByGetIp) obj;
                    if (smallPlatformByGetIp != null) {
                        String localIp = smallPlatformByGetIp.getLocalIp();
                        String hotelId = smallPlatformByGetIp.getHotelId();
                        // 保存酒店id
                        try {
                            Integer hid = Integer.valueOf(hotelId);
                            if(hid>0) {
                                mSession.setHotelid(hid);
                            }
                        }catch (Exception e) {
                        }
                        // 保存云平台获取的小平台信息
                        if (!TextUtils.isEmpty(localIp)) {
                            mSession.setSmallPlatInfoByGetIp(smallPlatformByGetIp);
                        }
                    }
                }
                break;
            case POST_WAITER_EXTENSION_JSON:
                // 上传服务员推广数据，将本地缓存数据置空
                LogUtils.d("savor:waiter 服务员推广数据上传成功");
                mSession.setWaiterData(null);
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
            case GET_CLIENTSTART_JSON:
//                super.onError(method, obj);
                delayedTime = 0;
                jumpDelayed();
                break;
            case POST_WAITER_EXTENSION_JSON:
                if(obj == AppApi.ERROR_TIMEOUT) {
                    if(uploadWaiterDataCount++>=2) {
                        LogUtils.d("savor:waiter 3次上传失败 ，将data保存在本地下次启动上传");
                        mSession.setWaiterData(waiterData);
                    }else {
                        LogUtils.d("savor:waiter 第"+uploadWaiterDataCount+"次上传失败，开始重传");
                        if(!TextUtils.isEmpty(waiterData)) {
                            postWaiterToServer(waiterData);
                        }
                    }
                }
                break;
        }
    }

    private void handleDownLoadFile(Object obj) {
        /**
         // 下载成功后将临时文件拷贝到缓存目录并删除临时文件
         */
        if(obj instanceof File) {
            File file = (File) obj;
            String splashCachePath = SavorApplication.getInstance().getSplashCachePath();
            String url = latestSettingsBean.getUrl();
            File cacheDir = new File(splashCachePath);
            if(!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            String cacheFilePath = splashCachePath+File.separator+ImageCacheUtils.getCacheKeyByUrl(url);
//            File cacheFile = new File(cacheFilePath);
            LogUtils.d(TAG+"文件下载完成，拷贝到目录："+cacheFilePath);
            FileUtils.copyFile(file,cacheFilePath);
            LogUtils.d(TAG+"删除临时文件"+file.getAbsolutePath());
//            file.delete();
            mSession.setStartUpSettings(latestSettingsBean);
        }

    }

    private void handleStartUpSettings(Object obj) {
        if(obj instanceof StartUpSettingsBean) {
            latestSettingsBean = (StartUpSettingsBean) obj;
            String id = latestSettingsBean.getId();
            String url = latestSettingsBean.getUrl();
            StartUpSettingsBean cacheSettingsBean = mSession.getStartUpSettings();

            // 如果接口没有数据 本地有缓存，清除缓存
            if(latestSettingsBean== null|| TextUtils.isEmpty(url)) {
                if(cacheSettingsBean !=null) {
                    LogUtils.d("savor:splash 接口没有数据，本地如果有缓存，清除缓存");
                    mSession.setDeleteStartUp(true);
                }
            }else{// 如果接口有数据
                // 如果本地没有缓存下载启动图
                if(cacheSettingsBean == null||TextUtils.isEmpty(cacheSettingsBean.getUrl())) {
                    LogUtils.d("savor:splash 接口有数据，本地没有缓存下载启动图");
                    uploadStartUpData(url);
                }else {// 本地有缓存判断如果url不同更新数据,
                    if(!cacheSettingsBean.getUrl().equals(url)) {
                        LogUtils.d("savor:splash 接口有数据，本地有缓存url不同更新数据，删除本地缓存");
                        String splashTempPath = SavorApplication.getInstance().getSplashTempPath();
                        String cachePath = SavorApplication.getInstance().getSplashCachePath();
                        com.common.api.utils.FileUtils.deleteFileAndFoder(cachePath);
                        com.common.api.utils.FileUtils.deleteFileAndFoder(splashTempPath);
                        uploadStartUpData(url);
                    }
                }
            }
        }
    }

    /**
     * 下载启动图或视频
     * @param url
     */
    private void uploadStartUpData(String url) {
        String splashTempCachePath = ((SavorApplication) getApplication()).getSplashTempPath();
        File tempDir = new File(splashTempCachePath);
        if(!tempDir.exists())
            tempDir.mkdirs();
        String cacheKeyByUrl = ImageCacheUtils.getCacheKeyByUrl(url);
        String targetFilePath = splashTempCachePath+File.separator+cacheKeyByUrl;
        LogUtils.d("savor:splash 缓存文件路径--"+targetFilePath);
        AppApi.donloadStartUpFile(this,targetFilePath,url,this);
    }

    /**
     * 启动jetty服务service
     */
    private void startJettyServer() {
        Intent intent = new Intent(this, LocalJettyService.class);
        this.startService(intent);
    }

    /**组播阻塞方式获取小平台发送的本身地址*/
    private void startServerDiscoveryService() {
        if(!AppUtils.isWifiNetwork(this)) {
            LogUtils.d("savor:sp 当前网络不可用不接受ssdp");
        }else {
            LogUtils.d("savor:sp 当前wifi状态接受ssdp");
            Intent intent = new Intent(this, SSDPService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
        }
    }

    @Override
    public void getViews() {
        mParentLayout = (RelativeLayout) findViewById(R.id.parent);
//        mDefaultSPlahIv = (ImageView) findViewById(R.id.iv_splash);
        mStartUpImageView = (ImageView) findViewById(R.id.iv_start_up);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
    }

    @Override
    public void setViews() {

    }

    @Override
    public void setListeners() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(ConstantValues.LOG_PREFIX+"销毁Splash...");
        if(mp!=null&&mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
        if(locationService!=null) {
            locationService.stop();
        }
    }

    @Override
    public void onBackPressed() {
        if(mp!=null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        super.onBackPressed();
    }


    /*****
     *
     * 点击首页底部悬浮窗进行定位结果回调
     *
     */
    private BDLocationListener mLocationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            String hid = mSession.getHotelid()==0?"":(String.valueOf(mSession.getHotelid()));
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                mSession.setLatestLat(latitude);
                mSession.setLatestLng(longitude);
            }
        }

        public void onConnectHotSpotMessage(String s, int i){
            LogUtils.d("savor:location onconnect = "+s);
//            int hotelid = Session.get(mContext).getHotelid();
//            String hotelId = session.getHotelid()==0?"":(session.getHotelid()+"");
//            AppApi.getNearlyHotel(mContext,ProjectionDialog.this,"","",hotelId);
        }
    };
}
