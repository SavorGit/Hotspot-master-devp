package com.savor.savorphone.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.activity.VideoPlayVODInHotelActivity;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.widget.LinkDialog;
import com.savor.savorphone.widget.WebDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.umeng.socialize.utils.SocializeUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hezd on 2017/3/1.
 */

public class ShareManager {
    private static volatile ShareManager instance = null;
    private CustomShareListener mShareListener;
    private ShareAction mShareAction;
    //private static ProgressDialog progressBar;
    private static WebDialog progressBar;
    private static String category_id;
    private static String content_id;
    private static boolean isShortcutShare = false;

    private ShareManager(){}
    public static ShareManager getInstance() {
        if (instance == null) {
            synchronized (ShareManager.class) {
                if (instance == null) {
                    instance = new ShareManager();
                }
            }
        }
        return instance;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public void CloseDialog (){
        SocializeUtils.safeCloseDialog(progressBar);
    }
    public void share(final Activity activity, final String text, final String title, final String image, final String targeturl,final CopyCallBack cb) {
        ShareBoardConfig config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
        config.setShareboardBackgroundColor(activity.getResources().getColor(R.color.color_share_bg));
        config.setCancelButtonBackground(activity.getResources().getColor(R.color.color_share_cl_bg));
        config.setCancelButtonTextColor(activity.getResources().getColor(R.color.color_share_text));
        config.setIndicatorVisibility(false);
        config.setTitleText("");
        mShareListener = new CustomShareListener(activity);
        progressBar = new WebDialog(activity);
         /*增加自定义按钮的分享面板*/
        mShareAction = new ShareAction(activity).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA,
                SHARE_MEDIA.WEIXIN_FAVORITE).addButton("umeng_socialize_copy_btn_str",
                "umeng_sharebutton_custom",
                "umeng_socialize_link",
                "umeng_socialize_link")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if(share_media == SHARE_MEDIA.SINA) {
                            UMImage umImage = new UMImage(activity,R.drawable.ic_launcher);
                            umImage.compressFormat = Bitmap.CompressFormat.PNG;
                            new ShareAction(activity)
                                    .withText(text+targeturl)
                                    .withMedia(umImage)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        }else {
                            if (snsPlatform.mKeyword.equals("umeng_sharebutton_custom")) {
                               // RecordUtils.onEvent(activity,activity.getString(R.string.details_recommended));
                                cb.copyLink();
                            }else {
                                UMWeb umWeb = new UMWeb(targeturl);
                                UMImage umImage = new UMImage(activity,R.drawable.ic_launcher);
                                umImage.compressFormat = Bitmap.CompressFormat.PNG;
                                umWeb.setThumb(umImage);
                                umWeb.setTitle(title);
                                umWeb.setDescription(text);
                                new ShareAction(activity)
                                        .withText(text+targeturl)
                                        .withMedia(umWeb)
                                        .setPlatform(share_media)
                                        .setCallback(mShareListener)
                                        .share();
                            }

                        }
                    }
                });
        isShortcutShare = false;
        mShareAction.open(config);
    }

    public void share(final Activity activity, final String text, final String title, final String image, final String targeturl, final CopyCallBack cb, final ShareBoardlistener shareBoardlistener) {
        ShareBoardConfig config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
        config.setShareboardBackgroundColor(activity.getResources().getColor(R.color.color_share_bg));
        config.setCancelButtonBackground(activity.getResources().getColor(R.color.color_share_cl_bg));
        config.setCancelButtonTextColor(activity.getResources().getColor(R.color.color_share_text));
        config.setIndicatorVisibility(false);
        config.setTitleText("");
        mShareListener = new CustomShareListener(activity);
        progressBar = new WebDialog(activity);
         /*增加自定义按钮的分享面板*/
        mShareAction = new ShareAction(activity).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA,
                SHARE_MEDIA.WEIXIN_FAVORITE).addButton("umeng_socialize_copy_btn_str",
                "umeng_sharebutton_custom",
                "umeng_socialize_link",
                "umeng_socialize_link")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if(share_media == SHARE_MEDIA.SINA) {
                            shareBoardlistener.onclick(snsPlatform,share_media);
                            UMImage umImage = new UMImage(activity,R.drawable.ic_launcher);
                            umImage.compressFormat = Bitmap.CompressFormat.PNG;
                            new ShareAction(activity)
                                    .withText(text+targeturl)
                                    .withMedia(umImage)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        }else {
                            if (snsPlatform.mKeyword.equals("umeng_sharebutton_custom")) {
                                RecordUtils.onEvent(activity,activity.getString(R.string.details_recommended));
                                cb.copyLink();
                            }else {
                                UMWeb umWeb = new UMWeb(targeturl);
                                UMImage umImage = new UMImage(activity, R.drawable.ic_launcher);
                                umImage.compressFormat = Bitmap.CompressFormat.PNG;
                                umWeb.setThumb(umImage);
                                umWeb.setTitle(title);
                                umWeb.setDescription(text);
                                new ShareAction(activity)
                                        .withText(text+targeturl)
                                        .withMedia(umWeb)
                                        .setPlatform(share_media)
                                        .setCallback(mShareListener)
                                        .share();
                            }

                        }
                    }
                });
        isShortcutShare = false;
        mShareAction.open(config);
    }


    public void setShortcutShare(){
        isShortcutShare = true;
    }
    public static class CustomShareListener implements UMShareListener {

        private WeakReference<Activity> mActivity;

        public CustomShareListener(Activity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {
            SocializeUtils.safeShowDialog(progressBar);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            SocializeUtils.safeCloseDialog(progressBar);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST

                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
            String mapkey = "";
            if(mActivity.get() instanceof VideoPlayVODNotHotelActivity) {
                mapkey = "bunch_planting_page_share";
            }else if(mActivity.get() instanceof VideoPlayVODInHotelActivity) {
                mapkey = "details_page_share";
            }
            if (platform == SHARE_MEDIA.WEIXIN) {
                writeAppLogPv(mActivity.get(),"weixin");
                HashMap<String,String> hashMap = new HashMap<>();
                if (isShortcutShare) {
                    hashMap.put(mActivity.get().getString(R.string.shortcut_share_weixin),"success");
                }else {

                    hashMap.put(mActivity.get().getString(R.string.details_page_share_weixin),"success");
                }

                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                writeAppLogPv(mActivity.get(),"weixin_circle");
                HashMap<String,String> hashMap = new HashMap<>();
                if (isShortcutShare) {
                    hashMap.put(mActivity.get().getString(R.string.shortcut_share_weixin_friends),"success");
                }else {

                    hashMap.put(mActivity.get().getString(R.string.details_page_share_weixin_friends),"success");
                }
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.WEIXIN_FAVORITE) {
                writeAppLogPv(mActivity.get(),"weixin_collection");
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(mActivity.get().getString(R.string.details_page_share_weixin_collection),"success");
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.QQ) {
                writeAppLogPv(mActivity.get(),"qq");
                HashMap<String,String> hashMap = new HashMap<>();
                if (isShortcutShare) {
                    hashMap.put(mActivity.get().getString(R.string.shortcut_share_qq),"success");
                }else {

                    hashMap.put(mActivity.get().getString(R.string.details_page_share_qq),"success");
                }
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.QZONE) {
                writeAppLogPv(mActivity.get(),"qq_zone");
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(mActivity.get().getString(R.string.details_page_share_qq_zone),"success");
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.SINA) {
                writeAppLogPv(mActivity.get(),"weibo");
                HashMap<String,String> hashMap = new HashMap<>();
                if (isShortcutShare) {
                    hashMap.put(mActivity.get().getString(R.string.shortcut_share_sina),"success");
                }else {

                    hashMap.put(mActivity.get().getString(R.string.details_page_share_sina),"success");
                }
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }
            SocializeUtils.safeCloseDialog(progressBar);
        }

        private void writeAppLogPv(Context context,String custom_volume) {
            AliLogBean bean = new AliLogBean();
            bean.setUUID(System.currentTimeMillis()+"");
            int hotelid = Session.get( mActivity.get().getApplicationContext()).getHotelid();
            int roomid =Session.get( mActivity.get().getApplicationContext()).getRoomid();
            Session mSession = Session.get(mActivity.get());
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
            bean.setContent_id(content_id);
            bean.setCategory_id(category_id);
            bean.setMobile_id(STIDUtil.getDeviceId(mActivity.get().getApplicationContext()));
            bean.setMedia_id("");
            bean.setOs_type("andriod");
            bean.setCustom_volume(custom_volume);

            String logFilePath = SavorApplication.getInstance().getLogFilePath();
            AliLogFileUtil.getInstance().writeLogToFile(context,bean,logFilePath);
        }
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            SocializeUtils.safeCloseDialog(progressBar);
            String mapkey = "";
            if(mActivity.get() instanceof VideoPlayVODNotHotelActivity) {
                mapkey = "bunch_planting_page_share";
            }else if(mActivity.get() instanceof VideoPlayVODInHotelActivity) {
                mapkey = "details_page_share";
            }
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                if(platform==SHARE_MEDIA.WEIXIN
                        ||platform==SHARE_MEDIA.WEIXIN_FAVORITE
                        ||platform==SHARE_MEDIA.WEIXIN_CIRCLE) {
                    if(!isWeixinAvilible(mActivity.get())) {
                        Toast.makeText(mActivity.get(),"请安装应用后分享",Toast.LENGTH_SHORT).show();
                    }else {
                       // Toast.makeText(mActivity.get(), " 分享失败啦", Toast.LENGTH_SHORT).show();
                    }
                }else if(platform == SHARE_MEDIA.QQ||platform==SHARE_MEDIA.QZONE){
                    if(!isQQClientAvailable(mActivity.get())) {
                        Toast.makeText(mActivity.get(),"请安装应用后分享",Toast.LENGTH_SHORT).show();
                    }else {
                      //  Toast.makeText(mActivity.get(), " 分享失败啦", Toast.LENGTH_SHORT).show();
                    }
                }else {
                   // Toast.makeText(mActivity.get(),  "分享失败啦", Toast.LENGTH_SHORT).show();
                }
                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                }
            }

            if(platform == SHARE_MEDIA.WEIXIN) {
                HashMap<String,String> hashMap = new HashMap<>();
                if (isShortcutShare) {
                    hashMap.put(mActivity.get().getString(R.string.shortcut_share_weixin),"fail");
                }else {

                    hashMap.put(mActivity.get().getString(R.string.details_page_share_weixin),"fail");
                }

                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                HashMap<String,String> hashMap = new HashMap<>();
                if (isShortcutShare) {
                    hashMap.put(mActivity.get().getString(R.string.shortcut_share_weixin_friends),"fail");
                }else {

                    hashMap.put(mActivity.get().getString(R.string.details_page_share_weixin_friends),"fail");
                }
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.WEIXIN_FAVORITE) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(mActivity.get().getString(R.string.details_page_share_weixin_collection),"fail");
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.QQ) {
                HashMap<String,String> hashMap = new HashMap<>();
                if (isShortcutShare) {
                    hashMap.put(mActivity.get().getString(R.string.shortcut_share_qq),"fail");
                }else {

                    hashMap.put(mActivity.get().getString(R.string.details_page_share_qq),"fail");
                }
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.QZONE) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(mActivity.get().getString(R.string.details_page_share_qq_zone),"fail");
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }else if(platform == SHARE_MEDIA.SINA) {
                HashMap<String,String> hashMap = new HashMap<>();
                if (isShortcutShare) {
                    hashMap.put(mActivity.get().getString(R.string.shortcut_share_sina),"fail");
                }else {

                    hashMap.put(mActivity.get().getString(R.string.details_page_share_sina),"fail");
                }
                RecordUtils.onEvent(mActivity.get(),mapkey,hashMap);
            }
            SocializeUtils.safeCloseDialog(progressBar);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            SocializeUtils.safeCloseDialog(progressBar);
            String mapkey = "";
            if(mActivity.get() instanceof VideoPlayVODNotHotelActivity) {
                mapkey = "details_page_share_cancel_collection";
            }else if(mActivity.get() instanceof VideoPlayVODInHotelActivity) {
                mapkey = "bunch_planting_page_share_cancel";
            }
            RecordUtils.onEvent(mActivity.get(),mapkey);
           // Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
            SocializeUtils.safeCloseDialog(progressBar);
        }
    }

    public static boolean isWeixinAvilible(Context context) {

        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        SocializeUtils.safeCloseDialog(progressBar);
        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

}
