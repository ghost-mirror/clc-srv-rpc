package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IEexecutor;
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
                        log.info("wait fo comand...");
                        this.wait();
                    } catch (InterruptedException e) {

                    }
                }
                if(command != null) {
                    log.info("execute...");
                    pool.execute(command);
                    command = null;
                    log.info("notify...");
                    this.notifyAll();
                }
            }
        }
    }

    @Override
    public void execute(Runnable command) {
        synchronized (this) {
            while (this.command != null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {

                }
            }
            if(this.command == null && canExecute()) {
                this.command = command;
                this.notifyAll();
            }
        }
    }

    @Override
    public void blockedExecute(Runnable command) {
        synchronized (this) {
            log.info("new command");
            while (this.command != null) {
                try {
                    log.info("command wait...");
                    this.wait();
                } catch (InterruptedException e) {

                }
            }
            log.info("command can exec");
            if(this.command == null && canExecute()) {
                this.command = command;
                log.info("command notify");
                this.notifyAll();
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
        log.info("Connection Rejected!");
        ((ClientSession)r).close();
    }
}
