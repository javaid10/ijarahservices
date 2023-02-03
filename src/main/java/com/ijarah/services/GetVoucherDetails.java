package com.ijarah.services;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
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
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.constants.OperationIDConstants.SP_GET_VOUCHER_DETAILS_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;

public class GetVoucherDetails implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(GetVoucherDetails.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        try {
            if (preProcess(dataControllerRequest)) {
                //String customerID = String.valueOf(dataControllerRequest.getSession().getAttribute("user_id"));
                String customerID = fetchCustomerIdByNationalID(dataControllerRequest);
                if (StringUtils.isNotBlank(customerID)) {
                    return getVoucherDetails(customerID, dataControllerRequest);
                } else {
                    Result result = new Result();
                    StatusEnum.error.setStatus(result);
                    IjarahErrors.ERR_GET_USER_ID_FAILED_019.setErrorCode(result);
                    return result;
                }
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
            if (IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("nationalId"))) {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return true;
    }

    private String fetchCustomerIdByNationalID(DataControllerRequest dataControllerRequest) throws DBPApplicationException {

        HashMap<String, Object> imap = new HashMap();
        imap.put("$filter", "UserName eq " + dataControllerRequest.getParameter("nationalId"));
        String customerId = "";

        String res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices").withOperationId("dbxdb_customer_get").withRequestParameters(imap).build().getResponse();

        JSONObject JsonResponse = new JSONObject(res);
        if (JsonResponse.has("customer")) {
            if (JsonResponse.getJSONArray("customer").length() > 0) {
                customerId = JsonResponse.getJSONArray("customer").getJSONObject(0).getString("id");
            }
        }
        return customerId;
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