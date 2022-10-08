package com.ijarah.services;

import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.constants.OperationIDConstants.CUSTOMER_APPLICATION_GET_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;

public class CreateLoan implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(CreateLoan.class);
    Map<String, String> inputParams = new HashMap<>();
    private String NATIONAL_ID = "";
    private String SID_PRO_ACTIVE = "SID_PRO_ACTIVE";
    private String APPLICATION_STATUS = "";
    private String CSA_STATUS = "";
    private String SANAD_STATUS = "";

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

        try {
            if (preProcess(inputParams)) {
                Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);
                extractValuesFromCustomerApplication(getCustomerApplicationData);
                return null;
            }
        } catch (Exception ex) {
            LOG.error("ERROR invoke :: " + ex);
        }
        return result;
    }

    private boolean preProcess(Map<String, String> inputParams) {
        try {

        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return !inputParams.isEmpty();
    }

    private Result getCustomerApplicationData(DataControllerRequest dataControllerRequest) {
        Result getCustomerApplicationData = StatusEnum.error.setStatus();
        try {
            getCustomerApplicationData = StatusEnum.success.setStatus();
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "applicationID" + DBPUtilitiesConstants.EQUAL + NATIONAL_ID);
            getCustomerApplicationData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, CUSTOMER_APPLICATION_GET_OPERATION_ID, filter, null, dataControllerRequest));
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerApplicationData :: " + ex);
        }
        return getCustomerApplicationData;
    }

    private void extractValuesFromCustomerApplication(Result getCustomerApplicationData) {
        try {
            APPLICATION_STATUS = HelperMethods.getFieldValue(getCustomerApplicationData, "applicationStatus");

            if (APPLICATION_STATUS.equalsIgnoreCase(SID_PRO_ACTIVE)) {

                CSA_STATUS = HelperMethods.getFieldValue(getCustomerApplicationData, "csaApproval");
                SANAD_STATUS = HelperMethods.getFieldValue(getCustomerApplicationData, "sanadApproval");

                if (CSA_STATUS.equalsIgnoreCase("true") && SANAD_STATUS.equalsIgnoreCase("true")) {
                    //Activate Customer
                    //Create loan
                    //Nafaes transfer
                    //Sell order has to be created
                }
            }
        } catch (Exception ex) {
            LOG.error("ERROR extractValuesFromCustomerApplication :: " + ex);
        }
    }
}
