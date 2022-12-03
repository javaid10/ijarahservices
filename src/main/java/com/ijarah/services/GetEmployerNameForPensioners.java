package com.ijarah.services;

import com.google.gson.Gson;
import com.ijarah.Model.EMPLOYER_NAME_FOR_PENSIONERS.EmployerNameForPensionerResponse;
import com.ijarah.Model.EMPLOYER_NAME_FOR_PENSIONERS.EmployernamesforpensionerItem;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.StatusEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ijarah.utils.constants.OperationIDConstants.EMPLOYER_NAMES_FOR_PENSIONERS_GET_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.kony.adminconsole.commons.utils.InlineServiceExecutor.LOG;

public class GetEmployerNameForPensioners implements JavaService2 {
    String[] EMPLOYER_NAME_FOR_PENSIONERS;

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        Result result = StatusEnum.error.setStatus();
        Result getEmployerNameForPensionersResponse = getEmployerNameForPensioners(dataControllerRequest);
        Gson gson = new Gson();
        EmployerNameForPensionerResponse employerNameForPensionerResponse = gson.fromJson(ResultToJSON.convert(getEmployerNameForPensionersResponse), EmployerNameForPensionerResponse.class);
        extractValuesFromEmployerNamesForPensionerResponse(employerNameForPensionerResponse);
        LOG.error("EMPLOYER_NAME_FOR_PENSIONERS 0:: " + EMPLOYER_NAME_FOR_PENSIONERS[0]);
        LOG.error("EMPLOYER_NAME_FOR_PENSIONERS 1:: " + EMPLOYER_NAME_FOR_PENSIONERS[1]);
        return result;
    }

    private void extractValuesFromEmployerNamesForPensionerResponse(EmployerNameForPensionerResponse employerNameForPensionerResponse) {
        List<EmployernamesforpensionerItem> employerNamesForPensionerList = employerNameForPensionerResponse.getEmployernamesforpensioner();
        EMPLOYER_NAME_FOR_PENSIONERS = new String[employerNamesForPensionerList.size()];
        int index = 0;
        for (EmployernamesforpensionerItem employernamesforpensionerItem : employerNamesForPensionerList) {
            EMPLOYER_NAME_FOR_PENSIONERS[index] = employernamesforpensionerItem.getEmployerNamesForPensionersValue();
            index++;
        }
    }

    private Result getEmployerNameForPensioners(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Map<String, String> inputParams = new HashMap<>();
            return ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, EMPLOYER_NAMES_FOR_PENSIONERS_GET_OPERATION_ID, inputParams, null, dataControllerRequest);
        } catch (Exception ex) {
            LOG.error("ERROR getEmployerNameForPensioners :: " + ex);
        }
        return result;
    }
}
