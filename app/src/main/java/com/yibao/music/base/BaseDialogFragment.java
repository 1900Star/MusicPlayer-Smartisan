package com.yibao.music.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.yibao.music.MyApplication;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/31 18:50
 * @author Stran
 */
public abstract class BaseDialogFragment
        extends DialogFragment
{

    public MyApplication mApplication;
    public CompositeDisposable mDisposable;
    public static int MAX_DOWN_PREGRESS = 100;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow()
                   .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mApplication = (MyApplication) getActivity().getApplication();
        mDisposable = new CompositeDisposable();
        return getViews();
    }

    public abstract View getViews();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
        mDisposable.clear();
        }
    }
}
