package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IInterrupted;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;

import java.net.SocketException;

public interface IClient extends IInterrupted {
    void start();
    IServerMessage remoteCall(String service, String method, Object[] params) throws SocketException, ClientException, ClientStopped;
    IServerMessage remoteCall(String service, String method) throws SocketException, ClientException, ClientStopped;
}
