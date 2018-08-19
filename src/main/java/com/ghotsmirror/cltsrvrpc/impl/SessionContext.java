package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IServiceContainer;
import com.ghotsmirror.cltsrvrpc.server.IServiceSessionPool;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;

public class SessionContext implements ISessionContext {
    private IServiceContainer   container;
    private IServiceSessionPool sessionPool;

    public SessionContext (int poolSize, int queueSize, IServiceContainer container) throws Exception {
        this.container = container;
        sessionPool = new ServiceSessionPool(new ServiceSessionFactory(), poolSize, queueSize);
        sessionPool.setPoolSize(poolSize, true);
    }

    public IServiceContainer getContainer() {
        return container;
    }

    public IServiceSessionPool getSessionPool() {
        return sessionPool;
    }
}
