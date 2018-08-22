package com.ghostmirror.cltsrvrpc.server;

public interface IServiceSessionFactory {
    IServiceSession createServiceSession(IServiceContainer serviceContainer);
}
