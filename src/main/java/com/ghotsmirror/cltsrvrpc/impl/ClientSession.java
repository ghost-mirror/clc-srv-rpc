package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.*;
import com.ghotsmirror.cltsrvrpc.server.IService;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;
import com.ghotsmirror.cltsrvrpc.server.IServiceSession;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;

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
            serverBusy();
            return;
        }
    }

    @Override
    public void run() {
        while (true) {
            if (context.getSessionPool().isStopped()) {
                serverIsStopped();
                return;
            }
            try {
                Object object = objectInput.readObject();
                if(!(object instanceof IClientMessage)) {
                    writeMessage(new ServerMessage(0, new WrongClientMessage(), false));
                    close();
                    return;
                }
                clientMessage = (IClientMessage) object;
            } catch (IOException e) {
                writeMessage(new ServerMessage(0, e, false));
                close();
                return;
            } catch (ClassNotFoundException e) {
                writeMessage(new ServerMessage(0, new WrongClientMessage(), false));
                close();
                return;
            }

            IService service = context.getContainer().getService(clientMessage.getService());
            if(service == null) {
                writeMessage(new ServerMessage(clientMessage.getId(), new WrongService(), false));
            }
            IServiceResult result =  serviceSession.invoke(service, clientMessage.getMethod(), clientMessage.getParams());
            writeMessage(new ServerMessage(clientMessage.getId(), result.getObject(), result.isVoid()));
        }
    }

    private void writeMessage (IServerMessage message) {
        try {
            objectOutput.writeObject(message);
            socket.close();
        } catch (IOException e) {
        }
    }

    private void serverBusy() {
        writeMessage(new ServerMessage(0, new ServerBusyError(), false));
    }
    private void serverIsStopped() {
        writeMessage(new ServerMessage(0, new ServerStoppedError(), false));
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

}
