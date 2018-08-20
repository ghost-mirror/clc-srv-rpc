package com.ghotsmirror.cltsrvrpc.server;

import com.ghotsmirror.cltsrvrpc.core.IServerMessage;

public interface IServerMessageFactory {
    IServerMessage createMessage(int id, IServiceResult result);
    IServerMessage createMessage(Object obj);
}
