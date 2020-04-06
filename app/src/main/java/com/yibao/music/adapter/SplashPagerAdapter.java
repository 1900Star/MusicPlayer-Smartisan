package com.yibao.music.adapter;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.yibao.music.util.Api;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.view.ZoomImageView;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.adapter
 * @文件名: SplashPagerAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.strangermy98@gmail.com
 * @创建时间: 2018/3/16 19:52
 * @描述： {TODO}
 */

public class SplashPagerAdapter extends PagerAdapter {
    private OnZoomViewClickListener mZoomViewClickListener;
    private boolean mPicUrlFlag;
    public SplashPagerAdapter(boolean picUrlFlag) {
        this.mPicUrlFlag = picUrlFlag;
    }

    @Override
    public int getCount() {
        return Api.picUrlArr != null ? Api.picUrlArr.length : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ZoomImageView createZoomView = ImageUitl.createZoomView(container.getContext());
        String url = RandomUtil.getRandomUrl(mPicUrlFlag);
        ImageUitl.loadPlaceholder(container.getContext(), url, createZoomView);
        createZoomView.setOnClickListener(v -> {
            if (mZoomViewClickListener != null) {
                mZoomViewClickListener.doSomething();
            }
        });
        container.addView(createZoomView);
        return createZoomView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    public void setZoomViewClickListener(OnZoomViewClickListener listener) {
        this.mZoomViewClickListener = listener;
    }

    public interface OnZoomViewClickListener {
        /**
         * doSomething
         */
        void doSomething();
    }
}
