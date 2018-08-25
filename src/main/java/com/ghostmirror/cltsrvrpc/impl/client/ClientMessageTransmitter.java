package com.ghostmirror.cltsrvrpc.impl.client;

import com.ghostmirror.cltsrvrpc.client.IClientMessageTransmitter;
import com.ghostmirror.cltsrvrpc.common.EServerResult;
import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientMessageTransmitter implements Runnable, IClientMessageTransmitter {
//    private static final Logger log = Logger.getLogger(Client.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Client");
    private final Object monitor = new Object();
    private final Socket socket;
    private final ObjectInputStream objectInput;
    private final ObjectOutputStream objectOutput;
    private boolean isClosed = false;
    private IServerMessage msg;
    private IServerMessage id;

    public ClientMessageTransmitter(String host, int port) throws IOException {
        socket = new Socket(host, port);
        objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectInput  = new ObjectInputStream (socket.getInputStream());
        log.info("Client connected to socket.");
    }

    @Override
    public void run() {
        while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            try {
                readData();
            } catch (SocketException e) {
                return;
            }
        }
    }

    public synchronized int writeMessage (IClientMessage obj) throws SocketException {
        if(socket.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        try {
            log.debug("Request : " + obj.getId());
            objectOutput.writeObject(obj);
            objectOutput.flush();
            log.debug("Request send");
            IServerMessage message = readId();
            if(message == null){
                return 0;
            }
            return message.getId();
        } catch (SocketException e) {
            close();
            throw e;
        } catch (IOException e) {
            close();
            return 0;
        }
    }

    public IServerMessage readMessage (int sessionId) throws SocketException  {
        if(socket.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        synchronized (monitor) {
            while (!isReadyObjectId(sessionId)) {
                if(socket.isClosed()) {
                    throw new SocketException("Socket is closed");
                }
                try {
                    log.debug("client waiting object...");
                    monitor.wait();
                    log.debug("client free object");
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }
        }

        return getObject();
    }

    public void close() {
        synchronized (monitor) {
            monitor.notifyAll();
        }
        try {
            objectInput.close();
            if(!socket.isClosed()) {
                objectOutput.close();
                socket.close();
            }
        } catch (IOException e) {
            log.error("Close socked error!");
//            e.printStackTrace();
            return;
        } finally {
            isClosed = true;
        }
        log.info("Socked closed!");
    }

    private IServerMessage readId () {
        synchronized (monitor) {
            while (id == null) {
                if(socket.isClosed()) {
                    return null;
                }
                try {
                    log.debug("client waiting id...");
                    monitor.wait();
                    log.debug("client free  id");
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

    private void readData() throws SocketException {
        synchronized (monitor) {
            Object object;
            try {
                log.debug("waiting for object...");
                object = objectInput.readObject();
                if(object == null) {
                    log.debug("new object : null");
                    return;
                }
            } catch (InvalidClassException e) {
                log.error("InvalidClassException");
                return;
            } catch (ClassNotFoundException e) {
                log.error("ClassNotFoundException");
                return;
            } catch (SocketException e) {
                if(!isClosed) {
                    log.error("SocketException");
                    close();
                }
                throw e;
            } catch (IOException e) {
                log.error("IOException");
                SocketException exception = new SocketException();
                exception.initCause(e.getCause());
                close();
                throw exception;
            }
            if(!(object instanceof IServerMessage)){
                log.error("object not instanceof IServerMessage");
                return;
            }
            IServerMessage message = (IServerMessage)object;
            if(message.getType() == EServerResult.ID) {
                id = message;
                log.debug("ID delivered");
                monitor.notifyAll();
                do {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        log.debug("reader waiting(ID)...");
                        monitor.wait();
                        log.debug("reader free(ID)");
                    } catch (InterruptedException e) {
                        return;
                    }
                } while (id != null);
            } else {
                msg = message;
                log.debug("Object delivered");
                monitor.notifyAll();
                do {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        log.debug("reader waiting(msg)...");
                        monitor.wait();
                        log.debug("reader free(msg)");
                    } catch (InterruptedException e) {
                        return;
                    }
                } while (msg != null);
            }
        }
    }
}
