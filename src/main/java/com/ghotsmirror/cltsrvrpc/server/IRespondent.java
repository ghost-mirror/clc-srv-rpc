package com.ghotsmirror.cltsrvrpc.server;

public interface IRespondent {
    Object request(Object obj);
    void request(Object obj, IResponseHandler handler);
}
