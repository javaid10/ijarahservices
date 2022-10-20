package com.ijarah.utils;


import com.ijarah.utils.interfaces.AbstractHelperMethods;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import org.apache.http.entity.ContentType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class FabricHelperMethods implements AbstractHelperMethods {

    public FabricHelperMethods() {
    }

    private static FabricRequestManager fabricRequestManager;
    private static DataControllerRequest dataControllerRequest;

    public FabricHelperMethods(DataControllerRequest dataControllerRequest, FabricRequestManager fabricRequestManager) {
        FabricHelperMethods.fabricRequestManager = fabricRequestManager;
        FabricHelperMethods.dataControllerRequest = dataControllerRequest;
    }

    private static AbstractHelperMethods getInternalHelper() {
        Method methodToFind = null;
        try {
            methodToFind = dataControllerRequest != null ? dataControllerRequest.getClass().getMethod("getInternalHelperMethod")
                    : fabricRequestManager.getClass().getMethod("getInternalHelperMethod");
            return (AbstractHelperMethods) methodToFind.invoke(dataControllerRequest);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Map<String, Object> convertToObjectMap(Map<String, String> map) {
        return HelperMethods.convertToObjectMap(map);
    }

    public String getAPIUserIdFromSession(DataControllerRequest dcRequest) {
        AbstractHelperMethods internalHelper = FabricHelperMethods.getInternalHelper();
        if (internalHelper == null) {
            return HelperMethods.getAPIUserIdFromSession(dcRequest);
        }
        return internalHelper.getAPIUserIdFromSession(dcRequest);
    }

    public String getAPIUserIdFromSession(FabricRequestManager requestManager) {
        return HelperMethods.getAPIUserIdFromSession(requestManager);
    }

    public void removeNullValues(Map map) {
        HelperMethods.removeNullValues(map);
    }

    public static Map<String, String> getHeaders(FabricRequestManager requestManager) {
        Map<String, String> headerMap = new HashMap();
        headerMap.put("X-Kony-Authorization", requestManager.getHeadersHandler().getHeader("X-Kony-Authorization"));
        headerMap.put("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        return headerMap;
    }

    public static Map<String, String> getHeaders(DataControllerRequest dcRequest) {
        Map<String, String> headerMap = new HashMap();
        headerMap.put("X-Kony-Authorization", dcRequest.getHeader("X-Kony-Authorization"));
        headerMap.put("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        return headerMap;
    }

    public Result callApi(DataControllerRequest dcRequest, Map inputParams, Map<String, String> headerParams, String url) throws HttpCallException {
        AbstractHelperMethods internalHelper = FabricHelperMethods.getInternalHelper();
        if (internalHelper == null) {
            return ServiceCallHelper.invokeServiceAndGetResult(dcRequest, inputParams, headerParams, url);
        }
        System.out.println("in callAPI" + url);
        return new Result();
    }

    public Result callApi(FabricRequestManager requestManager, Map<String, String> inputParams, Map<String, String> headerParams, String url) {
        AbstractHelperMethods internalHelper = FabricHelperMethods.getInternalHelper();
        if (internalHelper == null) {
            return ServiceCallHelper.invokeServiceAndGetResult(requestManager, convertToObjectMap(inputParams), convertToObjectMap(headerParams), url);
        }

        System.out.println("in callAPI" + url);
        return new Result();
    }
}
