package com.ghostmirror.cltsrvrpc;

import com.ghostmirror.cltsrvrpc.client.ClientException;
import com.ghostmirror.cltsrvrpc.impl.client.Client;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ClientManager {
//    private static final Logger log = Logger.getLogger(ClientManager.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Client");
    public static void main(String args[]) {
        Client client;
        List<Thread> threads = new ArrayList<Thread>();

        try {
            client = new Client("localhost", 2323);
        } catch (IOException e) {
            log.error("Connection Error.");
            e.printStackTrace();
            return;
        }
        for(int i=0; i<1000; i++) {
            log.info("thread : " + i);
            Thread tr = new Thread(new Caller(client));
            threads.add(tr);
            tr.start();
        }
        for(Thread tr:threads) {
            try {
                tr.join();
            } catch (InterruptedException e) {
            }
        }
    }
}

class Caller implements Runnable {
    private static final Logger log = Logger.getLogger(Caller.class.getCanonicalName());
    private Client c;
    public Caller(Client c) {
        this.c = c;
    }
    public void run() {
        log.info("Caller: run ...");
        while(true) {
            try {
                c.remoteCall("arithmetic", "sum", new Object[]{33, 11});
                c.remoteCall("arithmetic", "mul", new Object[]{5, 10});
                c.remoteCall("sleepy", "sleep", new Object[]{1});
                c.remoteCall("stupid",     "nothing");
                c.remoteCall("sleepy", "currentDate");
            } catch (ClientException e) {
//                log.error(e.getMessage());
            } catch (SocketException e) {
                log.error("Caller: stoped!");
                return;
//            } catch (InterruptedException e) {
//                log.error("Caller: InterruptedException!");
//                return;
            }
        }
    }
}


