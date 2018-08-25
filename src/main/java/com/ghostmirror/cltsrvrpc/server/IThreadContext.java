package com.ghostmirror.cltsrvrpc.server;

public interface IThreadContext {
    boolean isShutdown();
    void shutdown();
}
