package com.savor.savorphone.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;

/**
 * Created by hezd on 2017/5/19.
 */

public class UploadFirstUseService extends Service implements ApiRequestListener {

    private Session session;

    @Override
    public void onCreate() {
        super.onCreate();
        session = Session.get(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean ismUploadFirstUse = session.ismUploadFirstUse();
        int hotelid = session.getHotelid();
        if(!ismUploadFirstUse) {
            LogUtils.d("savor:firstuse 未上传过首次使用并且酒店id不为0 开始上传 设置标识为已上传");
            session.setIsUploadFirstuse(true);
            AppApi.staticsFirstUseInHotel(this,String.valueOf(hotelid),this);
        }else {
            LogUtils.d("savor:firstuse 已上传过 不上传");
            stopSelf();

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
        switch (method) {
            case POST_STATICS_FIRSTUSE_JSON:
                LogUtils.d("savor:firstuse 上传成功");
                stopSelf();
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_STATICS_FIRSTUSE_JSON:
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    if(code == 20001) {
                        LogUtils.d("savor:firstuse 已上传过");
                    }else {
                        LogUtils.d("savor:firstuse 上传失败 设置标识为未上传");
                        session.setIsUploadFirstuse(false);
                    }
                }
                stopSelf();
                break;
        }
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {
        stopSelf();
    }
}
