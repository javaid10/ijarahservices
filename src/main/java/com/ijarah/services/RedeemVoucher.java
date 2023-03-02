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
import static com.ijarah.utils.constants.OperationIDConstants.VOUCHER_UPDATE_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.MURABAHA_T24_JSON_2_SERVICE_ID;

public class RedeemVoucher implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(RedeemVoucher.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        Map<String, String> inputParams = HelperMethods.getInputParamMap(objects);
        Result result = new Result();
        StatusEnum.error.setStatus(result);

        try {
            if (preProcess(dataControllerRequest)) {
                Result redeemVoucher = redeemVoucher(inputParams, dataControllerRequest);
                if (IjarahHelperMethods.hasSuccessStatus(redeemVoucher)) {
                    LOG.error("redeemVoucher7");
                    Gson gsonS2 = new Gson();
                    RedeemVoucherResponse redeemVoucherResponse = gsonS2.fromJson(ResultToJSON.convert(redeemVoucher), RedeemVoucherResponse.class);
                    if (redeemVoucherResponse != null) {
                        LOG.error("redeemVoucher8");
                        if (redeemVoucherResponse.getHeader() != null) {
                            LOG.error("redeemVoucher9");
                            if (redeemVoucherResponse.getHeader().getStatus() != null && redeemVoucherResponse.getHeader().getStatus().equalsIgnoreCase("success")) {
                                LOG.error("redeemVoucher10");
                                Result updateVoucherData = updateVoucherData(createInputParamsForUpdateVoucherService(inputParams), dataControllerRequest);
                                if (HelperMethods.hasRecords(updateVoucherData)) {
                                    LOG.error("redeemVoucher11");
                                    StatusEnum.success.setStatus(updateVoucherData);
                                    updateVoucherData.addParam("ResponseCode", "ERR_60000");
                                    updateVoucherData.addParam("StatusMessage", "Voucher Redeemed Successfully");
                                    return updateVoucherData;
                                } else {
                                    LOG.error("redeemVoucher6");
                                    IjarahErrors.ERR_UPDATE_VOUCHER_STATUS_SERVICE_FAILED_026.setErrorCode(result);
                                    return result;
                                }
                            } else {
                                LOG.error("redeemVoucher5");
                                IjarahErrors.ERR_UPDATE_VOUCHER_STATUS_SERVICE_FAILED_026.setErrorCode(result);
                                return result;
                            }
                        } else {
                            LOG.error("redeemVoucher4");
                            IjarahErrors.ERR_UPDATE_VOUCHER_STATUS_SERVICE_FAILED_026.setErrorCode(result);
                            return result;
                        }
                    } else {
                        LOG.error("redeemVoucher3");
                        IjarahErrors.ERR_UPDATE_VOUCHER_STATUS_SERVICE_FAILED_026.setErrorCode(result);
                        return result;
                    }
                } else {
                    LOG.error("redeemVoucher2");
                    IjarahErrors.ERR_REDEEM_VOUCHER_T24_SERVICE_FAILED_027.setErrorCode(result);
                    return result;
                }
            } else {
                IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);
                return result;
            }
        } catch (Exception ex) {
            IjarahErrors.ERR_GET_VOUCHER_DETAILS_SERVICE_FAILED_018.setErrorCode(result);
            return result;
        }
    }

    private boolean preProcess(DataControllerRequest dataControllerRequest) {
        try {
            if (IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("voucherStatus"))
                    || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("voucherCode"))
                    || IjarahHelperMethods.isBlank(dataControllerRequest.getParameter("voucherId"))) {
                return false;
            } else if (!dataControllerRequest.getParameter("voucherStatus").equals("REDEEMED")) {
                return false;
            }
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return true;
    }

    private Map<String, String> createInputParamsForUpdateVoucherService(Map<String, String> inputParam) {
        Map<String, String> inputParamLocal = new HashMap<>();
        inputParamLocal.put("id", inputParam.get("voucherId"));
        inputParamLocal.put("voucherCode", inputParam.get("voucherCode"));
        inputParamLocal.put("voucherStatus", VoucherStatus.REDEEMED.name());
        inputParamLocal.put("T24Status", VoucherStatus.REDEEMED.name());
        return inputParamLocal;
    }

    private Result redeemVoucher(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result redeemVoucher = ServiceCaller.internal(MURABAHA_T24_JSON_2_SERVICE_ID, REDEEM_VOUCHER_OPERATION_ID, inputParams, null, dataControllerRequest);
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(redeemVoucher);
            dataControllerRequest.addRequestParam_("NationalID", "");
            auditLogData(dataControllerRequest, inputRequest, outputResponse, MURABAHA_T24_JSON_2_SERVICE_ID + " : " + REDEEM_VOUCHER_OPERATION_ID);

            if (IjarahHelperMethods.hasSuccessCode(redeemVoucher)) {
                StatusEnum.success.setStatus(redeemVoucher);
                return redeemVoucher;
            }
        } catch (Exception ex) {
            LOG.error("ERROR redeemVoucher :: " + ex);
        }
        LOG.error("redeemVoucher1");
        return result;
    }

    private Result updateVoucherData(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        return ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, VOUCHER_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest);
    }
}