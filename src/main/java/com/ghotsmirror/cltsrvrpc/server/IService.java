package com.ghotsmirror.cltsrvrpc.server;

public interface IService {
    public IServiceResult invoke(String method, Object[] params);
}
