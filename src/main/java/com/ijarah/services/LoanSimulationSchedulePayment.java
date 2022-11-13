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
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.LOAN_SIMULATION_SCHEDULE_PAYMENT_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.MORA_T24_SERVICE_ID;

public class LoanSimulationSchedulePayment implements JavaService2 {

    Map<String, String> inputParams;
    private static final Logger LOG = Logger.getLogger(LoanSimulationSchedulePayment.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        if (preprocess()) {
            return loanSchedule(dataControllerRequest);
        }
        return result;
    }

    private boolean preprocess() {
        return true;
    }

    private Result loanSchedule(DataControllerRequest dataControllerRequest) throws InterruptedException {
        Result finalResult = new Result();
        Dataset finalDataset = new Dataset();
        Record finalRecord = new Record();

        Result getLoanSimulationResult = ServiceCaller.internal(LOAN_SIMULATION_SCHEDULE_PAYMENT_SERVICE_ID, LOAN_SIMULATION_ORCH_OPERATION_ID, inputParams, null, dataControllerRequest);
//        if(!HelperMethods.hasRecords(getLoanSimulationResult)){
//            finalRecord.addParam("Status", "Record Not Found");
//            finalDataset.addRecord(finalRecord);
//            finalResult.addDataset(finalDataset);
//        }else{
        //Thread.sleep(10000);

        if (!IjarahHelperMethods.isBlank(getLoanSimulationResult.getParamValueByName("arrangementId")) && !IjarahHelperMethods.isBlank(getLoanSimulationResult.getParamValueByName("simulationId"))) {
            Map<String, String> inputParamGetPayment = new HashMap<>();
            //inputParamGetPayment.put("simulationId", getLoanSimulationResult.getParamValueByName("simulationId"));
            inputParamGetPayment.put("simulationId", "AASIMR22147GC7L5B8");
            //inputParamGetPayment.put("arrangementId", getLoanSimulationResult.getParamValueByName("arrangementId"));
            inputParamGetPayment.put("arrangementId", "AA22147XQ7W2");
            Result getPaymentScheduleResult = ServiceCaller.internal(LOAN_SIMULATION_SCHEDULE_PAYMENT_SERVICE_ID, PAYMENT_SCHEDULE_OPERATION_ID, inputParamGetPayment, null, dataControllerRequest);
            if (!HelperMethods.hasRecords(getPaymentScheduleResult)) {
                finalRecord.addParam("Status", "Second Record Not Found");
                finalDataset.addRecord(finalRecord);
                finalResult.addDataset(finalDataset);
            } else {
                finalDataset = getPaymentScheduleResult.getDatasetById("body");
                finalResult.addDataset(finalDataset);
            }

        } else {
            finalRecord.addParam("Status", "Parameters Not Found");
            finalDataset.addRecord(finalRecord);
            finalResult.addDataset(finalDataset);
        }

//        }
        return finalResult;
    }

}