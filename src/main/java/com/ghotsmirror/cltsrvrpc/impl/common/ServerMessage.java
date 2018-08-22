package com.ghotsmirror.cltsrvrpc.impl.common;

import com.ghotsmirror.cltsrvrpc.common.EServerResult;
import com.ghotsmirror.cltsrvrpc.common.IServerMessage;

public class ServerMessage implements IServerMessage {
    private final int    id;
    private final Object object;
    private final EServerResult type;

    public ServerMessage(int id, Object object, EServerResult type) {
        this.id     = id;
        this.object = object;
        this.type   = type;
    }

    public int getId() {
        return id;
    }
    public EServerResult getType() {
        return type;
    }
    public Object getObject() {
        return object;
    }

}
