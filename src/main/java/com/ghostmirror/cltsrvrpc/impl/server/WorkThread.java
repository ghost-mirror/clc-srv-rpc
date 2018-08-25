package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IThreadContext;

public class WorkThread extends Thread implements IThreadContext {
    private volatile boolean shutdownFlag = false;

    public WorkThread(Runnable target) {
        super(target);
    }

    @Override
    public boolean isShutdown() {
        return shutdownFlag;
    }

    @Override
    public void shutdown() {
        shutdownFlag = true;
    }
}
