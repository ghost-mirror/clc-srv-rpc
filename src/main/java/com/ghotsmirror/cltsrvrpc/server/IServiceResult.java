package com.ghotsmirror.cltsrvrpc.server;

import com.ghotsmirror.cltsrvrpc.common.EServiceResult;

public interface IServiceResult {
    Object getObject();
    EServiceResult getType();
}
