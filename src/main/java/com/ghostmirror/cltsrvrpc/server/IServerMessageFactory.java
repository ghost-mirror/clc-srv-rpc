package com.ghostmirror.cltsrvrpc.server;

import com.ghostmirror.cltsrvrpc.common.IServerMessage;

public interface IServerMessageFactory {
    IServerMessage createMessage(int id, IServiceResult result);
    IServerMessage createMessageId(Object obj);
    IServerMessage createMessageException(Exception e);
    IServerMessage createError(Object obj);
}
