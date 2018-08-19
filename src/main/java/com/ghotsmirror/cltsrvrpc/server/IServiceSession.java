package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceSession extends Runnable {
    public IServiceResult invoke (IService service, String method, Object[] params);
//    public void abort ();
//    public boolean isStopped ();
}
