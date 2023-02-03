package com.ijarah.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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
import static com.ijarah.utils.constants.OperationIDConstants.REDEEM_VOUCHER_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.SP_CANCELED_VOUCHER_BY_VOUCHER_ID_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.MURABAHA_T24_JSON_2_SERVICE_ID;

public class CancelVoucherByVoucherID implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(CancelVoucherByVoucherID.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        Map<String, String> inputParams = HelperMethods.getInputParamMap(objects);
        Result result = new Result();
        StatusEnum.error.setStatus(result);
        try {
            if (preProcess(dataControllerRequest)) {
                Result cancelVoucherT24 = cancelVoucherT24(inputParams, dataControllerRequest);
                if (IjarahHelperMethods.hasSuccessStatus(cancelVoucherT24)) {
                    Gson gsonS2 = new Gson();
                    RedeemVoucherResponse redeemVoucherResponse = gsonS2.fromJson(ResultToJSON.convert(cancelVoucherT24), RedeemVoucherResponse.class);
                    if (redeemVoucherResponse != null) {
                        if (redeemVoucherResponse.getHeader() != null) {
                            if (redeemVoucherResponse.getHeader().getStatus() != null && redeemVoucherResponse.getHeader().getStatus().equalsIgnoreCase("success")) {
                                Result cancelVoucherDB = cancelVoucherDB(dataControllerRequest);
                                if (HelperMethods.hasRecords(cancelVoucherDB)) {
                                    StatusEnum.success.setStatus(cancelVoucherDB);
                                    cancelVoucherDB.addParam("ResponseCode", "ERR_60000");
                                    cancelVoucherDB.addParam("StatusMessage", "Voucher Canceled Successfully");
                                    return cancelVoucherDB;
                                } else {
                                    IjarahErrors.ERR_CANCEL_VOUCHER_DB_SERVICE_FAILED_032.setErrorCode(result);
                                    return result;
                                }
                            } else {
                                IjarahErrors.ERR_CANCEL_VOUCHER_T24_SERVICE_FAILED_031.setErrorCode(result);
                                return result;
                            }
                        } else {
                            IjarahErrors.ERR_CANCEL_VOUCHER_T24_SERVICE_FAILED_031.setErrorCode(result);
                            return result;
                        }
                    } else {
                        IjarahErrors.ERR_CANCEL_VOUCHER_T24_SERVICE_FAILED_031.setErrorCode(result);
                        return result;
                    }
                } else {
                    IjarahErrors.ERR_CANCEL_VOUCHER_T24_SERVICE_FAILED_031.setErrorCode(result);
                    return result;
                }
            } else {
                IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);
                return result;
            }
        } catch (Exception ex) {
            IjarahErrors.ERR_GET_VOUCHER_DETAILS_SERVICE_FAILED_025.setErrorCode(result);
            return result;
        }
    }

    private boolean preProcess(DataControllerRequest dataControllerRequest) {
        try {
            if (IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("voucherID"))
                    || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("voucherCode"))) {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return true;
    }

    private Result cancelVoucherT24(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            inputParams.put("voucherStatus", VoucherStatus.CANCELLED.name());
            Result redeemVoucher = ServiceCaller.internal(MURABAHA_T24_JSON_2_SERVICE_ID, REDEEM_VOUCHER_OPERATION_ID, inputParams, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(redeemVoucher);
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MURABAHA_T24_JSON_2_SERVICE_ID + " : " + REDEEM_VOUCHER_OPERATION_ID);

            if (IjarahHelperMethods.hasSuccessCode(redeemVoucher)) {
                StatusEnum.success.setStatus(redeemVoucher);
                return redeemVoucher;
            }
        } catch (Exception ex) {
            LOG.error("ERROR cancelVoucherT24 :: " + ex);
        }
        return result;
    }

    private Result cancelVoucherDB(DataControllerRequest dataControllerRequest) {
        Result result = new Result();
        try {
            Map<String, String> inputParams = new HashMap<>();

            inputParams.put("voucherID", dataControllerRequest.getParameter("voucherID"));
            result = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_CANCELED_VOUCHER_BY_VOUCHER_ID_OPERATION_ID, inputParams, null, dataControllerRequest);
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