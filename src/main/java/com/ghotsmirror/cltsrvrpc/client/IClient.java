package com.ghotsmirror.cltsrvrpc.client;

import java.net.SocketException;

public interface IClient {
    String remoteCall(String service, String method, Object[] params) throws SocketException;
    void close();
}
