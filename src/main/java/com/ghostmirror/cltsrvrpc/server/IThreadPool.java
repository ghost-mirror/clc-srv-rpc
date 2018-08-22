package com.ghostmirror.cltsrvrpc.server;

public interface IThreadPool extends Runnable, IInterrupted {
    void setQueueSize(int queueSize);
    void setPoolSize (int poolSize);
    void setCoreSize (int corePoolSize);
    void setKeepAlive(long time);
}
