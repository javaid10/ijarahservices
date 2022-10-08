package com.ijarah.utils;


import com.ijarah.utils.interfaces.AbstractInvocationWrapper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class DBPServiceInvocationWrapper implements AbstractInvocationWrapper {
    private static FabricRequestManager fabricRequestManager;
    private static DataControllerRequest dataControllerRequest;

    public DBPServiceInvocationWrapper(DataControllerRequest dataControllerRequest, FabricRequestManager fabricRequestManager) {
        DBPServiceInvocationWrapper.fabricRequestManager = fabricRequestManager;
        DBPServiceInvocationWrapper.dataControllerRequest = dataControllerRequest;
    }

    private static AbstractInvocationWrapper getInvocationWrapper() {
        Method methodToFind = null;
        try {
            methodToFind = dataControllerRequest != null ? dataControllerRequest.getClass().getMethod("getInvocationWrapper")
                    : fabricRequestManager.getClass().getMethod("getInvocationWrapper");
            return (AbstractInvocationWrapper) methodToFind.invoke(dataControllerRequest);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Result invokeServiceAndGetResult(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, DataControllerRequest dataControllerRequest) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokeServiceAndGetResult(serviceId, objectId, operationId, requestParameters, requestHeaders, dataControllerRequest);
        }
        return invocationWrapper.invokeServiceAndGetResult(serviceId, objectId, operationId, requestParameters, requestHeaders, dataControllerRequest);

    }

    public Result invokeServiceAndGetResult(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, FabricRequestManager fabricRequestManager) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokeServiceAndGetResult(serviceId, objectId, operationId, requestParameters, requestHeaders, fabricRequestManager);
        }
        return invocationWrapper.invokeServiceAndGetResult(serviceId, objectId, operationId, requestParameters, requestHeaders, dataControllerRequest);
    }

    public Result invokeServiceAndGetResult(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, String konyAuthToken) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokeServiceAndGetResult(serviceId, objectId, operationId, requestParameters, requestHeaders, konyAuthToken);
        }
        return invocationWrapper.invokeServiceAndGetResult(serviceId, objectId, operationId, requestParameters, requestHeaders, konyAuthToken);
    }

    public String invokeServiceAndGetJSON(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, DataControllerRequest dataControllerRequest) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceId, objectId, operationId, requestParameters, requestHeaders, dataControllerRequest);
        }
        return invocationWrapper.invokeServiceAndGetJSON(serviceId, objectId, operationId, requestParameters, requestHeaders, dataControllerRequest);
    }

    public String invokeServiceAndGetJSON(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, FabricRequestManager fabricRequestManager) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceId, objectId, operationId, requestParameters, requestHeaders, fabricRequestManager);
        }
        return invocationWrapper.invokeServiceAndGetJSON(serviceId, objectId, operationId, requestParameters, requestHeaders, fabricRequestManager);
    }

    public String invokeServiceAndGetJSON(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, String konyAuthToken) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceId, objectId, operationId, requestParameters, requestHeaders, konyAuthToken);
        }
        return invocationWrapper.invokeServiceAndGetJSON(serviceId, objectId, operationId, requestParameters, requestHeaders, konyAuthToken);
    }

    public String invokePassThroughServiceAndGetString(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, DataControllerRequest dataControllerRequest) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceId, objectId, operationId, requestParameters, requestHeaders, dataControllerRequest);
        }

        return invocationWrapper.invokeServiceAndGetJSON(serviceId, objectId, operationId, requestParameters, requestHeaders, dataControllerRequest);
    }

    public String invokePassThroughServiceAndGetString(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, FabricRequestManager fabricRequestManager) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokePassThroughServiceAndGetString(serviceId, objectId, operationId, requestParameters, requestHeaders, fabricRequestManager);
        }
        return invocationWrapper.invokePassThroughServiceAndGetString(serviceId, objectId, operationId, requestParameters, requestHeaders, fabricRequestManager);

    }

    public String invokePassThroughServiceAndGetString(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, String konyAuthToken) throws Exception {
        AbstractInvocationWrapper invocationWrapper = DBPServiceInvocationWrapper.getInvocationWrapper();
        if (DBPServiceInvocationWrapper.getInvocationWrapper() == null) {
            return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokePassThroughServiceAndGetString(serviceId, objectId, operationId, requestParameters, requestHeaders, konyAuthToken);
        }
        return invocationWrapper.invokePassThroughServiceAndGetString(serviceId, objectId, operationId, requestParameters, requestHeaders, konyAuthToken);
    }

    //TODO: complete those method for local usage
    public byte[] invokePassThroughServiceAndGetBytes(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, DataControllerRequest dataControllerRequest) throws Exception {
        return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokePassThroughServiceAndGetBytes(serviceId, objectId, operationId, requestParameters, requestHeaders, dataControllerRequest);
    }

    public byte[] invokePassThroughServiceAndGetBytes(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, FabricRequestManager fabricRequestManager) throws Exception {
        return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokePassThroughServiceAndGetBytes(serviceId, objectId, operationId, requestParameters, requestHeaders, fabricRequestManager);
    }

    public byte[] invokePassThroughServiceAndGetBytes(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, String konyAuthToken) throws Exception {
        return com.dbp.core.fabric.extn.DBPServiceInvocationWrapper.invokePassThroughServiceAndGetBytes(serviceId, objectId, operationId, requestParameters, requestHeaders, konyAuthToken);
    }
}
