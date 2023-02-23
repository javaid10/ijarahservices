package com.ijarah.services;

import com.google.common.primitives.Chars;
import com.google.gson.Gson;

import com.ijarah.Model.EMPLOYER_NAME_FOR_PENSIONERS.EmployerNameForPensionerResponse;
import com.ijarah.Model.EMPLOYER_NAME_FOR_PENSIONERS.EmployernamesforpensionerItem;
import com.ijarah.Model.MORTGAGE_PRODUCT.MortgageProductResponse;
import com.ijarah.Model.MORTGAGE_PRODUCT.MortgageproductItem;
import com.ijarah.Model.ScorecardS2.ScoreCardS2;
import com.ijarah.Model.ScorecardS3.ScoreCardS3;
import com.ijarah.Model.consumerEnquiryModel.*;
import com.ijarah.Model.consumerEnquiryModel.RESPONSEItem;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.ijarah.Model.consumerEnquiryModelFinal.ConsumerEnquiryModelResponse;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.ijarah.utils.IjarahHelperMethods.*;
import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.*;
import static com.ijarah.utils.constants.OperationIDConstants.SP_INCREMENT_SANAD_SIGN_COUNT_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.*;

public class ScoringEngine implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(ScoringEngine.class);
	Map<String, String> inputParams = new HashMap<>();
	String MONTHLY_NET_SALARY = "0";
	String CURRENT_LENGTH_OF_SERVICE = "0";
	String GLOBAL_DTI = "";
	String INTERNAL_DTI = "";
	String CURRENT_DELINQUENCY = "1";
	private String CURRENT_DELINQUENCY_T = "1";
	String MAX_DELINQUENCY = "0";
	String NON_FINANCIAL_DEFAULT_AMOUNT = "0";
	String BOUNCED_CHEQUE = "NB";
	String COURT_JUDGEMENT = "NJ";
	String EMPLOYER_CATEGORISATION = "NULL";
	String MAX_LOAN_AMOUNT_CAPPING = "";
	String MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY = "0";
	String NEW_TO_INDUSTRY = "";
	String SALARY_WITHOUT_ALLOWANCES = "0";
	String INSIDE_KSA = "";
	String NATIONALITY = "SA";
	String AMOUNT_OFFER = "";

	String EMPLOYER_TYPE_ID = "1";
	String EMPLOYMENT_STATUS = "";

	String[] MORTGAGE_PRODUCT;
	String[] CREDIT_CARD_PRODUCT = { "CDC", "CHC", "CRC", "LCRC" };
	String[] EMPLOYER_NAME_FOR_PENSIONERS;
	String[] NON_FINANCIAL_PRODUCTS = { "MBL", "LND", "DAT", "NET" };
	char[] CURRENT_DELINQUENCY_VALUES = { '0', 'C', 'D', 'N' };

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
	private List<CIDETAILItem> CI_DETAIL = null;
	private List<DEFAULTItem> DEFAULT = null;
	private List<BOUNCEDCHECKItem> BOUNCED_CHECK = null;
	private List<JUDGEMENTItem> JUDGEMENT = null;
	private String CUSTOMER_ID = "";

	private double MAX_EMI = 0;
	private double CUSTOMER_GLOBAL_DTI = 0.0;
	private double CUSTOMER_INTERNAL_DTI = 0.0;
	private String PARTY_ID = "";

	// Rajath Params for schedule and simulation

	private String EMI = "";
	private String SAAD = "";
	private String SABB = "";
	private String SIMID = "";
	private String AAID = "";
	private String SC_SCORE = "0";
	private List<com.ijarah.Model.consumerEnquiryModelFinal.CIDETAILSItem> CI_DETAIL2 = null;
	private List<com.ijarah.Model.consumerEnquiryModelFinal.DEFAULTSItem> DEFAULT2 = null;
	private List<com.ijarah.Model.consumerEnquiryModelFinal.BOUNCEDCHECKSItem> BOUNCED_CHEQUE2 = null;
	private List<com.ijarah.Model.consumerEnquiryModelFinal.JUDGEMENTSItem> JUDGEMENT2 = null;

	private StringBuilder BOUNCED_CHEQUE_DETAILS;
	private StringBuilder COURT_JUDGEMENT_DETAILS;
	private StringBuilder PRODUCT_DETAILS;
	private StringBuilder SUMMARY_DETAILS;
	private StringBuilder ALLOWANCE_DETAILS;
	private StringBuilder INSTALLMENT_AMOUNT;
	private StringBuilder LIMIT_AMOUNT;
	private StringBuilder CI_SUMMARY;

	@Override
	public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
			DataControllerResponse dataControllerResponse) throws Exception {
		Result result = StatusEnum.error.setStatus();

		// assigning values to parameters

		BOUNCED_CHEQUE_DETAILS = new StringBuilder();
		COURT_JUDGEMENT_DETAILS = new StringBuilder();
		PRODUCT_DETAILS = new StringBuilder();
		SUMMARY_DETAILS = new StringBuilder();
		ALLOWANCE_DETAILS = new StringBuilder();
		INSTALLMENT_AMOUNT = new StringBuilder();
		LIMIT_AMOUNT = new StringBuilder();
		CI_SUMMARY = new StringBuilder();
		MONTHLY_NET_SALARY = "0";
		SALARY_WITHOUT_ALLOWANCES = "0";
		DOB = "";

		try {
			EMPLOYER_CATEGORISATION = "";
			inputParams = HelperMethods.getInputParamMap(objects);
			IjarahErrors.ERR_PREPROCESS_INVALID_INPUT_PARAMS_001.setErrorCode(result);

			if (preProcess(dataControllerRequest)) {
				LOG.error("preProcess  :: ");
				// DB INTEGRATION SERVICES CALLS
				Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);

				Result getCustomerData = getCustomerData(dataControllerRequest, getCustomerApplicationData);

				// 3RD PARTY INTEGRATION SERVICES CALLS

				EMPLOYER_TYPE_ID = "1";
				Result getSalaryCertificate = getSIMAHSalaryCertificate(createRequestForSIMAHSALARY(getCustomerData,
						EMPLOYER_TYPE_ID, dataControllerRequest.getParameter("NationalID")), dataControllerRequest);
				if (IjarahHelperMethods.isBlank(getSalaryCertificate.getParamValueByName("payMonth"))) {
					LOG.error("PRIVATE EMPLOYEE");
					EMPLOYER_TYPE_ID = "3";
					EMPLOYER_CATEGORISATION = "P";
					getSalaryCertificate = getSIMAHSalaryCertificate(createRequestForSIMAHSALARY(getCustomerData,
							EMPLOYER_TYPE_ID, dataControllerRequest.getParameter("NationalID")), dataControllerRequest);
				} else {
					LOG.error("GOVT EMPLOYEE");
					EMPLOYER_TYPE_ID = "1";
					EMPLOYER_CATEGORISATION = "G";
				}

				if (Integer.parseInt(getSalaryCertificate.getParamValueByName("opstatus_getToken")) != 0
						|| !Boolean.parseBoolean(getSalaryCertificate.getParamValueByName("isSuccess"))) {
					LOG.error("Simah Failed to fetch Salary  :: ");
					result = updateCustomerApplicationData(
							createRequestForUpdateCustomerApplicationDataServiceS2(getCustomerApplicationData,
									dataControllerRequest),
							dataControllerRequest, "SIMAH", "Simah Failed to Fetch Salary");
					return result;
				}
				// CALCULATION OF SCORING ENGINES
				initEmployerNamesForPensionerArray(dataControllerRequest);
				calculatePensioner(getSalaryCertificate);
				calculateCurrentLengthOfService(getSalaryCertificate);
				getEmployerName(getSalaryCertificate);
				calculateManagingSeasonalAndTemporaryLiftInSalary(getSalaryCertificate);
				boolean checkSalaryBool = calculateMonthlyNetSalary(getSalaryCertificate);
				LOG.error("checkSalaryBool :: " + checkSalaryBool);
				if (!checkSalaryBool) {
					LOG.error("Failed due to User Non Active :: ");
					result = updateCustomerApplicationData(
							createRequestForUpdateCustomerApplicationDataServiceS2(getCustomerApplicationData,
									dataControllerRequest),
							dataControllerRequest, "Salary Certificate", "Failed Due To User Non-Active");
					return result;
				}

				calculateSalaryWithoutAllowances(getSalaryCertificate);

				Result getNationalAddress = getNationalAddress(inputParams, dataControllerRequest);

				createCustomerAddress(
						createRequestForCreateCustomerAddressService(getNationalAddress, dataControllerRequest),
						dataControllerRequest);
				createT24CustomerAddressUpdate(createRequestForT24CustomerAddressUpdateService(getNationalAddress),
						dataControllerRequest);

				createEmployerDetails(
						createRequestForCreateEmployerDetailsService(getSalaryCertificate, dataControllerRequest),
						dataControllerRequest);
				createT24CustomerEmployeeDetails(
						createRequestForT24CustomerEmployeeDetailsService(getSalaryCertificate), dataControllerRequest);

				// 3RD PARTY INTEGRATION SERVICES CALLS
				Result getScoreCardS2 = calculateScoreCardS2(createRequestForScoreCardS2Service(getSalaryCertificate,
						dataControllerRequest, getCustomerApplicationData), dataControllerRequest);
				Gson gsonS2 = new Gson();
				ScoreCardS2 scoreCardS2 = gsonS2.fromJson(ResultToJSON.convert(getScoreCardS2), ScoreCardS2.class);

				if (scoreCardS2.getBody() != null) {
					if (scoreCardS2.getBody().getApplicationCategory() != null) {
						if (scoreCardS2.getBody().getApplicationCategory().equalsIgnoreCase("0")) {
							// DB INTEGRATION SERVICES CALLS
							result = updateCustomerApplicationData(
									createRequestForUpdateCustomerApplicationDataServiceS2(getCustomerApplicationData,
											dataControllerRequest),
									dataControllerRequest, "S2", "Score Card S2 Failed");
							saveMISReportData(createRequestForMISReportDBCallSCorecard2(dataControllerRequest, "FAIL", scoreCardS2.getBody().getEmployeeCAtegory()),
									dataControllerRequest);
							return result;

						} else {
							saveMISReportData(createRequestForMISReportDBCallSCorecard2(dataControllerRequest, "PASS", scoreCardS2.getBody().getEmployeeCAtegory()),
									dataControllerRequest);
						}
					}
				}

				Result getConsumerEnquiry = getSIMAHConsumerEnquiry(
						createRequestForConsumerEnquiryService(inputParams, getCustomerData, getSalaryCertificate,
								dataControllerRequest, getCustomerApplicationData, getNationalAddress),
						dataControllerRequest);

				// Check if consumer has failed with 404 request
				String jsonObject = ResultToJSON.convert(getConsumerEnquiry);

				JSONObject consumerEnquiryObj = new JSONObject(jsonObject);
				String checkConsumerResp = "";
				checkConsumerResp = consumerEnquiryObj.optString("errmsg");
				LOG.error("ERROR BEfore consumerEnquiryObj :: " + checkConsumerResp);
				if (checkConsumerResp != "") {
					LOG.error("ERROR After consumerEnquiryObj :: " + checkConsumerResp);
					result = updateCustomerApplicationData(
							createRequestForUpdateCustomerApplicationDataServiceS2(getCustomerApplicationData,
									dataControllerRequest),
							dataControllerRequest, "Consumer Enquiry", "Consumer Enquiry 404 Request");
					return result;
				}

				// check if consumer enquiry is failed with some wrong parameters
				// Gson gson = new Gson();
				// ConsumerEnquiryModelResponse consumerEnquiryModelResponse = gson
				// .fromJson(ResultToJSON.convert(getConsumerEnquiry),
				// ConsumerEnquiryModelResponse.class);

				// String checkErrorResp =
				// consumerEnquiryModelResponse.getRESPONSE().getSTATUS();

				// LOG.error("ERROR BEfore checkErrorItem :: " + checkErrorResp);
				// if (!checkErrorResp.equalsIgnoreCase("OK")) {
				// LOG.error("ERROR After checkErrorItem :: " + checkErrorResp);
				// result = updateCustomerApplicationData(
				// createRequestForUpdateCustomerApplicationDataServiceS2(getCustomerApplicationData),
				// dataControllerRequest);
				// return result;
				// }

				extractValuesFromConsumerEnquiryResponse(getConsumerEnquiry);
				initMortgageProductArray(dataControllerRequest);

				calculateCurrentDelinquencyAndCurrentDelinquencyT();
				calculateMaxDelinquency();

				calculateMaxLoanAmountCapping();
				calculateNewToIndustry();

				int calMAXGblDti = calculateMaxGlobalDTI(dataControllerRequest);
				int calMAXinternalDti = calculateMaxInternalDTI(dataControllerRequest);

				double customerInternalDti = calculateInternalDTI(calMAXinternalDti);

				JSONObject checkDTIBool = calculateGlobalDTI(calMAXGblDti);
				double customerGblDti = checkDTIBool.optDouble("customerGblDti");

				double maxEMI = calculateMaxEmi(calMAXGblDti, calMAXinternalDti, customerGblDti, customerInternalDti);

				LOG.error("check customerInternalDti DTI :: " + customerInternalDti);
				LOG.error("checkDTIBool DTI :: " + checkDTIBool);
				if (!checkDTIBool.optBoolean("check")) {
					LOG.error("Failed due to higher DTI :: ");
					result = updateCustomerApplicationData(
							createRequestForUpdateCustomerApplicationDataServiceS2(getCustomerApplicationData,
									dataControllerRequest),
							dataControllerRequest, "DTI Calculation", "Failed due to higher DTI");

					saveMISReportData(createRequestForMISReportDBCall(null, maxEMI, customerGblDti,
							customerInternalDti, calMAXGblDti, calMAXinternalDti, dataControllerRequest),
							dataControllerRequest);

					return result;
				}

				Result getScoreCardS3 = calculateScoreCardS3(createRequestForScoreCardS3Service(getConsumerEnquiry,
						getCustomerApplicationData, getCustomerData, dataControllerRequest), dataControllerRequest);

				// DB INTEGRATION SERVICES CALLS
				result = updateCustomerApplicationData(
						createRequestForUpdateCustomerApplicationDataService(getCustomerApplicationData, getScoreCardS2,
								getScoreCardS3, dataControllerRequest, maxEMI, calMAXGblDti, calMAXinternalDti,
								customerGblDti, customerInternalDti),
						dataControllerRequest, "S3", "Application Declined");
			}

			return result;
		} catch (Exception ex) {
			LOG.error("ERROR invoke :: " + ex.getMessage());
			return result;
		}
	}

	private void initMortgageProductArray(DataControllerRequest dataControllerRequest) {
		Result getMortgageProductsResponse = getMortgageProducts(dataControllerRequest);
		Gson gson = new Gson();
		MortgageProductResponse mortgageProductResponse = gson
				.fromJson(ResultToJSON.convert(getMortgageProductsResponse), MortgageProductResponse.class);
		extractValuesFromMortgageProductResponse(mortgageProductResponse);
	}

	private void extractValuesFromMortgageProductResponse(MortgageProductResponse mortgageProductResponse) {
		List<MortgageproductItem> mortgageProductList = mortgageProductResponse.getMortgageproduct();
		MORTGAGE_PRODUCT = new String[mortgageProductList.size()];
		int index = 0;
		for (MortgageproductItem mortgageproductItem : mortgageProductList) {
			MORTGAGE_PRODUCT[index] = mortgageproductItem.getMortgageProductValue();
			index++;
		}

		LOG.error("MortgageProduct Array:: " + MORTGAGE_PRODUCT);
	}

	private Result getMortgageProducts(DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Map<String, String> inputParams = new HashMap<>();
			return ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, MORTGAGE_PRODUCT_GET_OPERATION_ID, inputParams,
					null, dataControllerRequest);
		} catch (Exception ex) {
			LOG.error("ERROR getCustomerApplicationData :: " + ex);
		}
		return result;
	}

	private void initEmployerNamesForPensionerArray(DataControllerRequest dataControllerRequest) {
		Result getEmployerNameForPensionersResponse = getEmployerNameForPensioners(dataControllerRequest);
		Gson gson = new Gson();
		EmployerNameForPensionerResponse employerNameForPensionerResponse = gson.fromJson(
				ResultToJSON.convert(getEmployerNameForPensionersResponse), EmployerNameForPensionerResponse.class);
		extractValuesFromEmployerNamesForPensionerResponse(employerNameForPensionerResponse);
	}

	private void extractValuesFromEmployerNamesForPensionerResponse(
			EmployerNameForPensionerResponse employerNameForPensionerResponse) {
		List<EmployernamesforpensionerItem> employerNamesForPensionerList = employerNameForPensionerResponse
				.getEmployernamesforpensioner();
		EMPLOYER_NAME_FOR_PENSIONERS = new String[employerNamesForPensionerList.size()];
		int index = 0;
		for (EmployernamesforpensionerItem employernamesforpensionerItem : employerNamesForPensionerList) {
			EMPLOYER_NAME_FOR_PENSIONERS[index] = employernamesforpensionerItem.getEmployerNamesForPensionersValue();
			index++;
		}
	}

	private Result getEmployerNameForPensioners(DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Map<String, String> inputParams = new HashMap<>();
			return ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, EMPLOYER_NAMES_FOR_PENSIONERS_GET_OPERATION_ID,
					inputParams, null, dataControllerRequest);
		} catch (Exception ex) {
			LOG.error("ERROR getEmployerNameForPensioners :: " + ex);
		}
		return result;
	}

	private Result createT24CustomerAddressUpdate(Map<String, String> inputParams,
			DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Result T24CustomerAddress = ServiceCaller.internal(MORA_T24_SERVICE_ID,
					T24_CUSTOMER_ADDRESS_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest);
			String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
			String outputResponse = ResultToJSON.convert(T24CustomerAddress);
			auditLogData(dataControllerRequest, inputRequest, outputResponse,
					MORA_T24_SERVICE_ID + " : " + T24_CUSTOMER_ADDRESS_UPDATE_OPERATION_ID);
			StatusEnum.success.setStatus(T24CustomerAddress);
			return T24CustomerAddress;
		} catch (Exception ex) {
			LOG.error("ERROR createT24CustomerAddressUpdate :: ", ex);
		}
		return result;
	}

	private Result createT24CustomerEmployeeDetails(Map<String, String> inputParams,
			DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Result T24CustomerEmployeeDetails = ServiceCaller.internal(MORA_T24_SERVICE_ID,
					CUSTOMER_EMPLOYEE_DETAILS_OPERATION_ID, inputParams, null, dataControllerRequest);

			String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
			String outputResponse = ResultToJSON.convert(T24CustomerEmployeeDetails);
			auditLogData(dataControllerRequest, inputRequest, outputResponse,
					MORA_T24_SERVICE_ID + " : " + CUSTOMER_EMPLOYEE_DETAILS_OPERATION_ID);

			StatusEnum.success.setStatus(T24CustomerEmployeeDetails);
			return T24CustomerEmployeeDetails;
		} catch (Exception ex) {
			LOG.error("ERROR createT24CustomerEmployeeDetails :: ", ex);
		}
		return result;
	}

	private String getLoanSimulation(HashMap<String, Object> inputParams) {
		String schedRes = "";
		try {
			String res = DBPServiceExecutorBuilder.builder().withServiceId("LoanSimulationSchedulePayment")
					.withOperationId("LoanSimulation").withRequestParameters(inputParams).build().getResponse();
			LOG.error("====:::::  Response from SIMULATION  :::::" + res);
			JSONObject JsonResponse = new JSONObject(res);
			String simid = JsonResponse.optString("simulationId");
			String aaid = JsonResponse.optString("arrangementId");

			LOG.error("AAID");

			if (!simid.isEmpty() && !aaid.isEmpty()) {
				Thread.sleep(Long.parseLong("20000"));
				LOG.error("====::::: Inside if condition  :::::");

				HashMap<String, Object> schedParam = new HashMap();
				schedParam.put("simulationId", simid);
				schedParam.put("arrangementId", aaid);

				schedRes = DBPServiceExecutorBuilder.builder().withServiceId("LoanSimulationSchedulePayment")
						.withOperationId("PaymentScheduleOrch").withRequestParameters(schedParam).build().getResponse();
				JSONObject jsonSchedule = new JSONObject(schedRes);

				EMI = jsonSchedule.getJSONArray("body").getJSONObject(1).optString("totalAmount");

				SAAD = jsonSchedule.getJSONArray("body").getJSONObject(0).optString("sadadNumber");

				SABB = jsonSchedule.getJSONArray("body").getJSONObject(0).optString("sabbNumber");
				AAID = aaid;
				SIMID = simid;
				LOG.error("====:::::  Response from Schedule  :::::" + schedRes);
				LOG.error("Sabb number ======" + SAAD);
				LOG.error("Sabb number ======" + EMI);
				LOG.error("Sabb number ======" + SABB);

			}

		} catch (Exception ex) {
			LOG.error("ERROR getLoanSimulation :: ", ex);
		}
		return schedRes;
	}

	private Map<String, String> createRequestForT24CustomerAddressUpdateService(Result getNationalAddress) {
		Map<String, String> inputParams = new HashMap<>();
		inputParams.put("partyId", PARTY_ID);
		inputParams.put("country", "SA");
		String streetVal = "";
		String streetEnMfb = "NONE";
		String districtName = "";

		JSONObject resObj = new JSONObject(ResultToJSON.convert(getNationalAddress));
		// for PT testing
		JSONObject mainObj = resObj.optJSONObject("CitizenAddressInfoResult");
		LOG.error("mainObj Response " + mainObj);

		if (mainObj.opt("addressListList") instanceof JSONObject) {

			if (mainObj.opt("addressListList") != null) {
				LOG.error("addressListList single address ");
				try {
					streetVal = mainObj.optJSONObject("addressListList").optString("streetName");
					districtName = mainObj.optJSONObject("addressListList").optString("district");
					String upToNCharacters = streetVal.substring(0, Math.min(streetVal.length(), 35));

					if (streetVal.isBlank() || streetVal.isEmpty() || streetVal == null) {
						streetVal = "NONE";
						upToNCharacters = "NONE";
					}
					if (districtName.isBlank() || districtName.isEmpty() || districtName == null) {
						districtName = "NONE";
					}

					inputParams.put("street", upToNCharacters);
					inputParams.put("StreetEnMfb", streetVal);
					inputParams.put("buildingNumber",
							mainObj.optJSONObject("addressListList").optString("buildingNumber"));
					inputParams.put("flatNumber", mainObj.optJSONObject("addressListList").optString("unitNumber"));
					inputParams.put("districtName", districtName);
					inputParams.put("addressCity", mainObj.optJSONObject("addressListList").optString("city"));
					inputParams.put("postCode", mainObj.optJSONObject("addressListList").optString("postCode"));

				} catch (Exception e) {
					LOG.error("Single Address Exception =" + e.getMessage());
				}

			} else {
				LOG.error("addressListList empty single address ");
				inputParams.put("street", "street");
				inputParams.put("StreetEnMfb", "StreetEnMfb");
				inputParams.put("buildingNumber", "buildingNumber");
				inputParams.put("flatNumber", "flatNumber");
				inputParams.put("districtName", "districtName");
				inputParams.put("addressCity", "addressCity");
				inputParams.put("postCode", "postCode");
			}

			LOG.error("createRequestForT24CustomerAddressUpdateService Single Address " + inputParams);
		} else {

			if (mainObj.opt("addressListList") != null) {
				LOG.error("addressListList mulitple address ");
				try {

					streetVal = mainObj.optJSONArray("addressListList").getJSONObject(0).optString("streetName");
					districtName = mainObj.optJSONArray("addressListList").getJSONObject(0).optString("district");

					String upToNCharacters = streetVal.substring(0, Math.min(streetVal.length(), 35));

					if (streetVal.isBlank() || streetVal.isEmpty() || streetVal == null) {
						streetVal = "NONE";
						upToNCharacters = "NONE";
					}

					if (districtName.isBlank() || districtName.isEmpty() || districtName == null) {
						districtName = "NONE";
					}

					inputParams.put("street", upToNCharacters);
					inputParams.put("StreetEnMfb", streetVal);
					inputParams.put("districtName", districtName);
					inputParams.put("flatNumber",
							mainObj.optJSONArray("addressListList").getJSONObject(0).optString("unitNumber"));
					inputParams.put("buildingNumber",
							mainObj.optJSONArray("addressListList").getJSONObject(0).optString("buildingNumber"));
					inputParams.put("postCode",
							mainObj.optJSONArray("addressListList").getJSONObject(0).optString("postCode"));
					inputParams.put("addressCity",
							mainObj.optJSONArray("addressListList").getJSONObject(0).optString("city"));
				} catch (Exception e) {
					LOG.error("Multiple Address Exception =" + e.getMessage());
				}

			} else {
				LOG.error("addressListList empty multiple address ");
				inputParams.put("street", "street");
				inputParams.put("StreetEnMfb", "StreetEnMfb");
				inputParams.put("buildingNumber", "buildingNumber");
				inputParams.put("flatNumber", "flatNumber");
				inputParams.put("districtName", "districtName");
				inputParams.put("addressCity", "addressCity");
				inputParams.put("postCode", "postCode");
			}
			LOG.error("createRequestForCreateCustomerAddressService Multiple Address " + inputParams);
		}

		/*
		 * HashMap<String, Object> map = new
		 * Gson().fromJson(ResultToJSON.convert(getNationalAddress), new
		 * TypeToken<HashMap<String, Object>>() { }.getType()); String addressListList =
		 * (String) map.get("addressListList"); Gson gson = new Gson(); if
		 * (addressListList.charAt(0) == '{') { NationalAddress nationalAddress =
		 * gson.fromJson(ResultToJSON.convert(getNationalAddress),
		 * NationalAddress.class); if (nationalAddress != null &&
		 * nationalAddress.getCitizenAddressInfoResult() != null &&
		 * nationalAddress.getCitizenAddressInfoResult().getAddressListList() != null) {
		 * AddressListList addressList =
		 * nationalAddress.getCitizenAddressInfoResult().getAddressListList();
		 * inputParams.put("street", addressList.getStreetName());
		 * inputParams.put("address", addressList.getDistrict() + " " +
		 * addressList.getUnitNumber()); inputParams.put("addressCity",
		 * addressList.getCity()); } else { inputParams.put("street", "-");
		 * inputParams.put("address", "-"); inputParams.put("addressCity", "-"); } }
		 * else { NationalAddressList nationalAddress =
		 * gson.fromJson(ResultToJSON.convert(getNationalAddress),
		 * NationalAddressList.class); if (nationalAddress != null &&
		 * nationalAddress.getCitizenAddressInfoResult() != null &&
		 * nationalAddress.getCitizenAddressInfoResult().getAddressListList() != null) {
		 * AddressListListItem addressList =
		 * nationalAddress.getCitizenAddressInfoResult().getAddressListList() .get(0);
		 * inputParams.put("street", addressList.getStreetName());
		 * inputParams.put("address", addressList.getDistrict() + " " +
		 * addressList.getUnitNumber()); inputParams.put("addressCity",
		 * addressList.getCity()); } else { inputParams.put("street", "-");
		 * inputParams.put("address", "-"); inputParams.put("addressCity", "-"); } }
		 */
		return inputParams;
	}

	private Map<String, String> createRequestForT24CustomerEmployeeDetailsService(Result getSalaryCertificate) {

		Map<String, String> inputParams = new HashMap<>();

		inputParams.put("partyId", PARTY_ID);

		LOG.error("ID =====>>>" + EMPLOYER_TYPE_ID);
		switch (EMPLOYER_TYPE_ID) {
		case "1":

			String basic = getSalaryCertificate.getParamValueByName("basicSalary");
			String allowence = getSalaryCertificate.getParamValueByName("totalAllownces");
			String deduc = getSalaryCertificate.getParamValueByName("totalDeductions");

			String netsalary = String
					.valueOf(Double.parseDouble(basic) + Double.parseDouble(allowence) - Double.parseDouble(deduc));

			if (!getSalaryCertificate.getParamValueByName("agencyName").isEmpty()) {
				inputParams.put("employerName", getSalaryCertificate.getParamValueByName("agencyName"));
				inputParams.put("lEmpName", getSalaryCertificate.getParamValueByName("agencyName"));

			} else {
				inputParams.put("employerName", "Employer Name");
				inputParams.put("lEmpName", "Employer Name");
			}
			inputParams.put("employStatus", "EMPLOYED");
			// inputParams.put("jobTitleMfb",
			// getSalaryCertificate.getParamValueByName("employeeJobTitle"));
			// inputParams.put("employerName",
			// getSalaryCertificate.getParamValueByName("agencyName"));
			inputParams.put("salaryCurrency", "SAR");
			inputParams.put("employStartDate", getSalaryCertificate.getParamValueByName("agencyEmploymentDate"));
			inputParams.put("salaryMfb", netsalary);
			inputParams.put("basicWageMfb", basic);
			inputParams.put("houseAllowMfb", "0");
			inputParams.put("othAllowMfb", allowence);

			break;
		case "3":
			String pvtNetsalary = String
					.valueOf((Double.parseDouble(getSalaryCertificate.getParamValueByName("basicWage"))
							+ Double.parseDouble(getSalaryCertificate.getParamValueByName("housingAllowance"))
							+ Double.parseDouble(getSalaryCertificate.getParamValueByName("otherAllowance"))));

			pvtNetsalary = String.format("%.2f", Double.parseDouble(pvtNetsalary));
			// if (!getSalaryCertificate.getParamValueByName("employerName").isEmpty()) {
			LOG.error("EMPLOYEEE NAMEE=====>>>" + getSalaryCertificate.getParamValueByName("employerName"));

			inputParams.put("employerName", getSalaryCertificate.getParamValueByName("employerName"));
			inputParams.put("lEmpName", getSalaryCertificate.getParamValueByName("employerName"));
			inputParams.put("employStatus", "EMPLOYED");
			inputParams.put("salaryCurrency", "SAR");
			inputParams.put("houseAllowMfb", getSalaryCertificate.getParamValueByName("housingAllowance"));
			inputParams.put("othAllowMfb", getSalaryCertificate.getParamValueByName("otherAllowance"));
			// TODO Salary Pay Date should be YYYYMMDD M0128 for SALARY.DATE.FREQ

			LocalDate dateOfJoining = LocalDate.parse(getSalaryCertificate.getParamValueByName("dateOfJoining"),
					DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			inputParams.put("employStartDate", String.valueOf(dateOfJoining));
			inputParams.put("salaryMfb", pvtNetsalary);
			inputParams.put("basicWageMfb", getSalaryCertificate.getParamValueByName("basicWage"));
			break;
		}
		return inputParams;
	}

	private void extractValuesFromConsumerEnquiryResponse(Result getConsumerEnquiry) {
		ConsumerEnquiry consumerEnquiry = null;
		DEFAULT = null;
		CI_DETAIL = null;
		BOUNCED_CHECK = null;
		JUDGEMENT = null;
		SC_SCORE = null;

		Gson gson = new Gson();

		consumerEnquiry = gson.fromJson(ResultToJSON.convert(getConsumerEnquiry), ConsumerEnquiry.class);

		CONSUMERItem CONSUMER = null;
		CONSUMER = new CONSUMERItem();
		boolean isConsumer = false;
		if (consumerEnquiry.getDATA() != null && consumerEnquiry.getDATA().size() > 0) {
			LOG.error("ConsumerEnquiryModelResponse OLD");
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
					LOG.error("DEFAULT DATA :: " + DEFAULT.get(0));

				}
			}
		}
		if (isConsumer) {
			if (CONSUMER.getBOUNCEDCHECKS() != null && CONSUMER.getBOUNCEDCHECKS().size() > 0) {
				BOUNCEDCHECKSItem BOUNCED_CHECKS = CONSUMER.getBOUNCEDCHECKS().get(0);
				if (BOUNCED_CHECKS.getBOUNCEDCHECK() != null && BOUNCED_CHECKS.getBOUNCEDCHECK().size() > 0) {
					LOG.error("BOUNCED_CHECK DATA :: " + BOUNCED_CHECKS.getBOUNCEDCHECK().get(0).getBCSETTLDDATE());
					BOUNCED_CHECK = BOUNCED_CHECKS.getBOUNCEDCHECK();
				}
			}
		}
		if (isConsumer) {
			if (CONSUMER.getJUDGEMENTS() != null && CONSUMER.getJUDGEMENTS().size() > 0) {
				JUDGEMENTSItem JUDGEMENTS = CONSUMER.getJUDGEMENTS().get(0);
				if (JUDGEMENTS.getJUDGEMENT() != null && JUDGEMENTS.getJUDGEMENT().size() > 0) {
					LOG.error("JUDGEMENT DATA :: " + JUDGEMENTS.getJUDGEMENT().get(0).getEJSETTLEDATE());
					JUDGEMENT = JUDGEMENTS.getJUDGEMENT();
				}
			}
		}
		if (isConsumer) {
			if (CONSUMER.getSCORE() != null && CONSUMER.getSCORE().size() > 0) {
				SCOREItem SCORE = CONSUMER.getSCORE().get(0);
				if (SCORE.getSCSCORE() != null) {
					SC_SCORE = SCORE.getSCSCORE();
				}
			}
		} else {
			ConsumerEnquiryModelResponse consumerEnquiryModelResponse = null;
			CI_DETAIL2 = null;
			SC_SCORE = null;
			DEFAULT2 = null;
			BOUNCED_CHEQUE2 = null;
			JUDGEMENT2 = null;

			Gson gson2 = new Gson();

			consumerEnquiryModelResponse = gson2.fromJson(ResultToJSON.convert(getConsumerEnquiry),
					ConsumerEnquiryModelResponse.class);

			if (consumerEnquiryModelResponse != null) {
				LOG.error("ConsumerEnquiryModelResponse FINAL");
				if (consumerEnquiryModelResponse.getCIDETAILS() != null
						&& consumerEnquiryModelResponse.getCIDETAILS().size() > 0) {
					CI_DETAIL2 = consumerEnquiryModelResponse.getCIDETAILS();
				}
				if (consumerEnquiryModelResponse.getSCORE() != null) {
					if (consumerEnquiryModelResponse.getSCORE().getSCSCORE() != null) {
						SC_SCORE = consumerEnquiryModelResponse.getSCORE().getSCSCORE();
					}
				}
				if (consumerEnquiryModelResponse.getDEFAULTS() != null
						&& consumerEnquiryModelResponse.getDEFAULTS().size() > 0) {
					DEFAULT2 = consumerEnquiryModelResponse.getDEFAULTS();
				}

				if (consumerEnquiryModelResponse.getBOUNCEDCHECKS() != null
						&& consumerEnquiryModelResponse.getBOUNCEDCHECKS().size() > 0) {
					BOUNCED_CHEQUE2 = consumerEnquiryModelResponse.getBOUNCEDCHECKS();
				}
				if (consumerEnquiryModelResponse.getJUDGEMENTS() != null
						&& consumerEnquiryModelResponse.getJUDGEMENTS().size() > 0) {
					JUDGEMENT2 = consumerEnquiryModelResponse.getJUDGEMENTS();
				}

			}
		}
	}

	private Map<String, String> createRequestForCreateCustomerAddressService(Result getNationalAddress,
			DataControllerRequest request) {
		Map<String, String> inputParams = new HashMap<>();

		// String currentDateTime = getDate(LocalDateTime.now(),
		// DATE_FORMAT_WITH_SECONDS_MS);

		inputParams.put("Region_id", "SAU");

		inputParams.put("country", "SAU");
		inputParams.put("type", "home");
		inputParams.put("state", "SAU");
		inputParams.put("createdby", "Admin");
		inputParams.put("modifiedby", "Admin");
		inputParams.put("softdeleteflag", "0");
		inputParams.put("isPreferredAddress", "true");
		// inputParams.put("applicationID", APPLICATION_ID);
		// inputParams.put("User_id", NATIONAL_ID);
		inputParams.put("User_id", request.getParameter("NationalID"));
		inputParams.put("applicationID", request.getParameter("ApplicationID"));

		inputParams.put("id", generateUUID());

		JSONObject resObj = new JSONObject(ResultToJSON.convert(getNationalAddress));

		// for PT testing
		JSONObject mainObj = resObj.optJSONObject("CitizenAddressInfoResult");
		LOG.error("mainObj Response " + mainObj);

		if (mainObj.opt("addressListList") instanceof JSONObject) {
			inputParams.put("City_id", mainObj.optJSONObject("addressListList").optString("city"));
			inputParams.put("cityName", mainObj.optJSONObject("addressListList").optString("city"));
			inputParams.put("addressLine1", mainObj.optJSONObject("addressListList").optString("district"));
			inputParams.put("addressLine2", mainObj.optJSONObject("addressListList").optString("streetName"));
			inputParams.put("addressLine3",
					String.valueOf(mainObj.optJSONObject("addressListList").optString("buildingNumber")));
			inputParams.put("zipCode", String.valueOf(mainObj.optJSONObject("addressListList").optString("postCode")));
			inputParams.put("latitude",
					mainObj.optJSONObject("addressListList").optString("locationCoordinates").split(" ")[0].trim());
			inputParams.put("logitude",
					mainObj.optJSONObject("addressListList").optString("locationCoordinates").split(" ")[1].trim());

			LOG.error("createRequestForCreateCustomerAddressService Single Address " + inputParams);
		} else {
			inputParams.put("City_id", mainObj.optJSONArray("addressListList").getJSONObject(0).optString("city"));
			inputParams.put("cityName", mainObj.optJSONArray("addressListList").getJSONObject(0).optString("city"));
			inputParams.put("addressLine1",
					mainObj.optJSONArray("addressListList").getJSONObject(0).optString("district"));
			inputParams.put("addressLine2",
					mainObj.optJSONArray("addressListList").getJSONObject(0).optString("streetName"));
			inputParams.put("addressLine3", String
					.valueOf(mainObj.optJSONArray("addressListList").getJSONObject(0).optString("buildingNumber")));
			inputParams.put("zipCode",
					String.valueOf(mainObj.optJSONArray("addressListList").getJSONObject(0).optString("postCode")));
			inputParams.put("latitude", mainObj.optJSONArray("addressListList").getJSONObject(0)
					.optString("locationCoordinates").split(" ")[0].trim());
			inputParams.put("logitude", mainObj.optJSONArray("addressListList").getJSONObject(0)
					.optString("locationCoordinates").split(" ")[1].trim());

			LOG.error("createRequestForCreateCustomerAddressService Multiple Address " + inputParams);
		}

		/*
		 * HashMap<String, Object> map = new
		 * Gson().fromJson(ResultToJSON.convert(getNationalAddress), new
		 * TypeToken<HashMap<String, Object>>() { }.getType()); String addressListList =
		 * (String) map.get("addressListList");
		 *
		 * Gson gson = new Gson(); if (addressListList.charAt(0) == '{') {
		 * NationalAddress nationalAddress =
		 * gson.fromJson(ResultToJSON.convert(getNationalAddress),
		 * NationalAddress.class);
		 *
		 * if (nationalAddress != null && nationalAddress.getCitizenAddressInfoResult()
		 * != null && nationalAddress.getCitizenAddressInfoResult().getAddressListList()
		 * != null) {
		 *
		 * AddressListList addressList =
		 * nationalAddress.getCitizenAddressInfoResult().getAddressListList();
		 * inputParams.put("City_id", addressList.getCity());
		 * inputParams.put("cityName", addressList.getCity());
		 * inputParams.put("addressLine1", addressList.getDistrict());
		 * inputParams.put("addressLine2", addressList.getStreetName());
		 * inputParams.put("addressLine3", String.valueOf(addressList.getUnitNumber()));
		 * inputParams.put("zipCode", String.valueOf(addressList.getPostCode()));
		 * inputParams.put("latitude",
		 * (addressList.getLocationCoordinates().split(" ")[0]).trim());
		 * inputParams.put("logitude",
		 * (addressList.getLocationCoordinates().split(" ")[1]).trim()); } else {
		 * inputParams.put("City_id", "-"); inputParams.put("cityName", "-");
		 * inputParams.put("addressLine1", "-"); inputParams.put("addressLine2", "-");
		 * inputParams.put("addressLine3", "-"); inputParams.put("zipCode", "-");
		 * inputParams.put("latitude", "-"); inputParams.put("logitude", "-"); }
		 *
		 * } else { NationalAddressList nationalAddress =
		 * gson.fromJson(ResultToJSON.convert(getNationalAddress),
		 * NationalAddressList.class);
		 *
		 * if (nationalAddress != null &&
		 * nationalAddress.getCitizenAddressInfoResult().getAddressListList() != null &&
		 * nationalAddress.getCitizenAddressInfoResult().getAddressListList().size() >
		 * 0) {
		 *
		 * AddressListListItem addressList =
		 * nationalAddress.getCitizenAddressInfoResult().getAddressListList().get(0);
		 * inputParams.put("City_id", addressList.getCity());
		 * inputParams.put("cityName", addressList.getCity());
		 * inputParams.put("addressLine1", addressList.getDistrict());
		 * inputParams.put("addressLine2", addressList.getStreetName());
		 * inputParams.put("addressLine3", String.valueOf(addressList.getUnitNumber()));
		 * inputParams.put("zipCode", String.valueOf(addressList.getPostCode()));
		 * inputParams.put("latitude",
		 * (addressList.getLocationCoordinates().split(" ")[0]).trim());
		 * inputParams.put("logitude",
		 * (addressList.getLocationCoordinates().split(" ")[1]).trim()); } else {
		 * inputParams.put("City_id", "-"); inputParams.put("cityName", "-");
		 * inputParams.put("addressLine1", "-"); inputParams.put("addressLine2", "-");
		 * inputParams.put("addressLine3", "-"); inputParams.put("zipCode", "-");
		 * inputParams.put("latitude", "-"); inputParams.put("logitude", "-"); } }
		 */
		return inputParams;
	}

	// saif
	private Map<String, String> createRequestForCreateEmployerDetailsService(Result getSalaryCertificate,
			DataControllerRequest dataControllerRequest) {
		Map<String, String> inputParams = new HashMap<>();

		inputParams.put("id", generateUUID());
		inputParams.put("nationalid", dataControllerRequest.getParameter("NationalID"));
		inputParams.put("applicationID", dataControllerRequest.getParameter("ApplicationID"));

		switch (EMPLOYER_TYPE_ID) {
		case "1":
			String basic = getSalaryCertificate.getParamValueByName("basicSalary");
			String allowence = getSalaryCertificate.getParamValueByName("totalAllownces");
			String deduc = getSalaryCertificate.getParamValueByName("totalDeductions");

			String netsalary = String
					.valueOf(Double.parseDouble(basic) + Double.parseDouble(allowence) - Double.parseDouble(deduc));

			inputParams.put("agencycode", getSalaryCertificate.getParamValueByName("agencyCode"));
			inputParams.put("accountnumber", getSalaryCertificate.getParamValueByName("accountNumber"));
			inputParams.put("employeejobnumber", getSalaryCertificate.getParamValueByName("employeeJobNumber"));
			inputParams.put("agencyname", getSalaryCertificate.getParamValueByName("agencyName"));
			inputParams.put("govsalary", getSalaryCertificate.getParamValueByName("govSalary"));
			inputParams.put("agencyemploymentdate", getSalaryCertificate.getParamValueByName("agencyEmploymentDate"));
			inputParams.put("paymonth", getSalaryCertificate.getParamValueByName("payMonth"));
			inputParams.put("employeenamear", getSalaryCertificate.getParamValueByName("employeeNameAr"));
			inputParams.put("totalallownces", getSalaryCertificate.getParamValueByName("totalAllownces"));
			inputParams.put("basicsalary", getSalaryCertificate.getParamValueByName("basicSalary"));
			inputParams.put("netsalary", MONTHLY_NET_SALARY);
			inputParams.put("employeenameen", getSalaryCertificate.getParamValueByName("employeeNameEn"));
			inputParams.put("employeejobtitle", getSalaryCertificate.getParamValueByName("employeeJobTitle"));
			break;
		case "3":
			inputParams.put("agencycode", "agencycodeValue");
			inputParams.put("accountnumber", "accountnumberValue");
			inputParams.put("employeejobnumber", "employeejobnumberValue");
			inputParams.put("agencyname", getSalaryCertificate.getParamValueByName("employerName"));
			inputParams.put("govsalary", getSalaryCertificate.getParamValueByName("basicWage"));
			inputParams.put("agencyemploymentdate", getSalaryCertificate.getParamValueByName("dateOfJoining"));
			inputParams.put("paymonth", "paymonthValue");
			inputParams.put("employeenamear", "employeenamearValue");
			inputParams.put("totalallownces", getSalaryCertificate.getParamValueByName("otherAllowance"));
			inputParams.put("basicsalary", getSalaryCertificate.getParamValueByName("basicWage"));
			inputParams.put("netsalary", MONTHLY_NET_SALARY);
			inputParams.put("employeenameen", getSalaryCertificate.getParamValueByName("fullName"));
			inputParams.put("employeejobtitle", "employeejobtitleValue");
			break;
		}
		return inputParams;
	}

	private Map<String, String> createRequestForSIMAHSALARY(Result getCustomerData, String empId, String nanId) {
		Map<String, String> inputParams = new HashMap<>();
		try {
			LOG.error("DOB from DB :: " + HelperMethods.getFieldValue(getCustomerData, "DateOfBirth"));
			LOG.error("DOB from After formatting DB :: "
					+ dateFormatter(HelperMethods.getFieldValue(getCustomerData, "DateOfBirth"), "dd/MM/yyyy"));
			inputParams.put("employerTypeId", empId);
			inputParams.put("dateOfBirth",
					dateFormatter(HelperMethods.getFieldValue(getCustomerData, "DateOfBirth"), "dd/MM/yyyy"));
			DOB = dateFormatter(HelperMethods.getFieldValue(getCustomerData, "DateOfBirth"), "dd-MM-yyyy").substring(3);
			inputParams.put("idNumber", nanId);
		} catch (Exception ex) {
			LOG.error("ERROR createRequestForSIMAHSALARY :: " + ex);
		}
		LOG.error("createRequestForSIMAHSALARY input param " + inputParams);
		return inputParams;
	}

	private static String dateFormatter(String dateN, String pattern) {
		String convertDate = "";
		String dateFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		try {
			Date dt = sdf.parse(dateN);
			SimpleDateFormat sdf2 = new SimpleDateFormat(pattern);
			convertDate = sdf2.format(dt);
		} catch (Exception e) {
			LOG.error("Date formatter in exception = " + e.getMessage());
		}
		return convertDate;
	}

	private Map<String, String> createRequestForConsumerEnquiryService(Map<String, String> globalInputParams,
			Result getCustomerData, Result getSalaryCertificate, DataControllerRequest request,
			Result getCustomerApplicationData, Result getNationalAddress) {
		Map<String, String> inputParams = new HashMap<>();
		try {

			JSONObject resObj = new JSONObject(ResultToJSON.convert(getNationalAddress));
			JSONObject mainObj = resObj.optJSONObject("CitizenAddressInfoResult");
			if (mainObj.opt("addressListList") instanceof JSONObject) {
				inputParams.put("CAD7", mainObj.optJSONObject("addressListList").optString("city"));
				inputParams.put("CAD8E",
						String.valueOf(mainObj.optJSONObject("addressListList").optString("postCode")));
			}

			String expiryDate = String.valueOf(HelperMethods.getFieldValue(getCustomerData, "IDExpiryDate"));
			String expiryDateF = expiryDate.replaceAll("-", "/");
			DOB = String.valueOf(HelperMethods.getFieldValue(getCustomerData, "DateOfBirth"));

			String dateOfBirth = DOB.replaceAll("-", "/");
			inputParams.put("PRODUCT_TYPE", "PLN");
			inputParams.put("ENQUIRY_REFERENCE", generateUUID());
			// inputParams.put("AMOUNT", LOAN_AMOUNT);
			// inputParams.put("CID1", NATIONAL_ID.startsWith("1") ? "T" : "Q");
			// inputParams.put("CID2", NATIONAL_ID);
			inputParams.put("AMOUNT", HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount"));
			inputParams.put("CID1", request.getParameter("NationalID").startsWith("1") ? "T" : "Q");
			inputParams.put("CID2", request.getParameter("NationalID"));

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
					HelperMethods.getFieldValue(getCustomerData, "Gender").equalsIgnoreCase("M") ? "M" : "F");
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
			inputParams.put("CEML", HelperMethods.getFieldValue(getCustomerData, "FullName")); // Customer email
			inputParams.put("CADR", "");
			inputParams.put("CAD1A", "1223");
			// inputParams.put("CAD7", "CAD7Value" /*
			// getNationalAddress.getParamValueByName("postCode") */);
			// inputParams.put("CAD8E", "CAD8EValue" /*
			// getNationalAddress.getParamValueByName("city") */);
			inputParams.put("CAD9", "SAU");
			inputParams.put("CCN1", "M");
			inputParams.put("CCN2", "966");
			inputParams.put("CCN3", "0");
			// inputParams.put("CCN4", CONTACT_NUMBER);
			inputParams.put("CCN4", request.getParameter("Mobile"));

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
			inputParams.put("EAD9", "SAU");

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

	private Map<String, String> createRequestForUpdateCustomerApplicationDataServiceS2(
			Result getCustomerApplicationData, DataControllerRequest request) {

		Map<String, String> inputParams = new HashMap<>();

		inputParams.put("id", HelperMethods.getFieldValue(getCustomerApplicationData, "id"));
		inputParams.put("Customer_id", HelperMethods.getFieldValue(getCustomerApplicationData, "Customer_id"));
		inputParams.put("mobile", HelperMethods.getFieldValue(getCustomerApplicationData, "mobile"));
		// inputParams.put("nationalId", NATIONAL_ID);
		inputParams.put("nationalId", request.getParameter("NationalID"));

		inputParams.put("productId", HelperMethods.getFieldValue(getCustomerApplicationData, "productId"));
		inputParams.put("productName", request.getParameter("Product"));
		inputParams.put("isknockouts1", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockouts1"));
		inputParams.put("knockoutStatus", "FAIL");
		inputParams.put("applicationStatus", "SID_SUSPENDED");
		inputParams.put("applicationID", request.getParameter("ApplicationID"));
		inputParams.put("createdby", HelperMethods.getFieldValue(getCustomerApplicationData, "createdby"));
		inputParams.put("modifiedby", HelperMethods.getFieldValue(getCustomerApplicationData, "modifiedby"));
		// inputParams.put("lastmodifiedts",
		// IjarahHelperMethods.getDate(LocalDateTime.now(),
		// DATE_FORMAT_WITH_SECONDS_MS));
		inputParams.put("isknockoutTnC", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockoutTnC"));
		inputParams.put("loanAmount", HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount"));
		// inputParams.put("tenor", TENOR);
		inputParams.put("tenor", HelperMethods.getFieldValue(getCustomerApplicationData, "tenor"));

		inputParams.put("approx", "0");
		inputParams.put("scoredCardId", HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId"));
		inputParams.put("loanAmountCap", "0");
		inputParams.put("loanRate", "0");
		// inputParams.put("insideKsa", INSIDE_KSA);
		inputParams.put("insideKsa", HelperMethods.getFieldValue(getCustomerApplicationData, "insideKsa"));

		inputParams.put("customerAge", HelperMethods.getFieldValue(getCustomerApplicationData, "customerAge"));
		inputParams.put("loanAmountCore", "0");
		inputParams.put("loanAmountInf", "0");
		inputParams.put("monthlyRepay", "0");
		inputParams.put("offerAmount", "0");
		inputParams.put("csaApporval", "0");
		inputParams.put("sanadApproval", "0");
		inputParams.put("tenorCore", "0");

		LOG.error(inputParams);
		return inputParams;
	}

	private Map<String, String> createRequestForUpdateCustomerApplicationDataService(Result getCustomerApplicationData,
			Result getScoreCardS2, Result getScoreCardS3, DataControllerRequest request, double maxEMI,
			int calMAXGblDti, int calMAXinternalDti, double customerGblDti, double customerInternalDti) {
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
				loanRate = scoreCardS3.getBody().getLoanRate();
			}

			if (scoreCardS3.getBody().getAprRate() != null) {
				approx = scoreCardS3.getBody().getAprRate();
			}

			if (scoreCardS3.getBody().getLoanAmountCap() != null) {
				loanAmountCap = scoreCardS3.getBody().getLoanAmountCap();
			}

			if (scoreCardS3.getBody().getTenor() != null) {
				tenor = scoreCardS3.getBody().getTenor();
			}
		}

		// TODO
		// Remove static value and add employee name
		// loanAmountCap = "20000";

		// inputParams.put("nationalId", NATIONAL_ID);
		inputParams.put("nationalId", request.getParameter("NationalID"));

		inputParams.put("id", HelperMethods.getFieldValue(getCustomerApplicationData, "id"));
		inputParams.put("Customer_id", HelperMethods.getFieldValue(getCustomerApplicationData, "Customer_id"));
		inputParams.put("mobile", HelperMethods.getFieldValue(getCustomerApplicationData, "mobile"));

		inputParams.put("productId", HelperMethods.getFieldValue(getCustomerApplicationData, "productId"));
		inputParams.put("productName", request.getParameter("Product"));
		inputParams.put("isknockouts1", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockouts1"));
		inputParams.put("knockoutStatus", knockoutStatus);
		inputParams.put("applicationStatus", applicationStatus);
		inputParams.put("applicationID", request.getParameter("ApplicationID"));
		inputParams.put("createdby", HelperMethods.getFieldValue(getCustomerApplicationData, "createdby"));
		inputParams.put("modifiedby", HelperMethods.getFieldValue(getCustomerApplicationData, "modifiedby"));

		// inputParams.put("lastmodifiedts",
		// IjarahHelperMethods.getDate(LocalDateTime.now(),
		// DATE_FORMAT_WITH_SECONDS_MS));

		inputParams.put("isknockoutTnC", HelperMethods.getFieldValue(getCustomerApplicationData, "isknockoutTnC"));

		// inputParams.put("loanAmount",
		// HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount"));
		// inputParams.put("tenor", TENOR);

		inputParams.put("approx", approx);
		inputParams.put("scoredCardId", HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId"));
		inputParams.put("loanAmountCap", loanAmountCap);
		inputParams.put("loanRate", loanRate);
		// inputParams.put("insideKsa", INSIDE_KSA);
		inputParams.put("insideKsa", HelperMethods.getFieldValue(getCustomerApplicationData, "insideKsa"));
		inputParams.put("customerAge", HelperMethods.getFieldValue(getCustomerApplicationData, "customerAge"));
		inputParams.put("loanAmountCore", loanAmountCap);

		if (!loanRate.equalsIgnoreCase("0")) {

			inputParams.put("loanAmountInf",
					calculateLoanAmountInf(Double.parseDouble(loanRate), Integer.parseInt(tenor), maxEMI));
			double amountOffer = Math.min(
					Math.min(Double.parseDouble(inputParams.get("loanAmountCap")),
							Double.parseDouble(inputParams.get("loanAmountInf"))),
					Double.parseDouble(
							HelperMethods.getFieldValue(getCustomerApplicationData, "loanAmount").replaceAll(",", "")));

			// double amountOffer =
			// Math.min(Double.parseDouble(inputParams.get("loanAmountCap")),
			// Double.parseDouble(HelperMethods.getFieldValue(getCustomerApplicationData,
			// "loanAmount").replaceAll(",", "")));

			getLoanSimulation(
					createRequestForSimulation(String.valueOf(amountOffer), loanRate, getCustomerApplicationData));

			inputParams.put("monthlyRepay", EMI);
			inputParams.put("offerAmount", String.valueOf(amountOffer));
			// adding saad sabab sanad emi
			inputParams.put("sabbNumber", SABB);
			inputParams.put("sadadNumber", SAAD);
			AMOUNT_OFFER = String.valueOf(amountOffer);

			LOG.error("Offer Amount :::::======" + AMOUNT_OFFER);

		} else {
			inputParams.put("loanAmountInf", "0");
			inputParams.put("monthlyRepay", "0");
			inputParams.put("offerAmount", "0");
			inputParams.put("knockoutStatus", "FAIL");
			inputParams.put("applicationStatus", "SID_SUSPENDED");
		}
		inputParams.put("tenorCore", tenor);
		inputParams.put("csaApporval", "0");
		inputParams.put("sanadApproval", "0");

		double dbOfferAmount = fetchProductRangeOfferFromDB();
		LOG.error("dbOfferAmount :: " + dbOfferAmount);
		if (inputParams.get("offerAmount").equalsIgnoreCase("0")
				|| inputParams.get("offerAmount").equalsIgnoreCase("0.0")
				|| Double.parseDouble(inputParams.get("offerAmount")) < dbOfferAmount) {
			inputParams.put("knockoutStatus", "FAIL");
			inputParams.put("applicationStatus", "SID_SUSPENDED");
		}

		saveMISReportData(createRequestForMISReportDBCall(inputParams.get("knockoutStatus").toString(), maxEMI,
				customerGblDti, customerInternalDti, calMAXGblDti, calMAXinternalDti, request), request);
		return inputParams;
	}

	private double fetchProductRangeOfferFromDB() {
		double dbOfferRange = 0.0;

		HashMap<String, Object> inpUpdate = new HashMap();

		try {
			String res = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
					.withOperationId("dbxdb_productrange_get").withRequestParameters(inpUpdate).build().getResponse();

			JSONObject jsonObject = new JSONObject(res);

			if (jsonObject.optJSONArray("productrange").length() > 0) {
				String minAmount = jsonObject.optJSONArray("productrange").getJSONObject(0).optString("MinAmount");
				dbOfferRange = Double.parseDouble(minAmount);
			}

		} catch (DBPApplicationException e) {
			// TODO Auto-generated catch block
			dbOfferRange = 0.0;
			e.printStackTrace();
		}
		return dbOfferRange;
	}

	private String calculateLoanAmountInf(double loanRate, int tenor, double maxEMI) {

		/*
		 * double totalPayableAmount = MAX_EMI * tenor; double totalProfitRate =
		 * (loanRate * tenor) / 12; double principalPlusProfitRate = totalProfitRate +
		 * 100; return String.valueOf(Math.floor((totalPayableAmount /
		 * principalPlusProfitRate) * 100));
		 */
		loanRate /= 100;
		// double loanAmount = MAX_EMI * (1 - (1 / Math.pow((1 + loanRate / 12),
		// tenor))) / (loanRate / 12);

		double loanAmount = maxEMI * (1 - (1 / Math.pow((1 + loanRate / 12), tenor))) / (loanRate / 12);

		String calculateLoanAmountInfVal = String.valueOf((Math.floor(loanAmount / 1000)) * 1000);

		LOG.error("calculateLoanAmountInf MAX_EMI :: " + maxEMI);
		LOG.error("calculateLoanAmountInf loanRate :: " + loanRate);
		LOG.error("calculateLoanAmountInf tenor :: " + tenor);
		LOG.error("calculateLoanAmountInf loanAmount :: " + loanAmount);
		LOG.error("calculateLoanAmountInf calculateLoanAmountInfVal :: " + calculateLoanAmountInfVal);

		return calculateLoanAmountInfVal;
	}

	private String calculateMonthlyRepay(double amountOffer, double loanRate, int tenor) {
		return String.valueOf(Math.floor((amountOffer + (amountOffer * (loanRate / 100) * tenor / 12)) / tenor));
	}

	private void calculatePensioner(Result getSalaryCertificate) {
		try {
			switch (EMPLOYER_TYPE_ID) {
			case "1":
				if (Arrays.asList(EMPLOYER_NAME_FOR_PENSIONERS)
						.contains(getSalaryCertificate.getParamValueByName("agencyName"))) {
					PENSIONER = "1";
				}
				break;
			case "3":
				if (Arrays.asList(EMPLOYER_NAME_FOR_PENSIONERS)
						.contains(getSalaryCertificate.getParamValueByName("employerName"))) {
					PENSIONER = "1";
				}
				break;
			default:
				PENSIONER = "0";
				break;
			}
		} catch (Exception ex) {
			LOG.error("ERROR calculatePensioner :: " + ex);
		}
	}

	private boolean calculateMonthlyNetSalary(Result getSalaryCertificate) {

		boolean checkSalary = true;

		try {
			// NATIONALITY = getSalaryCertificate.getParamValueByName("nationality");
			LOG.error("calculateMonthlyNetSalary EMPLOYER_TYPE_ID :: " + EMPLOYER_TYPE_ID);
			switch (EMPLOYER_TYPE_ID) {
			case "1":
				String basic = getSalaryCertificate.getParamValueByName("basicSalary");
				String allowence = getSalaryCertificate.getParamValueByName("totalAllownces");
				String deduc = getSalaryCertificate.getParamValueByName("totalDeductions");

				String netsalary = String
						.valueOf(Double.parseDouble(basic) + Double.parseDouble(allowence) - Double.parseDouble(deduc));

				netsalary = String.format("%.2f", Double.parseDouble(netsalary));

				MONTHLY_NET_SALARY = netsalary;
				break;
			case "3":

				EMPLOYMENT_STATUS = getSalaryCertificate.getParamValueByName("employmentStatus");
				LOG.error("calculateMonthlyNetSalary EMPLOYMENT_STATUS 1:: " + EMPLOYMENT_STATUS);
				if (EMPLOYMENT_STATUS.equalsIgnoreCase("نشيط") || EMPLOYMENT_STATUS.equalsIgnoreCase("Active")) {
					LOG.error("calculateMonthlyNetSalary EMPLOYMENT_STATUS 2:: " + EMPLOYMENT_STATUS);

					double calculatedDeductions = 0;
					if (NATIONALITY.equalsIgnoreCase("SAU") || NATIONALITY.equalsIgnoreCase("SA")) {
						double minimumAmount = 0.1
								* (Double.parseDouble(getSalaryCertificate.getParamValueByName("basicWage")) + Double
										.parseDouble(getSalaryCertificate.getParamValueByName("housingAllowance")));
						calculatedDeductions = Math.min(minimumAmount, 4500);
						LOG.error("calculateMonthlyNetSalary calculatedDeductions :: " + calculatedDeductions);
					}
					String pvtNetsalary = String
							.valueOf((Double.parseDouble(getSalaryCertificate.getParamValueByName("basicWage"))
									+ Double.parseDouble(getSalaryCertificate.getParamValueByName("housingAllowance"))
									+ Double.parseDouble(getSalaryCertificate.getParamValueByName("otherAllowance")))
									- calculatedDeductions);

					pvtNetsalary = String.format("%.2f", Double.parseDouble(pvtNetsalary));

					MONTHLY_NET_SALARY = pvtNetsalary;
					LOG.error("calculateMonthlyNetSalary PVT MONTHLY_NET_SALARY :: " + MONTHLY_NET_SALARY);
				} else {
					checkSalary = false;
					LOG.error("calculateMonthlyNetSalary CAse Customer is non active :: ");
				}
				LOG.error("calculateMonthlyNetSalary CAse 3 :: " + MONTHLY_NET_SALARY);
				break;
			default:
				checkSalary = false;
				LOG.error("DEFAULT calculateMonthlyNetSalary :: " + MONTHLY_NET_SALARY);
				MONTHLY_NET_SALARY = "0";
				break;
			}
		} catch (Exception ex) {
			LOG.error("ERROR calculateMonthlyNetSalary :: " + ex);
		}
		SALARY_WITHOUT_ALLOWANCES = MONTHLY_NET_SALARY;

		return checkSalary;
	}

	private void calculateCurrentLengthOfService(Result getSalaryCertificate) {
		try {
			LOG.error("calculateCurrentLengthOfService EMPLOYER_TYPE_ID :: " + EMPLOYER_TYPE_ID);
			int currentLengthOfService = 0;
			switch (EMPLOYER_TYPE_ID) {
			case "1":
				LocalDate agencyEmploymentDate = LocalDate.parse(
						getSalaryCertificate.getParamValueByName("agencyEmploymentDate"),
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				LocalDate currentDate = LocalDate.now();
				currentLengthOfService = Math.toIntExact(
						ChronoUnit.MONTHS.between(YearMonth.from(agencyEmploymentDate), YearMonth.from(currentDate)));
				int employmentDay = agencyEmploymentDate.getDayOfMonth();
				int currentDay = currentDate.getDayOfMonth();
				if (currentDay < employmentDay) {
					currentLengthOfService -= 1;
				}
				break;
			case "3":
				LocalDate dateOfJoining = LocalDate.parse(getSalaryCertificate.getParamValueByName("dateOfJoining"),
						DateTimeFormatter.ofPattern("dd/MM/yyyy"));
				LocalDate currentDateNow = LocalDate.now();
				currentLengthOfService = Math.toIntExact(
						ChronoUnit.MONTHS.between(YearMonth.from(dateOfJoining), YearMonth.from(currentDateNow)));
				int dayOfJoining = dateOfJoining.getDayOfMonth();
				int currentDayNow = currentDateNow.getDayOfMonth();
				if (currentDayNow < dayOfJoining) {
					currentLengthOfService -= 1;
				}
				break;
			}
			CURRENT_LENGTH_OF_SERVICE = String.valueOf(currentLengthOfService);
		} catch (Exception ex) {
			LOG.error("ERROR calculateCurrentLengthOfService :: " + ex);
		}
	}

	private int calculateMaxGlobalDTI(DataControllerRequest request) {
		int maxGlobalDti = 45;
		LOG.error("ERROR Enter in  calculateMaxGlobalDTI :: ");
		if (request.getParameter("Product").equalsIgnoreCase("TAWARRUQ")) {
			if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
				for (CIDETAILItem ci_detail : CI_DETAIL) {
					String productType = ci_detail.getCIPRD();
					String status = ci_detail.getCISTATUS();
					// 26/01/2023 Its a logic when the customer has a mortgage product
					if (Arrays.asList(MORTGAGE_PRODUCT).contains(productType) && status.equalsIgnoreCase("A")) {
						if (Double.parseDouble(MONTHLY_NET_SALARY) <= 3999) {
							// MAX_GLOBAL_DTI = 55;
							maxGlobalDti = 55;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 3999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 14999) {
							// MAX_GLOBAL_DTI = 55;
							maxGlobalDti = 55;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 14999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 24999) {
							// MAX_GLOBAL_DTI = 65;
							maxGlobalDti = 65;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 24999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 999999) {
							// MAX_GLOBAL_DTI = 70;
							maxGlobalDti = 70;
						}
						break;
					} else {
						// 26/01/2023 Its a logic when the customer does not have a mortgage product
						if (Double.parseDouble(MONTHLY_NET_SALARY) <= 3999) {
							// MAX_GLOBAL_DTI = 45;
							maxGlobalDti = 45;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 3999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 14999) {
							// MAX_GLOBAL_DTI = 45;
							maxGlobalDti = 45;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 14999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 24999) {
							// MAX_GLOBAL_DTI = 45;
							maxGlobalDti = 45;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 24999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 999999) {
							// MAX_GLOBAL_DTI = 70;
							maxGlobalDti = 70;
						}
						// break;
					}
				}
			} else {
				if (CI_DETAIL2 != null && CI_DETAIL2.size() > 0) {
					for (com.ijarah.Model.consumerEnquiryModelFinal.CIDETAILSItem ci_detail : CI_DETAIL2) {
						String productType = ci_detail.getCIPRD();
						String status = ci_detail.getCISTATUS();
						if (Arrays.asList(MORTGAGE_PRODUCT).contains(productType) && status.equalsIgnoreCase("A")) {
							if (Double.parseDouble(MONTHLY_NET_SALARY) <= 3999) {
								// MAX_GLOBAL_DTI = 55;
								maxGlobalDti = 55;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 3999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 14999) {
								// MAX_GLOBAL_DTI = 55;
								maxGlobalDti = 55;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 14999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 24999) {
								// MAX_GLOBAL_DTI = 65;
								maxGlobalDti = 65;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 24999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 999999) {
								// MAX_GLOBAL_DTI = 70;
								maxGlobalDti = 70;
							}
							break;
						} else {
							// 26/01/2023 Its a logic when the customer does not have a mortgage product
							if (Double.parseDouble(MONTHLY_NET_SALARY) <= 3999) {
								// MAX_GLOBAL_DTI = 45;
								maxGlobalDti = 45;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 3999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 14999) {
								// MAX_GLOBAL_DTI = 45;
								maxGlobalDti = 45;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 14999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 24999) {
								// MAX_GLOBAL_DTI = 45;
								maxGlobalDti = 45;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 24999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 999999) {
								// MAX_GLOBAL_DTI = 70;
								maxGlobalDti = 70;
							}
							// break;
						}
					}
				}
			}
		}
		LOG.error("ERROR Enter in  calculateMaxGlobalDTI maxGlobalDti:: " + maxGlobalDti);
		return maxGlobalDti;
	}

	private int calculateMaxInternalDTI(DataControllerRequest request) {
		int maxInternalDti = 33;
		LOG.error("ERROR Enter in  calculateMaxInternalDTI :: ");
		if (request.getParameter("Product").equalsIgnoreCase("TAWARRUQ")) {
			if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
				for (CIDETAILItem ci_detail : CI_DETAIL) {
					String productType = ci_detail.getCIPRD();
					String status = ci_detail.getCISTATUS();

					// 26/01/2023 Its a logic when the customer has a mortgage product
					if (Arrays.asList(MORTGAGE_PRODUCT).contains(productType) && status.equalsIgnoreCase("A")) {
						if (Double.parseDouble(MONTHLY_NET_SALARY) <= 3999) {
							// MAX_INTERNAL_DTI = 45;
							maxInternalDti = 45;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 3999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 14999) {
							// MAX_INTERNAL_DTI = 45;
							maxInternalDti = 45;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 14999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 24999) {
							// MAX_INTERNAL_DTI = 45;
							maxInternalDti = 45;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 24999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 999999) {
							// MAX_INTERNAL_DTI = 45;
							maxInternalDti = 45;
						}
						break;
					} else {
						// 26/01/2023 Its a logic when the customer does not have a mortgage product
						if (Double.parseDouble(MONTHLY_NET_SALARY) <= 3999) {
							// MAX_INTERNAL_DTI = 25;
							maxInternalDti = 25;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 3999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 14999) {
							// MAX_INTERNAL_DTI = 33;
							maxInternalDti = 33;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 14999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 24999) {
							// MAX_INTERNAL_DTI = 33;
							maxInternalDti = 33;
						} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 24999
								&& Double.parseDouble(MONTHLY_NET_SALARY) <= 999999) {
							// MAX_INTERNAL_DTI = 33;
							maxInternalDti = 33;
						}
						// break;
					}
				}
			} else {
				if (CI_DETAIL2 != null && CI_DETAIL2.size() > 0) {
					for (com.ijarah.Model.consumerEnquiryModelFinal.CIDETAILSItem ci_detail : CI_DETAIL2) {
						String productType = ci_detail.getCIPRD();
						String status = ci_detail.getCISTATUS();
						if (Arrays.asList(MORTGAGE_PRODUCT).contains(productType) && status.equalsIgnoreCase("A")) {
							if (Double.parseDouble(MONTHLY_NET_SALARY) <= 3999) {
								// MAX_INTERNAL_DTI = 45;
								maxInternalDti = 45;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 3999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 14999) {
								// MAX_INTERNAL_DTI = 45;
								maxInternalDti = 45;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 14999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 24999) {
								// MAX_INTERNAL_DTI = 45;
								maxInternalDti = 45;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 24999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 999999) {
								// MAX_INTERNAL_DTI = 45;
								maxInternalDti = 45;
							}
							break;
						} else {
							// 26/01/2023 Its a logic when the customer does not have a mortgage product
							if (Double.parseDouble(MONTHLY_NET_SALARY) <= 3999) {
								// MAX_INTERNAL_DTI = 25;
								maxInternalDti = 25;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 3999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 14999) {
								// MAX_INTERNAL_DTI = 33;
								maxInternalDti = 33;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 14999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 24999) {
								// MAX_INTERNAL_DTI = 33;
								maxInternalDti = 33;
							} else if (Double.parseDouble(MONTHLY_NET_SALARY) > 24999
									&& Double.parseDouble(MONTHLY_NET_SALARY) <= 999999) {
								// MAX_INTERNAL_DTI = 33;
								maxInternalDti = 33;
							}
							// break;
						}
					}
				}
			}
		}
		LOG.error("ERROR Enter in  calculateMaxInternalDTI maxInternalDti:: " + maxInternalDti);
		return maxInternalDti;
	}

	private double calculateMaxEmi(int calMAXGblDti, int calMAXinternalDti, double customerGblDti,
			double customerInternalDti) {
		double maxEMI = 0.0;
		LOG.error("# MAX_GLOBAL_DTI :: " + calMAXGblDti);
		LOG.error("# CUSTOMER_GLOBAL_DTI :: " + customerGblDti);

		LOG.error("# MAX_INETRNAL_DTI :: " + calMAXinternalDti);
		LOG.error("# CUSTOMER_INTERNAL_DTI :: " + customerInternalDti);

		LOG.error("# calculateMaxEmi :: " + MONTHLY_NET_SALARY);

		int final_max_allowable_dti = Math.min(calculateMaxOverallAllowedDTI(calMAXGblDti, customerGblDti),
				calculateMaxInternalAllowedDTI(calMAXinternalDti, customerInternalDti));

		/*
		 * MAX_EMI = Double.parseDouble(MONTHLY_NET_SALARY) * final_max_allowable_dti;
		 * MAX_EMI = MAX_EMI / 100;
		 */

		maxEMI = Double.parseDouble(MONTHLY_NET_SALARY) * final_max_allowable_dti;
		maxEMI = maxEMI / 100;

		LOG.error("#final_max_allowable_dti :: " + final_max_allowable_dti);
		LOG.error("#MAX_EMI :: " + maxEMI);

		return maxEMI;
	}

	private int calculateMaxOverallAllowedDTI(int calMAXGblDti, double customerGblDti) {
		// return Math.max(((int) (MAX_GLOBAL_DTI - CUSTOMER_GLOBAL_DTI)), 0);
		return Math.max(((int) (Double.valueOf(calMAXGblDti) - customerGblDti)), 0);
	}

	private int calculateMaxInternalAllowedDTI(int calMAXinternalDti, double customerInternalDti) {
		// return Math.max(((int) (MAX_INTERNAL_DTI - CUSTOMER_INTERNAL_DTI)), 0);
		return Math.max(((int) (Double.valueOf(calMAXinternalDti) - customerInternalDti)), 0);
	}

	
	private JSONObject calculateGlobalDTI(int calMAXGblDti) {

		boolean checkAllow = true;
		double customerGblDti = 0.0;
		JSONObject jsonObject = new JSONObject();

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
					String ci_summary = ci_detail.getCISUMMRY();

					if (Arrays.asList(CREDIT_CARD_PRODUCT).contains(productType)) {
						totalDebtServicing += 0.05 * Double.parseDouble(creditLimit);
						INSTALLMENT_AMOUNT.append(Double.parseDouble(creditLimit) + ",");
					} else {
						if (frequency.equalsIgnoreCase("M")) {
							totalDebtServicing += installment;
							INSTALLMENT_AMOUNT.append(installment + ",");
						} else if (frequency.equalsIgnoreCase("Q")) {
							totalDebtServicing += installment / 3;
							INSTALLMENT_AMOUNT.append(installment / 3 + ",");
						} else if (frequency.equalsIgnoreCase("H")) {
							totalDebtServicing += installment / 6;
							INSTALLMENT_AMOUNT.append(installment / 6 + ",");
						} else if (frequency.equalsIgnoreCase("Y")) {
							totalDebtServicing += installment / 12;
							INSTALLMENT_AMOUNT.append(installment / 12+ ",");
						} else {
							totalDebtServicing += installment;
							INSTALLMENT_AMOUNT.append(installment + ",");
						}
					}

					PRODUCT_DETAILS.append(productType + ",");
					LIMIT_AMOUNT.append(creditLimit + ",");
					CI_SUMMARY.append(ci_summary + ",");
				}
			}
			LOG.error("#Ghufran totalDebtServicing :: " + totalDebtServicing);
			// CUSTOMER_GLOBAL_DTI = (totalDebtServicing /
			// Double.parseDouble(MONTHLY_NET_SALARY)) * 100;
			customerGblDti = (totalDebtServicing / Double.parseDouble(MONTHLY_NET_SALARY)) * 100;
			LOG.error("#Ghufran CUSTOMER_GLOBAL_DTI :: " + customerGblDti);
			/*
			 * if (CUSTOMER_GLOBAL_DTI >= MAX_GLOBAL_DTI) { GLOBAL_DTI = "0"; checkAllow =
			 * false; }
			 */
			if (customerGblDti >= calMAXGblDti) {
				GLOBAL_DTI = "0";
				checkAllow = false;
			}
		} else {
			if (CI_DETAIL2 != null && CI_DETAIL2.size() > 0) {
				double totalDebtServicing = 0;
				for (com.ijarah.Model.consumerEnquiryModelFinal.CIDETAILSItem ci_detail : CI_DETAIL2) {
					String productType = ci_detail.getCIPRD();

					String status = ci_detail.getCISTATUS();
					if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType) && status.equalsIgnoreCase("A")) {
						// Financial product
						String frequency = ci_detail.getCIFRQ();
						double installment = Double.parseDouble(ci_detail.getCIINSTL());
						String creditLimit = ci_detail.getCILIMIT();
						String ci_summary = ci_detail.getCISUMMRY();
						
						if (Arrays.asList(CREDIT_CARD_PRODUCT).contains(productType)) {
							totalDebtServicing += 0.05 * Double.parseDouble(creditLimit);
							INSTALLMENT_AMOUNT.append(Double.parseDouble(creditLimit) + ",");
						} else {
							if (frequency.equalsIgnoreCase("M")) {
								totalDebtServicing += installment;
								INSTALLMENT_AMOUNT.append(installment + ",");
							} else if (frequency.equalsIgnoreCase("Q")) {
								totalDebtServicing += installment / 3;
								INSTALLMENT_AMOUNT.append(installment / 3 + ",");
							} else if (frequency.equalsIgnoreCase("H")) {
								totalDebtServicing += installment / 6;
								INSTALLMENT_AMOUNT.append(installment / 6 + ",");
							} else if (frequency.equalsIgnoreCase("Y")) {
								totalDebtServicing += installment / 12;
								INSTALLMENT_AMOUNT.append(installment / 12 + ",");
							} else {
								totalDebtServicing += installment;
								INSTALLMENT_AMOUNT.append(installment + ",");
							}
						}
						PRODUCT_DETAILS.append(productType + ",");
						LIMIT_AMOUNT.append(creditLimit + ",");
						CI_SUMMARY.append(ci_summary + ",");
					}
				}
				LOG.error("#Ghufran totalDebtServicing :: " + totalDebtServicing);
				// CUSTOMER_GLOBAL_DTI = (totalDebtServicing /
				// Double.parseDouble(MONTHLY_NET_SALARY)) * 100;
				customerGblDti = (totalDebtServicing / Double.parseDouble(MONTHLY_NET_SALARY)) * 100;
				LOG.error("#Ghufran CUSTOMER_GLOBAL_DTI :: " + customerGblDti);
				/*
				 * if (CUSTOMER_GLOBAL_DTI >= MAX_GLOBAL_DTI) { GLOBAL_DTI = "0"; checkAllow =
				 * false; }
				 */
				if (customerGblDti >= calMAXGblDti) {
					GLOBAL_DTI = "0";
					checkAllow = false;
				}
			}
		}

		jsonObject.put("check", checkAllow);
		jsonObject.put("customerGblDti", customerGblDti);
		return jsonObject;
	}

	private double calculateInternalDTI(int calMAXinternalDti) {
		double customerInternalDti = 0.0;
		INTERNAL_DTI = "1";
		if (CI_DETAIL != null && CI_DETAIL.size() > 0) {
			double totalDebtServicing = 0;
			for (CIDETAILItem ci_detail : CI_DETAIL) {
				String productType = ci_detail.getCIPRD();
				String productCode = ci_detail.getCICRDTR();

				String status = ci_detail.getCISTATUS();
				if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType) && status.equalsIgnoreCase("A")
						&& productCode.equalsIgnoreCase("IJRH")) {
					// Financial product
					String frequency = ci_detail.getCIFRQ();
					double installment = Double.parseDouble(ci_detail.getCIINSTL());
					String creditLimit = ci_detail.getCILIMIT();
					String ci_summary = ci_detail.getCISUMMRY();

					if (Arrays.asList(CREDIT_CARD_PRODUCT).contains(productType)) {
						totalDebtServicing += 0.05 * Integer.parseInt(creditLimit);
						INSTALLMENT_AMOUNT.append(Integer.parseInt(creditLimit) + ",");
					} else {
						if (frequency.equalsIgnoreCase("M")) {
							totalDebtServicing += installment;
							INSTALLMENT_AMOUNT.append(installment + ",");
						} else if (frequency.equalsIgnoreCase("Q")) {
							totalDebtServicing += installment / 3;
							INSTALLMENT_AMOUNT.append(installment / 3 + ",");
						} else if (frequency.equalsIgnoreCase("H")) {
							totalDebtServicing += installment / 6;
							INSTALLMENT_AMOUNT.append(installment / 6 + ",");
						} else if (frequency.equalsIgnoreCase("Y")) {
							totalDebtServicing += installment / 12;
							INSTALLMENT_AMOUNT.append(installment / 12 + ",");
						} else {
							totalDebtServicing += installment;
							INSTALLMENT_AMOUNT.append(installment + ",");
						}
					}
					PRODUCT_DETAILS.append(productCode + ",");
					LIMIT_AMOUNT.append(creditLimit + ",");
					CI_SUMMARY.append(ci_summary + ",");
				}
			}
			// CUSTOMER_INTERNAL_DTI = (totalDebtServicing /
			// Double.parseDouble(MONTHLY_NET_SALARY)) * 100;
			customerInternalDti = (totalDebtServicing / Double.parseDouble(MONTHLY_NET_SALARY)) * 100;
			/*
			 * if (CUSTOMER_INTERNAL_DTI >= MAX_INTERNAL_DTI) { INTERNAL_DTI = "0"; }
			 */
			if (customerInternalDti >= calMAXinternalDti) {
				INTERNAL_DTI = "0";
			}
		} else {
			if (CI_DETAIL2 != null && CI_DETAIL2.size() > 0) {
				double totalDebtServicing = 0;
				for (com.ijarah.Model.consumerEnquiryModelFinal.CIDETAILSItem ci_detail : CI_DETAIL2) {
					String productType = ci_detail.getCIPRD();
					String productCode = ci_detail.getCICRDTR();

					String status = ci_detail.getCISTATUS();
					if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType) && status.equalsIgnoreCase("A")
							&& productCode.equalsIgnoreCase("IJRH")) {
						// Financial product
						String frequency = ci_detail.getCIFRQ();
						double installment = Double.parseDouble(ci_detail.getCIINSTL());
						String creditLimit = ci_detail.getCILIMIT();
						String ci_summary = ci_detail.getCISUMMRY();

						if (Arrays.asList(CREDIT_CARD_PRODUCT).contains(productType)) {
							totalDebtServicing += 0.05 * Integer.parseInt(creditLimit);
							INSTALLMENT_AMOUNT.append(Integer.parseInt(creditLimit) + ",");
						} else {
							if (frequency.equalsIgnoreCase("M")) {
								totalDebtServicing += installment;
								INSTALLMENT_AMOUNT.append(installment + ",");
							} else if (frequency.equalsIgnoreCase("Q")) {
								totalDebtServicing += installment / 3;
								INSTALLMENT_AMOUNT.append(installment / 3 + ",");
							} else if (frequency.equalsIgnoreCase("H")) {
								totalDebtServicing += installment / 6;
								INSTALLMENT_AMOUNT.append(installment / 6 + ",");
							} else if (frequency.equalsIgnoreCase("Y")) {
								totalDebtServicing += installment / 12;
								INSTALLMENT_AMOUNT.append(installment / 12 + ",");
							} else {
								totalDebtServicing += installment;
								INSTALLMENT_AMOUNT.append(installment + ",");
							}
						}

						PRODUCT_DETAILS.append(productCode + ",");
						LIMIT_AMOUNT.append(creditLimit + ",");
						CI_SUMMARY.append(ci_summary + ",");
					}
				}
				// CUSTOMER_INTERNAL_DTI = (totalDebtServicing /
				// Double.parseDouble(MONTHLY_NET_SALARY)) * 100;
				customerInternalDti = (totalDebtServicing / Double.parseDouble(MONTHLY_NET_SALARY)) * 100;
				/*
				 * if (CUSTOMER_INTERNAL_DTI >= MAX_INTERNAL_DTI) { INTERNAL_DTI = "0"; }
				 */
				if (customerInternalDti >= calMAXinternalDti) {
					INTERNAL_DTI = "0";
				}
			}
		}

		return customerInternalDti;
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
		} else {
			if (CI_DETAIL2 != null && CI_DETAIL2.size() > 0) {
				for (com.ijarah.Model.consumerEnquiryModelFinal.CIDETAILSItem ci_detail : CI_DETAIL2) {
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
		} else {
			if (CI_DETAIL2 != null && CI_DETAIL2.size() > 0) {
				int tempMaxDelinquency = 0;
				for (com.ijarah.Model.consumerEnquiryModelFinal.CIDETAILSItem ci_detail : CI_DETAIL2) {
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
	}

	private String calculateFinancialDefaultAmount() {
		int FINANCIAL_DEFAULT_AMOUNT_INNER = 0;
		if (DEFAULT != null && DEFAULT.size() > 0) {
			for (DEFAULTItem defaultItem : DEFAULT) {
				String productType = defaultItem.getDFPRD();
				if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
					double FINANCIAL_DEFAULT_AMOUNT_TEMP = Double.parseDouble(defaultItem.getDFCUB());
					FINANCIAL_DEFAULT_AMOUNT_INNER += FINANCIAL_DEFAULT_AMOUNT_TEMP;
				}
			}
		} else {
			if (DEFAULT2 != null && DEFAULT2.size() > 0) {
				for (com.ijarah.Model.consumerEnquiryModelFinal.DEFAULTSItem defaultItem : DEFAULT2) {
					String productType = defaultItem.getDFPRD();
					if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
						double FINANCIAL_DEFAULT_AMOUNT_TEMP = Double.parseDouble(defaultItem.getDFCUB());
						FINANCIAL_DEFAULT_AMOUNT_INNER += FINANCIAL_DEFAULT_AMOUNT_TEMP;
					}
				}
			}
		}

		LOG.error("calculateFinancialDefaultAmount END :: " + String.valueOf(FINANCIAL_DEFAULT_AMOUNT_INNER));

		return String.valueOf(FINANCIAL_DEFAULT_AMOUNT_INNER);
	}

	private String calculateNonFinancialDefaultAmount() {
		int NON_FINANCIAL_DEFAULT_AMOUNT = 0;
		if (DEFAULT != null && DEFAULT.size() > 0) {
			for (DEFAULTItem defaultItem : DEFAULT) {
				String productType = defaultItem.getDFPRD();
				if (Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
					double NON_FINANCIAL_DEFAULT_AMOUNT_TEMP = Double.parseDouble(defaultItem.getDFCUB());
					NON_FINANCIAL_DEFAULT_AMOUNT += NON_FINANCIAL_DEFAULT_AMOUNT_TEMP;
				}
			}
		} else {
			if (DEFAULT2 != null && DEFAULT2.size() > 0) {
				for (com.ijarah.Model.consumerEnquiryModelFinal.DEFAULTSItem defaultItem : DEFAULT2) {
					String productType = defaultItem.getDFPRD();
					if (Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
						double NON_FINANCIAL_DEFAULT_AMOUNT_TEMP = Double.parseDouble(defaultItem.getDFCUB());
						NON_FINANCIAL_DEFAULT_AMOUNT += NON_FINANCIAL_DEFAULT_AMOUNT_TEMP;
					}
				}
			}
		}

		LOG.error("calculateNonFinancialDefaultAmount END :: " + String.valueOf(NON_FINANCIAL_DEFAULT_AMOUNT));
		return String.valueOf(NON_FINANCIAL_DEFAULT_AMOUNT);
	}

	private String calculateBouncedCheque() {
		String checkBC = "";
		if (BOUNCED_CHECK != null && BOUNCED_CHECK.size() > 0) {
			for (BOUNCEDCHECKItem bouncedcheckItem : BOUNCED_CHECK) {
				if (bouncedcheckItem.getBCSETTLDDATE() != null) {
					if (!IjarahHelperMethods.isBlank(bouncedcheckItem.getBCSETTLDDATE())) {
						BOUNCED_CHEQUE = "SB";
						checkBC = "SB";
					} else {
						BOUNCED_CHEQUE = "UB";
						checkBC = "UB";
						break;
					}
				}
			}
		} else if (BOUNCED_CHEQUE2 != null && BOUNCED_CHEQUE2.size() > 0) {
			for (com.ijarah.Model.consumerEnquiryModelFinal.BOUNCEDCHECKSItem bouncedCheckItem : BOUNCED_CHEQUE2) {
				if (bouncedCheckItem.getBCSETTLDDATE() != null) {
					if (!IjarahHelperMethods.isBlank(bouncedCheckItem.getBCSETTLDDATE())) {
						BOUNCED_CHEQUE = "SB";
						checkBC = "SB";
					} else {
						BOUNCED_CHEQUE = "UB";
						checkBC = "UB";
						break;
					}
				}
			}
		} else {
			checkBC = "NB";
		}

		LOG.error("calculateBouncedCheque END :: " + checkBC);

		return checkBC;
	}

	private String calculateCourtJudgement() {
		String checkCJ = "";
		if (JUDGEMENT != null && JUDGEMENT.size() > 0) {
			for (JUDGEMENTItem judgementItem : JUDGEMENT) {

				if (judgementItem.getEJSETTLEDATE() != null) {
					if (!IjarahHelperMethods.isBlank(judgementItem.getEJSETTLEDATE())) {
						COURT_JUDGEMENT = "SJ";
						checkCJ = "SJ";
					} else {
						COURT_JUDGEMENT = "UJ";
						checkCJ = "UJ";
						break;
					}
				}
			}
		} else if (JUDGEMENT2 != null && JUDGEMENT2.size() > 0) {
			for (com.ijarah.Model.consumerEnquiryModelFinal.JUDGEMENTSItem judgementItem : JUDGEMENT2) {
				if (judgementItem.getEJSETTLEDATE() != null) {
					if (!IjarahHelperMethods.isBlank(judgementItem.getEJSETTLEDATE())) {
						COURT_JUDGEMENT = "SJ";
						checkCJ = "SJ";
					} else {
						COURT_JUDGEMENT = "UJ";
						checkCJ = "UJ";
						break;
					}
				}
			}
		} else {
			checkCJ = "NJ";
		}

		LOG.error("calculateCourtJudgement END :: " + checkCJ);

		return checkCJ;
	}

	private void getEmployerName(Result getSalaryCertificate) {

		try {
			switch (EMPLOYER_TYPE_ID) {
			case "1":
				if (getSalaryCertificate.getParamValueByName("agencyName") != null) {
					EMPLOYER_NAME = getSalaryCertificate.getParamValueByName("agencyName");
				} else {
					EMPLOYER_NAME = "EMPLOYER_NAME_VALUE";
				}
				break;
			case "3":
				if (getSalaryCertificate.getParamValueByName("employerName") != null) {
					EMPLOYER_NAME = getSalaryCertificate.getParamValueByName("employerName");
				} else {
					EMPLOYER_NAME = "EMPLOYER_NAME_VALUE";
				}
				break;
			default:
				EMPLOYER_NAME = "EMPLOYER_NAME_VALUE";
				break;
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
			} else {
				if (CI_DETAIL2 != null && CI_DETAIL2.size() > 0) {
					for (com.ijarah.Model.consumerEnquiryModelFinal.CIDETAILSItem ci_detail : CI_DETAIL2) {
						String productType = ci_detail.getCIPRD();
						if (!Arrays.asList(NON_FINANCIAL_PRODUCTS).contains(productType)) {
							NEW_TO_INDUSTRY = "N";
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			LOG.error("ERROR calculateNewToIndustry :: " + ex);
		}
	}

	private void calculateMaxLoanAmountCapping() {
		try {
			if (NEW_TO_INDUSTRY.equalsIgnoreCase("Y") && Double.parseDouble(MONTHLY_NET_SALARY) < 4500) {
				MAX_LOAN_AMOUNT_CAPPING = "100000";
			} else if (NEW_TO_INDUSTRY.equalsIgnoreCase("N") && Double.parseDouble(MONTHLY_NET_SALARY) < 7500) {
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
				OtherAllowance = Double.parseDouble(getSalaryCertificate.getParamValueByName("totalAllownces"));
				break;
			case "3": // Private
				basicSalary = Double.parseDouble(getSalaryCertificate.getParamValueByName("basicWage"));
				OtherAllowance = Double.parseDouble(getSalaryCertificate.getParamValueByName("otherAllowance"));
				basicSalary += Double.parseDouble(getSalaryCertificate.getParamValueByName("housingAllowance"));
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
				SALARY_WITHOUT_ALLOWANCES = String.format("%.2f", Double.parseDouble(SALARY_WITHOUT_ALLOWANCES));
				break;
			case "3": // Private
				SALARY_WITHOUT_ALLOWANCES = String.valueOf(Double.parseDouble(MONTHLY_NET_SALARY)
						- Double.parseDouble(getSalaryCertificate.getParamValueByName("otherAllowance")));
				SALARY_WITHOUT_ALLOWANCES = String.format("%.2f", Double.parseDouble(SALARY_WITHOUT_ALLOWANCES));
				break;
			}

		} catch (Exception ex) {
			LOG.error("ERROR calculateSalaryWithoutAllowances :: " + ex);
		}
	}

	private Map<String, String> createRequestForScoreCardS2Service(Result getSalaryCertificate,
			DataControllerRequest request, Result getCustomerApplicationData) {
		Map<String, String> inputParams = new HashMap<>();
		try {

			// inputParams.put("scorecardId", SCORECARD_ID);
			// inputParams.put("customerAge", CUSTOMER_AGE);
			// inputParams.put("loanRef", APPLICATION_ID);
			// inputParams.put("tenor", TENOR);
			// inputParams.put("insideKsa", INSIDE_KSA);
			inputParams.put("salaryAmount", MONTHLY_NET_SALARY);
			inputParams.put("tenor", HelperMethods.getFieldValue(getCustomerApplicationData, "tenor"));
			inputParams.put("scorecardId", HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId"));
			inputParams.put("validLos", CURRENT_LENGTH_OF_SERVICE);
			inputParams.put("liftSalary", MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY);
			inputParams.put("employeeName", EMPLOYER_NAME);
			inputParams.put("salaryNonAllowance", SALARY_WITHOUT_ALLOWANCES);
			inputParams.put("pensioner", PENSIONER);
			inputParams.put("employeeCategory", EMPLOYER_CATEGORISATION);

		} catch (Exception ex) {
			LOG.error("ERROR createRequestForScoreCardS2Service :: " + ex);
		}
		return inputParams;
	}

	private HashMap<String, Object> createRequestForSimulation(String amoOffer, String loanRate,
			Result getCustomerApplicationData) {
		// Map<String, Object> inputParams = new HashMap<>();

		HashMap<String, Object> inputParams = new HashMap<>();
		try {
			String term = HelperMethods.getFieldValue(getCustomerApplicationData, "tenor") + "M";
			// String term = TENOR + "M";

			// loanAmt= loanAmt.replace(",", "");
			LOG.error("========== AMount Offer sending for simulation is ::" + amoOffer);

			// LOG.error("========== Loan Amount sending for simulation is ::"+);
			inputParams.put("amount", amoOffer); // TODO amount offer should be sent
			inputParams.put("term", term);
			inputParams.put("partyId", PARTY_ID);
			inputParams.put("fixedRate", loanRate);

		} catch (Exception ex) {
			LOG.error("ERROR createRequestForSimulation :: " + ex);
		}
		return inputParams;
	}

	private Map<String, String> createRequestForScoreCardS3Service(Result getConsumerEnquiry,
			Result getCustomerApplicationData, Result getCustomerData, DataControllerRequest request) {
		Map<String, String> inputParams = new HashMap<>();
		try {

			// inputParams.put("scorecardId", SCORECARD_ID);
			// inputParams.put("customerAge", CUSTOMER_AGE);
			// inputParams.put("loanRef", APPLICATION_ID);
			// inputParams.put("tenor", TENOR);
			// inputParams.put("insideKsa", INSIDE_KSA);
			inputParams.put("insideKsa", HelperMethods.getFieldValue(getCustomerApplicationData, "insideKsa"));
			inputParams.put("customerAge", HelperMethods.getFieldValue(getCustomerApplicationData, "customerAge"));
			inputParams.put("loanRef", request.getParameter("ApplicationID"));
			inputParams.put("tenor", HelperMethods.getFieldValue(getCustomerApplicationData, "tenor"));
			inputParams.put("scorecardId", HelperMethods.getFieldValue(getCustomerApplicationData, "scoredCardId"));

			inputParams.put("dataType", request.getParameter("Product"));
			inputParams.put("calculate", CALCULATE);
			inputParams.put("pensioner", PENSIONER);

			inputParams.put("salaryAmount", MONTHLY_NET_SALARY);

			inputParams.put("validLos", CURRENT_LENGTH_OF_SERVICE);
			inputParams.put("validDti", GLOBAL_DTI);
			inputParams.put("currDelinquency", CURRENT_DELINQUENCY);
			inputParams.put("maxDelinquency", MAX_DELINQUENCY);
			// inputParams.put("bouncedCheck", BOUNCED_CHEQUE);
			inputParams.put("bouncedCheck", calculateBouncedCheque());

			// inputParams.put("courtJudgement", COURT_JUDGEMENT);
			inputParams.put("courtJudgement", calculateCourtJudgement());

			// inputParams.put("validUtil", NON_FINANCIAL_DEFAULT_AMOUNT);
			inputParams.put("validUtil", calculateNonFinancialDefaultAmount());

			// 31/01/2023 Add Financial default amount for validDefaults
			// inputParams.put("validDefaults", FINANCIAL_DEFAULT_AMOUNT);
			// inputParams.put("validDefaults", "0");
			inputParams.put("validDefaults", calculateFinancialDefaultAmount());

			inputParams.put("validInternalDti", INTERNAL_DTI);
			inputParams.put("currDelinquencyT", CURRENT_DELINQUENCY_T);
			inputParams.put("employeeName", EMPLOYER_NAME);
			//// Stop sending the employee categorization
			// if (EMPLOYER_TYPE_ID == "1") inputParams.put("employeeCategory",
			//// EMPLOYER_CATEGORISATION);

			inputParams.put("loanAmountCap", MAX_LOAN_AMOUNT_CAPPING);
			inputParams.put("liftSalary", MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY);
			inputParams.put("nationality", NATIONALITY);
			inputParams.put("newIndustry", NEW_TO_INDUSTRY);
			inputParams.put("salaryNonAllowance", SALARY_WITHOUT_ALLOWANCES);
			inputParams.put("score", SC_SCORE);
			inputParams.put("employeeCategory", EMPLOYER_CATEGORISATION);

		} catch (Exception ex) {
			LOG.error("ERROR createRequestForScoreCardS3Service :: " + ex);
		}
		return inputParams;
	}

	private boolean preProcess(DataControllerRequest request) {
		boolean checkValue = true;
		try {
			LOG.error("ERROR ApplicationID :: " + request.getParameter("ApplicationID"));
			LOG.error("ERROR NationalID :: " + request.getParameter("NationalID"));
			LOG.error("ERROR Product :: " + request.getParameter("Product"));
			LOG.error("ERROR Mobile :: " + request.getParameter("Mobile"));
			if (request.getParameter("ApplicationID") == null) {
				checkValue = false;

			}
			if (request.getParameter("NationalID") == null) {
				checkValue = false;

			}
			if (request.getParameter("Product") == null) {
				checkValue = false;

			}
			if (request.getParameter("Mobile") == null) {
				checkValue = false;

			}
			// APPLICATION_ID = request.getParameter("ApplicationID");
			// NATIONAL_ID = request.getParameter("NationalID");
			// DATA_TYPE = request.getParameter("Product");
			// CONTACT_NUMBER = request.getParameter("Mobile");
			// this.inputParams.put("ENQUIRY_REFERENCE",
			// String.valueOf(generateRandomInt()));
			// this.inputParams.put("employerTypeId", EMPLOYER_TYPE_ID);

		} catch (Exception ex) {
			checkValue = false;
			LOG.error("ERROR preProcess :: " + ex);

		}
		return checkValue;
		// return !inputParams.isEmpty();
	}

	private Result getCustomerApplicationData(DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Map<String, String> filter = new HashMap<>();
			// filter.put(DBPUtilitiesConstants.FILTER, "applicationID" +
			// DBPUtilitiesConstants.EQUAL + APPLICATION_ID);
			filter.put(DBPUtilitiesConstants.FILTER, "applicationID" + DBPUtilitiesConstants.EQUAL
					+ dataControllerRequest.getParameter("ApplicationID"));
			Result getCustomerApplicationData = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
					CUSTOMER_APPLICATION_GET_OPERATION_ID, filter, null, dataControllerRequest);
			StatusEnum.success.setStatus(getCustomerApplicationData);
			return getCustomerApplicationData;
		} catch (Exception ex) {
			LOG.error("ERROR getCustomerApplicationData :: " + ex);
		}
		return result;
	}

	private Result getCustomerData(DataControllerRequest dataControllerRequest, Result getCustomerApplicationData) {
		Result result = StatusEnum.error.setStatus();
		try {
			Map<String, String> filter = new HashMap<>();
			// filter.put(DBPUtilitiesConstants.FILTER, "id" + DBPUtilitiesConstants.EQUAL +
			// CUSTOMER_ID);
			filter.put(DBPUtilitiesConstants.FILTER, "id" + DBPUtilitiesConstants.EQUAL
					+ HelperMethods.getFieldValue(getCustomerApplicationData, "Customer_id"));
			Result getCustomerData = ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, CUSTOMER_GET_OPERATION_ID,
					filter, null, dataControllerRequest);
			StatusEnum.success.setStatus(getCustomerData);
			PARTY_ID = HelperMethods.getFieldValue(getCustomerData, "partyId");
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
			Result getConsumerEnquiry = ServiceCaller.internal(SIMAH_SERVICE_ID, CONSUMER_ENQUIRY_OPERATION_ID,
					inputParams, null, dataControllerRequest);
			String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
			String outputResponse = ResultToJSON.convert(getConsumerEnquiry);
			auditLogData(dataControllerRequest, inputRequest, outputResponse,
					SIMAH_SERVICE_ID + " : " + CONSUMER_ENQUIRY_OPERATION_ID);

			String optstatus = getConsumerEnquiry.getParamValueByName("opstatus");
			String httpstatusCode = getConsumerEnquiry.getParamValueByName("httpStatusCode");
			if (httpstatusCode.equals("200") && optstatus.equals("0")) {
				StatusEnum.success.setStatus(getConsumerEnquiry);
				return getConsumerEnquiry;
			} else {
				result = StatusEnum.error.setStatus();
				return result;
			}
		} catch (Exception ex) {
			LOG.error("ERROR getSIMAHConsumerEnquiry :: " + ex);
		}
		return result;
	}

	private Result getSIMAHSalaryCertificate(Map<String, String> inputParams,
			DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Result getSalaryCertificate = ServiceCaller.internal(SIMAH_SALARY_ORCH_SERVICE_ID,
					SIMAH_SALARY_CERT_OPERATION_ID, inputParams, null, dataControllerRequest);
			StatusEnum.success.setStatus(getSalaryCertificate);
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
			// inputParams.put("nin", NATIONAL_ID);
			inputParams.put("nin", dataControllerRequest.getParameter("NationalID"));
			inputParams.put("dateOfBirth", DOB);
			LOG.error("getNationalAddress input param " + inputParams);
			Result getNationalAddress = ServiceCaller.internal(YAKEEN_SOAP_API_SERVICE_ID,
					GET_CITIZEN_ADDRESS_INFO_OPERATION_ID, inputParams, null, dataControllerRequest);
			StatusEnum.success.setStatus(getNationalAddress);
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
			Result getScoreCardS2 = ServiceCaller.internal(KNOCKOUT_SERVICE_ID, CALCULATE_SCORECARD_S2_OPERATION_ID,
					inputParams, null, dataControllerRequest);
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
			Result getScoreCardS3 = ServiceCaller.internal(KNOCKOUT_SERVICE_ID, CALCULATE_SCORECARD_S3_OPERATION_ID,
					inputParams, null, dataControllerRequest);
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
			Map<String, String> filter = new HashMap<>();
			filter.put(DBPUtilitiesConstants.FILTER,
					"User_id" + DBPUtilitiesConstants.EQUAL + inputParams.get("User_id"));
			Result getCustomerAddress;
			getCustomerAddress = ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID,
					TBL_CUSTOMER_ADDRESS_GET_OPERATION_ID, filter, null, dataControllerRequest);

			if (HelperMethods.hasRecords(getCustomerAddress)) {
				inputParams.put("id", HelperMethods.getFieldValue(getCustomerAddress, "id"));
				ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, TBL_CUSTOMER_ADDRESS_UPDATE_OPERATION_ID,
						inputParams, null, dataControllerRequest);
			} else {
				ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, TBL_CUSTOMER_ADDRESS_CREATE_OPERATION_ID,
						inputParams, null, dataControllerRequest);
			}
		} catch (Exception ex) {
			LOG.error("ERROR createCustomerAddress :: " + ex);
		}
	}

	private void createEmployerDetails(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
		try {
			Map<String, String> filter = new HashMap<>();
			filter.put(DBPUtilitiesConstants.FILTER,
					"nationalid" + DBPUtilitiesConstants.EQUAL + inputParams.get("nationalid"));
			Result getEmployerDetails;
			getEmployerDetails = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
					TBL_EMPLOYER_DETAILS_GET_OPERATION_ID, filter, null, dataControllerRequest);

			if (HelperMethods.hasRecords(getEmployerDetails)) {
				inputParams.put("id", HelperMethods.getFieldValue(getEmployerDetails, "id"));
				ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, TBL_EMPLOYER_DETAILS_UPDATE_OPERATION_ID,
						inputParams, null, dataControllerRequest);
			} else {
				ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, TBL_EMPLOYER_DETAILS_CREATE_OPERATION_ID,
						inputParams, null, dataControllerRequest);

			}
		} catch (Exception ex) {
			LOG.error("ERROR createEmployerDetails :: " + ex);
		}
	}

	private Result updateCustomerApplicationData(Map<String, String> inputParams,
			DataControllerRequest dataControllerRequest, String knockOutStage, String failureReason) {
		Result updateCustomerApplicationData = StatusEnum.success.setStatus();
		updateCustomerApplicationData.appendResult(ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
				CUSTOMER_APPLICATION_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest));

		if (inputParams.get("knockoutStatus").equalsIgnoreCase("FAIL")
				|| inputParams.get("applicationStatus").equalsIgnoreCase("SID_SUSPENDED")) {
			updateCustomerApplicationData = StatusEnum.error.setStatus();
			IjarahErrors.ERR_660028.setErrorCode(updateCustomerApplicationData);
			customerBlockingDBCall(dataControllerRequest.getParameter("NationalID"), dataControllerRequest.getParameter("ApplicationID"), knockOutStage,
					failureReason, dataControllerRequest);
		}
		return updateCustomerApplicationData;
	}

	private void customerBlockingDBCall(String nationalId, String applicationID, String scoreStage,
			String FailureReason, DataControllerRequest dataControllerRequest) {
		try {
			Map<String, String> inputParams = new HashMap<>();
		    inputParams.put("nationalid", nationalId);
		    inputParams.put("applicationid", applicationID);
		    inputParams.put("scorestage", scoreStage);
		    inputParams.put("failurereason", FailureReason);
			ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, SP_CREATE_UPDATE_CUSTOMER_BLOCKING_OPERATION_ID,
					inputParams, null, dataControllerRequest);
		} catch (Exception ex) {
			LOG.error("ERROR customerBlockingDBCall :: " + ex);
		}
	}

	private Map<String, String> createRequestForMISReportDBCall(String status, double maxEMI, double customerGblDti,
			double customerInternalDti, int calMAXGblDti, int calMAXinternalDti, DataControllerRequest request) {

		Map<String, String> inputParam = new HashMap<>();
		inputParam.put("applicationID", request.getParameter("ApplicationID"));
		inputParam.put("nationalId", request.getParameter("NationalID"));
		inputParam.put("knockoutStatusS2", "PASS");
		inputParam.put("knockoutStatusS3", status);
		inputParam.put("maxEmi", String.valueOf(maxEMI));
		inputParam.put("customerGlobalDti", String.valueOf(customerGblDti));
		inputParam.put("customerInternalDti", String.valueOf(customerInternalDti));
		inputParam.put("maxInternalDti", String.valueOf(calMAXinternalDti));
		inputParam.put("maxGlobalDti", String.valueOf(calMAXGblDti));
		inputParam.put("bouncedChequeDetails", calculateBouncedCheque());
		inputParam.put("courtJudgementDetails", calculateCourtJudgement());
		inputParam.put("currentDelinquency", CURRENT_DELINQUENCY);
		inputParam.put("maxDelinquency", MAX_DELINQUENCY);
		inputParam.put("products", String.valueOf(PRODUCT_DETAILS));
		inputParam.put("summary", String.valueOf(SUMMARY_DETAILS));
		inputParam.put("financialDefaultAmount", calculateFinancialDefaultAmount());
		inputParam.put("valid_util", calculateNonFinancialDefaultAmount());
		
		inputParam.put("lengthOfService", CURRENT_LENGTH_OF_SERVICE);
		inputParam.put("temporaryLiftInSalary", MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY);
		inputParam.put("newToIndustry", NEW_TO_INDUSTRY);
		inputParam.put("allowance", String.valueOf(ALLOWANCE_DETAILS));
		inputParam.put("pensioner", PENSIONER);
		inputParam.put("score", SC_SCORE);
		inputParam.put("ci_installment", String.valueOf(INSTALLMENT_AMOUNT));
		inputParam.put("ci_limit", String.valueOf(LIMIT_AMOUNT));
		inputParam.put("ci_summary", String.valueOf(CI_SUMMARY));

		LOG.error("createRequestForMISReportDBCall :: " + inputParam);

		return inputParam;
	}

	private Map<String, String> createRequestForMISReportDBCallSCorecard2(DataControllerRequest request,
			String status, String employerCategory) {

		Map<String, String> inputParam = new HashMap<>();
		inputParam.put("applicationID", request.getParameter("ApplicationID"));
		inputParam.put("nationalId", request.getParameter("NationalID"));
		inputParam.put("knockoutStatusS2", status);
		inputParam.put("lengthOfService", CURRENT_LENGTH_OF_SERVICE);
		inputParam.put("temporaryLiftInSalary", MANAGING_SEASONAL_AND_TEMPORARY_LIFT_IN_SALARY);
		inputParam.put("newToIndustry", NEW_TO_INDUSTRY);
		inputParam.put("allowance", String.valueOf(ALLOWANCE_DETAILS));
		inputParam.put("pensioner", PENSIONER);
		inputParam.put("category", EMPLOYER_CATEGORISATION);
		inputParam.put("ci_installment", String.valueOf(INSTALLMENT_AMOUNT));
		inputParam.put("ci_limit", String.valueOf(LIMIT_AMOUNT));
		inputParam.put("ci_summary", String.valueOf(CI_SUMMARY));
		inputParam.put("employer_category", employerCategory);

		LOG.error("createRequestForMISReportDBCallSCorecard2 :: " + inputParam);
		return inputParam;
	}

	private void saveMISReportData(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
		try {
			LOG.error("ERROR saveMISReportData :: ");
			ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, MIS_REPORT_UPDATE_OPERATION_ID, inputParams, null,
					dataControllerRequest);
		} catch (Exception ex) {
			LOG.error("saveMISReportData :: " + ex);
		}
	}
}