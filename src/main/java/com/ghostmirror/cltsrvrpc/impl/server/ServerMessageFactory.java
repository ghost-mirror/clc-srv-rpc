package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.common.EServerResult;
import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;
import com.ghostmirror.cltsrvrpc.impl.common.ServerMessage;
import com.ghostmirror.cltsrvrpc.server.IServerMessageFactory;
import com.ghostmirror.cltsrvrpc.server.IServiceResult;

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
    public IServerMessage rejectedMessage(int id) {
        return new ServerMessage(id, id, EServerResult.Rejected);
    }

    @Override
    public IServerMessage createMessageId(Object obj) {
        int id = (obj instanceof IClientMessage)?((IClientMessage)obj).getId():0;
        return new ServerMessage(id, id, EServerResult.ID);
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
