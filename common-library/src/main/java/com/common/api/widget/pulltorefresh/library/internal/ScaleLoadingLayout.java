/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.common.api.widget.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.*;
import android.support.v4.view.ViewCompat;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.common.api.R;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.Mode;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.Orientation;

/**
 * 缩放动画布局
 * @author lenovo
 *
 */
public class ScaleLoadingLayout extends LoadingLayout {

	static final int ROTATION_ANIMATION_DURATION = 1200;
	/**缩放动画*/
	private final Animation mScalAnimation;
//	private final Matrix mHeaderImageMatrix;
	/**X,Y轴*/
	private float mRotationPivotX, mRotationPivotY;
	public static final float BASE_SCALE = 0.0f;
	private final boolean mRotateDrawableWhilePulling;

	public ScaleLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		/**创建缩放动画动画*/
		mRotateDrawableWhilePulling = attrs.getBoolean(R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);

//		mContentLayout.setSca(ScaleType.MATRIX);
//		mHeaderImageMatrix = new Matrix();
//		mHeaderImage.setImageMatrix(mHeaderImageMatrix);

		mScalAnimation = new ScaleAnimation(BASE_SCALE, 1, Animation.RELATIVE_TO_SELF, BASE_SCALE, Animation.RELATIVE_TO_SELF,
				1);
		mScalAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mScalAnimation.setDuration(ROTATION_ANIMATION_DURATION);
		mScalAnimation.setRepeatCount(Animation.INFINITE);
		mScalAnimation.setRepeatMode(Animation.RESTART);
	}
	
	public void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable) {
			mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
			mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
		}
	}

	protected void onPullImpl(float scaleOfLayout) {
		if(scaleOfLayout>1)
			scaleOfLayout = 1;
		if(scaleOfLayout<BASE_SCALE)
			scaleOfLayout = BASE_SCALE;
		ViewCompat.setPivotY(mContentLayout,mContentLayout.getHeight());
		android.support.v4.view.ViewCompat.setScaleX(mContentLayout,scaleOfLayout);
		android.support.v4.view.ViewCompat.setScaleY(mContentLayout,scaleOfLayout);
	}

	@Override
	protected void refreshingImpl() {
		mContentLayout.startAnimation(mScalAnimation);
	}

	@Override
	protected void resetImpl() {
		mContentLayout.clearAnimation();
		resetImageRotation();
	}

	private void resetImageRotation() {
		android.support.v4.view.ViewCompat.setScaleX(mContentLayout,0.0f);
		android.support.v4.view.ViewCompat.setScaleY(mContentLayout,0.0f);
	}

	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.drawable.default_ptr_rotate;
	}

}
