package com.ghostmirror.cltsrvrpc;

import com.ghostmirror.cltsrvrpc.client.ClientException;
import com.ghostmirror.cltsrvrpc.impl.client.Client;
import com.ghostmirror.cltsrvrpc.impl.server.ServiceContainer;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ClientManager {
    private static final Logger log = Logger.getLogger(ClientManager.class.getCanonicalName());
    public static void main(String args[]) {
        BufferedReader br;
        Client client;
        List<Thread> threads = new ArrayList<Thread>();
//        br = new BufferedReader(new InputStreamReader(System.in));

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

/*
        try {
            String s = br.readLine();
        } catch (IOException e) {
        }
*/
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
                Thread.sleep(100);
                c.remoteCall("arithmetic", "sum", new Object[]{33, 11});
                Thread.sleep(100);
                c.remoteCall("arithmetic", "mul", new Object[]{5, 10});
                Thread.sleep(100);
                c.remoteCall("sleepy", "sleep", new Object[]{1});
                Thread.sleep(100);
                c.remoteCall("stupid",     "nothing");
                Thread.sleep(100);
                c.remoteCall("sleepy", "sleep", new Object[]{1});
            } catch (ClientException e) {
                log.error(e.getMessage());
            } catch (SocketException e) {
                log.error("Caller: stoped!");
                return;
            } catch (InterruptedException e) {
                log.error("Caller: InterruptedException!");
                return;
            }
        }
    }
}


