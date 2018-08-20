package com.ghotsmirror.cltsrvrpc.client;

import com.ghotsmirror.cltsrvrpc.core.IClientMessage;

public interface IClientMessageFactory {
    public IClientMessage createMessage(int id, String service, String method, Object[] params);
}
