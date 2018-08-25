package com.ghostmirror.cltsrvrpc.server;

public interface IExecutor {
    void execute(ISession session);
    void blockedExecute(ISession session);
}
