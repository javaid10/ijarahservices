package com.ijarah.services;

import com.google.common.primitives.Chars;
import com.google.gson.Gson;
import com.ijarah.Model.ScorecardS3.ScoreCardS3;
import com.ijarah.Model.consumerEnquiryModel.*;
import com.ijarah.Model.consumerEnquiryModel.RESPONSEItem;
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
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;

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
    String MONTHLY_NET_SALARY = "0";
    String CURRENT_LENGTH_OF_SERVICE = "0";
    String GLOBAL_DTI = "";
    String INTERNAL_DTI = "";
    String CURRENT_DELINQUENCY = "";
    private String CURRENT_DELINQUENCY_T = "";
    String MAX_DELINQUENCY = "";
    String NON_FINANCIAL_DEFAULT_AMOUNT = "0";
    String BOUNCED_CHEQUE = "NB";
    String COURT_JUDGEMENT = "NJ";
    String EMPLOYER_CATEGORISATION = "";
    String MAX_LOAN_AMOUNT_CAPPING = "";
    String MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY = "0";
    String NEW_TO_INDUSTRY = "";
    String SALARY_WITHOUT_ALLOWANCES = "0";
    String INSIDE_KSA = "";
    String NATIONALITY = "";

    String EMPLOYER_TYPE_ID = "1";
    String EMPLOYMENT_STATUS = "";

    String[] MORTGAGE_PRODUCT = {"AMTG", "AQAR", "EMTG", "IMTG", "MMTG", "MSKN", "MTG", "OMTG", "RMSKN", "RMTG",
            "SMTG", "TMTG"};
    String[] CREDIT_CARD_PRODUCT = {"CDC", "CHC", "CRC", "LCRC"};
    String[] NON_FINANCIAL_PRODUCTS = {"MBL", "LND", "DAT", "NET"};
    char[] CURRENT_DELINQUENCY_VALUES = {'0', 'C', 'D', 'N'};

    int MAX_GLOBAL_DTI = 45;
    int MAX_INTERNAL_DTI = 33;
    private String FINANCIAL_DEFAULT_AMOUNT = "0";

    private String DATA_TYPE = "";

    private String TENOR = "0";

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
    private String EMPLOYER_NAME = "-";
    private List<CIDETAILItem> CI_DETAIL;
    private List<DEFAULTItem> DEFAULT;
    private List<BOUNCEDCHECKItem> BOUNCED_CHECK;
    private List<JUDGEMENTItem> JUDGEMENT;
    private String CUSTOMER_ID = "";

    private double MAX_EMI = 0;
    private double CUSTOMER_GLOBAL_DTI = 0.0;
    private double CUSTOMER_INTERNAL_DTI = 0.0;

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {

        inputParams = HelperMethods.getInputParamMap(objects);
        Result result = StatusEnum.error.setStatus();
        IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

        if (preProcess(dataControllerRequest)) {

            // DB INTEGRATION SERVICES CALLS
            Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);
            extractValuesFromCustomerApplication(getCustomerApplicationData);

            Result getCustomerData = getCustomerData(dataControllerRequest);

            // 3RD PARTY INTEGRATION SERVICES CALLS

            Result getSalaryCertificate = getSIMAHSalaryCertificate(
                    createRequestForSIMAHSALARY(getCustomerData, EMPLOYER_TYPE_ID, NATIONAL_ID), dataControllerRequest);
            if (!IjarahHelperMethods.isBlank(getSalaryCertificate.getParamValueByName("payMonth"))) {
                LOG.error("payMonth :: " + getSalaryCertificate.getParamValueByName("payMonth"));
                int payYear = Integer.parseInt(getSalaryCertificate.getParamValueByName("payMonth").substring(0, 4));
                int payMonth = Integer.parseInt(getSalaryCertificate.getParamValueByName("payMonth").substring(4, 6));
                int payDate = payMonth + payYear * 12;
                LocalDate Date = LocalDate.now();
                int currentMonth = Date.getMonthValue();
                int currentYear = Date.getYear();
                int currentDate = currentMonth + currentYear * 12;

                LOG.error("payYear :: " + payYear);
                LOG.error("payMonth :: " + payMonth);
                LOG.error("currentMonth :: " + currentMonth);
                LOG.error("currentYear :: " + currentYear);

                if (currentDate - payDate > 1) {
                    EMPLOYER_TYPE_ID = "3";
                    this.inputParams.put("employerTypeId", EMPLOYER_TYPE_ID);
                    getSalaryCertificate = getSIMAHSalaryCertificate(
                            createRequestForSIMAHSALARY(getCustomerData, EMPLOYER_TYPE_ID, NATIONAL_ID),
                            dataControllerRequest);
                }

            }
            //Result getNationalAddress = getNationalAddress(inputParams, dataControllerRequest);
            //createCustomerAddress(createRequestForCreateCustomerAddressService(getNationalAddress), dataControllerRequest);

            // CALCULATION OF SCORING ENGINES
            calculatePensioner();
            calculateCurrentLengthOfService(getSalaryCertificate);
            getEmployerName(getSalaryCertificate);
            calculateManagingSeasonalAndTemporaryLiftInSalary(getSalaryCertificate);
            calculateMonthlyNetSalary(getSalaryCertificate);
            calculateSalaryWithoutAllowances(getSalaryCertificate);

            // 3RD PARTY INTEGRATION SERVICES CALLS
            Result getScoreCardS2 = calculateScoreCardS2(createRequestForScoreCardS2Service(getSalaryCertificate),
                    dataControllerRequest);
            if (getScoreCardS2.hasParamByName("applicationCategory")
                    && !IjarahHelperMethods.isBlank(getScoreCardS2.getParamValueByName("applicationCategory"))) {

                if (getScoreCardS2.getParamValueByName("applicationCategory").equals("0")) {

                    // DB INTEGRATION SERVICES CALLS
                    result = updateCustomerApplicationData(createRequestForUpdateCustomerApplicationDataServiceS2(
                            getCustomerApplicationData, getScoreCardS2), dataControllerRequest, true);

                    return result;
                }
            }
            Result getConsumerEnquiry = getSIMAHConsumerEnquiry(createRequestForConsumerEnquiryService(inputParams,
                    getCustomerData, getSalaryCertificate), dataControllerRequest);
            Gson gson = new Gson();
            ConsumerEnquiry consumerEnquiry = gson.fromJson(ResultToJSON.convert(getConsumerEnquiry), ConsumerEnquiry.class);

            extractValuesFromConsumerEnquiryResponse(consumerEnquiry);

            calculateMaxGlobalDTI();
            calculateMaxInternalDTI();
            calculateGlobalDTI();
            calculateInternalDTI();
            calculateCurrentDelinquencyAndCurrentDelinquencyT(); //There are multiple products so of which product we need to calculate current delinquency
            calculateMaxDelinquency(); //There are multiple products so of which product we need to calculate max delinquency
            calculateFinancialDefaultAmount();
            calculateNonFinancialDefaultAmount();
            calculateBouncedCheque();
            calculateCourtJudgement();
            calculateMaxLoanAmountCapping();
            calculateNewToIndustry();

            Result getScoreCardS3 = calculateScoreCardS3(createRequestForScoreCardS3Service(getConsumerEnquiry),
                    dataControllerRequest);

            calculateMaxEmi();


            // DB INTEGRATION SERVICES CALLS
            result = updateCustomerApplicationData(createRequestForUpdateCustomerApplicationDataService(
                    getCustomerApplicationData, getScoreCardS2, getScoreCardS3), dataControllerRequest, false);
        }
        return result;
    }

    private void extractValuesFromConsumerEnquiryResponse(ConsumerEnquiry consumerEnquiry) {
        CONSUMERItem CONSUMER = new CONSUMERItem();
        boolean isConsumer = false;
        if (consumerEnquiry.getDATA() != null && consumerEnquiry.getDATA().size() > 0) {
            DATAItem DATA = consumerEnquiry.getDATA().get(0);
            if (DATA.getRESPONSE() != null && DATA.getRESPONSE().size() > 0) {
                RESPONSEItem RESPONSE = DATA.getRESPONSE().get(0);
                if (RESPONSE.getMESSAGE() != null && RESPONSE.getMESSAGE().size() > 0) {
                    MESSAGEItem MESSAGE = RESPONSE.getMESSAGE().get(0);
                    if (MESSAGE.getITEM() != null && MESSAGE.getITEM().size() > 0) {
                        ITEMItem ITEM = MESSAGE.getITEM().get(0);
                        if (ITEM.getRSPREPORT() != null && ITEM.getRSPREPORT().size() > 0) {
                            RSPREPORTItem RSPREPORT = ITEM.getRSPREPORT().get(0);
                            if (RSPREPORT.getCONSUMER() != null && RSPREPORT.getCONSUMER().size() > 0) {
                                CONSUMER = RSPREPORT.getCONSUMER().get(0);
                                isConsumer = true;
                                if (CONSUMER.getCIDETAILS() != null && CONSUMER.getCIDETAILS().size() > 0) {
                                    CIDETAILSItem CI_DETAILS = CONSUMER.getCIDETAILS().get(0);
                                    if (CI_DETAILS.getCIDETAIL() != null && CI_DETAILS.getCIDETAIL().size() > 0) {
                                        CI_DETAIL = CI_DETAILS.getCIDETAIL();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (isConsumer) {
            if (CONSUMER.getDEFAULTS() != null && CONSUMER.getDEFAULTS().size() > 0) {
                DEFAULTSItem DEFAULTS = CONSUMER.getDEFAULTS().get(0);
                if (DEFAULTS.getDEFAULT() != null && DEFAULTS.getDEFAULT().size() > 0) {
                    DEFAULT = DEFAULTS.getDEFAULT();
                }
            }
        }
        if (isConsumer) {
            if (CONSUMER.getBOUNCEDCHECKS() != null && CONSUMER.getBOUNCEDCHECKS().size() > 0) {
                BOUNCEDCHECKSItem BOUNCED_CHECKS = CONSUMER.getBOUNCEDCHECKS().get(0);
                if (BOUNCED_CHECKS.getBOUNCEDCHECK() != null && BOUNCED_CHECKS.getBOUNCEDCHECK().size() > 0) {
                    BOUNCED_CHECK = BOUNCED_CHECKS.getBOUNCEDCHECK();
                }
            }
        }
        if (isConsumer) {
            if (CONSUMER.getJUDGEMENTS() != null && CONSUMER.getJUDGEMENTS().size() > 0) {
                JUDGEMENTSItem JUDGEMENTS = CONSUMER.getJUDGEMENTS().get(0);
                if (JUDGEMENTS.getJUDGEMENT() != null && JUDGEMENTS.getJUDGEMENT().size() > 0) {
                    JUDGEMENT = JUDGEMENTS.getJUDGEMENT();
                }
            }
        }
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
                                                                       Result getCustomerData, Result getSalaryCertificate) {
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
            inputParams.put("CAD7", "-" /* getNationalAddress.getParamValueByName("postCode") */);
            inputParams.put("CAD8E", "-" /* getNationalAddress.getParamValueByName("city") */);
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
            double doubleRate = Double.parseDouble(rate);
            double rateFees = 1.15 + doubleRate;
            double monthlyRate = rateFees / 12;
            LOG.error("Calculated Monthly Rate :: " + monthlyRate);
            double aprD = (Math.pow(1 + monthlyRate, 12)) - 1;
            apr = Double.toString(Math.ceil(aprD));
            LOG.error("Calculated final APR :: " + apr);
        } catch (Exception e) {
            LOG.error("Error in APR calculation :: " + e);
        }
        return apr;
    }

    private Map<String, String> createRequestForUpdateCustomerApplicationDataServiceS2(Result getCustomerApplicationData,
                                                                                       Result getScoreCardS2) {


        Map<String, String> inputParams = new HashMap<>();

        inputParams.put("id", HelperMethods.getFieldValue(getCustomerApplicationData, "id"));
        inputParams.put("Customer_id", HelperMethods.getFieldValue(getCustomerApplicationData, "Customer_id"));
        inputParams.put("mobile", HelperMethods.getFieldValue(getCustomerApplicationData, "mobile"));
        inputParams.put("nationalId", NATIONAL_ID);
        inputParams.put("productId", HelperMethods.getFieldValue(getCustomerApplicationData, "productId"));
        inputParams.put("productName", DATA_TYPE);
        inputParams.put("isknockouts1", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockouts1"));
        inputParams.put("knockoutStatus", "FAIL");
        inputParams.put("applicationStatus", "SID_SUSPENDED");
        inputParams.put("applicationID", HelperMethods.getFieldValue(getCustomerApplicationData, "applicationID"));
        inputParams.put("createdby", HelperMethods.getFieldValue(getCustomerApplicationData, "createdby"));
        inputParams.put("modifiedby", HelperMethods.getFieldValue(getCustomerApplicationData, "modifiedby"));
        //inputParams.put("lastmodifiedts", IjarahHelperMethods.getDate(LocalDateTime.now(), DATE_FORMAT_WITH_SECONDS_MS));
        inputParams.put("isknockoutTnC", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockoutTnC"));
        inputParams.put("loanAmount", HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount"));
        inputParams.put("tenor", TENOR);
        inputParams.put("approx", "0");
        inputParams.put("scoredCardId", HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId"));
        inputParams.put("loanAmountCap", "0");
        inputParams.put("loanRate", "0");
        inputParams.put("insideKsa", INSIDE_KSA);
        inputParams.put("customerAge", HelperMethods.getFieldValue(getCustomerApplicationData, "customerAge"));
        inputParams.put("loanAmountCore", "0");
        inputParams.put("loanAmountInf", "0");
        inputParams.put("monthlyRepay", "0");
        inputParams.put("offerAmount", "0");
        inputParams.put("csaApporval", "0");
        inputParams.put("sanadApproval", "0");

        if (getScoreCardS2.hasParamByName("tenor")
                && !IjarahHelperMethods.isBlank(getScoreCardS2.getParamValueByName("tenor"))) {
            inputParams.put("tenorCore", getScoreCardS2.getParamValueByName("tenor"));
        } else {
            inputParams.put("tenorCore", "-");
        }

        LOG.error(inputParams);
        return inputParams;
    }

    private Map<String, String> createRequestForUpdateCustomerApplicationDataService(Result getCustomerApplicationData,
                                                                                     Result getScoreCardS2, Result getScoreCardS3) {
        Map<String, String> inputParams = new HashMap<>();
        String knockoutStatus = "FAIL";
        String applicationStatus = "SID_SUSPENDED";
        String approx = "0";
        String loanRate = "0";
        String loanAmountCap = "0";
        String tenor = "0";

        Gson gson = new Gson();
        ScoreCardS3 scoreCardS3 = gson.fromJson(ResultToJSON.convert(getScoreCardS3), ScoreCardS3.class);

        if (scoreCardS3.getBody() != null) {
            if (scoreCardS3.getBody().getApplicationCategory() != null) {
                if (scoreCardS3.getBody().getApplicationCategory().equalsIgnoreCase("0")) {
                    knockoutStatus = "FAIL";
                    applicationStatus = "SID_SUSPENDED";
                } else {
                    knockoutStatus = "PASS";
                    applicationStatus = "SID_PRO_ACTIVE";
                }
            }
            if (scoreCardS3.getBody().getLoanRate() != null) {
                approx = scoreCardS3.getBody().getLoanRate();
                loanRate = scoreCardS3.getBody().getLoanRate();
            }

            if (scoreCardS3.getBody().getLoanAmountCap() != null) {
                loanAmountCap = scoreCardS3.getBody().getLoanAmountCap();
            }

            if (scoreCardS3.getBody().getTenor() != null) {
                tenor = scoreCardS3.getBody().getTenor();
            }
        }


        inputParams.put("id", HelperMethods.getFieldValue(getCustomerApplicationData, "id"));
        inputParams.put("Customer_id", HelperMethods.getFieldValue(getCustomerApplicationData, "Customer_id"));
        inputParams.put("mobile", HelperMethods.getFieldValue(getCustomerApplicationData, "mobile"));
        inputParams.put("nationalId", NATIONAL_ID);
        inputParams.put("productId", HelperMethods.getFieldValue(getCustomerApplicationData, "productId"));
        inputParams.put("productName", DATA_TYPE);
        inputParams.put("isknockouts1", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockouts1"));
        inputParams.put("knockoutStatus", knockoutStatus);
        inputParams.put("applicationStatus", applicationStatus);
        inputParams.put("applicationID", HelperMethods.getFieldValue(getCustomerApplicationData, "applicationID"));
        inputParams.put("createdby", HelperMethods.getFieldValue(getCustomerApplicationData, "createdby"));
        inputParams.put("modifiedby", HelperMethods.getFieldValue(getCustomerApplicationData, "modifiedby"));
        //inputParams.put("lastmodifiedts", IjarahHelperMethods.getDate(LocalDateTime.now(), DATE_FORMAT_WITH_SECONDS_MS));
        inputParams.put("isknockoutTnC", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockoutTnC"));
        inputParams.put("loanAmount", HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount"));
        inputParams.put("tenor", TENOR);
        inputParams.put("approx", approx);
        inputParams.put("scoredCardId", HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId"));
        inputParams.put("loanAmountCap", loanAmountCap);
        inputParams.put("loanRate", loanRate);
        inputParams.put("insideKsa", INSIDE_KSA);
        inputParams.put("customerAge", HelperMethods.getFieldValue(getCustomerApplicationData, "customerAge"));
        inputParams.put("loanAmountCore", loanAmountCap);

        if (!loanRate.equalsIgnoreCase("0")) {

            inputParams.put("loanAmountInf", calculateLoanAmountInf(Double.parseDouble(loanRate), Integer.parseInt(tenor)));
            double amountOffer = Math.min(Math.min(Double.parseDouble(inputParams.get("loanAmountCap")), Double.parseDouble(inputParams.get("loanAmountInf"))), Double.parseDouble(inputParams.get("loanAmount").replaceAll(",", "")));
            inputParams.put("monthlyRepay", calculateMonthlyRepay(amountOffer, Double.parseDouble(loanRate), Integer.parseInt(tenor)));
            inputParams.put("offerAmount", String.valueOf(amountOffer));
        } else {
            inputParams.put("loanAmountInf", "0");
            inputParams.put("monthlyRepay", "0");
            inputParams.put("offerAmount", "0");
        }
        inputParams.put("tenorCore", tenor);
        inputParams.put("csaApporval", "0");
        inputParams.put("sanadApproval", "0");

        LOG.error("createRequestForUpdateCustomerApplicationDataService :: " + inputParams);
        return inputParams;
    }

    private String calculateLoanAmountInf(double loanRate, int tenor) {
        LOG.error("calculateLoanAmountInf");
        double totalPayableAmount = MAX_EMI * tenor;
        double totalProfitRate = (loanRate * tenor) / 12;
        double principalPlusProfitRate = totalProfitRate + 100;

        LOG.error("MAX_EMI :: " + MAX_EMI);
        LOG.error("tenor :: " + tenor);
        LOG.error("totalPayableAmount :: " + totalPayableAmount);
        LOG.error("totalProfitRate :: " + totalProfitRate);
        LOG.error("principalPlusProfitRate :: " + principalPlusProfitRate);

        return String.valueOf(Math.floor((totalPayableAmount / principalPlusProfitRate) * 100));
    }

    private String calculateMonthlyRepay(double amountOffer, double loanRate, int tenor) {
        return String.valueOf((amountOffer + (amountOffer * loanRate * tenor / 12)) / tenor);
    }

    private void calculatePensioner() {
        try {
            if (Integer.parseInt(CUSTOMER_AGE) > 60) {
                PENSIONER = "1";
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculatePensioner :: " + ex);
        }
    }

    private void calculateMonthlyNetSalary(Result getSalaryCertificate) {
        try {
            NATIONALITY = getSalaryCertificate.getParamValueByName("nationality");
            switch (EMPLOYER_TYPE_ID) {
                case "1":
                    MONTHLY_NET_SALARY = getSalaryCertificate.getParamValueByName("netSalary");
                    break;
                case "3":
                    EMPLOYMENT_STATUS = getSalaryCertificate.getParamValueByName("employmentStatus");
                    if (EMPLOYMENT_STATUS.equalsIgnoreCase("Active")) {
                        double calculatedDeductions = 0;
                        if (NATIONALITY.equalsIgnoreCase("SAU")) {
                            double minimumAmount = 0.1
                                    * (Integer.parseInt(getSalaryCertificate.getParamValueByName("basicWage"))
                                    + Integer
                                    .parseInt(getSalaryCertificate
                                            .getParamValueByName("housingAllowance")));
                            calculatedDeductions = Math.min(minimumAmount, 4500);
                        }
                        MONTHLY_NET_SALARY = String
                                .valueOf((Integer.parseInt(getSalaryCertificate.getParamValueByName("basicWage"))
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
        SALARY_WITHOUT_ALLOWANCES = MONTHLY_NET_SALARY;
    }

    private void calculateCurrentLengthOfService(Result getSalaryCertificate) {
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

    private void calculateMaxGlobalDTI() {
        if (DATA_TYPE.equalsIgnoreCase("TAWARRUQ")) {
            if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
                for (CIDETAILItem ci_detail : CI_DETAIL) {
                    String productType = ci_detail.getCIPRD();
                    String status = ci_detail.getCISTATUS();
                    if (Arrays.asList(MORTGAGE_PRODUCT).contains(productType) && status.equalsIgnoreCase("A")) {
                        if (Integer.parseInt(MONTHLY_NET_SALARY) < 3000) {
                            MAX_GLOBAL_DTI = 65;
                        } else if (Integer.parseInt(MONTHLY_NET_SALARY) < 14999) {
                            MAX_GLOBAL_DTI = 65;
                        }
                        break;
                    } else {
                        if (Integer.parseInt(MONTHLY_NET_SALARY) < 3000) {
                            MAX_GLOBAL_DTI = 65;
                        } else if (Integer.parseInt(MONTHLY_NET_SALARY) < 14999) {
                            MAX_GLOBAL_DTI = 65;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void calculateMaxInternalDTI() {
        if (DATA_TYPE.equalsIgnoreCase("TAWARRUQ")) {
            if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
                for (CIDETAILItem ci_detail : CI_DETAIL) {
                    String productType = ci_detail.getCIPRD();
                    String status = ci_detail.getCISTATUS();
                    if (Arrays.asList(MORTGAGE_PRODUCT).contains(productType) && status.equalsIgnoreCase("A")) {
                        if (Integer.parseInt(MONTHLY_NET_SALARY) < 3000) {
                            MAX_INTERNAL_DTI = 25;
                        } else if (Integer.parseInt(MONTHLY_NET_SALARY) < 14999) {
                            MAX_INTERNAL_DTI = 30;
                        }
                        break;
                    } else {
                        if (Integer.parseInt(MONTHLY_NET_SALARY) < 3000) {
                            MAX_INTERNAL_DTI = 25;
                        } else if (Integer.parseInt(MONTHLY_NET_SALARY) < 14999) {
                            MAX_INTERNAL_DTI = 30;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void calculateMaxEmi() {
        int final_max_allowable_dti = Math.min(calculateMaxOverallAllowedDTI(), calculateMaxInternalAllowedDTI());
        MAX_EMI = Integer.parseInt(MONTHLY_NET_SALARY) * final_max_allowable_dti;
        MAX_EMI = MAX_EMI / 100;
    }

    private int calculateMaxOverallAllowedDTI() {
        return Math.max(((int) (MAX_GLOBAL_DTI - CUSTOMER_GLOBAL_DTI)), 0);
    }

    private int calculateMaxInternalAllowedDTI() {
        return Math.max(((int) (MAX_INTERNAL_DTI - CUSTOMER_INTERNAL_DTI)), 0);
    }

    private void calculateGlobalDTI() {

        GLOBAL_DTI = "1";
        if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
            double totalDebtServicing = 0;
            for (CIDETAILItem ci_detail : CI_DETAIL) {
                String productType = ci_detail.getCIPRD();

                String status = ci_detail.getCISTATUS();
                if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType) && status.equalsIgnoreCase("A")) {
                    // Financial product
                    String frequency = ci_detail.getCIFRQ();
                    double installment = Double.parseDouble(ci_detail.getCIINSTL());
                    String creditLimit = ci_detail.getCILIMIT();

                    if (Arrays.asList(CREDIT_CARD_PRODUCT).contains(productType)) {
                        totalDebtServicing += 0.05 * Double.parseDouble(creditLimit);
                    } else {
                        if (frequency.equalsIgnoreCase("M")) {
                            totalDebtServicing += installment;
                        } else if (frequency.equalsIgnoreCase("Q")) {
                            totalDebtServicing += installment / 3;
                        } else if (frequency.equalsIgnoreCase("H")) {
                            totalDebtServicing += installment / 6;
                        } else if (frequency.equalsIgnoreCase("Y")) {
                            totalDebtServicing += installment / 12;
                        } else {
                            totalDebtServicing += installment;
                        }
                    }
                }
            }
            CUSTOMER_GLOBAL_DTI = (totalDebtServicing / Integer.parseInt(MONTHLY_NET_SALARY)) * 100;
            if (CUSTOMER_GLOBAL_DTI >= MAX_GLOBAL_DTI) {
                GLOBAL_DTI = "0";
            }
        }
    }

    private void calculateInternalDTI() {
        INTERNAL_DTI = "1";
        if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
            double totalDebtServicing = 0;
            for (CIDETAILItem ci_detail : CI_DETAIL) {
                String productType = ci_detail.getCIPRD();
                String productCode = ci_detail.getCICRDTR();

                String status = ci_detail.getCISTATUS();
                if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType) && status.equalsIgnoreCase("A") && productCode.equalsIgnoreCase("IJRH")) {
                    // Financial product
                    String frequency = ci_detail.getCIFRQ();
                    double installment = Double.parseDouble(ci_detail.getCIINSTL());
                    String creditLimit = ci_detail.getCILIMIT();

                    if (Arrays.asList(CREDIT_CARD_PRODUCT).contains(productType)) {
                        totalDebtServicing += 0.05 * Integer.parseInt(creditLimit);
                    } else {
                        if (frequency.equalsIgnoreCase("M")) {
                            totalDebtServicing += installment;
                        } else if (frequency.equalsIgnoreCase("Q")) {
                            totalDebtServicing += installment / 3;
                        } else if (frequency.equalsIgnoreCase("H")) {
                            totalDebtServicing += installment / 6;
                        } else if (frequency.equalsIgnoreCase("Y")) {
                            totalDebtServicing += installment / 12;
                        } else {
                            totalDebtServicing += installment;
                        }
                    }
                }
            }
            CUSTOMER_INTERNAL_DTI = (totalDebtServicing / Integer.parseInt(MONTHLY_NET_SALARY)) * 100;
            if (CUSTOMER_INTERNAL_DTI >= MAX_INTERNAL_DTI) {
                INTERNAL_DTI = "0";
            }
        }
    }

    private void calculateCurrentDelinquencyAndCurrentDelinquencyT() {
        CURRENT_DELINQUENCY_T = "1";
        if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
            for (CIDETAILItem ci_detail : CI_DETAIL) {
                String productType = ci_detail.getCIPRD();
                String summary = ci_detail.getCISUMMRY();
                if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                    // Financial Product
                    char firstMonthDelinquency = summary.charAt(0);
                    if (Chars.contains(CURRENT_DELINQUENCY_VALUES, firstMonthDelinquency)) {
                        CURRENT_DELINQUENCY = "1";
                    } else {
                        CURRENT_DELINQUENCY = "0";
                        break;
                    }
                    if (firstMonthDelinquency == 'M') {
                        CURRENT_DELINQUENCY_T = "0";
                    }
                }
            }
        }
    }

    private void calculateMaxDelinquency() {
        MAX_DELINQUENCY = "0";
        if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
            int tempMaxDelinquency = 0;
            for (CIDETAILItem ci_detail : CI_DETAIL) {
                String productType = ci_detail.getCIPRD();
                String summary = ci_detail.getCISUMMRY();
                if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                    // Financial Product
                    char[] summaryChar = summary.toCharArray();
                    for (char delinquencyChar : summaryChar) {
                        if (Character.isDigit(delinquencyChar)) {
                            if (tempMaxDelinquency < delinquencyChar) {
                                tempMaxDelinquency = delinquencyChar;
                            }
                        }
                    }
                    MAX_DELINQUENCY = String.valueOf(Character.getNumericValue(tempMaxDelinquency));
                }
            }
        }
    }

    private void calculateFinancialDefaultAmount() {
        int FINANCIAL_DEFAULT_AMOUNT = 0;
        if (DEFAULT != null && DEFAULT.size() > 0) {
            for (DEFAULTItem defaultItem : DEFAULT) {
                String productType = defaultItem.getDFPRD();
                if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                    double FINANCIAL_DEFAULT_AMOUNT_TEMP = Double.parseDouble(defaultItem.getDFCUB());
                    FINANCIAL_DEFAULT_AMOUNT += FINANCIAL_DEFAULT_AMOUNT_TEMP;
                }
            }
        }
        this.FINANCIAL_DEFAULT_AMOUNT = String.valueOf(FINANCIAL_DEFAULT_AMOUNT);
    }

    private void calculateNonFinancialDefaultAmount() {
        int NON_FINANCIAL_DEFAULT_AMOUNT = 0;
        if (DEFAULT != null && DEFAULT.size() > 0) {
            for (DEFAULTItem defaultItem : DEFAULT) {
                String productType = defaultItem.getDFPRD();
                if (Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                    double NON_FINANCIAL_DEFAULT_AMOUNT_TEMP = Double.parseDouble(defaultItem.getDFCUB());
                    NON_FINANCIAL_DEFAULT_AMOUNT += NON_FINANCIAL_DEFAULT_AMOUNT_TEMP;
                }
            }
        }
        this.NON_FINANCIAL_DEFAULT_AMOUNT = String.valueOf(NON_FINANCIAL_DEFAULT_AMOUNT);
    }

    private void calculateBouncedCheque() {

        if (BOUNCED_CHECK != null && BOUNCED_CHECK.size() > 0) {
            BOUNCEDCHECKItem bounced_cheque = BOUNCED_CHECK.get(0);
            if (bounced_cheque.getBCSETTLDDATE() != null) {
                if (IjarahHelperMethods.isBlank(bounced_cheque.getBCSETTLDDATE())) {
                    BOUNCED_CHEQUE = "SB";
                } else {
                    BOUNCED_CHEQUE = "UB";
                }
            } else {
                BOUNCED_CHEQUE = "NB";
            }
        }
    }

    private void calculateCourtJudgement() {

        if (JUDGEMENT != null && JUDGEMENT.size() > 0) {
            JUDGEMENTItem judgement = JUDGEMENT.get(0);
            if (judgement.getEJSETTLEDATE() != null) {
                if (IjarahHelperMethods.isBlank(judgement.getEJSETTLEDATE())) {
                    COURT_JUDGEMENT = "SJ";
                } else {
                    COURT_JUDGEMENT = "UJ";
                }
            } else {
                COURT_JUDGEMENT = "NJ";
            }
        }
    }

    private void getEmployerName(Result getSalaryCertificate) {
        try {
            if (getSalaryCertificate.getParamValueByName("employeeNameEn") != null) {
                EMPLOYER_NAME = IjarahHelperMethods
                        .checkStringNull(getSalaryCertificate.getParamValueByName("employeeNameEn"));
            }
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

    private void calculateNewToIndustry() {
        try {
            NEW_TO_INDUSTRY = "Y";
            if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
                for (CIDETAILItem ci_detail : CI_DETAIL) {
                    String productType = ci_detail.getCIPRD();
                    if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
                        NEW_TO_INDUSTRY = "N";
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateNewToIndustry :: " + ex);
        }
    }

    private void calculateMaxLoanAmountCapping() {
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

    private void calculateManagingSeasonalAndTemporaryLiftInSalary(Result getSalaryCertificate) {
        try {
            double basicSalary = 0;
            double OtherAllowance = 0;
            MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY = "1";
            switch (EMPLOYER_TYPE_ID) {
                case "1": // Govt
                    basicSalary = Double.parseDouble(getSalaryCertificate.getParamValueByName("basicSalary"));
                    OtherAllowance = Integer.parseInt(getSalaryCertificate.getParamValueByName("totalAllownces"));
                    break;
                case "3": // Private
                    basicSalary = Double.parseDouble(getSalaryCertificate.getParamValueByName("basicWage"));
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

    private void calculateSalaryWithoutAllowances(Result getSalaryCertificate) {
        try {
            switch (EMPLOYER_TYPE_ID) {
                case "1": // Govt
                    SALARY_WITHOUT_ALLOWANCES = String.valueOf(Double.parseDouble(MONTHLY_NET_SALARY)
                            - Double.parseDouble(getSalaryCertificate.getParamValueByName("totalAllownces")));
                    break;
                case "3": // Private
                    SALARY_WITHOUT_ALLOWANCES = String.valueOf(Double.parseDouble(MONTHLY_NET_SALARY)
                            - Double.parseDouble(getSalaryCertificate.getParamValueByName("otherAllowance")));
                    break;
            }
        } catch (Exception ex) {
            LOG.error("ERROR calculateSalaryWithoutAllowances :: " + ex);
        }
    }

    private Map<String, String> createRequestForScoreCardS2Service(Result getSalaryCertificate) {
        Map<String, String> inputParams = new HashMap<>();
        try {

            inputParams.put("scorecardId", SCORECARD_ID);
            inputParams.put("dataType", DATA_TYPE);
            inputParams.put("customerAge", CUSTOMER_AGE);
            inputParams.put("insideKsa", INSIDE_KSA);
            inputParams.put("loanRef", APPLICATION_ID);
            inputParams.put("validDti", String.valueOf(MAX_GLOBAL_DTI));
            inputParams.put("currDelinquency", CURRENT_DELINQUENCY);
            inputParams.put("maxDelinquency", MAX_DELINQUENCY);
            inputParams.put("bouncedCheck", BOUNCED_CHEQUE);
            inputParams.put("courtJudgement", COURT_JUDGEMENT);
            inputParams.put("validUtil", NON_FINANCIAL_DEFAULT_AMOUNT);
            inputParams.put("validDefaults", FINANCIAL_DEFAULT_AMOUNT);
            inputParams.put("validInternalDti", INTERNAL_DTI);
            inputParams.put("currDelinquencyT", CURRENT_DELINQUENCY_T);
            inputParams.put("loanAmountCap", MAX_LOAN_AMOUNT_CAPPING);
            inputParams.put("nationality", NATIONALITY);
            inputParams.put("newIndustry", NEW_TO_INDUSTRY);


            inputParams.put("calculate", CALCULATE);
            inputParams.put("tenor", TENOR);
            inputParams.put("pensioner", PENSIONER);
            inputParams.put("validLos", CURRENT_LENGTH_OF_SERVICE);
            inputParams.put("employeeName", EMPLOYER_NAME);
            inputParams.put("liftSalary", MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY);
            inputParams.put("salaryNonAllowance", SALARY_WITHOUT_ALLOWANCES);
            inputParams.put("salaryAmount", MONTHLY_NET_SALARY);

        } catch (Exception ex) {
            LOG.error("ERROR createRequestForScoreCardS2Service :: " + ex);
        }
        return inputParams;
    }

    private Map<String, String> createRequestForScoreCardS3Service(Result getConsumerEnquiry) {
        Map<String, String> inputParams = new HashMap<>();
        try {

            inputParams.put("scorecardId", SCORECARD_ID);
            inputParams.put("dataType", DATA_TYPE);
            inputParams.put("calculate", CALCULATE);
            inputParams.put("pensioner", PENSIONER);
            inputParams.put("customerAge", CUSTOMER_AGE);
            inputParams.put("salaryAmount", MONTHLY_NET_SALARY);
            inputParams.put("insideKsa", INSIDE_KSA);
            inputParams.put("loanRef", APPLICATION_ID);
            inputParams.put("validLos", CURRENT_LENGTH_OF_SERVICE);
            inputParams.put("validDti", GLOBAL_DTI);
            inputParams.put("currDelinquency", CURRENT_DELINQUENCY);
            inputParams.put("maxDelinquency", MAX_DELINQUENCY);
            inputParams.put("bouncedCheck", BOUNCED_CHEQUE);
            inputParams.put("courtJudgement", COURT_JUDGEMENT);
            inputParams.put("validUtil", NON_FINANCIAL_DEFAULT_AMOUNT);
            inputParams.put("tenor", TENOR);
            inputParams.put("validDefaults", FINANCIAL_DEFAULT_AMOUNT);
            inputParams.put("validInternalDti", INTERNAL_DTI);
            inputParams.put("currDelinquencyT", CURRENT_DELINQUENCY_T);
            inputParams.put("employeeName", EMPLOYER_NAME);
            inputParams.put("loanAmountCap", MAX_LOAN_AMOUNT_CAPPING);
            inputParams.put("liftSalary", MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY);
            inputParams.put("nationality", NATIONALITY);
            inputParams.put("newIndustry", NEW_TO_INDUSTRY);
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
            DATA_TYPE = request.getParameter("Product");
            CONTACT_NUMBER = request.getParameter("Mobile");
            this.inputParams.put("ENQUIRY_REFERENCE", String.valueOf(generateRandomInt()));
            this.inputParams.put("employerTypeId", EMPLOYER_TYPE_ID);

            LOG.error("natid====>>" + request.getParameter("NationalID"));

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
            filter.put(DBPUtilitiesConstants.FILTER, "applicationID" + DBPUtilitiesConstants.EQUAL + APPLICATION_ID);

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
            INSIDE_KSA = HelperMethods.getFieldValue(getCustomerApplicationData, "insideKsa");
            SCORECARD_ID = HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId");
            LOAN_AMOUNT = HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount");
            TENOR = HelperMethods.getFieldValue(getCustomerApplicationData, "tenor");
            CUSTOMER_ID = HelperMethods.getFieldValue(getCustomerApplicationData, "Customer_id");

        } catch (Exception ex) {
            LOG.error("ERROR extractValuesFromCustomerApplication :: " + ex);
        }
    }

    private Result getCustomerData(DataControllerRequest dataControllerRequest) {
        Result result = StatusEnum.error.setStatus();
        try {
            Result getCustomerData = StatusEnum.success.setStatus();
            Map<String, String> filter = new HashMap<>();
            filter.put(DBPUtilitiesConstants.FILTER, "id" + DBPUtilitiesConstants.EQUAL + CUSTOMER_ID);
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

    private Result getNationalAddress(Map<String, String> inputParams, DataControllerRequest
            dataControllerRequest) {
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

    private Result calculateScoreCardS2(Map<String, String> inputParams, DataControllerRequest
            dataControllerRequest) {
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

    private Result calculateScoreCardS3(Map<String, String> inputParams, DataControllerRequest
            dataControllerRequest) {
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

    private void createCustomerAddress(Map<String, String> inputParams, DataControllerRequest
            dataControllerRequest) {
        try {
            ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, CUSTOMER_ADDRESS_CREATE_OPERATION_ID, inputParams, null,
                    dataControllerRequest);
        } catch (Exception ex) {
            LOG.error("ERROR createCustomerAddress :: " + ex);
        }
    }

    private Result updateCustomerApplicationData(Map<String, String> inputParams,
                                                 DataControllerRequest dataControllerRequest, boolean isS2) {
        Result updateCustomerApplicationData = StatusEnum.success.setStatus();
        updateCustomerApplicationData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
                CUSTOMER_APPLICATION_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest));

        if (inputParams.get("knockoutStatus").equalsIgnoreCase("FAIL") || inputParams.get("applicationStatus").equalsIgnoreCase("SID_SUSPENDED")) {
            updateCustomerApplicationData = StatusEnum.error.setStatus();
            IjarahErrors.ERR_660028.setErrorCode(updateCustomerApplicationData);
        }
        return updateCustomerApplicationData;
    }
}