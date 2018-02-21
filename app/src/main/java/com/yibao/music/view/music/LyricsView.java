package com.yibao.music.view.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.model.MusicLyrBean;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.LyricsUtil;

import java.util.ArrayList;

/**
 * Author：Sid
 * Des：${自定义歌词View}
 * Time:2017/9/14 01:16
 *
 * @author Stran
 */
public class LyricsView
        extends TextView {

    private Paint mPaint;
    private int mViewW;
    private int mViewH;
    private String mCurrentLrc;
    private static ArrayList<MusicLyrBean> mList;
    private static int centerLine;
    private float mBigText;
    private int mLyricsSelected;
    private float smallText;
    private int mLyricsNormal;
    private int lineHeight;
    private int duration;
    private int progress;
    private float mFirstDown;

    public LyricsView(Context context) {
        super(context);
        initView();
    }


    public LyricsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mLyricsSelected = getResources().getColor(R.color.lyricsSelected);
        mLyricsNormal = getResources().getColor(R.color.lyricsNormal);
        mBigText = getResources().getDimension(R.dimen.bigLyrics);
        smallText = getResources().getDimension(R.dimen.smallLyrics);
        lineHeight = getResources().getDimensionPixelSize(R.dimen.line_height);
        setMaxLines(2);

        mPaint.setAntiAlias(true);
        mPaint.setColor(mLyricsSelected);
        mPaint.setTextSize(mBigText);
        mList = new ArrayList<>();

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
        if (mList == null || mList.size() == 1 || mList.size() == 0) {
            drawSingLine(canvas);
        } else {
            drawMunitLine(canvas);
        }

    }

    /**
     * 绘制多行歌词。
     *
     * @param canvas
     */
    private void drawMunitLine(Canvas canvas) {
        //        中间行y=中间行开始位置-移的距离
        int lineTime;
        //        最后一行居中
        if (centerLine == mList.size() - 1) {
            //     行可用时间 = 总进度 - 行开始时间
            lineTime = duration - mList.get(centerLine)
                    .getStartTime();
        } else {
//                           其它行居中，
//            行可用时间 = 下一行开始 时间 - 居中行开始 时间
            lineTime = mList.get(centerLine + 1)
                    .getStartTime() - mList.get(centerLine)
                    .getStartTime();
        }
        //          播放时间偏移 = 播放进度 - 居中开始时间
        int offsetTime = progress - mList.get(centerLine)
                .getStartTime();
        //           播放时间比 = 播放时间偏移/行可用时间
        float offsetTimePercent = offsetTime / (float) lineTime;

        //          y方向移动的距离 = 行高*播放时间比
        int offsetY = (int) (lineHeight * offsetTimePercent);
        //          中间行歌词
        String centerLrc = mList.get(centerLine)
                .getContent();
        Rect bounds = new Rect();
        mPaint.getTextBounds(centerLrc, 0, centerLrc.length(), bounds);
        //          中间行 y view 高度一半 + text高度一半
        //          中间行y = 中间行开始 位置 - 移动的距离
        int centerY = mViewH / 2 + bounds.height() / 2 - offsetY;
        for (int i = 0; i < mList.size(); i++) {
            if (i == centerLine) {
                mPaint.setTextSize(mBigText);
                mPaint.setColor(mLyricsSelected);
            } else {

                mPaint.setTextSize(smallText);
                mPaint.setColor(mLyricsNormal);
            }
            mCurrentLrc = mList.get(i)
                    .getContent();
            float textW = mPaint.measureText(mCurrentLrc, 0, mCurrentLrc.length());

            float x = mViewW / 2 - textW / 2;
            float y = centerY + (i - centerLine) * lineHeight;
            canvas.drawText(mCurrentLrc, 0, mCurrentLrc.length(), x, y, mPaint);

        }

    }

    /**
     * 根据播放时间滚动歌词，将已经播放的歌词滚动出屏幕。
     *
     * @param progress
     * @param duration
     */
    public void rollText(int progress, int duration) {
        LogUtil.d("==================正在滚动歌词");
        if (mList == null || mList.size() == 0) {
            return;
        }
        this.progress = progress;
        this.duration = duration;
        int startTime = mList.get(mList.size() - 1)
                .getStartTime();

        if (progress >= startTime) {
            centerLine = mList.size() - 1;
        } else {
            for (int i = 0; i < mList.size() - 1; i++) {
                if (progress >= mList.get(i)
                        .getStartTime() && progress < mList.get(i + 1)
                        .getStartTime()) {
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
     * @param songName
     * @param artist
     */
    public void setLrcFile(String songName, String artist) {

        mList = LyricsUtil.getLyricList(songName, artist);
        //默认剧中行=0
        centerLine = 0;
    }


    private void drawSingLine(Canvas canvas) {
        Rect bounds = new Rect();
        mCurrentLrc = "暂无歌词";
        mPaint.setColor(mLyricsNormal);
        mPaint.getTextBounds(mCurrentLrc, 0, mCurrentLrc.length(), bounds);
        float x = mViewW / 2 - bounds.width() / 2;
        float y = mViewH / 2 + bounds.height() / 2;
        canvas.drawText(mCurrentLrc, 0, mCurrentLrc.length(), x, y, mPaint);
    }
}
