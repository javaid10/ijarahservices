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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;

public class CheckCustomerValidity implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(CheckCustomerValidity.class);
    boolean canCustomerApply;
    String message, reApplyDate;

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        Result result = new Result();
        result.addParam("ResponseCode", "ERR_60000");
        canCustomerApply = false;
        message = "Customer can apply";
        reApplyDate = "";

        if (preProcess(dataControllerRequest)) {
            Result getReApplyDate = getReApplyDate(dataControllerRequest);
            if (HelperMethods.hasRecords(getReApplyDate)) {
                if (isReapplyDateValid(HelperMethods.getFieldValue(getReApplyDate, "reApplyDate"))) {
                    canCustomerApply = false;
                    reApplyDate = HelperMethods.getFieldValue(getReApplyDate, "reApplyDate");
                    result.addParam("reApplyDate", reApplyDate);
                    return result;
                } else {
                    canCustomerApply = true;
                }
            } else {
                canCustomerApply = true;
            }
            Result getCustomerApplicationJourneyDate = getCustomerApplicationJourneyDate(dataControllerRequest);
            if (HelperMethods.hasRecords(getCustomerApplicationJourneyDate)) {
                if (isReapplyDateValid(HelperMethods.getFieldValue(getCustomerApplicationJourneyDate, "reApplyCustomerApplicationJourney"))) {
                    canCustomerApply = false;
                    reApplyDate = HelperMethods.getFieldValue(getCustomerApplicationJourneyDate, "reApplyCustomerApplicationJourney");
                    result.addParam("reApplyDate", reApplyDate);
                    return result;
                } else {
                    canCustomerApply = true;
                }
            } else {
                canCustomerApply = true;
            }

            Result getSanadIterationDate = getSanadIterationDate(dataControllerRequest);
            if (HelperMethods.hasRecords(getSanadIterationDate)) {
                if (isReapplyDateValid(HelperMethods.getFieldValue(getSanadIterationDate, "retrySanadSignDate"))) {
                    canCustomerApply = false;
                    reApplyDate = HelperMethods.getFieldValue(getSanadIterationDate, "retrySanadSignDate");
                    result.addParam("reApplyDate", reApplyDate);
                    return result;
                } else {
                    canCustomerApply = true;
                }
            } else {
                canCustomerApply = true;
            }
        }
        result.addParam("StatusMessage", message);
        return result;
    }

    private boolean preProcess(DataControllerRequest dataControllerRequest) {
        try {
            if (IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("nationalId"))) {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return true;
    }

    private Result getReApplyDate(DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();

            inputParams.put("nationalId", dataControllerRequest.getParameter("nationalId"));
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_REAPPLY_DATE_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_GET_REAPPLY_DATE_SERVICE_FAILED_038.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_GET_REAPPLY_DATE_SERVICE_FAILED_038.setErrorCode(result);
            return result;
        }
    }

    private Result getCustomerApplicationJourneyDate(DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();

            inputParams.put("nationalId", dataControllerRequest.getParameter("nationalId"));
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_CUSTOMER_APPLICATION_JOURNEY_DATE_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_GET_CUSTOMER_APPLICATION_SERVICE_FAILED_039.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_GET_CUSTOMER_APPLICATION_SERVICE_FAILED_039.setErrorCode(result);
            return result;
        }
    }

    private Result getSanadIterationDate(DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();

            inputParams.put("nationalId", dataControllerRequest.getParameter("nationalId"));
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_SANAD_ITERATION_DATE_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_GET_SANAD_ITERATION_SERVICE_FAILED_040.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_GET_SANAD_ITERATION_SERVICE_FAILED_040.setErrorCode(result);
            return result;
        }
    }

    public static boolean isReapplyDateValid(String reApplyDate) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate = dateFormat.parse(reApplyDate);
        Date currentDate = new Date();
        return inputDate.compareTo(currentDate) >= 0;
    }
}