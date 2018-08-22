package com.ghostmirror.cltsrvrpc.impl.common;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;

public class DataLogger {
    public static String client_request (IClientMessage message) {
        return log_message("request", message);
    }

    public static String server_message (IServerMessage obj) {
        return server_response(obj);
    }

    public static String server_response (IServerMessage obj) {
        String msg;

        switch(obj.getType()) {
            case ID:       return log_message("accepted", obj.getRequest());
            case RESULT:   return log_message("response", obj.getRequest()) + ".result(" + obj.getObject().getClass().getSimpleName() + ":" + obj.getObject().toString() + ")";
            case VOID:     return log_message("response", obj.getRequest()) + ".result(void)";
            case Rejected:         msg = "Rejected"; break;
            case WrongService:     msg = "Wrong Service"; break;
            case WrongMethod:      msg = "Wrong Method"; break;
            case WrongParametrs:   msg = "Wrong Parameters"; break;
            case InnerError:       msg = "Inner Error"; break;
            case WrongObjectNull:  msg = "Object, is Null"; break;
            case WrongRequest:     msg = "Wrong Request"; break;
            case WrongClass:       msg = "Wrong Class"; break;
            case WrongObject:      msg = "Wrong Object"; break;
            default:               msg = "Undefined Error";
        }
        return log_message(msg, obj.getRequest());
    }

    private static String log_message (String type, IClientMessage message) {
        String par;
        Object[] params = message.getParams();
        if(params.length == 0) {
            par = "void";
        } else {
            Object o = params[0];
            par = o.getClass().getSimpleName() + ":" + o.toString();
            for (int i=1; i<params.length; i++) {
                o = params[i];
                par +=  "," + o.getClass().getSimpleName() + ":" + o.toString();
            }
        }
        return  type + "(" + message.getId() + "): service(" + message.getService() + ").method(" + message.getMethod() + ").params(" + par + ")";
    }

}
