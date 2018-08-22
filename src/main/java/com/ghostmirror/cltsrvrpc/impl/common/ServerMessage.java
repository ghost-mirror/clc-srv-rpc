package com.ghostmirror.cltsrvrpc.impl.common;

import com.ghostmirror.cltsrvrpc.common.EServerResult;
import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;

public class ServerMessage implements IServerMessage {
    private final int    id;
    private final Object object;
    private final EServerResult type;
    private final IClientMessage request;

    public ServerMessage(int id, Object object, EServerResult type, IClientMessage request) {
        this.id      = id;
        this.object  = object;
        this.type    = type;
        this.request = request;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public EServerResult getType() {
        return type;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public IClientMessage getRequest() {
        return request;
    }

}
