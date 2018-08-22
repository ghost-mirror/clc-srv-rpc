package com.ghostmirror.cltsrvrpc.impl.client;

import com.ghostmirror.cltsrvrpc.client.IClient;
import com.ghostmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;


public class Client implements IClient {
    private static final Logger log = Logger.getLogger(Client.class.getCanonicalName());
    private final ClientMessageTransmitter transmitter;
    private final Thread tr;
    private final IClientMessageFactory factory = new ClientMessageFactory();
    private AtomicInteger sessionId = new AtomicInteger(0);

    public Client(String host, int port) throws IOException {
        transmitter = new ClientMessageTransmitter(host, port);
        tr = new Thread(transmitter);
        tr.start();
        log.info("Client connected to socket.");
    }

    public void close() {
        transmitter.close();
    }

    public String remoteCall(String service, String method) throws SocketException {
        return  remoteCall(service, method, new Object[]{});
    }

    public String remoteCall(String service, String method, Object[] params) throws SocketException {
        int sessionId = this.sessionId.incrementAndGet();
        int id = transmitter.writeMessage(factory.createMessage(sessionId, service, method, params));
        if(id == 0) {
            log.error("id == 0");
            return null;
        }
        if(id != sessionId) {
            log.error("111 eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            return null;
        }
        log.info("Accepted : " + id);

        IServerMessage obj = transmitter.readMessage(sessionId);
        if(obj.getId() != sessionId) {
            log.error("222 eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
        log.info("Message Delivered : " + obj.getId());
        return obj.getObject().toString();
    }
}
