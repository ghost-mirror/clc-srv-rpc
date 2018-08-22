package com.ghotsmirror.cltsrvrpc.impl.server;

import com.ghotsmirror.cltsrvrpc.server.IResponseHandler;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.SocketException;

class ClientSession implements Runnable {
    private static final Logger log = Logger.getLogger(ClientSession.class.getSimpleName());
    private final Socket socket;
    private final ISessionContext context;
    private final ObjectInputStream objectInput;
    private final ObjectOutputStream objectOutput;
    private final IResponseHandler handler = new ResponseHandler();

    public ClientSession(Socket socket, ISessionContext context) throws IOException {
        this.socket  = socket;
        this.context = context;
        try {
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectInput  = new ObjectInputStream (socket.getInputStream());
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public void run() {
        if (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            log.info("Client connected!");
        }
        while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            Object object;
            try {
                object = readObject();
                writeObject(context.requestId(object));
            } catch (SocketException e) {
                close();
                return;
            } catch (IOException e) {
                log.error("IOException!");
                close();
                return;
            } catch (ClassNotFoundException e) {
                log.error("ClassNotFoundException!");
                writeObject(context.requestException(e));
                continue;
            }
            context.request(object, handler);
        }
        log.info("Client socket closed run!");
    }

    public void close() {
        try {
            objectInput.close();
            if(!socket.isClosed()) {
                objectOutput.close();
                socket.close();
            }
        } catch (IOException e) {
            log.error("IOException: Client socket closed!");
        }
        log.info("Client socket closed!");
    }

    private Object readObject () throws IOException, ClassNotFoundException {
        log.info("waiting object...");
        Object object = objectInput.readObject();
        log.info("object readed");
        return object;
    }

    private void writeObject (Object obj)  {
        synchronized(this) {
            try {
                objectOutput.writeObject(obj);
                objectOutput.flush();
                log.info("result writed");
            } catch (SocketException e) {
                close();
            } catch (IOException e) {
                close();
            }
        }
    }

    class ResponseHandler implements IResponseHandler {
        @Override
        public void response(Object obj) {
            writeObject(obj);
        }
    }

}
