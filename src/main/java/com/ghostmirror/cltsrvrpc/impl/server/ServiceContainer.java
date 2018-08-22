package com.ghostmirror.cltsrvrpc.impl.server;

import com.ghostmirror.cltsrvrpc.common.EServiceResult;
import com.ghostmirror.cltsrvrpc.server.IService;
import com.ghostmirror.cltsrvrpc.server.IServiceContainer;
import com.ghostmirror.cltsrvrpc.server.IServiceResult;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;

public class ServiceContainer implements IServiceContainer {
    private static final Logger log = Logger.getLogger(ServiceContainer.class.getCanonicalName());
    private final Properties property = new Properties();
    private final HashMap<String,IService> services = new HashMap<String,IService>();

    public IService getService(String name) {
        IService service = services.get(name);
        if(service == null) {
            return new WrongService();
        }
        return service;
    }

    public ServiceContainer(String path) throws Exception {
        log.info("Services initialization start");
        initProperties(path);
        initServices();
        log.info("Services initialization end");
    }

    private void initServices() throws Exception {
        log.info("Initializing services");
        for (String sname : property.stringPropertyNames()) {
            String cname = property.getProperty(sname);
            log.info("Loading service : " + sname + " {" + cname + "}");

            Class lclass;
            Object object;
            try {
                lclass = Class.forName(cname);
                log.info("Loaded Class : " + lclass.getCanonicalName());
            } catch (ClassNotFoundException e) {
                throw new Exception(e.getMessage());
            }
            try {
                object = lclass.newInstance();
            } catch (InstantiationException e) {
                throw new Exception(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new Exception(e.getMessage());
            }
            if (object == null) {
                throw new Exception("object == null");
            }

            services.put(sname, new Service(object));
        }
        log.info("Services are initialized");
    }

    private void initProperties(String path) throws IOException {
        log.info("Initializing properties");
        try {
            FileInputStream fis = new FileInputStream(path);
            try {
                property.load(fis);
            } finally {
                fis.close();
            }
        } catch (IOException e) {
            log.error("Server property file is absent!");
            throw e;
        }
        log.info("Properties are initialized");
    }

    private class WrongService extends Error implements IService {
        public IServiceResult invoke(String method, Object[] params) {
            return new ServiceResult(EServiceResult.WrongService);
        }
    }
}
