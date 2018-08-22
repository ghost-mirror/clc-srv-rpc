package com.ghostmirror.cltsrvrpc.impl.client;

import com.ghostmirror.cltsrvrpc.client.ClientException;
import com.ghostmirror.cltsrvrpc.client.IClient;
import com.ghostmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghostmirror.cltsrvrpc.common.EServerResult;
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

    public IServerMessage remoteCall(String service, String method) throws ClientException, SocketException {
        return  remoteCall(service, method, new Object[]{});
    }

    public IServerMessage remoteCall(String service, String method, Object[] params) throws ClientException, SocketException {
        int sessionId = this.sessionId.incrementAndGet();
        logger_request(sessionId, service, method, params);
        int id = transmitter.writeMessage(factory.createMessage(sessionId, service, method, params));
        if(id == 0) {
            ClientException.raise("id == 0");
        }
        if(id != sessionId) {
            ClientException.raise("Accepted ID("+id+") != Session ID("+sessionId+") !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        log.debug("Accepted : " + id);

        IServerMessage obj = transmitter.readMessage(sessionId);
        if(obj.getId() != sessionId || obj.getRequest().getId() != sessionId) {
            ClientException.raise("Response ID("+obj.getId()+"/"+obj.getRequest().getId()+") != Session ID("+sessionId+") !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        log.debug("Message Delivered : " + obj.getId());

        ClientException.raiseOnError(obj);
        logger_response(obj);
        return obj;
    }

    private void logger_request (int sessionId, String service, String method, Object[] params) {
        log.info(log_message("request", sessionId, service, method, params));
    }

    private void logger_response (IServerMessage obj) {
        String msg  = log_message("response", obj.getId(), obj.getRequest().getService(), obj.getRequest().getMethod(), obj.getRequest().getParams());

        if(obj.getType() == EServerResult.RESULT) {
            log.info(msg + ".result(" + obj.getObject().getClass().getSimpleName() + ":" + obj.getObject().toString() + ")");
        } else {
            log.info(msg + ".result(void)");
        }
    }

    private String log_message (String type, int sessionId, String service, String method, Object[] params) {
        String par;
        if(params.length == 0) {
            par = "void";
        } else {
            Object o = params[0];
            par = o.getClass().getSimpleName() + ":" + o.toString();
            for (int i=1; i<params.length; i++) {
                o = params[i];
                par +=  "," + o.getClass().getSimpleName() + ":" + o.toString();
            }
        }
        return  type + "(" + sessionId + "): service(" + service + ").method(" + method + ").params(" + par + ")";
    }
}
