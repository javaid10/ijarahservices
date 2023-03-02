package com.ijarah.services;

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
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.ijarah.utils.IjarahHelperMethods.DATE_FORMAT_yyyy_MM_dd;
import static com.ijarah.utils.constants.OperationIDConstants.MURABAHA_CONFIGURATION_GET_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.VOUCHER_CREATE_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;

public class CreateVoucher implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(CreateVoucher.class);
    private String EXPIRY_DATE = "10";

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        try {
            if (preProcess(dataControllerRequest)) {
                return createVoucherRecordInDB(dataControllerRequest);
            } else {
                Result result = new Result();
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);
                return result;
            }
        } catch (Exception ex) {
            Result result = new Result();
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_CREATE_VOUCHER_SERVICE_FAILED_021.setErrorCode(result);
            return result;
        }
    }

    private boolean preProcess(DataControllerRequest dataControllerRequest) {
        try {
            if (IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("retailerID"))
                    || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("retailerName"))
                    || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("commissionRate"))) {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return true;
    }

    private Result createVoucherRecordInDB(DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            getExpiryDateFromDB(dataControllerRequest);

            Map<String, String> inputParams = new HashMap<>();
            inputParams.put("retailerID", dataControllerRequest.getParameter("retailerID"));
            inputParams.put("retailerName", dataControllerRequest.getParameter("retailerName"));
            inputParams.put("commissionRate", dataControllerRequest.getParameter("commissionRate"));
            inputParams.put("voucherCode", generateVoucherCode());
            inputParams.put("expiryDate", getExpiryDate());

            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, VOUCHER_CREATE_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_CREATE_VOUCHER_SERVICE_FAILED_021.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_CREATE_VOUCHER_SERVICE_FAILED_021.setErrorCode(result);
            return result;
        }
    }

    private String generateVoucherCode() {
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

    private String getExpiryDate() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_yyyy_MM_dd);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, Integer.parseInt(EXPIRY_DATE));
        return dateFormat.format(calendar.getTime());
    }
}