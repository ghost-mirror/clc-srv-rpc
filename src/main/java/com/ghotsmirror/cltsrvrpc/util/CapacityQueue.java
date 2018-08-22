package com.ghotsmirror.cltsrvrpc.util;

import java.util.concurrent.LinkedBlockingQueue;

public class CapacityQueue extends LinkedBlockingQueue<Runnable> {
    private volatile int maxCapacity;

    public CapacityQueue (int maxCapacity) {
        super();
        this.maxCapacity = maxCapacity;
    }

    @Override
    public synchronized boolean offer(Runnable e) {
        if(size() >= maxCapacity) {
            return false;
        }
        return super.offer(e);
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return  maxCapacity;
    }
}