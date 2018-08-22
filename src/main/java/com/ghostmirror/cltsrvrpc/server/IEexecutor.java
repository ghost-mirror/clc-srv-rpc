package com.ghostmirror.cltsrvrpc.server;

public interface IEexecutor {
    void execute(Runnable command);
    void blockedExecute(Runnable command);
}
