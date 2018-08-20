package com.ghotsmirror.cltsrvrpc.server;

import java.lang.Runnable;

public interface IServer extends Runnable {
    public void shutdown();
    public void shutdown(int wait);
    public boolean isShutdown();
    public boolean isTerminated();

    public void setQueueSize(int queueSize);
    public void setPoolSize (int poolSize);
    public void setCoreSize (int corePoolSize);
    public void setKeepAlive(long time);
}
