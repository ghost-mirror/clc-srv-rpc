package com.ghostmirror.cltsrvrpc;

import com.ghostmirror.cltsrvrpc.client.ClientException;
import com.ghostmirror.cltsrvrpc.client.ClientStopped;
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
            return;
        }
        for(int i=0; i<10; i++) {
            log.info("thread : " + i);
            Thread tr = new Thread(new Caller(client));
            threads.add(tr);
            tr.start();
        }

//  Shutdown test
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            client.shutdown();
        }


        for(Thread tr:threads) {
            try {
                tr.join();
            } catch (InterruptedException e) {
                tr.interrupt();
            }
        }
        client.shutdown();


        if(client.isStopped()) {
            log.info("Client Manager Stopped!");
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
        while(!Thread.currentThread().isInterrupted()) {
            try {
                if(!c.isShutdown()) {
// You can check Shutdown state before remoteCall
                    c.remoteCall("arithmetic", "sum", new Object[]{33, 11});
                }

                c.remoteCall("arithmetic", "mul", new Object[]{5, 10});
                c.remoteCall("arithmetic", "div", new Object[]{22, 4});
                c.remoteCall("sleepy", "sleep", new Object[]{1});
//                c.remoteCall("sleepy", "sleep", new Object[]{1000});
                c.remoteCall("stupid",     "nothing");
                c.remoteCall("sleepy", "currentDate");

//                c.remoteCall(null, null, null);
//                c.remoteCall(null, "div", new Object[]{33, 11});
//                c.remoteCall("arithmetic", null, new Object[]{33, 11});
//                c.remoteCall("arithmetic", "div", new Object[]{5, 0});
//                c.remoteCall("arithmetic", "div", new Object[]{5, null});
//                c.remoteCall("arithmetic", "summ", new Object[]{33, 11});

            } catch (ClientException e) {
//                log.error(e.getMessage());
            } catch (ClientStopped e) {
//                log.info("Client Stopped!");
                return;
            } catch (SocketException e) {
                log.error("Caller: Socket Exception!");
                return;
//            } catch (InterruptedException e) {
//                log.error("Caller: InterruptedException!");
//                return;
            }
        }
    }
}


