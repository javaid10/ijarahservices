package com.ijarah.services;

import com.emdha.esign.eSignEmdha;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class EmdhaSign implements JavaService2 {

    Map<String, String> inputParams = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(EmdhaSign.class);


    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

        if (preProcess(inputParams)) {

        }
        return result;
    }

    private boolean preProcess(Map<String, String> inputParams) {
        try {
            if (!inputParams.isEmpty()) {
                if (IjarahHelperMethods.isBlank(inputParams.get("type"))
                        || IjarahHelperMethods.isBlank(inputParams.get("nationalId"))
                        || IjarahHelperMethods.isBlank(inputParams.get("applicationId"))) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }

        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return false;
    }
}
