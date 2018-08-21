package com.ghotsmirror.cltsrvrpc.server;

import com.ghotsmirror.cltsrvrpc.core.EServiceResult;

public interface IServiceResult {
    Object getObject();
    EServiceResult getType();
}
