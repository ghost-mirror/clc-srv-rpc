package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceSessionPool {
    public void setPoolSize(int poolSize, boolean urgent);
    public IServiceSession getServiceSession();
    public boolean isStopped();
    public void start();
    public void stop();
    public void abort();
}
