package com.ghostmirror.cltsrvrpc.impl.client;

import com.ghostmirror.cltsrvrpc.client.IClientMessageFactory;
import com.ghostmirror.cltsrvrpc.common.IClientMessage;
import com.ghostmirror.cltsrvrpc.impl.common.ClientMessage;
import com.ghostmirror.cltsrvrpc.impl.common.DataLogger;
import org.apache.log4j.Logger;

public class ClientMessageFactory implements IClientMessageFactory {
//    private static final Logger log = Logger.getLogger(Client.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Client");

    @Override
    public IClientMessage createMessage(int id, String service, String method, Object[] params) {
        IClientMessage msg = new ClientMessage(id, service, method, params);
        log.info(DataLogger.client_request(msg));
        return msg;
    }
}
