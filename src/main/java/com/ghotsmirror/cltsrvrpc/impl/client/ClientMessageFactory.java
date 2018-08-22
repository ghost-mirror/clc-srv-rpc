package com.ghotsmirror.cltsrvrpc.impl.client;

import com.ghotsmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghotsmirror.cltsrvrpc.common.IClientMessage;
import com.ghotsmirror.cltsrvrpc.impl.common.ClientMessage;

public class ClientMessageFactory implements IClientMessageFactory {
    @Override
    public IClientMessage createMessage(int id, String service, String method, Object[] params) {
        return new ClientMessage(id, service, method, params);
    }
}
