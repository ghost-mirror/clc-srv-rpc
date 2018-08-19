package com.ghotsmirror.cltsrvrpc.server;

public interface ISessionContext {
    public IServiceContainer getContainer();
    public IServiceSessionPool getSessionPool();
}
