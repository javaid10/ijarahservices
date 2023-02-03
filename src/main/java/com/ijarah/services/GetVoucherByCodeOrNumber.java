package com.ijarah.services;

import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
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

import static com.ijarah.utils.constants.OperationIDConstants.SP_GET_VOUCHER_DETAILS_BY_MOBILE_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.SP_GET_VOUCHER_DETAILS_BY_VOUCHER_CODE_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.RETAILER_DB_2_SERVICE_ID;

public class GetVoucherByCodeOrNumber implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(GetVoucherByCodeOrNumber.class);
    String FLAG;

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        try {
            if (preProcess(dataControllerRequest)) {
                return getVoucherDetails(dataControllerRequest.getParameter("input_value"), dataControllerRequest.getParameter("input_value_type"), dataControllerRequest);
            } else {
                Result result = new Result();
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);
                return result;
            }
        } catch (Exception ex) {
            Result result = new Result();
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_GET_VOUCHER_DETAILS_SERVICE_FAILED_018.setErrorCode(result);
            return result;
        }
    }

    private boolean preProcess(DataControllerRequest dataControllerRequest) {
        try {
            if (IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("input_value"))
                    || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("input_value_type"))) {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return true;
    }

    private Result getVoucherDetails(String input_value, String input_value_type, DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();

            if (input_value_type.equalsIgnoreCase("MOBILE")) {
                inputParams.put("mobile", input_value);
                result = ServiceCaller.internalDB(RETAILER_DB_2_SERVICE_ID, SP_GET_VOUCHER_DETAILS_BY_MOBILE_OPERATION_ID, inputParams, null, dataControllerRequest);
            } else {
                inputParams.put("voucherCode", input_value);
                result = ServiceCaller.internalDB(RETAILER_DB_2_SERVICE_ID, SP_GET_VOUCHER_DETAILS_BY_VOUCHER_CODE_OPERATION_ID, inputParams, null, dataControllerRequest);
            }
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_NO_VOUCHER_FOUND_024.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_NO_VOUCHER_FOUND_024.setErrorCode(result);
            return result;
        }
    }
}