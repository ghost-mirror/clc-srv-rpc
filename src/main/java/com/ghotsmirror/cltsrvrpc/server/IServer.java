package com.ghotsmirror.cltsrvrpc.server;

import java.lang.Runnable;

public interface IServer extends Runnable {
    public void start();
    public void stop();
    public void stop(int wait);
    public String status(String command);
    public void setPoolSize(int poolSize, boolean urgent);
    public void loadServices(IServiceContainer container);

    public boolean IsStopped();
}
