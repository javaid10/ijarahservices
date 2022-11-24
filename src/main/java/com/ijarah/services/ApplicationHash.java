package com.ijarah.services;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

import static com.ijarah.utils.enums.EnvironmentConfig.APP_HASH_VALUE;

public class ApplicationHash implements JavaService2 {
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        Result hashResult = new Result();
        hashResult.addParam("Hash", APP_HASH_VALUE.getValue(dataControllerRequest));
        return hashResult;
    }
}