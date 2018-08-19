package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceContainer {
    public IService getService(String name);
}
