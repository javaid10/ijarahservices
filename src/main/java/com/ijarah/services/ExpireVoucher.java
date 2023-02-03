package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ijarah.Model.LoanDetailsForExpiry.LoanDetailsforExpiryResponse;
import com.ijarah.Model.LoanDetailsForExpiry.RecordsItem;
import com.ijarah.Model.RedeemVoucher.RedeemVoucherResponse;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.ijarah.utils.enums.VoucherStatus;
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
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.MURABAHA_T24_JSON_2_SERVICE_ID;

public class ExpireVoucher implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(ExpireVoucher.class);

    Map<String, String> inputParams;

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        initializeVariables();
        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        try {
            Result getLoanDetailsForExpiryData = getLoanDetailsForExpiry(dataControllerRequest);
            if (HelperMethods.hasRecords(getLoanDetailsForExpiryData)) {
                Gson gson = new Gson();
                LoanDetailsforExpiryResponse loanDetailsforExpiryResponse = gson.fromJson(ResultToJSON.convert(getLoanDetailsForExpiryData), LoanDetailsforExpiryResponse.class);
                for (RecordsItem loanDetailForVoucherExpiryRecord : loanDetailsforExpiryResponse.getRecords()) {
                    Map<String, String> inputParam = new HashMap<>();
                    inputParam.put("voucherCode", loanDetailForVoucherExpiryRecord.getVoucherCode());
                    Result expireVoucherT24 = expireVoucherT24(inputParam, dataControllerRequest);
                    if (IjarahHelperMethods.hasSuccessStatus(expireVoucherT24)) {
                        Gson gsonS2 = new Gson();
                        RedeemVoucherResponse redeemVoucherResponse = gsonS2.fromJson(ResultToJSON.convert(expireVoucherT24), RedeemVoucherResponse.class);
                        if (redeemVoucherResponse != null) {
                            if (redeemVoucherResponse.getHeader() != null) {
                                if (redeemVoucherResponse.getHeader().getStatus() != null && redeemVoucherResponse.getHeader().getStatus().equalsIgnoreCase("success")) {
                                    if (HelperMethods.hasRecords(expireVoucherDB(dataControllerRequest, loanDetailForVoucherExpiryRecord.getVoucherID()))) {
                                        StatusEnum.success.setStatus(result);
                                        result.addParam("ResponseCode", "ERR_60000");
                                        result.addParam("StatusMessage", "Voucher Expired Successfully");
                                    } else {
                                        IjarahErrors.ERR_EXPIRE_VOUCHER_DB_SERVICE_FAILED_035.setErrorCode(result);
                                    }
                                } else {
                                    IjarahErrors.ERR_EXPIRE_VOUCHER_T24_SERVICE_FAILED_036.setErrorCode(result);
                                }
                            } else {
                                IjarahErrors.ERR_EXPIRE_VOUCHER_T24_SERVICE_FAILED_036.setErrorCode(result);
                            }
                        } else {
                            IjarahErrors.ERR_EXPIRE_VOUCHER_T24_SERVICE_FAILED_036.setErrorCode(result);
                        }
                    } else {
                        IjarahErrors.ERR_EXPIRE_VOUCHER_T24_SERVICE_FAILED_036.setErrorCode(result);
                    }
                }
            } else {
                IjarahErrors.ERR_NO_VOUCHER_FOUND_024.setErrorCode(result);
            }
        } catch (Exception ex) {
            LOG.error("ERROR invoke :: " + ex);
            IjarahErrors.ERR_VOUCHER_EXPIRY_SERVICE_FAILED_034.setErrorCode(result);
        }
        return result;
    }

    private void initializeVariables() {
        inputParams = new HashMap<>();
    }

    private Result getLoanDetailsForExpiry(DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_GET_LOAN_DETAILS_FOR_EXPIRY_CODE_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_NO_VOUCHER_FOUND_024.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            LOG.error("ERROR getLoanDetailsForExpiry :: " + ex);
            IjarahErrors.ERR_GET_VOUCHER_DETAILS_FOR_EXPIRY_DB_SERVICE_FAILED_037.setErrorCode(result);
        }
        return result;
    }

    private Result expireVoucherT24(Map<String, String> inputParam, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            inputParam.put("voucherStatus", VoucherStatus.EXPIRED.name());
            Result redeemVoucher = ServiceCaller.internal(MURABAHA_T24_JSON_2_SERVICE_ID, REDEEM_VOUCHER_OPERATION_ID, inputParam, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
            String outputResponse = ResultToJSON.convert(redeemVoucher);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MURABAHA_T24_JSON_2_SERVICE_ID + " : " + REDEEM_VOUCHER_OPERATION_ID);

            if (IjarahHelperMethods.hasSuccessCode(redeemVoucher)) {
                StatusEnum.success.setStatus(redeemVoucher);
                return redeemVoucher;
            }
        } catch (Exception ex) {
            LOG.error("ERROR expireVoucherT24 :: " + ex);
        }
        return result;
    }

    private Result expireVoucherDB(DataControllerRequest dataControllerRequest, String voucherID) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();

            inputParams.put("voucherID", voucherID);
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_EXPIRED_VOUCHER_BY_VOUCHER_ID_OPERATION_ID, inputParams, null, dataControllerRequest);
            if (HelperMethods.hasRecords(result)) {
                StatusEnum.success.setStatus(result);
            } else {
                StatusEnum.error.setStatus(result);
                IjarahErrors.ERR_NO_VOUCHER_FOUND_024.setErrorCode(result);
            }
            return result;
        } catch (Exception ex) {
            StatusEnum.error.setStatus(result);
            IjarahErrors.ERR_NO_VOUCHER_FOUND_024.setErrorCode(result);
            return result;
        }
    }
}
