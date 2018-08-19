package com.ghotsmirror.cltsrvrpc.impl;

import java.lang.Thread;

public class ServiceImpl3 {
    public Integer sleep (Integer ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
        return ms;
    }
}