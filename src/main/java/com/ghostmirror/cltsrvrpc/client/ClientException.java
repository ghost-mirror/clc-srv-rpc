package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IServerMessage;

public class ClientException extends Exception {
    private final String message;

    private ClientException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static void raise(String message) throws ClientException {
        throw new ClientException(message);
    }

    public static void raiseOnError(IServerMessage result) throws ClientException {
        String msg = "ERROR! Message ID: " + result.getId()     + " Type: ";
        switch(result.getType()) {
            case ID:         return;
            case RESULT:     return;
            case VOID:       return;
            case Rejected:         msg += "Rejected"; break;
            case WrongService:     msg += "Wrong Service"; break;
            case WrongMethod:      msg += "Wrong Method"; break;
            case WrongParametrs:   msg += "Wrong Parametrs"; break;
            case InnerError:       msg += "Inner Error"; break;
            case WrongObjectNull:  msg += "Object, is Null"; break;
            case WrongRequest:     msg += "Wrong Request"; break;
            case WrongClass:       msg += "Wrong Class"; break;
            case WrongObject:      msg += "Wrong Object"; break;
            default:               msg += "Undefined Errror";
        }
        raise(msg);
    }
}
