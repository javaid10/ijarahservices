package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.LOAN_SIMULATION_SCHEDULE_PAYMENT_SERVICE_ID;

public class LoanSimulationSchedulePayment implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(LoanSimulationSchedulePayment.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParams = HelperMethods.getInputParamMap(objects);
            IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

            if (preProcess(inputParams)) {
                Result loanSimulation = getLoanSimulation(inputParams, dataControllerRequest);
                if (loanSimulation.hasParamByName("simulationId") && loanSimulation.hasParamByName("arrangementId")) {
                    if (!IjarahHelperMethods.isBlank(loanSimulation.getParamValueByName("simulationId")) && !IjarahHelperMethods.isBlank(loanSimulation.getParamValueByName("simulationId"))) {
                        Thread.sleep(10000);
                        Map<String, String> inputParamGetPayment = new HashMap<>();
                        inputParamGetPayment.put("simulationId", loanSimulation.getParamValueByName("simulationId"));
                        inputParamGetPayment.put("arrangementId", loanSimulation.getParamValueByName("arrangementId"));
                        Result paymentSchedule = getPaymentSchedule(inputParamGetPayment, dataControllerRequest);
                        loanSimulation.appendResult(paymentSchedule);
                        StatusEnum.success.setStatus(loanSimulation);
                        return loanSimulation;
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            LOG.error("ERROR invoke :: " + ex);
            return result;
        }
    }

    private boolean preProcess(Map<String, String> inputParams) {
        try {
            if (IjarahHelperMethods.isBlank(inputParams.get("partyId"))
                    || IjarahHelperMethods.isBlank(inputParams.get("amount"))
                    || IjarahHelperMethods.isBlank(inputParams.get("term"))) {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return true;
    }

    private Result getLoanSimulation(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getLoanSimulationResult = ServiceCaller.internal(LOAN_SIMULATION_SCHEDULE_PAYMENT_SERVICE_ID, LOAN_SIMULATION_ORCH_OPERATION_ID, inputParams, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getLoanSimulationResult);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, LOAN_SIMULATION_SCHEDULE_PAYMENT_SERVICE_ID + " : " + LOAN_SIMULATION_ORCH_OPERATION_ID);
            return getLoanSimulationResult;
        } catch (Exception ex) {
            LOG.error("ERROR getLoanSimulation :: " + ex);
        }
        return result;
    }

    private Result getPaymentSchedule(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        try {
            Result getPaymentScheduleResult = ServiceCaller.internal(LOAN_SIMULATION_SCHEDULE_PAYMENT_SERVICE_ID, PAYMENT_SCHEDULE_OPERATION_ID, inputParams, null, dataControllerRequest);
//            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
//            String outputResponse = ResultToJSON.convert(getPaymentScheduleResult);
//            auditLogData(dataControllerRequest, inputRequest, outputResponse, LOAN_SIMULATION_SCHEDULE_PAYMENT_SERVICE_ID + " : " + PAYMENT_SCHEDULE_OPERATION_ID);
            return getPaymentScheduleResult;
        } catch (Exception ex) {
            LOG.error("ERROR getPaymentSchedule :: " + ex);
            return StatusEnum.error.setStatus();
        }
    }
}