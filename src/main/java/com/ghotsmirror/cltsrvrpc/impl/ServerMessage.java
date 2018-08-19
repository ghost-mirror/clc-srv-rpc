package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.IServerMessage;

public class ServerMessage implements IServerMessage {
    private final int    id;
    private final Object object;
    private final boolean voidResult;

    public ServerMessage(int id, Object object, boolean voidResult) {
        this.id          = id;
        this.object      = object;
        this.voidResult  = voidResult;
    }

    public int getId() {
        return id;
    }
    public boolean isVoid() {
        return voidResult;
    }
    public Object getResult() {
        return object;
    }

}
