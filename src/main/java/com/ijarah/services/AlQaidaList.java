package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.IjarahHelperMethods.DATE_FORMAT_yyyy_MM_dd;
import static com.ijarah.utils.IjarahHelperMethods.getDate;
import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.SC_SANCTIONS_SERVICE_ID;

public class AlQaidaList implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(AlQaidaList.class);
    Map<String, String> inputParams = new HashMap<>();
    private Dataset INDIVIDUALS = new Dataset();

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

        try {
            Result getAlQaidaList = getAlQaidaList(inputParams, dataControllerRequest);
            extractDataFromAlQaidaList(getAlQaidaList);
            for (int index = 0; index < INDIVIDUALS.getAllRecords().size(); index++) {
                createOrUpdateAlQaidaList(createInputParamsAlQaidaList(INDIVIDUALS.getRecord(index)), dataControllerRequest);
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return result;
    }

    private void extractDataFromAlQaidaList(Result getAlQaidaList) {
        try {
            INDIVIDUALS = getAlQaidaList.getDatasetById("INDIVIDUALS");
        } catch (Exception ex) {
            LOG.error("ERROR extractDataFromAlQaidaList :: " + ex);
        }
    }

    private Map<String, String> createInputParamsAlQaidaList(Record individual) {
        Map<String, String> inputParams = new HashMap<>();
        String currentDateTime = getDate(LocalDateTime.now(), DATE_FORMAT_yyyy_MM_dd);

        try {
            inputParams.put("dataid", individual.getParamValueByName("dataid"));
            inputParams.put("firstname", individual.getParamValueByName("first_name"));
            inputParams.put("secondname", individual.getParamValueByName("second_name"));
            inputParams.put("thirdname", individual.getParamValueByName("third_name"));
            inputParams.put("fourthname", individual.getParamValueByName("fourth_name"));
            inputParams.put("listedon", individual.getParamValueByName("listed_on"));
            inputParams.put("createdts", currentDateTime);
            inputParams.put("lastmodifieddts", currentDateTime);
        } catch (Exception ex) {
            LOG.error("ERROR createInputParamsForUpdateAlQaidaList :: " + ex);
        }

        return inputParams;
    }

    private Result getAlQaidaList(Map<String, String> inputParams, DataControllerRequest
            dataControllerRequest) {
        Result getAlQaidaList = StatusEnum.error.setStatus();
        try {
            getAlQaidaList = StatusEnum.success.setStatus();
            getAlQaidaList.appendResult(ServiceCaller.internal(SC_SANCTIONS_SERVICE_ID, GET_AL_QAIDA_LIST_OPERATION_ID, inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getAlQaidaList);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, SC_SANCTIONS_SERVICE_ID + " : " + GET_AL_QAIDA_LIST_OPERATION_ID);
        } catch (Exception ex) {
            LOG.error("ERROR getAlQaidaList :: " + ex);
        }
        return getAlQaidaList;
    }

    private Result createOrUpdateAlQaidaList(Map<String, String> inputParams, DataControllerRequest
            dataControllerRequest) {

        Result result = StatusEnum.error.setStatus();

        try {
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "dataid" + DBPUtilitiesConstants.EQUAL + inputParams.get("dataid"));
            Result getAlQaidaListData = StatusEnum.success.setStatus();
            getAlQaidaListData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, AL_QAIDA_LIST_GET_OPERATION_ID, filter, null, dataControllerRequest));

            Result createOrUpdateAlQaidaListData = StatusEnum.success.setStatus();

            if (getAlQaidaListData.getDatasetById("alqaidalist").getAllRecords().isEmpty() && !HelperMethods.hasRecords(getAlQaidaListData.getDatasetById("alqaidalist"))) {
                createOrUpdateAlQaidaListData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, AL_QAIDA_LIST_CREATE_OPERATION_ID, inputParams, null, dataControllerRequest));
            } else {
                createOrUpdateAlQaidaListData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, AL_QAIDA_LIST_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest));
            }
            return createOrUpdateAlQaidaListData;
        } catch (Exception ex) {
            LOG.error("ERROR updateAlQaidaList :: " + ex);
        }
        return result;
    }
}