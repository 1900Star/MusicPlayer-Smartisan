package com.yibao.music.view.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.yibao.music.R;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constant;
import com.yibao.music.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;
import java.util.List;

public class LyricsViewBack extends AppCompatTextView {

    private Paint mPaint;
    private int mViewW;
    private int mViewH;
    private String mCurrentLrc;
    private List<MusicLyricBean> musicLyrList;
    private int centerLine; // 当前居中的歌词行
    private float mBigText;
    private int mLyricsSelected;
    private float smallText;
    private int mLyricsNormal;
    private int lineHeight; // 每行歌词高度
    private int duration;
    private int currentProgress; // 当前播放进度（毫秒）
    private Rect mBounds;
    private Rect mSingleBounds;
    private String mLyricsMsg;

    // 手动滚动核心变量
    private Scroller mScroller;
    private GestureDetector mGestureDetector;
    private boolean isDragging = false;
    private boolean isAutoScroll = true; // 自动/手动模式切换
    private int touchSlop; // 触摸滑动阈值
    private float lastY; // 上次触摸Y坐标
    private VelocityTracker velocityTracker;
    private int minimumVelocity;
    private int maximumVelocity;
    private int manualScrollOffset = 0; // 手动滚动总偏移量
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final long AUTO_SCROLL_DELAY = 3000; // 无操作恢复自动滚动延迟

    public LyricsViewBack(Context context) {
        super(context);
        initView();
    }

    public LyricsViewBack(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mBounds = new Rect();
        mSingleBounds = new Rect();
        mLyricsSelected = ColorUtil.lyricsSelecte;
        mLyricsNormal = ColorUtil.lyricsNormal;
        mBigText = getResources().getDimension(R.dimen.bigLyrics);
        smallText = getResources().getDimension(R.dimen.smallLyrics);
        lineHeight = getResources().getDimensionPixelSize(R.dimen.line_height);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMaxLines(2);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mLyricsSelected);
        mPaint.setTextSize(mBigText);
        musicLyrList = new ArrayList<>();

        // 初始化滚动相关组件
        mScroller = new Scroller(getContext());
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = configuration.getScaledTouchSlop();
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();

