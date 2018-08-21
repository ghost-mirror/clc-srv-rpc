package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.*;
import com.ghotsmirror.cltsrvrpc.server.IServerMessageFactory;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;

import java.io.IOException;

public class ServerMessageFactory implements IServerMessageFactory {
    @Override
    public IServerMessage createMessage(int id, IServiceResult result) {
        EServerResult type;
        switch(result.getType()) {
            case VOID:           type = EServerResult.VOID;           break;
            case RESULT:         type = EServerResult.RESULT;         break;
            case WrongService:   type = EServerResult.WrongService;   break;
            case WrongMethod:    type = EServerResult.WrongMethod;    break;
            case WrongParametrs: type = EServerResult.WrongParametrs; break;
            case InnerError:     type = EServerResult.InnerError;     break;
            default:             type = EServerResult.InnerError;
        }
        return new ServerMessage(id, result.getObject(), type);
    }

    @Override
    public IServerMessage createMessageId(Object obj) {
        int id = (obj instanceof IClientMessage)?((IClientMessage)obj).getId():0;
        new ServerMessage(id, null, EServerResult.ID);
        return null;
    }

    @Override
    public IServerMessage createMessageException(Exception e) {
        if(e instanceof IOException) {
            return new ServerMessage(0, e, EServerResult.WrongRequest);
        }
        if(e instanceof ClassNotFoundException) {
            return new ServerMessage(0, e, EServerResult.WrongClass);
        }
        return new ServerMessage(0, e, EServerResult.WrongRequest);
    }

    @Override
    public IServerMessage createError(Object obj) {
        if(obj == null) {
            return new ServerMessage(0, null, EServerResult.WrongObjectNull);
        }
        return new ServerMessage(0, null, EServerResult.WrongObject);
    }
}
