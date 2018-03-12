package com.yibao.music.base.listener;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.CountDownTimer;

/**
 * Author：Sid
 * Des：${动画监听器}
 * Time:2017/7/23 20:40
 * @author Stran
 */
public class MyAnimatorUpdateListener
        implements ValueAnimator.AnimatorUpdateListener
{
    public MyAnimatorUpdateListener(ObjectAnimator animator) {
        this.animator = animator;
    }

    /**
     * 暂停状态
     */
    private boolean isPause  = false;
    /**
     * 是否已经暂停，如果一已经暂停，那么就不需要再次设置停止的一些事件和监听器了
     */
    private boolean isPaused = false;

    private boolean isPlay           = true;
    /**
     * 当前的动画的播放位置
     */
    private float   fraction         = 0.0f;
    /**
     * 当前动画的播放运行时间
     */
    private long    mCurrentPlayTime = 0L;

    /**
     * 是否是暂停状态
     *
     * @return c
     */
    private ObjectAnimator animator;

    public boolean isPause() {
        return isPause;
    }

    public boolean isPlay() {
        return isPlay;
    }

    /**
     * 停止方法，只是设置标志位，剩余的工作会根据状态位置在onAnimationUpdate进行操作
     */
    public void pause() {
        isPause = true;
        isPlay = false;
        isPaused = true;
    }

    public void play() {
        isPause = false;
        isPaused = false;
        isPlay = true;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        /*
         * 如果是暂停则将状态保持下来，并每个刷新动画的时间了；来设置当前时间，让动画
         * 在时间上处于暂停状态，同时要设置一个静止的时间加速器，来保证动画不会抖动
         */
        if (isPause) {
            if (isPaused) {
                mCurrentPlayTime = animation.getCurrentPlayTime();
                fraction = animation.getAnimatedFraction();
                animation.setInterpolator(input -> fraction);
                isPaused = true;
            }
            // 每隔动画播放的时间，我们都会将播放时间往回调整，
            // 以便重新播放的时候接着使用这个时间,同时也为了让整个动画不结束
            new CountDownTimer(ValueAnimator.getFrameDelay(), ValueAnimator.getFrameDelay()) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    animator.setCurrentPlayTime(mCurrentPlayTime);
                }
            }.start();
        } else {
            // 将时间拦截器恢复成线性的，如果您有自己的，也可以在这里进行恢复
            animation.setInterpolator(null);
        }
    }


}
