package com.savor.savorphone.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.common.api.utils.ShowProgressDialog;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.RotateProResponse;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.interfaces.IBaseView;
import com.savor.savorphone.interfaces.IHotspotSenseView;
import com.savor.savorphone.presenter.BindTvPresenter;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.WifiUtil;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by hezd on 2016/12/13.
 */
public abstract class BaseActivity extends Activity implements ApiRequestListener,IBaseView, IBindTvView, IHotspotSenseView {

    private static final int CHECK_WIFI_LINKED = 0x1;
    private static final int CANCEL_CHECK_WIFI = 0x2;
    protected Session mSession;
    protected Activity mContext;
    private FrameLayout mParentLayout;
    private ContentLoadingProgressBar mLoadingPb;
    private ProgressDialog mProgressDialog;
    private CommonDialog mHintDialog;
    /**
     * 投屏后台service
     */
    protected ProjectionService mProjectionService;
    /**
     * 绑定service需要的serviceconnect，绑定成功后可获取到service实例
     */
    protected ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mProjectionService = ((ProjectionService.ProjectionBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    protected BindTvPresenter mBindTvPresenter;
    protected LinkDialog mQrcodeDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                case CANCEL_CHECK_WIFI:
                    mSession.setTvBoxUrl(null);
                    mHandler.removeMessages(CHECK_WIFI_LINKED);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 统计应用启动数据,如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。
        mSession = Session.get(getApplicationContext());
        mContext = this;
        ActivitiesManager.getInstance().pushActivity(this);
        setContentView(R.layout.base_layout);
    }

    @Override
    public void showLoadingLayout() {
        if(mProgressDialog == null)
            mProgressDialog = ShowProgressDialog.showProgressDialog(getApplicationContext(), AlertDialog.THEME_HOLO_LIGHT, getString(R.string.loading_hint));
    }

    @Override
    public void hideLoadingLayout() {
        if(mProgressDialog!=null&&mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showToast(String message) {
        if(this.isFinishing()) {
            return;
        }
        if(mHintDialog == null) {
            mHintDialog = new CommonDialog(this,message);
        }
        mHintDialog.setContent(message);
        mHintDialog.show();
    }

    protected void showErrorToast(Object obj, String defaultMsg) {
        if (obj instanceof ResponseErrorMessage) {
            ResponseErrorMessage errorMessage = (ResponseErrorMessage) obj;
            if (!TextUtils.isEmpty(errorMessage.getMessage())) {
                defaultMsg = errorMessage.getMessage();
            }
        }
        ShowMessage.showToastSavor(getApplicationContext(), defaultMsg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getName());
    }

    protected BaseProReqeust getBasePrepareInfo(MediaInfo media){
        BaseProReqeust prepareInfo = new BaseProReqeust();
        prepareInfo.setRotatevalue(media.getRotatevalue());
        prepareInfo.setAction(media.getAction());
        prepareInfo.setAssetname(media.getAssetname());
        prepareInfo.setImageId(media.getImageId());
        String imageType = ProjectionManager.getInstance().getImageType();
        if("3".equals(imageType)) {
            prepareInfo.setImageType("3");
        }
        return prepareInfo;
    }

    protected void initProjectionService() {
        // 开始查询进度
        Intent intent = new Intent(this, ProjectionService.class);
        intent.putExtra(ProjectionService.EXTRA_TYPE,ProjectionService.TYPE_VOD_NOMARL);
        bindService(intent,mServiceConn, Context.BIND_AUTO_CREATE);
    }

    protected void initPresenter() {
        mBindTvPresenter = new BindTvPresenter(this,this,this,this);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {

    }

    @Override
    public void onError(AppApi.Action method, Object obj) {

        if(obj instanceof ResponseErrorMessage) {
            ResponseErrorMessage message = (ResponseErrorMessage) obj;
            int code = message.getCode();
            String msg = message.getMessage();
            showToast(msg);
        }
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitiesManager.getInstance().popActivity(this);
        if(mBindTvPresenter!=null) {
            mBindTvPresenter.onDestroy();
        }
    }

    @Override
    public void readyForCode() {

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
        String ssid = mSession.getSsid();
        ShowMessage.showToast(this,ssid+"连接成功，可以投屏");
    }

    @Override
    public void startLinkTv() {
        closeQrcodeDialog();
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

    /**
     * 检查是否在同一wifi，如果三分钟内连接到同一wifi提示连接成功
     */
    protected void checkWifiLinked(TvBoxInfo tvBoxInfo) {
        String ssid = tvBoxInfo.getSsid();
        String localSSid = WifiUtil.getWifiName(this);
        if(!TextUtils.isEmpty(ssid)) {
            LogUtils.d(ConstantValues.LOG_CHECKWIFI_PREFIX+" 当前wifi"+localSSid+"，需要连接wifi"+ssid);
            if(ssid.endsWith(localSSid)) {
                LogUtils.d(ConstantValues.LOG_CHECKWIFI_PREFIX+" 连接到指定wifi，绑定成功取消检测");
                mSession.setWifiSsid(ssid);
                mSession.setTvBoxUrl(tvBoxInfo);
                initBindcodeResult();
                mHandler.removeMessages(CANCEL_CHECK_WIFI);
                mHandler.sendEmptyMessage(CANCEL_CHECK_WIFI);
            }else {
                LogUtils.d(ConstantValues.LOG_CHECKWIFI_PREFIX+" 当前未连接wifi，继续检测...");
                startCheckWifiLinkedTimer();
            }
        }else {
            // 每隔一秒检测是否已连接同一wifi
            startCheckWifiLinkedTimer();
        }
    }

    /**开启检查是否是同一wifi定时器每隔一秒检查一次*/
    private void startCheckWifiLinkedTimer() {
        mHandler.removeMessages(CHECK_WIFI_LINKED);
        mHandler.sendEmptyMessageDelayed(CHECK_WIFI_LINKED,1000);
    }

    @Override
    public void showChangeWifiDialog() {

    }
}
