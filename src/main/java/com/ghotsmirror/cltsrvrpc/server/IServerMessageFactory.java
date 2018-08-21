package com.ghotsmirror.cltsrvrpc.server;

import com.ghotsmirror.cltsrvrpc.core.IServerMessage;

public interface IServerMessageFactory {
    IServerMessage createMessage(int id, IServiceResult result);
    IServerMessage createMessageId(Object obj);
    IServerMessage createMessageException(Exception e);
    IServerMessage createError(Object obj);
}
