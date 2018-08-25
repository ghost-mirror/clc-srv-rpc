package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.common.EServiceResult;
import com.ghostmirror.cltsrvrpc.server.IService;
import com.ghostmirror.cltsrvrpc.server.IServiceResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Service implements IService {
    private final Object object;
    private final Class  ServiceClass;

    public Service (Object object) {
       this.object = object;
       ServiceClass = object.getClass();
    }

    @Override
    public IServiceResult invoke(String method, Object[] params) {
        if(method == null) {
            return new ServiceResult(EServiceResult.WrongMethod);
        }

        int len = (params == null)?0:params.length;
        Class[] paramTypes = new Class[len];
        Method m;

        for (int i = 0; i < len; i++) {
            if(params[i] == null) {
                paramTypes[i] = null;
            } else {
                paramTypes[i] = params[i].getClass();
            }
        }

        try {
            m = ServiceClass.getMethod(method, paramTypes);
        } catch (NoSuchMethodException e) {
            Method[] methods = ServiceClass.getMethods();
            for (Method mm : methods) {
                if(mm.getName().equals(method)) {
                    return new ServiceResult(EServiceResult.WrongParameters);
                }
            }
            return new ServiceResult(EServiceResult.WrongMethod);
        } catch (SecurityException e) {
            return new ServiceResult(EServiceResult.WrongMethod);
        }

        try {
            return new ServiceResult(m.invoke(object, params), (m.getReturnType()==void.class)?EServiceResult.VOID:EServiceResult.RESULT);
        } catch (IllegalAccessException|InvocationTargetException e) {
            return new ServiceResult(EServiceResult.InnerError);
        }
    }
}


