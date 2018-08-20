package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IServiceContainer;
import com.ghotsmirror.cltsrvrpc.server.IServiceSession;
import com.ghotsmirror.cltsrvrpc.server.IServiceSessionFactory;

public class ServiceSessionFactory implements IServiceSessionFactory {
    public IServiceSession createServiceSession(IServiceContainer serviceContainer) {
        return new ServiceSession(serviceContainer);
    }
}
