package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceContainer {
    IService getService(String name);
}
