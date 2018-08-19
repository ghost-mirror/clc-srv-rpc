package com.ghotsmirror.cltsrvrpc.core;

import java.io.Serializable;

public interface IClientMessage extends Serializable {
    public int getId();
    public String getService();
    public String getMethod();
    public Object[] getParams();
}
