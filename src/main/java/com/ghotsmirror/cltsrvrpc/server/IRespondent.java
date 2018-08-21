package com.ghotsmirror.cltsrvrpc.server;

public interface IRespondent {
    void   request(Object obj, IResponseHandler handler);
    Object request(Object obj);
    Object requestId(Object obj);
    Object requestException(Exception e);
}
