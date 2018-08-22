package com.ghotsmirror.cltsrvrpc.client;

import com.ghotsmirror.cltsrvrpc.common.IClientMessage;
import com.ghotsmirror.cltsrvrpc.common.IServerMessage;

import java.net.SocketException;

public interface IClientMessageTransmitter {
    void close();
    int writeMessage (IClientMessage obj) throws SocketException;
    IServerMessage readMessage (int sessionId);
}
