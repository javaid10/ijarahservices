package com.ijarah.utils.interfaces;

import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

import java.util.Map;

public interface AbstractInvocationWrapper {
    public Result invokeServiceAndGetResult(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, DataControllerRequest dataControllerRequest) throws Exception;

    public Result invokeServiceAndGetResult(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, FabricRequestManager fabricRequestManager) throws Exception;

    public Result invokeServiceAndGetResult(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, String konyAuthToken) throws Exception;

    public String invokeServiceAndGetJSON(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, DataControllerRequest dataControllerRequest) throws Exception;

    public String invokeServiceAndGetJSON(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, FabricRequestManager fabricRequestManager) throws Exception;

    public String invokeServiceAndGetJSON(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, String konyAuthToken) throws Exception;

    public String invokePassThroughServiceAndGetString(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, DataControllerRequest dataControllerRequest) throws Exception;

    public String invokePassThroughServiceAndGetString(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, FabricRequestManager fabricRequestManager) throws Exception;

    public String invokePassThroughServiceAndGetString(String serviceId, String objectId, String operationId, Map<String, Object> requestParameters, Map<String, Object> requestHeaders, String konyAuthToken) throws Exception;
}