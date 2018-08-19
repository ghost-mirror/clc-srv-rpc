package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IServer;
import com.ghotsmirror.cltsrvrpc.server.IServiceContainer;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;


public class Server implements IServer {
    private ServerSocket serverSocket;
    private ISessionContext sessionContext;

    public Server (int port, ISessionContext sessionContext) throws Exception {
        this.sessionContext = sessionContext;
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        sessionContext.getSessionPool().start();
    }
    public String status(String command) {return command;}
    public void stop() {}
    public void stop(int wait){}
    public void setPoolSize(int poolSize, boolean urgent){}
    public void loadServices(IServiceContainer container){}

    public void run() {
        while(true) {
            if (serverSocket.isClosed()) {
                break;
            }
//            if (sessionPool.isStopped()) {
//                continue;
//            }
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                new Thread(new ClientSession(clientSocket, sessionContext)).start();
            } catch (IOException e) {
                continue;
            }
        }
    }

    public boolean IsStopped() {
        return false;
    }
}

