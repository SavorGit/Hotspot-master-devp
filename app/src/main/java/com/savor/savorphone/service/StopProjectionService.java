package com.savor.savorphone.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.projection.ProjectionManager;

/**
 *
 * 获取小平台
 * Created by hezd on 2017/1/23.
 */

public class StopProjectionService extends Service implements ApiRequestListener {

    private Session mSession;

    public StopProjectionService() {
        super();
        mSession = Session.get(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //请求接口
        LogUtils.d("savor:onstart service");
        String projectId = ProjectionManager.getInstance().getProjectId();
        String tvBoxUrl = mSession.getTVBoxUrl();
        if(!TextUtils.isEmpty(tvBoxUrl)) {
            AppApi.notifyTvBoxStop(this,mSession.getTVBoxUrl(),projectId,this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        LogUtils.d("savor:quit success");
        stopSelf();
    }


    @Override
    public void onError(AppApi.Action method, Object obj) {
        LogUtils.d("savor:quit error");
        stopSelf();
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {
        stopSelf();
    }
}
