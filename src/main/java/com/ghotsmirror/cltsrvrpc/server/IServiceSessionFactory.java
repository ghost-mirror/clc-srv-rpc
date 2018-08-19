package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceSessionFactory {
    public IServiceSession createServiceSession(IServiceSessionPool pool);
}
