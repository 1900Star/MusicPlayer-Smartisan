package com.yibao.music.view.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.yibao.music.R;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Des：${自定义歌词View}
 * Time:2017/9/14 01:16
 *
 * @author Stran
 */
public class LyricsView
        extends androidx.appcompat.widget.AppCompatTextView {

    private Paint mPaint;
    private int mViewW;
    private int mViewH;
    private String mCurrentLrc;
    private static List<MusicLyricBean> musicLyrList;
    private static int centerLine;
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

    public LyricsView(Context context) {
        super(context);
        initView();
    }


    public LyricsView(Context context, @Nullable AttributeSet attrs) {
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
        if (musicLyrList == null || musicLyrList.size() == 1 || musicLyrList.isEmpty()) {
            drawSingLine(canvas);
        } else {
            drawMunitLine(canvas);
        }

    }

    /**
     * 绘制多行歌词。
     *
     * @param canvas c
     */
    private void drawMunitLine(Canvas canvas) {
        //        中间行y=中间行开始位置-移的距离
        int offsetY = getOffsetY();
        //          中间行歌词
        String centerLrc = musicLyrList.get(centerLine)
                .getContent();
        // 歌词绘制的边界
        mPaint.getTextBounds(centerLrc, 0, centerLrc.length(), mBounds);
        //          中间行 y view 高度一半 + text高度一半
        //          中间行y = 中间行开始 位置 - 移动的距离
        int centerY = mViewH / 2 + mBounds.height() / 2 - offsetY;
        for (int i = 0; i < musicLyrList.size(); i++) {
            if (i == centerLine) {
                mPaint.setTextSize(mBigText);
                mPaint.setColor(mLyricsSelected);
            } else {

                mPaint.setTextSize(smallText);
                mPaint.setColor(mLyricsNormal);
            }
            mCurrentLrc = musicLyrList.get(i)
                    .getContent();


            float textW = mPaint.measureText(mCurrentLrc, 0, mCurrentLrc.length());

            float x = (mViewW >> 1) - textW / 2;
            float y = centerY + (i - centerLine) * lineHeight;
            canvas.drawText(mCurrentLrc, 0, mCurrentLrc.length(), x, y, mPaint);

        }

    }

    private int getOffsetY() {
        int lineTime;
        //        最后一行居中
        if (centerLine == musicLyrList.size() - 1) {
            //     行可用时间 = 总进度 - 行开始时间
            lineTime = duration - musicLyrList.get(centerLine)
                    .getStartTime();
        } else {
//                           其它行居中，
//            行可用时间 = 下一行开始 时间 - 居中行开始 时间
            lineTime = musicLyrList.get(centerLine + 1)
                    .getStartTime() - musicLyrList.get(centerLine)
                    .getStartTime();
        }
        //          播放时间偏移 = 播放进度 - 居中开始时间
        int offsetTime = currentProgress - musicLyrList.get(centerLine)
                .getStartTime();
        //           播放时间比 = 播放时间偏移/行可用时间
        float offsetTimePercent = offsetTime / (float) lineTime;

        //          y方向移动的距离 = 行高*播放时间比
        return (int) (lineHeight * offsetTimePercent);
    }

    /**
     * 根据播放时间滚动歌词，将已经播放的歌词滚动出屏幕。
     *
     * @param progress p
     * @param duration d
     */
    public void rollText(int progress, int duration) {
        if (musicLyrList == null || musicLyrList.isEmpty()) {
            return;
        }
        this.currentProgress = progress;
        this.duration = duration;
        int startTime = musicLyrList.get(musicLyrList.size() - 1)
                .getStartTime();

        if (progress >= startTime) {
            centerLine = musicLyrList.size() - 1;
        } else {
            for (int i = 0; i < musicLyrList.size() - 1; i++) {
                boolean b = progress >= musicLyrList.get(i)
                        .getStartTime() && progress < musicLyrList.get(i + 1)
                        .getStartTime();
                if (b) {
                    centerLine = i;
                    break;
                }
            }


        }
//        触发重新绘制
        invalidate();
    }

    /**
     * 根据歌曲名和歌手名查找歌词，并将歌词解析到List里。
     *
     * @param lrcList s
     */
    public void setLrcFile(List<MusicLyricBean> lrcList, String lyricsMsg) {
        mLyricsMsg = lyricsMsg;
        musicLyrList = lrcList;
        //默认剧中行=0
        centerLine = 0;
        invalidate();
    }

    private void drawSingLine(Canvas canvas) {
        mCurrentLrc = mLyricsMsg.equals(Constant.PURE_MUSIC) ? Constant.PURE_MUSIC : Constant.NO_LYRICS;
        mPaint.setColor(mLyricsNormal);
        mPaint.getTextBounds(mCurrentLrc, 0, mCurrentLrc.length(), mSingleBounds);
        float x = (mViewW >> 1) - (mSingleBounds.width() >> 1);
        float y = (mViewH >> 1) + (mSingleBounds.height() >> 1);
        canvas.drawText(mCurrentLrc, 0, mCurrentLrc.length(), x, y, mPaint);

    }

}
