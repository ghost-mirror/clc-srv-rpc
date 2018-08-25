package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.impl.common.DataLogger;
import com.ghostmirror.cltsrvrpc.server.*;
import org.apache.log4j.Logger;

public class ServerRespondent implements IRespondent {
//    private static final Logger log = Logger.getLogger(ClientSession.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Server");
    private final IServerMessageFactory factory;
    private final IServiceContainer     container;
    private final IExecutor             executor;

    public ServerRespondent(IServerMessageFactory factory, IServiceContainer container, IExecutor executor) {
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
        log.info(DataLogger.client_request(message));
        IServiceResult result  = container.getService(message.getService()).
                invoke(message.getMethod(), message.getParams());
        return factory.createMessage(message, result);
    }

    @Override
    public void request(Object obj, IResponseHandler handler) {
        if(isNotValidRequest(obj)) {
            handler.response(factory.createError(obj));
            return;
        }
        IClientMessage message = (IClientMessage)obj;
        log.info(DataLogger.client_request(message));
        ISession session = new ServiceSession(factory, container, message, handler);
        executor.blockedExecute(session);
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
        if(obj == null) {
            return true;
        }
        return !(obj    instanceof IClientMessage);
    }
}
