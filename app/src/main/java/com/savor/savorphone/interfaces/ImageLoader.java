package com.savor.savorphone.interfaces;

import android.content.Context;
import android.widget.ImageView;

/**
 * 图片加载要实现的接口
 * Created by hezd on 2016/6/26.
 */
public interface ImageLoader<T> {
    void loadImage(Context context, String imagePath, ImageView showView);
    void loadImage(Context context, String imagePath, ImageView showView, int placeholderResId, int failedResId);
    void loadImage(Context context, String imagePath, ImageView showView, int placeholderResId, T listener);
    void loadRoundImage(Context context, String imagePath, ImageView showView, int placeholderResId);
    void loadRoundImage(Context context, String imgPath, ImageView imageView, int placeholderResId, int failedResId);
}
