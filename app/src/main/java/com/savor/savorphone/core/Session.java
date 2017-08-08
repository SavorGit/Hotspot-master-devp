
package com.savor.savorphone.core;

/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.common.api.codec.binary.Base64;
import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.Pair;
import com.common.api.utils.SaveFileData;
import com.savor.savorphone.bean.HotelMapBean;
import com.savor.savorphone.bean.HotelMapCache;
import com.savor.savorphone.bean.HotelMapListData;
import com.savor.savorphone.bean.PdfInfo;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.StartUpSettingsBean;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.bean.VodBean;
import com.savor.savorphone.utils.STIDUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@SuppressLint("WorldReadableFiles")
public class Session {
    private final static String TAG = "Session";
    /**是否开启调试模式，调试模式连接海强机顶盒*/
    public static final boolean isDev = false;
    /**服务员推广数据*/
    private static final String P_APP_WAITER_DATA = "p_app_waiter_data";
    private int roomid;
    private int hotelid = 0;
    private String LocalIp;
    private Context mContext;
    private SaveFileData mPreference;
    private static  Session mInstance;
    public boolean isDebug = true;
    private int interval;

    /** 是否已经显示引导图，没有显示则显示 */
    private boolean isNeedGuide = true;
    private boolean isScanGuide = true;
    private static final String P_APP_DEVICEID = "pref.savor.deviceid";
    private static final String P_APP_HOTELID = "pref.savor.hotelid";
    private static final String P_APP_PLATFORM_URL = "pref.savor.platformurl";
    private static final String P_APP_COLLECTIONS = "pref.savor.collections";
    private static final String P_APP_COLLECTIONS_URL = "pref.savor.collectionsurl";
    /**是否显示引导图*/
    private static final String P_APP_IS_SHOW_GUIDE = "version_v1.0";

    private static final String P_APP_IS_SHOW_SCAN_GUIDE = "isScanGuide";


    /**应用上次启动时间*/
    private static final String P_APP_LASTSTARTUP = "p_app_laststartup";
    /**首次播放蒙层提示引导图*/
    private static final String P_APP_FIRST_PLAY = "p_app_firstplay";
    /**启动配置*/
    private static final String APP_START_UP_SETTINGS = "app_start_up_settings";
    public static final String SLIDE_INTERVAL = "slide_interval";
    private long lastTime;
    /**
     * 设备deviceId
     */
    private static final String P_APP_NET_TYPE = "pref.savor.net_type";
    /**pdf浏览历史*/
    private static final String P_APP_PDF_LIST = "p_app_pdf_list";

    private static final String P_APP_AREA_ID = "p_app_area_id";
    /**首次使用*/
    private static final String P_APP_FIRST_USE = "p_app_first_use";
    /**最近可投屏酒店*/
    private static final String P_APP_HOTEL_MAP = "p_app_hotel_map";



    /**
     * The version of OS
     */
    private int osVersion;

    /**
     * The Application Debug flag
     */
    private String debugType;

    /**
     * The Application Version Code
     */
    private int versionCode;

    /**
     * The Application package name
     */
    private String packageName;

    /**
     * The Application version name
     */
    private String versionName;

    /**
     * The Application version name
     */
    private String appName;

    /**
     * The mobile IMEI code
     */
    private String imei = "";

    /**
     * The mobile sim code
     */
    private String sim;

    /**
     * The mobile mac address
     */
    private String macAddress;

    /**
     * The mobile model such as "Nexus One" Attention: some model type may have
     * illegal characters
     */
    private String model;

    /**
     * The user-visible version string. E.g., "1.0"
     */
    private String buildVersion;

    /**
     * 网络连接方式
     */
    private String netType;

    private String deviceid;

    private SmallPlatInfoBySSDP smallPlatInfoBySSDP;
    /***/
    private String mTVBoxUrl;

