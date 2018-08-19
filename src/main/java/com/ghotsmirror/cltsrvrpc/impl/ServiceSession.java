package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IService;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;
import com.ghotsmirror.cltsrvrpc.server.IServiceSession;
import com.ghotsmirror.cltsrvrpc.server.IServiceSessionPool;

public class ServiceSession implements IServiceSession {
    private final IServiceSessionPool serviceSessionPool;

    public ServiceSession (IServiceSessionPool serviceSessionPool) {
        this.serviceSessionPool = serviceSessionPool;
    }

    @Override
    public IServiceResult invoke (IService service, String method, Object[] params)  {
        return null;
        //return service.invoke(method, params);
    }

    @Override
    public void run() {

    }

//    public void abort () {

//    }

//    public boolean isStopped () {
//        return serviceSessionPool.isStopped();
//    }


}
