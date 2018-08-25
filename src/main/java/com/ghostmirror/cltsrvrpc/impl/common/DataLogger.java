package com.ghostmirror.cltsrvrpc.impl.common;

import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.common.IServerMessage;

public class DataLogger {
    private static StringBuilder sb = new StringBuilder();

    public static synchronized String client_request (IClientMessage message) {
        sb.delete(0, sb.length());
        sb.append("request");
        log_message(message);
        return sb.toString();
    }

    public static synchronized String server_message (IServerMessage obj) {
        return server_response(obj);
    }

    public static synchronized String server_response (IServerMessage obj) {
        sb.delete(0, sb.length());
        switch(obj.getType()) {
            case ID:       sb.append("accepted"); log_message(obj.getRequest());
                return sb.toString();
            case RESULT:   sb.append("response"); log_message(obj.getRequest()); sb.append(".result(").append(obj.getObject().getClass().getSimpleName()).append(":").append(obj.getObject().toString()).append(")");
                return sb.toString();
            case VOID:     sb.append("response"); log_message(obj.getRequest()); sb.append(".result(void)");
                return sb.toString();
            case Rejected:          sb.append("Rejected ")        .append(obj.getObject()); break;
            case WrongService:      sb.append("Wrong Service ")   .append(obj.getObject()); break;
            case WrongMethod:       sb.append("Wrong Method ")    .append(obj.getObject()); break;
            case WrongParameters:   sb.append("Wrong Parameters ").append(obj.getObject()); break;
            case InnerError:        sb.append("Inner Error ")     .append(obj.getObject()); break;
            case WrongObjectNull:   sb.append("Object, is Null ") .append(obj.getObject()); break;
            case WrongRequest:      sb.append("Wrong Request ")   .append(obj.getObject()); break;
            case WrongClass:        sb.append("Wrong Class ")     .append(obj.getObject()); break;
            case WrongObject:       sb.append("Wrong Object ")    .append(obj.getObject()); break;
            case RuntimeError:      sb.append("Runtime Error: ")  .append(obj.getObject()); break;
            default:                sb.append("Undefined Error");
        }
        log_message(obj.getRequest());
        return sb.toString();
    }

    private static void log_message (IClientMessage message) {
        if(message == null) {
            sb.append("(message == null)");
            return;
        }
        Object[] params = message.getParams();
        sb.append("(").append(message.getId()).
                append("): service(").append(message.getService()).
                append(").method(").append(message.getMethod()).
                append(").params(");

        if(params == null) {
            sb.append("null").append(")");
            return;
        }
        if(params.length == 0) {
            sb.append("void");
        } else {
            Object o = params[0];
            if(o != null) {
                sb.append(o.getClass().getSimpleName()).append(":").append(o.toString());
            } else {
                sb.append("null");
            }
            for (int i=1; i<params.length; i++) {
                o = params[i];
                if(o != null) {
                    sb.append(",").append(o.getClass().getSimpleName()).append(":").append(o.toString());
                } else {
                    sb.append(", null");
                }
            }
        }
        sb.append(")");
    }
}
