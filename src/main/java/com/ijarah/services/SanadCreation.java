package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.*;
import static com.ijarah.utils.enums.EnvironmentConfig.SANAD_ACCESS_TOKEN_AUTHORIZATION;
import static com.ijarah.utils.enums.EnvironmentConfig.SIGN_SECRET;

public class SanadCreation implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(SanadCreation.class);
    private Map<String, String> inputParams = new HashMap<>();

    private String CLIENT_CREDENTIALS = "client_credentials";
    private String READ_WRITE = "read write";
    private String NATIONAL_ID = "";
    private String ACCESS_TOKEN = "";
    private String LOAN_AMOUNT = "";
    private String PHONE_NUMBER = "";
    private String APPLICATION_ID = "";
    private String CUSTOMER_ID = "";

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

        if (preProcess(inputParams, dataControllerRequest)) {

            Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);
            extractValuesFromCustomerApplicationData(getCustomerApplicationData);

            Result getCustomerCommunicationData = getCustomerCommunicationData(dataControllerRequest);
            extractValuesFromCustomerCommunicationData(getCustomerCommunicationData);

            Result getAccessTokenServiceResult = callAccessTokenCreationService(createRequestForAccessTokenCreationService(), dataControllerRequest);
            extractValuesFromAccessTokenService(getAccessTokenServiceResult);

            Result getCreateSingleSanadServiceResult = callCreateSingleSanadService(createRequestForCreateSingleSanadService(), dataControllerRequest);
            extractValuesFromCreateSingleSanadService(getAccessTokenServiceResult);
        }

        return null;
    }

    private boolean preProcess(Map<String, String> inputParams, DataControllerRequest request) {
        LOG.error("preProcess");
        NATIONAL_ID = inputParams.get("national_id");
        return !this.inputParams.isEmpty();
    }

    private Result getCustomerApplicationData(DataControllerRequest dataControllerRequest) {
        Result getCustomerApplicationData = StatusEnum.error.setStatus();
        try {
            getCustomerApplicationData = StatusEnum.success.setStatus();
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "nationalId" + DBPUtilitiesConstants.EQUAL + NATIONAL_ID);
            getCustomerApplicationData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
                    CUSTOMER_APPLICATION_GET_OPERATION_ID, filter, null, dataControllerRequest));
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerApplicationData :: " + ex);
        }
        return getCustomerApplicationData;
    }

    private void extractValuesFromCustomerApplicationData(Result getCustomerApplicationData) {
        try {
            if (getCustomerApplicationData.hasParamByName("applicationID")
                    && getCustomerApplicationData.hasParamByName("loanAmount")
                    && IjarahHelperMethods.hasSuccessCode(getCustomerApplicationData)) {

                if (!(IjarahHelperMethods.isBlank(HelperMethods.getFieldValue(getCustomerApplicationData, "applicationID"))
                        && IjarahHelperMethods.isBlank(HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount")))) {

                    APPLICATION_ID = HelperMethods.getFieldValue(getCustomerApplicationData, "applicationID");
                    LOAN_AMOUNT = HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount");
                }
            }
        } catch (Exception ex) {
            LOG.error("ERROR extractValuesFromCustomerApplication :: " + ex);
        }
    }

    private Result getCustomerCommunicationData(DataControllerRequest dataControllerRequest) {
        Result getCustomerCommunicationData = StatusEnum.error.setStatus();
        try {
            getCustomerCommunicationData = StatusEnum.success.setStatus();
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "Customer_id"
                    + DBPUtilitiesConstants.EQUAL
                    + CUSTOMER_ID
                    + DBPUtilitiesConstants.AND
                    + "Type_id"
                    + DBPUtilitiesConstants.EQUAL
                    + "COMM_TYPE_PHONE");
            getCustomerCommunicationData.appendResult(ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID,
                    CUSTOMER_COMMUNICATION_GET_OPERATION_ID, filter, null, dataControllerRequest));
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerCommunicationData :: " + ex);
        }
        return getCustomerCommunicationData;
    }

    private void extractValuesFromCustomerCommunicationData(Result getCustomerCommunicationData) {
        try {
            if (getCustomerCommunicationData.hasParamByName("Value")
                    && IjarahHelperMethods.hasSuccessCode(getCustomerCommunicationData)
                    && !(IjarahHelperMethods.isBlank(HelperMethods.getFieldValue(getCustomerCommunicationData, "Value")))) {
                PHONE_NUMBER = HelperMethods.getFieldValue(getCustomerCommunicationData, "Value");
            }
        } catch (Exception ex) {
            LOG.error("ERROR extractValuesFromCustomerCommunicationData :: " + ex);
        }
    }

    private Map<String, String> createRequestForAccessTokenCreationService() {
        Map<String, String> inputParams = new HashMap<>();
        inputParams.put("grant_type", CLIENT_CREDENTIALS);
        inputParams.put("scope", READ_WRITE);

        return inputParams;
    }

    private Result callAccessTokenCreationService(Map<String, String> inputParams,
                                                  DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {

            dataControllerRequest.getHeaderMap().put("Content-Type", "application/x-www-form-urlencoded");
            dataControllerRequest.getHeaderMap().put("Authorization", SANAD_ACCESS_TOKEN_AUTHORIZATION.getValue(dataControllerRequest));

            Result getAccessTokenServiceResult = StatusEnum.success.setStatus();
            getAccessTokenServiceResult.appendResult(ServiceCaller.internal(SANAD_CREATION_SERVICE_ID,
                    GET_ACCESS_TOKEN_OPERATION_ID, inputParams, null, dataControllerRequest));

            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getAccessTokenServiceResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    SANAD_CREATION_SERVICE_ID + " : " + GET_ACCESS_TOKEN_OPERATION_ID);

            return getAccessTokenServiceResult;
        } catch (Exception ex) {
            LOG.error("ERROR callAccessTokenCreationService :: " + ex);
        }
        return result;
    }

    private void extractValuesFromAccessTokenService(Result getAccessTokenServiceResult) {
        if (IjarahHelperMethods.hasSuccessCode(getAccessTokenServiceResult)
                && !IjarahHelperMethods.isBlank(getAccessTokenServiceResult.getParamValueByName("access_token"))) {
            ACCESS_TOKEN = getAccessTokenServiceResult.getParamValueByName("access_token");
        }
    }

    private Map<String, String> createRequestForCreateSingleSanadService() {

        Map<String, String> inputParams = new HashMap<>();

        inputParams.put("national_id", NATIONAL_ID);
        inputParams.put("city_of_issuance", "1");
        inputParams.put("debtor_phone_number", PHONE_NUMBER);
        inputParams.put("total_value", LOAN_AMOUNT);
        inputParams.put("currency", "SAR");
        inputParams.put("max_approve_duration", "1320");
        inputParams.put("reference_id", APPLICATION_ID);
        inputParams.put("due_type", "upon request");

        return inputParams;
    }

    private String createNafithSignature(DataControllerRequest dataControllerRequest) {
        String X_Nafith_Signature = "";
        String signSecret = SIGN_SECRET.getValue(dataControllerRequest);
        String data = "{}";

        return X_Nafith_Signature;
    }

    private Result callCreateSingleSanadService(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {

        Result result = StatusEnum.error.setStatus();
        try {
            dataControllerRequest.getHeaderMap().put("Content-Type", "application/json");
            dataControllerRequest.getHeaderMap().put("X-Nafith-Timestamp", String.valueOf(Instant.now().getEpochSecond()));
            dataControllerRequest.getHeaderMap().put("X-Nafith-Tracking-Id", "145");
            dataControllerRequest.getHeaderMap().put("X-Nafith-Signature", createNafithSignature(dataControllerRequest));

            Result getCreateSingleSanadServiceResult = StatusEnum.success.setStatus();
            getCreateSingleSanadServiceResult.appendResult(ServiceCaller.internal(SANAD_CREATION_SERVICE_ID, CREATE_SINGLE_SANAD_OPERATION_ID,
                    inputParams, null, dataControllerRequest));

            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getCreateSingleSanadServiceResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    SANAD_CREATION_SERVICE_ID + " : " + CREATE_SINGLE_SANAD_OPERATION_ID);

            return getCreateSingleSanadServiceResult;
        } catch (Exception ex) {
            LOG.error("ERROR callCreateSingleSanadService :: " + ex);
        }
        return result;

    }

    private void extractValuesFromCreateSingleSanadService(Result getAccessTokenServiceResult) {
        // create a table "nafaith_sanad"
        // and call that service
    }
}
