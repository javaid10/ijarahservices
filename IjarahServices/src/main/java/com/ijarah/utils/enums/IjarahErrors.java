package com.ijarah.utils.enums;


import com.google.gson.JsonObject;
import com.ijarah.utils.IjarahHelperMethods;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public enum IjarahErrors {
    // Pre-login Related Errors.
    ERR_PRE_LOGIN_001("001_001", "Unable to validate user."),

    ERR_PREPROCESS_INVALID_INPUT_PARAMS_001("000_000", "Input params are not valid");

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