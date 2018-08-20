package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.server.*;

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
    public void request(Object obj, IResponseHandler handler) {
        respondent.request(obj, handler);
    }
}
