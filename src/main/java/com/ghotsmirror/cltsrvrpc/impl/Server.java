package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IServer;
import com.ghotsmirror.cltsrvrpc.server.IServiceContainer;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;

import java.net.ServerSocket;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements IServer {
    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor pool;
    private final CapacityQueue queue;
    private ISessionContext sessionContext;

    public Server (int port, ISessionContext sessionContext) throws Exception {
        this.sessionContext = sessionContext;
        serverSocket = new ServerSocket(port);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        queue = new CapacityQueue(100);
        pool = new ThreadPoolExecutor(3, 10, 10, TimeUnit.SECONDS, queue, threadFactory, new RejectedExecutionHandlerImpl());
        //Callable task;
    }

    public void run() {
        while(true) {
            if (serverSocket.isClosed()) {
                break;
            }
            try {
                pool.execute(new ClientSession(serverSocket.accept(), sessionContext));
            } catch (IOException e) {
                continue;
            }
        }
    }

    class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {


        }

    }

    public void setQueueSize(int queueSize) {
        queue.setMaxCapacity(queueSize);
    }
    public void setPoolSize (int poolSize) {
        pool.setMaximumPoolSize(poolSize);
    }
    public void setCoreSize (int corePoolSize) {
        pool.setCorePoolSize(corePoolSize);

    }
    public void setKeepAlive(long time) {
        pool.setKeepAliveTime(time, TimeUnit.SECONDS);
    }

    public void shutdown() {
        pool.shutdown();
    }
    public void shutdown(int wait) {
        try {
            pool.awaitTermination(wait, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

        }
    }
    public boolean isShutdown() {
        return pool.isShutdown();
    }

    public boolean isTerminated() {
        return pool.isTerminated();
    }


}

    class CapacityQueue extends LinkedBlockingQueue<Runnable> {
        private int maxCapacity;

        public CapacityQueue (int maxCapacity) {
            super();
            this.maxCapacity = maxCapacity;
        }

        @Override
        public synchronized boolean offer(Runnable e) {
            if(size() >= getMaxCapacity()) {
                return false;
            }
            return super.offer(e);
        }

        public synchronized void setMaxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public synchronized int getMaxCapacity() {
           return  maxCapacity;
        }
    }

