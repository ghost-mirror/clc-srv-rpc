package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IServerContext;
import com.ghostmirror.cltsrvrpc.server.ISessionContext;
import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.TimeUnit;


public class Server extends AThreadPool {
//    private static final Logger log = Logger.getLogger(Server.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Server");
    private final ServerSocket serverSocket;
    private final ISessionContext sessionContext;
//    private IServerContext  serverContext;
    private volatile boolean IsShutdown = false;


    public Server (int port, int queueSize, int poolSize, IServerContext serverContext, ISessionContext sessionContext)
            throws Exception {
        super(queueSize, poolSize, (r,t) -> {log.info("Connection Rejected!"); ((ClientSession)r).close();});
//        this.serverContext  = serverContext;
        this.sessionContext = sessionContext;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        log.info("started....");
        while(!IsShutdown && !Thread.currentThread().isInterrupted()) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                log.info("Server socket closed");
                break;
            }
            ClientSession session;
            try {
                session =  new ClientSession(socket, sessionContext);
            } catch (IOException e) {
                continue;
            }
            execute(session);
        }

        try {
            getPool().awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            IsShutdown = true;
        }
        System.out.println("Server Stopped");
    }

    private void close() {
       try {
            if(!serverSocket.isClosed()) {
                serverSocket.close();
            }
       } catch (IOException e) {
           log.error("Server socket closing IOException");
       }
    }

    @Override
    public void shutdown() {
        log.info("Server shutting down...");
        close();
        getPool().shutdownNow();
        IsShutdown = true;
    }

    @Override
    public boolean isShutdown() {
        return IsShutdown;
    }

    @Override
    public boolean isStopped() {
        return IsShutdown && getPool().getActiveCount() == 0;
    }
}
