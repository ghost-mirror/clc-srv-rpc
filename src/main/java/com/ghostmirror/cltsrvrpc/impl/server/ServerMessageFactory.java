package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.common.EServerResult;
import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;
import com.ghostmirror.cltsrvrpc.impl.common.DataLogger;
import com.ghostmirror.cltsrvrpc.impl.common.ServerMessage;
import com.ghostmirror.cltsrvrpc.server.IServerMessageFactory;
import com.ghostmirror.cltsrvrpc.server.IServiceResult;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ServerMessageFactory implements IServerMessageFactory {
//    private static final Logger log = Logger.getLogger(ClientSession.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Server");

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
        IServerMessage message = new ServerMessage(msg.getId(), result.getObject(), type, msg);
        if(type == EServerResult.VOID || type == EServerResult.RESULT) {
            log.info(DataLogger.server_message(message));
        } else {
            log.error(DataLogger.server_message(message));
        }
        return message;
    }

    @Override
    public IServerMessage rejectedMessage(IClientMessage msg) {
        IServerMessage message = new ServerMessage(msg.getId(), msg.getId(), EServerResult.Rejected, msg);
        log.error(DataLogger.server_message(message));
        return message;
    }

    @Override
    public IServerMessage createMessageId(Object obj) {
        IServerMessage message;
        if(obj instanceof IClientMessage) {
            int id = ((IClientMessage)obj).getId();
            message = new ServerMessage(id, "", EServerResult.ID, (IClientMessage)obj);
            log.info(DataLogger.server_message(message));
            return message;
        } else {
            message = new ServerMessage(0, "", EServerResult.ID, null);
            log.error(DataLogger.server_message(message));
            return message;
        }
    }

    @Override
    public IServerMessage createMessageException(Exception e) {
        return createMessageException(e, null);
    }

    @Override
    public IServerMessage createMessageException(Exception e, IClientMessage msg) {
        IServerMessage message;
        int id = (msg == null)?0:msg.getId();
        if (e instanceof IOException) {
            message = new ServerMessage(id, e.toString(), EServerResult.WrongRequest, msg);
        } else if (e instanceof ClassNotFoundException) {
            message = new ServerMessage(id, e.toString(), EServerResult.WrongClass, msg);
        } else if (e instanceof RuntimeException) {
            message = new ServerMessage(id, e.toString(), EServerResult.RuntimeErrror, msg);
        } else {
            message = new ServerMessage(id, e.toString(), EServerResult.WrongRequest, msg);
        }
        log.error(DataLogger.server_message(message));
        return message;
    }

    @Override
    public IServerMessage createError(Object obj) {
        IServerMessage message;
        if(obj == null) {
            message = new ServerMessage(0, "", EServerResult.WrongObjectNull, null);
        } else {
            message = new ServerMessage(0, obj, EServerResult.WrongObject, null);
        }
        log.error(DataLogger.server_response(message));
        return message;
    }
}
