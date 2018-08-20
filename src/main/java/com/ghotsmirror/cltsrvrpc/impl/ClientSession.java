package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.IClientMessage;
import com.ghotsmirror.cltsrvrpc.core.IServerMessage;
import com.ghotsmirror.cltsrvrpc.core.ServerBusy;
import com.ghotsmirror.cltsrvrpc.server.IService;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;
import com.ghotsmirror.cltsrvrpc.server.IServiceSession;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;

import com.ghotsmirror.cltsrvrpc.core.WrongClientMessage;

import java.io.*;
import java.net.Socket;

class ClientSession implements Runnable {
    private final Socket socket;
    private final ISessionContext context;
    private final IServiceSession serviceSession;
    private final DataInputStream inputStream;
    private final ObjectInputStream objectInput;
    private final DataOutputStream outputStream;
    private final ObjectOutputStream objectOutput;
    private IClientMessage clientMessage;

    public ClientSession(@org.jetbrains.annotations.NotNull Socket socket, ISessionContext context) throws IOException {
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
        serviceSession = context.getSessionPool().getServiceSession();
        if(serviceSession == null) {
            writeMessage(new ServerMessage(0, new ServerBusy(), false));
            close();
            throw new IOException("serverBusy");
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                Object object = objectInput.readObject();
                if(!(object instanceof IClientMessage)) {
                    writeMessage(context.getServerMessageFactory().createMessage(new WrongClientMessage()));
                    continue;
                }
                clientMessage = (IClientMessage) object;
            } catch (IOException e) {
                writeMessage(context.getServerMessageFactory().createMessage(e));
                close();
                return;
            } catch (ClassNotFoundException e) {
                writeMessage(context.getServerMessageFactory().createMessage(new WrongClientMessage()));
            }

            IService service = context.getContainer().getService(clientMessage.getService());
            IServiceResult result =  serviceSession.invoke(clientMessage.getService(), clientMessage.getMethod(), clientMessage.getParams());
            writeMessage(context.getServerMessageFactory().createMessage(clientMessage.getId(), result));
        }
    }

    private void writeMessage (IServerMessage message) {
        try {
            objectOutput.writeObject(message);
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

}
