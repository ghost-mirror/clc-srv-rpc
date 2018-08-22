package com.ghotsmirror.cltsrvrpc.impl.server;

import com.ghotsmirror.cltsrvrpc.common.EServiceResult;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;

class ServiceResult implements IServiceResult {
    private final Object         object;
    private final EServiceResult type;

    public ServiceResult(EServiceResult type) {
        this.object = null;
        this.type   = type;
    }

    public ServiceResult(Object object, EServiceResult type) {
        this.object = object;
        this.type   = type;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public EServiceResult getType() {
        return type;
    }
}
