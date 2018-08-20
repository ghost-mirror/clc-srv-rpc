package com.ghotsmirror.cltsrvrpc;

import com.ghotsmirror.cltsrvrpc.impl.*;
import com.ghotsmirror.cltsrvrpc.server.IServer;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;

public class ServerManager {
    public static void main(String[] args) {
        ISessionContext context;
        IServer server;
        try {
            context = new SessionContext(new Respondent(new ServerMessageFactory(), new ServiceContainer("src/main/resources/service.properties")));
            server = new Server(Integer.parseInt(args[0]), context);
        } catch (Exception e) {
            System.out.println("Initialization error");
            e.printStackTrace();
            return;
        }
/*
        try {
            server.wait();
        }  catch (InterruptedException e) {
            System.out.println("Stopped");
            e.printStackTrace();
        }
*/      while (true) {
            server.isShutdown();
        }

    }
}
