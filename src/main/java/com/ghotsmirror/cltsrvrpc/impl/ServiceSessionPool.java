package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IServiceSession;
import com.ghotsmirror.cltsrvrpc.server.IServiceSessionPool;

//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServiceSessionPool implements IServiceSessionPool {
//    private ExecutorService pool;
    private volatile boolean isRunned;
    private volatile int queueSize = 0;
    private volatile int queueCount = 0;
    private final Queue<IServiceSession> queue = new ConcurrentLinkedQueue<IServiceSession>();
    private final ServiceSessionFactory factory;
    private int poolSize  = 0;


    public ServiceSessionPool (ServiceSessionFactory factory, int queueSize, int poolSize) {
//        pool = Executors.newFixedThreadPool(10);
        this.factory  = factory;
        setPoolSize(poolSize, true);
        setQueueSize(poolSize, true);
        this.poolSize  = poolSize;
        this.queueSize = queueSize;
        start();
    }

    public void setPoolSize(int poolSize, boolean urgent) {
        this.poolSize = poolSize;
    }

    public void setQueueSize(int queueSize, boolean urgent) {
        this.queueSize = queueSize;
    }

    public IServiceSession getServiceSession() {
        return null;
    }

    public boolean isStopped () {
        return isRunned;
    }

    public void start() {
        isRunned = true;
    }

    public void stop() {
        isRunned = false;
    }

    public void abort() {
        isRunned = false;
    }

    public  IServiceSession getNextSession()  {
        synchronized (this)        {
            if(queueCount < queue.size()) {
                IServiceSession session = queue.poll();
                queue.add(session);
                queueCount++;
                return session;
            }
            if(queueSize < queue.size()) {
                IServiceSession session = factory.createServiceSession(this);
                queue.add(session);
                queueCount++;
                return session;
            }
            return null;
        }
    }



}
