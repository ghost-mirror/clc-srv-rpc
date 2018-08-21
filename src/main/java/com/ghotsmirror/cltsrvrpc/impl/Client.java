package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.client.IClient;
import com.ghotsmirror.cltsrvrpc.core.IClientMessage;
import com.ghotsmirror.cltsrvrpc.core.IServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;


public class Client implements IClient {
    private final Transmitter transmitter;
    private final Thread tr;
    private AtomicInteger sessionId = new AtomicInteger(0);

    public Client(String host, int port) throws IOException {
        transmitter = new Transmitter(host, port);
        tr = new Thread(transmitter);
        tr.start();
        System.out.println("Client connected to socket.");
    }

    public void close() {
        transmitter.close();
    }

    public String remoteCall(String service, String method, Object[] params) throws SocketException {
        int sessionId = this.sessionId.incrementAndGet();
        System.out.println("Request : " + sessionId);
        int id = transmitter.writeObject(new ClientMessage(sessionId, service, method, params));
        if(id == 0) {
            System.out.println("id == 0");
            return null;
        }
        if(id != sessionId) {
            System.out.println("111 eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            return null;
        }
        System.out.println("Accepted : " + id);

        IServerMessage obj = transmitter.readObject(sessionId);
        if(obj.getId() != sessionId) {
            System.out.println("222 eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
        System.out.print("ID : ");
        System.out.println(obj.getId());
        return obj.getObject().toString();
    }
}


class Transmitter implements Runnable {
    private final Socket socket;
    private final ObjectInputStream objectInput;
    private final ObjectOutputStream objectOutput;
    private static final Object monitor = new Object();
    private IServerMessage msg;

    public Transmitter(String host, int port) throws IOException {
        socket = new Socket("localhost", port);
        objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectInput  = new ObjectInputStream (socket.getInputStream());
        System.out.println("Client connected to socket.");
    }

    public void close() {
        try {
            objectInput.close();
            if(!socket.isClosed()) {
                objectOutput.close();
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Close socked error!");
            e.printStackTrace();
            return;
        }
        System.out.println("Socked closed!");
    }

    public synchronized int writeObject (IClientMessage obj) throws SocketException  {
        if(socket.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        try {
            objectOutput.writeObject(obj);
            objectOutput.flush();
            Object object = objectInput.readObject();
            if(!(object instanceof IServerMessage)){
                return 0;
            }
            return ((IServerMessage)object).getId();
        } catch (IOException e) {
            close();
            return 0;
        } catch (ClassNotFoundException e) {
            return 0;
        }
    }

    public IServerMessage readObject (int sessionId) {
        synchronized (monitor) {
            while (!isReadyObjectId(sessionId)) {
                if(socket.isClosed()) {
                    return null;
                }
                try {
                    System.out.println("client waiting...");
                    monitor.wait();
                    System.out.println("client free");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return getObject();
    }

    private boolean isReadyObjectId(int sessionId) {
        if(msg == null) {
            return false;
        }
        return (msg.getId() == sessionId);
    }

    private IServerMessage getObject() {
        synchronized (monitor) {
            IServerMessage cur_msg = msg;
            msg = null;
            monitor.notifyAll();
            return cur_msg;
        }
    }

    private void readData() {
        synchronized (monitor) {
            Object object;
            try {
                object = objectInput.readObject();
            } catch (IOException e) {
                return;
            } catch (ClassNotFoundException e) {
                return;
            }
            if(!(object instanceof IServerMessage)){
                return;
            }
            msg = (IServerMessage)object;
            System.out.println("Oblect readed");
            monitor.notifyAll();
            while (msg != null) {
                if(socket.isClosed()) {
                    return;
                }
                try {
                    System.out.println("reader waiting...");
                    monitor.wait();
                    System.out.println("reader free");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            readData();
        }
    }
}