package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;

public interface IClientMessageFactory {
    IClientMessage createMessage(int id, String service, String method, Object[] params);
}
