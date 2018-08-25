package com.ghostmirror.cltsrvrpc.impl.common;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;

public class ClientMessage implements IClientMessage {
    private final int    id;
    private final String service;
    private final String method;
    private final Object[] params;

    @SuppressWarnings({"Annotator"})
    public ClientMessage(int id, String service, String method, Object[] params) {
        this.id      = id;
        this.service = service;
        this.method  = method;
        this.params  = params;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @SuppressWarnings({"Annotator"})
    @Override
    public Object[] getParams() {
        return params;
    }
}