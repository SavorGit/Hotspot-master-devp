package com.savor.savorphone.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.common.api.utils.AppUtils;

import java.io.File;
import java.io.InputStream;

/**
 * Created by hezd on 2017/3/1.
 */

public class CustomCachingGlideModule implements GlideModule {
    private static final int DISK_CACHE_SIZE = 100 * 1024 * 1024;
//    private static final int MEMORY_CACHE_SIZE = 30 * 1024 * 1024;
    @Override public void applyOptions(final Context context, GlideBuilder builder) {
        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                File cacheLocation = new File(AppUtils.getPath(context, AppUtils.StorageFile.cache));
                cacheLocation.mkdirs();
                return DiskLruCacheWrapper.get(cacheLocation, DISK_CACHE_SIZE);
            }
        });
//        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
//        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
//        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
//
//        int customMemoryCacheSize = (int) (0.25 * defaultMemoryCacheSize);
//        int customBitmapPoolSize = (int) (0.25* defaultBitmapPoolSize);
//
//        builder.setMemoryCache( new LruResourceCache( customMemoryCacheSize ));
//        builder.setBitmapPool( new LruBitmapPool( customBitmapPoolSize ));
    }

    @Override public void registerComponents(Context context, Glide glide) {
        // nothing to do here
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
