package com.ghostmirror.cltsrvrpc.common;

public interface IInterrupted {
    void shutdown();
    boolean isShutdown();
    boolean isStopped();
}
