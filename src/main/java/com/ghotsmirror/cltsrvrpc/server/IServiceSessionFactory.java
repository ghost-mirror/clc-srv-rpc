package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceSessionFactory {
    public IServiceSession createServiceSession(IServiceContainer serviceContainer);
}
