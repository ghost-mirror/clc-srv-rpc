package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IThreadPool;
import com.ghostmirror.cltsrvrpc.util.CapacityQueue;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThreadPool implements IThreadPool {
    protected final ThreadPoolExecutor pool;
    protected final CapacityQueue queue;

    public ThreadPool (int queueSize, int poolSize, RejectedExecutionHandler handler) {
        queue = new CapacityQueue(queueSize);
        pool = new ThreadPoolExecutor(poolSize, poolSize, 100, TimeUnit.SECONDS, queue,
                Executors.defaultThreadFactory(), handler);
    }

    @Override
    public void setQueueSize(int queueSize) {
        queue.setMaxCapacity(queueSize);
    }

    @Override
    public void setPoolSize (int poolSize) {
        pool.setMaximumPoolSize(poolSize);
    }

    @Override
    public void setCoreSize (int corePoolSize) {
        pool.setCorePoolSize(corePoolSize);

    }

    @Override
    public void setKeepAlive(long time) {
        pool.setKeepAliveTime(time, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        pool.shutdown();
    }

    @Override
    public void shutdown(int wait) {
        pool.shutdown();
        try {
            pool.awaitTermination(wait, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public boolean isShutdown() {
        return pool.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return pool.isTerminated();
    }
}


