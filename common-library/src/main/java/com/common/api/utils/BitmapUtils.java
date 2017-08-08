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

package com.common.api.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.common.api.bitmap.BitmapCacheListener;
import com.common.api.bitmap.BitmapCommonUtils;
import com.common.api.bitmap.BitmapDisplayConfig;
import com.common.api.bitmap.BitmapGlobalConfig;
import com.common.api.bitmap.callback.BitmapLoadCallBack;
import com.common.api.bitmap.callback.BitmapLoadFrom;
import com.common.api.bitmap.callback.BitmapSetter;
import com.common.api.bitmap.callback.SimpleBitmapLoadCallBack;
import com.common.api.bitmap.core.BitmapSize;
import com.common.api.bitmap.download.Downloader;
import com.common.api.core.CompatibleAsyncTask;

import java.io.File;
import java.lang.ref.WeakReference;

public class BitmapUtils {

    private boolean pauseTask = false;
    private final Object pauseTaskLock = new Object();

    private Context context;
    private BitmapGlobalConfig globalConfig;
    private BitmapDisplayConfig defaultDisplayConfig;
    /////////////////////////////////////////////// create ///////////////////////////////////////////////////
    public BitmapUtils(Context context) {
    	
        this(context, null);
    }

    public BitmapUtils(Context context, String diskCachePath) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        if(diskCachePath==null){
        	diskCachePath=AppUtils.getPath(context, AppUtils.StorageFile.cache);
        }
        this.context = context;
        globalConfig = new BitmapGlobalConfig(context, diskCachePath);
        defaultDisplayConfig = new BitmapDisplayConfig();
    }

    public BitmapUtils(Context context, String diskCachePath, int memoryCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemoryCacheSize(memoryCacheSize);
    }

    public BitmapUtils(Context context, String diskCachePath, int memoryCacheSize, int diskCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemoryCacheSize(memoryCacheSize);
        globalConfig.setDiskCacheSize(diskCacheSize);
    }

    public BitmapUtils(Context context, String diskCachePath, float memoryCachePercent) {
        this(context, diskCachePath);
        globalConfig.setMemCacheSizePercent(memoryCachePercent);
    }

    public BitmapUtils(Context context, String diskCachePath, float memoryCachePercent, int diskCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemCacheSizePercent(memoryCachePercent);
        globalConfig.setDiskCacheSize(diskCacheSize);
    }

    //////////////////////////////////////// config ////////////////////////////////////////////////////////////////////

    public BitmapUtils configDefaultLoadingImage(Drawable drawable) {
        defaultDisplayConfig.setLoadingDrawable(drawable);
        return this;
    }

    public BitmapUtils configDefaultLoadingImage(int resId) {
        defaultDisplayConfig.setLoadingDrawable(context.getResources().getDrawable(resId));
        return this;
    }

    public BitmapUtils configDefaultLoadingImage(Bitmap bitmap) {
        defaultDisplayConfig.setLoadingDrawable(new BitmapDrawable(context.getResources(), bitmap));
        return this;
    }

    public BitmapUtils configDefaultLoadFailedImage(Drawable drawable) {
        defaultDisplayConfig.setLoadFailedDrawable(drawable);
        return this;
    }

    public BitmapUtils configDefaultLoadFailedImage(int resId) {
        defaultDisplayConfig.setLoadFailedDrawable(context.getResources().getDrawable(resId));
        return this;
    }

    public BitmapUtils configDefaultLoadFailedImage(Bitmap bitmap) {
        defaultDisplayConfig.setLoadFailedDrawable(new BitmapDrawable(context.getResources(), bitmap));
        return this;
    }

    public BitmapUtils configDefaultBitmapMaxSize(int maxWidth, int maxHeight) {
        defaultDisplayConfig.setBitmapMaxSize(new BitmapSize(maxWidth, maxHeight));
        return this;
    }

    public BitmapUtils configDefaultBitmapMaxSize(BitmapSize maxSize) {
        defaultDisplayConfig.setBitmapMaxSize(maxSize);
        return this;
    }

    public BitmapUtils configDefaultImageLoadAnimation(Animation animation) {
        defaultDisplayConfig.setAnimation(animation);
        return this;
    }

    public BitmapUtils configDefaultShowOriginal(boolean showOriginal) {
        defaultDisplayConfig.setShowOriginal(showOriginal);
        return this;
    }

    public BitmapUtils configDefaultBitmapConfig(Bitmap.Config config) {
        defaultDisplayConfig.setBitmapConfig(config);
        return this;
    }

    public BitmapUtils configDefaultDisplayConfig(BitmapDisplayConfig displayConfig) {
        defaultDisplayConfig = displayConfig;
        return this;
    }

    public BitmapUtils configDownloader(Downloader downloader) {
        globalConfig.setDownloader(downloader);
        return this;
    }

    public BitmapUtils configDefaultCacheExpiry(long defaultExpiry) {
        globalConfig.setDefaultCacheExpiry(defaultExpiry);
        return this;
    }

    public BitmapUtils configDefaultConnectTimeout(int connectTimeout) {
        globalConfig.setDefaultConnectTimeout(connectTimeout);
        return this;
    }

    public BitmapUtils configDefaultReadTimeout(int readTimeout) {
        globalConfig.setDefaultReadTimeout(readTimeout);
        return this;
    }

    public BitmapUtils configThreadPoolSize(int threadPoolSize) {
        globalConfig.setThreadPoolSize(threadPoolSize);
        return this;
    }

    public BitmapUtils configMemoryCacheEnabled(boolean enabled) {
        globalConfig.setMemoryCacheEnabled(enabled);
        return this;
    }

    public BitmapUtils configDiskCacheEnabled(boolean enabled) {
        globalConfig.setDiskCacheEnabled(enabled);
        return this;
    }

    public BitmapUtils configDiskCacheFileNameGenerator(LruDiskCache.DiskCacheFileNameGenerator diskCacheFileNameGenerator) {
        globalConfig.setDiskCacheFileNameGenerator(diskCacheFileNameGenerator);
        return this;
    }

    public BitmapUtils configBitmapCacheListener(BitmapCacheListener listener) {
        globalConfig.setBitmapCacheListener(listener);
        return this;
    }

    public BitmapUtils configGlobalConfig(BitmapGlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    ////////////////////////// display ////////////////////////////////////

    public void display(ImageView container, String url) {
        BitmapLoadCallBack<ImageView> callBack = new SimpleBitmapLoadCallBack<ImageView>();
        callBack.setBitmapSetter(BitmapCommonUtils.sDefaultImageViewSetter);
        display(container, url, defaultDisplayConfig, callBack);
    }

    public void display(ImageView container, String url, BitmapDisplayConfig displayConfig) {
        BitmapLoadCallBack<ImageView> callBack = new SimpleBitmapLoadCallBack<ImageView>();
        callBack.setBitmapSetter(BitmapCommonUtils.sDefaultImageViewSetter);
        display(container, url, displayConfig, callBack);
    }

    public <T extends View> void display(T container, String url, BitmapLoadCallBack<T> callBack) {
        display(container, url, null, callBack);
    }

    /**
     * 根据url在一个控件上显示网络图片
     * @param container
     * @param url
     * @param displayConfig
     * @param callBack
     */
    public <T extends View> void display(T container, String url, BitmapDisplayConfig displayConfig, BitmapLoadCallBack<T> callBack) {
        if (container == null) {
            return;
        }
        /**清除控件上的动画效果 */
        container.clearAnimation();
        /**若回调参数为空，默认开启一个简单的回调 */
        if (callBack == null) {
            callBack = new SimpleBitmapLoadCallBack<T>();
        }

        if (displayConfig == null || displayConfig == defaultDisplayConfig) {
            displayConfig = defaultDisplayConfig.cloneNew();
        }

        // Optimize Max Size
        BitmapSize size = displayConfig.getBitmapMaxSize();
        /**图片类配置参数，displayConfig 设置图片的宽和高 */
        displayConfig.setBitmapMaxSize(
        		BitmapCommonUtils.optimizeMaxSizeByView(container, size.getWidth(), size.getHeight())
        );

        callBack.onPreLoad(container, url, displayConfig);

        if (TextUtils.isEmpty(url)) {
            callBack.onLoadFailed(container, url, displayConfig.getLoadFailedDrawable());
            return;
        }
        /**先从缓存中查找图片是否存在 */
        Bitmap bitmap = globalConfig.getBitmapCache().getBitmapFromMemCache(url, displayConfig);

        if (bitmap != null) {
            callBack.onLoadStarted(container, url, displayConfig);
            callBack.onLoadCompleted(
                    container,
                    url,
                    bitmap,
                    displayConfig,
                    BitmapLoadFrom.MEMORY_CACHE);
        } 
        /**从正在下载的任务中查找是否存在 */
        else if (!bitmapLoadTaskExist(container, callBack.getBitmapSetter(), url)) {
        	 /**开始下载任务 */
            final BitmapLoadTask<T> loadTask = new BitmapLoadTask<T>(container, callBack, url, displayConfig);
            // set loading image
            final AsyncBitmapDrawable<T> asyncBitmapDrawable = new AsyncBitmapDrawable<T>(
                    displayConfig.getLoadingDrawable(),
                    loadTask);
            BitmapSetter<T> setter = callBack.getBitmapSetter();
            if (setter != null) {
                setter.setDrawable(container, asyncBitmapDrawable);
            } else {
                container.setBackgroundDrawable(asyncBitmapDrawable);
            }

            // load bitmap from url or diskCache
            loadTask.executeOnExecutor(globalConfig.getBitmapLoadExecutor());
        }
    }

    /////////////////////////////////////////////// cache /////////////////////////////////////////////////////////////////

    public void clearCache() {
        globalConfig.clearCache();
    }

    public void clearMemoryCache() {
        globalConfig.clearMemoryCache();
    }

    public void clearDiskCache() {
        globalConfig.clearDiskCache();
    }

    public void clearCache(String url, BitmapDisplayConfig config) {
        if (config == null) {
            config = defaultDisplayConfig;
        }
        globalConfig.clearCache(url, config);
    }

    public void clearMemoryCache(String url, BitmapDisplayConfig config) {
        if (config == null) {
            config = defaultDisplayConfig;
        }
        globalConfig.clearMemoryCache(url, config);
    }

    public void clearDiskCache(String url) {
        globalConfig.clearDiskCache(url);
    }

    public void flushCache() {
        globalConfig.flushCache();
    }

    public void closeCache() {
        globalConfig.closeCache();
    }

    public File getBitmapFileFromDiskCache(String url) {
        return globalConfig.getBitmapCache().getBitmapFileFromDiskCache(url);
    }

    public Bitmap getBitmapFromMemCache(String url, BitmapDisplayConfig displayConfig) {
        return globalConfig.getBitmapCache().getBitmapFromMemCache(url, displayConfig);
    }

    ////////////////////////////////////////// tasks //////////////////////////////////////////////////////////////////////

    public void resumeTasks() {
        pauseTask = false;
        synchronized (pauseTaskLock) {
            pauseTaskLock.notifyAll();
        }
    }

    public void pauseTasks() {
        pauseTask = true;
        flushCache();
    }

    public void stopTasks() {
        pauseTask = true;
        synchronized (pauseTaskLock) {
            pauseTaskLock.notifyAll();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    private static <T extends View> BitmapLoadTask<T> getBitmapTaskFromContainer(T container, BitmapSetter<T> bitmapSetter) {
        if (container != null) {
            final Drawable drawable = bitmapSetter == null ? container.getBackground() : bitmapSetter.getDrawable(container);
            if (drawable instanceof AsyncBitmapDrawable) {
                final AsyncBitmapDrawable<T> asyncBitmapDrawable = (AsyncBitmapDrawable<T>) drawable;
                return asyncBitmapDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private static <T extends View> boolean bitmapLoadTaskExist(T container, BitmapSetter<T> bitmapSetter, String url) {
        final BitmapLoadTask<T> oldLoadTask = getBitmapTaskFromContainer(container, bitmapSetter);

        if (oldLoadTask != null) {
            final String oldUrl = oldLoadTask.url;
            if (TextUtils.isEmpty(oldUrl) || !oldUrl.equals(url)) {
                oldLoadTask.cancel(true);
            } else {
                return true;
            }
        }
        return false;
    }

    private class AsyncBitmapDrawable<T extends View> extends Drawable {

        private final WeakReference<BitmapLoadTask<T>> bitmapLoadTaskReference;

        private final Drawable baseDrawable;

        public AsyncBitmapDrawable(Drawable drawable, BitmapLoadTask<T> bitmapWorkerTask) {
            if (drawable == null) {
                throw new IllegalArgumentException("drawable may not be null");
            }
            if (bitmapWorkerTask == null) {
                throw new IllegalArgumentException("bitmapWorkerTask may not be null");
            }
            baseDrawable = drawable;
            bitmapLoadTaskReference = new WeakReference<BitmapLoadTask<T>>(bitmapWorkerTask);
        }

        public BitmapLoadTask<T> getBitmapWorkerTask() {
            return bitmapLoadTaskReference.get();
        }

        @Override
        public void draw(Canvas canvas) {
            baseDrawable.draw(canvas);
        }

        @Override
        public void setAlpha(int i) {
            baseDrawable.setAlpha(i);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            baseDrawable.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return baseDrawable.getOpacity();
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            baseDrawable.setBounds(left, top, right, bottom);
        }

        @Override
        public void setBounds(Rect bounds) {
            baseDrawable.setBounds(bounds);
        }

        @Override
        public void setChangingConfigurations(int configs) {
            baseDrawable.setChangingConfigurations(configs);
        }

        @Override
        public int getChangingConfigurations() {
            return baseDrawable.getChangingConfigurations();
        }

        @Override
        public void setDither(boolean dither) {
            baseDrawable.setDither(dither);
        }

        @Override
        public void setFilterBitmap(boolean filter) {
            baseDrawable.setFilterBitmap(filter);
        }

        @Override
        public void invalidateSelf() {
            baseDrawable.invalidateSelf();
        }

        @Override
        public void scheduleSelf(Runnable what, long when) {
            baseDrawable.scheduleSelf(what, when);
        }

        @Override
        public void unscheduleSelf(Runnable what) {
            baseDrawable.unscheduleSelf(what);
        }

        @Override
        public void setColorFilter(int color, PorterDuff.Mode mode) {
            baseDrawable.setColorFilter(color, mode);
        }

        @Override
        public void clearColorFilter() {
            baseDrawable.clearColorFilter();
        }

        @Override
        public boolean isStateful() {
            return baseDrawable.isStateful();
        }

        @Override
        public boolean setState(int[] stateSet) {
            return baseDrawable.setState(stateSet);
        }

        @Override
        public int[] getState() {
            return baseDrawable.getState();
        }

        @Override
        public Drawable getCurrent() {
            return baseDrawable.getCurrent();
        }

        @Override
        public boolean setVisible(boolean visible, boolean restart) {
            return baseDrawable.setVisible(visible, restart);
        }

        @Override
        public Region getTransparentRegion() {
            return baseDrawable.getTransparentRegion();
        }

        @Override
        public int getIntrinsicWidth() {
            return baseDrawable.getIntrinsicWidth();
        }

        @Override
        public int getIntrinsicHeight() {
            return baseDrawable.getIntrinsicHeight();
        }

        @Override
        public int getMinimumWidth() {
            return baseDrawable.getMinimumWidth();
        }

        @Override
        public int getMinimumHeight() {
            return baseDrawable.getMinimumHeight();
        }

        @Override
        public boolean getPadding(Rect padding) {
            return baseDrawable.getPadding(padding);
        }

        @Override
        public Drawable mutate() {
            return baseDrawable.mutate();
        }

        @Override
        public ConstantState getConstantState() {
            return baseDrawable.getConstantState();
        }
    }

    private class BitmapLoadTask<T extends View> extends CompatibleAsyncTask<Object, Object, Bitmap> {
        private final String url;
        private final WeakReference<T> containerReference;
        private final BitmapLoadCallBack<T> callBack;
        private final BitmapDisplayConfig displayConfig;

        private BitmapLoadFrom from = BitmapLoadFrom.DISK_CACHE;

        public BitmapLoadTask(T container, BitmapLoadCallBack<T> callBack, String url, BitmapDisplayConfig config) {
            if (container == null || callBack == null || url == null || config == null) {
                throw new IllegalArgumentException("args may not be null");
            }

            this.containerReference = new WeakReference<T>(container);
            this.callBack = callBack;
            this.url = url;
            this.displayConfig = config;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {

            synchronized (pauseTaskLock) {
                while (pauseTask && !this.isCancelled()) {
                    try {
                        pauseTaskLock.wait();
                    } catch (Throwable e) {
                    }
                }
            }

            Bitmap bitmap = null;

            // get cache from disk cache
            if (!this.isCancelled() && this.getTargetContainer() != null) {
                this.publishProgress(url, displayConfig);
                bitmap = globalConfig.getBitmapCache().getBitmapFromDiskCache(url, displayConfig);
            }

            // download image
            if (bitmap == null && !this.isCancelled() && this.getTargetContainer() != null) {
                bitmap = globalConfig.getBitmapCache().downloadBitmap(url, displayConfig);
                from = BitmapLoadFrom.URL;
            }

            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            if (values != null && values.length == 2) {
                final T container = this.getTargetContainer();
                if (container != null) {
                    callBack.onLoadStarted(container, (String) values[0], (BitmapDisplayConfig) values[1]);
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            final T container = this.getTargetContainer();
            if (container != null) {
                if (bitmap != null) {
                    callBack.onLoadCompleted(
                            container,
                            this.url,
                            bitmap,
                            displayConfig,
                            from);
                } else {
                    callBack.onLoadFailed(
                            container,
                            this.url,
                            displayConfig.getLoadFailedDrawable());
                }
            }
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            synchronized (pauseTaskLock) {
                pauseTaskLock.notifyAll();
            }
        }

        private T getTargetContainer() {
            final T container = containerReference.get();
            final BitmapLoadTask<T> bitmapWorkerTask = getBitmapTaskFromContainer(container, callBack.getBitmapSetter());

            if (this == bitmapWorkerTask) {
                return container;
            }

            return null;
        }
    }

    public Bitmap getRotateBitmap(Bitmap toTransform,int rotateRotationAngle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(rotateRotationAngle);

        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);

    }
}
