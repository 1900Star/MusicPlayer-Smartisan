package com.yibao.music.view.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.activity.SearchActivity;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SpUtil;

import butterknife.OnClick;

/**
 * @author Luoshipeng
 * @ Name:   QqControlBar
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/11/ 21:50
 * @ Des:    TODO
 */
public class MusicToolBar extends LinearLayout implements View.OnClickListener {

    private TextView mToolbarTitle;
    private TextView mTvEdit;
    private TextView mTvEditDelete;
    private ImageView mIvSearch;

    public MusicToolBar(Context context) {
        super(context);
        initView();
    }

    public MusicToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.music_toolbar_content, this, true);
        mToolbarTitle = findViewById(R.id.tv_music_toolbar_title);
        mTvEdit = findViewById(R.id.tv_edit);
        mTvEditDelete = findViewById(R.id.tv_edit_delete);
        mIvSearch = findViewById(R.id.iv_search);
        initData();
        initListener();
    }

    private void initData() {

    }

    private void initListener() {
        mTvEdit.setOnClickListener(this);
        mTvEditDelete.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
        mToolbarTitle.setOnClickListener(this);
        mToolbarTitle.setOnLongClickListener(v -> {
            SpUtil.setPicUrlFlag(getContext(), !SpUtil.getPicUrlFlag(getContext(), false));
            return true;
        });
    }

    public void setTvEditText(int resourceId) {
        mTvEdit.setText(resourceId);
    }

    public void setTvEditVisibility(boolean visibility) {
        mTvEdit.setVisibility(visibility ? VISIBLE : GONE);
    }

    public void setToolbarTitle(String toolbarTitle) {
        mToolbarTitle.setText(toolbarTitle);
    }

    public void setTvDeleteVisibility(int visibility) {
        mTvEditDelete.setVisibility(visibility);
    }

    public void setIvSearchVisibility(boolean visibility) {
        mIvSearch.setVisibility(visibility ? VISIBLE : GONE);
    }

    @Override
    @OnClick({R.id.tv_edit, R.id.tv_music_toolbar_title, R.id.iv_search, R.id.tv_edit_delete})
    public void onClick(View v) {
        if (mListener != null) {
            int vId = v.getId();
            switch (vId) {
                case R.id.tv_edit:
                    mListener.clickEdit();
                    break;
                case R.id.tv_music_toolbar_title:
                    mListener.switchMusicControlBar();
                    break;
                case R.id.iv_search:
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("pageType", Constants.NUMBER_ZERO);
                    getContext().startActivity(intent);
                    ((Activity) getContext()).overridePendingTransition(R.anim.dialog_push_in, 0);
                    break;
                case R.id.tv_edit_delete:
                    mListener.clickDelete();
                    break;
                default:
                    break;

            }
        }

    }

    private OnToolbarClickListener mListener;

    public void setClickListener(OnToolbarClickListener listener) {
        mListener = listener;
    }

    public interface OnToolbarClickListener {

        void clickEdit();

        void switchMusicControlBar();

        void clickDelete();
    }
}
