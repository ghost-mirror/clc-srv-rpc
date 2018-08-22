package com.ghostmirror.cltsrvrpc.impl.client;

import com.ghostmirror.cltsrvrpc.client.IClientMessageTransmitter;
import com.ghostmirror.cltsrvrpc.common.EServerResult;
import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientMessageTransmitter implements Runnable, IClientMessageTransmitter {
    private static final Logger log = Logger.getLogger(Client.class.getCanonicalName());
    private final Socket socket;
    private final ObjectInputStream objectInput;
    private final ObjectOutputStream objectOutput;
    private static final Object monitor = new Object();
    private IServerMessage msg;
    private IServerMessage id;

    public ClientMessageTransmitter(String host, int port) throws IOException {
        socket = new Socket("localhost", port);
        objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectInput  = new ObjectInputStream (socket.getInputStream());
        log.info("Client connected to socket.");
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
            log.error("Close socked error!");
            e.printStackTrace();
            return;
        }
        log.info("Socked closed!");
    }

    public int writeMessage (IClientMessage obj) throws SocketException {
        synchronized (this) {
            if(socket.isClosed()) {
                throw new SocketException("Socket is closed");
            }
            try {
                log.info("Request : " + obj.getId());
                objectOutput.writeObject(obj);
                objectOutput.flush();
                log.info("Request send");
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
    }

    public IServerMessage readMessage (int sessionId) {
        synchronized (monitor) {
            while (!isReadyObjectId(sessionId)) {
                if(socket.isClosed()) {
                    return null;
                }
                try {
                    log.info("client waiting object...");
                    monitor.wait();
                    log.info("client free object");
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
                    log.info("client waiting id...");
                    monitor.wait();
                    log.info("client free  id");
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
                log.info("waiting for object...");
                object = objectInput.readObject();
                if(object == null) {
                    log.info("new object : null");
                    return;
                }
            } catch (IOException e) {
                log.error("IOException");
                return;
            } catch (ClassNotFoundException e) {
                log.error("ClassNotFoundException");
                return;
            }
            if(!(object instanceof IServerMessage)){
                log.error("new object : null");
                return;
            }
            IServerMessage message = (IServerMessage)object;
            if(message.getType() == EServerResult.ID) {
                id = message;
                monitor.notifyAll();
                log.info("ID delivered");
                monitor.notifyAll();
                while (id != null) {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        log.info("reader waiting(ID)...");
                        monitor.wait();
                        log.info("reader free(ID)");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                msg = message;
                log.info("Object delivered");
                monitor.notifyAll();
                while (msg != null) {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        log.info("reader waiting(msg)...");
                        monitor.wait();
                        log.info("reader free(msg)");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
