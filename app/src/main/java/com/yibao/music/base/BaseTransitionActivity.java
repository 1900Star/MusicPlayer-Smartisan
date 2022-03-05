package com.yibao.music.base;

import com.bumptech.glide.Glide;
import com.yibao.music.R;
import com.yibao.music.base.listener.OnGlideLoadListener;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SpUtil;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BaseActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/20 13:07
 * @描述： {TODO}
 */

public abstract class BaseTransitionActivity extends BaseActivity implements OnGlideLoadListener {
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void resumeRequests() {
        if (!isDestroyed()) {
            Glide.with(this).resumeRequests();
        }
    }

    @Override
    public void pauseRequests() {
        if (!isDestroyed()) {
            Glide.with(this).pauseRequests();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SpUtil.setAddTodPlayListFlag(this, Constants.NUMBER_ZERO);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
    }

}
