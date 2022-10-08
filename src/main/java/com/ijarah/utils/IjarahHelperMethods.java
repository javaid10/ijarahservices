package com.ijarah.utils;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.adminconsole.commons.utils.CommonUtilities;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.*;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.exceptions.MiddlewareException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kony.dbputilities.util.HelperMethods.getHeaders;

public class IjarahHelperMethods {
    private static final Logger LOG = Logger.getLogger(IjarahHelperMethods.class);

    public static final String DATE_FORMAT_WITH_TIME = "yyyy-MM-dd'T'hh:mm:ss";
    public static final String DATE_FORMAT_WITH_SECONDS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
    public static final String DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String DATE_FORMAT_WITH_TIME_HH = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_d_MMM_yyyy = "d-MMM-yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_dd_MM_yyyy = "dd.MM.yyyy";
    public static final String DATE_FORMAT_ddMMyyyy = "ddMMyyyy";
    public static final String DATE_FORMAT_WITH_SECONDS_AND_T = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATE_FORMAT_WITH_SECONDS_MS = "yyyy-MM-dd HH:mm:ss.S";
    public static final String ALPHANUMERIC_REGEX = "^[a-zA-Z0-9]+$";
    public static final String NUMBERS_REGEX = "\\d+";
    public static final String PHONE_PREFIX_INDIA = "968";
    public static final String PHONE_PREFIX_MPCLEAR = "00968";
    public static final String DATE_FORMAT_WITH_AM_MP = "dd/MM/YYYY HH:mm a";

    //TODO: Replace those with utils from
    //org.apache.commons.lang3.StringUtils;
    public static boolean isNotBlank(String s) {
        return null != s && !s.isEmpty() && !s.trim().isEmpty();
    }

    public static boolean isBlank(String s) {
        return null == s || s.isEmpty() || s.trim().isEmpty();
    }


    public static String getStringFromJsonObject(JsonObject object, String key, boolean required) {
        JsonElement element = getElementFromJsonObject(object, key, required);
        return isJsonEleNull(element) ? null : element.getAsString();
    }

    public static JsonElement getElementFromJsonObject(JsonObject object, String key, boolean required) {
        JsonElement element = object.get(key);
        if (isJsonEleNull(element) && required) {
            throw new IllegalArgumentException("Required attribute '" + key + "' was not present");
        }
        return element;
    }

    public static boolean isJsonEleNull(JsonElement ele) {
        return null == ele || ele.isJsonNull();
    }

    public static void setValidationErrorMsg(String message, Result result) {
        Param validionMsg = new Param("errorMessage", message, "String");
        result.addParam(validionMsg);
        Param status = new Param("httpStatusCode", "400", "String");
        result.addParam(status);
        Param opstatus = new Param("opstatus", "400", "String");
        result.addParam(opstatus);
    }

    public static void setSuccessMsg(String message, Result result) {
        Param validionMsg = new Param("success", message, "String");
        result.addParam(validionMsg);
    }

    public static Result constructResultFromJSONObject(JSONObject JSONObject) {
        Result response = new Result();
        if (JSONObject == null || JSONObject.length() == 0) {
            return response;
        }
        Iterator<String> keys = JSONObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            if (JSONObject.get(key) instanceof String) {
                Param param = new Param(key, JSONObject.getString(key), DBPConstants.FABRIC_STRING_CONSTANT_KEY);
                response.addParam(param);

            } else if (JSONObject.get(key) instanceof Integer) {
                Param param = new Param(key, JSONObject.get(key).toString(), DBPConstants.FABRIC_INT_CONSTANT_KEY);
                response.addParam(param);

            } else if (JSONObject.get(key) instanceof Boolean) {
                Param param = new Param(key, JSONObject.get(key).toString(), DBPConstants.FABRIC_BOOLEAN_CONSTANT_KEY);
                response.addParam(param);

            } else if (JSONObject.get(key) instanceof JSONArray) {
                Dataset dataset = CommonUtilities.constructDatasetFromJSONArray(JSONObject.getJSONArray(key));
                dataset.setId(key);
                response.addDataset(dataset);

            } else if (JSONObject.get(key) instanceof JSONObject) {
                Record record = CommonUtilities.constructRecordFromJSONObject(JSONObject.getJSONObject(key));
                record.setId(key);
                response.addRecord(record);

            }
        }

