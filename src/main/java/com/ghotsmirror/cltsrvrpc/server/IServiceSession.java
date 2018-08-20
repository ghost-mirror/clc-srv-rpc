package com.ghotsmirror.cltsrvrpc.server;

public interface IServiceSession extends Runnable {
    public IServiceResult invoke (String service, String method, Object[] params);
//    public void abort ();
//    public boolean isStopped ();
}
