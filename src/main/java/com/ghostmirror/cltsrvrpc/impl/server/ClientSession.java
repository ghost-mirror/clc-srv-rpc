package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IResponseHandler;
import com.ghostmirror.cltsrvrpc.server.ISessionContext;

import com.ghostmirror.cltsrvrpc.server.IThreadContext;
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
        if (isShutdown()) {
            return;
        }
        log.info("Client connected!");

        while (!isTerminate()) {
            Object object;

            try {
                object = readObject();
                if(socket.isClosed() || Thread.currentThread().isInterrupted()) {
                    break;
                }
                writeObject0(context.requestId(object));
            } catch (SocketException|EOFException e) {
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

        System.out.println("Client session is interrupted, WorkCounter = " + WorkCounter);
        synchronized (monitor) {
            while (WorkCounter != 0) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    if(Thread.currentThread() instanceof IThreadContext) {
                        ((IThreadContext)Thread.currentThread()).shutdown();
                    } else {
                        break;
                    }
                }
            }
        }
        close();
        System.out.println("Client Session stopped!");
        log.info("Client Session stopped!");
    }

    private boolean isShutdown () {
        if(socket.isClosed()) return true;
        if(!(Thread.currentThread() instanceof IThreadContext)) {
            return  Thread.currentThread().isInterrupted();
        }
        return ((IThreadContext)Thread.currentThread()).isShutdown() || Thread.currentThread().isInterrupted();
    }

    private boolean isTerminate () {
        if(socket.isClosed()) return true;
        if(!(Thread.currentThread() instanceof IThreadContext)) {
            return  Thread.currentThread().isInterrupted();
        }
        return ((IThreadContext)Thread.currentThread()).isShutdown();
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
//        objectInput.available();
        log.debug("object read");
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
            log.debug("result written");
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