        return response;
    }

    public static JsonObject constructJsonFromResult(Result result) {
        JsonObject response = new JsonObject();
        if (result == null) {
            return response;
        }

        List<Param> params = result.getAllParams();

        for (Param param : params) {
            response.addProperty(param.getName(), param.getValue());
        }

        List<Dataset> datasets = result.getAllDatasets();
        for (Dataset dataset : datasets) {
            response.add(dataset.getId(), constructJsonfromDataset(dataset));
        }

        List<Record> records = result.getAllRecords();
        for (Record record : records) {
            response.add(record.getId(), constructJsonfromRecord(record));
        }

        return response;
    }

    private static JsonElement constructJsonfromRecord(Record parentRecord) {
        JsonObject response = new JsonObject();
        if (parentRecord == null) {
            return response;
        }

        List<Param> params = parentRecord.getAllParams();

        for (Param param : params) {
            response.addProperty(param.getName(), param.getValue());
        }

        List<Dataset> datasets = parentRecord.getAllDatasets();
        for (Dataset dataset : datasets) {
            response.add(dataset.getId(), constructJsonfromDataset(dataset));
        }

        List<Record> records = parentRecord.getAllRecords();
        for (Record record : records) {
            response.add(record.getId(), constructJsonfromRecord(record));
        }

        return response;
    }

    public static JsonElement constructJsonfromDataset(Dataset parentDataset) {
        JsonArray response = new JsonArray();
        if (parentDataset == null) {
            return response;
        }

        List<Record> records = parentDataset.getAllRecords();
        for (Record record : records) {
            response.add(constructJsonfromRecord(record));
        }

        return response;
    }

    public static Map<String, String> getEntries(Map<String, String> fromMap, String... keys) {
        Map<String, String> resultMap = new HashMap<>();
        for (String key : keys) {
            resultMap.put(key, fromMap.get(key));
        }

        return resultMap;
    }

    public static ZonedDateTime getNowOmanDateTime() {
        Instant nowUtc = Instant.now();
        ZoneId asiaMuscat = ZoneId.of("Asia/Muscat");
        return ZonedDateTime.ofInstant(nowUtc, asiaMuscat);
    }

    public static LocalDateTime getNowLocalDateTime() {
        return LocalDateTime.now();
    }

    public static String getCurrentTimeStamp(String pattern) {
        return getNowLocalDateTime().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date formatedDate = simpleDateFormat.parse(date);
        return formatter.format(formatedDate);
    }

    public static String formatDateIslamic(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date formattedDate = formatter.parse(date);
        return simpleDateFormat.format(formattedDate);
    }

    public static String formatDateForIslamic(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date formattedDate = formatter.parse(date);
        return simpleDateFormat.format(formattedDate);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static int generateRandomInt() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

    public static boolean hasSuccessCode(Result toCheck) {
        return "200".equals(toCheck.getParamValueByName("httpStatusCode"));
    }

    public static boolean hasSuccessCode(JsonObject toCheck) {
        return toCheck.has("httpStatusCode") &&
                !toCheck.get("httpStatusCode").isJsonNull() &&
                "200".equals(toCheck.get("httpStatusCode").getAsString());
    }

    public static boolean hasErrorCode(Result toCheck) {
        return !"0".equals(toCheck.getParamValueByName("errorCode"));
    }

    public static boolean hasErrorOpstatus(Result toCheck) {
        return !"0".equals(toCheck.getParamValueByName("opstatus"));
    }

    public static boolean hasErrorHttpStatusCode(Result toCheck) {
        return !"200".equals(toCheck.getParamValueByName("httpStatusCode"));
    }

    public static boolean accountNumberStatusOk(Result toCheck) {
        return "0".equals(toCheck.getParamValueByName("responseCode")) &&
                "OK".equalsIgnoreCase(toCheck.getParamValueByName("accountNumberStatus"));
    }

    public static boolean noErrorsCode(Result toCheck) {
        return "0".equals(toCheck.getParamValueByName("errorCode"));
    }

    public static LocalDate parseDate(String date, String pattern) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(date, formatter);
    }

    public static String getDate(LocalDateTime date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getJsonField(JsonObject object, String key) {
        return JSONUtil.isJsonNotNull(object.get(key)) ? object.get(key).getAsString() : "";
    }

    public static boolean hasParam(Result result, String fieldName) {
        return result.getParamValueByName(fieldName) != null;
    }

    public static boolean hasParam(JsonObject json, String fieldName) {
        return json.has(fieldName) && !json.isJsonNull();
    }

    public static String convertToCamelCase(String name) {
        name = name.toLowerCase();
        StringBuilder titleCase = new StringBuilder(name.length());
        boolean nextTitleCase = true;

        for (char c : name.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return String.valueOf(titleCase);
    }

    public static String getCifFromUserId(String userId, FabricRequestManager requestManager) throws HttpCallException {
//        if (requestManager.getSessionHandler().getAttribute("retailUserCif") != null) {
//            return requestManager.getSessionHandler().getAttribute("retailUserCif").toString();
//        }

        String CIF = null;
        String filter = "id eq " + userId;
        Result customer = HelperMethods.callGetApi(requestManager, filter, HelperMethods.getHeaders(requestManager), "CustomerVerify.readRecord");
        if (HelperMethods.hasRecords(customer))
            CIF = HelperMethods.getFieldValue(customer, "Ssn");

//        requestManager.getSessionHandler().setAttribute("retailUserCif", CIF);

        return CIF;
    }

    public static String getCompanyCode(String userId, DataControllerRequest requestManager) {
        String companyCode = null;
        String filter = "id eq " + userId;
        Result customerResult = null;
        try {
            customerResult = HelperMethods.callGetApi(requestManager, filter, HelperMethods.getHeaders(requestManager), "CustomerVerify.readRecord");
        } catch (HttpCallException e) {
            return "";
        }
        if (HelperMethods.hasRecords(customerResult))
            // TODO: Make sure the colomn in DB has the same name 'companyCode'
            companyCode = HelperMethods.getFieldValue(customerResult, "companyCode");
        return companyCode;
    }

    public static JsonObject addCifToResult(FabricRequestManager requestManager, String CIF) {
        PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
        JsonObject resultJson = new JsonObject();
        if (requestPayloadHandler.getPayloadAsJson() != null)
            resultJson = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();
        resultJson.addProperty("cif-id", CIF);
        resultJson.addProperty("cif", CIF);
        return resultJson;
    }

    public static String getDeviceId(DataControllerRequest dcRequest) throws Exception {
        if (StringUtils.isNotBlank(dcRequest.getHeader("X-Kony-ReportingParams"))) {
            JSONObject reportingParamsJson = new JSONObject(
                    URLDecoder.decode(dcRequest.getHeader("X-Kony-ReportingParams"), StandardCharsets.UTF_8.name()));
            return reportingParamsJson.optString("did");
        }
        throw new Exception("Could not fetch device id!");
    }

    public static String getReportingParam(DataControllerRequest dcRequest, String param) throws Exception {
        if (StringUtils.isNotBlank(dcRequest.getHeader("X-Kony-ReportingParams"))) {
            JSONObject reportingParamsJson = new JSONObject(
                    URLDecoder.decode(dcRequest.getHeader("X-Kony-ReportingParams"), StandardCharsets.UTF_8.name()));
            return reportingParamsJson.optString(param);
        }
        throw new Exception("Could not fetch param " + param);
    }

    public static boolean isSuccessfulRequest(Result result) {
        return result.getParamByName("opstatus").getValue().equals("0");
    }

    public static boolean verifyAccountsForUser(String userId, FabricRequestManager requestManager, DataControllerRequest dcRequest, String... accounts) {
        if (accounts.length == 0) return false;

        StringBuilder accountsQuery = new StringBuilder();
        Arrays.stream(accounts).forEach(account -> accountsQuery.append("or Account_id eq ").append(account).append(" "));
        // Sample: accountsQuery gives -> "or Account_id eq 1234 or Account_id eq 5432 or Account_id eq 6321 "
        String filter = "(" + accountsQuery.substring(accountsQuery.indexOf("Account_id")) + ")" + " and User_id eq " + userId;
        Map<String, String> params = new HashMap<>();
        params.put(DBPUtilitiesConstants.FILTER, filter);
        return HelperMethods.hasRecords(ServiceCaller.internalDB("dbpRbLocalServicesdb", "accounts_get", params, requestManager, dcRequest));
    }

    public static LocalTime parseHours(String hour, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(hour, formatter);
    }

    public static String formatAmount(String value, String currency) {
        if (StringUtils.isNotEmpty(value)) {
            BigDecimal bigDecimal;
            try {
                bigDecimal = new BigDecimal(value);
            } catch (NumberFormatException e) {
                return value;
            }

            return bigDecimal.movePointLeft("OMR".equalsIgnoreCase(currency) ? 3 : 2).toString();
        }
        return value;
    }

    public static String formatSignedAmount(String value, String currency) {
        if (StringUtils.isNotEmpty(value) &&
                (value.startsWith("+") || value.startsWith("-"))) {
            String sign = value.substring(0, 1);
            String removedSign = value.substring(1);
            BigDecimal bigDecimal;
            try {
                bigDecimal = new BigDecimal(removedSign);
            } catch (NumberFormatException e) {
                return value;
            }

            String formattedValue = bigDecimal.movePointLeft("OMR".equalsIgnoreCase(currency) ? 3 : 2).toString();

            return "-".equals(sign) ? "-" + formattedValue : formattedValue;
        }
        return value;
    }

    public static String formatPercentage(String value) {
        if (StringUtils.isNotEmpty(value)) {
            BigDecimal bigDecimal;
            try {
                bigDecimal = new BigDecimal(value);
            } catch (NumberFormatException e) {
                return value;
            }
            return bigDecimal.setScale(2, RoundingMode.CEILING).toString();
        }

        return value;
    }
    public static String checkStringNull(String value) {
        if (value != null && !value.isEmpty()){
            return value;
        }else{
            return "";
        }
    }
    public static String formatInteger(String value) {
        if (StringUtils.isNotEmpty(value)) {
            int integer;
            try {
                integer = Integer.parseInt(value);
            } catch (Exception e) {
                return value;
            }
            return "" + integer;
        }

        return value;
    }

    public static boolean isPasswordValid(String password, Map<String, String> input, JsonObject resultJson, FabricRequestManager requestManager) throws HttpCallException {
        JsonObject rules = AdminUtil.invokeAPIAndGetJson(input, URLConstants.ADMIN_USERNAME_PASSWORD_RULES,
                requestManager);
        Map<String, String> passwordRules = HelperMethods.getRecordMap(rules.get("passwordrules").toString());

        return validatePassword(password, passwordRules, resultJson);
    }

    private static boolean validatePassword(String password, Map<String, String> passwordRules, JsonObject resultJson) {
        int passMinLength = (passwordRules.get("minLength") != null ? Integer.parseInt((passwordRules.get("minLength")))
                : 8);
        int passMaxLength = (passwordRules.get("maxLength") != null ? Integer.parseInt((passwordRules.get("maxLength")))
                : 64);
        Boolean lowerCase = (passwordRules.get("atleastOneLowerCase") != null
                ? Boolean.parseBoolean((passwordRules.get("maxLength")))
                : true);
        Boolean upperCase = (passwordRules.get("atleastOneUpperCase") != null
                ? Boolean.parseBoolean((passwordRules.get("atleastOneUpperCase")))
                : true);
        Boolean number = (passwordRules.get("atleastOneNumber") != null
                ? Boolean.parseBoolean((passwordRules.get("atleastOneNumber")))
                : true);
        Boolean symbol = (passwordRules.get("atleastOneSymbol") != null
                ? Boolean.parseBoolean((passwordRules.get("atleastOneSymbol")))
                : true);
        int charRepeatCount = (passwordRules.get("charRepeatCount") != null
                ? Integer.parseInt((passwordRules.get("charRepeatCount")))
                : 4);
        String supportedSymbols = (passwordRules.get("supportedSymbols") != null
                ? (passwordRules.get("supportedSymbols"))
                : ".,-,_,@,!,#,$");
        String givenSupportedSymbols = supportedSymbols;

        supportedSymbols = supportedSymbols.replace(",", "");
        if (supportedSymbols.contains("-")) {
            supportedSymbols = supportedSymbols.replace("-", "\\-");
        }

        if (StringUtils.isBlank(password)) {
            ErrorCodeEnum.ERR_10125.setErrorCode(resultJson);
            return false;
        }
        if (!(password.length() >= passMinLength && password.length() <= passMaxLength)) {
            ErrorCodeEnum.ERR_10126.setErrorCode(resultJson,
                    " password length should be in between the limits " + passMinLength + " and " + passMaxLength);
            return false;
        }
        if (Boolean.TRUE.equals(lowerCase)) {
            Pattern pattern = Pattern.compile("^(?=[^a-z]*[a-z])");
            Matcher m = pattern.matcher(password);
            if (!m.lookingAt()) {
                ErrorCodeEnum.ERR_10127.setErrorCode(resultJson);
                return false;
            }
        }
        if (Boolean.TRUE.equals(upperCase)) {
            Pattern pattern = Pattern.compile("^(?=[^A-Z]*[A-Z])");
            Matcher m = pattern.matcher(password);
            if (!m.lookingAt()) {
                ErrorCodeEnum.ERR_10128.setErrorCode(resultJson);
                return false;
            }
        }
        if (Boolean.TRUE.equals(number)) {
            Pattern pattern = Pattern.compile("^(?=\\D*\\d)");
            Matcher m = pattern.matcher(password);
            if (!m.lookingAt()) {
                ErrorCodeEnum.ERR_10129.setErrorCode(resultJson);
                return false;
            }
        }
        if (Boolean.TRUE.equals(symbol)) {
            Pattern pattern = Pattern.compile("^(?=.*[" + supportedSymbols + "])");
            Matcher m = pattern.matcher(password);
            if (!m.lookingAt()) {
                ErrorCodeEnum.ERR_10130.setErrorCode(resultJson);
                return false;
            }
            Pattern newpattern = Pattern.compile("^(?=.*[^\\s\\w\\d" + supportedSymbols + "])");
            Matcher newm = newpattern.matcher(password);
            if (newm.find()) {
                ErrorCodeEnum.ERR_10131.setErrorCode(resultJson, "special characters other than these "
                        + givenSupportedSymbols + " are not allowed for password");
                return false;
            }
        }
        if (charRepeatCount > 0) {
            Pattern pattern = Pattern.compile("(.)\\1{" + charRepeatCount + ",}");
            Matcher m = pattern.matcher(password);
            if (m.find()) {
                ErrorCodeEnum.ERR_10132.setErrorCode(resultJson,
                        "Maximum number of times a character can be repeated consecutively in the password is "
                                + charRepeatCount);
                return false;
            }
        }
        return true;
    }

    public static String getAppMode(DataControllerRequest dcRequest) {
        return EnvironmentConfigurationsHandler.getValue("API_MODE", dcRequest);
    }

    public static String getAppMode(FabricRequestManager requestManager) {
        return EnvironmentConfigurationsHandler.getValue("API_MODE", requestManager);
    }

    public static String getFormattedPhone(String phone) {
        if (phone.length() <= 8) {
            return phone;
        }

        Pattern pattern = Pattern.compile("(\\+\\d{3})?(\\d+)");
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            return matcher.group(2);
        }
        return phone;
    }

    public static Result formatChargesResult(Result result, String... params) {
        Result resultFormatted = new Result();
        String fee = result.getParamValueByName(params[0]);
        String feeCurrency = result.getParamValueByName(params[1]);

        if ("withinOman".equals(params[1])) {
            resultFormatted.addParam("currency", "OMR");
        } else {
            resultFormatted.addParam("currency", feeCurrency);
        }

        resultFormatted.addParam("amount", fee);
        return resultFormatted;
    }

    private static String getCompanyUserAttributeFromSession(String keyName, FabricRequestManager requestManager, DataControllerRequest dcRequest) {
        try {
            Object item = (requestManager != null) ? requestManager.getSessionHandler().getAttribute(keyName) : dcRequest.getSession().getAttribute(keyName);
            if (item != null) {
                return item.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ServicesManager sm = (requestManager != null) ? requestManager.getServicesManager() : dcRequest.getServicesManager();
            return sm.getIdentityHandler().getUserAttributes().get(keyName).toString();

        } catch (MiddlewareException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCompanyCifFromSession(FabricRequestManager requestManager, DataControllerRequest dcRequest) {
        return getCompanyUserAttributeFromSession("companyCif", requestManager, dcRequest);
    }

    public static String getCompanyIdFromSession(FabricRequestManager requestManager, DataControllerRequest dcRequest) {
        return getCompanyUserAttributeFromSession("companyId", requestManager, dcRequest);
    }

    public static String convertIndicatorToSign(String indicator) {
        return indicator.equalsIgnoreCase("D") ? "-" : "+";
    }

    public static void MPClearMapInputs(Result result, JsonObject inputJson) {
        inputJson.addProperty("customerName", HelperMethods.getParamValue(result.getParamByName("name")));
        inputJson.addProperty("country", HelperMethods.getParamValue(result.getParamByName("country")));
        inputJson.addProperty("customerId", HelperMethods.getParamValue(result.getParamByName("nationalId")));
        String phoneNumber = HelperMethods.getParamValue(result.getParamByName("phone")).replace("+", "00");

        if (!phoneNumber.startsWith("00968")) {
            phoneNumber = PHONE_PREFIX_MPCLEAR + phoneNumber;
        }
        inputJson.addProperty("mobileNumber", phoneNumber);
        String birthDate = HelperMethods.getParamValue(result.getParamByName("birthDate"));
        inputJson.addProperty("dobOrRegistrationDate", birthDate);
    }

    public static void showErrorMessage(FabricResponseManager responseManager, String message) {
        PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
        JsonObject resultJson = new JsonObject();
        ErrorCodeEnum.ERR_10152.setErrorCode(resultJson, message);
        responsePayloadHandler.updatePayloadAsJson(resultJson);
    }

    public static String getValueFromResultByName(Result result, String paramName) {
        return hasParam(result, paramName) ? result.getParamValueByName(paramName) : "";
    }

    public static String getServerConfig(DataControllerRequest dcRequest, String key) {
        return EnvironmentConfigurationsHandler.getValue(key, dcRequest);
    }

    public static String getServerConfig(FabricRequestManager requestManager, String key) {
        return EnvironmentConfigurationsHandler.getValue(key, requestManager);
    }

    public static String getServerConfig(String key, FabricRequestManager requestManager, DataControllerRequest dcRequest) {
        if (requestManager != null) {
            return getServerConfig(requestManager, key);
        } else {
            return getServerConfig(dcRequest, key);
        }
    }

    public static void generalServiceResponseLogger(String serviceName, Result result, DataControllerRequest dcRequest, DataControllerResponse dcResponse, Logger LOG) {
        LOG.error("Datasource response for service " + serviceName + "-->" + dcResponse.getResponse());
    }

    public static List<Record> getAllRecordsFromResult(Result result) {
        Dataset ds = result.getAllDatasets().get(0);
        if (ds != null && ds.getAllRecords().size() > 0) {
            return ds.getAllRecords();
        }

        List<Record> emptyRecordList = Collections.<Record>emptyList();
        return emptyRecordList;
    }

    ;

    public static List<Record> getAllRecordsFromResult(Result result, String dataSetId) {
        if (StringUtils.isNotEmpty(dataSetId)) {
            Dataset ds = result.getDatasetById(dataSetId);
            if (ds != null && ds.getAllRecords().size() > 0) {
                return ds.getAllRecords();
            }
        }

        List<Record> emptyRecordList = Collections.<Record>emptyList();
        return emptyRecordList;
    }

    /**
     * Checks if given customer is corporate
     *
     * @param customer_id
     * @param dcRequest
     * @return true if corporate
     */
    public static boolean isCorporateUser(FabricRequestManager requestManager, DataControllerRequest dcRequest, String customer_id) throws HttpCallException {
        String filter = "id eq " + customer_id + DBPUtilitiesConstants.AND + "CustomerType_id" + DBPUtilitiesConstants.EQUAL + "TYPE_ID_CORPORATION";
        Result result = (requestManager != null ?
                HelperMethods.callGetApi(requestManager, filter, getHeaders(requestManager), "Customer.readRecord") :
                HelperMethods.callGetApi(dcRequest, filter, getHeaders(dcRequest), "Customer.readRecord"));
        return HelperMethods.hasRecords(result);
    }

    /**
     * Returns full name based on the input params - firstName and lastName
     *
     * @param firstName
     * @param lastName
     * @return
     */
    public static String buildFullName(String firstName, String lastName) {
        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
            return firstName + " " + lastName;
        }
        if (StringUtils.isBlank(firstName)) {
            return lastName;
        }

        return firstName;
    }

    /**
     * Returns safe value for given param from given record.
     *
     * @param record
     * @param paramName
     * @return
     */
    public static String returnSafeValue(Record record, String paramName) {
        return StringUtils.isNotEmpty(record.getParamValueByName(paramName)) ?
                record.getParamValueByName(paramName) : "";
    }

    /**
     * Returns phone number with a given prefix.
     *
     * @param phone
     * @param prefix
     * @return
     */
    public static String formatCustomerPhonePrefix(String phone, String prefix) {
        if (phone.length() <= 9) {
            phone = prefix + phone;
        }
        return phone;
    }

    /**
     * Returns cif by a given userid.
     *
     * @param dcRequest
     * @param userId
     * @return
     */
    private String getCifWithDataControllerRequest(DataControllerRequest dcRequest, String userId) {
        String CIF = null;
        Map<String, String> filter = new HashMap<>();
        filter.put(DBPUtilitiesConstants.FILTER, "id" + DBPUtilitiesConstants.EQUAL + userId);
        Result customer = ServiceCaller.internalDB("dbpRbLocalServicesdb", "customerverify_get", filter, null, dcRequest);
        if (HelperMethods.hasRecords(customer)) {
            CIF = HelperMethods.getFieldValue(customer, "Ssn");
        }
        return CIF;
    }

    public static String doFirstCharUpperCase(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static Map<String, String> writeCompanyId(Map<String, String> inputParams, DataControllerRequest dcRequest, String columnName) throws MiddlewareException {
        String customerType = dcRequest.getServicesManager().getIdentityHandler().getUserAttributes().getOrDefault("CustomerType_id", "").toString();
        if (StringUtils.isNotBlank(customerType) && "TYPE_ID_CORPORATION".equalsIgnoreCase(customerType)) {
            inputParams.put(columnName, getCompanyIdFromSession(null, dcRequest));
        }
        return inputParams;
    }

    /**
     * Receive date in format '2021-03-01 19:14:00:000'
     * and returns '2021-03-01'
     * Used in Islamic services
     *
     * @param date
     * @return
     */
    public static String splitDateForIslamic(String date) {
        if (StringUtils.isNotBlank(date)) {
            return date.split(" ")[0];
        }
        return date;
    }

    /**
     * Return number of months between given date and current date.
     * For example, dateFrom = '2021-01-31',
     * till now = '2021-02-31' will return - 1
     *
     * @param dateFrom
     * @return
     */
    public static int getMonthsFromDate(String dateFrom) {
        return Period.between(LocalDate.parse(dateFrom), LocalDate.now()).getMonths();
    }
}
