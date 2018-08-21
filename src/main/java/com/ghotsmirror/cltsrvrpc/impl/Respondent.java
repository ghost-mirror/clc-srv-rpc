package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.IClientMessage;
import com.ghotsmirror.cltsrvrpc.server.*;

public class Respondent implements IRespondent {
    private final IServerMessageFactory factory;
    private final IServiceContainer     container;

    public Respondent(IServerMessageFactory factory, IServiceContainer container) {
        this.factory   = factory;
        this.container = container;
    }

    @Override
    public Object request(Object obj) {
        if(isNotValidRequest(obj)) {
            return factory.createError(obj);
        }
        IClientMessage message = (IClientMessage)obj;
        IServiceResult result  = container.getService(message.getService()).invoke(message.getMethod(), message.getParams());
        return factory.createMessage(message.getId(), result);
    }

    @Override
    public void request(Object obj, IResponseHandler handler) {
        if(isNotValidRequest(obj)) {
            handler.response(factory.createError(obj));
            return;
        }
        IClientMessage message = (IClientMessage)obj;
        IServiceResult result  = container.getService(message.getService()).invoke(message.getMethod(), message.getParams());
        handler.response(factory.createMessage(message.getId(), result));
    }

    @Override
    public Object requestId(Object obj) {
        return factory.createMessageId(obj);
    }

    @Override
    public Object requestException(Exception e) {
        return factory.createMessageException(e);
    }

    private boolean isNotValidRequest(Object obj) {
        if(obj == null) return true;
        return !(obj    instanceof IClientMessage);
    }
}
