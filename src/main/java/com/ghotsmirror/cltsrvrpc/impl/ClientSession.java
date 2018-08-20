package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.*;

import java.io.*;
import java.net.Socket;

class ClientSession implements Runnable {
    private final Socket socket;
    private final ISessionContext context;
    private final DataInputStream inputStream;
    private final ObjectInputStream objectInput;
    private final DataOutputStream outputStream;
    private final ObjectOutputStream objectOutput;
    private final IResponseHandler handler = new ResponseHandler();

    public ClientSession(Socket socket, ISessionContext context) throws IOException {
        this.socket  = socket;
        this.context = context;
        try {
            inputStream  = new DataInputStream   (socket.getInputStream());
            objectInput  = new ObjectInputStream (inputStream);
            outputStream = new DataOutputStream  (socket.getOutputStream());
            objectOutput = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            Object object;
            try {
                object = objectInput.readObject();
            } catch (IOException e) {
                writeObject(context.request(e));
                close();
                return;
            } catch (ClassNotFoundException e) {
                writeObject(context.request(e));
                continue;
            }
            context.request(object, handler);
        }
    }

    private void writeObject (Object obj) {
        try {
            objectOutput.writeObject(obj);
            objectOutput.flush();
        } catch (IOException e) {
            close();
        }
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    class ResponseHandler implements IResponseHandler {

        @Override
        public void response(Object obj) {
            writeObject(obj);
        }
    }

}
