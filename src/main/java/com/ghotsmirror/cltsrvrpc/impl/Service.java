package com.ghotsmirror.cltsrvrpc.impl;

import com.ghotsmirror.cltsrvrpc.core.InnerError;
import com.ghotsmirror.cltsrvrpc.core.WrongMethod;
import com.ghotsmirror.cltsrvrpc.core.WrongParametrs;
import com.ghotsmirror.cltsrvrpc.server.IService;
import com.ghotsmirror.cltsrvrpc.server.IServiceResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Service implements IService {
    private Object impl;

    public Service (Object impl) {
       this.impl = impl;
    }

    @Override
    public IServiceResult invoke(String method, Object[] params) {
        Class cls = impl.getClass();
        Class[] paramTypes = new Class[params.length];
        Method m;

        for (int i = 0; i < params.length; i++) {
            paramTypes[i] = params[i].getClass();
        }

        try {
            m = cls.getMethod(method, paramTypes);
        } catch (NoSuchMethodException e) {
            Method[] methods = cls.getMethods();
            for (Method mm : methods) {
                if(mm.getName().equals(method)) {
                    return new ServiceResult(new WrongParametrs(), false);
                }
            }
            return new ServiceResult(new WrongMethod(), false);
        }

        try {
            return new ServiceResult(m.invoke(impl, params), m.getReturnType()==void.class);
        } catch (IllegalAccessException e) {
            return new ServiceResult(new InnerError(), false);
        } catch (InvocationTargetException e) {
            return new ServiceResult(new InnerError(), false);
        }
    }

    private class ServiceResult implements IServiceResult {
        private final Object  object;
        private final boolean isVoid;

        public ServiceResult(Object object, boolean isVoid) {
            this.object = object;
            this.isVoid = isVoid;
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
