package com.yibao.music.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import com.yibao.music.base.listener.MyAnimatorUpdateListener;

/**
 * 作者：Stran on 2017/3/28 02:24
 * 描述：${动画帮助类}
 * 邮箱：strangermy@outlook.com
 * @author Luoshipeng
 */
public class AnimationUtil {
    public static void applyBobbleAnim(View targetView) {
        AnimationSet bobbleAnimSet = new AnimationSet(true);
        ScaleAnimation expand = new ScaleAnimation(0.8f,
                                                   1.0f,
                                                   0.8f,
                                                   1.0f,
                                                   Animation.RELATIVE_TO_SELF,
                                                   0.5f,
                                                   Animation.RELATIVE_TO_SELF,
                                                   0.5f);
        expand.setDuration(300);

        bobbleAnimSet.addAnimation(expand);
        bobbleAnimSet.setFillAfter(true);
        bobbleAnimSet.setInterpolator(new OvershootInterpolator());

        targetView.startAnimation(bobbleAnimSet);
    }
    /**
     * 旋转动画
     */
    public static ObjectAnimator getRotation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        animator.setDuration(15000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(-1);
        animator.addUpdateListener(new MyAnimatorUpdateListener(animator));
        return animator;
    }
}
