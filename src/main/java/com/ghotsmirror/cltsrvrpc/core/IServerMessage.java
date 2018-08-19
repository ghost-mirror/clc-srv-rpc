package com.ghotsmirror.cltsrvrpc.core;

import java.io.Serializable;

public interface IServerMessage extends Serializable {
    public int getId();
    public boolean isVoid();
    public Object getResult();
}
