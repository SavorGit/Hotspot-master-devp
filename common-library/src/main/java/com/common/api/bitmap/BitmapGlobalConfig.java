/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.common.api.bitmap;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.common.api.bitmap.core.BitmapCache;
import com.common.api.bitmap.download.Downloader;
import com.common.api.bitmap.download.SimpleDownloader;
import com.common.api.core.CompatibleAsyncTask;
import com.common.api.utils.LogUtils;
import com.common.api.utils.LruDiskCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: wyouflf
 * Date: 13-7-31
 * Time: 下午11:15
 */
public class BitmapGlobalConfig {

    private String diskCachePath;
    public final static int MIN_MEMORY_CACHE_SIZE = 1024 * 1024 * 2; // 2M
    private int memoryCacheSize = 1024 * 1024 * 4; // 4MB
    public final static int MIN_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10M
    private int diskCacheSize = 1024 * 1024 * 50;  // 50M

    private boolean memoryCacheEnabled = true;
    private boolean diskCacheEnabled = true;

    private Downloader downloader;
    private BitmapCache bitmapCache;

    private int threadPoolSize = 5;
    private boolean _dirty_params_bitmapLoadExecutor = true;
    private ExecutorService bitmapLoadExecutor;

    private long defaultCacheExpiry = 1000L * 60 * 60 * 24 * 30; // 30 days
    private int defaultConnectTimeout = 1000 * 15; // 15 sec
    private int defaultReadTimeout = 1000 * 15; // 15 sec

    private LruDiskCache.DiskCacheFileNameGenerator diskCacheFileNameGenerator;

