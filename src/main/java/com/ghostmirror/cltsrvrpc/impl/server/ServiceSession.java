package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IService;
import com.ghostmirror.cltsrvrpc.server.IServiceContainer;
import com.ghostmirror.cltsrvrpc.server.IServiceResult;
import com.ghostmirror.cltsrvrpc.server.IServiceSession;

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