    private int sessionID;
    public static String LocalUrl;
    /**压缩图片的路径*/
    public String mCompressPath;
    /**机顶盒连接的wifi名称*/
    private String sid;
//    /**是否*/
//    private boolean mApConnectionEnable;
    public boolean isApkDownloading = false;
    private String mSsid;
    /**是否首次播放引导图蒙层*/
    private boolean isFirstPlay = true;
    /**小平台地址*/
    private String platformIp;
    /**扫码后缓存的二维码，3分钟后清除*/
    private String qrcode;
    private String channelName;
    private String channelId;
    private StartUpSettingsBean mStartUpSettingsBean;
    /**机顶盒信息*/
    private TvBoxInfo mTvboxInfo;
    /**机顶盒ssdp信息*/
    private TvBoxSSDPInfo mTvBoxSSDPInfo;
    /**是否需要删除启动图或视频*/
    private boolean isDeleteStartUp;
    /**通过云平台获取的小平台信息*/
    private SmallPlatformByGetIp mSmallPlatInfoByIp;
    /**pdf浏览历史*/
    private List<PdfInfo> mPdfList;
    /**区域id*/
    private String area_id;
    /**服务员推广缓存数据（上传失败后会缓存在本地）*/
    private String mWaiterData;
    /**是否已提交首次使用*/
    private boolean mUploadFirstUse;
    private String boxMac;
    /**是否已经写入open日志，启动后如果已经写入就不会再次写入了*/
    private boolean isWriteOpenLog;
    private HotelMapCache mHotelMapCache;
    private HotelMapListData hotelMapListData;
    private double latestLat;
    private double latestLng;

    private Session(Context context) {

        mContext = context;
        mPreference = new SaveFileData(context, "savor");
        osVersion = Build.VERSION.SDK_INT;
        buildVersion = Build.VERSION.RELEASE;
        try {
            model = Build.MODEL;
            AppUtils.clearExpiredFile(context, false);
            AppUtils.clearExpiredCacheFile(context);
            readSettings();
        } catch (Exception e) {
           // LogUtils.e(e.getMessage());
        }
    }
    public void setNetType(String netType) {
        this.netType = netType;
        writePreference(new Pair<String, Object>(P_APP_NET_TYPE, netType));
    }

