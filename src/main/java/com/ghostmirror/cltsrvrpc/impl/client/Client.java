package com.ghostmirror.cltsrvrpc.impl.client;

import com.ghostmirror.cltsrvrpc.client.ClientException;
import com.ghostmirror.cltsrvrpc.client.IClient;
import com.ghostmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghostmirror.cltsrvrpc.common.EServerResult;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;
import com.ghostmirror.cltsrvrpc.impl.common.DataLogger;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;


public class Client implements IClient {
//    private static final Logger log = Logger.getLogger(Client.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Client");
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

    public IServerMessage remoteCall(String service, String method) throws ClientException, SocketException {
        return  remoteCall(service, method, new Object[0]);
    }

    public IServerMessage remoteCall(String service, String method, Object[] params) throws ClientException, SocketException {
        int sessionId = this.sessionId.incrementAndGet();
        int id = transmitter.writeMessage(factory.createMessage(sessionId, service, method, params));
        if(id == 0) {
            ClientException.raise("id == 0");
        }
        if(id != sessionId) {
            ClientException.raise("Accepted ID("+id+") != Session ID("+sessionId+") !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        log.debug("Accepted : " + id);

        IServerMessage obj = transmitter.readMessage(sessionId);
        if(obj == null) {
//            transmitter.close();
            log.error("obj == null");
            throw new SocketException("obj == null");
        }
        if(obj.getId() != sessionId || obj.getRequest().getId() != sessionId) {
            ClientException.raise("Response ID("+obj.getId()+"/"+obj.getRequest().getId()+") != Session ID("+sessionId+") !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        log.debug("Message Delivered : " + obj.getId());

        try {
            ClientException.raiseOnError(obj);
        } catch (ClientException e) {
            log.error(DataLogger.server_response(obj));
            throw e;
        }
        log.info(DataLogger.server_response(obj));
        return obj;
    }

}
