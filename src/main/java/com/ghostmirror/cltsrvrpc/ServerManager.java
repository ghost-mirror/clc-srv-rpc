package com.ghostmirror.cltsrvrpc;

import com.ghostmirror.cltsrvrpc.impl.server.*;
import com.ghostmirror.cltsrvrpc.server.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class ServerManager {
//    private static final Logger log = Logger.getLogger(ServerManager.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Server");

    public static void main(String[] args) {
        IServerMessageFactory factory;
        IServiceContainer     container;
        ServiceSessionPool    pool;
        IRespondent           respondent;
        ISessionContext       sessionContext;
        IServerContext        serverContext;
        IThreadPool           server;
        InputStreamReader     inputStreamReader;
        BufferedReader        bufferedReader;

        try {
            factory           = new ServerMessageFactory();
            container         = new ServiceContainer("src/main/resources/service.properties");
            pool              = new ServiceSessionPool(10, 10);
            respondent        = new ServerRespondent(factory, container, pool);
            sessionContext    = new SessionContext(respondent);
            serverContext     = new ServerContext(pool);
            server            = new Server(Integer.parseInt(args[0]), 10, 10, serverContext, sessionContext);
            inputStreamReader = new InputStreamReader(System.in);
            bufferedReader    = new BufferedReader(inputStreamReader);
        } catch (Exception e) {
            log.error("Initialization error");
            e.printStackTrace();
            return;
        }

        Thread poolThread   = new Thread(pool);
        Thread serverThread = new Thread(server);
        poolThread.start();
        serverThread.start();
        log.info("Server started");

        while (!server.isShutdown() && !pool.isShutdown()) {
            try {
                System.out.print("cmd> ");
                String command = bufferedReader.readLine();
                if (command == null) {
                    continue;
                }
                if(command.equals("stop")) {
                    System.out.println("Waiting for clients to disconnect...");
                    break;
                }
            } catch (IOException e) {
                log.error("Reader error");
                log.error(e);
                break;
            }
        }

        server.shutdown();
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            serverThread.interrupt();
        }

        pool.shutdown();
        try {
            poolThread.join();
        } catch (InterruptedException e) {
            poolThread.interrupt();
        }

        System.out.println("Server stopped!");
        log.info("Server stopped!");
    }
}
