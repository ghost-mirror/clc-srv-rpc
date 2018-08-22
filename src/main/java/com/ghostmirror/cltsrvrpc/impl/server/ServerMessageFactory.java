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
    public IServerMessage createMessage(IClientMessage msg, IServiceResult result) {
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
        return new ServerMessage(msg.getId(), result.getObject(), type, msg);
    }

    @Override
    public IServerMessage rejectedMessage(int id) {
        return new ServerMessage(id, id, EServerResult.Rejected, null);
    }

    @Override
    public IServerMessage createMessageId(Object obj) {
        if(obj instanceof IClientMessage) {
            int id = ((IClientMessage)obj).getId();
            return new ServerMessage(id, id, EServerResult.ID, (IClientMessage)obj);
        } else {
            return new ServerMessage(0, 0, EServerResult.ID, null);
        }
    }

    @Override
    public IServerMessage createMessageException(Exception e) {
        if(e instanceof IOException) {
            return new ServerMessage(0, e, EServerResult.WrongRequest, null);
        }
        if(e instanceof ClassNotFoundException) {
            return new ServerMessage(0, e, EServerResult.WrongClass, null);
        }
        return new ServerMessage(0, e, EServerResult.WrongRequest, null);
    }

    @Override
    public IServerMessage createError(Object obj) {
        if(obj == null) {
            return new ServerMessage(0, null, EServerResult.WrongObjectNull, null);
        }
        return new ServerMessage(0, null, EServerResult.WrongObject, null);
    }
}
