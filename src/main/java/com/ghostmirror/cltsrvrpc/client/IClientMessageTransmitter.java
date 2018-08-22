package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;

import java.net.SocketException;

public interface IClientMessageTransmitter {
    void close();
    int writeMessage (IClientMessage obj) throws SocketException;
    IServerMessage readMessage (int sessionId);
}
