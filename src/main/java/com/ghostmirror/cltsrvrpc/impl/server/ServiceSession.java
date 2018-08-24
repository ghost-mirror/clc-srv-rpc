package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.server.*;

public class ServiceSession implements ISession {
    private final IServerMessageFactory factory;
    private final IServiceContainer     container;
    private final IClientMessage        message;
    private final IResponseHandler      handler;

    public ServiceSession (IServerMessageFactory factory, IServiceContainer container, IClientMessage message, IResponseHandler handler) {
        this.factory   = factory;
        this.container = container;
        this.message   = message;
        this.handler   = handler;
    }

    @Override
    public void run() {
        IServiceResult result;
        IService service;
        try {
            service = container.getService(message.getService());
            result = service.invoke(message.getMethod(), message.getParams());
        } catch (RuntimeException e) {
            handler.response(factory.createMessageException(e, message));
            return;
        }
        handler.response(factory.createMessage(message, result));
    }

    @Override
    public void rejected() {
        handler.response(factory.rejectedMessage(message));
    }
}
