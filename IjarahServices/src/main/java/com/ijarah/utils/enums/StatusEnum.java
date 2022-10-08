package com.ijarah.utils.enums;


import com.google.gson.JsonObject;
import com.ijarah.utils.IjarahHelperMethods;
import com.kony.adminconsole.commons.utils.FabricConstants;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;


public enum StatusEnum {
    success("Success"),
    error("Failure");

    StatusEnum(String status) {
        this.status = status;
    }

    private final String status;

    public Result setStatus() {
        Result result = new Result();
        result.addParam(new Param("status", this.status, "string"));
        if (hasSuccess(result)) {
            result.addParam(new Param(FabricConstants.OPSTATUS, "0", FabricConstants.INT));
            result.addParam(new Param(FabricConstants.HTTP_STATUS_CODE, "200", FabricConstants.INT));
        }
        return result;
    }

    public void setStatus(Result result) {
        if (result == null) {
            result = new Result();
        }
        result.addParam(new Param("status", this.status, "string"));
        if (hasSuccess(result)) {
            result.addParam(new Param(FabricConstants.OPSTATUS, "0", FabricConstants.INT));
            result.addParam(new Param(FabricConstants.HTTP_STATUS_CODE, "200", FabricConstants.INT));
        }
    }

    public JsonObject setStatusJson() {
        JsonObject result = new JsonObject();
        result.addProperty("status", this.status);
        return result;
    }

    public JsonObject setStatusJson(JsonObject result) {
        if (result == null) {
            result = new JsonObject();
        }

        result.addProperty("status", this.status);

        return result;
    }

    public JsonObject setStatusJson(JsonObject result, String errorMessage) {
        if (result == null) {
            result = new JsonObject();
        }

        result.addProperty("errorMessage", errorMessage);
        result.addProperty("status", this.status);

        return result;
    }

    public static boolean hasSuccess(Result result) {
        if (result == null) {
            return false;
        }
        if (IjarahHelperMethods.hasParam(result, "status")) {
            try {
                return StatusEnum.valueOf(result.getParamValueByName("status").toLowerCase()) == success;
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
        if (IjarahHelperMethods.hasParam(result, "apperrormsg")) {
            try {
                return StatusEnum.valueOf(result.getParamValueByName("apperrormsg")) == error;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

}
