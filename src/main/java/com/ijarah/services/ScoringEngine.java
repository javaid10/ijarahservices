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
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.ijarah.utils.IjarahHelperMethods.*;
import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.ServiceIDConstants.*;

public class ScoringEngine implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(ScoringEngine.class);
    Map<String, String> inputParams = new HashMap<>();
    String MONTHLY_NET_SALARY = "";
    String CURRENT_LENGTH_OF_SERVICE = "";
    String GLOBAL_DTI = "";
    String INTERNAL_DTI = "";
    String CURRENT_DELINQUENCY = "";
    private String CURRENT_DELINQUENCY_T = "";
    String MAX_DELINQUENCY = "";
    String NON_FINANCIAL_DEFAULT_AMOUNT = "";
    String BOUNCED_CHEQUE = "";
    String COURT_JUDGEMENT = "";
    String EMPLOYER_CATEGORISATION = "";
    String MAX_LOAN_AMOUNT_CAPPING = "";
    String MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY = "";
    String NEW_TO_INDUSTRY = "";
    String SALARY_WITHOUT_ALLOWANCES = "";
    String INSIDE_KSA = "";
    String NATIONALITY = "";

    String EMPLOYER_TYPE_ID = "1";
    String EMPLOYMENT_STATUS = "";

    String[] MORTGAGE_PRODUCT = { "AMTG", "AQAR", "EMTG", "IMTG", "MMTG", "MSKN", "MTG", "OMTG", "RMSKN", "RMTG",
            "SMTG", "TMTG" };
    String[] CREDIT_CARD_PRODUCT = { "CDC", "CHC", "CRC", "LCRC" };
    String[] NON_FINANCIAL_PRODUCTS = { "MBL", "LND", "DAT" };
    char[] CURRENT_DELINQUENCY_VALUES = { '0', 'C', 'D', 'N' };

    int MAX_GLOBAL_DTI = 45;
    int MAX_INTERNAL_DTI = 33;
    private String FINANCIAL_DEFAULT_AMOUNT = "";

    private String DATA_TYPE = "";

    private String TENOR = "";

    private String CALCULATE = "YES";

    private String PENSIONER = "0";

    private String CUSTOMER_AGE = "";

    private String APPLICATION_ID = "";
    private String LOAN_REF = "";
    private String SCORECARD_ID = "";
    private String NATIONAL_ID = "";
    private String DOB = "";
    private String CONTACT_NUMBER = "";
    private String LOAN_AMOUNT = "";

    private String APROX = "";
    private String EMPLOYER_NAME = "";

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
            DataControllerResponse dataControllerResponse) throws Exception {

        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

		try {
        if (preProcess(dataControllerRequest)) {

            // DB INTEGRATION SERVICES CALLS
            Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);
            extractValuesFromCustomerApplication(getCustomerApplicationData);

            Result getCustomerData = getCustomerData(dataControllerRequest);

            // 3RD PARTY INTEGRATION SERVICES CALLS
            Result getSalaryCertificate = getSIMAHSalaryCertificate(
                    createRequestForSIMAHSALARY(getCustomerData, EMPLOYER_TYPE_ID, NATIONAL_ID), dataControllerRequest);
            if (getSalaryCertificate.getParamValueByName("bankCode").isEmpty()
                    || IjarahHelperMethods.isBlank(getSalaryCertificate.getParamValueByName("bankCode"))) {
                EMPLOYER_TYPE_ID = "3";
//					this.inputParams.put("employerTypeId", EMPLOYER_TYPE_ID);
                getSalaryCertificate = getSIMAHSalaryCertificate(
                        createRequestForSIMAHSALARY(getCustomerData, EMPLOYER_TYPE_ID, NATIONAL_ID),
                        dataControllerRequest);
            }
            Result getNationalAddress = getNationalAddress(inputParams, dataControllerRequest);
//				createCustomerAddress(createRequestForCreateCustomerAddressService(getNationalAddress),
//						dataControllerRequest);
            Result getConsumerEnquiry = getSIMAHConsumerEnquiry(createRequestForConsumerEnquiryService(inputParams,
                    getCustomerData, getSalaryCertificate, getNationalAddress), dataControllerRequest);

            // CALCULATION OF SCORING ENGINES
//            calculateMonthlyNetSalary(getConsumerEnquiry, getSalaryCertificate);
//            calculatePensioner(getSalaryCertificate);
//            calculateCurrentLengthOfService(getConsumerEnquiry, getSalaryCertificate);
//            calculateGlobalDTI(getConsumerEnquiry, getSalaryCertificate);
//            calculateInternalDTI(getConsumerEnquiry, getSalaryCertificate);
//            calculateCurrentDelinquencyAndCurrentDelinquencyT(getConsumerEnquiry, getSalaryCertificate);
//            calculateMaxDelinquency(getConsumerEnquiry, getSalaryCertificate);
//            calculateFinancialDefaultAmount(getConsumerEnquiry, getSalaryCertificate);
//            calculateNonFinancialDefaultAmount(getConsumerEnquiry, getSalaryCertificate);
//            calculateBouncedCheque(getConsumerEnquiry, getSalaryCertificate);
//            calculateCourtJudgement(getConsumerEnquiry, getSalaryCertificate);
//            getEmployerName(getConsumerEnquiry, getSalaryCertificate);
//            calculateMaxLoanAmountCapping(getConsumerEnquiry, getSalaryCertificate);
//            calculateManagingSeasonalAndTemporaryLiftInSalary(getConsumerEnquiry, getSalaryCertificate);
//            calculateNewToIndustry(getConsumerEnquiry, getSalaryCertificate);
//            calculateSalaryWithoutAllowances(getConsumerEnquiry, getSalaryCertificate);

            // 3RD PARTY INTEGRATION SERVICES CALLS
            Result getScoreCardS2 = calculateScoreCardS2(createRequestForScoreCardS2Service(getSalaryCertificate),
                    dataControllerRequest);
            Result getScoreCardS3 = calculateScoreCardS3(createRequestForScoreCardS3Service(getConsumerEnquiry),
                    dataControllerRequest);

            // DB INTEGRATION SERVICES CALLS
            result = updateCustomerApplicationData(createRequestForUpdateCustomerApplicationDataService(
                    getCustomerApplicationData, getScoreCardS2, getScoreCardS3), dataControllerRequest);
//            result = createResultObject(updateCustomerResult);
//            JSONObject updateResult = new JSONObject(ResultToJSON.convert(updateCustomerResult));
//            result.addParam("ResponseCode", "sucess");
//        result.addParam("Message", ErrorCode.ERR_60000.getErrorMessage());
        }
		} catch (Exception ex) {
			LOG.error("ERROR invoke :: " + ex);
		}
        
        return result;
    }

    private Map<String, String> createRequestForCreateCustomerAddressService(Result getNationalAddress) {
        Map<String, String> inputParams = new HashMap<>();
        String currentDateTime = getDate(LocalDateTime.now(), DATE_FORMAT_WITH_SECONDS_MS);
        inputParams.put("Region_id", "SAU");
        inputParams.put("City_id", getNationalAddress.getParamValueByName("city"));
        inputParams.put("addressLine1", getNationalAddress.getParamValueByName("district"));
        inputParams.put("addressLine2", getNationalAddress.getParamValueByName("streetName"));
        inputParams.put("addressLine3", getNationalAddress.getParamValueByName("unitNumber"));
        inputParams.put("zipCode", getNationalAddress.getParamValueByName("postCode"));
        inputParams.put("latitude", getNationalAddress.getParamValueByName("locationCoordinates"));
        inputParams.put("logitude", getNationalAddress.getParamValueByName("locationCoordinates"));
        inputParams.put("isPreferredAddress", "true");
        inputParams.put("cityName", getNationalAddress.getParamValueByName("city"));
        inputParams.put("User_id", NATIONAL_ID);
        inputParams.put("country", "SAU");
        inputParams.put("type", "home");
        inputParams.put("state", "SAU");
        inputParams.put("createdby", "Admin");
        inputParams.put("modifiedby", "Admin");
        inputParams.put("createdts", currentDateTime);
        inputParams.put("lastmodifiedts", currentDateTime);
        inputParams.put("synctimestamp", currentDateTime);
        inputParams.put("softdeleteflag", "0");
        LOG.error("createRequestForCreateCustomerAddressService");
        LOG.error(inputParams);
        return inputParams;
    }

    private Map<String, String> createRequestForSIMAHSALARY(
            Result getCustomerData, String empId, String nanId) {
        Map<String, String> inputParams = new HashMap<>();
        try {
            inputParams.put("employerTypeId", empId);
            inputParams.put("dateOfBirth", HelperMethods.getFieldValue(getCustomerData, "DateOfBirth"));
            inputParams.put("idNumber", nanId);
            LOG.error("Nationalid for simah salary====" + NATIONAL_ID);
        } catch (Exception ex) {
            LOG.error("ERROR createRequestForSIMAHSALARY :: " + ex);
        }
        return inputParams;
    }

    private Map<String, String> createRequestForConsumerEnquiryService(Map<String, String> globalInputParams,
            Result getCustomerData, Result getSalaryCertificate, Result getNationalAddress) {
        Map<String, String> inputParams = new HashMap<>();
        try {

            String expiryDate = String.valueOf(HelperMethods.getFieldValue(getCustomerData, "IDExpiryDate"));
            String expiryDateF = expiryDate.replaceAll("-", "/");
            DOB = String.valueOf(HelperMethods.getFieldValue(getCustomerData, "DateOfBirth"));

            String dateOfBirth = DOB.replaceAll("-", "/");
            inputParams.put("PRODUCT_TYPE", "PLN");
            inputParams.put("ENQUIRY_REFERENCE", generateUUID());
            inputParams.put("AMOUNT", LOAN_AMOUNT);
            inputParams.put("CID1", NATIONAL_ID.startsWith("1") ? "T" : "Q");
            inputParams.put("CID2", NATIONAL_ID);
            inputParams.put("CID3", expiryDateF);
            inputParams.put("CID3D", expiryDateF.split("/")[0]);
            inputParams.put("CID3M", expiryDateF.split("/")[1]);
            inputParams.put("CID3Y", expiryDateF.split("/")[2]);
            inputParams.put("CDOB", dateOfBirth);
            inputParams.put("CDBD", dateOfBirth.split("/")[2]);
            inputParams.put("CDBM", dateOfBirth.split("/")[1]);
            inputParams.put("CDBY", dateOfBirth.split("/")[0]);
            LOG.error("InputParamse dateOfBirth====>" + inputParams.getOrDefault(expiryDate, dateOfBirth));
            inputParams.put("CGND",
                    HelperMethods.getFieldValue(getCustomerData, "Gender").equalsIgnoreCase("Male") ? "M" : "F");
            inputParams.put("CMAR",
                    HelperMethods.getFieldValue(getCustomerData, "MartialStatus_id").equalsIgnoreCase("SID_MARRIED")
                            ? "M"
                            : "S");
            inputParams.put("CNAT", getSalaryCertificate.getParamValueByName("nationality"));
            inputParams.put("CNAM", HelperMethods.getFieldValue(getCustomerData, "ArFullName").split(" ")[3]);
            inputParams.put("CNM1A", HelperMethods.getFieldValue(getCustomerData, "ArFullName").split(" ")[0]);
            inputParams.put("CNM2A", HelperMethods.getFieldValue(getCustomerData, "ArFullName").split(" ")[1]);
            inputParams.put("CNM3A", HelperMethods.getFieldValue(getCustomerData, "ArFullName").split(" ")[2]);
            inputParams.put("CNMFE", HelperMethods.getFieldValue(getCustomerData, "FirstName"));
            inputParams.put("CNM1E", HelperMethods.getFieldValue(getCustomerData, "MiddleName"));
            inputParams.put("CNM2E", HelperMethods.getFieldValue(getCustomerData, "LastName"));
            inputParams.put("CNM3E", HelperMethods.getFieldValue(getCustomerData, "FullName"));
            inputParams.put("CEML", "test@gmail.com");
            inputParams.put("CADR", "");
            inputParams.put("CAD1A", "1223");
            inputParams.put("CAD7", getNationalAddress.getParamValueByName("postCode"));
            inputParams.put("CAD8E", getNationalAddress.getParamValueByName("city"));
            inputParams.put("CAD9", "SAU");
            inputParams.put("CCN1", "M");
            inputParams.put("CCN2", "966");
            inputParams.put("CCN3", "0");
            inputParams.put("CCN4", CONTACT_NUMBER);
            inputParams.put("EMPLOYER", getSalaryCertificate.getParamValueByName("employerName"));
            inputParams.put("ETYP", "C");
            inputParams.put("EOCA", "Ijarah");
            inputParams.put("EOCE", "سعيد");
            inputParams.put("ELEN", CURRENT_LENGTH_OF_SERVICE);
            inputParams.put("EMBS", MONTHLY_NET_SALARY);
            inputParams.put("ESLF", "N");
            inputParams.put("ENME", "Ijarah");
            inputParams.put("ENMA", "الإجارة");
            inputParams.put("EBUS", "");
            inputParams.put("EAD6", "111");
            inputParams.put("EAD7", "3333");
            inputParams.put("EAD8E", "RIY");
            inputParams.put("EAD9", "+966");

        } catch (Exception ex) {
            LOG.error("ERROR createRequestForConsumerEnquiryService :: " + ex);
        }
        return inputParams;
    }

    private String calculateAPR(String rate) {
        String apr = "";
        try {
           
            double doubleRate = Double.valueOf(rate);
            double rateFees = 1.15 + doubleRate;
            double monthlyRate = rateFees / 12;
            LOG.error("Calculated monthl rate=====>"+monthlyRate);
            double aprD = (Math.pow(1 + monthlyRate, 12)) - 1;
            apr = Double.toString(Math.ceil(aprD));
            LOG.error("Calculated final apr=====>"+apr); 
        }catch(Exception e) {
            LOG.error("Error in APR caluclation===>"+e);
        }
        
        return apr;
    }

    private Map<String, String> createRequestForUpdateCustomerApplicationDataService(Result getCustomerApplicationData,
            Result getScoreCardS2, Result getScoreCardS3) {
        Map<String, String> inputParams = new HashMap<>();
        String knockoutStatus = "";
        String applicationStatus = "";
//        try {

//        LOG.error("With helper methods get data =======>>>>>"+HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId"));
//        LOG.error("Without helper methods get data =======>>>>>"+getCustomerApplicationData.getParamByName("applicationID"));

        LOG.error("Reuslt object in JSON score card3===========>>>"
                + new JSONObject(ResultToJSON.convert(getScoreCardS3)));
        LOG.error("Reuslt object in JSON score card2===========>>>"
                + new JSONObject(ResultToJSON.convert(getScoreCardS2)));
        JSONObject sc3 = new JSONObject(ResultToJSON.convert(getScoreCardS3));
        int catVal = sc3.optInt("applicationCategory");
        if (catVal > 0) {
            knockoutStatus = "PASS";
            applicationStatus = "SID_PRO_ACTIVE";
        } else {
            knockoutStatus = "FAIL";
            applicationStatus = "SID_SUSPENDED";
        }

        String approx = calculateAPR(sc3.optString("loanRate"));
        inputParams.put("isknockoutTnC", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockoutTnC"));
        inputParams.put("lastmodifiedts",
                IjarahHelperMethods.getDate(LocalDateTime.now(), DATE_FORMAT_WITH_SECONDS_MS));
        inputParams.put("productId", HelperMethods.getFieldValue(getCustomerApplicationData, "productId"));
        inputParams.put("mobile", HelperMethods.getFieldValue(getCustomerApplicationData, "mobile"));
        inputParams.put("scoredCardId", HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId"));
//			inputParams.put("createdts", HelperMethods.getFieldValue(getCustomerApplicationData, "createdts"));
//			inputParams.put("softdeleteflag",
//					HelperMethods.getFieldValue(getCustomerApplicationData, "softdeleteflag"));
        inputParams.put("productName", DATA_TYPE);
        inputParams.put("createdby", HelperMethods.getFieldValue(getCustomerApplicationData, "createdby"));
        inputParams.put("Customer_id", HelperMethods.getFieldValue(getCustomerApplicationData, "Customer_id"));
        inputParams.put("id", HelperMethods.getFieldValue(getCustomerApplicationData, "id"));
        inputParams.put("applicationID", HelperMethods.getFieldValue(getCustomerApplicationData, "applicationID"));
        inputParams.put("tenor", sc3.optString("tenor"));
        inputParams.put("knockoutStatus", knockoutStatus);
        inputParams.put("applicationStatus", applicationStatus);
        inputParams.put("monthlyRepay", HelperMethods.getFieldValue(getCustomerApplicationData, "monthlyRepay"));
        // inputParams.put("loanAmount",
        // HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount"));
        inputParams.put("isknockouts1", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockouts1"));
        inputParams.put("approx", HelperMethods.getFieldValue(getCustomerApplicationData,approx!="" ? approx :sc3.optString("loanRate") ));

        inputParams.put("loanAmountCap", sc3.optString("loanAmountCap"));
        inputParams.put("loanRate", sc3.optString("loanRate"));
//        } catch (Exception ex) {
//            LOG.error("ERROR createRequestForUpdateCustomerApplicationDataService :: " + ex);
//        }
        LOG.error(inputParams);
        return inputParams;
    }

    private void calculatePensioner(Result getSalaryCertificate) {
        try {
            if (Integer.parseInt(CUSTOMER_AGE) > 60) {
                PENSIONER = "1";
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculatePensioner :: " + ex);
        }
    }

    private void calculateMonthlyNetSalary(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            NATIONALITY = getSalaryCertificate.getParamValueByName("nationality");
            switch (EMPLOYER_TYPE_ID) {
                case "1":
                    /*
                     * int currentMonth = Calendar.getInstance().get(Calendar.MONTH); int lastMonth
                     * = currentMonth == 0 ? 11 : currentMonth - 1; int payMonth =
                     * Integer.parseInt(getSalaryCertificate.getParamValueByName("Paymonth")); if
                     * (payMonth < lastMonth) { // Call Simah Salary Certificate with EmployeerId =
                     * 3 } else { MONTHLY_NET_SALARY = inputParams.get("netSalary"); }
                     */

                    MONTHLY_NET_SALARY = getSalaryCertificate.getParamValueByName("netSalary");
                    break;
                case "3":
                    EMPLOYMENT_STATUS = getSalaryCertificate.getParamValueByName("employmentStatus");
                    if (EMPLOYMENT_STATUS.equalsIgnoreCase("Active")) {
                        double calculatedDeductions = 0;
                        if (NATIONALITY.equalsIgnoreCase("SAU")) {
                            double minimumAmount = 0.1
                                    * (Integer.parseInt(getSalaryCertificate.getParamValueByName("basicSalary"))
                                            + Integer
                                                    .parseInt(getSalaryCertificate
                                                            .getParamValueByName("housingAllowance")));
                            calculatedDeductions = minimumAmount < 4500 ? minimumAmount : 4500;
                        }
                        MONTHLY_NET_SALARY = String
                                .valueOf((Integer.parseInt(getSalaryCertificate.getParamValueByName("basicSalary"))
                                        + Integer.parseInt(getSalaryCertificate.getParamValueByName("housingAllowance"))
                                        + Integer.parseInt(getSalaryCertificate.getParamValueByName("otherAllowance")))
                                        - calculatedDeductions);
                    }
                    break;
                default:
                    MONTHLY_NET_SALARY = "0";
                    break;
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateMonthlyNetSalary :: " + ex);
        }
    }

    private void calculateCurrentLengthOfService(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            switch (EMPLOYER_TYPE_ID) {
                case "1":
                    LocalDate agencyEmploymentDate = LocalDate.parse(
                            getSalaryCertificate.getParamValueByName("agencyEmploymentDate"),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalDate currentDate = LocalDate.now();
                    CURRENT_LENGTH_OF_SERVICE = String.valueOf(Math.toIntExact(
                            ChronoUnit.MONTHS.between(YearMonth.from(agencyEmploymentDate),
                                    YearMonth.from(currentDate))));
                    break;
                case "3":
                    LocalDate dateOfJoining = LocalDate.parse(getSalaryCertificate.getParamValueByName("dateOfJoining"),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate currentDateNow = LocalDate.now();
                    CURRENT_LENGTH_OF_SERVICE = String.valueOf(Math.toIntExact(
                            ChronoUnit.MONTHS.between(YearMonth.from(dateOfJoining), YearMonth.from(currentDateNow))));
                    break;
                default:
                    CURRENT_LENGTH_OF_SERVICE = "0";
                    break;
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateCurrentLengthOfService :: " + ex);
        }
    }

    private void calculateGlobalDTI(Result getConsumerEnquiry, Result getSalaryCertificate) {
//		try {
        GLOBAL_DTI = "1";
        Record productDetailRecord = getConsumerEnquiry.getRecordById("CI_DETAIL");
        String productType = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_PRD"));
        String status = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_STATUS"));

        if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType) && status.equalsIgnoreCase("A")) {
            // Financial product
            String installment = IjarahHelperMethods
                    .checkStringNull(productDetailRecord.getParamValueByName("CI_INSTL"));
            String frequency = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_FRQ"));
            String creditLimit = IjarahHelperMethods
                    .checkStringNull(productDetailRecord.getParamValueByName("CI_LIMIT"));
            double totalDebtServicing = 0;
            if (Arrays.asList(MORTGAGE_PRODUCT).contains(productType)) {
                MAX_GLOBAL_DTI = 65;
            }
            if (Arrays.asList(CREDIT_CARD_PRODUCT).contains(productType)) {
                totalDebtServicing = 0.05 * Integer.parseInt(creditLimit);
            } else {
                if (frequency.equalsIgnoreCase("M")) {
                    totalDebtServicing = Integer.parseInt(installment);
                } else if (frequency.equalsIgnoreCase("Q")) {
                    totalDebtServicing = Double.parseDouble(installment) / 3;
                } else if (frequency.equalsIgnoreCase("H")) {
                    totalDebtServicing = Double.parseDouble(installment) / 6;
                } else {
                    totalDebtServicing = Double.parseDouble(installment) / 12;
                }
            }
            double customerGlobalDTI = (totalDebtServicing / Integer.parseInt(MONTHLY_NET_SALARY)) * 100;
            if (customerGlobalDTI >= MAX_GLOBAL_DTI) {
                GLOBAL_DTI = "0";
            }
        }
//		} catch (Exception ex) {
//			LOG.error("ERROR calculateGlobalDTI :: " + ex);
//		}
    }

    private void calculateInternalDTI(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            INTERNAL_DTI = "1";
            Record productDetailRecord = getConsumerEnquiry.getRecordById("CI_DETAIL");
            String productType = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_PRD"));
            String productCode = IjarahHelperMethods
                    .checkStringNull(productDetailRecord.getParamValueByName("CI_CRDTR"));
            String status = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_STATUS"));

            if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType) && status.equalsIgnoreCase("A")
                    && productCode.equalsIgnoreCase("IJRH")) {
                // Financial product
                String installment = IjarahHelperMethods
                        .checkStringNull(productDetailRecord.getParamValueByName("CI_INSTL"));
                String frequency = IjarahHelperMethods
                        .checkStringNull(productDetailRecord.getParamValueByName("CI_FRQ"));
                String creditLimit = IjarahHelperMethods
                        .checkStringNull(productDetailRecord.getParamValueByName("CI_LIMIT"));
                double totalDebtServicing = 0;
                if (Arrays.asList(MORTGAGE_PRODUCT).contains(productType)) {
                    MAX_INTERNAL_DTI = 45;
                }
                if (Arrays.asList(CREDIT_CARD_PRODUCT).contains(productType)) {
                    totalDebtServicing = 0.05 * Integer.parseInt(creditLimit);
                } else {
                    if (frequency.equalsIgnoreCase("M")) {
                        totalDebtServicing = Integer.parseInt(installment);
                    } else if (frequency.equalsIgnoreCase("Q")) {
                        totalDebtServicing = Double.parseDouble(installment) / 3;
                    } else if (frequency.equalsIgnoreCase("H")) {
                        totalDebtServicing = Double.parseDouble(installment) / 6;
                    } else {
                        totalDebtServicing = Double.parseDouble(installment) / 12;
                    }
                }
                double customerGlobalDTI = (totalDebtServicing / Integer.parseInt(MONTHLY_NET_SALARY)) * 100;
                if (customerGlobalDTI >= MAX_INTERNAL_DTI) {
                    INTERNAL_DTI = "0";
                }
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateInternalDTI :: " + ex);
        }
    }

    private void calculateCurrentDelinquencyAndCurrentDelinquencyT(Result getConsumerEnquiry,
            Result getSalaryCertificate) {
        try {
            CURRENT_DELINQUENCY = "0";
            CURRENT_DELINQUENCY_T = "1";
            Record productDetailRecord = getConsumerEnquiry.getRecordById("CI_DETAIL");
            String productType = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_PRD"));
            String summary = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_SUMMRY"));

            if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                // Financial Product
                char firstMonthDelinquency = summary.charAt(0);
                if (Objects.equals(CURRENT_DELINQUENCY_VALUES, firstMonthDelinquency)) {
                    CURRENT_DELINQUENCY = "1";
                }

                if (firstMonthDelinquency == 'M') {
                    CURRENT_DELINQUENCY_T = "0";
                }

            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateCurrentDelinquencyAndCurrentDelinquencyT :: " + ex);
        }
    }

    private void calculateMaxDelinquency(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            MAX_DELINQUENCY = "0";
            Record productDetailRecord = getConsumerEnquiry.getRecordById("CI_DETAIL");
            String productType = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_PRD"));
            String summary = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_SUMMRY"));

            if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                // Financial Product
                char[] summmaryChar = summary.toCharArray();
                int tempMaxDelinquency = 0;
                for (char delinquencyChar : summmaryChar) {
                    if (Character.isDigit(delinquencyChar)) {
                        if (tempMaxDelinquency < delinquencyChar) {
                            tempMaxDelinquency = delinquencyChar;
                        }
                    }
                }
                MAX_DELINQUENCY = String.valueOf(tempMaxDelinquency);
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateMaxDelinquency :: " + ex);
        }
    }

    private void calculateFinancialDefaultAmount(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            // Record productDetailRecord = getConsumerEnquiry.getRecordById("DEFAULT");
            String productType = IjarahHelperMethods.checkStringNull(getConsumerEnquiry.getParamValueByName("DF_PRD"));

            if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                FINANCIAL_DEFAULT_AMOUNT = IjarahHelperMethods
                        .checkStringNull(getConsumerEnquiry.getParamValueByName("DF_CUB"));
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateFinancialDefaultAmount :: " + ex);
        }
    }

    private void calculateNonFinancialDefaultAmount(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            // Record productDetailRecord = getConsumerEnquiry.getRecordById("DEFAULT");
            String productType = IjarahHelperMethods.checkStringNull(getConsumerEnquiry.getParamValueByName("DF_PRD"));

            if (Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                NON_FINANCIAL_DEFAULT_AMOUNT = IjarahHelperMethods
                        .checkStringNull(getConsumerEnquiry.getParamValueByName("DF_CUB"));
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateNonFinancialDefaultAmount :: " + ex);
        }
    }

    private void calculateBouncedCheque(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
//            getConsumerEnquiry.getParamValueByName("");
//            if (getConsumerEnquiry.getRecordById("BOUNCED_CHECKS") != null) {
//                Record bouncedChequeRecord = getConsumerEnquiry.getRecordById("BOUNCED_CHECKS");
            String bouncedChequeValue = IjarahHelperMethods
                    .checkStringNull(getConsumerEnquiry.getParamValueByName("BC_SETTLE_DATE"));
            BOUNCED_CHEQUE = "NB";
        } catch (Exception ex) {
            LOG.error("ERROR calculateBouncedCheque :: " + ex);
        }
    }

    private void calculateCourtJudgement(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            // Record courtJudgementRecord = getConsumerEnquiry.getRecordById("JUDGEMENT");
            String courtJudgementValue = IjarahHelperMethods
                    .checkStringNull(getConsumerEnquiry.getParamValueByName("EJ_SETTLE_DATE"));
            COURT_JUDGEMENT = "NJ";
        } catch (Exception ex) {
            LOG.error("ERROR calculateCourtJudgement :: " + ex);
        }
    }

    private void getEmployerName(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            EMPLOYER_NAME = IjarahHelperMethods
                    .checkStringNull(getSalaryCertificate.getParamValueByName("employeeNameEn"));
            /*
             * int currentLengthOfService = Integer.parseInt(CURRENT_LENGTH_OF_SERVICE); if
             * (currentLengthOfService <= 3) { EMPLOYER_CATEGORISATION = "G"; } else if
             * (currentLengthOfService <= 6) { EMPLOYER_CATEGORISATION = "C"; } else {
             * EMPLOYER_CATEGORISATION = "U"; }
             */
        } catch (Exception ex) {
            LOG.error("ERROR getEmployerName :: " + ex);
        }
    }

    private void calculateNewToIndustry(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            NEW_TO_INDUSTRY = "Y";
            Record productDetailRecord = getConsumerEnquiry.getRecordById("CI_DETAIL");
            String productType = IjarahHelperMethods.checkStringNull(productDetailRecord.getParamValueByName("CI_PRD"));
            if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                NEW_TO_INDUSTRY = "N";
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateNewToIndustry :: " + ex);
        }
    }

    private void calculateMaxLoanAmountCapping(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            if (NEW_TO_INDUSTRY.equalsIgnoreCase("Y") && Integer.parseInt(MONTHLY_NET_SALARY) < 4500) {
                MAX_LOAN_AMOUNT_CAPPING = "100000";
            } else if (NEW_TO_INDUSTRY.equalsIgnoreCase("N") && Integer.parseInt(MONTHLY_NET_SALARY) < 7500) {
                MAX_LOAN_AMOUNT_CAPPING = "200000";
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateMaxLoanAmountCapping :: " + ex);
        }
    }

    private void calculateManagingSeasonalAndTemporaryLiftInSalary(Result getConsumerEnquiry,
            Result getSalaryCertificate) {
        try {
            double basicSalary = Integer.parseInt(getSalaryCertificate.getParamValueByName("basicSalary"));
            int OtherAllowance = 0;
            MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY = "1";
            switch (EMPLOYER_TYPE_ID) {
                case "1": // Govt
                    OtherAllowance = Integer.parseInt(getSalaryCertificate.getParamValueByName("totalAllownces"));
                    break;
                case "3": // Private
                    OtherAllowance = Integer.parseInt(getSalaryCertificate.getParamValueByName("otherAllowance"));
                    basicSalary += Integer.parseInt(getSalaryCertificate.getParamValueByName("housingAllowance"));
                    break;
            }
            if (OtherAllowance >= (basicSalary * 2)) {
                MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY = "0";
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateManagingSeasonalAndTemporaryLiftInSalary :: " + ex);
        }
    }

    private void calculateSalaryWithoutAllowances(Result getConsumerEnquiry, Result getSalaryCertificate) {
        try {
            switch (EMPLOYER_TYPE_ID) {
                case "1": // Govt
                    SALARY_WITHOUT_ALLOWANCES = String.valueOf(Integer.parseInt(MONTHLY_NET_SALARY)
                            - Integer.parseInt(getSalaryCertificate.getParamValueByName("totalAllownces")));
                    break;
                case "3": // Private
                    SALARY_WITHOUT_ALLOWANCES = String.valueOf(Integer.parseInt(MONTHLY_NET_SALARY)
                            - Integer.parseInt(getSalaryCertificate.getParamValueByName("otherAllowance")));
                    break;
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateSalaryWithoutAllowances :: " + ex);
        }
    }

    private Map<String, String> createRequestForScoreCardS2Service(Result getSalaryCertificate) {
        Map<String, String> inputParams = new HashMap<>();
        try {
//            inputParams.put("scorecardId", SCORECARD_ID);
//            inputParams.put("dataType", DATA_TYPE);
//            inputParams.put("calculate", CALCULATE);
//            inputParams.put("pensioner", PENSIONER);
//            inputParams.put("customerAge", CUSTOMER_AGE);
//            inputParams.put("salaryAmount", MONTHLY_NET_SALARY);
//            inputParams.put("insideKsa", INSIDE_KSA);
//            inputParams.put("loanRef", APPLICATION_ID);
//            inputParams.put("validLos", CURRENT_LENGTH_OF_SERVICE);
//            inputParams.put("validDti", String.valueOf(MAX_GLOBAL_DTI));
//            inputParams.put("currDelinquency", CURRENT_DELINQUENCY);
//            inputParams.put("maxDelinquency", MAX_DELINQUENCY);
//            inputParams.put("bouncedCheck", BOUNCED_CHEQUE);
//            inputParams.put("courtJudgement", COURT_JUDGEMENT);
//            inputParams.put("validUtil", NON_FINANCIAL_DEFAULT_AMOUNT);
//            inputParams.put("tenor", TENOR);
//            inputParams.put("validDefaults", FINANCIAL_DEFAULT_AMOUNT);
//            inputParams.put("validInternalDti", INTERNAL_DTI);
//            inputParams.put("currDelinquencyT", CURRENT_DELINQUENCY_T);
//            inputParams.put("employeeName", EMPLOYER_NAME);
//            inputParams.put("loanAmountCap", MAX_LOAN_AMOUNT_CAPPING);
//            inputParams.put("liftSalary", MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY);
//            inputParams.put("nationality", NATIONALITY);
//            inputParams.put("newIndustry", NEW_TO_INDUSTRY);
//            inputParams.put("salaryNonAllowance", SALARY_WITHOUT_ALLOWANCES);

            inputParams.put("scorecardId", SCORECARD_ID);
            inputParams.put("dataType", DATA_TYPE);
            inputParams.put("calculate", CALCULATE);
            inputParams.put("pensioner", "0");
            inputParams.put("customerAge", CUSTOMER_AGE);
            inputParams.put("salaryAmount", "45000");
            inputParams.put("insideKsa", INSIDE_KSA);
            inputParams.put("loanRef", APPLICATION_ID);
            inputParams.put("validLos", "24");
            inputParams.put("validDti", String.valueOf(MAX_GLOBAL_DTI));
            inputParams.put("currDelinquency", CURRENT_DELINQUENCY);
            inputParams.put("maxDelinquency", MAX_DELINQUENCY);
            inputParams.put("bouncedCheck", BOUNCED_CHEQUE);
            inputParams.put("courtJudgement", COURT_JUDGEMENT);
            inputParams.put("validUtil", NON_FINANCIAL_DEFAULT_AMOUNT);
            inputParams.put("tenor", "24");
            inputParams.put("validDefaults", FINANCIAL_DEFAULT_AMOUNT);
            inputParams.put("validInternalDti", INTERNAL_DTI);
            inputParams.put("currDelinquencyT", CURRENT_DELINQUENCY_T);
            inputParams.put("employeeName", "ijarah");
            inputParams.put("loanAmountCap", MAX_LOAN_AMOUNT_CAPPING);
            inputParams.put("liftSalary", "1");
            inputParams.put("nationality", NATIONALITY);
            inputParams.put("newIndustry", NEW_TO_INDUSTRY);
            inputParams.put("salaryNonAllowance", "3500");

        } catch (Exception ex) {
            LOG.error("ERROR createRequestForScoreCardS2Service :: " + ex);
        }
        return inputParams;
    }

    private Map<String, String> createRequestForScoreCardS3Service(Result getConsumerEnquiry) {
        Map<String, String> inputParams = new HashMap<>();
        try {
//            inputParams.put("scorecardId", SCORECARD_ID);
//            inputParams.put("dataType", DATA_TYPE);
//            inputParams.put("calculate", CALCULATE);
//            inputParams.put("pensioner", PENSIONER);
//            inputParams.put("customerAge", CUSTOMER_AGE);
//            inputParams.put("salaryAmount", MONTHLY_NET_SALARY);
//            inputParams.put("insideKsa", INSIDE_KSA);
//            inputParams.put("loanRef", APPLICATION_ID);
//            inputParams.put("validLos", CURRENT_LENGTH_OF_SERVICE);
//            inputParams.put("validDti", String.valueOf(MAX_GLOBAL_DTI));
//            inputParams.put("currDelinquency", CURRENT_DELINQUENCY);
//            inputParams.put("maxDelinquency", MAX_DELINQUENCY);
//            inputParams.put("bouncedCheck", BOUNCED_CHEQUE);
//            inputParams.put("courtJudgement", COURT_JUDGEMENT);
//            inputParams.put("validUtil", NON_FINANCIAL_DEFAULT_AMOUNT);
//            inputParams.put("tenor", TENOR);
//            inputParams.put("validDefaults", FINANCIAL_DEFAULT_AMOUNT);
//            inputParams.put("validInternalDti", INTERNAL_DTI);
//            inputParams.put("currDelinquencyT", CURRENT_DELINQUENCY_T);
//            inputParams.put("employeeName", EMPLOYER_NAME);
//            inputParams.put("loanAmountCap", MAX_LOAN_AMOUNT_CAPPING);
//            inputParams.put("liftSalary", MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY);
//            inputParams.put("nationality", NATIONALITY);
//            inputParams.put("newIndustry", NEW_TO_INDUSTRY);
//            inputParams.put("salaryNonAllowance", SALARY_WITHOUT_ALLOWANCES);

            inputParams.put("scorecardId", SCORECARD_ID);
            inputParams.put("dataType", DATA_TYPE);
            inputParams.put("calculate", CALCULATE);
            inputParams.put("pensioner", PENSIONER);
            inputParams.put("customerAge", CUSTOMER_AGE);
            inputParams.put("salaryAmount", MONTHLY_NET_SALARY);
            inputParams.put("insideKsa", INSIDE_KSA);
            inputParams.put("loanRef", APPLICATION_ID);
            inputParams.put("validLos", CURRENT_LENGTH_OF_SERVICE);
            inputParams.put("validDti", "1");
            inputParams.put("currDelinquency", "1");
            inputParams.put("maxDelinquency", "2");
            inputParams.put("bouncedCheck", "NB");
            inputParams.put("courtJudgement", "NJ");
            inputParams.put("validUtil", "15000");
            inputParams.put("tenor", TENOR);
            inputParams.put("validDefaults", "5000");
            inputParams.put("validInternalDti", "1");
            inputParams.put("currDelinquencyT", CURRENT_DELINQUENCY_T);
            inputParams.put("employeeName", EMPLOYER_NAME);
            inputParams.put("loanAmountCap", MAX_LOAN_AMOUNT_CAPPING);
            inputParams.put("liftSalary", "1");
            inputParams.put("nationality", NATIONALITY);
            inputParams.put("newIndustry", "N");
            inputParams.put("salaryNonAllowance", SALARY_WITHOUT_ALLOWANCES);
        } catch (Exception ex) {
            LOG.error("ERROR createRequestForScoreCardS3Service :: " + ex);
        }
        return inputParams;
    }

    private boolean preProcess(DataControllerRequest request) {
        try {
            APPLICATION_ID = request.getParameter("ApplicationID");
            NATIONAL_ID = request.getParameter("NationalID");
            LOG.error("natid====>>" + request.getParameter("NationalID"));
            DATA_TYPE = request.getParameter("Product");
            CONTACT_NUMBER = request.getParameter("Mobile");
            this.inputParams.put("ENQUIRY_REFERENCE", String.valueOf(generateRandomInt()));
            this.inputParams.put("employerTypeId", EMPLOYER_TYPE_ID);
        } catch (Exception ex) {
            LOG.error("ERROR preProcess :: " + ex);
        }
        return !inputParams.isEmpty();
    }

    private Result getCustomerApplicationData(DataControllerRequest dataControllerRequest) {
        Result getCustomerApplicationData = StatusEnum.error.setStatus();
        try {
            getCustomerApplicationData = StatusEnum.success.setStatus();
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "nationalId" + DBPUtilitiesConstants.EQUAL + NATIONAL_ID);
            getCustomerApplicationData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
                    CUSTOMER_APPLICATION_GET_OPERATION_ID, filter, null, dataControllerRequest));
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerApplicationData :: " + ex);
        }
        return getCustomerApplicationData;
    }

    private void extractValuesFromCustomerApplication(Result getCustomerApplicationData) {
        try {
            CUSTOMER_AGE = HelperMethods.getFieldValue(getCustomerApplicationData, "customerAge");
            // LOAN_REF = HelperMethods.getFieldValue(getCustomerApplicationData,
            // "loanRef");
            INSIDE_KSA = HelperMethods.getFieldValue(getCustomerApplicationData, "insideKsa");
            SCORECARD_ID = HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId");
            LOAN_AMOUNT = HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount");
            TENOR = HelperMethods.getFieldValue(getCustomerApplicationData, "tenor");
        } catch (Exception ex) {
            LOG.error("ERROR extractValuesFromCustomerApplication :: " + ex);
        }
    }

    private Result getCustomerData(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getCustomerData = StatusEnum.success.setStatus();
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "UserName" + DBPUtilitiesConstants.EQUAL + NATIONAL_ID);
            getCustomerData.appendResult(ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, CUSTOMER_GET_OPERATION_ID,
                    filter, null, dataControllerRequest));
            return getCustomerData;
        } catch (Exception ex) {
            LOG.error("ERROR getCustomerData :: " + ex);
        }
        return result;
    }

    private Result getSIMAHConsumerEnquiry(Map<String, String> inputParams,
            DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getConsumerEnquiry = StatusEnum.success.setStatus();
            getConsumerEnquiry.appendResult(ServiceCaller.internal(SIMAH_SERVICE_ID, CONSUMER_ENQUIRY_OPERATION_ID,
                    inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getConsumerEnquiry);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    SIMAH_SERVICE_ID + " : " + CONSUMER_ENQUIRY_OPERATION_ID);
            return getConsumerEnquiry;
        } catch (Exception ex) {
            LOG.error("ERROR getSIMAHConsumerEnquiry :: " + ex);
        }
        return result;
    }

    private Result getSIMAHSalaryCertificate(Map<String, String> inputParams,
            DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getSalaryCertificate = StatusEnum.success.setStatus();

            getSalaryCertificate.appendResult(ServiceCaller.internal(SIMAH_SALARY_ORCH_SERVICE_ID,
                    SIMAH_SALARY_CERT_OPERATION_ID, inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getSalaryCertificate);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    SIMAH_SALARY_ORCH_SERVICE_ID + " : " + SIMAH_SALARY_CERT_OPERATION_ID);
            return getSalaryCertificate;
        } catch (Exception ex) {
            LOG.error("ERROR getSIMAHSalaryCertificate :: " + ex);
        }
        return result;
    }

    private Result getNationalAddress(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getNationalAddress = StatusEnum.success.setStatus();
            inputParams.put("nin", NATIONAL_ID);
            inputParams.put("dateOfBirth", DOB);
            getNationalAddress.appendResult(ServiceCaller.internal(YAKEEN_SOAP_API_SERVICE_ID,
                    GET_CITIZEN_ADDRESS_INFO_OPERATION_ID, inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getNationalAddress);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    YAKEEN_SOAP_API_SERVICE_ID + " : " + GET_CITIZEN_ADDRESS_INFO_OPERATION_ID);
            return getNationalAddress;
        } catch (Exception ex) {
            LOG.error("ERROR getNationalAddress :: " + ex);
        }
        return result;
    }

    private Result calculateScoreCardS2(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getScoreCardS2 = StatusEnum.success.setStatus();
            getScoreCardS2.appendResult(ServiceCaller.internal(KNOCKOUT_SERVICE_ID, CALCULATE_SCORECARD_S2_OPERATION_ID,
                    inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getScoreCardS2);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    KNOCKOUT_SERVICE_ID + " : " + CALCULATE_SCORECARD_S2_OPERATION_ID);
            return getScoreCardS2;
        } catch (Exception ex) {
            LOG.error("ERROR calculateScoreCardS2 :: " + ex);
        }
        return result;
    }

    private Result calculateScoreCardS3(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getScoreCardS3 = StatusEnum.success.setStatus();
            getScoreCardS3.appendResult(ServiceCaller.internal(KNOCKOUT_SERVICE_ID, CALCULATE_SCORECARD_S3_OPERATION_ID,
                    inputParams, null, dataControllerRequest));
            String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
            String outputResponse = ResultToJSON.convert(getScoreCardS3);
            auditLogData(dataControllerRequest, inputRequest, outputResponse,
                    KNOCKOUT_SERVICE_ID + " : " + CALCULATE_SCORECARD_S3_OPERATION_ID);
            return getScoreCardS3;
        } catch (Exception ex) {
            LOG.error("ERROR calculateScoreCardS3 :: " + ex);
        }
        return result;
    }

    private void createCustomerAddress(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
        try {
            ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, CUSTOMER_ADDRESS_CREATE_OPERATION_ID, inputParams, null,
                    dataControllerRequest);
        } catch (Exception ex) {
            LOG.error("ERROR createCustomerAddress :: " + ex);
        }
    }

    private Result updateCustomerApplicationData(Map<String, String> inputParams,
            DataControllerRequest dataControllerRequest) {
        Result updateCustomerApplicationData = StatusEnum.success.setStatus();
        updateCustomerApplicationData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
                CUSTOMER_APPLICATION_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest));
        return updateCustomerApplicationData;
    }

}