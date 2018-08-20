package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.IServerMessage;
import com.ghotsmirror.cltsrvrpc.server.IServerMessageFactory;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;

public class ServerMessageFactory implements IServerMessageFactory {
    @Override
    public IServerMessage createMessage(int id, IServiceResult result) {
        return new ServerMessage(id, result.getObject(), result.isVoid());
    }

    @Override
    public IServerMessage createMessage(Exception exception) {
        return new ServerMessage(0, exception, false);
    }

    @Override
    public IServerMessage createMessage(Error error) {
        return new ServerMessage(0, error, false);
    }
}
