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
            case Rejected:         msg += " Type: Rejected"; break;
            case WrongService:     msg += " Type: Wrong Service"; break;
            case WrongMethod:      msg += " Type: Wrong Method"; break;
            case WrongParametrs:   msg += " Type: Wrong Parametrs"; break;
            case InnerError:       msg += " Type: Inner Error"; break;
            case WrongObjectNull:  msg += " Type: Object, is Null"; break;
            case WrongRequest:     msg += " Type: Wrong Request"; break;
            case WrongClass:       msg += " Type: Wrong Class"; break;
            case WrongObject:      msg += " Type: Wrong Object"; break;
            default:               msg += " Type: Undefined Errror";
        }
        raise(msg);
    }
}
