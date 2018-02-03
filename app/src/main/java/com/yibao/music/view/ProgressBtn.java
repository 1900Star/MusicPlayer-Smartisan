package com.yibao.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/12 21:32
 */
public class ProgressBtn
        extends Button
{
    private boolean isProgressEnable = true;
    private int     max              = 0;
    private int      progress;
    private Drawable mDrawable;
    private int color = Color.GRAY;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * 是否允许有进度
     */
    public void setProgressEnable(boolean progressEnable) {
        isProgressEnable = progressEnable;
    }

    /**
     * 设置进度的最大值
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * 设置进度的当前值
     */
    public void setProgress(int progress) {
        this.progress = progress;
        //重绘进度
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public ProgressBtn(Context context) {
        super(context);
    }

    public ProgressBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //之前
        //        canvas.drawText("haha",20,20,getPaint());
        if (isProgressEnable) {
            if (mDrawable == null) {
                mDrawable = new ColorDrawable(color);
            }
            int left   = 0;
            int top    = 0;
            int right  = (int) (progress * 1.0f / max * getMeasuredWidth() + .5f);
            int bottom = getBottom();
            mDrawable.setBounds(left, top, right, bottom);

            mDrawable.draw(canvas);

        }
        super.onDraw(canvas);
    }
}
