package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IExecutor;
import com.ghostmirror.cltsrvrpc.server.ISession;
import com.ghostmirror.cltsrvrpc.server.IThreadContext;
import org.apache.log4j.Logger;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceSessionPool extends AThreadPool implements IExecutor {
//    private static final Logger log = Logger.getLogger(ServiceSessionPool.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Server");
    private Runnable command;
    private final Lock commandLock = new ReentrantLock();
    private final Condition setCommand  = commandLock.newCondition();
    private final Condition runCommand  = commandLock.newCondition();
    private int WorkCounter = 0;

    public ServiceSessionPool (int queueSize, int poolSize) {
        super(queueSize, poolSize, new ServiceSessionRejected());
    }

    @Override
    public void shutdown() {
        getPool().shutdownNow();
        System.out.println("Service Session Pool shutdown...");
        commandLock.lock();
        try {
            runCommand.signal();
        } finally {
            commandLock.unlock();
        }
    }

    @Override
    public boolean isShutdown() {
        return getPool().isShutdown();
    }

    @Override
    public boolean isStopped() {
        return isShutdown() && getPool().getActiveCount() == 0;
    }

    @Override
    public void run() {
        while(!isShutdown()  && !Thread.currentThread().isInterrupted()) {
            commandLock.lock();
            try {
                while(command == null) {
                    log.debug("waiting for command...");
                    runCommand.await();
                    if(isShutdown()) {
                        break;
                    }
                }
                if(isShutdown()) {
                    break;
                }
                execute(command);
                log.debug("execute command..." + getQueue().size());
                command = null;
                setCommand.signal();
            } catch (InterruptedException e) {
                if(isShutdown()) {
                    break;
                }
                Thread.currentThread().interrupt();
                System.out.println("ServiceSessionPool InterruptedException!");
            } finally {
                commandLock.unlock();
            }
        }
        System.out.println("ServiceSessionPool closed!");
    }

    public void execute(ISession session) {
        commandLock.lock();
        log.debug("new command");
        try {
            while(command != null) {
                log.debug("!canExecuteWithoutReject " + getQueue().size());
                try {
                    setCommand.await();
                } catch (InterruptedException e) {
                    if(isNeedShutdownReturn()) {
                        return;
                    }
                }
            }
            this.command = session;
            log.debug("command can run");
            runCommand.signal();
        } finally {
            commandLock.unlock();
        }
    }

    private boolean isNeedShutdownReturn () {
        if(!(Thread.currentThread() instanceof IThreadContext)) {
            Thread.currentThread().interrupt();
            return true;
        }
        IThreadContext hc = (IThreadContext)Thread.currentThread();
        hc.shutdown();
        return false;
    }

    public void blockedExecute(ISession session) {
        commandLock.lock();
        log.debug("new command");
        try {
            while(!canExecuteWithoutReject()) {
                log.debug("!canExecuteWithoutReject " + getQueue().size());
                try {
                    setCommand.await();
                } catch (InterruptedException e) {
                    if(isNeedShutdownReturn()) {
                        return;
                    }
                }
            }
            this.command = session;
            log.debug("command can run");
            runCommand.signal();
        } finally {
            commandLock.unlock();
        }
    }

    private boolean canExecuteWithoutReject() {
        return (command == null) && (getQueue().size() <  getQueue().getMaxCapacity());
//        return (command == null);  Test for Rejected
    }

    @Override
    protected void afterExecution(Runnable r, Throwable t) {
        commandLock.lock();
        try {
            setCommand.signal();
        } finally {
            commandLock.unlock();
        }
    }
}

class ServiceSessionRejected implements RejectedExecutionHandler {
//    private static final Logger log = Logger.getLogger(ServiceSessionPool.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Server");

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.debug("Connection Rejected!");
        ISession session = (ISession)r;
        session.rejected();
    }
}
