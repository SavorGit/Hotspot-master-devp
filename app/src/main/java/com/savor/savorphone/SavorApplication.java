package com.savor.savorphone;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.fm.openinstall.OpenInstall;
import com.google.gson.Gson;
import com.savor.savorphone.activity.HotspotMainActivity;
import com.savor.savorphone.activity.ImageTextActivity;
import com.savor.savorphone.activity.PictureSetActivity;
import com.savor.savorphone.activity.SplashActivity;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.location.LocationService;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.utils.RecordUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.util.Map;

/**
 * Created by hezd on 2016/12/13.
 */

public class SavorApplication extends MultiDexApplication {

    private static SavorApplication mInstance;

    private String mSplashCachePath;
    private String mSplashTempPath;
    public String VodTypePath;
    public String VodStorePath;
    public String ImageCachePath;
    public String ImageSplash;
    public String GalleyPath;
    public String PdfJsPath;
    public String OfficePath;
    private String mLogFilePath;
    private String mLogTempFilePath;
    /**抽奖剩余次数本地缓存文件*/
    private String mLottoryNumDir;
    /**抽奖随机数缓存路径*/
    private String mLottoryRandomDir;
    /**当前抽奖次数*/
    private String mLottoryCountDir;

    public ProjectionService projectionService;
    /**列表缓存数据目录*/
    private String mListCachePath;

    public static SavorApplication getInstance() {
        return mInstance;
    }
    public LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();
        // 设置异常捕获处理类
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        Session.get(this);
        mInstance = this;
        MobclickAgent.openActivityDurationTrack(false);
        //初始化友盟分享
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
//        Config.DEBUG = true;
        UMShareAPI.get(this);
        initCacheFile(this);
        OpenInstall.init(this);
        //打开调试，便于看到Log
        OpenInstall.setDebug(false);

        initUmengPush();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initUmengPush() {
        LogUtils.d("savor:push initUmengPush");
        PushAgent mPushAgent = PushAgent.getInstance(this);
        LogUtils.d("savor:push deviceToken="+mPushAgent.getRegistrationId());
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                LogUtils.d("savor:push deviceToken="+deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtils.d("savor:push register failed ,message="+s);
            }
        });

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                // 点击收到推送，友盟埋点
                RecordUtils.onEvent(getApplicationContext(),R.string.click_notification);
                Map<String,String> custom = msg.extra;
                boolean isRunning = ActivitiesManager.getInstance().contains(HotspotMainActivity.class);
                String type = custom.get("type");
                String params = custom.get("params");
                if("1".equals(type)) {
                    if(!isRunning) {
                        Intent intent = new Intent(context, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }else if("2".equals(type)) {
                    if(!isRunning) {
                        Intent intent = new Intent(context, HotspotMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                    CommonListItem vodBean = new Gson().fromJson(params, CommonListItem.class);
                    if(vodBean!=null) {
                        int vodType = vodBean.getType();
                        String id = vodBean.getId();
                        String artid = vodBean.getArtid();
                        if(TextUtils.isEmpty(artid)) {
                            vodBean.setArtid(id);
                            artid = id;
                        }
                        // voidType 0:纯文本，1:图文，2:图集,3:视频,4:纯视频
                        if(vodType == 4||vodType == 3) {
                            Intent videoIntent = new Intent(context, VideoPlayVODNotHotelActivity.class);
                            videoIntent.putExtra("voditem", vodBean);
                            videoIntent.putExtra("category_id", "-1");
                            videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(videoIntent);
                        }else if(vodType == 0 || vodType == 1){
                            Intent intent = new Intent(context,ImageTextActivity.class);
                            intent.putExtra("item",vodBean);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }else if(vodType == 2) {
                            Intent intentp = new Intent(context, PictureSetActivity.class);
                            intentp.putExtra("content_id",artid);
                            intentp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentp);
                        }
                    }
                }
            }
        };

        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            @Override
            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                super.dealWithNotificationMessage(context, uMessage);
                // 收到推送
                RecordUtils.onEvent(getApplicationContext(),R.string.receive_notification);
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setDebugMode(false);
    }

    private void initCacheFile(Context context) {
        String cachePath = AppUtils.getSDCardPath()+"savor"+File.separator;
        mListCachePath = cachePath+"cache";
        mSplashCachePath = cachePath+"splash";
        mSplashTempPath = cachePath+"temp";
        mLogFilePath = cachePath + "log";
        mLogTempFilePath = cachePath + "log"+File.separator+"temp";
        mLottoryNumDir = cachePath+"lottery"+File.separator+"num";
        mLottoryRandomDir = cachePath+"lottery"+File.separator+"random";
        mLottoryCountDir = cachePath+"lottery"+File.separator+"count";

        File externalCacheDir = context.getExternalCacheDir();
        File fileDir = context.getFilesDir();
        VodTypePath = externalCacheDir + File.separator + ".VodTypeFile";
        VodStorePath = fileDir + File.separator + ".VodStoreFile";
        ImageCachePath = externalCacheDir + File.separator + ".ImageCacheFile";
        ImageSplash = ImageCachePath + File.separator + ".bg_splash.png";
        GalleyPath = ImageCachePath + "/galley";
        PdfJsPath = externalCacheDir + File.separator;
        OfficePath = fileDir + File.separator + "documents/";
    }

    public String getListCachePath() {
        return mListCachePath;
    }

    public String getLottoryCountDir() {
        return mLottoryCountDir;
    }

    public String getLogTempFilePath() {
        return mLogTempFilePath;
    }

    public String getLottoryRandomDir() {
        return mLottoryRandomDir;
    }

    public String getLottoryNumDir() {
        return mLottoryNumDir;
    }

    public String getLogFilePath() {
        return mLogFilePath;
    }

    /**
     * 获取启动图或视频缓存路径
     * @return
     */
    public String getSplashCachePath() {
        return mSplashCachePath;
    }

    /**
     * 获取启动图或视频临时文件路径
     * @return
     */
    public String getSplashTempPath() {
        return mSplashTempPath;
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin("wx59643f058e9b544c", "ad5cf8b259673427421a1181614c33c7");
        PlatformConfig.setQQZone("1105235421", "wZ1iLVjm6vRUyxbv");
        PlatformConfig.setSinaWeibo("258257010", "7b2701caad98239314089869bec08982","http://sns.whalecloud.com/sina2/callback");
//        PlatformConfig.setSinaWeibo("258257010", "7b2701caad98239314089869bec08982","https://api.weibo.com/oauth2/default.html");
    }
//        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
}
