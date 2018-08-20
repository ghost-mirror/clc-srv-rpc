package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IServerMessageFactory;
import com.ghotsmirror.cltsrvrpc.server.IServiceContainer;
import com.ghotsmirror.cltsrvrpc.server.IServiceSessionPool;
import com.ghotsmirror.cltsrvrpc.server.ISessionContext;

public class SessionContext implements ISessionContext {
    private final IServiceContainer   container;
    private final IServiceSessionPool sessionPool;
    private final IServerMessageFactory messageFactory;

    public SessionContext (int poolSize, int queueSize, IServiceContainer container) throws Exception {
        this.container = container;
        sessionPool = new ServiceSessionPool(new ServiceSessionFactory(), poolSize, queueSize);
        sessionPool.setPoolSize(poolSize, true);
        messageFactory = new ServerMessageFactory();
    }

    public IServiceContainer getContainer() {
        return container;
    }

    public IServiceSessionPool getSessionPool() {
        return sessionPool;
    }

    public IServerMessageFactory getServerMessageFactory() {
        return messageFactory;
    }

}
