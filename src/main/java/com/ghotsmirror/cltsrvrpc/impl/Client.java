package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.client.IClient;
import com.ghotsmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghotsmirror.cltsrvrpc.core.EServerResult;
import com.ghotsmirror.cltsrvrpc.core.IClientMessage;
import com.ghotsmirror.cltsrvrpc.core.IServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;


public class Client implements IClient {
    private final MessageTransmitter transmitter;
    private final Thread tr;
    private final IClientMessageFactory factory = new ClientMessageFactory();
    private AtomicInteger sessionId = new AtomicInteger(0);

    public Client(String host, int port) throws IOException {
        transmitter = new MessageTransmitter(host, port);
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
        int id = transmitter.writeMessage(factory.createMessage(sessionId, service, method, params));
        if(id == 0) {
            System.out.println("id == 0");
            return null;
        }
        if(id != sessionId) {
            System.out.println("111 eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            return null;
        }
        System.out.println("Accepted : " + id);

        IServerMessage obj = transmitter.readMessage(sessionId);
        if(obj.getId() != sessionId) {
            System.out.println("222 eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
        System.out.println("Message Delivered : " + obj.getId());
        return obj.getObject().toString();
    }
}


class MessageTransmitter implements Runnable {
    private final Socket socket;
    private final ObjectInputStream objectInput;
    private final ObjectOutputStream objectOutput;
    private static final Object monitor = new Object();
    private IServerMessage msg;
    private IServerMessage id;

    public MessageTransmitter(String host, int port) throws IOException {
        socket = new Socket("localhost", port);
        objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectInput  = new ObjectInputStream (socket.getInputStream());
        System.out.println("Client connected to socket.");
    }

    @Override
    public void run() {
        while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            readData();
        }
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

    public synchronized int writeMessage (IClientMessage obj) throws SocketException  {
        if(socket.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        try {
            objectOutput.writeObject(obj);
            objectOutput.flush();
            IServerMessage message = readId();
            if(message == null){
                return 0;
            }
            return message.getId();
        } catch (IOException e) {
            close();
            return 0;
        }
    }

    public IServerMessage readMessage (int sessionId) {
        synchronized (monitor) {
            while (!isReadyObjectId(sessionId)) {
                if(socket.isClosed()) {
                    return null;
                }
                try {
                    System.out.println("client waiting object...");
                    monitor.wait();
                    System.out.println("client free object");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return getObject();
    }

    private IServerMessage readId () {
        synchronized (monitor) {
            while (id == null) {
                if(socket.isClosed()) {
                    return null;
                }
                try {
                    System.out.println("client waiting id...");
                    monitor.wait();
                    System.out.println("client free  id");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            IServerMessage cur_id = id;
            id = null;
            monitor.notifyAll();
            return cur_id;
        }
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
                System.out.println("waiting for object...");
                object = objectInput.readObject();
                if(object == null) {
                    System.out.println("new object : null");
                    return;
                }
            } catch (IOException e) {
                System.out.println("IOException");
                return;
            } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFoundException");
                return;
            }
            if(!(object instanceof IServerMessage)){
                System.out.println("new object : null");
                return;
            }
            IServerMessage message = (IServerMessage)object;
            if(message.getType() == EServerResult.ID) {
                id = message;
                monitor.notifyAll();
                System.out.println("ID delivered");
                monitor.notifyAll();
                while (id != null) {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        System.out.println("reader waiting(ID)...");
                        monitor.wait();
                        System.out.println("reader free(ID)");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                msg = message;
                System.out.println("Object delivered");
                monitor.notifyAll();
                while (msg != null) {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        System.out.println("reader waiting(msg)...");
                        monitor.wait();
                        System.out.println("reader free(msg)");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}