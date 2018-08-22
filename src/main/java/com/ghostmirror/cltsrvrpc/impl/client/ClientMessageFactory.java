package com.ghostmirror.cltsrvrpc.impl.client;

import com.ghostmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.impl.common.ClientMessage;

public class ClientMessageFactory implements IClientMessageFactory {
    @Override
    public IClientMessage createMessage(int id, String service, String method, Object[] params) {
        return new ClientMessage(id, service, method, params);
    }
}
