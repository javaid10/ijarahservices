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
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.constants.OperationIDConstants.SP_GET_VOUCHER_DETAILS_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;

public class GetVoucherDetails implements JavaService2 {
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        try {
            String customerID = String.valueOf(dataControllerRequest.getSession().getAttribute("user_id"));
            if (StringUtils.isNotBlank(customerID)) {
                return getVoucherDetails(customerID, dataControllerRequest);
            } else {
                Result result = new Result();
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_GET_USER_ID_FAILED_019.setErrorCode(result);
                return result;
            }
        } catch (Exception ex) {
            Result result = new Result();
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_GET_VOUCHER_DETAILS_SERVICE_FAILED_018.setErrorCode(result);
            return result;
        }
    }

    private Result getVoucherDetails(String customerID, DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();
            inputParams.put("customerID", customerID);
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_VOUCHER_DETAILS_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_GET_USER_ID_FAILED_020.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_GET_USER_ID_FAILED_019.setErrorCode(result);
            return result;
        }
    }
}