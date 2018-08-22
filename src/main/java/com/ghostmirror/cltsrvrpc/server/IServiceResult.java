package com.ghostmirror.cltsrvrpc.server;

import com.ghostmirror.cltsrvrpc.common.EServiceResult;

public interface IServiceResult {
    Object getObject();
    EServiceResult getType();
}
