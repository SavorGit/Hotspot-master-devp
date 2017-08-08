package com.common.api.bitmap;

/**
 * 图片缓存监听器
 * Date: 13-10-16
 * Time: 下午4:26
 */
public interface BitmapCacheListener {
    void onInitMemoryCacheFinished();

    void onInitDiskFinished();

    void onClearCacheFinished();
    void onClearMemoryCacheFinished();
    void onClearDiskCacheFinished();

    void onClearCacheFinished(String uri, BitmapDisplayConfig config);

    void onClearMemoryCacheFinished(String uri, BitmapDisplayConfig config);

    void onClearDiskCacheFinished(String uri);

    void onFlushCacheFinished();

    void onCloseCacheFinished();
}
