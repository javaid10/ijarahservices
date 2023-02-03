package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ijarah.Model.LoanDetails.LoanDetailsResponse;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.ApplicationStatusForDashboard;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.ijarah.utils.enums.VoucherStatus;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.GET_LOAN_DETAILS_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.SP_GET_LOAN_DETAILS_BY_NATIONAL_ID_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.MORA_T24_SERVICE_ID;

public class Dashboard implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(Dashboard.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        Map<String, String> inputParams = HelperMethods.getInputParamMap(objects);
        Result result = new Result();
        StatusEnum.error.setStatus(result);
        IjarahErrors.ERR_DASHBOARD_SERVICE_FAILED_028.setErrorCode(result);

        Result noLoanCreatedResult = new Result();
        StatusEnum.error.setStatus(noLoanCreatedResult);
        noLoanCreatedResult.addParam("applicationStatus", "SID_SUSPENDED");
        noLoanCreatedResult.addParam("Message", "No Loan is having loan created status");
        noLoanCreatedResult.addParam("ResponseCode", "ERR_60000");

        try {
            if (preProcess(dataControllerRequest)) {
                Result getLoanDetails = getLoanDetails(inputParams, dataControllerRequest);

                if (IjarahHelperMethods.hasSuccessStatus(getLoanDetails) && !HelperMethods.getFieldValue(getLoanDetails, "partyId").isEmpty()) {
                    if (HelperMethods.getFieldValue(getLoanDetails, "applicationStatus").equalsIgnoreCase(ApplicationStatusForDashboard.LOAN_CREATED.name())) {
                        inputParams.put("partyId", HelperMethods.getFieldValue(getLoanDetails, "partyId"));
                        Result T24LoanDetailsResponse = getT24LoanDetails(inputParams, dataControllerRequest);
                        if (IjarahHelperMethods.hasSuccessStatus(T24LoanDetailsResponse)) {
                            Gson gsonS2 = new Gson();
                            LoanDetailsResponse getLoanDetailsResponse = gsonS2.fromJson(ResultToJSON.convert(T24LoanDetailsResponse), LoanDetailsResponse.class);
                            if (getLoanDetailsResponse != null) {
                                if (getLoanDetailsResponse.getHeader() != null) {
                                    if (getLoanDetailsResponse.getHeader().getStatus() != null && getLoanDetailsResponse.getHeader().getStatus().equalsIgnoreCase("success")
                                            && !getLoanDetailsResponse.getBody().isEmpty() && getLoanDetailsResponse.getBody().size() > 0) {
                                        return createLoanDetailsResponse(T24LoanDetailsResponse, getLoanDetails);
                                    } else {
                                        return noLoanCreatedResult;
                                    }
                                } else {
                                    return noLoanCreatedResult;
                                }
                            } else {
                                return noLoanCreatedResult;
                            }
                        } else {
                            return noLoanCreatedResult;
                        }
                    } else {
                        return createLoanDetailsResponse(getLoanDetails);
                    }
                } else {
                    return noLoanCreatedResult;
                }
            } else {
                IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);
                return result;
            }
        } catch (Exception ex) {
            IjarahErrors.ERR_DASHBOARD_SERVICE_FAILED_028.setErrorCode(result);
            return result;
        }
    }

    private Result createLoanDetailsResponse(Result t24LoanDetailsResponse, Result getLoanDetails) {
        Dataset t24LoanDetails = t24LoanDetailsResponse.getDatasetById("body");
        Result result = new Result();
        Dataset dataset = new Dataset();
        dataset.setId("body");
        Record record = t24LoanDetails.getAllRecords().get(0);
        record.setId("T24LoanDetails");
        Record loanRecordDB = getLoanDetails.getAllDatasets().get(0).getAllRecords().get(0);
        loanRecordDB.setId("DBXDBLoanDetails");
        if (HelperMethods.getFieldValue(loanRecordDB, "voucherStatus").equalsIgnoreCase(VoucherStatus.REDEEMED.name())) {
            loanRecordDB.addParam("overallApplicationStatus", ApplicationStatusForDashboard.REDEEMED.name());
        } else {
            loanRecordDB.addParam("overallApplicationStatus", ApplicationStatusForDashboard.LOAN_CREATED.name());
        }
        loanRecordDB.addParam("arrangementId", record.getParamValueByName("arrangementId"));
        loanRecordDB.addParam("repaymentAmount", record.getParamValueByName("repaymentAmount"));
        loanRecordDB.addParam("loanStartDate", record.getParamValueByName("loanStartDate"));
        loanRecordDB.addParam("roleDisplayName", record.getParamValueByName("roleDisplayName"));
        loanRecordDB.addParam("sabbId", record.getParamValueByName("sabbId"));
        loanRecordDB.addParam("loanAmount", record.getParamValueByName("loanAmount").replace(",",""));
        loanRecordDB.addParam("loanInterestType", record.getParamValueByName("loanInterestType"));
        loanRecordDB.addParam("productName", record.getParamValueByName("productName"));
        loanRecordDB.addParam("loanBalance", record.getParamValueByName("loanBalance"));
        loanRecordDB.addParam("loanNextPayDate", record.getParamValueByName("loanNextPayDate"));
        loanRecordDB.addParam("customerShortName", record.getParamValueByName("customerShortName"));
        loanRecordDB.addParam("sadadId", record.getParamValueByName("sadadId"));
        loanRecordDB.addParam("loanProduct", record.getParamValueByName("loanProduct"));
        loanRecordDB.addParam("loanAccountId", record.getParamValueByName("loanAccountId"));
        loanRecordDB.addParam("curCommitment", record.getParamValueByName("curCommitment"));
        loanRecordDB.addParam("loanStatus", record.getParamValueByName("loanStatus"));
        loanRecordDB.addParam("totCommitment", record.getParamValueByName("totCommitment"));
        loanRecordDB.addParam("loanCurrency", record.getParamValueByName("loanCurrency"));
        loanRecordDB.addParam("loanEndDate", record.getParamValueByName("loanEndDate"));


        dataset.addRecord(loanRecordDB);
        result.addDataset(dataset);
        StatusEnum.success.setStatus(result);
        result.addParam("ResponseCode", "ERR_60000");
        return result;
    }

    private Result createLoanDetailsResponse(Result getLoanDetails) {
        Result result = new Result();
        Dataset dataset = new Dataset();
        dataset.setId("body");
        Record loanRecordDB = getLoanDetails.getAllDatasets().get(0).getAllRecords().get(0);
        loanRecordDB.setId("DBXDBLoanDetails");
        if (HelperMethods.getFieldValue(loanRecordDB, "csaApporval").equalsIgnoreCase("true")
                && HelperMethods.getFieldValue(loanRecordDB, "sanadApproval").equalsIgnoreCase("true")) {
            loanRecordDB.addParam("overallApplicationStatus", ApplicationStatusForDashboard.BOTH_APPROVED.name());
        } else if (HelperMethods.getFieldValue(loanRecordDB, "csaApporval").equalsIgnoreCase("false")
                && HelperMethods.getFieldValue(loanRecordDB, "sanadApproval").equalsIgnoreCase("true")) {
            loanRecordDB.addParam("overallApplicationStatus", ApplicationStatusForDashboard.CSA_APPROVAL_WAITING.name());
        } else if (HelperMethods.getFieldValue(loanRecordDB, "csaApporval").equalsIgnoreCase("true")
                && HelperMethods.getFieldValue(loanRecordDB, "sanadApproval").equalsIgnoreCase("false")) {
            loanRecordDB.addParam("overallApplicationStatus", ApplicationStatusForDashboard.SANAD_WAITING.name());
        } else {
            loanRecordDB.addParam("overallApplicationStatus", ApplicationStatusForDashboard.PENDING_APPROVAL.name());
        }
        loanRecordDB.addParam("loanAmount", HelperMethods.getFieldValue(loanRecordDB, "loanAmount").replace(",",""));
        dataset.addRecord(loanRecordDB);
        result.addDataset(dataset);
        StatusEnum.success.setStatus(result);
        result.addParam("ResponseCode", "ERR_60000");
        return result;
    }

    private Result getT24LoanDetails(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            inputParams.put("customerId", inputParams.get("partyId"));
            Result getLoanDetails = ServiceCaller.internal(MORA_T24_SERVICE_ID, GET_LOAN_DETAILS_OPERATION_ID, inputParams, null, dataControllerRequest);

            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getLoanDetails);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MORA_T24_SERVICE_ID + " : " + GET_LOAN_DETAILS_OPERATION_ID);

            if (IjarahHelperMethods.hasSuccessCode(getLoanDetails)) {
                StatusEnum.success.setStatus(getLoanDetails);
                return getLoanDetails;
            }
        } catch (Exception ex) {
            LOG.error("ERROR getT24LoanDetails :: " + ex);
            return result;
        }
        return result;
    }

    private boolean checkLoanStatus(Result getLoanDetails) {
        for (Record record : getLoanDetails.getAllRecords()) {
            if (record.getParamValueByName("applicationStatus").equalsIgnoreCase("LOAN_CREATED")) {
                return true;
            }
        }
        return false;
    }

    private Result getLoanDetails(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_LOAN_DETAILS_BY_NATIONAL_ID_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_NO_LOAN_DETAIL_FOUND_030.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_GET_LOAN_DETAILS_SERVICE_FAILED_029.setErrorCode(result);
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
}