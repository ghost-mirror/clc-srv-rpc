package com.ghostmirror.cltsrvrpc.server;

public interface IServiceContainer {
    IService getService(String name);
}
