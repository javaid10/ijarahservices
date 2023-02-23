package com.ijarah.services;

import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.MISReportModes;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;

public class MISReport implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(MISReport.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        Result result = new Result();
        StatusEnum.error.setStatus(result);

        try {
            if (preProcess(dataControllerRequest)) {
                Result MISReportResult = getMISReport(dataControllerRequest.getParameter("MISMode"), dataControllerRequest.getParameter("MIS_Value"), dataControllerRequest);
                if (HelperMethods.hasRecords(MISReportResult) && IjarahHelperMethods.hasSuccessStatus(MISReportResult)) {
                    if (dataControllerRequest.getParameter("MISMode").equalsIgnoreCase(MISReportModes.DATE.name())) {
                        MISReportResult.appendResult(calculateParameters(MISReportResult));
                    }
                    return MISReportResult;
                } else {
                    IjarahErrors.ERR_MIS_REPORT_NO_RECORD_AVAILABLE_042.setErrorCode(result);
                    return result;
                }
            } else {
                IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);
                return result;
            }
        } catch (Exception ex) {
            IjarahErrors.ERR_MIS_REPORT_SERVICE_FAILED_041.setErrorCode(result);
            return result;
        }
    }

    private boolean preProcess(DataControllerRequest dataControllerRequest) {
        return !(IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("MISMode")) || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("MIS_Value")));
    }

    private Result getMISReport(String misMode, String mis_value, DataControllerRequest dataControllerRequest) {
        Result result;
        Map<String, String> inputParams = new HashMap<>();
        if (misMode.equalsIgnoreCase(MISReportModes.FROM_TO.name())) {
            inputParams.put("startDate", mis_value.split(":")[0]);
            inputParams.put("endDate", mis_value.split(":")[1]);
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_CUSTOMER_INFO_FROM_TO_OPERATION_ID, inputParams, null, dataControllerRequest);
        } else if (misMode.equalsIgnoreCase(MISReportModes.DATE.name())) {
            inputParams.put("dateValue", mis_value);
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_CUSTOMER_INFO_DATE_OPERATION_ID, inputParams, null, dataControllerRequest);
        } else if (misMode.equalsIgnoreCase(MISReportModes.NATIONAL_ID.name())) {
            inputParams.put("nationalId", mis_value);
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_CUSTOMER_INFO_NATIONAL_ID_OPERATION_ID, inputParams, null, dataControllerRequest);
        } else if (misMode.equalsIgnoreCase(MISReportModes.MOBILE.name())) {
            inputParams.put("mobile", mis_value);
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_CUSTOMER_INFO_MOBILE_OPERATION_ID, inputParams, null, dataControllerRequest);
        } else if (misMode.equalsIgnoreCase(MISReportModes.APPLICATION_ID.name())) {
            inputParams.put("applicationID", mis_value);
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_CUSTOMER_INFO_APPLICATION_ID_OPERATION_ID, inputParams, null, dataControllerRequest);
        } else {
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_CUSTOMER_INFO_VIEW_OPERATION_ID, inputParams, null, dataControllerRequest);
        }
        return result;
    }

    private Result calculateParameters(Result misReportResult) {
        Result result = new Result();
        int countSanadApprovedByCustomers = 0;
        int countUnsuccessfulApplications = 0;
        int countSuccessfulApplications = 0;
        int countCSAApprovedByBackOffice = 0;
        int countSanadCreated = 0;
        double totalLoanAmountValueOfSanadApprovedByCustomers = 0;
        double totalLoanAmountValueOfSanadCreated = 0;
        for (Record MIS_Record : misReportResult.getAllDatasets().get(0).getAllRecords()) {
            if (HelperMethods.getFieldValue(MIS_Record, "sanadApproval").equalsIgnoreCase("true")) {
                countSanadApprovedByCustomers++;
                totalLoanAmountValueOfSanadApprovedByCustomers += Double.parseDouble(HelperMethods.getFieldValue(MIS_Record, "loanAmount").replace(",", "").replace(".", ""));
            }
            if (HelperMethods.getFieldValue(MIS_Record, "knockoutStatus").equalsIgnoreCase("FAIL")) {
                countUnsuccessfulApplications++;
            } else {
                countSuccessfulApplications++;
            }
            if (HelperMethods.getFieldValue(MIS_Record, "csaApporval").equalsIgnoreCase("true")) {
                countCSAApprovedByBackOffice++;
            }
            if (!IjarahHelperMethods.isBlank(HelperMethods.getFieldValue(MIS_Record, "sanadNumber"))) {
                countSanadCreated++;
                totalLoanAmountValueOfSanadCreated += Double.parseDouble(HelperMethods.getFieldValue(MIS_Record, "loanAmount").replace(",", "").replace(".", ""));
            }
        }
        result.addParam("countSanadApprovedByCustomers", String.valueOf(countSanadApprovedByCustomers));
        result.addParam("countUnsuccessfulApplications", String.valueOf(countUnsuccessfulApplications));
        result.addParam("countSuccessfulApplications", String.valueOf(countSuccessfulApplications));
        result.addParam("countCSAApprovedByBackOffice", String.valueOf(countCSAApprovedByBackOffice));
        result.addParam("totalLoanAmountValueOfSanadApprovedByCustomers", String.valueOf(totalLoanAmountValueOfSanadApprovedByCustomers));
        result.addParam("countSanadCreated", String.valueOf(countSanadCreated));
        result.addParam("totalLoanAmountValueOfSanadCreated", String.valueOf(totalLoanAmountValueOfSanadCreated));
        return result;
    }
}