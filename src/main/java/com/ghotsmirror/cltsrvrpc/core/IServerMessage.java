package com.ghotsmirror.cltsrvrpc.core;

import java.io.Serializable;

public interface IServerMessage extends Serializable {
    int getId();
    boolean isVoid();
    Object getObject();
}
