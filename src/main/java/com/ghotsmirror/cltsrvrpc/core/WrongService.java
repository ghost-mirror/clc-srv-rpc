package com.ghotsmirror.cltsrvrpc.core;

import com.ghotsmirror.cltsrvrpc.server.IService;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;

public class WrongService extends Error implements IService {
    public IServiceResult invoke(String method, Object[] params) {
        return new ServiceResult(this);
    }
    private class ServiceResult implements IServiceResult {
        private final Object  object;
        private final boolean isVoid = false;

        public ServiceResult(Object object) {
            this.object = object;
        }

        @Override
        public Object getObject() {
            return object;
        }

        @Override
        public boolean isVoid() {
            return isVoid;
        }
    }
}
