package com.ghostmirror.cltsrvrpc.server;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;

public interface IServerMessageFactory {
    IServerMessage createMessage(IClientMessage msg, IServiceResult result);
    IServerMessage createMessageId(Object obj);
    IServerMessage createMessageException(Exception e);
    IServerMessage createMessageException(Exception e, IClientMessage msg);
    IServerMessage createError(Object obj);
    IServerMessage rejectedMessage(IClientMessage msg);
}
