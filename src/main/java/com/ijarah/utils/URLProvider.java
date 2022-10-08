package com.ijarah.utils;

import com.ijarah.utils.constants.ServiceIDConstants;
import com.konylabs.middleware.common.URLProvider2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.registry.vo.Service;

import java.util.Map;

import static com.ijarah.utils.constants.OperationIDConstants.SAMPLE_OPERATION_ID;


public class URLProvider implements URLProvider2 {
    @Override
    public String execute(String string, Service service, Map map, DataControllerRequest dataControllerRequest, Map map1) {

        String BaseURL = "";
        String endPoint = "";

        final String SERVICE_ID = "current_appID";
        final String OPERATION_ID = "current_serviceID";

        String serviceID = dataControllerRequest.getParameterValues(SERVICE_ID)[0];
        switch (serviceID) {
            case ServiceIDConstants.SAMPLE_SERVICE_ID:
                BaseURL = "https://www.google.com";
                break;
            default:
                BaseURL = "https://dummy.com";
                break;
        }

        String operationID = dataControllerRequest.getParameterValues(OPERATION_ID)[0];
        switch (operationID) {
            case SAMPLE_OPERATION_ID:
                endPoint = "https://www.google.com";
                break;
            default:
                endPoint = "https://dummy.com";
                break;
        }

        return BaseURL + endPoint;
    }
}