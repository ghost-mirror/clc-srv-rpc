package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceSessionFactory {
    IServiceSession createServiceSession(IServiceContainer serviceContainer);
}
