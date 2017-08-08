package com.savor.savorphone.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.interfaces.IBaseView;
import com.savor.savorphone.interfaces.IHotspotSenseView;

/**
 * 首页场景主导器
 * Created by hezd on 2017/1/19.
 */

public class SensePresenter extends BasePresenter implements ApiRequestListener {
    public static final String SMALL_PLATFORM = "small_platform";
    private static final int STATIC_FIRST_USE = 0x1;
    private IHotspotSenseView mHotSpotView;
    private Session mSession;
    private SmallPlatformReciver smallPlatformReciver;

    public SensePresenter(Context context, IBaseView baseView, IHotspotSenseView hotspotMainView) {
        super(context, baseView);
        this.mHotSpotView = hotspotMainView;
    }

    @Override
    public void onCreate() {
        mSession = Session.get(mContext);
    }

    /**检查是否是场景模式*/
    public void checkSense() {
        boolean smallPlatformEnable = isHotelEnvironment();
        if(smallPlatformEnable) {
            mHotSpotView.showProjection(mSession.isBindTv());
        }else {
            mHotSpotView.hideProjection();
        }
    }

    /**是否是酒店环境*/
    public boolean isHotelEnvironment() {
        boolean smallPlatformEnable = mSession.getHotelid()>0;
        return smallPlatformEnable;
    }

    /**
     * 是否有可连接设备
     * @return
     */
    public boolean isHasSmallPlatform() {
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        if((smallPlatInfoBySSDP ==null|| TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp()))
                &&(tvBoxSSDPInfo==null||TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp()))
                &&(smallPlatformByGetIp == null || TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp()))) {
            return false;
        }
        return true;
    }

    /**
     * 注册小平台发现广播
     */
    public void regitsterSmallPlatformReciever() {
        smallPlatformReciver = new SmallPlatformReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMALL_PLATFORM);
        mContext.registerReceiver(smallPlatformReciver,filter);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {

    }

    @Override
    public void onError(AppApi.Action method, Object obj) {

    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {

    }

    public class SmallPlatformReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SensePresenter.SMALL_PLATFORM)) {
                LogUtils.d("savor:收到小平台接受广播");
                checkSense();
            }
        }
    }

    @Override
    public void onDestroy() {
        mViewCallBack = null;
        mHotSpotView = null;
        if(smallPlatformReciver!=null)
        mContext.unregisterReceiver(smallPlatformReciver);
    }
}
