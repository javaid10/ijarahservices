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

import static com.ijarah.utils.constants.OperationIDConstants.VOUCHER_CREATE_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;

public class CreateVoucher implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(CreateVoucher.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        try {
            if (preProcess(dataControllerRequest)) {
                return createVoucherRecordInDB(dataControllerRequest);
            } else {
                Result result = new Result();
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);
                return result;
            }
        } catch (Exception ex) {
            Result result = new Result();
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_CREATE_VOUCHER_SERVICE_FAILED_021.setErrorCode(result);
            return result;
        }
    }

    private boolean preProcess(DataControllerRequest dataControllerRequest) {
        try {
            if (IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("retailerID"))
                    || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("retailerName"))
                    || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("commissionRate"))) {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return true;
    }

    private Result createVoucherRecordInDB(DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();
            inputParams.put("retailerID", dataControllerRequest.getParameter("retailerID"));
            inputParams.put("retailerName", dataControllerRequest.getParameter("retailerName"));
            inputParams.put("commissionRate", dataControllerRequest.getParameter("commissionRate"));

            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, VOUCHER_CREATE_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_CREATE_VOUCHER_SERVICE_FAILED_021.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_CREATE_VOUCHER_SERVICE_FAILED_021.setErrorCode(result);
            return result;
        }
    }
}
