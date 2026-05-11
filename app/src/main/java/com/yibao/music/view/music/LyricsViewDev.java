package com.yibao.music.view.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
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

public class LyricsViewDev extends AppCompatTextView {
    private String TAG = "===";
    private Paint mPaint;
    private int mViewW;
    private int mViewH;
    private String mCurrentLrc;
    private List<MusicLyricBean> musicLyrList;
    private int centerLine;
    private float mBigText;
    private int mLyricsSelected;
    private float smallText;
    private int mLyricsNormal;
    private int lineHeight;
    private int duration;
    private int currentProgress;
    private Rect mBounds;
    private Rect mSingleBounds;
    private String mLyricsMsg;

    // 手动滚动相关变量
    private Scroller mScroller;
    private GestureDetector mGestureDetector;
    private boolean isDragging = false;
    private boolean isAutoScroll = true; // 是否允许自动滚动
    private int touchSlop;
    private float lastY;
    private VelocityTracker velocityTracker;
    private int minimumVelocity;
    private int maximumVelocity;
    private int manualScrollOffset = 0; // 手动滚动偏移量

    public LyricsViewDev(Context context) {
        super(context);
        initView();
    }

    public LyricsViewDev(Context context, @Nullable AttributeSet attrs) {
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

        // 初始化滚动相关
        mScroller = new Scroller(getContext());
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = configuration.getScaledTouchSlop();
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();

        // 设置可点击，否则触摸事件可能不响应
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
        int offsetY = isAutoScroll ? getOffsetY() : 0;
        // 计算总偏移量 = 自动滚动偏移 + 手动滚动偏移
        int totalOffset = offsetY + manualScrollOffset;

        String centerLrc = musicLyrList.get(centerLine).getContent();
        mPaint.getTextBounds(centerLrc, 0, centerLrc.length(), mBounds);
        int centerY = mViewH / 2 + mBounds.height() / 2 - totalOffset;

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

            // 只绘制可见区域内的歌词，优化性能
            if (y > -mBounds.height() && y < mViewH + mBounds.height()) {
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

        int offsetTime = currentProgress - musicLyrList.get(centerLine).getStartTime();
        float offsetTimePercent = offsetTime / (float) lineTime;
        return (int) (lineHeight * offsetTimePercent);
    }

    public void rollText(int progress, int duration) {
        LogUtil.d(TAG, "当前进度：" + progress + "  == 时长： " + duration);
        // 如果处于手动滚动模式，不执行自动滚动
        if (!isAutoScroll || musicLyrList == null || musicLyrList.isEmpty()) {
            return;
        }

        this.currentProgress = progress;
        this.duration = duration;
        int startTime = musicLyrList.get(musicLyrList.size() - 1).getStartTime();

        if (progress >= startTime) {
            centerLine = musicLyrList.size() - 1;
        } else {
            for (int i = 0; i < musicLyrList.size() - 1; i++) {
                boolean b = progress >= musicLyrList.get(i).getStartTime()
                        && progress < musicLyrList.get(i + 1).getStartTime();
                if (b) {
                    centerLine = i;
                    break;
                }
            }
        }
        invalidate();
    }

    public void setLrcFile(List<MusicLyricBean> lrcList, String lyricsMsg) {
        mLyricsMsg = lyricsMsg;
        musicLyrList = lrcList;
        centerLine = 0;
        manualScrollOffset = 0; // 重置手动滚动偏移
        isAutoScroll = true; // 重置为自动滚动模式
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

    // 手动滚动相关方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 将触摸事件交给手势检测器处理
        boolean result = mGestureDetector.onTouchEvent(event);
        LogUtil.d("===", "Y轴：" + event.getY() + " == " + event.getAction());
        // 同时处理 velocity tracker
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }

        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                lastY = event.getY();
                isDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                }
                float currentY = event.getY();
                float dy = lastY - currentY;

                if (!isDragging && Math.abs(dy) > touchSlop) {
                    isDragging = true;
                    // 开始拖动时暂停自动滚动
                    isAutoScroll = false;
                }

