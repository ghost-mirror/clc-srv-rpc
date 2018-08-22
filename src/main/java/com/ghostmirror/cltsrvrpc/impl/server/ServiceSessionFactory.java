package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IServiceContainer;
import com.ghostmirror.cltsrvrpc.server.IServiceSession;
import com.ghostmirror.cltsrvrpc.server.IServiceSessionFactory;

public class ServiceSessionFactory implements IServiceSessionFactory {
    public IServiceSession createServiceSession(IServiceContainer serviceContainer) {
        return new ServiceSession(serviceContainer);
    }
}
