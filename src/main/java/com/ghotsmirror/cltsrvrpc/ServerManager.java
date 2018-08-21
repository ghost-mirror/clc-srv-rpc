package com.ghotsmirror.cltsrvrpc;

import com.ghotsmirror.cltsrvrpc.impl.*;
import com.ghotsmirror.cltsrvrpc.server.IServer;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerManager {
    private static final Logger log = Logger.getLogger(ServerManager.class.getSimpleName());

    public static void main(String[] args) {
        ISessionContext context;
        IServer server;
        BufferedReader br;

        try {
            context = new SessionContext(new Respondent(new ServerMessageFactory(), new ServiceContainer("src/main/resources/service.properties")));
            server = new Server(Integer.parseInt(args[0]), context);
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            log.error("Initialization error");
            e.printStackTrace();
            return;
        }
        log.info("Server started");
        Thread tr = new Thread(server);
        tr.start();

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
