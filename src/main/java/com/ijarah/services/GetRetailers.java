package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ijarah.Model.Retailers.GetRetailersResponse;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.GET_RETAILERS_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.MURABAHA_T24_JSON_SERVICE_ID;


public class GetRetailers implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(GetRetailers.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        Result result = new Result();
        StatusEnum.error.setStatus(result);
        IjarahErrors.ERR_GET_RETAILER_SERVICE_FAILED_022.setErrorCode(result);
        try {
            Result getRetailers = getRetailers(dataControllerRequest);
            if (IjarahHelperMethods.hasSuccessStatus(getRetailers)) {
                Gson gsonS2 = new Gson();
                GetRetailersResponse getRetailersResponse = gsonS2.fromJson(ResultToJSON.convert(getRetailers), GetRetailersResponse.class);
                if (getRetailersResponse != null) {
                    if (getRetailersResponse.getHeader() != null) {
                        if (getRetailersResponse.getHeader().getStatus() != null && getRetailersResponse.getHeader().getStatus().equalsIgnoreCase("success")) {
                            Result successResult = new Result();
                            StatusEnum.success.setStatus(successResult);
                            successResult.addDataset(getRetailers.getDatasetById("body"));
                            successResult.addParam("ResponseCode", "ERR_60000");
                            return successResult;
                        }
                    }
                }
            } else {
                return result;
            }
        } catch (Exception ex) {
            return result;
        }
        IjarahErrors.ERR_NO_RETAILER_FOUND_023.setErrorCode(result);
        return result;
    }

    private Result getRetailers(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParams = new HashMap<>();
            Result getRetailers = ServiceCaller.internal(MURABAHA_T24_JSON_SERVICE_ID, GET_RETAILERS_OPERATION_ID, inputParams, null, dataControllerRequest);

            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getRetailers);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MURABAHA_T24_JSON_SERVICE_ID + " : " + GET_RETAILERS_OPERATION_ID);

            if (IjarahHelperMethods.hasSuccessCode(getRetailers)) {
                StatusEnum.success.setStatus(getRetailers);
                return getRetailers;
            }
        } catch (Exception ex) {
            LOG.error("ERROR getRetailers :: " + ex);
            return result;
        }
        return result;
    }
}