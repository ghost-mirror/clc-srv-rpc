package com.ghostmirror.cltsrvrpc.impl.service;

import java.lang.Thread;
import java.util.Date;

public class ServiceImpl3 {
    public Integer sleep (Integer ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
        return ms;
    }

    public Date currentDate() {
        return new Date();
    }
}