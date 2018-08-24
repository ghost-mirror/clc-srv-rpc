package com.ghostmirror.cltsrvrpc.server;

import com.ghostmirror.cltsrvrpc.common.IInterrupted;

public interface IThreadPool extends Runnable, IInterrupted {
    void setQueueSize(int queueSize);
    void setPoolSize (int poolSize);
    void setCoreSize (int corePoolSize);
    void setKeepAlive(long time);
}
