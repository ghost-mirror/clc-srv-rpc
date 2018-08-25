package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.common.EServiceResult;
import com.ghostmirror.cltsrvrpc.server.IService;
import com.ghostmirror.cltsrvrpc.server.IServiceResult;

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
        int len = (params == null)?0:params.length;
        Class[] paramTypes = new Class[len];
        Method m;

        if(method == null) {
            return new ServiceResult(EServiceResult.WrongMethod);
        }

        for (int i = 0; i < len; i++) {
            if(params[i] == null) {
                paramTypes[i] = null;
            } else {
                paramTypes[i] = params[i].getClass();
            }
        }

        try {
            m = cls.getMethod(method, paramTypes);
        } catch (NoSuchMethodException e) {
            Method[] methods = cls.getMethods();
            for (Method mm : methods) {
                if(mm.getName().equals(method)) {
                    return new ServiceResult(EServiceResult.WrongParameters);
                }
            }
            return new ServiceResult(EServiceResult.WrongMethod);
        }

        try {
            return new ServiceResult(m.invoke(impl, params), (m.getReturnType()==void.class)?EServiceResult.VOID:EServiceResult.RESULT);
        } catch (IllegalAccessException|InvocationTargetException e) {
            return new ServiceResult(EServiceResult.InnerError);
        }
    }
}


