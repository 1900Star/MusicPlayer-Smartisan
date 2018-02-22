package com.yibao.music.util;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/4/29 10:35
 */
public class RxBus {


    public RxBus() {
    }

    private PublishSubject<Object> bus = PublishSubject.create();

    public void post(Object o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者

    public <T> Observable<T> toObserverable(Class<T> eventType) {
        //本质是先filter再cast
        return bus.ofType(eventType);
    }



}