        setClickable(true);
        setLongClickable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewW = w;
        mViewH = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (musicLyrList == null || musicLyrList.size() <= 1 || musicLyrList.isEmpty()) {
            drawSingLine(canvas);
        } else {
            drawMunitLine(canvas);
        }
    }

    private void drawMunitLine(Canvas canvas) {
        // 手动模式用手动偏移，自动模式用进度计算的偏移
        int totalOffset = isAutoScroll ? getOffsetY() : manualScrollOffset;

        String centerLrc = musicLyrList.get(centerLine).getContent();
        mPaint.getTextBounds(centerLrc, 0, centerLrc.length(), mBounds);
        int centerY = mViewH / 2 + mBounds.height() / 2 - totalOffset;

        // 只绘制可见区域内的歌词
        for (int i = 0; i < musicLyrList.size(); i++) {
            if (i == centerLine) {
                mPaint.setTextSize(mBigText);
                mPaint.setColor(mLyricsSelected);
            } else {
                mPaint.setTextSize(smallText);
                mPaint.setColor(mLyricsNormal);
            }

            mCurrentLrc = musicLyrList.get(i).getContent();
            float textW = mPaint.measureText(mCurrentLrc);
            float x = (mViewW >> 1) - textW / 2;
            float y = centerY + (i - centerLine) * lineHeight;

            if (y > -lineHeight && y < mViewH + lineHeight) {
                canvas.drawText(mCurrentLrc, x, y, mPaint);
            }
        }
    }

    private int getOffsetY() {
        int lineTime;
        if (centerLine == musicLyrList.size() - 1) {
            lineTime = duration - musicLyrList.get(centerLine).getStartTime();
        } else {
            lineTime = musicLyrList.get(centerLine + 1).getStartTime()
                    - musicLyrList.get(centerLine).getStartTime();
        }

        if (lineTime <= 0) {
            return 0;
        }

        int offsetTime = currentProgress - musicLyrList.get(centerLine).getStartTime();
        offsetTime = Math.max(0, Math.min(offsetTime, lineTime));

        return (int) (lineHeight * (offsetTime / (float) lineTime));
    }

    public void rollText(int progress, int duration) {
        if (!isAutoScroll || musicLyrList == null || musicLyrList.isEmpty()) {
            return;
        }

        this.currentProgress = progress;
        this.duration = duration;
        int lastCenterLine = centerLine;
        findCurrentCenterLine(progress);

        if (lastCenterLine != centerLine) {
            invalidate();
        }
    }

    // 核心修复1：优化进度匹配逻辑，确保能找到正确的歌词行
    private void findCurrentCenterLine(int progress) {
        // 安全校验
        if (musicLyrList.isEmpty()) return;

        // 1. 处理最后一行
        if (progress >= musicLyrList.get(musicLyrList.size() - 1).getStartTime()) {
            centerLine = musicLyrList.size() - 1;
            return;
        }

        // 2. 处理第一行
        if (progress < musicLyrList.get(0).getStartTime()) {
            centerLine = 0;
            return;
        }

        // 3. 全量遍历查找（避免局部遍历遗漏）
        for (int i = 0; i < musicLyrList.size() - 1; i++) {
            int currentStart = musicLyrList.get(i).getStartTime();
            int nextStart = musicLyrList.get(i + 1).getStartTime();

            if (progress >= currentStart && progress < nextStart) {
                centerLine = i;
                return;
            }
        }

        // 4. 兜底：默认第一行
        centerLine = 0;
    }

    public void setLrcFile(List<MusicLyricBean> lrcList, String lyricsMsg) {
        mLyricsMsg = lyricsMsg;
        musicLyrList = new ArrayList<>(lrcList);
        centerLine = 0;
        manualScrollOffset = 0;
        isAutoScroll = true;
        invalidate();
    }

    private void drawSingLine(Canvas canvas) {
        mCurrentLrc = mLyricsMsg.equals(Constant.PURE_MUSIC) ?
                Constant.PURE_MUSIC : Constant.NO_LYRICS;
        mPaint.setColor(mLyricsNormal);
        mPaint.getTextBounds(mCurrentLrc, 0, mCurrentLrc.length(), mSingleBounds);
        float x = (mViewW >> 1) - (mSingleBounds.width() >> 1);
        float y = (mViewH >> 1) + (mSingleBounds.height() >> 1);
        canvas.drawText(mCurrentLrc, x, y, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (musicLyrList == null || musicLyrList.size() <= 1) {
            return super.onTouchEvent(event);
        }

        mHandler.removeCallbacks(mAutoScrollRunnable);
        boolean result = mGestureDetector.onTouchEvent(event);

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                lastY = event.getY();
                isDragging = false;
                if (isAutoScroll) {
                    isAutoScroll = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                float currentY = event.getY();
                float dy = lastY - currentY;

                if (!isDragging && Math.abs(dy) > touchSlop) {
                    isDragging = true;
                }

                if (isDragging) {
                    manualScrollOffset += dy;
                    limitScrollOffset();
//                    updateCenterLineByScrollOffset();
                    lastY = currentY;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                if (isDragging) {
//                    velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
//                    int initialVelocity = (int) velocityTracker.getYVelocity();
//
//                    if (Math.abs(initialVelocity) > minimumVelocity) {
//                        fling(-initialVelocity);
//                    } else {
//                        isDragging = false;
//                        startAutoScrollDelay();
//                    }
//                    velocityTracker.recycle();
//                    velocityTracker = null;
//                } else {
//                    // 单击恢复自动滚动
//                    syncCenterLineWithProgress();
//                    isAutoScroll = true;
//                    manualScrollOffset = 0;
//                    updateProgressByCenterLine();
//                    invalidate();
//                }
                break;
        }
        return result || super.onTouchEvent(event);
    }

    private void fling(int velocityY) {
        if (musicLyrList == null || musicLyrList.size() <= 1) {
            return;
        }

        int maxScroll = lineHeight * (musicLyrList.size() - 1);
        mScroller.fling(
                0, manualScrollOffset,
                0, velocityY,
                0, 0,
                -maxScroll,
                lineHeight
        );
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            manualScrollOffset = mScroller.getCurrY();
            limitScrollOffset();
            updateCenterLineByScrollOffset();
            invalidate();
        } else if (!isAutoScroll && !isDragging) {
            startAutoScrollDelay();
        }
    }

    private void limitScrollOffset() {
        if (musicLyrList == null || musicLyrList.size() <= 1) {
            manualScrollOffset = 0;
            return;
        }

        int maxScroll = lineHeight * (musicLyrList.size() - 1);
        manualScrollOffset = Math.max(-maxScroll, Math.min(lineHeight, manualScrollOffset));
    }

    private void updateCenterLineByScrollOffset() {
        if (musicLyrList == null || musicLyrList.isEmpty()) {
            return;
        }

        int newCenterLine = (-manualScrollOffset + lineHeight / 2) / lineHeight;
        newCenterLine = Math.max(0, Math.min(musicLyrList.size() - 1, newCenterLine));

        if (newCenterLine != centerLine) {
            centerLine = newCenterLine;
        }
    }

    private void startAutoScrollDelay() {
        mHandler.postDelayed(mAutoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    // 核心修复2：增强同步逻辑，确保恢复时中心线正确匹配进度
    private Runnable mAutoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            // 1. 强制根据当前进度重新计算中心线（关键修复）
            int oldCenterLine = centerLine;
            findCurrentCenterLine(currentProgress);

            // 2. 重置手动偏移，确保自动滚动从正确位置开始
            manualScrollOffset = 0;

            // 3. 切换回自动模式
            isAutoScroll = true;

            // 4. 如果中心线有变化，强制重绘
            if (oldCenterLine != centerLine) {
                invalidate();
            }
        }
    };

    // 强制同步中心线与当前进度
    private void syncCenterLineWithProgress() {
        if (musicLyrList != null && !musicLyrList.isEmpty() && currentProgress >= 0) {
            findCurrentCenterLine(currentProgress);
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (isDragging) {
                fling((int) -velocityY);
                return true;
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            manualScrollOffset += distanceY;
            limitScrollOffset();
            updateCenterLineByScrollOffset();
            invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            syncCenterLineWithProgress();
            isAutoScroll = true;
            manualScrollOffset = 0;
            updateProgressByCenterLine();
            invalidate();
            return true;
        }
    }

    private void updateProgressByCenterLine() {
        if (musicLyrList == null || musicLyrList.isEmpty() || centerLine >= musicLyrList.size()) {
            return;
        }

        int newProgress = musicLyrList.get(centerLine).getStartTime();
        if (mOnLyricsClickListener != null) {
            mOnLyricsClickListener.onLyricClick(newProgress);
        }
    }

    public interface OnLyricsClickListener {
        void onLyricClick(int progress);
    }

    private OnLyricsClickListener mOnLyricsClickListener;

    public void setOnLyricsClickListener(OnLyricsClickListener listener) {
        mOnLyricsClickListener = listener;
    }

    public void resumeAutoScroll() {
        mHandler.removeCallbacks(mAutoScrollRunnable);
        syncCenterLineWithProgress();
        isAutoScroll = true;
        manualScrollOffset = 0;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }
}
