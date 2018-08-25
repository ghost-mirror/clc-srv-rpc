package com.ghostmirror.cltsrvrpc;

import com.ghostmirror.cltsrvrpc.client.ClientException;
import com.ghostmirror.cltsrvrpc.client.ClientStopped;
import com.ghostmirror.cltsrvrpc.client.IClient;
import com.ghostmirror.cltsrvrpc.common.EServerResult;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;
import com.ghostmirror.cltsrvrpc.impl.client.Client;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

class ClientManager {
//    private static final Logger log = Logger.getLogger(ClientManager.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Client");
    public static void main(String args[]) {
        IClient client;
        List<Thread> threads = new ArrayList<>();

        try {
            client = new Client("localhost", 2323);
            client.start();
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
            Thread.sleep(60000);
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
                client.shutdown();
                Thread.currentThread().interrupt();
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
    private final IClient c;
    public Caller(IClient c) {
        this.c = c;
    }
    public void run() {
        IServerMessage message;

        log.info("Caller: run ...");
        while(!Thread.currentThread().isInterrupted()) {
            try {
                if(!c.isShutdown()) {
// You can check Shutdown state before remoteCall
                    c.remoteCall("arithmetic", "sum", new Object[]{33, 11});
                }

                message = c.remoteCall("arithmetic", "mul", new Object[]{5, 10});
                if(message.getType() == EServerResult.RESULT && ((Integer) message.getObject()).intValue() != 50) {
                    log.error("Result must be 50 but received " + ((Integer) message.getObject()).intValue());
                }
                c.remoteCall("arithmetic", "div", new Object[]{22, 4});
                c.remoteCall("sleepy", "sleep", new Object[]{1});
//                c.remoteCall("sleepy", "sleep", new Object[]{1000});
                message = c.remoteCall("stupid",     "nothing");
                if(message.getType() == EServerResult.RESULT) {
                    log.error("Result must be VOID");
                }
                c.remoteCall("sleepy", "currentDate");

//                c.remoteCall(null, null, null);
//                c.remoteCall(null, "div", new Object[]{33, 11});
//                c.remoteCall("arithmetic", null, new Object[]{33, 11});
//                c.remoteCall("arithmetic", "div", new Object[]{5, 0});
//                c.remoteCall("arithmetic", "div", new Object[]{5, null});
//                c.remoteCall("arithmetic", "sum0", new Object[]{33, 11});

            } catch (ClientException e) {
//                log.error(e.getMessage());
            } catch (ClientStopped e) {
                log.info("Client Stopped!");
                break;
            } catch (SocketException e) {
                log.error("Caller: Socket Exception!");
                break;
//            } catch (InterruptedException e) {
//                log.error("Caller: InterruptedException!");
//                return;
            }
        }
        Thread.currentThread().interrupt();
    }
}


