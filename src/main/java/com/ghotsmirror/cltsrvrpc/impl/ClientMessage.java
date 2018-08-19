package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.IClientMessage;

public class ClientMessage implements IClientMessage {
    private final int    id;
    private final String service;
    private final String method;
    private final Object[] params;

    public ClientMessage(int id, String service, String method, Object[] params) {
        this.id      = id;
        this.service = service;
        this.method  = method;
        this.params  = params;
    }

    public int getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getParams() {
        return params;
    }
}