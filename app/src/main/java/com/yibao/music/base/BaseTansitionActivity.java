package com.yibao.music.base;

import com.bumptech.glide.Glide;
import com.yibao.music.R;
import com.yibao.music.base.listener.OnGlideLoadListener;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BaseActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/20 13:07
 * @描述： {TODO}
 */

public abstract class BaseTansitionActivity extends BaseActivity implements OnGlideLoadListener {
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_push_out);
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

}
