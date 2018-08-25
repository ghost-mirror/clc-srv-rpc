package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;

import java.net.SocketException;

public interface IClientMessageTransmitter extends Runnable {
    int writeMessage (IClientMessage obj) throws SocketException;
    IServerMessage readMessage (int sessionId) throws SocketException;
    void close();
}
