package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.*;
import com.ghotsmirror.cltsrvrpc.server.IServerMessageFactory;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;

import java.io.IOException;

public class ServerMessageFactory implements IServerMessageFactory {
    @Override
    public IServerMessage createMessage(int id, IServiceResult result) {
        return new ServerMessage(id, result.getObject(), result.isVoid());
    }

    @Override
    public IServerMessage createMessage(Object obj) {
        if(obj == null) {
            return new ServerMessage(0, new WrongClientMessage(), false);
        }
        if(obj instanceof IOException) {
            return new ServerMessage(0, new WrongRequest(), false);
        }
        if(obj instanceof ClassNotFoundException) {
            return new ServerMessage(0, new WrongClass(), false);
        }

        return new ServerMessage(0, new WrongRequest(), false);
    }
}
