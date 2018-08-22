package com.ghotsmirror.cltsrvrpc.impl.server;

import com.ghotsmirror.cltsrvrpc.common.EServiceResult;
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
                    return new ServiceResult(EServiceResult.WrongParametrs);
                }
            }
            return new ServiceResult(EServiceResult.WrongMethod);
        }

        try {
            return new ServiceResult(m.invoke(impl, params), (m.getReturnType()==void.class)?EServiceResult.VOID:EServiceResult.RESULT);
        } catch (IllegalAccessException e) {
            return new ServiceResult(EServiceResult.InnerError);
        } catch (InvocationTargetException e) {
            return new ServiceResult(EServiceResult.InnerError);
        }
    }
}


