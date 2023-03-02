package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijarah.utils.HTTPOperations;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.ijarah.utils.enums.VoucherStatus;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.IjarahHelperMethods.DATE_FORMAT_yyyy_MM_dd;
import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.*;

public class MurabahaCreateLoan implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(MurabahaCreateLoan.class);
    Map<String, String> inputParams = new HashMap<>();
    private String NATIONAL_ID = "";
    private String SID_PRO_ACTIVE = "SID_PRO_ACTIVE";
    private String APPLICATION_STATUS = "applicationStatus";
    private String CSA_APPROVAL = "csaAppoRval";
    private String SANAD_APPROVAL = "sanadApproval";
    private String PRODUCT_NAME = "productName";
    private String MURABAHA = "MURABAHA";
    private Dataset CUSTOMERS_APPLICATION_DATA = new Dataset();
    private String FIXED_AMOUNT_VALUE = "100";

    private Dataset NAFAES_DATA = new Dataset();
    private String REFERENCE_NUMBER = "";
    private String ACCESS_TOKEN = "";
    private String LOAN_CREATED = "LOAN_CREATED";

    private String EXPIRY_DATE = "10";

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        IjarahErrors.ERR_CREATE_LOAN_002.setErrorCode(result);

        Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);
        Result successResult = StatusEnum.success.setStatus();
        successResult.addParam("Message", "Loan Creation Successfully Completed");
        if (HelperMethods.hasRecords(getCustomerApplicationData) && IjarahHelperMethods.hasSuccessCode(getCustomerApplicationData)) {
            extractValuesFromCustomerApplication(getCustomerApplicationData);
            for (int index = 0; index < CUSTOMERS_APPLICATION_DATA.getAllRecords().size(); index++) {
                Result getCustomerData = getPartyIDFromCustomerTable(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("applicationID"), dataControllerRequest);
                if (IjarahHelperMethods.hasSuccessCode(getCustomerData) && HelperMethods.hasRecords(getCustomerData)) {
                    Result activateCustomer = activateCustomer(createInputParamsForActivateCustomerService(getCustomerData), dataControllerRequest);
                    if (IjarahHelperMethods.hasSuccessCode(activateCustomer)) {
                        Map<String, String> inputParams = createInputParamsForCreateLoanService(index, getCustomerData, CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("voucherID"), dataControllerRequest);
                        Result createLoanResult = createLoan(inputParams, dataControllerRequest);
                        if (IjarahHelperMethods.hasSuccessStatus(createLoanResult)) {
                            if (createLoanResult.getRecordById("header").getParamValueByName("status").equalsIgnoreCase("success")) {
                                updateCustomerApplicationData(createInputParamsForCustomerApplicationService(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("id")), dataControllerRequest);
                                //getExpiryDateFromDB(dataControllerRequest);
                                updateVoucherData(createInputParamsForUpdateVoucherService(inputParams.get("voucherId"), CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("voucherID")), dataControllerRequest);
                                /*
                                Result getNafaesData = getNafaesData(getCustomerData, dataControllerRequest);
                                if (IjarahHelperMethods.hasSuccessCode(getNafaesData) && HelperMethods.hasRecords(getNafaesData)) {
                                    extractValuesFromNafaes(getNafaesData);
                                    ACCESS_TOKEN = getAccessToken();
                                    Result transferOrder = callTransferOrder(dataControllerRequest);
                                    String transferOrderStatus = transferOrder.getParamValueByName("status");
                                    if (StringUtils.equalsAnyIgnoreCase("success", transferOrderStatus)) {
                                        Result callTransferOrderResult = callTransferOrderResult(dataControllerRequest);
                                        String transferOrderResult_Status = callTransferOrderResult.getParamValueByName("status");
                                        if (StringUtils.equalsAnyIgnoreCase("success", transferOrderResult_Status)) {
                                            callSaleOrder(dataControllerRequest);
                                        } else {
                                            IjarahErrors.ERR_TRANSFER_ORDER_OR_SALE_ORDER_008.setErrorCode(result);
                                        }
                                    } else {
                                        IjarahErrors.ERR_TRANSFER_ORDER_OR_SALE_ORDER_008.setErrorCode(result);
                                    }
                                } else {
                                    IjarahErrors.ERR_NAFAES_DATA_NOT_FOUND_007.setErrorCode(result);
                                } */
                            } else {
                                IjarahErrors.ERR_LOAN_CREATION_FAILED_006.setErrorCode(result);
                            }
                        } else {
                            IjarahErrors.ERR_LOAN_CREATION_FAILED_006.setErrorCode(result);
                        }
                    } else {
                        IjarahErrors.ERR_ACTIVATE_CUSTOMER_FAILED_005.setErrorCode(result);
                    }
                } else {
                    IjarahErrors.ERR_NO_CUSTOMER_RECORD_FOUND_004.setErrorCode(result);
                }
            }
        } else {
            IjarahErrors.ERR_CREATE_LOAN_003.setErrorCode(result);
        }
        return result;
    }

    private void getExpiryDateFromDB(DataControllerRequest dataControllerRequest) {
        try {
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put(DBPUtilitiesConstants.FILTER, "keyName"
                    + DBPUtilitiesConstants.EQUAL
                    + "expiryDate");
            Result getExpiryDate = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, MURABAHA_CONFIGURATION_GET_OPERATION_ID, inputParam, null, dataControllerRequest);
            if (HelperMethods.hasRecords(getExpiryDate)) {
                EXPIRY_DATE = HelperMethods.getFieldValue(getExpiryDate, "murabahaconfiguration", "keyValue");
            }
        } catch (Exception ex) {
            LOG.error("ERROR getExpiryDateFromDB :: " + ex);
        }
    }

    private Map<String, String> createInputParamsForCustomerApplicationService(String id) {
        Map<String, String> inputParam = new HashMap<>();
        inputParam.put("id", id);
        inputParam.put(APPLICATION_STATUS, LOAN_CREATED);
        return inputParam;
    }

    private Map<String, String> createInputParamsForUpdateVoucherService(String voucherCode, String voucherID) {
        Map<String, String> inputParam = new HashMap<>();
        inputParam.put("id", voucherID);
        //inputParam.put("voucherCode", voucherCode);
        inputParam.put("voucherStatus", VoucherStatus.UNUTILISED.name());
        inputParam.put("T24Status", VoucherStatus.UNUTILISED.name());
        //inputParam.put("expiryDate", getExpiryDate());
        return inputParam;
    }

    private String getExpiryDate() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_yyyy_MM_dd);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, Integer.parseInt(EXPIRY_DATE));
        return dateFormat.format(calendar.getTime());
    }


    private Result callTransferOrderResult(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put("uuid", IjarahHelperMethods.generateUUID() + "-TOR");
            inputParam.put("accessToken", ACCESS_TOKEN);
            inputParam.put("referenceNo", REFERENCE_NUMBER);
            inputParam.put("orderType", "TO");
            inputParam.put("lng", "2");
            Result transferOrderResult = ServiceCaller.internal(NAFAES_REST_API_SERVICE_ID, TRANSFER_ORDER_RESULT_OPERATION_ID, inputParam, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
            String outputResponse = ResultToJSON.convert(transferOrderResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, NAFAES_REST_API_SERVICE_ID + " : " + TRANSFER_ORDER_RESULT_OPERATION_ID);
            if (IjarahHelperMethods.hasSuccessStatus(transferOrderResult)) {
                StatusEnum.success.setStatus(transferOrderResult);
                return transferOrderResult;
            }
        } catch (Exception ex) {
            LOG.error("ERROR callTransferOrder :: " + ex);
        }
        return result;
    }

    private Result callTransferOrder(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put("uuid", IjarahHelperMethods.generateUUID() + "-TO");
            inputParam.put("accessToken", ACCESS_TOKEN);
            inputParam.put("referenceNo", REFERENCE_NUMBER);
            inputParam.put("orderType", "TO");
            inputParam.put("lng", "2");
            Result transferOrderResult = ServiceCaller.internal(NAFAES_REST_API_SERVICE_ID, TRANSFER_ORDER_OPERATION_ID, inputParam, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
            String outputResponse = ResultToJSON.convert(transferOrderResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, NAFAES_REST_API_SERVICE_ID + " : " + TRANSFER_ORDER_OPERATION_ID);
            if (IjarahHelperMethods.hasSuccessStatus(transferOrderResult)) {
                StatusEnum.success.setStatus(transferOrderResult);
                return transferOrderResult;
            }
        } catch (Exception ex) {
            LOG.error("ERROR callTransferOrder :: " + ex);
        }
        return result;
    }

    private Result callSaleOrder(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put("uuid", IjarahHelperMethods.generateUUID() + "-SO");
            inputParam.put("accessToken", ACCESS_TOKEN);
            inputParam.put("referenceNo", REFERENCE_NUMBER);
            inputParam.put("orderType", "SO");
            inputParam.put("lng", "2");
            Result saleOrderResult = ServiceCaller.internal(NAFAES_REST_API_SERVICE_ID, SALE_ORDER_PUSH_METHOD_OPERATION_ID, inputParam, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
            String outputResponse = ResultToJSON.convert(saleOrderResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, NAFAES_REST_API_SERVICE_ID + " : " + SALE_ORDER_PUSH_METHOD_OPERATION_ID);
            if (IjarahHelperMethods.hasSuccessStatus(saleOrderResult)) {
                StatusEnum.success.setStatus(saleOrderResult);
                return saleOrderResult;
            }
        } catch (Exception ex) {
            LOG.error("ERROR callSaleOrder :: " + ex);
        }
        return result;
    }

    private Result getNafaesData(Result getCustomerData, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            String currentAppId = HelperMethods.getFieldValue(getCustomerData, "currentAppId");
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put(DBPUtilitiesConstants.FILTER, "applicationid"
                    + DBPUtilitiesConstants.EQUAL
                    + currentAppId);
            Result getNafaesData = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, NAFAES_GET_OPERATION_ID, inputParam, null, dataControllerRequest);
            StatusEnum.success.setStatus(getNafaesData);
            return getNafaesData;
        } catch (Exception ex) {
            LOG.error("ERROR getNafaesData :: " + ex);
        }
        return result;
    }

    private Map<String, String> createInputParamsForCreateLoanService(int index, Result getCustomerData, String voucherId,DataControllerRequest dataControllerRequest) {
        Map<String, String> inputParams = new HashMap<>();
        try {
            inputParams.put("partyId", StringUtils.isNotBlank(HelperMethods.getFieldValue(getCustomerData, "partyId")) ? HelperMethods.getFieldValue(getCustomerData, "partyId") : "");
            inputParams.put("fixedAmount", FIXED_AMOUNT_VALUE);
            inputParams.put("amount", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("offerAmount").replace(",", ""));
            inputParams.put("fixed", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("loanRate"));
            inputParams.put("term", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("tenor") + "M");
            inputParams.put("sabbNumber", StringUtils.isNotBlank(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("sabbNumber")) ? CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("sabbNumber") : "");
            inputParams.put("sadadNumber", StringUtils.isNotBlank(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("sadadNumber")) ? CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("sadadNumber") : "");
            inputParams.put("sanadRef", StringUtils.isNotBlank(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("sanadNumber")) ? CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("sanadNumber") : "");
            inputParams.put("infIoanRef", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("applicationID"));
            inputParams.put("charge", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("murabahaCommissionRate"));
            inputParams.put("voucherId", generateVoucherCode(voucherId, dataControllerRequest));
            inputParams.put("voucherStatus", VoucherStatus.CREATED.name());
        } catch (Exception ex) {
            LOG.error("ERROR createInputParamsForCreateLoanService :: " + ex);
        }
        return inputParams;
    }

    private String generateVoucherCode(String voucherId,DataControllerRequest dataControllerRequest) {
    	/*
        String[] AlphaNumericArray = {"01234ABCDEFGHIJKL56789MNOPQRSTUVWXYZ", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", "ABCDEFGHIJKL56789MNOPQRSTUVWXYZ01234"};
        String AlphaNumericString = AlphaNumericArray[(int) (AlphaNumericArray.length * Math.random())];
        StringBuilder sb = new StringBuilder(17);
        sb.append("MORA-");
        for (int i = 0; i < 11; i++) {
            if (i == 6) {
                sb.append("-");
            }
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
        */
        Result voucherDetails = getVoucherData(voucherId, dataControllerRequest);
        String voucherCode = HelperMethods.getFieldValue(voucherDetails, "voucherCode");
        return voucherCode;
    }

    private Result createLoan(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            LOG.error("createLoan PARTY_ID :: " + inputParams.get("partyId"));
            Result getCreateLoanResult = ServiceCaller.internal(MURABAHA_T24_JSON_SERVICE_ID, MURABAHA_LOAN_CREATION_OPERATION_ID, inputParams, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getCreateLoanResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MURABAHA_T24_JSON_SERVICE_ID + " : " + MURABAHA_LOAN_CREATION_OPERATION_ID);

            if (IjarahHelperMethods.hasSuccessCode(getCreateLoanResult)) {
                StatusEnum.success.setStatus(getCreateLoanResult);
                return getCreateLoanResult;
            }
        } catch (Exception ex) {
            LOG.error("ERROR createLoan :: " + ex);
        }
        return result;
    }

    private Result activateCustomer(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getActivateCustomerResult = ServiceCaller.internal(MORA_T24_SERVICE_ID, ACTIVATE_CUSTOMER_OPERATION_ID, inputParams, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getActivateCustomerResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MORA_T24_SERVICE_ID + " : " + ACTIVATE_CUSTOMER_OPERATION_ID);
            if (IjarahHelperMethods.hasSuccessStatus(getActivateCustomerResult)) {
                StatusEnum.success.setStatus(getActivateCustomerResult);
                return getActivateCustomerResult;
            }
        } catch (Exception ex) {
            LOG.error("ERROR activateCustomer :: " + ex);
        }
        return result;
    }

    private Map<String, String> createInputParamsForActivateCustomerService(Result getCustomerData) {
        Map<String, String> inputParams = new HashMap<>();
        try {
            inputParams.put("partyId", HelperMethods.getFieldValue(getCustomerData, "partyId"));
        } catch (Exception ex) {
            LOG.error("ERROR createInputParamsForActivateCustomerService :: " + ex);
        }
        return inputParams;
    }

    private Result getPartyIDFromCustomerTable(String applicationID, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            LOG.error("getPartyIDFromCustomerTable applicationID :: " + applicationID);
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put(DBPUtilitiesConstants.FILTER, "currentAppId"
                    + DBPUtilitiesConstants.EQUAL
                    + applicationID);
            Result getCustomerData = ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, CUSTOMER_GET_OPERATION_ID, inputParam, null, dataControllerRequest);
            StatusEnum.success.setStatus(getCustomerData);
            return getCustomerData;
        } catch (Exception ex) {
            LOG.error("ERROR getPartyIDFromCustomerTable :: " + ex);
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
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, APPLICATION_STATUS
                    + DBPUtilitiesConstants.EQUAL
                    + SID_PRO_ACTIVE
                    + DBPUtilitiesConstants.AND
                    + CSA_APPROVAL
                    + DBPUtilitiesConstants.EQUAL
                    + DBPUtilitiesConstants.BOOLEAN_STRING_TRUE
                    + DBPUtilitiesConstants.AND
                    + SANAD_APPROVAL
                    + DBPUtilitiesConstants.EQUAL
                    + DBPUtilitiesConstants.BOOLEAN_STRING_TRUE
                    + DBPUtilitiesConstants.AND
                    + PRODUCT_NAME
                    + DBPUtilitiesConstants.EQUAL
                    + MURABAHA);
            Result getCustomerApplicationData = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, CUSTOMER_APPLICATION_GET_OPERATION_ID, filter, null, dataControllerRequest);
            StatusEnum.success.setStatus(getCustomerApplicationData);
            return getCustomerApplicationData;
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerApplicationData :: " + ex);
        }
        return result;
    }

    private void extractValuesFromCustomerApplication(Result getCustomerApplicationData) {
        try {
            CUSTOMERS_APPLICATION_DATA = getCustomerApplicationData.getDatasetById("tbl_customerapplication");

        } catch (Exception ex) {
            LOG.error("ERROR extractValuesFromCustomerApplication :: " + ex);
        }
    }

    private void extractValuesFromNafaes(Result getNafaesData) {
        try {
            ACCESS_TOKEN = getNafaesData.getDatasetById("nafaes").getRecord(0).getParamValueByName("accessToken");
            REFERENCE_NUMBER = getNafaesData.getDatasetById("nafaes").getRecord(0).getParamValueByName("referencenumber");
        } catch (Exception ex) {
            LOG.error("ERROR extractValuesFromNafaes :: " + ex);
        }
    }

    private void updateCustomerApplicationData(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, CUSTOMER_APPLICATION_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest);
    }

    private void updateVoucherData(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, VOUCHER_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest);
    }

    private Result getVoucherData(String voucherID, DataControllerRequest dataControllerRequest) {
        Map<String, String> filter = new HashMap<>();
        filter.put(DBPUtilitiesConstants.FILTER, "id" + DBPUtilitiesConstants.EQUAL + voucherID);
        Result getVoucherData = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, VOUCHER_GET_OPERATION_ID, filter, null, dataControllerRequest);
        LOG.error("getCustomerApplicationData :: " + ResultToJSON.convert(getVoucherData));
        return getVoucherData;
    }

    /**
     * @return
     */
    private static String getAccessToken() {
        LOG.debug("==========> Nafaes - excuteLogin - Begin");
        String authToken = null;

        String loginURL = "https://testapi.nafaes.com/oauth/token?grant_type=password&username=APINIG1102&client_id=IFCSUD2789";
        LOG.debug("==========> Login URL  :: " + loginURL);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("password", "<fq$h(59@3");
        paramsMap.put("client_secret", "$69$is9@n>");

        HashMap<String, String> headersMap = new HashMap<String, String>();

        String endPointResponse = HTTPOperations.hitPOSTServiceAndGetResponse(loginURL, paramsMap, null, headersMap);
        JSONObject responseJson = getStringAsJSONObject(endPointResponse);
        LOG.debug("==========> responseJson :: " + responseJson);
        authToken = responseJson.getString("access_token");
        LOG.debug("==========> authToken :: " + authToken);
        LOG.debug("==========> Nafaes - excuteLogin - End");
        return authToken;
    }

    /**
     * Converts the given String into the JSONObject
     *
     * @param jsonString
     * @return
     */
    public static JSONObject getStringAsJSONObject(String jsonString) {
        JSONObject generatedJSONObject = new JSONObject();
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            generatedJSONObject = new JSONObject(jsonString);
            return generatedJSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}