                if (isDragging) {
                    // 计算滚动偏移
                    manualScrollOffset += (int) dy;
                    // 限制滚动范围
                    limitScrollOffset();
                    lastY = currentY;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    // 处理惯性滚动
                    if (velocityTracker != null) {
                        velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
                        int initialVelocity = (int) velocityTracker.getYVelocity();

                        if (Math.abs(initialVelocity) > minimumVelocity) {
//                            fling(-initialVelocity);
                        } else {
                            // 没有足够的速度，停止拖动状态
//                            isDragging = false;
                        }
                    }

                    // 释放velocity tracker
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }
                }
                break;
        }
        return result || super.onTouchEvent(event);
    }

    private void fling(int velocityY) {
        // 设置惯性滚动参数
        mScroller.fling(
                0, manualScrollOffset, // 起始位置
                0, velocityY, // 速度
                0, 0, // x方向范围
                -lineHeight * (musicLyrList.size() - 1), // 最小y偏移
                0 // 最大y偏移
        );
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            manualScrollOffset = mScroller.getCurrY();
            limitScrollOffset();
            invalidate();
        } else if (!isAutoScroll && !isDragging) {
            // 滚动结束后，根据当前位置更新centerLine
//            updateCenterLineByScrollOffset();
            startAutoScrollDelay();

        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final long AUTO_SCROLL_DELAY = 3000; // 无操作恢复自动滚动延迟

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


    // 限制滚动偏移在有效范围内
    private void limitScrollOffset() {
        int minOffset = -lineHeight * (musicLyrList.size() - 1);
        int maxOffset = lineHeight * musicLyrList.size();
        manualScrollOffset = Math.max(minOffset, Math.min(maxOffset, manualScrollOffset));
    }

    // 根据滚动偏移更新中心线
    private void updateCenterLineByScrollOffset() {
        // 计算当前应该居中的行
        int newCenterLine = -manualScrollOffset / lineHeight;
        newCenterLine = Math.max(0, Math.min(musicLyrList.size() - 1, newCenterLine));

        if (newCenterLine != centerLine) {
            centerLine = newCenterLine;
            invalidate();
        }
    }

    // 手势监听器
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            LogUtil.d(TAG, "AAAAAA");
            // 确保scroller停止
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            return true; // 必须返回true才能接收后续事件
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtil.d(TAG, "AAAAAA");
            if (isDragging) {
                fling((int) -velocityY);
                return true;
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtil.d(TAG, "BBBBBB");
            // 这里的distanceY是上一次位置到当前位置的距离
            manualScrollOffset += distanceY;
            limitScrollOffset();
            updateCenterLineByScrollOffset();
            invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            LogUtil.d(TAG, "CCCCCCC");
            syncCenterLineWithProgress();
            // 单击时恢复自动滚动
            isAutoScroll = true;
            // 根据当前位置更新进度
            updateProgressByCenterLine();
            invalidate();
            return true;
        }
    }


    // 根据当前中心线位置更新进度
    private void updateProgressByCenterLine() {
        if (musicLyrList == null || musicLyrList.isEmpty() || centerLine >= musicLyrList.size()) {
            return;
        }

        // 可以在这里回调更新播放器进度
        int newProgress = musicLyrList.get(centerLine).getStartTime();
        // 如果有监听器，可以通知外部更新进度
        if (mOnLyricsClickListener != null) {
            mOnLyricsClickListener.onLyricClick(newProgress);
        }
    }

    // 歌词点击监听器，用于更新播放进度
    public interface OnLyricsClickListener {
        void onLyricClick(int progress);
    }

    // 强制同步中心线与当前进度
    private void syncCenterLineWithProgress() {
        if (musicLyrList != null && !musicLyrList.isEmpty() && currentProgress >= 0) {
            findCurrentCenterLine(currentProgress);
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

    private OnLyricsClickListener mOnLyricsClickListener;

    public void setOnLyricsClickListener(OnLyricsClickListener listener) {
        mOnLyricsClickListener = listener;
    }

    // 提供方法手动恢复自动滚动
    public void resumeAutoScroll() {
        isAutoScroll = true;
        invalidate();
    }
}
