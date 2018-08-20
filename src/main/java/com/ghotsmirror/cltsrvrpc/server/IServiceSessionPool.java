package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceSessionPool {
    void setPoolSize(int poolSize, boolean urgent);
    IServiceSession getServiceSession();
    boolean isStopped();
    void start();
    void stop();
    void abort();
}
