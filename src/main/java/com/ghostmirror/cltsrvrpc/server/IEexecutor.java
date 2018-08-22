package com.ghostmirror.cltsrvrpc.server;

public interface IEexecutor {
    void execute(ISession session);
    void blockedExecute(ISession session);
}
