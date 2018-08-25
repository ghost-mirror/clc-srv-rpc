package com.ghostmirror.cltsrvrpc.client;

import com.ghostmirror.cltsrvrpc.common.IServerMessage;

public class ClientException extends Exception {
    private static final StringBuilder sb = new StringBuilder();

    private ClientException(String message) {
        super(message);
    }

    public static void raise(String message) throws ClientException {
        throw new ClientException(message);
    }

    public static synchronized void raiseOnError(IServerMessage result) throws ClientException {
        switch(result.getType()) {
            case ID:         return;
            case RESULT:     return;
            case VOID:       return;
            default:
         }
        sb.delete(0, sb.length());
        sb.append("ERROR! Message ID: ").append(result.getId());
        switch(result.getType()) {
            case Rejected:         sb.append(" Type: Rejected ");         break;
            case WrongService:     sb.append(" Type: Wrong Service ");    break;
            case WrongMethod:      sb.append(" Type: Wrong Method ");     break;
            case WrongParameters:  sb.append(" Type: Wrong Parameters "); break;
            case InnerError:       sb.append(" Type: Inner Error ");      break;
            case WrongObjectNull:  sb.append(" Type: Object, is Null ");  break;
            case WrongRequest:     sb.append(" Type: Wrong Request ");    break;
            case WrongClass:       sb.append(" Type: Wrong Class ");      break;
            case WrongObject:      sb.append(" Type: Wrong Object ");     break;
            case RuntimeError:     sb.append(" Type: Runtime Error ");    break;
            default:               sb.append(" Type: Undefined Error ");
        }
        sb.append(result.getObject());
        raise(sb.toString());
    }
}
