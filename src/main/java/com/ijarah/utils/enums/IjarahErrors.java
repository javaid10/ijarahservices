package com.ijarah.utils.enums;


import com.google.gson.JsonObject;
import com.ijarah.utils.IjarahHelperMethods;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public enum IjarahErrors {
    // Pre-login Related Errors.
    ERR_PRE_LOGIN_001("001_001", "Unable to validate user."),

    ERR_PREPROCESS_INVALID_INPUT_PARAMS_001("000_000", "Input params are not valid"),
    ERR_PREPROCESS_INVALID_RESPONSE_PARAMS_001("000_000", "Response is not valid"),

    ERR_CREATE_LOAN_002("002", "Create loan Service failed"),
    ERR_CREATE_LOAN_003("003", "No customers found for loan creation"),
    ERR_NO_CUSTOMER_RECORD_FOUND_004("004", "No Customer Record Found"),
    ERR_ACTIVATE_CUSTOMER_FAILED_005("005", "Customer Activation Failed"),
    ERR_LOAN_CREATION_FAILED_006("006", "Loan Creation Failed"),
    ERR_NAFAES_DATA_NOT_FOUND_007("007", "No Nafaes Data found for Customer"),
    ERR_TRANSFER_ORDER_OR_SALE_ORDER_008("008", "Transfer Order OR Sale Order Got Failed"),
    ERR_CUSTOMER_APPLICATION_DATA_NOT_FOUND_009("009", "No Customer Application Record Found"),
    ERR_CUSTOMER_COMMUNICATION_DATA_NOT_FOUND_010("010", "No Customer Communication Record Found"),
    ERR_GET_ACCESS_TOKEN_FAILED_011("011", "Get Access Token Service Failed"),
    ERR_SINGLE_SANAD_CREATION_FAILED_012("012", "Single Sanad Creation Service Failed"),
    ERR_CREATE_NAFAITH_RECORD_FAILED_013("013", "Create Nafaith Record Failed"),
    ERR_UPDATE_DOCUMENT_STORAGE_014("014", "Update Document Storage Record Failed"),
    ERR_UNABLE_TO_SIGN_DOCUMENT_015("015", "Unable To Get Document Signed By Emdha"),
    ERR_NO_DOCUMENT_FOUND_016("016", "No Document Found"),
    ERR_NO_DOCUMENT_STORAGE_RECORD_FOUND_017("017", "No Document Record Found"),
    ERR_660028("660028", "Knockout Failed, Application has Declined"),
    ERR_GET_VOUCHER_DETAILS_SERVICE_FAILED_018("018", "Get Voucher Details Service Failed."),
    ERR_GET_USER_ID_FAILED_019("019", "Failed to fetch User ID from Session."),
    ERR_GET_USER_ID_FAILED_020("020", "No voucher found"),
    ERR_SIMHA_CONSUMER_INQUIRY_FAILED("017", "Consumer Enquiry Failed"),

    ERR_GET_REAPPLY_DATE_SERVICE_FAILED_038("038", "Get Re Apply Date Failed Service Failed."),
    ERR_GET_CUSTOMER_APPLICATION_SERVICE_FAILED_039("039", "Get Customer Application Journey Service Failed."),
    ERR_GET_SANAD_ITERATION_SERVICE_FAILED_040("040", "Get Sanad Iteration Failed.");

    public static final String ERROR_CODE_KEY = "dbpErrCode";
    public static final String ERROR_MESSAGE_KEY = "dbpErrMsg";
    public static final String STATUS_KEY = "status";
    public static final String OP_STATUS_CODE = "opstatus";
    public static final String HTTP_STATUS_CODE = "httpStatusCode";
    private final String errorCode;
    private final String message;

    IjarahErrors(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public Result setErrorCode() {
        Result result = new Result();
        StatusEnum.error.setStatus(result);

        result.addParam(new Param(ERROR_CODE_KEY, this.errorCode, "string"));
        result.addParam(new Param(ERROR_MESSAGE_KEY, this.message, "string"));
        result.addParam(new Param(OP_STATUS_CODE, "0", "int"));
        result.addParam(new Param(HTTP_STATUS_CODE, "0", "int"));

        return result;
    }

    public void setErrorCode(Result result) {
        if (result == null) {
            result = new Result();
        }
        StatusEnum.error.setStatus(result);
        result.addParam(new Param(ERROR_CODE_KEY, this.errorCode, "string"));
        result.addParam(new Param(ERROR_MESSAGE_KEY, this.message, "string"));
        result.addParam(new Param(OP_STATUS_CODE, "0", "int"));
        result.addParam(new Param(HTTP_STATUS_CODE, "0", "int"));

    }


    public JsonObject setErrorCodeJson() {
        JsonObject result = new JsonObject();
        result.addProperty(STATUS_KEY, StatusEnum.error.toString());
        return result;
    }


    public JsonObject setStatusJson(JsonObject result) {
        if (result == null) {
            result = new JsonObject();
        }

        result.addProperty(STATUS_KEY, StatusEnum.error.toString());
        result.addProperty(ERROR_CODE_KEY, this.errorCode);
        result.addProperty(ERROR_MESSAGE_KEY, this.message);
        result.addProperty(OP_STATUS_CODE, "0");
        result.addProperty(HTTP_STATUS_CODE, "0");

        return result;
    }

    public JsonObject setErrorCodeJson(JsonObject result, String errorMessage) {
        if (result == null) {
            result = new JsonObject();
        }

        result.addProperty(ERROR_MESSAGE_KEY, errorMessage);
        result.addProperty(STATUS_KEY, StatusEnum.error.toString());
        result.addProperty(ERROR_CODE_KEY, this.errorCode);
        result.addProperty(OP_STATUS_CODE, "0");
        result.addProperty(HTTP_STATUS_CODE, "0");

        return result;

    }

    public static boolean hasSuccess(Result result) {
        if (result == null) {
            return false;
        }

        if (IjarahHelperMethods.hasParam(result, STATUS_KEY)) {
            try {
                return StatusEnum.valueOf(result.getParamValueByName(STATUS_KEY)) == StatusEnum.success;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return false;
    }

    public static boolean hasError(Result result) {
        if (result == null) {
            return false;
        }

        if (IjarahHelperMethods.hasParam(result, STATUS_KEY)) {
            try {
                return StatusEnum.valueOf(result.getParamValueByName(STATUS_KEY)) == StatusEnum.error;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }
}