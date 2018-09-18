package com.ghostmirror.cltsrvrpc.impl.service;

import java.util.Date;

@SuppressWarnings({"unused"})
public class ServiceImpl3 {
    public void sleep (Integer ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static public Date currentDate() {
        return new Date();
    }
}