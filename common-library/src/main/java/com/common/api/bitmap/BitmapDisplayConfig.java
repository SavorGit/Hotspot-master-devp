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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;

import com.common.api.bitmap.core.BitmapSize;

/**
 * @author 朵朵花开
 *BitmapDisplay 配置参数
 */
public class BitmapDisplayConfig {

    /**位图最大值 */
    private BitmapSize bitmapMaxSize;
    /**动画 */
    private Animation animation;
    /**读取中-动画 */
    private Drawable loadingDrawable;
    /**读取失败-动画 */
    private Drawable loadFailedDrawable;
    private boolean showOriginal = false;
    /**配置参数 */
    private Bitmap.Config bitmapConfig = Bitmap.Config.RGB_565;

    private static final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);

    public BitmapDisplayConfig() {
    }

    public BitmapSize getBitmapMaxSize() {
        if (bitmapMaxSize == null) {
            return BitmapSize.ZERO;
        }
        return bitmapMaxSize;
    }

    public void setBitmapMaxSize(BitmapSize bitmapMaxSize) {
        this.bitmapMaxSize = bitmapMaxSize;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Drawable getLoadingDrawable() {
        return loadingDrawable == null ? TRANSPARENT_DRAWABLE : loadingDrawable;
    }

    public void setLoadingDrawable(Drawable loadingDrawable) {
        this.loadingDrawable = loadingDrawable;
    }

    public Drawable getLoadFailedDrawable() {
        return loadFailedDrawable == null ? TRANSPARENT_DRAWABLE : loadFailedDrawable;
    }

    public void setLoadFailedDrawable(Drawable loadFailedDrawable) {
        this.loadFailedDrawable = loadFailedDrawable;
    }

    public boolean isShowOriginal() {
        return showOriginal;
    }

    public void setShowOriginal(boolean showOriginal) {
        this.showOriginal = showOriginal;
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    public void setBitmapConfig(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
    }

    @Override
    public String toString() {
        return isShowOriginal() ? "" : bitmapMaxSize.toString();
    }

    public BitmapDisplayConfig cloneNew() {
        BitmapDisplayConfig config = new BitmapDisplayConfig();
        config.bitmapMaxSize = this.bitmapMaxSize;
        config.animation = this.animation;
        config.loadingDrawable = this.loadingDrawable;
        config.loadFailedDrawable = this.loadFailedDrawable;
        config.showOriginal = this.showOriginal;
        config.bitmapConfig = this.bitmapConfig;
        return config;
    }
}
