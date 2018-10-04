package com.yibao.music.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yibao.music.R;
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
        ZoomImageView creatZoomView = ImageUitl.creatZoomView(container.getContext());
        creatZoomView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        String url = RandomUtil.getRandomUrl();
        ImageUitl.loadPlaceholder(container.getContext(), url, creatZoomView);
        creatZoomView.setOnClickListener(v -> {
            if (mZoomViewClickListener != null) {
                mZoomViewClickListener.doSomething();
            }
        });
        container.addView(creatZoomView);
        return creatZoomView;
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
