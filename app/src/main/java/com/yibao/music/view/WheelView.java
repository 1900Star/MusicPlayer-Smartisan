package com.yibao.music.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Author: Luoshipeng
 * @ Name:   WhereView
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2019/1/1/ 18:18
 * @ Des:    TODO
 */
public class WheelView extends ScrollView {
    public static class OnWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }
    }


    private Context context;

    private LinearLayout views;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    List<String> items;

    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<>();
        }
        items.clear();
        items.addAll(list);

        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }

        initData();

    }


    public static final int OFF_SET_DEFAULT = 1;
    int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    int displayItemCount; // 每页显示的数量

    int selectedIndex = 1;


    private void init(Context context) {
        this.context = context;

//        this.setOrientation(VERTICAL);
        this.setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);

        scrollerTask = () -> {

            int newY = getScrollY();
            if (initialY - newY == 0) { // stopped
                final int remainder = initialY % itemHeight;
                final int divided = initialY / itemHeight;
                if (remainder == 0) {
                    selectedIndex = divided + offset;

                    onSeletedCallBack();
                } else {
                    if (remainder > itemHeight / 2) {
                        WheelView.this.post(() -> {
                            WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                            selectedIndex = divided + offset + 1;
                            onSeletedCallBack();
                        });
                    } else {
                        WheelView.this.post(() -> {
                            WheelView.this.smoothScrollTo(0, initialY - remainder);
                            selectedIndex = divided + offset;
                            onSeletedCallBack();
                        });
                    }


                }


            } else {
                initialY = getScrollY();
                WheelView.this.postDelayed(scrollerTask, newCheck);
            }
        };


    }

    int initialY;

    Runnable scrollerTask;
    int newCheck = 50;

    public void startScrollerTask() {

        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;

        for (String item : items) {
            views.addView(createView(item));
        }

        refreshItemView(0);
    }

    int itemHeight = 0;

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        float selecteTvSize = 22;
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, selecteTvSize);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(15);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        refreshItemView(t);

        if (t > oldt) {
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
            scrollDirection = SCROLL_DIRECTION_UP;

        }


    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }

        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView textView = (TextView) views.getChildAt(i);
            if (null == textView) {
                return;
            }
            if (position == i) {
                textView.setTextColor(ColorUtil.textName);
            } else {
                textView.setTextColor(ColorUtil.normalTimeTv);
            }
        }
    }

    /**
     * 获取选中区域的边界
     */

    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    Paint paint;
    int viewWidth;


    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(getResources().getDrawable(R.drawable.time_picker_widget_bg));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    /**
     * 选中回调
     */
    private void onSeletedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex >= items.size() ? 0 : selectedIndex));
        }

    }

    public void setSeletion(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(() -> WheelView.this.smoothScrollTo(0, p * itemHeight));

    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

}
