package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IServerMessage;

import java.net.SocketException;

public interface IClient {
    IServerMessage remoteCall(String service, String method, Object[] params) throws SocketException, ClientException;
    IServerMessage remoteCall(String service, String method) throws SocketException, ClientException;
    void close();
}
