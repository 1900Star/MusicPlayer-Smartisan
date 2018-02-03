package com.yibao.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.biggirl.R;


/**
 * 作者：Stran on 2017/3/27 16:55
 * 描述：${自定义ProgressBar}
 * 邮箱：strangermy@outlook.com
 * @author Stran
 */
public class ProgressView
        extends LinearLayout
{
    private FloatingActionButton mIvIcon;
    private TextView             mTvNote;

    private boolean isProgressEnable = true;
    private int     mMax             = 100;
    private int   mProgress;
    private RectF mOval;
    private Paint mPaint;

    /**
     * 设置是否允许进度
     *
     * @param progressEnable
     */
    public void setProgressEnable(boolean progressEnable) {
        isProgressEnable = progressEnable;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public void setMax(int max) {
        mMax = max;
    }

    /**
     * 设置进度的当前值
     *
     * @param progress
     */
    public void setProgress(int progress) {
        mProgress = progress;
        //重绘
        //        invalidate()        这个方法会报子线程修改UI异常，
        postInvalidate();
    }

    /**
     * 修改图标的内容
     */
    public void setIcon(int resId) {
        mIvIcon.setImageResource(resId);
    }

    /**
     * 修改文本的内容
     */
    public void setNote(String content) {
        mTvNote.setText(content);
    }

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //挂载布局

        View view = View.inflate(context, R.layout.progress_btn, this);
        mIvIcon = view.findViewById(R.id.fab_progress);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);//绘制背景(透明)
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);//绘制图标和文字
        int bottom = mIvIcon.getBottom();
        int top    = mIvIcon.getTop();
        int left   = mIvIcon.getLeft();
        int right  = mIvIcon.getRight();
        if (isProgressEnable) {
            if (mOval == null) {

                mOval = new RectF(left, top, right, bottom);

            }
            float startAngle = -90;
            float sweepAngle = mProgress * 1.0f / mMax * 360;
            //是否以图片的中心为圆点
            boolean useCenter = false;
            if (mPaint == null) {
                mPaint = new Paint();
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(12);
                mPaint.setAntiAlias(true);
                //                mPaint.setColor(Color.argb(255,212, 200, 55));
                mPaint.setColor(Color.argb(255, 90, 180, 63));
            }
            canvas.drawArc(mOval, startAngle, sweepAngle, useCenter, mPaint);
        }

    }
}
