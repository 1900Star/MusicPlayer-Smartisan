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
import com.yibao.music.adapter.AlbumAdapter;
import com.yibao.music.adapter.ArtistAdapter;
import com.yibao.music.adapter.SongAdapter;
import com.yibao.music.util.Constants;


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
    private RecyclerView mRecyclerView;
    private int mIndex;
    private Paint mCirclePaint;
    private Context mContext;
    private TextView mStickyViwe;
    private int adapterType;

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
        mTvPaint.setFakeBoldText(true);
        mTvPaint.setStyle(Paint.Style.FILL);

        // 画选中时的圆形背景
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.TRANSPARENT);
        mIndex = 0;


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewW = w;
        //最后加2是为了取一个合适的绘制高度导，使航栏能够完整显示 ，
        // 防止出现高度不够或者最后一个字符与底部距离过大
        // 比如最下面的(Z 和 #)，不同尺寸的设备可能会有不同，根据情况调整。
        singleHeight = h / (names.length + 1) + 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < names.length; i++) {
            if (i == 0) {
                mTvPaint.setColor(Color.GRAY);
            } else if (i == mIndex) {
//                mTvPaint.setColor(Color.WHITE);
                mTvPaint.setColor(Color.parseColor("#E64040"));

                canvas.drawCircle(viewW / 2,
                        i * singleHeight + singleHeight / 2 - dip2px(1),
                        dip2px(8),
                        mCirclePaint);
            } else {
                mTvPaint.setColor(Color.GRAY);

            }
            Paint.FontMetrics metrics = mTvPaint.getFontMetrics();


            canvas.drawText(names[i],
                    viewW / 2,
                    i * singleHeight + singleHeight / 2 + metrics.bottom,
                    mTvPaint);

        }
    }


    /**
     * 设置Adapter的类型 1 : SongListAdapter  、 2  ：ArtistAdapter  、
     * 3  ： AlbumAdapter  普通视图  、 4  ： AlbumAdapter  平铺视图 GridView3列
     *
     * @param type
     */
    public void setAdapterType(int type) {
        this.adapterType = type;
    }

    public void setBarVisibility(int visibilityType) {
        if (visibilityType == Constants.NUMBER_ZOER) {
            setVisibility(View.VISIBLE);
        } else if (visibilityType == Constants.NUMBER_FOUR) {
            setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //改变背景颜色
                setBackgroundResource(R.drawable.shape_slibar);
                mCirclePaint.setColor(Color.parseColor("#8e8e8f"));
                performTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mCirclePaint.setColor(Color.parseColor("#8e8e8f"));
                performTouch(event);
                break;
            case MotionEvent.ACTION_UP:
                //设置背景为透明
                mCirclePaint.setColor(Color.TRANSPARENT);
                setBackgroundColor(Color.TRANSPARENT);
                mTvPaint.setColor(Color.GRAY);
                break;
            default:
                break;
        }


        return true;
    }


    /**
     * 处理SlideBar触摸事件
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
        //设置导航字母    “mStickyViwe.setText(name);”
        int sectionForPosition = 1;
        // 根据adapterType得到指定页面的Adapter
        if (adapterType == Constants.NUMBER_ONE) {
            SongAdapter songListAdapter = (SongAdapter) mRecyclerView.getAdapter();
            sectionForPosition = songListAdapter.getPositionForSection(name.charAt(0));
        } else if (adapterType == Constants.NUMBER_TWO) {
            ArtistAdapter artistAdapter = (ArtistAdapter) mRecyclerView.getAdapter();
            sectionForPosition = artistAdapter.getPositionForSection(name.charAt(0));
        } else if (adapterType == Constants.NUMBER_THRRE || adapterType == Constants.NUMBER_FOUR) {
            AlbumAdapter albumAdapter = (AlbumAdapter) mRecyclerView.getAdapter();
            sectionForPosition = albumAdapter.getPositionForSection(name.charAt(0));
        }
        if (sectionForPosition != -1) {
            LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            manager.scrollToPositionWithOffset(sectionForPosition, 0);
        }


    }


    private void initView() {
        // 获取父容器
        FrameLayout parent = (FrameLayout) getParent();
        if (mStickyViwe == null) {
            mStickyViwe = parent.findViewById(R.id.music_rv_sticky_view);
        }
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
