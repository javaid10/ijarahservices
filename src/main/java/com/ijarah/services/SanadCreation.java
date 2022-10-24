package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijarah.Model.NafaithSignatureData.Debtor;
import com.ijarah.Model.NafaithSignatureData.NafaithSignatureData;
import com.ijarah.Model.NafaithSignatureData.SanadItem;
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
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.log4j.Logger;

import java.time.Instant;
import java.util.*;

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

        if (preProcess(inputParams)) {

            Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);
            if (IjarahHelperMethods.hasSuccessCode(getCustomerApplicationData) && HelperMethods.hasRecords(getCustomerApplicationData)) {

                extractValuesFromCustomerApplicationData(getCustomerApplicationData);
                Result getCustomerCommunicationData = getCustomerCommunicationData(dataControllerRequest);

                if (IjarahHelperMethods.hasSuccessCode(getCustomerCommunicationData) && HelperMethods.hasRecords(getCustomerCommunicationData)) {

                    extractValuesFromCustomerCommunicationData(getCustomerCommunicationData);
                    Result getAccessTokenServiceResult = callAccessTokenCreationService(createRequestForAccessTokenCreationService(), dataControllerRequest);

                    if (IjarahHelperMethods.hasSuccessCode(getAccessTokenServiceResult) && HelperMethods.hasRecords(getAccessTokenServiceResult)) {

                        extractValuesFromAccessTokenService(getAccessTokenServiceResult);
                        Result getCreateSingleSanadServiceResult = callCreateSingleSanadService(createRequestForCreateSingleSanadService(), dataControllerRequest);

                        if (IjarahHelperMethods.hasSuccessCode(getCreateSingleSanadServiceResult) && HelperMethods.hasRecords(getCreateSingleSanadServiceResult)) {

                            extractValuesFromCreateSingleSanadService(getCreateSingleSanadServiceResult);
                            Result createNafaithSanadData = createNafaithSanadData(createRequestForCreateNafaithService(), dataControllerRequest);

                            if (IjarahHelperMethods.hasSuccessCode(createNafaithSanadData) && HelperMethods.hasRecords(createNafaithSanadData)) {
                                StatusEnum.success.setStatus(result);
                            } else {
                                IjarahErrors.ERR_CREATE_NAFAITH_RECORD_FAILED_013.setErrorCode(result);
                            }
                        } else {
                            IjarahErrors.ERR_SINGLE_SANAD_CREATION_FAILED_012.setErrorCode(result);
                        }
                    } else {
                        IjarahErrors.ERR_GET_ACCESS_TOKEN_FAILED_011.setErrorCode(result);
                    }
                } else {
                    IjarahErrors.ERR_CUSTOMER_COMMUNICATION_DATA_NOT_FOUND_010.setErrorCode(result);
                }
            } else {
                IjarahErrors.ERR_CUSTOMER_APPLICATION_DATA_NOT_FOUND_009.setErrorCode(result);
            }
        }
        return result;
    }

    private boolean preProcess(Map<String, String> inputParams) {
        LOG.error("preProcess");
        NATIONAL_ID = inputParams.get("national_id");
        return !this.inputParams.isEmpty();
    }

    private Result getCustomerApplicationData(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "nationalId" + DBPUtilitiesConstants.EQUAL + NATIONAL_ID);
            Result getCustomerApplicationData = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, CUSTOMER_APPLICATION_GET_OPERATION_ID, filter, null, dataControllerRequest);
            StatusEnum.success.setStatus(getCustomerApplicationData);
            return getCustomerApplicationData;
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerApplicationData :: " + ex);
        }
        return result;
    }

    private void extractValuesFromCustomerApplicationData(Result getCustomerApplicationData) {
        try {
            if (getCustomerApplicationData.hasParamByName("applicationID") && getCustomerApplicationData.hasParamByName("loanAmount")) {

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
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "Customer_id"
                    + DBPUtilitiesConstants.EQUAL
                    + CUSTOMER_ID
                    + DBPUtilitiesConstants.AND
                    + "Type_id"
                    + DBPUtilitiesConstants.EQUAL
                    + "COMM_TYPE_PHONE");
            Result getCustomerCommunicationData = ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID,
                    CUSTOMER_COMMUNICATION_GET_OPERATION_ID, filter, null, dataControllerRequest);
            StatusEnum.success.setStatus(getCustomerCommunicationData);
            return getCustomerCommunicationData;
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerCommunicationData :: " + ex);
        }
        return result;
    }

    private void extractValuesFromCustomerCommunicationData(Result getCustomerCommunicationData) {
        try {
            if (getCustomerCommunicationData.hasParamByName("Value")
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

            Result getAccessTokenServiceResult = ServiceCaller.internal(SANAD_CREATION_SERVICE_ID,
                    GET_ACCESS_TOKEN_OPERATION_ID, inputParams, null, dataControllerRequest);

            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getAccessTokenServiceResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    SANAD_CREATION_SERVICE_ID + " : " + GET_ACCESS_TOKEN_OPERATION_ID);

            StatusEnum.success.setStatus(getAccessTokenServiceResult);
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

    private Result callCreateSingleSanadService(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {

        Result result = StatusEnum.error.setStatus();
        try {
            dataControllerRequest.getHeaderMap().put("Content-Type", "application/json");
            dataControllerRequest.getHeaderMap().put("X-Nafith-Timestamp", String.valueOf(Instant.now().getEpochSecond()));
            dataControllerRequest.getHeaderMap().put("X-Nafith-Tracking-Id", "145");
            dataControllerRequest.getHeaderMap().put("X-Nafith-Signature", createNafithSignature(dataControllerRequest));

            Result getCreateSingleSanadServiceResult = ServiceCaller.internal(SANAD_CREATION_SERVICE_ID, CREATE_SINGLE_SANAD_OPERATION_ID,
                    inputParams, null, dataControllerRequest);

            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getCreateSingleSanadServiceResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    SANAD_CREATION_SERVICE_ID + " : " + CREATE_SINGLE_SANAD_OPERATION_ID);
            StatusEnum.success.setStatus(getCreateSingleSanadServiceResult);
            return getCreateSingleSanadServiceResult;
        } catch (Exception ex) {
            LOG.error("ERROR callCreateSingleSanadService :: " + ex);
        }
        return result;

    }

    private String createNafithSignature(DataControllerRequest dataControllerRequest) {
        try {
            NafaithSignatureData nafaithSignatureData = new NafaithSignatureData();

            Debtor debtor = new Debtor();
            debtor.setNationalId("1000473387");

            nafaithSignatureData.setDebtor(debtor);
            nafaithSignatureData.setCityOfIssuance("Riyadh");
            nafaithSignatureData.setCityOfPayment("Riyadh");
            nafaithSignatureData.setDebtorPhoneNumber("0546258295");
            nafaithSignatureData.setTotalValue(1000);
            nafaithSignatureData.setCurrency("SAR");
            nafaithSignatureData.setMaxApproveDuration(14400);
            nafaithSignatureData.setReferenceId("1");
            nafaithSignatureData.setCountryOfIssuance("SA");
            nafaithSignatureData.setCountryOfPayment("SA");

            SanadItem sanadItem = new SanadItem();
            sanadItem.setDueDate("2020-12-28");
            sanadItem.setDueType("upon request");
            sanadItem.setTotalValue(1000);
            sanadItem.setReferenceId("sanad1");

            List<SanadItem> sanadItemList = new ArrayList<>();
            sanadItemList.add(sanadItem);
            nafaithSignatureData.setSanad(sanadItemList);

            String data = nafaithSignatureData.toString();
            LOG.error("createNafithSignature DATA :: " + data);
            String method = "POST";
            String connection_url = "nafith.sa";
            String endpoint = "/api/sanad-group/";
            String sanad_object = "";
            String secret_key = SIGN_SECRET.getValue(dataControllerRequest);
            String unix_timestamp = String.valueOf(Instant.now().getEpochSecond());
            String hmacSHA256Algorithm = "HmacSHA256";

            return hmacWithApacheCommons(hmacSHA256Algorithm, data, secret_key);
        } catch (Exception ex) {
            LOG.error("ERROR createNafithSignature :: " + ex);
        }
        return "";
    }

    public static String hmacWithApacheCommons(String algorithm, String data, String key) {
        try {
            return new HmacUtils(algorithm, key).hmacHex(data);
        } catch (Exception ex) {
            LOG.error("ERROR hmacWithApacheCommons :: " + ex);
        }
        return "";
    }

    private void extractValuesFromCreateSingleSanadService(Result getAccessTokenServiceResult) {
        // create a table "nafaith_sanad"
        // and call that service
    }

    private Map<String, String> createRequestForCreateNafaithService() {
        Map<String, String> inputParams = new HashMap<>();
        return inputParams;
    }

    private Result createNafaithSanadData(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result createNafaithSanadData = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, NAFAITH_SANAD_CREATE_OPERATION_ID, inputParams, null, dataControllerRequest);
            StatusEnum.success.setStatus(createNafaithSanadData);
            return createNafaithSanadData;
        } catch (Exception ex) {
            LOG.error("ERROR createNafaithSanadData :: " + ex);
        }
        return result;
    }
}