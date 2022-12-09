package com.ijarah.services;

import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.ACTIVATE_CUSTOMER_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.CUSTOMER_APPLICATION_GET_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.CUSTOMER_APPLICATION_UPDATE_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.CUSTOMER_GET_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.LOAN_CREATION_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.NAFAES_GET_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.SALE_ORDER_PUSH_METHOD_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.TRANSFER_ORDER_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.TRANSFER_ORDER_RESULT_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DBXDB_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.MORA_T24_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.NAFAES_REST_API_SERVICE_ID;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ijarah.Model.AccessToken.AccessTokenResponse;
import com.ijarah.utils.HTTPOperations;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.EnvironmentConfig;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class CreateLoan implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(CreateLoan.class);
    Map<String, String> inputParams = new HashMap<>();
    private String NATIONAL_ID = "";
    private String SID_PRO_ACTIVE = "SID_PRO_ACTIVE";
    private String APPLICATION_STATUS = "applicationStatus";
    private String CSA_APPROVAL = "csaAppoRval";
    private String SANAD_APPROVAL = "sanadApproval";
    private Dataset CUSTOMERS_APPLICATION_DATA = new Dataset();
    private String FIXED_AMOUNT_VALUE = "100";

    private Dataset NAFAES_DATA = new Dataset();
    private String REFERENCE_NUMBER = "";
    private String ACCESS_TOKEN = "";
    private String LOAN_CREATED = "LOAN_CREATED";

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
                        Result createLoanResult = createLoan(createInputParamsForCreateLoanService(index, getCustomerData), dataControllerRequest);
                        if (IjarahHelperMethods.hasSuccessCode(createLoanResult)) {
                            updateCustomerApplicationData(createInputParamsForCustomerApplicationService(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("id")), dataControllerRequest);
                            Result getNafaesData = getNafaesData(getCustomerData, dataControllerRequest);
                            if (IjarahHelperMethods.hasSuccessCode(getNafaesData) && HelperMethods.hasRecords(getNafaesData)) {
                                extractValuesFromNafaes(getNafaesData);
                                ACCESS_TOKEN = getAccessToken();

                                Result transferOrder = callTransferOrder(dataControllerRequest);
                                LOG.debug("======> Transfer Order Result 1 " + ResultToJSON.convert(transferOrder));

                                String transferOrderStatus = transferOrder.getParamValueByName("status");

                                if (StringUtils.equalsAnyIgnoreCase("success", transferOrderStatus)) {

                                    Result transferOrderresult = callTransferOrderResult(dataControllerRequest);
                                    LOG.debug("======> Transfer Order Result 2 " + ResultToJSON.convert(transferOrderresult));
                                    String transferOrderResult_Status = transferOrderresult.getParamValueByName("status");

                                    if (StringUtils.equalsAnyIgnoreCase("success", transferOrderResult_Status)) {
                                        Result saleOrder = callSaleOrder(dataControllerRequest);
                                        LOG.debug("======> Sale Order Result " + ResultToJSON.convert(saleOrder));
                                    }
                                }
                                    /*
                                    if (IjarahHelperMethods.hasSuccessCode(transferOrder) && IjarahHelperMethods.hasSuccessCode(saleOrder)) {
                                        updateCustomerApplicationData(createInputParamsForCustomerApplicationService(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("id")), dataControllerRequest);
                                        return successResult;
                                    } else {
                                        IjarahErrors.ERR_TRANSFER_ORDER_OR_SALE_ORDER_008.setErrorCode(result);
                                    }
                                     */
                            } else {
                                IjarahErrors.ERR_NAFAES_DATA_NOT_FOUND_007.setErrorCode(result);
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

    private Map<String, String> createInputParamsForCustomerApplicationService(String id) {
        Map<String, String> inputParam = new HashMap<>();
        inputParam.put("id", id);
        inputParam.put(APPLICATION_STATUS, LOAN_CREATED);
        return inputParam;
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

    private Map<String, String> createInputParamsForCreateLoanService(int index, Result getCustomerData) {
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
            inputParams.put("mobileNumber", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("mobile"));
        } catch (Exception ex) {
            LOG.error("ERROR createInputParamsForCreateLoanService :: " + ex);
        }
        return inputParams;
    }

    private Result createLoan(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            LOG.error("createLoan PARTY_ID :: " + inputParams.get("partyId"));
            Result getCreateLoanResult = ServiceCaller.internal(MORA_T24_SERVICE_ID, LOAN_CREATION_OPERATION_ID, inputParams, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getCreateLoanResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MORA_T24_SERVICE_ID + " : " + LOAN_CREATION_OPERATION_ID);
            if (IjarahHelperMethods.hasSuccessStatus(getCreateLoanResult)) {
                StatusEnum.success.setStatus(getCreateLoanResult);
                
                TriggerNotification.sendMessage(getMessageBody(inputParams, dataControllerRequest), inputParams.get("mobileNumber"));
                
                return getCreateLoanResult;
            }
        } catch (Exception ex) {
            LOG.error("ERROR createLoan :: " + ex);
        }
        return result;
    }

    /**
     * 
     * @param inputParams
     * @param dataControllerRequest
     * @return
     */
    private static String getMessageBody (Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
    	String message = EnvironmentConfig.CREATE_LOAN_MESSAGE_TEMPLATE.getValue(dataControllerRequest);
        LOG.error("======> Message Body Before parsing " + message);
    	inputParams.put("#SADAD#", inputParams.get("sadadNumber"));
    	inputParams.put("#SABB ACCOUNT#", inputParams.get("sabbNumber"));
    	String messageBody = TriggerNotification.getJsonFromTemplate(message, inputParams);
        LOG.error("======> Message Body after parsing " + messageBody);
    	return messageBody;
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
                    + DBPUtilitiesConstants.BOOLEAN_STRING_TRUE);
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

    public static void main(String[] args) {
        getAccessToken();
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

    public String extractAccessToken() {
        try {
            LOG.error("extractAccessToken 1");
            String url = "https://testapi.nafaes.com/oauth/token?grant_type=password&username=APINIG1102&password=<fq$h(59@3&client_id=IFCSUD2789&client_secret=$69$is9@n>";
            String encodedURL = URLEncoder.encode(url, "UTF-8");
            HttpClient httpClient = HttpClients
                    .custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
            HttpPost request = new HttpPost(encodedURL);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
            LOG.error("extractAccessToken 2");
            HttpResponse httpResponse = httpClient.execute(request);
            LOG.error("extractAccessToken 3");
            HttpEntity entity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(entity);
            LOG.error("extractAccessToken 4");
            JSONObject responseObject = new JSONObject(responseString);
            LOG.error("extractAccessToken  1 = " + responseString);
            LOG.error("extractAccessToken  2 = " + responseObject);
            Result accessTokenResult = JSONToResult.convert(responseString);
            LOG.error("extractAccessToken  3 = " + accessTokenResult.getParamValueByName("access_token"));
            Gson gson = new Gson();
            AccessTokenResponse accessTokenResponse = gson.fromJson(ResultToJSON.convert(accessTokenResult), AccessTokenResponse.class);
            LOG.error("extractAccessToken  4 = " + accessTokenResponse.getProviderToken().getParams().getAccessToken());
            return accessTokenResponse.getProviderToken().getParams().getAccessToken();
        } catch (Exception e) {
            LOG.error("extractAccessToken  postWithFormData exception= " + e);
            return null;
        }
    }
}