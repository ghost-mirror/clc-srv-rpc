package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.server.IEexecutor;
import com.ghostmirror.cltsrvrpc.server.IThreadPool;
import com.ghostmirror.cltsrvrpc.util.CapacityQueue;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class ServiceSessionPool extends ThreadPool implements IEexecutor {
    private static final Logger log = Logger.getLogger(ServiceSessionPool.class.getCanonicalName());

    public ServiceSessionPool (int queueSize, int poolSize) {
        super(queueSize, poolSize, new ServiceSessionRejected());

    }
    @Override
    public void run() {

    }

    @Override
    public void execute(Runnable command) {

    }
}

class ServiceSessionRejected implements RejectedExecutionHandler {
    private static final Logger log = Logger.getLogger(ServiceSessionPool.class.getCanonicalName());

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.info("Connection Rejected!");
        ((ClientSession)r).close();
    }
}
