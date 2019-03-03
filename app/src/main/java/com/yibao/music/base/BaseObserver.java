package com.yibao.music.base;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @ Name:   BaseObserver
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/14/ 17:36
 * @ Des:    TODO
 * @author Luoshipeng
 */
public class BaseObserver<T> implements Observer<T> {


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
