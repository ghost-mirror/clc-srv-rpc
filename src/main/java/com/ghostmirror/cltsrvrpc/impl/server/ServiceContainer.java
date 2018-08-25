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
//    private static final Logger log = Logger.getLogger(ServiceContainer.class.getCanonicalName());
    private static final Logger log = Logger.getLogger("Server");
    private final Properties property = new Properties();
    private final HashMap<String,IService> services = new HashMap<>();

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
        for (String key : property.stringPropertyNames()) {
            String value = property.getProperty(key);
            log.info("Loading service : " + key + " {" + value + "}");

            Class ServiceClass;
            Object object;
            try {
                ServiceClass = Class.forName(value);
                log.info("Loaded Class : " + ServiceClass.getCanonicalName());
            } catch (ClassNotFoundException e) {
                throw new Exception(e.getMessage());
            }
            try {
                object = ServiceClass.newInstance();
            } catch (InstantiationException|IllegalAccessException e) {
                throw new Exception(e.getMessage());
            }
            if (object == null) {
                throw new Exception("object == null");
            }

            services.put(key, new Service(object));
        }
        log.info("Services are initialized");
    }

    private void initProperties(String path) throws IOException {
        log.info("Initializing properties");
        try {

            try(FileInputStream fis = new FileInputStream(path)) {
                property.load(fis);
            }
        } catch (IOException e) {
            log.error("Server property file is absent!");
            throw e;
        }
        log.info("Properties are initialized");
    }

    private static class WrongService extends Error implements IService {
        public IServiceResult invoke(String method, Object[] params) {
            return new ServiceResult(EServiceResult.WrongService);
        }
    }
}
