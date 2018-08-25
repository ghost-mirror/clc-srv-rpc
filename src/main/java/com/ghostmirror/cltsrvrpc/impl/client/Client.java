package com.ghostmirror.cltsrvrpc.impl.client;

import com.ghostmirror.cltsrvrpc.client.ClientException;
import com.ghostmirror.cltsrvrpc.client.ClientStopped;
import com.ghostmirror.cltsrvrpc.client.IClient;
import com.ghostmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;
import com.ghostmirror.cltsrvrpc.impl.common.DataLogger;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Client implements IClient {
//    private static final Logger log = Logger.getLogger(Client.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Client");
    private final ClientMessageTransmitter transmitter;
    private final Thread transmitterThread;
    private final IClientMessageFactory factory = new ClientMessageFactory();
    private final AtomicInteger sessionId   = new AtomicInteger(0);
    private final AtomicInteger WorkCounter = new AtomicInteger(0);
    private volatile boolean Shutdown = false;

    public Client(String host, int port) throws IOException {
        transmitter = new ClientMessageTransmitter(host, port);
        transmitterThread = new Thread(transmitter);
        transmitterThread.start();
        log.info("Client connected to socket.");
    }

    @Override
    public void shutdown() {
        Shutdown = true;
    }

    @Override
    public boolean isShutdown() {
        return Shutdown;
    }

    @Override
    public synchronized boolean isStopped() {
        return WorkCounter.get() == 0 && Shutdown;
    }

    @Override
    public IServerMessage remoteCall(String service, String method) throws ClientException, SocketException, ClientStopped {
        return  remoteCall(service, method, new Object[0]);
    }

    @Override
    public IServerMessage remoteCall(String service, String method, Object[] params) throws ClientException, SocketException, ClientStopped {
        if(Shutdown) {
           throw new ClientStopped();
        }
        try {
            WorkCounter.incrementAndGet();
            return remoteCall0(service, method, params);
        } finally {
            WorkCounter.decrementAndGet();
            if(isStopped()) {
                transmitterThread.interrupt();
                transmitter.close();
            }
        }
    }

    private IServerMessage remoteCall0(String service, String method, Object[] params) throws ClientException, SocketException {
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
