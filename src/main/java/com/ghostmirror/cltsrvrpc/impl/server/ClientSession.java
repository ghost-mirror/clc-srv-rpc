package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IResponseHandler;
import com.ghostmirror.cltsrvrpc.server.ISessionContext;

import org.apache.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.SocketException;

class ClientSession implements Runnable {
//    private static final Logger log = Logger.getLogger(ClientSession.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Server");
    private final Socket socket;
    private final ISessionContext context;
    private final ObjectInputStream objectInput;
    private final ObjectOutputStream objectOutput;
    private final IResponseHandler handler = new ResponseHandler();
    private volatile int WorkCounter = 0;
    private final Object monitor = new Object();

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

 //   public synchronized boolean isStopped() {
 //       return WorkCounter == 0;
 //   }

    @Override
    public void run() {
        if (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            log.info("Client connected!");
        }
        while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            Object object;

            try {
                object = readObject();
                if(socket.isClosed() || Thread.currentThread().isInterrupted()) {
                    break;
                }
                writeObject0(context.requestId(object));
            } catch (SocketException e) {
                close();
                return;
            } catch (EOFException e) {
                close();
                return;
            } catch (IOException e) {
                log.error("IOException!");
                e.printStackTrace();
                close();
                return;
            } catch (ClassNotFoundException e) {
                log.error("ClassNotFoundException!");
                writeObject0(context.requestException(e));
                e.printStackTrace();
                continue;
            }
            incWorkCounter();
            context.request(object, handler);
        }


        System.out.println("Client session is interrupted");
        synchronized (monitor) {
            while (WorkCounter != 0) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        close();
        System.out.println("Client stopped!");
        log.info("Client stopped!");
    }

    public void close() {
        try {
            objectInput.close();
            if(!socket.isClosed()) {
                socket.close();
                objectOutput.close();
            }
        } catch (IOException e) {
            log.error("IOException: Client socket closed!");
        }
        log.info("Client socket closed!");
    }

    private  void incWorkCounter() {
        synchronized (monitor) {
            WorkCounter++;
        }
    }

    private void decWorkCounter() {
        synchronized (monitor) {
            WorkCounter--;
            monitor.notify();
        }
    }

    private Object readObject () throws IOException, ClassNotFoundException {
        log.debug("waiting object...");
        Object object = objectInput.readObject();
        log.debug("object readed");
        return object;
    }

    private void writeObject (Object obj)  {
        try {
            writeObject0(obj);
        } finally {
            decWorkCounter();
        }
    }

    private synchronized void writeObject0 (Object obj)  {
        try {
            objectOutput.writeObject(obj);
            objectOutput.flush();
            log.debug("result writed");
        } catch (SocketException e) {
            close();
        } catch (IOException e) {
            close();
        }
    }

    class ResponseHandler implements IResponseHandler {
        @Override
        public void response(Object obj) {
            writeObject(obj);
        }
    }

}
