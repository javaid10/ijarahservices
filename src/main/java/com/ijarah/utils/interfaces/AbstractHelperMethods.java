package com.ijarah.utils.interfaces;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

import java.util.Map;

public interface AbstractHelperMethods {

    public String getAPIUserIdFromSession(DataControllerRequest dcRequest);

    public String getAPIUserIdFromSession(FabricRequestManager requestManager);

    public void removeNullValues(Map map);

    public Result callApi(DataControllerRequest dcRequest, Map inputParams, Map<String, String> headerParams, String url) throws HttpCallException;

    public Result callApi(FabricRequestManager requestManager, Map<String, String> inputParams, Map<String, String> headerParams, String url);
}