package com.ghotsmirror.cltsrvrpc.impl.server;

import com.ghotsmirror.cltsrvrpc.util.CapacityQueue;
import com.ghotsmirror.cltsrvrpc.server.IServer;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;
import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Server implements IServer {
    private static final Logger log = Logger.getLogger(Server.class.getSimpleName());
    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor pool;
    private final CapacityQueue queue;
    private ISessionContext sessionContext;

    public Server (int port, ISessionContext sessionContext) throws Exception {
        this.sessionContext = sessionContext;
        serverSocket = new ServerSocket(port);
        queue = new CapacityQueue(3);
        pool = new ThreadPoolExecutor(3, 3, 100, TimeUnit.SECONDS, queue,
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


