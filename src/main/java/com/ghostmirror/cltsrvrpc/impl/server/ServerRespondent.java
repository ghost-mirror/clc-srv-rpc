package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.server.*;

public class ServerRespondent implements IRespondent {
    private final IServerMessageFactory factory;
    private final IServiceContainer     container;
    private final IEexecutor            executor;

    public ServerRespondent(IServerMessageFactory factory, IServiceContainer container, IEexecutor executor) {
        this.factory   = factory;
        this.container = container;
        this.executor  = executor;
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
        IServiceSession session = new ServiceSession(factory, container, (IClientMessage)obj, handler);
        session.run();
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
