package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IServer;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class Server implements IServer {
    private static final Logger log = Logger.getLogger(Server.class.getSimpleName());
    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor pool;
    private final CapacityQueue queue;
    private ISessionContext sessionContext;

    public Server (int port, ISessionContext sessionContext) throws Exception {
        this.sessionContext = sessionContext;
        serverSocket = new ServerSocket(port);
        queue = new CapacityQueue(1);
        pool = new ThreadPoolExecutor(1, 1, 100, TimeUnit.SECONDS, queue,
                                       Executors.defaultThreadFactory(), new RejectedExecutionHandlerImpl());
        //Callable task;
    }

    public void run() {
        log.info("runned....");
        while(!pool.isShutdown()) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    log.info("Server socket closed");
                    pool.shutdown();
                }
                continue;
            }
            try {
                pool.execute(new ClientSession(socket, sessionContext));
            } catch (IOException e) {
            }
        }
    }

    class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.info("Connection Rejected!");
            ((ClientSession)r).close();
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
        log.info("Shutting down...");
        pool.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    public void shutdown(int wait) {
        log.info("Shutting down...");
        pool.shutdown();
        try {
            pool.awaitTermination(wait, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
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
    private volatile int maxCapacity;

    public CapacityQueue (int maxCapacity) {
        super();
        this.maxCapacity = maxCapacity;
    }

    @Override
    public synchronized boolean offer(Runnable e) {
        if(size() >= maxCapacity) {
            return false;
        }
        return super.offer(e);
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return  maxCapacity;
    }
}

