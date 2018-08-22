package com.ghotsmirror.cltsrvrpc.common;

import java.io.Serializable;

public interface IClientMessage extends Serializable {
    int getId();
    String getService();
    String getMethod();
    Object[] getParams();
}
