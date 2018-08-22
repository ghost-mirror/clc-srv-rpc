package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IEexecutor;
import com.ghostmirror.cltsrvrpc.server.IServerContext;
import com.ghostmirror.cltsrvrpc.server.IThreadPool;
import com.ghostmirror.cltsrvrpc.util.CapacityQueue;
import com.ghostmirror.cltsrvrpc.server.ISessionContext;
import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Server extends ThreadPool implements IEexecutor{
    private static final Logger log = Logger.getLogger(Server.class.getCanonicalName());
    private final ServerSocket serverSocket;
    private ISessionContext sessionContext;
    private IServerContext  serverContext;

    public Server (int port, int queueSize, int poolSize, IServerContext serverContext, ISessionContext sessionContext) throws Exception {
        super(queueSize, poolSize, new ClientSessionRejected());
        this.serverContext  = serverContext;
        this.sessionContext = sessionContext;
        serverSocket = new ServerSocket(port);
    }

    @Override
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
                execute(new ClientSession(socket, sessionContext));
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void execute(Runnable command) {
        pool.execute(command);
    }

    @Override
    public void shutdown() {
        log.info("Shutting down...");
        super.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void shutdown(int wait) {
        log.info("Shutting down...");
        super.shutdown(wait);
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }
}


class ClientSessionRejected implements RejectedExecutionHandler {
    private static final Logger log = Logger.getLogger(Server.class.getCanonicalName());

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.info("Connection Rejected!");
        ((ClientSession)r).close();
    }
}
