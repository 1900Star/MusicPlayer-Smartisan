package com.yibao.music.util;


import com.yibao.music.model.Message;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


/**
 * Des：${TODO}
 * Time:2017/4/29 10:35
 *
 * @author Luoshipeng
 */
public class RxBus {


    private final PublishSubject<Object> bus;
    private static volatile RxBus instance;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者

    private RxBus() {
        bus = PublishSubject.create();
    }

    public static RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public void post(Object o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者

    public <T> Observable<T> toObserverable(Class<T> eventType) {
        //本质是先filter再cast
        return bus.ofType(eventType);
    }

    public void post(int msgKey, Object o) {
        bus.onNext(new Message(msgKey, o));
    }

    public <T> Observable<T> toObservableType(final int msgKey, final Class<T> eventType) {
        return bus.ofType(Message.class)
                .filter(msg -> msg.getMsgKey() != null && msg.getCode() == (msgKey) && eventType.isInstance(msg.getObject())).map(Message::getObject).cast(eventType);
    }

    /**
     * 提供了一个新的事件,根据msgKey进行分发
     *
     * @param msgKey 事件key
     */
    public void post(String msgKey, Object o) {
        bus.onNext(new Message(msgKey, o));
    }


    /**
     * 根据传递的magKey和 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param msgKey    事件flag
     * @param eventType 事件类型
     * @param <T>       t
     * @return r
     */

    public <T> Observable<T> toObservableType(final String msgKey, final Class<T> eventType) {
        return bus.ofType(Message.class)
                .filter(msg -> msg.getMsgKey() != null && msg.getMsgKey().equals(msgKey) && eventType.isInstance(msg.getObject())).map(Message::getObject).cast(eventType);
    }


}
