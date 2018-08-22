package com.ghotsmirror.cltsrvrpc.impl.client;

import com.ghotsmirror.cltsrvrpc.client.IClient;
import com.ghotsmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghotsmirror.cltsrvrpc.common.EServerResult;
import com.ghotsmirror.cltsrvrpc.common.IClientMessage;
import com.ghotsmirror.cltsrvrpc.common.IServerMessage;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;


public class Client implements IClient {
    private final ClientMessageTransmitter transmitter;
    private final Thread tr;
    private final IClientMessageFactory factory = new ClientMessageFactory();
    private AtomicInteger sessionId = new AtomicInteger(0);

    public Client(String host, int port) throws IOException {
        transmitter = new ClientMessageTransmitter(host, port);
        tr = new Thread(transmitter);
        tr.start();
        System.out.println("Client connected to socket.");
    }

    public void close() {
        transmitter.close();
    }

    public String remoteCall(String service, String method) throws SocketException {
        return  remoteCall(service, method, new Object[]{});
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
