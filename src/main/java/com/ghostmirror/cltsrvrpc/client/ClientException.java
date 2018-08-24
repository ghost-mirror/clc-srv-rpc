package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IServerMessage;

public class ClientException extends Exception {
    private ClientException(String message) {
        super(message);
    }

    public static void raise(String message) throws ClientException {
        throw new ClientException(message);
    }

    public static void raiseOnError(IServerMessage result) throws ClientException {
        String msg = "ERROR! Message ID: " + result.getId();
        switch(result.getType()) {
            case ID:         return;
            case RESULT:     return;
            case VOID:       return;
            case Rejected:         msg += " Type: Rejected" + result.getObject(); break;
            case WrongService:     msg += " Type: Wrong Service" + result.getObject(); break;
            case WrongMethod:      msg += " Type: Wrong Method" + result.getObject(); break;
            case WrongParametrs:   msg += " Type: Wrong Parametrs" + result.getObject(); break;
            case InnerError:       msg += " Type: Inner Error" + result.getObject(); break;
            case WrongObjectNull:  msg += " Type: Object, is Null" + result.getObject(); break;
            case WrongRequest:     msg += " Type: Wrong Request" + result.getObject(); break;
            case WrongClass:       msg += " Type: Wrong Class" + result.getObject(); break;
            case WrongObject:      msg += " Type: Wrong Object" + result.getObject(); break;
            case RuntimeErrror:    msg += " Type: Runtime Errror" + result.getObject(); break;
            default:               msg += " Type: Undefined Errror";
        }
        raise(msg);
    }
}
