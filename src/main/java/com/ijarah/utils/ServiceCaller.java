package com.ijarah.utils;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.ijarah.utils.enums.EnvironmentConfig;
import com.ijarah.utils.enums.StatusEnum;
import com.ijarah.utils.interfaces.AbstractInvocationWrapper;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServiceCaller {
    private static final Logger LOG = Logger.getLogger(ServiceCaller.class);
    private FabricRequestManager fabricRequestManager;
    private DataControllerRequest dataControllerRequest;
    private FabricHelperMethods helper = null;
    private AbstractInvocationWrapper invocationWrapper = null;

    public ServiceCaller(FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest) {
        this.fabricRequestManager = fabricRequestManager;
        this.dataControllerRequest = dataControllerRequest;

        this.helper = new FabricHelperMethods(this.dataControllerRequest, this.fabricRequestManager);
        this.invocationWrapper = new DBPServiceInvocationWrapper(this.dataControllerRequest, this.fabricRequestManager);
    }

    public String getAPIUserIdFromSession(FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest) {
        String userId = this.fabricRequestManager != null ?
                this.helper.getAPIUserIdFromSession(this.fabricRequestManager) :
                this.helper.getAPIUserIdFromSession(this.dataControllerRequest);

        return userId;
    }

    public Result invokeServiceAndGetResult(String serviceId, String objectId, String operationId, Map<String, String> inputParams) throws Exception {
        return this.fabricRequestManager != null ?
                this.invocationWrapper.invokeServiceAndGetResult(serviceId, objectId, operationId, this.helper.convertToObjectMap(inputParams), convertMap(this.helper.getHeaders(this.fabricRequestManager)), this.fabricRequestManager) :
                this.invocationWrapper.invokeServiceAndGetResult(serviceId, objectId, operationId, this.helper.convertToObjectMap(inputParams), convertMap(this.helper.getHeaders(this.dataControllerRequest)), this.dataControllerRequest);

    }

    /**
     * internal calls don't have User_id in session and needs to be taken from parameters
     *
     * @param serviceId
     * @param operationId
     * @param inputParams
     * @param fabricRequestManager  pass null value if you have dataControllerRequest
     * @param dataControllerRequest pass null value if you have fabricRequestManager
     * @return
     */
    public static Result internal(String serviceId, String operationId, Map<String, String> inputParams, FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest) {
        ServiceCaller servicesCaller = new ServiceCaller(fabricRequestManager, dataControllerRequest);
        servicesCaller.helper.removeNullValues(inputParams);

        /*

        String userId = servicesCaller.getAPIUserIdFromSession(fabricRequestManager, dataControllerRequest);
        if (StringUtils.isNotEmpty(userId)) {
            inputParams.put("User_id", userId);
        }

        String devLogId = devLoggingRequest(inputParams, fabricRequestManager, dataControllerRequest, serviceId, operationId);

         */
        try {
            Result result = servicesCaller.invokeServiceAndGetResult(serviceId, null, operationId, inputParams);
            //devLoggingResponse(devLogId, result, fabricRequestManager, dataControllerRequest, serviceId, operationId);
            return result;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return StatusEnum.error.setStatus();
        }
    }

    public static Result internalDB(String serviceId, String operationId, Map<String, String> inputParams, FabricRequestManager requestManager, DataControllerRequest dcRequest) {
        String prefix = dcRequest != null ? EnvironmentConfig.SME_SCHEMA_NAME_IJARAH.getValue(dcRequest) :
                EnvironmentConfig.SME_SCHEMA_NAME_IJARAH.getValue(dcRequest);

        return internal(serviceId, prefix + "_" + operationId, inputParams, requestManager, dcRequest);
    }

    private static void devLogging(Map<String, String> inputParams, FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest) {
        boolean devFlagLogging = "true".equalsIgnoreCase(fabricRequestManager != null ?
                EnvironmentConfigurationsHandler.getValue("DEV_API_LOGS", fabricRequestManager) :
                EnvironmentConfigurationsHandler.getValue("DEV_API_LOGS", dataControllerRequest));

        if (devFlagLogging) {
            try {
                callDevLogging(fabricRequestManager, dataControllerRequest, inputParams);
            } catch (Exception e) {
                LOG.error("Failed to log api call in dev db " + e.getMessage());
            }
        }
    }

    /**
     * @param inputParams           for logging
     * @param fabricRequestManager  pass null value if you have dataControllerRequest
     * @param dataControllerRequest pass null value if you have fabricRequestManager
     * @param serviceId             for logging
     * @param operationId           for logging
     */
    private static String devLoggingRequest(Map<String, String> inputParams, FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest, String serviceId, String operationId) {
        String id = System.currentTimeMillis() + "";
        Map<String, String> inputs = new HashMap<>();
        inputs.put("requestId", id);
        inputs.put("module", serviceId + "/" + operationId + " request");
        inputs.put("message", inputParams.toString() + "");
        devLogging(inputs, fabricRequestManager, dataControllerRequest);
        return id;
    }

    /**
     * @param id                    generated from devLoggingRequest()
     * @param response              for logging
     * @param fabricRequestManager  pass null value if you have dataControllerRequest
     * @param dataControllerRequest pass null value if you have fabricRequestManager
     * @param serviceId             for logging
     * @param operationId           for logging
     */
    private static void devLoggingResponse(String id, Result response, FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest, String serviceId, String operationId) {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("requestId", id);
        inputs.put("module", serviceId + "/" + operationId + " response");
        inputs.put("message", IjarahHelperMethods.constructJsonFromResult(response) + "");
        devLogging(inputs, fabricRequestManager, dataControllerRequest);
    }

    private static Result callDevLogging(FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest, Map<String, String> inputs) throws Exception {
        String devLog = "DevLog";
        String devLogOperation = "dev_logs_logs_create";
        ServiceCaller servicesCaller = new ServiceCaller(fabricRequestManager, dataControllerRequest);
        return servicesCaller.invokeServiceAndGetResult(devLog, null, devLogOperation, inputs);
    }

    public static Result devLog(String subject, String message, FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest) throws Exception {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("module", subject);
        inputs.put("message", message);
        return ServiceCaller.callDevLogging(fabricRequestManager, dataControllerRequest, inputs);
    }

    /**
     * @param filter                can be empty String or null
     * @param url                   String
     * @param fabricRequestManager  pass null value if you have dataControllerRequest
     * @param dataControllerRequest pass null value if you have fabricRequestManager
     * @return
     */
    public static Result get(String filter, String url, FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest) {
        ServiceCaller servicesCaller = new ServiceCaller(fabricRequestManager, dataControllerRequest);
        try {
            return fabricRequestManager != null ?
                    HelperMethods.callGetApi(fabricRequestManager, filter, HelperMethods.getHeaders(fabricRequestManager), url) :
                    HelperMethods.callGetApi(dataControllerRequest, filter, HelperMethods.getHeaders(dataControllerRequest), url);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return StatusEnum.error.setStatus();
        }
    }

    /**
     * @param inputParams
     * @param url
     * @param fabricRequestManager  pass null value if you have dataControllerRequest
     * @param dataControllerRequest pass null value if you have fabricRequestManager
     * @return
     */
    public static Result post(Map<String, String> inputParams, String url, FabricRequestManager fabricRequestManager, DataControllerRequest dataControllerRequest) {
        ServiceCaller servicesCaller = new ServiceCaller(fabricRequestManager, dataControllerRequest);
        servicesCaller.helper.removeNullValues(inputParams);

        String userId = servicesCaller.getAPIUserIdFromSession(fabricRequestManager, dataControllerRequest);
        if (StringUtils.isNotEmpty(userId)) {
            inputParams.put("User_id", userId);
        }

        String devLogId = devLoggingRequest(inputParams, fabricRequestManager, dataControllerRequest, "URI", url);

        try {
            Result result = fabricRequestManager != null ?
                    servicesCaller.helper.callApi(fabricRequestManager, inputParams, HelperMethods.getHeaders(fabricRequestManager), url) :
                    servicesCaller.helper.callApi(dataControllerRequest, inputParams, HelperMethods.getHeaders(dataControllerRequest), url);

            devLoggingResponse(devLogId, result, fabricRequestManager, dataControllerRequest, "URI", url);

            return result;
        } catch (HttpCallException e) {
            LOG.error(e.getMessage());
            return StatusEnum.error.setStatus();
        }
    }

    public static Map<String, Object> convertMap(Map<String, String> map) {
        Map<String, Object> resultMap = new HashMap<>();
        if (map == null || map.isEmpty()) {
            return resultMap;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            resultMap.put(entry.getKey(), entry.getValue());
        }
        return resultMap;
    }

    public static boolean auditLogData(DataControllerRequest request, String req, String res, String apiHost) throws DBPApplicationException {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        String cusId = request.getParameter("NationalID");
        String logResponse;
        String channelDevice = "Mobile";
        String ipAddress = request.getRemoteAddr();
        HashMap<String, Object> logdataRequestMap = new HashMap<>();
        logdataRequestMap.put("id", uuidAsString);
        logdataRequestMap.put("Customer_id", cusId);
        logdataRequestMap.put("Application_id", "");
        logdataRequestMap.put("channelDevice", channelDevice);
        logdataRequestMap.put("apihost", apiHost);
        logdataRequestMap.put("request_payload", req);
        logdataRequestMap.put("reponse_payload", res);
        logdataRequestMap.put("ipAddress", ipAddress);
        logResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
                .withOperationId("dbxlogs_auditlog_create").withRequestParameters(logdataRequestMap).build()
                .getResponse();
        if (logResponse != null && logResponse.length() > 0)
            return true;
        return false;
    }

}
