package com.yibao.music.view.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yibao.music.R;
import com.yibao.music.util.LogUtil;


/**
 * @author ThinkPad
 * @date 2016/8/12
 */
public class MusicSlideBarBack
        extends View {
    private String[] names = new String[]{"A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z",
            "#"};
    private Paint mTvPaint;
    private int viewW;
    private int singleHeight;
    private int mIndex;
    private Paint mCirclePaint;
    private Context mContext;
    private OnMusicSlidbarTouchListener mTouchListener;

    public MusicSlideBarBack(Context context) {
        super(context);
        initView(context);
    }

    public MusicSlideBarBack(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MusicSlideBarBack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mTvPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // x以字体的x方向中点为坐标点
        mTvPaint.setTextAlign(Paint.Align.CENTER);
        mTvPaint.setTextSize(30);
        mTvPaint.setAntiAlias(true);
        mTvPaint.setFakeBoldText(true);
        mTvPaint.setStyle(Paint.Style.FILL);


        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.TRANSPARENT);
        mIndex = 0;


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewW = w;
        singleHeight = h / (names.length + 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < names.length; i++) {
            if (i == 0) {
                mTvPaint.setColor(Color.GRAY);
            } else if (i == mIndex) {
                mTvPaint.setColor(Color.parseColor("#E64040"));

                canvas.drawCircle(viewW / 2,
                        i * singleHeight + singleHeight / 2 - dip2px(1),
                        dip2px(8),
                        mCirclePaint);
            } else {
                mTvPaint.setColor(Color.GRAY);

            }
            Paint.FontMetrics metrics = mTvPaint.getFontMetrics();

            //singleHeight * (i + 1) 加1是为了导航栏能够完整显示 ，
            // 比如最下面的(Z 和 #)，不同尺寸的设备可能会有不同，可能 会加2 或者加3，根据情况调整
            canvas.drawText(names[i],
                    viewW / 2,
                    i * singleHeight + singleHeight / 2 + metrics.bottom,
                    mTvPaint);
            //                canvas.drawText(names[i], viewW / 2, singleHeight * (i + 1), mTvPaint);

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //改变背景颜色
                setBackgroundResource(R.drawable.shape_slibar);
                mCirclePaint.setColor(Color.parseColor("#FF939396"));
//                performTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mCirclePaint.setColor(Color.parseColor("#FF939396"));
//                performTouch(event);
//                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //设置背景为透明
                mCirclePaint.setColor(Color.TRANSPARENT);
                setBackgroundColor(Color.TRANSPARENT);
                mTvPaint.setColor(Color.GRAY);
//                invalidate();
                break;
            default:
                break;
        }


        return true;
    }


    /**
     * 处理触摸事件
     *
     * @param event
     */
    private void performTouch(MotionEvent event) {
        //确定触摸的是哪个字母
        float eventY = event.getY();
        //当前触摸点的高度/每一个的高度
        mIndex = (int) (eventY / singleHeight);
        if (mIndex < 0) {
            mIndex = 0;
        } else if (mIndex > names.length - 1) {
            mIndex = names.length - 1;
        }
        postInvalidate();
        //获取当前要显示的字母
        String cureentIndex = names[mIndex];
        LogUtil.d("=======Section index=======  " + cureentIndex);
        mTouchListener.cureentSectionIndex(cureentIndex);

        //显示toast
//        mContactToast.setVisibility(VISIBLE);
        //处理列表section位置
        //        rvListener();
        // 获取listview的适配器
//        SongAdapter adapter = (SongAdapter) mRecyclerView.getAdapter();
        //获取sections的集合
//        String[] sections = adapter.getSections();

        //当前是否要处理section
//        int sectionIndex = -1;
//        for (int i = 0; i < sections.length; i++) {
//            if (name.equals(sections[i])) {
//                sectionIndex = i;
//            }
//        }
//        if (sectionIndex == -1) {
//            return;
//        }


//        //        //确定当前section首联系人的position
//        int positionForSection = adapter.getPositionForSection(sectionIndex);
//        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//        manager.scrollToPositionWithOffset(positionForSection, 0);


    }


    public interface OnMusicSlidbarTouchListener {
        /**
         * 将当前触摸的SlideBar上的字母传出去
         * @param sectionIndex
         */
        void cureentSectionIndex(String sectionIndex);

    }

    public void setSlidbarTouchListener(OnMusicSlidbarTouchListener touchListener) {
        this.mTouchListener = touchListener;
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
