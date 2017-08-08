package com.savor.savorphone.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.common.api.utils.LogUtils;

import java.io.File;

/**
 * Created by hezd on 2016/12/26.
 */

public class ClearImageCacheService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ClearImageCacheService() {
        super("ClearImageCacheService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String path = intent.getStringExtra("path");
        if(TextUtils.isEmpty(path))
            return;
        LogUtils.d("clear cache:start");
        File cacheDirectory =new File(path);
        if (cacheDirectory.exists()) {
            File[] files = cacheDirectory.listFiles();

            if (files == null || files.length == 0) {
                LogUtils.d("clear cache: file count 0");
                return;
            }
            LogUtils.d("clear cache:file count "+files.length);
            for (File f : files) {
                f.delete();
            }
        }
        LogUtils.d("clear cache:end");
        stopSelf();
    }
}
