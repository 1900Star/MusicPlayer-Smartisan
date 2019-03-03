package com.yibao.music.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Luoshipeng
 * @ Name:    ThreadPoolProxyFactory
 * @ Email:   strangermy98@gmail.com
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/3/3/ 18:27
 * @ Des:     TODO
 */
public class ThreadPoolProxyFactory {
    private ThreadPoolExecutor mExecutor;

    public static ThreadPoolProxyFactory newInstance() {

        return new ThreadPoolProxyFactory();
    }

    public void execute(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.execute(task);
    }

    private void initThreadPoolExecutor() {
        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                    long keepAliveTime = 3000;
                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    BlockingQueue workQueue = new LinkedBlockingDeque<>();
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
                    mExecutor = new ThreadPoolExecutor(5, 5, keepAliveTime, unit, workQueue,
                            threadFactory, handler);
                }
            }
        }
    }
}