package com.ghostmirror.cltsrvrpc;

import com.ghostmirror.cltsrvrpc.impl.server.*;
import com.ghostmirror.cltsrvrpc.server.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerManager {
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
        BufferedReader br;

        try {
            factory    = new ServerMessageFactory();
            container  = new ServiceContainer("src/main/resources/service.properties");
            pool       = new ServiceSessionPool(10, 10);
            respondent = new ServerRespondent(factory, container, pool);
            sessionContext   = new SessionContext(respondent);
            serverContext    = new ServerContext(pool);
            server     = new Server(Integer.parseInt(args[0]), 10, 10, serverContext, sessionContext);
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            log.error("Initialization error");
            e.printStackTrace();
            return;
        }
        log.info("Server started");
        Thread tr = new Thread(server);
        Thread pl = new Thread(pool);
        tr.start();
        pl.start();

        while (!server.isTerminated()) {
            try {
                System.out.print("cmd> ");
                String command = br.readLine();
                    if(command.equals("abort")) {
                    server.shutdown(0);
                    break;
                }
                if(command.equals("stop")) {
                    server.shutdown();
                    System.out.println("Wait for clients connections closed...");
                }
            } catch (IOException e) {
                log.error("Readder error");
                log.error(e);
                server.shutdown(0);
                return;
            }
        }
        log.info("Server stopped!");
    }
}
