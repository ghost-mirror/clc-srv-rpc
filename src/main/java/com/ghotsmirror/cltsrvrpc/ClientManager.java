package com.ghotsmirror.cltsrvrpc;

import com.ghotsmirror.cltsrvrpc.impl.client.Client;
import com.ghotsmirror.cltsrvrpc.impl.server.ServiceContainer;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ClientManager {
    public static void main(String args[]) {
        BufferedReader br;
        Client client;
        List<Thread> threads = new ArrayList<Thread>();
//        br = new BufferedReader(new InputStreamReader(System.in));

        try {
            client = new Client("localhost", 2323);
        } catch (IOException e) {
            System.out.println("Connection Error.");
            e.printStackTrace();
            return;
        }
        for(int i=0; i<1000; i++) {
            System.out.println("thread : " + i);
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
    private static final org.apache.log4j.Logger log = Logger.getLogger(ServiceContainer.class.getSimpleName());
    private Client c;
    public Caller(Client c) {
        this.c = c;
    }
    public void run() {
        System.out.println("Caller: run ...");
        while(true) {
            try {
                Thread.sleep(100);
                System.out.println("Sum:" + c.remoteCall("arithmetic", "sum", new Object[]{5, 10}));
                Thread.sleep(100);
                System.out.println("Mul:" + c.remoteCall("arithmetic", "mul", new Object[]{5, 10}));
//                Thread.sleep(100);
//                System.out.println("Sleep:" + c.remoteCall("sleepy", "sleep", new Object[]{1500}));
            } catch (SocketException e) {
                System.out.println("Caller: stoped!");
                return;
            } catch (InterruptedException e) {
                System.out.println("Caller: InterruptedException!");
                return;
            }
        }
    }
}


