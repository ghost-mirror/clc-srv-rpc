package com.ghostmirror.cltsrvrpc.common;

import java.io.Serializable;

public interface IServerMessage extends Serializable {
    EServerResult getType();
    int getId();
    Object getObject();
}
