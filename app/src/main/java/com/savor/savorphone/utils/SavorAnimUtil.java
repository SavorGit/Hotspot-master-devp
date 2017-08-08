package com.savor.savorphone.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.android.tedcoder.wkvideoplayer.util.DensityUtil;
import com.common.api.utils.AppUtils;

/**
 * Created by hezd on 2017/1/20.
 */

public class SavorAnimUtil {

    public static void vodButtonToBottom(final Context context, final View view, final OnAnimationEndListener listener){
        view.post(new Runnable() {
            @Override
            public void run() {
                view.clearAnimation();
                TranslateAnimation translateAnimation = new TranslateAnimation(0,0,0, com.common.api.utils.DensityUtil.dpToPx(context,80));
                translateAnimation.setDuration(100);
                view.setAnimation(translateAnimation);
                translateAnimation.start();
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(View.GONE);
                        if(listener!=null) {
                            listener.onEnd();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    public static void vodButtonToCenter(final Context context, final View view, final OnAnimationEndListener listener){
                view.clearAnimation();
                TranslateAnimation translateAnimation = new TranslateAnimation(0,0,0, com.common.api.utils.DensityUtil.dpToPx(context,-80));
                translateAnimation.setDuration(50);
                view.setAnimation(translateAnimation);
                translateAnimation.start();
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(View.GONE);
                        if(listener!=null) {
                            listener.onEnd();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
    }

    public static void waterLoggingAlpha(final View view, final Animator.AnimatorListener listener) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0.1f, 1f);

        anim.setDuration(1200);// 动画持续时间

        if(listener!=null) {
            anim.addListener(listener);
        }

        anim.start();
    }

    public static void hammerHit(final View view, final Animator.AnimatorListener listener) {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setPivotX(view.getWidth());
                view.setPivotY(view.getHeight());
                ObjectAnimator shakeAnim = ObjectAnimator.ofFloat(view, "rotation", 0f, -15f, 0f,-15,0f).setDuration(1200);
                if (listener != null) {
                    shakeAnim.addListener(listener);
                }
                shakeAnim.start();
            }
        });
    }
    public static void shake(final View view, final Animator.AnimatorListener listener) {
        view.clearAnimation();
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setPivotX(view.getWidth()/2);
                view.setPivotY(view.getHeight());
                ObjectAnimator shakeAnim = ObjectAnimator.ofFloat(view, "rotation", 0f,-15f,0f, 15f, 0f).setDuration(1200);
//        shakeAnim.setInterpolator(new AccelerateInterpolator());
                if(listener!=null) {
                    shakeAnim.addListener(listener);
                }
                shakeAnim.setStartDelay(500);
                shakeAnim.start();
            }
        });

    }

    public static PropertyValuesHolder getHolder(String propertyName,float ...property) {
        return  PropertyValuesHolder.ofFloat(propertyName,property);
    }

    public static void startListRefreshHintAnimation(final View view) {
        int offset = view.getHeight() - 10;
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                final ValueAnimator valueAnimatorIn = ValueAnimator.ofFloat(0,view.getHeight());
                valueAnimatorIn.setTarget(view);
                valueAnimatorIn.setDuration(500);
                final ValueAnimator valueAnimatorStay = ValueAnimator.ofFloat(view.getHeight(),view.getHeight());
                valueAnimatorStay.setTarget(view);
                valueAnimatorStay.setDuration(1500);
                final ValueAnimator valueAnimatorOut = ValueAnimator.ofFloat(view.getHeight(),0);
                valueAnimatorOut.setTarget(view);
                valueAnimatorOut.setDuration(500);

                valueAnimatorIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
//                        view.setVisibility(View.VISIBLE);
                        view.setTranslationY((Float) valueAnimatorIn.getAnimatedValue());
                    }
                });
                valueAnimatorStay.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setTranslationY((Float) valueAnimatorStay.getAnimatedValue());
                    }
                });
                valueAnimatorOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setTranslationY((Float) valueAnimatorOut.getAnimatedValue());
                    }
                });
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(valueAnimatorIn).before(valueAnimatorStay);
                animatorSet.play(valueAnimatorStay).before(valueAnimatorOut);
                animatorSet.play(valueAnimatorOut);
                animatorSet.start();
            }
        },1000);
    }

    public static void hideProjectionImage(Context context,final View view) {
        view.clearAnimation();
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,view.getWidth()+DensityUtil.dip2px(context,6),Animation.RELATIVE_TO_SELF,1.0f);
        translateAnimation.setDuration(500);
        view.setAnimation(translateAnimation);
        translateAnimation.start();
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void hideProjectionImage(Context context, final View view, final OnAnimationEndListener listener) {
        view.clearAnimation();
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,view.getWidth()+DensityUtil.dip2px(context,6),Animation.RELATIVE_TO_SELF,1.0f);
        translateAnimation.setDuration(500);
        view.setAnimation(translateAnimation);
        translateAnimation.start();
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                if(listener!=null) {
                    listener.onEnd();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void showProBtnAnim(Context context, final View view) {
        view.clearAnimation();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                final ValueAnimator valueAnimatorIn = ValueAnimator.ofFloat(view.getHeight()*2,0);
                valueAnimatorIn.setTarget(view);
                valueAnimatorIn.setDuration(300);
                valueAnimatorIn.setInterpolator(new OvershootInterpolator());
                valueAnimatorIn.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                valueAnimatorIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setTranslationY((Float) animation.getAnimatedValue());
                    }
                });
                valueAnimatorIn.start();
            }
        },0);

    }

    public static void hideProBtnAnim(Context context, final View view, final OnAnimationEndListener listener) {
        view.clearAnimation();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                final ValueAnimator valueAnimatorIn = ValueAnimator.ofFloat(0,view.getHeight()*2);
                valueAnimatorIn.setTarget(view);
                valueAnimatorIn.setDuration(300);
                valueAnimatorIn.setInterpolator(new AnticipateInterpolator());
                valueAnimatorIn.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(listener!=null) {
                            listener.onEnd();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                valueAnimatorIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setTranslationY((Float) animation.getAnimatedValue());
                    }
                });
                valueAnimatorIn.start();
            }
        },0);

    }

    public interface OnAnimationEndListener {
        void onEnd();
    }
}
