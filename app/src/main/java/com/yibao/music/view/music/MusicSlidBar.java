package com.yibao.music.view.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.artisanlist.MusicListAdapter;
import com.yibao.music.util.LogUtil;


/**
 * @author ThinkPad
 * @date 2016/8/12
 */
public class MusicSlidBar
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
    private TextView mContactToast;
    private RecyclerView mRecyclerView;
    private int mIndex;
    private Paint mCirclePaint;
    private Context mContext;
    private TextView mStickyViwe;

    public MusicSlidBar(Context context) {
        super(context);
        initView(context);
    }

    public MusicSlidBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MusicSlidBar(Context context, AttributeSet attrs, int defStyleAttr) {
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
                mTvPaint.setColor(Color.parseColor("#FF4081"));
                canvas.drawCircle(viewW / 2,
                        i * singleHeight + singleHeight / 2 - dip2px(1),
                        dip2px(12),
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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //改变背景颜色
                setBackgroundResource(R.drawable.shape_slibar);
                mCirclePaint.setColor(Color.parseColor("#6e6e6e"));
                performTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mCirclePaint.setColor(Color.parseColor("#6e6e6e"));
                performTouch(event);
                break;
            case MotionEvent.ACTION_UP:
                //设置背景为透明
                mCirclePaint.setColor(Color.TRANSPARENT);
                setBackgroundColor(Color.TRANSPARENT);
                mTvPaint.setColor(Color.GRAY);
                //隐藏toast
//                if (mContactToast != null) {
//                    mContactToast.setVisibility(GONE);
//                }
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
        String name = names[mIndex];
        //显示toast字母
        initView();
//        mContactToast.setText(name);
        //设置导航字母
        mStickyViwe.setText(name);
        LogUtil.d("设置导航字母 *******   " + name);
        //显示toast
//        mContactToast.setVisibility(VISIBLE);
        //处理列表section位置
        //        rvListener();
        // 获取listview的适配器
        MusicListAdapter adapter = (MusicListAdapter) mRecyclerView.getAdapter();
        //获取sections的集合
        String[] sections = adapter.getSections();

        //当前是否要处理section
        int sectionIndex = -1;
        for (int i = 0; i < sections.length; i++) {
            if (name.equals(sections[i])) {
                sectionIndex = i;
            }
        }
        if (sectionIndex == -1) {
            return;
        }


        //        //确定当前section首联系人的position
        int positionForSection = adapter.getPositionForSection(sectionIndex);
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        manager.scrollToPositionWithOffset(positionForSection, 0);


    }

    private void initView() {
        // 获取父容器
        FrameLayout parent = (FrameLayout) getParent();
        if (mStickyViwe == null) {
            mStickyViwe = parent.findViewById(R.id.music_rv_sticky_view);
        }
//        if (mContactToast == null) {
//            mContactToast = parent.findViewById(R.id.contac_toast);
//        }
        //初始化listview
        if (mRecyclerView == null) {
            mRecyclerView = parent.findViewById(R.id.rv);
        }
    }


    public int dip2px(float dpValue) {
        final float scale = mContext.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
