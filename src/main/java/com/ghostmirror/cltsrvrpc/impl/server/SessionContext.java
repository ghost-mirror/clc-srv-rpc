package com.ghostmirror.cltsrvrpc.impl.server;


import com.ghostmirror.cltsrvrpc.server.IRespondent;
import com.ghostmirror.cltsrvrpc.server.IResponseHandler;
import com.ghostmirror.cltsrvrpc.server.ISessionContext;
import com.ghostmirror.cltsrvrpc.server.IThreadPool;

public class SessionContext implements ISessionContext {
    private final IRespondent respondent;

    public SessionContext (IRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public Object request(Object obj) {
        return respondent.request(obj);
    }

    @Override
    public Object requestId(Object obj) {
        return respondent.requestId(obj);
    }

    @Override
    public Object requestException(Exception e) {
        return respondent.requestException(e);
    }

    @Override
    public void request(Object obj, IResponseHandler handler) {
        respondent.request(obj, handler);
    }
}
