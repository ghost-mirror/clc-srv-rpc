package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IServerMessage;

import java.net.SocketException;

public interface IClient {
    IServerMessage remoteCall(String service, String method, Object[] params) throws SocketException, ClientException, ClientStopped;
    IServerMessage remoteCall(String service, String method) throws SocketException, ClientException, ClientStopped;
    void shutdown();
    boolean isShutdown();
    boolean isStopped();
}
