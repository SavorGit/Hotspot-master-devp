package com.savor.savorphone.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.activity.HotspotMainActivity;
import com.savor.savorphone.activity.IBindTvView;
import com.savor.savorphone.activity.LinkTvActivity;
import com.savor.savorphone.activity.LocalVideoProAcitvity;
import com.savor.savorphone.activity.PdfPreviewActivity;
import com.savor.savorphone.activity.SingleImageProActivity;
import com.savor.savorphone.activity.SlidesActivity;
import com.savor.savorphone.activity.VideoPlayVODInHotelActivity;
import com.savor.savorphone.bean.RotateProResponse;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.interfaces.IBaseView;
import com.savor.savorphone.interfaces.IHotspotSenseView;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.receiver.NetworkConnectChangedReceiver;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.service.SSDPService;
import com.savor.savorphone.utils.ActivitiesManager;

import java.util.List;

import static com.savor.savorphone.activity.HotspotMainActivity.SCAN_QR;
import static com.savor.savorphone.core.AppApi.Action.GET_CALL_CODE_BY_BOXIP_JSON;

/**
 * 绑定电视主导器
 * Created by hezd on 2016/12/16.
 */
public class BindTvPresenter extends BasePresenter implements ApiRequestListener {
    private static final int START_LINKTV = 100;
    private static final int CLOSE_DIALOG = 101;
    private static final int CALL_QRCODE = 102;
    private static final int CALL_CODE_ERRO = 103;
    private static final int GET_SMALL_FLATFORM = 104;
    private static final int CHECK_PLATFORM = 105;
    private static final int QRCODE_BY_LOCAL = 106;
    private static final int REMOVE_QRCODE = 107;
    /**清除机顶盒信息*/
    private static final int REMOVE_BOX_INFO = 108;
    private static final int SAVE_BOX_INFO = 109;
    private static final int CLOSE_SSDP_SERVICE = 110;
    /**错误总数，比如请求两个接口，errorMax失败两次认为请求失败*/
    private  int errorMax = 4;
    private IBindTvView mBindTvView;
    private IHotspotSenseView mSenseView;
    private NetworkConnectChangedReceiver mChangedReceiver;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLOSE_SSDP_SERVICE:
                    LogUtils.d("savor:ssdp 搜索10秒，关闭ssdp服务");
                    Intent ssdpIntent = new Intent(mContext, SSDPService.class);
                    mContext.stopService(ssdpIntent);
                    break;
                case SAVE_BOX_INFO:
                    Object obj = msg.obj;
                    if(obj instanceof TvBoxInfo) {
                        TvBoxInfo tvBoxInfo = (TvBoxInfo) obj;
                        mSession.setTvBoxUrl(tvBoxInfo);
                        mBindTvView.initBindcodeResult();
                    }
                    break;
                case SCAN_QR:
                    LogUtils.d("二维码结果：" + msg.obj);
                    if (TextUtils.isEmpty(msg.obj + "")) {
                        mViewCallBack.showToast("无效的数字");
                        return;
                    }
                    // 绑定tv
                    mSession.setTVBoxUrl(msg.obj + "");
                    mBindTvView.initBindcodeResult();
                    break;
                case START_LINKTV:
                    if(mBindTvView!=null) {
                        mBindTvView.closeQrcodeDialog();
                        String topNameActivity = getTopActivityName(mContext);
                        if(!topNameActivity.equals(LinkTvActivity.class.getClass().getName())){
                            mBindTvView.startLinkTv();
                        }
                    }
                case CLOSE_DIALOG:
                    if(mBindTvView!=null)
                        mBindTvView.closeQrcodeDialog();
                    break;
                case CALL_QRCODE:

