package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IThreadPool;
import com.ghostmirror.cltsrvrpc.util.CapacityQueue;

import java.util.concurrent.*;

public abstract class AThreadPool implements IThreadPool {
    private final ThreadPoolExecutor pool;
    private CapacityQueue queue;

    protected CapacityQueue getQueue() {
        return queue;
    }

    public ThreadPoolExecutor getPool() {
        return pool;
    }

    public AThreadPool (int queueSize, int poolSize, RejectedExecutionHandler handler) {
        queue = new CapacityQueue(queueSize);
        pool  = new CustomThreadPoolExecutor(poolSize, poolSize, 100, TimeUnit.SECONDS, queue,
                new WorkThreadFactory(), handler, this);
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

    protected void execute(Runnable command) {
        pool.execute(command);
    }

    abstract protected void afterExecution(Runnable r, Throwable t);

        private class CustomThreadPoolExecutor extends ThreadPoolExecutor {
        private final AThreadPool aThreadPool;

        public CustomThreadPoolExecutor(int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory,
                                  RejectedExecutionHandler handler, AThreadPool aThreadPool) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
            this.aThreadPool = aThreadPool;
        }

        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            aThreadPool.afterExecution(r, t);
        }
    }
}

class WorkThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        return new WorkThread(r);
    }
}


