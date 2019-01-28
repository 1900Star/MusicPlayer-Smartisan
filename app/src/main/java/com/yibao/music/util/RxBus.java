package com.yibao.music.util;


import com.yibao.music.model.Message;

import org.reactivestreams.Subscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


/**
 * @ Author: Luoshipeng
 * Des：${TODO}
 * Time:2017/4/29 10:35
 */
public class RxBus {


    private final PublishSubject<Object> bus;
    private static volatile RxBus instance;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    private RxBus() {
        bus = PublishSubject.create();
    }

    // 单例RxBus
    public static RxBus getInstance() {
        RxBus rxBus = instance;
        if (instance == null) {
            synchronized (RxBus.class) {
                rxBus = instance;
                if (instance == null) {
                    rxBus = new RxBus();
                    instance = rxBus;
                }
            }
        }
        return rxBus;
    }

    public void post(Object o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者

    public <T> Observable<T> toObserverable(Class<T> eventType) {
        //本质是先filter再cast
        return bus.ofType(eventType);
    }
    /**
     * 提供了一个新的事件,根据code进行分发
     *
     * @param code 事件code
     */
    public void post(int code, Object o) {
        bus.onNext(new Message(code, o));
    }
    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param code      事件code
     * @param eventType 事件类型
     * @param <T> t
     * @return r
     */
    public <T> Observable<T> toObservableType(final int code, final Class<T> eventType) {
        return bus.ofType(Message.class)
                .filter(message -> message.getCode() == code && eventType.isInstance(message.getObject())).map(Message::getObject).cast(eventType);
    }

}
