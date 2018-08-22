package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IServerContext;
import com.ghostmirror.cltsrvrpc.server.IThreadPool;

public class ServerContext implements IServerContext {
    private final IThreadPool pool;

    public ServerContext (IThreadPool pool) {
        this.pool = pool;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void shutdown(int wait) {

    }

    @Override
    public boolean isShutdown() {
        return pool.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return pool.isTerminated();
    }
}
