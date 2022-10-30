package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ijarah.Model.NafaithSignatureData.Debtor;
import com.ijarah.Model.NafaithSignatureData.NafaithSignatureData;
import com.ijarah.Model.NafaithSignatureData.SanadItem;
import com.ijarah.Model.SanadResponseModel.SingleSanadResponse;
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
                Result getAccessTokenServiceResult = callAccessTokenCreationService(createRequestForAccessTokenCreationService(), dataControllerRequest);

                if (IjarahHelperMethods.hasSuccessCode(getAccessTokenServiceResult) && HelperMethods.hasRecords(getAccessTokenServiceResult)) {

                    extractValuesFromAccessTokenService(getAccessTokenServiceResult);
                    Result getCreateSingleSanadServiceResult = callCreateSingleSanadService(createRequestForCreateSingleSanadService(), dataControllerRequest);

                    if (IjarahHelperMethods.hasSuccessCode(getCreateSingleSanadServiceResult) && HelperMethods.hasRecords(getCreateSingleSanadServiceResult)) {

                        Result createNafaithSanadData = createNafaithSanadData(createRequestForCreateRecordForSanadService(getCreateSingleSanadServiceResult), dataControllerRequest);

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
                IjarahErrors.ERR_CUSTOMER_APPLICATION_DATA_NOT_FOUND_009.setErrorCode(result);
            }
        }
        return result;
    }

    private boolean preProcess(Map<String, String> inputParams) {
        LOG.error("preProcess");
        NATIONAL_ID = inputParams.get("nationalId");
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
                    CUSTOMER_ID = HelperMethods.getFieldValue(getCustomerApplicationData, "Customer_id");
                    PHONE_NUMBER = HelperMethods.getFieldValue(getCustomerApplicationData, "mobile");
                    LOG.error("APPLICATION_ID :: " + APPLICATION_ID);
                    LOG.error("LOAN_AMOUNT :: " + LOAN_AMOUNT);
                    LOG.error("CUSTOMER_ID :: " + CUSTOMER_ID);

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
                    + DBPUtilitiesConstants.COMM_TYPE_PHONE);
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
            debtor.setNationalId(NATIONAL_ID);

            nafaithSignatureData.setDebtor(debtor);
            nafaithSignatureData.setCityOfIssuance("1");
            nafaithSignatureData.setCityOfPayment("Riyadh");
            nafaithSignatureData.setDebtorPhoneNumber(PHONE_NUMBER);
            nafaithSignatureData.setTotalValue(Double.parseDouble(LOAN_AMOUNT));
            nafaithSignatureData.setCurrency("SAR");
            nafaithSignatureData.setMaxApproveDuration(1320);
            nafaithSignatureData.setReferenceId(APPLICATION_ID);
            nafaithSignatureData.setCountryOfIssuance("SA");
            nafaithSignatureData.setCountryOfPayment("SA");

            SanadItem sanadItem = new SanadItem();
            sanadItem.setDueType("upon request");
            sanadItem.setTotalValue(Double.parseDouble(LOAN_AMOUNT));
            sanadItem.setReferenceId(APPLICATION_ID);

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

    private Map<String, String> createRequestForCreateRecordForSanadService(Result getAccessTokenServiceResult) {
        Map<String, String> inputParams = new HashMap<>();
        try {
            Gson gson = new Gson();
            SingleSanadResponse singleSanadResponse = gson.fromJson(ResultToJSON.convert(getAccessTokenServiceResult), SingleSanadResponse.class);

            inputParams.put("id", singleSanadResponse.getId());
            inputParams.put("debtor_phone_number", singleSanadResponse.getDebtorPhoneNumber());
            inputParams.put("total_value", singleSanadResponse.getTotalValue());
            inputParams.put("sanad_number", singleSanadResponse.getSanad().get(0).getNumber());
            inputParams.put("application_id", singleSanadResponse.getReferenceId());
            inputParams.put("due_type", singleSanadResponse.getSanad().get(0).getDueType());
            inputParams.put("status", singleSanadResponse.getSanad().get(0).getStatus());
            inputParams.put("code", String.valueOf(singleSanadResponse.getCode()));
            inputParams.put("type", singleSanadResponse.getType());
            inputParams.put("debtor_national_id", singleSanadResponse.getDebtor().getNationalId());
            inputParams.put("debtor_first_name", singleSanadResponse.getDebtor().getFirstName());
            inputParams.put("debtor_second_name", singleSanadResponse.getDebtor().getSecondName());
            inputParams.put("debtor_third_name", singleSanadResponse.getDebtor().getThirdName());
            inputParams.put("debtor_last_name", singleSanadResponse.getDebtor().getLastName());
            inputParams.put("currency", singleSanadResponse.getCurrency());
            inputParams.put("creditor_national_id", singleSanadResponse.getCreditor().getNationalId());
            inputParams.put("creditor_first_name", singleSanadResponse.getCreditor().getFirstName());
            inputParams.put("creditor_second_name", singleSanadResponse.getCreditor().getSecondName());
            inputParams.put("creditor_third_name", singleSanadResponse.getCreditor().getThirdName());
            inputParams.put("creditor_last_name", singleSanadResponse.getCreditor().getLastName());
            inputParams.put("creditor_phone_number", singleSanadResponse.getCreditor().getPhoneNumber());

        } catch (Exception ex) {
            LOG.error("ERROR createRequestForCreateRecordForSanadService :: " + ex);
        }
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