    public String getNetType() {
        return AppUtils.getNetworkType(mContext)+"";
    }
    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
        writePreference(new Pair<String, Object>(P_APP_LASTSTARTUP, lastTime));
    }
    public void setFirstPlay(boolean firstPlay) {
        this.isFirstPlay = firstPlay;
        writePreference(new Pair<String, Object>(P_APP_FIRST_PLAY, firstPlay));
    }

    public boolean ismUploadFirstUse() {
        return mUploadFirstUse;
    }

    public boolean isFristPlay() {
        return isFirstPlay;
    }
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSessionID() {
        return sessionID;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public boolean isNeedGuide() {
        return isNeedGuide;
    }

    public void setNeedGuide(boolean needGuide) {
        isNeedGuide = needGuide;
        writePreference(new Pair<String, Object>(P_APP_IS_SHOW_GUIDE, needGuide));
    }

    public boolean isScanGuide() {
        return isScanGuide;
    }

    public void setScanGuide(boolean scanGuide) {
        isScanGuide = scanGuide;
        writePreference(new Pair<String, Object>(P_APP_IS_SHOW_SCAN_GUIDE, scanGuide));
    }


    public String getLocalIp() {
        return LocalIp;
    }

    public void setLocalIp(String localIp) {
        LocalIp = localIp;
        setLocalUrl(localIp);
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public static String getLocalUrl() {
        return LocalUrl;
    }

    public static void setLocalUrl(String localUrl) {
        LocalUrl = "http://" + localUrl + ":8080";
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        writePreference(new Pair<String, Object>(SLIDE_INTERVAL, interval));
    }

    public String getTVBoxUrl() {
//        if(isDev) {
//          return "http://192.168.2.125:8080";
//        }else {
            return mTVBoxUrl;
//        }
    }

    public boolean isBindTv() {
//        if(isDev) {
//         return true;
//        }else {
            return !TextUtils.isEmpty(mTVBoxUrl);
//        }
    }

    public boolean isApkDownloading() {
        return isApkDownloading;
    }

    public void setApkDownloading(boolean isApkDownloading) {
        this.isApkDownloading = isApkDownloading;
    }
    public void resetPlatform() {
        mTVBoxUrl = null;
    }
    public void setTVBoxUrl(String tvBoxUrl) {
        Uri parse = Uri.parse(tvBoxUrl);
        String ip = parse.getQueryParameter("ip");
        String bId = parse.getQueryParameter("bid");
        String rId = parse.getQueryParameter("rid");
        String sid = parse.getQueryParameter("sid");
        tvBoxUrl = "http://" + ip + ":8080";

        int hotelId = -1;
        int roomId = -1;
        try {
            hotelId = Integer.parseInt(bId);
            roomId = Integer.parseInt(rId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(hotelId!=-1)
            setHotelid(hotelId);
        setRoomid(roomId);
        setWifiSsid(sid);
        LogUtils.d("PlatformUrl=" + tvBoxUrl);
        this.mTVBoxUrl = tvBoxUrl;
    }

    public void setWaiterData(String waiterData) {
        this.mWaiterData = waiterData;
        writePreference(new Pair<String, Object>(P_APP_WAITER_DATA, waiterData));
    }

    public String getWaiterData() {
        return this.mWaiterData;
    }

    public void setTvBoxInfo(TvBoxInfo info) {
        this.mTvboxInfo = info;
    }

    public void setTvBoxUrl(TvBoxInfo info) {
        this.mTvboxInfo = info;
        if (info == null) {
            return;
        }
        String box_ip = info.getBox_ip();
        String hotel_id = info.getHotel_id();
        String room_id = info.getRoom_id();
        String ssid = info.getSsid();
        String box_mac = info.getBox_mac();
        String tvBoxUrl = "http://" + box_ip + ":8080";
        int hotelId = 0;
        int roomId = 0;
        try {
            hotelId = Integer.parseInt(hotel_id);
            roomId = Integer.parseInt(room_id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        setHotelid(hotelId);
        setRoomid(roomId);
        setWifiSsid(ssid);
        setBoxMac(box_mac);
        this.mTVBoxUrl = tvBoxUrl;
    }

    public TvBoxInfo getTvboxInfo() {
        return this.mTvboxInfo;
    }

    public void setTvBoxIp(String tvboxIp) {
        this.mTVBoxUrl = tvboxIp;
    }


    public int getRoomid() {
//        if(isDev)
//            return 1555;
//        else
        return roomid;
    }

    public void setStartUpSettings(StartUpSettingsBean bean) {
        this.mStartUpSettingsBean = bean;
        setObj(APP_START_UP_SETTINGS,bean);
    }

    public StartUpSettingsBean getStartUpSettings() {
        return mStartUpSettingsBean;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public int getHotelid() {
//        if(isDev)
            return hotelid;
//        else
//         return 156;
    }

    public void setHotelid(int hotelid) {
        this.hotelid = hotelid;
    }

    public static Session get(Context context) {

        if (mInstance == null) {
            synchronized (Session.class) {
                if(mInstance == null) {
                    mInstance = new Session(context);
                }
            }
        }
        return mInstance;
    }

    public String getCompressPath() {
        return mCompressPath;
    }

    public void setmCompressPath(String mCompressPath) {
        this.mCompressPath = mCompressPath;
    }

    private void readSettings() {
//        mHotelMapCache = (HotelMapCache) getObj(P_APP_HOTEL_MAP);
        mUploadFirstUse = mPreference.loadBooleanKey(P_APP_FIRST_USE,false);
        mWaiterData = mPreference.loadStringKey(P_APP_WAITER_DATA,null);
        mPdfList = (List<PdfInfo>) getObj(P_APP_PDF_LIST);
        mStartUpSettingsBean = (StartUpSettingsBean) getObj(APP_START_UP_SETTINGS);
        mCompressPath = getCompressPath(mContext);
        deviceid = STIDUtil.getDeviceId(mContext);
        netType = mPreference.loadStringKey(P_APP_NET_TYPE, "");
        isNeedGuide = mPreference.loadBooleanKey(P_APP_IS_SHOW_GUIDE, isNeedGuide);
        isScanGuide = mPreference.loadBooleanKey(P_APP_IS_SHOW_SCAN_GUIDE, isScanGuide);
        interval = mPreference.loadIntKey(SLIDE_INTERVAL, 10);
        lastTime = mPreference.loadLongKey(P_APP_LASTSTARTUP,0);
        isFirstPlay = mPreference.loadBooleanKey(P_APP_FIRST_PLAY,true);
        area_id = mPreference.loadStringKey(P_APP_AREA_ID, "");

        setDeviceid(deviceid);
        getApplicationInfo();

        /** 清理App缓存 */
        AppUtils.clearExpiredFile(mContext, false);

        /** 刷机防止操作 */
//        readDeviceNum();
    }

    /**获取存放压缩图片的目录*/
    public String getCompressPath(Context context) {
        String path = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            path+=path.endsWith(File.separator)?"":File.separator;
            path+=".Gallery/";
        }else {
            path = context.getCacheDir().getAbsolutePath();
            path+=path.endsWith(File.separator)?"":File.separator;
            path+=".Gallery/";
        }
        LogUtils.d("gallery path:"+path);
        return path;
    }

    public void setPdfList(List<PdfInfo> pdfList) {
        this.mPdfList = pdfList;
        setObj(P_APP_PDF_LIST,pdfList);
    }

    public List<PdfInfo> getPdfList() {
        return this.mPdfList;
    }

    /*
     * 读取App配置信息
     */
    private void getApplicationInfo() {

        final PackageManager pm = mContext.getPackageManager();
        try {
            final PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                    0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;

            final ApplicationInfo ai = pm.getApplicationInfo(
                    mContext.getPackageName(), PackageManager.GET_META_DATA);
			channelName = ai.metaData.get("UMENG_CHANNEL").toString();
			channelId = STIDUtil.getChannelIdByChannelName(channelName);
            debugType = ai.metaData.get("app_debug").toString();

            if ("1".equals(debugType)) {
                // developer mode
                isDebug = true;
            } else if ("0".equals(debugType)) {
                // release mode
                isDebug = false;
            }
            LogUtils.allow = isDebug;

            appName = String.valueOf(ai.loadLabel(pm));
            LogUtils.appTagPrefix = appName;
            packageName = mContext.getPackageName();

            TelephonyManager telMgr = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telMgr.getDeviceId();
            sim = telMgr.getSimSerialNumber();
        } catch (NameNotFoundException e) {
            Log.d(TAG, "met some error when get application info");
        }
    }

    public void readDeviceNum() {
//        String path = AppUtils.getPath(mContext, StorageFile.other);
//        String readNum;
//        File destFile = new File(path, "num.txt");
//        try {
//            if (destFile.exists()) {
//                readNum = FileUtils.readFileToString(destFile);
//                if (!TextUtils.isEmpty(readNum)) {
//                }
//            } else {
//                readNum = getMac() + "-" + System.currentTimeMillis();
//                destFile.createNewFile();
//                FileUtils.writeStringToFile(destFile, readNum);
//            }
//        } catch (Exception e) {
//            LogUtils.e(e.toString());
//        }

    }


    /**
     * 移除用户添加的Key，轻易不要使用
     *
     * @param key
     */
    public void removeKey(String key) {
        mPreference.removeKey(key);
    }

    private void writePreference(Pair<String, Object> updateItem) {
        //
        // // the preference key
        final String key = (String) updateItem.first;

        if ("".equals(key) ||P_APP_PLATFORM_URL.equals(key)
                ||P_APP_AREA_ID.equals(key)
                || P_APP_WAITER_DATA.equals(key)) {
            mPreference.saveStringKey(key, (String) updateItem.second);
        }else if(P_APP_IS_SHOW_GUIDE.equals(key)
                ||P_APP_FIRST_PLAY.equals(key)
                ||P_APP_IS_SHOW_SCAN_GUIDE.equals(key)
                ||P_APP_FIRST_USE.equals(key)){
            mPreference.saveBooleanKey(key,(boolean)updateItem.second);
        }else if(SLIDE_INTERVAL.equals(key)||P_APP_HOTELID.equals(key)){
            mPreference.saveIntKey(key,(Integer) updateItem.second);
        }else if(P_APP_LASTSTARTUP.equals(key)) {
            mPreference.saveLongKey(key,(Long)updateItem.second);
        }
// else if () {
//            mPreference.saveBooleanKey(key, (boolean) updateItem.second);
//        }
    else {//  默认存放对象Object这样的数据 情况特殊，一般类型的数据最好还是写上键值对，方便操作
            String string = ObjectToString(updateItem.second);
            mPreference.saveStringKey(key, string);
        }
    }

    private Object getObj(String key) {
        String string = mPreference.loadStringKey(key, "");
        Object object = null;
        if (!TextUtils.isEmpty(string)) {
            try {
                object = StringToObject(string);
            } catch (Exception ex) {
                Log.e("wang", "异常" + ex.toString());
            }
        }
        return object;
    }

    private void setObj(String key, Object obj) {
        try {
            writePreference(new Pair<String, Object>(key, obj));
        } catch (Exception ex) {
            Log.e("wang", ex.toString());
        }
    }

    private String ObjectToString(Object obj) {
        String productBase64 = null;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            productBase64 = new String(Base64.encodeBase64(baos.toByteArray()));
        } catch (Exception e) {
            Log.e("错误", "保存错误" + e.toString());
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return productBase64;
    }

    private Object StringToObject(String str) {
        Object obj = null;
        byte[] base64Bytes;
        ByteArrayInputStream bais = null;
        try {
            String productBase64 = str;
            if (null == productBase64
                    || TextUtils.isEmpty(productBase64.trim())) {
                return null;
            }

            base64Bytes = Base64.decodeBase64(productBase64.getBytes());
            bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
            ois.close();
        } catch (Exception e) {
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }



    public int getVersionCode() {
        if (versionCode <= 0) {
            getApplicationInfo();
        }
        return versionCode;
    }


    public String getMac() {
        if (TextUtils.isEmpty(macAddress)) {
            try {
                WifiManager wifi = (WifiManager) mContext
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                macAddress = info.getMacAddress();
            } catch (Exception ex) {
                LogUtils.e(ex.toString());
            }
        }
        return macAddress;
    }


    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
        writePreference(new Pair<String, Object>(P_APP_DEVICEID, deviceid));
    }


    /**
     * 返回设备相关信息
     */
    public String getDeviceInfo() {
        StringBuffer buffer = new StringBuffer();
		buffer.append("versionname=");
		buffer.append(versionName);
        buffer.append(";versioncode=");
        buffer.append(versionCode);
        buffer.append(";buildversion=");
        buffer.append(buildVersion);
        buffer.append(";osversion=");
        buffer.append(osVersion);
        buffer.append(";model=");
        buffer.append(model);
        buffer.append(";appname=");
        buffer.append("hotSpot");
        buffer.append(";clientname=");
        buffer.append("android");
        buffer.append(";channelid=");
        buffer.append(channelId);
        buffer.append(";channelName=");
        buffer.append(channelName);
        buffer.append(";deviceid=");
        buffer.append(deviceid);
        buffer.append(";network=");
        /** 需要修改 */
        buffer.append(getNetType());
        buffer.append(";language=");
        buffer.append("");
        buffer.append(";location=");
        buffer.append(latestLng+","+latestLat);
//        buffer.append(";imei=");
//        buffer.append(imei);
//        buffer.append(";sim=");
//        buffer.append(sim);
//        buffer.append(";macaddress=");
//        buffer.append(getMac());

//        buffer.append(";source=");
//        buffer.append("22");

//        TimeZone timeZone = TimeZone.getDefault();
//        buffer.append(";systemtimezone=");
//        buffer.append(timeZone.getID());

        /** 加入流水号 */
//		buffer.append(";seq=");
//		buffer.append(imei + AppUtils.getXuHao());
//		buffer.append(";num=");
//		buffer.append(num);
        // LogUtils.e(buffer.toString());
        return buffer.toString();
    }

    /**获取小平台信息*/
    public SmallPlatInfoBySSDP getSmallPlatInfoBySSDP() {
        return smallPlatInfoBySSDP;
    }

    /**是否有可连接设备，是否获取到小平台*/
    public boolean isSmallPlatformEnable() {
        return !TextUtils.isEmpty(platformIp);
    }

    public void setSmallPlatInfoBySSDP(SmallPlatInfoBySSDP smallPlatInfoBySSDP) {
        this.smallPlatInfoBySSDP = smallPlatInfoBySSDP;
    }

    public void collectVod(VodBean vodAndTopicItemVo) {
        List<VodBean> collections = getCollections();
        List<String> collectionsUrl = getCollectionsUrl();
        if(collections == null){
            collections = new ArrayList<>();
        }

        if (collectionsUrl == null) {
            collectionsUrl = new ArrayList<>();
        }

//        if(collections.contains(vodAndTopicItemVo))
//            collections.remove(vodAndTopicItemVo);
//        collections.add(0,vodAndTopicItemVo);
//        setObj(P_APP_COLLECTIONS,collections);

        if (containsUrl(vodAndTopicItemVo.getContentURL())) {
            collections.remove(vodAndTopicItemVo);
            collectionsUrl.remove(vodAndTopicItemVo.getContentURL());
        }

        collections.add(0,vodAndTopicItemVo);
        collectionsUrl.add(0,vodAndTopicItemVo.getContentURL());
        setObj(P_APP_COLLECTIONS,collections);
        setObj(P_APP_COLLECTIONS_URL,collectionsUrl);

    }

    public List<VodBean> getCollections() {
        Object obj = getObj(P_APP_COLLECTIONS);
        if(obj instanceof List)
            return (List<VodBean>) obj;
        return null;
    }

    public List<String> getCollectionsUrl() {
        Object obj = getObj(P_APP_COLLECTIONS_URL);
        if(obj instanceof List)
            return (List<String>) obj;
        return null;
    }
    public boolean contains(VodBean itemVo) {
        List<VodBean> collections = getCollections();
        if(collections!=null) {
            return collections.contains(itemVo);
        }
        return false;
    }

    public boolean containsUrl(String url) {
        List<String> collections = getCollectionsUrl();
        if(collections!=null) {
            return collections.contains(url);
        }
        return false;
    }

    public void remoeCollect(VodBean itemVo) {
        List<VodBean> collections = getCollections();

        if(collections!=null) {

            for (int i = 0; i < collections.size(); i++){
                VodBean vo = collections.get(i);
                if (vo.getContentURL().equals(itemVo.getContentURL())) {
                    collections.remove(vo);
                }
            }
            remoeCollectUrl(itemVo.getContentURL());
        }
        setObj(P_APP_COLLECTIONS,collections);
    }

    public void remoeCollectUrl(String url) {
        List<String> collections = getCollectionsUrl();

        if(collections!=null) {
            collections.remove(url);
        }
        setObj(P_APP_COLLECTIONS_URL,collections);
    }

//    public void setApConnection(boolean b) {
//        this.mApConnectionEnable = b;
//    }


    public void setWifiSsid(String wifiName) {
        this.mSsid = wifiName;
    }

    public String getSsid() {
        return mSsid;
    }


    public void setSmallIp(String platformIp) {
        this.platformIp = platformIp;
    }

    public String getPlatformUrl() {
        return  platformIp;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getQrcode() {
        return this.qrcode;
    }

    public void setTvBoxSSDPInfo(TvBoxSSDPInfo tvBoxSSDPInfo) {
        this.mTvBoxSSDPInfo = tvBoxSSDPInfo;
    }

    public TvBoxSSDPInfo getTvBoxSSDPInfo() {
        return this.mTvBoxSSDPInfo;
    }

    public void setDeleteStartUp(boolean isDeleteStartUp) {
        this.isDeleteStartUp = isDeleteStartUp;
    }

    public boolean isDeleteStartUp() {
        return this.isDeleteStartUp;
    }

    public void setSmallPlatInfoByGetIp(SmallPlatformByGetIp smallPlatformByGetIp) {
        this.mSmallPlatInfoByIp = smallPlatformByGetIp;
        setArea_id(smallPlatformByGetIp.getArea_id());
    }

    public SmallPlatformByGetIp getmSmallPlatInfoByIp() {
        return mSmallPlatInfoByIp;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
        writePreference(new Pair<String, Object>(P_APP_AREA_ID, area_id));
    }

    public void setIsUploadFirstuse(boolean isUploadFirstuse) {
        this.mUploadFirstUse = isUploadFirstuse;
        writePreference(new Pair<String, Object>(P_APP_FIRST_USE, isUploadFirstuse));
    }

    public void setBoxMac(String boxMac) {
        this.boxMac = boxMac;
    }

    public String getBoxMac() {
        return boxMac;
    }

    public void setIsWriteOpenLog(boolean isWriteOpenLog) {
        this.isWriteOpenLog = isWriteOpenLog;
    }

    public boolean isWriteOpenLog() {
        return this.isWriteOpenLog;
    }

    public void setHotelMapCache(HotelMapCache cache) {
        this.mHotelMapCache = cache;
//        writePreference(new Pair<String, Object>(P_APP_HOTEL_MAP, cache));
    }

    public HotelMapCache getmHotelMapCache() {
        return mHotelMapCache;
    }

    public void setHotelMapList(HotelMapListData hotelMapListData) {
        this.hotelMapListData = hotelMapListData;
    }

    public HotelMapListData getHotelMapList() {
        return hotelMapListData;
    }

    public void setLatestLat(double latestLat) {
        this.latestLat = latestLat;
    }

    public void setLatestLng(double latestLng) {
        this.latestLng = latestLng;
    }

    public double getLatestLat() {
        return latestLat;
    }

    public double getLatestLng() {
        return latestLng;
    }
}