                    break;
                case CALL_CODE_ERRO:
                    mViewCallBack.showToast("呼出二维码网络超时");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    LogUtils.d("savor:网络变为可用");
                    // 为了解决多次重复发送请求利用延时发送方式
                    getSmallPlatformUrl(true);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    LogUtils.d("savor:hotel 网络不可用重置酒店id为0");
                    mSession.resetPlatform();
                    mSession.setHotelid(0);
                    mSenseView.hideProjection();
                    stopProAndLink();
                    break;
                case CHECK_PLATFORM:
                    getSmallPlatformUrl(false);
                    break;
                case GET_SMALL_FLATFORM:
                    getSmallPlatformUrl(false);
                    break;
                case REMOVE_QRCODE:
                    mSession.setQrcode(null);
                    break;
                case REMOVE_BOX_INFO:
                    mSession.setTvBoxUrl(null);
                    break;
            }
        }
    };

    /**
     * 退出投屏断开连接状态
     */
    private void stopProAndLink() {
        Class projectionActivity = ProjectionManager.getInstance().getProjectionActivity();
        if(projectionActivity == SingleImageProActivity.class) {
            SingleImageProActivity iGActivity = (SingleImageProActivity) ActivitiesManager.getInstance().getSpecialActivity(projectionActivity);
            if (iGActivity!=null){
                iGActivity.stopPro();
            }
        }else if(projectionActivity == VideoPlayVODInHotelActivity.class) {
            ActivitiesManager.getInstance().popSpecialActivity(projectionActivity);
            ProjectionService projectionService = SavorApplication.getInstance().projectionService;
            if(projectionService!=null) {
                projectionService.stopQuerySeek();
            }
        }else if(projectionActivity == LocalVideoProAcitvity.class) {
            ActivitiesManager.getInstance().popSpecialActivity(projectionActivity);
            ProjectionService projectionService = SavorApplication.getInstance().projectionService;
            if(projectionService!=null) {
                projectionService.stopQuerySeek();
            }
        }else if(projectionActivity == SlidesActivity.class) {
            SlidesActivity slidesActivity = (SlidesActivity) ActivitiesManager.getInstance().getSpecialActivity(projectionActivity);
            if (slidesActivity!=null){
                slidesActivity.stopSlideProjection();
            }
            ProjectionService projectionService = SavorApplication.getInstance().projectionService;
            if(projectionService!=null) {
                projectionService.stopSlide();
            }
        }else if(projectionActivity == PdfPreviewActivity.class){
            Activity currentActivity = ActivitiesManager.getInstance().getCurrentActivity();
            if(currentActivity instanceof PdfPreviewActivity) {
                PdfPreviewActivity pdfPreviewActivity = (PdfPreviewActivity) ActivitiesManager.getInstance().getCurrentActivity();
                pdfPreviewActivity.stopPdfPro();
            }
        }
        HotspotMainActivity mainActivity = (HotspotMainActivity)ActivitiesManager.getInstance().getSpecialActivity(HotspotMainActivity.class);
        if (mainActivity!=null){
            mainActivity.initBackgroundProjectionHint();
            mainActivity.disconnectTv();
        }
        Activity currentActivity = ActivitiesManager.getInstance().getCurrentActivity();
        if(projectionActivity!=null) {
            ShowMessage.showToast(currentActivity,"与电视断开连接，请重试");
        }
    }

    private String errorMsg = "呼码失败";

    /**
     * 通过机顶盒提供的小平台地址进行呼玛
     * */
    private void callcodeBysmallFromBox() {
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        if(tvBoxSSDPInfo!=null) {
            String serverIp = tvBoxSSDPInfo.getServerIp();
            String commandPort = tvBoxSSDPInfo.getCommandPort();
            String hotelId = tvBoxSSDPInfo.getHotelId();
            String type = tvBoxSSDPInfo.getType();
            if(!TextUtils.isEmpty(serverIp)
                    &&!TextUtils.isEmpty(commandPort)
                    &&!TextUtils.isEmpty(hotelId)
                    &&!"-1".equals(commandPort)
                    &&!TextUtils.isEmpty(type)) {
                AppApi.callQrcodeFromBoxInfo(mContext,tvBoxSSDPInfo,this);
            }else {
                callQrcodeErrorCount++;
            }
        }else {
            callQrcodeErrorCount++;
        }
    }

    private void callcodeByBoxIp() {
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        if(tvBoxSSDPInfo!=null) {
            String boxIp = tvBoxSSDPInfo.getBoxIp();
            if (!TextUtils.isEmpty(boxIp)) {
                String boxUrl = "http://"+boxIp+":8080";
                AppApi.callCodeByBoxIp(mContext,boxUrl,this);
            }else {
                callQrcodeErrorCount++;
            }
        }else {
            callQrcodeErrorCount++;
        }
    }

    private Session mSession;
    /**是否首次请求，如果第一次请求没获取到小平台地址，不在请求第二次*/
    public boolean mFirstRequest = true;
    /**是否从后台切到前台发起请求*/
    private boolean isOnRestart;
    /**解决打开两次扫码页面，判断如果小平台请求成功打开扫码页面标示*/
    private boolean mCallQrByPlatform;
    /**解决打开两次扫码页面，判断本地呼码是否成功*/
    private boolean mCallQrByLocalIp;
    /**是否已打开扫码页面*/
    private boolean mOpenQrCode;
    /**扫描失败的次数2表示点对点和小平台请求扫描都失败*/
    private int callQrcodeErrorCount;
    /**重新请求小平台时，是否需要刷新首页数据*/
    private boolean isNeedRefresh;

    public BindTvPresenter(Context context, IBaseView viewCallBack, IBindTvView hotspotView, IHotspotSenseView senseView) {
        super(context, viewCallBack);
        this.mBindTvView = hotspotView;
        this.mSenseView = senseView;
    }

    public BindTvPresenter(Context context) {
        super(context,null);
    }

    @Override
    public void onCreate() {
        mSession = Session.get(mContext);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_STATICS_FIRSTUSE_JSON:
                LogUtils.d("savor:firstUse 首次使用上传成功");
                mSession.setIsUploadFirstuse(true);
                break;
            case POST_PHOTO_ROTATE_JSON:
                if(obj instanceof RotateProResponse) {
                    RotateProResponse rotateResponse = (RotateProResponse) obj;
                    mBindTvView.rotate(rotateResponse);
                }
                break;
            case GET_CALL_QRCODE_JSON:
            case GET_CALL_CODE_BY_BOXIP_JSON:
                handleQrcodeSuccess();
                break;
            case GET_SAMLL_PLATFORMURL_JSON:
                // 获取小平台地址
                if(obj instanceof SmallPlatformByGetIp) {
                    SmallPlatformByGetIp cloudServerInfo = (SmallPlatformByGetIp) obj;
                    LogUtils.d("savor:hotel sp info:"+cloudServerInfo);
                    if (cloudServerInfo != null) {
                        String serverIp = cloudServerInfo.getIp();
                        String hotelId = cloudServerInfo.getHotelId();
                        // 保存酒店id
                        int hid = 0;
                        try {
                            hid = Integer.valueOf(hotelId);
                        }catch (Exception e) {
                        }


                        // 如果hotelid不同并且isneedrefresh为true
                        if(isNeedRefresh) {
                            if(mSenseView!=null) {
                                if(hid != mSession.getHotelid()) {
                                    mSenseView.refreshData();
                                    if(hid>0) {
                                        mSession.setHotelid(hid);
                                    }
                                }
                                mSenseView.checkSense();
                            }
                        }

                        // 保存小平台信息
                        mSession.setSmallPlatInfoByGetIp(cloudServerInfo);

                    }else {
                        mBindTvView.showChangeWifiDialog();
                    }
                }
                break;
        }
    }

    /**
     * 处理扫码成功
     */
    private void handleQrcodeSuccess() {
        if(!mOpenQrCode) {
            mHandler.removeMessages(START_LINKTV);
            mHandler.sendEmptyMessage(START_LINKTV);
            mOpenQrCode = true;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
            case GET_SAMLL_PLATFORMURL_JSON:
                LogUtils.d("savor:hotel getIp获取失败");
                break;
            case GET_CALL_QRCODE_JSON:
            case GET_CALL_CODE_BY_BOXIP_JSON:
                handleErroResponse(method, obj);
                handleCallQrcodeError();
                break;
        }

    }

    private void handleErroResponse(AppApi.Action method, Object obj) {
        if(obj instanceof ResponseErrorMessage) {
            if(method != GET_CALL_CODE_BY_BOXIP_JSON) {
                ResponseErrorMessage msg = (ResponseErrorMessage) obj;
                String message = msg.getMessage();
                int code = msg.getCode();
                if (AppApi.ERROR_TIMEOUT.equals(code)){
                    message = "呼码超时";
                }else if (AppApi.ERROR_NETWORK_FAILED.equals(code)){
                    message = "呼码失败";
                }
                if(!TextUtils.isEmpty(message))
                    errorMsg = message;
            }else {
                errorMsg = "呼码失败";
            }
        }else {
            errorMsg = "呼码失败";
        }
    }

    /**
     * 处理呼码失败
     */
    private void handleCallQrcodeError() {
        callQrcodeErrorCount++;
        if(callQrcodeErrorCount == errorMax) {
            if(mBindTvView!=null) {
                if(!mOpenQrCode) {
                    mBindTvView.showToast(errorMsg);
                }
                mHandler.sendEmptyMessage(CLOSE_DIALOG);
            }
        }
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {
        mViewCallBack.showToast(mContext.getString(R.string.network_error));
    }

    /**
     * 设置两个标识扫码页面是否已打开和当前失败次数。
     * 首先重置状态，然后如果页面已经打开不在重新打开页面。
     * 如果失败count为指定次数在提示扫码失败
     *
     */
    public void bindTv() {
        if(AppUtils.isWifiNetwork(mContext)) {
            if(!AppUtils.isNetworkAvailable(mContext)) {
                mBindTvView.showToast("网络不可用");
                return;
            }
            mBindTvView.readyForCode();
            callQrcode();
        }else {
            mBindTvView.showChangeWifiDialog();
        }
    }

    public void callQrcode() {
        resetCallQrcodeState();
        //  1.小平台ssdp机顶盒ssdp 云平台getip都获取到，进行三次呼玛errormax 3
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        if(smallPlatInfoBySSDP!=null
                &&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())
                &&!TextUtils.isEmpty(smallPlatInfoBySSDP.getType())) {
            AppApi.callQrcodeBySPSSDP(mContext,this);
        }else {
            callQrcodeErrorCount++;
        }
        // 2.getip获取的小平台地址进行呼玛
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        if(smallPlatformByGetIp!=null
                &&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())
                &&!TextUtils.isEmpty(smallPlatformByGetIp.getType())) {
            AppApi.callQrcodeByClound(mContext,this);
        }else {
            callQrcodeErrorCount++;
        }
        // 3.机顶盒获取的小平台地址进行呼码
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        if(tvBoxSSDPInfo!=null
                &&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
            AppApi.callQrcodeFromBoxInfo(mContext,tvBoxSSDPInfo,this);
        }else {
            callQrcodeErrorCount++;
        }

        // 4.点对点呼码
        if(tvBoxSSDPInfo!=null
                &&!TextUtils.isEmpty(tvBoxSSDPInfo.getBoxIp())) {
            String boxUrl = "http://"+tvBoxSSDPInfo.getBoxIp()+":8080";
            AppApi.callCodeByBoxIp(mContext,boxUrl,this);
        }else {
            callQrcodeErrorCount++;
        }

    }

    /**
     * 重置扫码状态
     * mOpenQrCode 是否已经打开扫码页面，false未打开，true已打开
     * callQrcodeErrorCount 当前扫码请求失败次数，如果为2（点对点和小平台请求扫码）呼码失败
     */
    private void resetCallQrcodeState() {
        mOpenQrCode = false;
        callQrcodeErrorCount = 0;
    }

    /**处理三位数字结果*/
    public void handleQrcodeResult(String scanResult) {
        if (TextUtils.isEmpty(scanResult)) {
            mBindTvView.showToast("无效的二维码");
            return;
        }
        LogUtils.e("length= " + scanResult.split("&").length);
        if (scanResult.split("&").length != 4) {
            mBindTvView.showToast("连接失败，请扫描电视中的二维码");
            return;
        }

        // 将二维码内容缓存到session，延时3分钟清除。每当应用切回到前台，判断ssid是否与当前相同如果相同
        // 绑定成功
        mSession.setQrcode(scanResult);
        mHandler.sendEmptyMessageDelayed(REMOVE_QRCODE,3*60*1000);

        WifiManager wifiManger = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManger.getConnectionInfo();
        String localIp = intToIp(wifiInfo.getIpAddress());
        mSession.setLocalIp(localIp);
        LogUtils.d("localIp:"+localIp);
        String ipStr = Uri.parse(scanResult).getQueryParameter("ip");
        String wifiName = getWifiName(mContext);
        if(!TextUtils.isEmpty(wifiName)) {
            wifiName = wifiName.replace("\"","");
        }
        String sid = Uri.parse(scanResult).getQueryParameter("sid");
        mSession.setWifiSsid(sid);
//        // 如果判断不在一个网段或者wifi不是同一个提示切换wifi
        if (TextUtils.isEmpty(ipStr)||!localIp.substring(0,localIp.lastIndexOf(".")).equals(ipStr.substring(0, ipStr.lastIndexOf(".")))
                ||TextUtils.isEmpty(wifiName)
                ||!wifiName.equals(sid)) {
            mBindTvView.showChangeWifiDialog();
//            mBindTvView.showToast(mContext.getResources().getString(R.string.connect_fail_tip));
            return;
        }

        // 扫码完判断如果酒店id和之前酒店id不同则刷新热点数据
//        String bid = Uri.parse(scanResult).getQueryParameter("bid");
//        String hotelid = String.valueOf(mSession.getHotelid());
//        if(!hotelid.equals(bid)) {
//            // 刷新首页数据
//            Intent intent = new Intent(mContext, HotspotFragment.RefreshReceiver.class);
//            mContext.sendBroadcast(intent);
//        }
        Message obtain = Message.obtain();
        obtain.what = SCAN_QR;
        obtain.obj = scanResult;
        mHandler.sendMessage(obtain);
    }

    /**处理三位数字结果*/
    public void handleBindCodeResult(TvBoxInfo tvBoxInfo) {
        if (tvBoxInfo == null) {
            mBindTvView.showToast("无效的机顶盒信息");
            return;
        }

        // 将机顶盒内容缓存到session，延时3分钟清除。每当应用切回到前台，判断ssid是否与当前相同如果相同
        // 绑定成功
        mSession.setTvBoxInfo(tvBoxInfo);
        mHandler.removeMessages(REMOVE_BOX_INFO);
        mHandler.sendEmptyMessageDelayed(REMOVE_BOX_INFO,3*60*1000);

        WifiManager wifiManger = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManger.getConnectionInfo();
        String wifiName = getWifiName(mContext);
        if(!TextUtils.isEmpty(wifiName)) {
            wifiName = wifiName.replace("\"","");
        }
        String sid = tvBoxInfo.getSsid();
        String localIp = intToIp(wifiInfo.getIpAddress());
        String ipStr = tvBoxInfo.getBox_ip();
        mSession.setLocalIp(localIp);
        mSession.setWifiSsid(sid);
//        // 如果判断不在一个网段或者wifi不是同一个提示切换wifi
        if (TextUtils.isEmpty(ipStr)||!localIp.substring(0,localIp.lastIndexOf(".")).equals(ipStr.substring(0, ipStr.lastIndexOf(".")))
                ||TextUtils.isEmpty(wifiName)
                ||!wifiName.equals(sid)) {
            mBindTvView.showChangeWifiDialog();
//            mBindTvView.showToast(mContext.getResources().getString(R.string.connect_fail_tip));
            return;
        }

        // 扫码完判断如果酒店id和之前酒店id不同则刷新热点数据
//        String bid = tvBoxInfo.getHotel_id();
//        String hotelid = String.valueOf(mSession.getHotelid());
//        if(!hotelid.equals(bid)) {
//            // 刷新首页数据
//            Intent intent = new Intent(mContext, HotspotFragment.RefreshReceiver.class);
//            mContext.sendBroadcast(intent);
//        }
        Message obtain = Message.obtain();
        obtain.what = SAVE_BOX_INFO;
        obtain.obj = tvBoxInfo;
        mHandler.sendMessage(obtain);
    }

    public  String getWifiName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        if(!TextUtils.isEmpty(wifiId)) {
            wifiId = wifiId.replace("\"","");
        }
        return wifiId;
    }

    /**
     * 格式化ip
     *
     * @param i
     * @return
     */
    public String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    /**当应用切回前台时重新获取小平台地址
     * @param isNeedRefresh 是否需要刷新首页数据
     * */
    public void getSmallPlatformUrl(boolean isNeedRefresh) {
        LogUtils.d("getSmallPlatformUrl........");
        if(!AppUtils.isWifiNetwork(mContext)) {
            LogUtils.d("savor:sp 当前wifi不可用请求getip，不启动ssdp获取酒店id");
            mSession.setHotelid(0);
            return;
        }

        LogUtils.d("savor:sp 当前wifi状态getip ，ssdp获取hotelid");
        this.isNeedRefresh = isNeedRefresh;
        mFirstRequest = false;
        // 组播获取小平台地址
        Intent intent = new Intent(mContext, SSDPService.class);
        mContext.startService(intent);
        mHandler.sendEmptyMessageDelayed(CLOSE_SSDP_SERVICE,10*1000);

        //  判断是否获取到小平台地址，如果没有获取到请求云平台（小平台是局域网）获取小平台ip
        //请求接口
        AppApi.getSmallPlatformIp(mContext,this);
    }

    @Override
    public void onDestroy() {
        mViewCallBack = null;
        mBindTvView = null;
        mSenseView = null;
        if(mChangedReceiver!=null) {
            mContext.unregisterReceiver(mChangedReceiver);
            mChangedReceiver = null;
        }

        Intent intent = new Intent(mContext,SSDPService.class);
        mContext.stopService(intent);
    }

    /**停止投屏*/
    public void stopProjection() {
        String projectId = ProjectionManager.getInstance().getProjectId();
        AppApi.notifyTvBoxStop(mContext,mSession.getTVBoxUrl(),projectId,this);
    }


    public void registerNetWorkReceiver() {
//        initWifiState();
        if(mChangedReceiver==null)
            mChangedReceiver = new NetworkConnectChangedReceiver(mHandler);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mChangedReceiver, filter);
    }

    private String getTopActivityName(Context context) {
        ActivityManager manager = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE));
            //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
        List localList = manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);
        return localRunningTaskInfo.topActivity.getClassName();
    }
}
