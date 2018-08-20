package com.ghotsmirror.cltsrvrpc.server;

public interface IService {
    IServiceResult invoke(String method, Object[] params);
}
