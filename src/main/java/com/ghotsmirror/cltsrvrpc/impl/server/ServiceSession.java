package com.ghotsmirror.cltsrvrpc.impl.server;

import com.ghotsmirror.cltsrvrpc.server.IService;
import com.ghotsmirror.cltsrvrpc.server.IServiceContainer;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;
import com.ghotsmirror.cltsrvrpc.server.IServiceSession;

public class ServiceSession implements IServiceSession {
    private final IServiceContainer serviceContainer;

    public ServiceSession (IServiceContainer serviceContainer) {
        this.serviceContainer = serviceContainer;
    }

    @Override
    public IServiceResult invoke (String serviceName, String method, Object[] params)  {
        try {
            this.wait();
            //Semaphore
        } catch (InterruptedException e) {
        }
        IService service = serviceContainer.getService(serviceName);
        return service.invoke(method, params);
    }

    @Override
    public void run() {

    }
}
