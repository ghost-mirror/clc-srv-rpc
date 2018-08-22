package com.ghostmirror.cltsrvrpc.server;

public interface IInterrupted {
    void shutdown();
    void shutdown(int wait);
    boolean isShutdown();
    boolean isTerminated();
}
