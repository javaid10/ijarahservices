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
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.*;

public class CreateLoan implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(CreateLoan.class);
    Map<String, String> inputParams = new HashMap<>();
    private String NATIONAL_ID = "";
    private String SID_PRO_ACTIVE = "SID_PRO_ACTIVE";
    private String APPLICATION_STATUS = "applicationStatus";
    private String CSA_APPROVAL = "csaAppoRval";
    private String SANAD_APPROVAL = "sanadApproval";
    private Dataset CUSTOMERS_APPLICATION_DATA = new Dataset();
    private String PARTY_ID = "";
    private String FIXED_AMOUNT_VALUE = "100";
    private Dataset NAFAES_DATA = new Dataset();
    private String REFERENCE_NUMBER = "";
    private String ACCESS_TOKEN = "";

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

        try {
            if (preProcess(inputParams)) {
                Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);
                Result successResult = StatusEnum.success.setStatus();
                if (HelperMethods.hasRecords(getCustomerApplicationData) && IjarahHelperMethods.hasSuccessCode(getCustomerApplicationData)) {
                    extractValuesFromCustomerApplication(getCustomerApplicationData);
                    for (int index = 0; index < CUSTOMERS_APPLICATION_DATA.getAllRecords().size(); index++) {
                        Result getCustomerData = getPartyIDFromCustomerTable(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("Customer_id"), dataControllerRequest);
                        if (IjarahHelperMethods.hasSuccessCode(getCustomerData) && !IjarahHelperMethods.isBlank(PARTY_ID)) {
                            Result activateCustomer = activateCustomer(createInputParamsForActivateCustomerService(), dataControllerRequest);
                            if (IjarahHelperMethods.hasSuccessCode(activateCustomer)) {
                                Result createLoanResult = createLoan(createInputParamsForCreateLoanService(index), dataControllerRequest);
                                if (IjarahHelperMethods.hasSuccessCode(createLoanResult)) {
                                    Result getNafaesData = getNafaesData(CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("nationalId"), dataControllerRequest);
                                    if (IjarahHelperMethods.hasSuccessCode(getNafaesData) && HelperMethods.hasRecords(getNafaesData)) {
                                        extractValuesFromNafaes(getNafaesData);
                                        Result transferOrder = callTransferOrder(dataControllerRequest);
                                        Result saleOrder = callSaleOrder(dataControllerRequest);
                                    }
                                }
                            }
                        }
                    }
                }
                return successResult;
            }
        } catch (Exception ex) {
            LOG.error("ERROR invoke :: " + ex);
        }
        return result;
    }

    private Result callTransferOrder(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put("accessToken", ACCESS_TOKEN);
            inputParam.put("referenceNo", REFERENCE_NUMBER);
            Result transferOrderResult = StatusEnum.success.setStatus();
            transferOrderResult.appendResult(ServiceCaller.internal(NAFAES_REST_API_SERVICE_ID, TRANSFER_ORDER_OPERATION_ID, inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
            String outputResponse = ResultToJSON.convert(transferOrderResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, NAFAES_REST_API_SERVICE_ID + " : " + TRANSFER_ORDER_OPERATION_ID);
        } catch (Exception ex) {
            LOG.error("ERROR callTransferOrder :: " + ex);
        }
        return result;
    }

    private Result callSaleOrder(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put("accessToken", ACCESS_TOKEN);
            inputParam.put("referenceNo", REFERENCE_NUMBER);
            Result saleOrderResult = StatusEnum.success.setStatus();
            saleOrderResult.appendResult(ServiceCaller.internal(NAFAES_REST_API_SERVICE_ID, SALE_ORDER_PUSH_METHOD_OPERATION_ID, inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
            String outputResponse = ResultToJSON.convert(saleOrderResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, NAFAES_REST_API_SERVICE_ID + " : " + SALE_ORDER_PUSH_METHOD_OPERATION_ID);
        } catch (Exception ex) {
            LOG.error("ERROR callSaleOrder :: " + ex);
        }
        return result;
    }

    private Result getNafaesData(String nationalId, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getNafaesData = StatusEnum.success.setStatus();
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put(DBPUtilitiesConstants.FILTER, "nationalid"
                    + DBPUtilitiesConstants.EQUAL
                    + nationalId);
            getNafaesData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, NAFAES_GET_OPERATION_ID, inputParam, null, dataControllerRequest));
            return getNafaesData;
        } catch (Exception ex) {
            LOG.error("ERROR getNafaesData :: " + ex);
        }
        return result;
    }

    private Map<String, String> createInputParamsForCreateLoanService(int index) {
        Map<String, String> inputParams = new HashMap<>();
        try {
            inputParams.put("partyId", PARTY_ID);
            inputParams.put("fixedAmount", FIXED_AMOUNT_VALUE);
            inputParams.put("amount", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("loanAmount"));
            inputParams.put("fixed", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("approx"));
            inputParams.put("term", CUSTOMERS_APPLICATION_DATA.getRecord(index).getParamValueByName("tenor"));
        } catch (Exception ex) {
            LOG.error("ERROR createInputParamsForCreateLoanService :: " + ex);
        }
        return inputParams;
    }

    private Result createLoan(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getCreateLoanResult = StatusEnum.success.setStatus();
            getCreateLoanResult.appendResult(ServiceCaller.internal(MORA_T24_SERVICE_ID, LOAN_CREATION_OPERATION_ID, inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getCreateLoanResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MORA_T24_SERVICE_ID + " : " + LOAN_CREATION_OPERATION_ID);
        } catch (Exception ex) {
            LOG.error("ERROR createLoan :: " + ex);
        }
        return result;
    }

    private Result activateCustomer(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getActivateCustomerResult = StatusEnum.success.setStatus();
            getActivateCustomerResult.appendResult(ServiceCaller.internal(MORA_T24_SERVICE_ID, ACTIVATE_CUSTOMER_OPERATION_ID, inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getActivateCustomerResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MORA_T24_SERVICE_ID + " : " + ACTIVATE_CUSTOMER_OPERATION_ID);
        } catch (Exception ex) {
            LOG.error("ERROR activateCustomer :: " + ex);
        }
        return result;
    }

    private Map<String, String> createInputParamsForActivateCustomerService() {
        Map<String, String> inputParams = new HashMap<>();
        try {
            inputParams.put("partyId", PARTY_ID);
        } catch (Exception ex) {
            LOG.error("ERROR createInputParamsForActivateCustomerService :: " + ex);
        }
        return inputParams;
    }

    private Result getPartyIDFromCustomerTable(String customer_id, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getCustomerData = StatusEnum.success.setStatus();
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put(DBPUtilitiesConstants.FILTER, "id"
                    + DBPUtilitiesConstants.EQUAL
                    + customer_id);
            getCustomerData.appendResult(ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, CUSTOMER_GET_OPERATION_ID, inputParam, null, dataControllerRequest));
            PARTY_ID = HelperMethods.getFieldValue(getCustomerData, "partyId");
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
            Result getCustomerApplicationData = StatusEnum.success.setStatus();
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
            getCustomerApplicationData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, CUSTOMER_APPLICATION_GET_OPERATION_ID, filter, null, dataControllerRequest));
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
}
