package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.IServiceSession;
import com.ghotsmirror.cltsrvrpc.server.IServiceSessionFactory;
import com.ghotsmirror.cltsrvrpc.server.IServiceSessionPool;

public class ServiceSessionFactory implements IServiceSessionFactory {
    public IServiceSession createServiceSession(IServiceSessionPool pool) {return new ServiceSession(pool);}
}
