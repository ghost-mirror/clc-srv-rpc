package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IEexecutor;
import com.ghostmirror.cltsrvrpc.server.ISession;
import org.apache.log4j.Logger;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class ServiceSessionPool extends AThreadPool implements IEexecutor {
    private static final Logger log = Logger.getLogger(ServiceSessionPool.class.getCanonicalName());
    private Runnable command;

    public ServiceSessionPool (int queueSize, int poolSize) {
        super(queueSize, poolSize, new ServiceSessionRejected());
        command = null;

    }
    @Override
    public void run() {
        synchronized (this) {
            while(!pool.isShutdown()) {
                while (command == null) {
                    try {
                        log.debug("wait fo comand...");
                        this.wait();
                    } catch (InterruptedException e) {

                    }
                }
                if(command != null) {
                    log.debug("execute...");
                    pool.execute(command);
                    command = null;
                    log.debug("notify...");
                    this.notifyAll();
                }
            }
        }
    }

    @Override
    public void execute(ISession session) {
        while(true) {
            synchronized (this) {
                while (command != null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {

                    }
                }
            }

            synchronized (this) {
                if(command == null) {
                    command = session;
                    this.notifyAll();
                    return;
                }
            }
        }
    }

    @Override
    public void blockedExecute(ISession session) {
        while(true) {
            synchronized (this) {
                log.debug("new command");
                while (command != null) {
                    try {
                        log.debug("command wait...");
                        this.wait();
                    } catch (InterruptedException e) {

                    }
                }
            }

            synchronized (this) {
                log.debug("command can exec");
                if(command == null && canExecute()) {
                    command = session;
                    log.debug("command notify");
                    this.notifyAll();
                    return;
                }
            }
        }
    }

    private boolean canExecute() {
        return pool.getMaximumPoolSize() >  pool.getActiveCount();
    }
}

class ServiceSessionRejected implements RejectedExecutionHandler {
    private static final Logger log = Logger.getLogger(ServiceSessionPool.class.getCanonicalName());

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.error("Connection Rejected!");
        ISession session = (ISession)r;
        session.rejected();
    }
}
