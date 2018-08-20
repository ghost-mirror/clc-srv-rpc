package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghotsmirror.cltsrvrpc.core.IClientMessage;

public class ClientMessageFactory implements IClientMessageFactory {
    @Override
    public IClientMessage createMessage(int id, String service, String method, Object[] params) {
        return new ClientMessage(id, service, method, params);
    }
}