    private BitmapCacheListener bitmapCacheListener;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "BitmapUtils #" + mCount.getAndIncrement());
            thread.setPriority(Thread.NORM_PRIORITY - 1);
            return thread;
        }
    };

    private Context mContext;

    /**
     * @param context
     * @param diskCachePath If null, use default appCacheDir+"/xBitmapCache"
     */
    public BitmapGlobalConfig(Context context, String diskCachePath) {
        if (context == null) throw new IllegalArgumentException("context may not be null");
        this.mContext = context;
        this.diskCachePath = diskCachePath;
        initBitmapCache();
    }

    private void initBitmapCache() {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_INIT_MEMORY_CACHE);
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_INIT_DISK_CACHE);
    }

    public String getDiskCachePath() {
        if (TextUtils.isEmpty(diskCachePath)) {
            diskCachePath = BitmapCommonUtils.getDiskCacheDir(mContext, "xBitmapCache");
        }
        return diskCachePath;
    }

    public Downloader getDownloader() {
        if (downloader == null) {
            downloader = new SimpleDownloader();
        }
        downloader.setDefaultExpiry(getDefaultCacheExpiry());
        downloader.setDefaultConnectTimeout(getDefaultConnectTimeout());
        downloader.setDefaultReadTimeout(getDefaultReadTimeout());
        return downloader;
    }

    public void setDownloader(Downloader downloader) {
        this.downloader = downloader;
    }

    public long getDefaultCacheExpiry() {
        return defaultCacheExpiry;
    }

    public void setDefaultCacheExpiry(long defaultCacheExpiry) {
        this.defaultCacheExpiry = defaultCacheExpiry;
    }

    public int getDefaultConnectTimeout() {
        return defaultConnectTimeout;
    }

    public void setDefaultConnectTimeout(int defaultConnectTimeout) {
        this.defaultConnectTimeout = defaultConnectTimeout;
    }

    public int getDefaultReadTimeout() {
        return defaultReadTimeout;
    }

    public void setDefaultReadTimeout(int defaultReadTimeout) {
        this.defaultReadTimeout = defaultReadTimeout;
    }

    public BitmapCache getBitmapCache() {
        if (bitmapCache == null) {
            bitmapCache = new BitmapCache(this);
        }
        return bitmapCache;
    }

    public int getMemoryCacheSize() {
        return memoryCacheSize;
    }

    public void setMemoryCacheSize(int memoryCacheSize) {
        if (memoryCacheSize >= MIN_MEMORY_CACHE_SIZE) {
            this.memoryCacheSize = memoryCacheSize;
            if (bitmapCache != null) {
                bitmapCache.setMemoryCacheSize(this.memoryCacheSize);
            }
        } else {
            this.setMemCacheSizePercent(0.3f);// Set default memory cache size.
        }
    }

    /**
     * @param percent between 0.05 and 0.8 (inclusive)
     */
    public void setMemCacheSizePercent(float percent) {
        if (percent < 0.05f || percent > 0.8f) {
            throw new IllegalArgumentException("percent must be between 0.05 and 0.8 (inclusive)");
        }
        this.memoryCacheSize = Math.round(percent * getMemoryClass() * 1024 * 1024);
        if (bitmapCache != null) {
            bitmapCache.setMemoryCacheSize(this.memoryCacheSize);
        }
    }

    public int getDiskCacheSize() {
        return diskCacheSize;
    }

    public void setDiskCacheSize(int diskCacheSize) {
        if (diskCacheSize >= MIN_DISK_CACHE_SIZE) {
            this.diskCacheSize = diskCacheSize;
            if (bitmapCache != null) {
                bitmapCache.setDiskCacheSize(this.diskCacheSize);
            }
        }
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        if (threadPoolSize > 0 && threadPoolSize != this.threadPoolSize) {
            _dirty_params_bitmapLoadExecutor = true;
            this.threadPoolSize = threadPoolSize;
        }
    }

    public ExecutorService getBitmapLoadExecutor() {
        if (_dirty_params_bitmapLoadExecutor || bitmapLoadExecutor == null) {
            bitmapLoadExecutor = Executors.newFixedThreadPool(getThreadPoolSize(), sThreadFactory);
            _dirty_params_bitmapLoadExecutor = false;
        }
        return bitmapLoadExecutor;
    }

    public boolean isMemoryCacheEnabled() {
        return memoryCacheEnabled;
    }

    public void setMemoryCacheEnabled(boolean memoryCacheEnabled) {
        this.memoryCacheEnabled = memoryCacheEnabled;
    }

    public boolean isDiskCacheEnabled() {
        return diskCacheEnabled;
    }

    public void setDiskCacheEnabled(boolean diskCacheEnabled) {
        this.diskCacheEnabled = diskCacheEnabled;
    }

    public LruDiskCache.DiskCacheFileNameGenerator getDiskCacheFileNameGenerator() {
        return diskCacheFileNameGenerator;
    }

    public void setDiskCacheFileNameGenerator(LruDiskCache.DiskCacheFileNameGenerator diskCacheFileNameGenerator) {
        this.diskCacheFileNameGenerator = diskCacheFileNameGenerator;
        if (bitmapCache != null) {
            bitmapCache.setDiskCacheFileNameGenerator(diskCacheFileNameGenerator);
        }
    }

    public BitmapCacheListener getBitmapCacheListener() {
        return bitmapCacheListener;
    }

    public void setBitmapCacheListener(BitmapCacheListener bitmapCacheListener) {
        this.bitmapCacheListener = bitmapCacheListener;
    }

    private int getMemoryClass() {
        return ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    ////////////////////////////////// bitmap cache management task ///////////////////////////////////////
    private class BitmapCacheManagementTask extends CompatibleAsyncTask<Object, Void, Object[]> {
        public static final int MESSAGE_INIT_MEMORY_CACHE = 0;
        public static final int MESSAGE_INIT_DISK_CACHE = 1;
        public static final int MESSAGE_FLUSH = 2;
        public static final int MESSAGE_CLOSE = 3;
        public static final int MESSAGE_CLEAR = 4;
        public static final int MESSAGE_CLEAR_MEMORY = 5;
        public static final int MESSAGE_CLEAR_DISK = 6;
        public static final int MESSAGE_CLEAR_BY_KEY = 7;
        public static final int MESSAGE_CLEAR_MEMORY_BY_KEY = 8;
        public static final int MESSAGE_CLEAR_DISK_BY_KEY = 9;

        @Override
        protected Object[] doInBackground(Object... params) {
            if (params == null || params.length < 1) return params;
            BitmapCache cache = getBitmapCache();
            if (cache == null) return params;
            try {
                switch ((Integer) params[0]) {
                    case MESSAGE_INIT_MEMORY_CACHE:
                    	LogUtils.d("*************************MESSAGE_INIT_MEMORY_CACHE ......");
                        cache.initMemoryCache();
                        break;
                    case MESSAGE_INIT_DISK_CACHE:
                        cache.initDiskCache();
                    	LogUtils.d("*************************MESSAGE_INIT_DISK_CACHE ......");
                        break;
                    case MESSAGE_FLUSH:
                    	LogUtils.d("*************************MESSAGE_FLUSH ......");
                        cache.flush();
                        break;
                    case MESSAGE_CLOSE:
                    	LogUtils.d("*************************MESSAGE_CLOSE ......cache.clearMemoryCache() cache.close()");
                        cache.clearMemoryCache();
                        cache.close();
                        break;
                    case MESSAGE_CLEAR:
                    	LogUtils.d("*************************MESSAGE_CLEAR ......cache.clearCache();");
                        cache.clearCache();
                        break;
                    case MESSAGE_CLEAR_MEMORY:
                    	LogUtils.d("*************************MESSAGE_CLEAR_MEMORY ......cache.clearMemoryCache();");
                        cache.clearMemoryCache();
                        break;
                    case MESSAGE_CLEAR_DISK:
                    	LogUtils.d("*************************MESSAGE_CLEAR_DISK ......cache.clearDiskCache();");
                        cache.clearDiskCache();
                        break;
                    case MESSAGE_CLEAR_BY_KEY:
                        if (params.length != 3) return params;
                        LogUtils.d("*************************MESSAGE_CLEAR_BY_KEY ......cache.clearCache--->"+String.valueOf(params[1]));
                        cache.clearCache(String.valueOf(params[1]), (BitmapDisplayConfig) params[2]);
                        break;
                    case MESSAGE_CLEAR_MEMORY_BY_KEY:
                        if (params.length != 3) return params;
                        LogUtils.d("*************************MESSAGE_CLEAR_BY_KEY ......cache.clearMemoryCache--->"+String.valueOf(params[1]));
                        cache.clearMemoryCache(String.valueOf(params[1]), (BitmapDisplayConfig) params[2]);
                        break;
                    case MESSAGE_CLEAR_DISK_BY_KEY:
                        if (params.length != 2) return params;
                        LogUtils.d("*************************MESSAGE_CLEAR_DISK_BY_KEY ......cache.clearDiskCache--->"+String.valueOf(params[1]));

                        cache.clearDiskCache(String.valueOf(params[1]));
                        break;
                    default:
                        break;
                }
            } catch (Throwable e) {
                LogUtils.e(e.getMessage(), e);
            }
            return params;
        }

        @Override
        protected void onPostExecute(Object[] params) {
            if (bitmapCacheListener == null || params == null || params.length < 1) return;
            try {
                switch ((Integer) params[0]) {
                    case MESSAGE_INIT_MEMORY_CACHE:
                        bitmapCacheListener.onInitMemoryCacheFinished();
                        break;
                    case MESSAGE_INIT_DISK_CACHE:
                        bitmapCacheListener.onInitDiskFinished();
                        break;
                    case MESSAGE_FLUSH:
                        bitmapCacheListener.onFlushCacheFinished();
                        break;
                    case MESSAGE_CLOSE:
                        bitmapCacheListener.onCloseCacheFinished();
                        break;
                    case MESSAGE_CLEAR:
                        bitmapCacheListener.onClearCacheFinished();
                        break;
                    case MESSAGE_CLEAR_MEMORY:
                        bitmapCacheListener.onClearMemoryCacheFinished();
                        break;
                    case MESSAGE_CLEAR_DISK:
                        bitmapCacheListener.onClearDiskCacheFinished();
                        break;
                    case MESSAGE_CLEAR_BY_KEY:
                        if (params.length != 3) return;
                        bitmapCacheListener.onClearCacheFinished(
                                String.valueOf(params[1]),
                                (BitmapDisplayConfig) params[2]);
                        break;
                    case MESSAGE_CLEAR_MEMORY_BY_KEY:
                        if (params.length != 3) return;
                        bitmapCacheListener.onClearMemoryCacheFinished(
                                String.valueOf(params[1]),
                                (BitmapDisplayConfig) params[2]);
                        break;
                    case MESSAGE_CLEAR_DISK_BY_KEY:
                        if (params.length != 2) return;
                        bitmapCacheListener.onClearDiskCacheFinished(String.valueOf(params[1]));
                        break;
                    default:
                        break;
                }
            } catch (Throwable e) {
                LogUtils.e(e.getMessage(), e);
            }
        }
    }

    public void clearCache() {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_CLEAR);
    }

    public void clearMemoryCache() {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_CLEAR_MEMORY);
    }

    public void clearDiskCache() {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_CLEAR_DISK);
    }

    public void clearCache(String uri, BitmapDisplayConfig config) {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_CLEAR_BY_KEY, uri, config);
    }

    public void clearMemoryCache(String uri, BitmapDisplayConfig config) {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_CLEAR_MEMORY_BY_KEY, uri, config);
    }

    public void clearDiskCache(String uri) {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_CLEAR_DISK_BY_KEY, uri);
    }

    public void flushCache() {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_FLUSH);
    }

    public void closeCache() {
        new BitmapCacheManagementTask().execute(BitmapCacheManagementTask.MESSAGE_CLOSE);
    }
}