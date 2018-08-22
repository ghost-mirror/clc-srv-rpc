package com.ghostmirror.cltsrvrpc.server;

import java.lang.Runnable;

public interface IServer extends Runnable {
    void shutdown();
    void shutdown(int wait);
    boolean isShutdown();
    boolean isTerminated();

    void setQueueSize(int queueSize);
    void setPoolSize (int poolSize);
    void setCoreSize (int corePoolSize);
    void setKeepAlive(long time);
}
