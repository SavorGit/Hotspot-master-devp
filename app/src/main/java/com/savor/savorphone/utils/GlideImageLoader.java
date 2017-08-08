package com.savor.savorphone.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.savor.savorphone.interfaces.ImageLoader;


/**
 * Glide图片加载工具类
 * 1、调用者须确保在主线程调用load***相关方法
 * 2、应用于CircleImageView等圆形ImageView时须使用禁用动画效果
 *
 * 详细说明文档参见：https://github.com/bumptech/glide
 * Created by zhanghq on 2016/6/25.
 */
public class GlideImageLoader implements ImageLoader<RequestListener> {
    
    private static int globalPlaceholderResId;
    private static int globalFailedResId;
    private static volatile GlideImageLoader instance=null;

    private GlideImageLoader() {

    }

    public static GlideImageLoader getInstance() {
        if(instance==null) {
            synchronized (GlideImageLoader.class) {
                if(instance==null)
                    instance =  new GlideImageLoader();
            }
        }

        return instance;
    }

    public  void setGlobalPlaceholderResId(int globalPlaceholderResId) {
        GlideImageLoader.globalPlaceholderResId = globalPlaceholderResId;
    }

    public  void setGlobalFailedResId(int globalFailedResId) {
        GlideImageLoader.globalFailedResId = globalFailedResId;
    }

    public  void loadImage(Context context, String imgPath, ImageView imageView) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        loadImage(appContext, imgPath, imageView, globalPlaceholderResId, globalFailedResId);
    }


    public  void loadImage(Context context, String imgPath, ImageView imageView, int placeHolderId, RequestListener listener) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        loadImage(appContext, imgPath, imageView, placeHolderId, placeHolderId,listener);
    }

    public  void loadRoundImage(Context context, String imgPath, ImageView imageView, int defaultId) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        loadRoundImage(appContext, imgPath, imageView, defaultId, defaultId);
    }

    public  void loadImage(Context context, String imgPath, ImageView imageView, int placeholderResId, int failedResId) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        Glide.with(appContext)
                .load(imgPath)
                .placeholder(placeholderResId)
                .error(failedResId)
                .crossFade()
                .into(imageView);
    }

    public  void loadImage(Context context, String imgPath, ImageView imageView, int placeholderResId, int failedResId, RequestListener listener) {
        if(!(listener instanceof RequestListener))
            throw new RuntimeException("this listener is not RequestListener type!");

        Glide.with(context)
                .load(imgPath).listener(listener)
                .placeholder(placeholderResId)
                .error(failedResId)
                .crossFade()
                .into(imageView);
    }

    public  void loadRoundImage(final Context context, String imgPath, final ImageView imageView, int placeholderResId, int failedResId) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        Glide.with(appContext).
                load(imgPath)
                .asBitmap()
                .placeholder(placeholderResId)
                .error(failedResId)
                .centerCrop()
                .into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static void loadImageWithNoAnimate(Context context, String imgPath, ImageView imageView, int placeholderResId, int failedResId) {
        Glide.with(context)
                .load(imgPath)
                .placeholder(placeholderResId)
                .error(failedResId)
                .dontAnimate()
                .crossFade()
                .into(imageView);
    }

    public  void loadImage(Fragment fragment, String imgPath, ImageView imageView) {
        loadImage(fragment, imgPath, imageView, globalPlaceholderResId, globalFailedResId);
    }

    public  void loadImage(Fragment fragment, String imgPath, ImageView imageView, int placeholderResId, int failedResId) {
        Glide.with(fragment)
                .load(imgPath)
                .placeholder(placeholderResId)
                .error(failedResId)
                .crossFade()
                .into(imageView);
    }

    public  void loadImageWithNoAnimate(Fragment fragment, String imgPath, ImageView imageView, int placeholderResId, int failedResId) {
        Glide.with(fragment)
                .load(imgPath)
                .placeholder(placeholderResId)
                .error(failedResId)
                .dontAnimate()
                .crossFade()
                .into(imageView);
    }

    public void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }
}
