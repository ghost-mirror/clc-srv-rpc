package com.ghotsmirror.cltsrvrpc.server;

import com.ghotsmirror.cltsrvrpc.core.IServerMessage;

public interface IServerMessageFactory {
    public IServerMessage createMessage(int id, IServiceResult result);
    public IServerMessage createMessage(Exception exception);
    public IServerMessage createMessage(Error error);
}
