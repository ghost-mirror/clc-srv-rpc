package com.ghotsmirror.cltsrvrpc.client;

import com.ghotsmirror.cltsrvrpc.common.IClientMessage;

public interface IClientMessageFactory {
    IClientMessage createMessage(int id, String service, String method, Object[] params);
}
