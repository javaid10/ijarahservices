package com.ijarah.services;

import static com.ijarah.utils.ServiceCaller.auditLogData;
import static com.ijarah.utils.constants.OperationIDConstants.ACTIVATE_CUSTOMER_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.CUSTOMER_APPLICATION_GET_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.CUSTOMER_APPLICATION_UPDATE_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.CUSTOMER_GET_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.LOAN_CREATION_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.NAFAES_GET_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.SALE_ORDER_PUSH_METHOD_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.TRANSFER_ORDER_OPERATION_ID;
import static com.ijarah.utils.constants.OperationIDConstants.TRANSFER_ORDER_RESULT_OPERATION_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DBXDB_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.DB_MORA_SERVICES_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.MORA_T24_SERVICE_ID;
import static com.ijarah.utils.constants.ServiceIDConstants.NAFAES_REST_API_SERVICE_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijarah.utils.IjarahHelperMethods;
import com.ijarah.utils.ServiceCaller;
import com.ijarah.utils.enums.EnvironmentConfig;
import com.ijarah.utils.enums.IjarahErrors;
import com.ijarah.utils.enums.StatusEnum;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class CreateLoan implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(CreateLoan.class);
	private String SID_PRO_ACTIVE = "SID_PRO_ACTIVE";
	private String APPLICATION_STATUS = "applicationStatus";
	private String CSA_APPROVAL = "csaAppoRval";
	private String SANAD_APPROVAL = "sanadApproval";
	private String FIXED_AMOUNT_VALUE = "100";
	private String LOAN_CREATED = "LOAN_CREATED";

	@Override
	public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
			DataControllerResponse dataControllerResponse) throws Exception {
		LOG.debug("======> CreateLoan - Begin ");
		Result result = StatusEnum.error.setStatus();
		IjarahErrors.ERR_CREATE_LOAN_002.setErrorCode(result);

		Result getCustomerApplicationData = getCustomerApplicationData(dataControllerRequest);
		LOG.debug("======> Customer Application Data:  " + ResultToJSON.convert(getCustomerApplicationData));
		if (HelperMethods.hasRecords(getCustomerApplicationData)
				&& IjarahHelperMethods.hasSuccessCode(getCustomerApplicationData)) {
			Dataset customerApplicationData = extractValuesFromCustomerApplication(getCustomerApplicationData);
			for (int index = 0; index < customerApplicationData.getAllRecords().size(); index++) {
				Result getCustomerData = getPartyIDFromCustomerTable(
						customerApplicationData.getRecord(index).getParamValueByName("applicationID"),
						dataControllerRequest);
				LOG.debug("======> Customer Data:  " + ResultToJSON.convert(getCustomerData));
				if (IjarahHelperMethods.hasSuccessCode(getCustomerData) && HelperMethods.hasRecords(getCustomerData)) {
					Result getNafaesData = getNafaesData(getCustomerData, dataControllerRequest);
					LOG.debug("======> Nafaes Data:  " + ResultToJSON.convert(getNafaesData));
					if (IjarahHelperMethods.hasSuccessCode(getNafaesData) && HelperMethods.hasRecords(getNafaesData)) {
						Map<String, String> nafaesData = extractValuesFromNafaes(getNafaesData);

						Result transferOrder = new Result();
						if (!nafaesData.get("transferOrderStatus").equals("2")
								|| !nafaesData.get("transferOrderStatus").equals("1")) {
							transferOrder = callTransferOrder(nafaesData.get("accessToken"),
									nafaesData.get("referenceId"), dataControllerRequest);
						}
						LOG.debug("======> Transfer Order Result 1 " + ResultToJSON.convert(transferOrder));

						String transferOrderStatus = transferOrder.getParamValueByName("status");

						if (nafaesData.get("transferOrderStatus").equals("2")
								|| StringUtils.equalsAnyIgnoreCase("success", transferOrderStatus)) {
							Result transferOrderresult = callTransferOrderResult(nafaesData.get("accessToken"),
									nafaesData.get("referenceId"), dataControllerRequest);
							LOG.debug("======> Transfer Order Result 2 " + ResultToJSON.convert(transferOrderresult));
							String transferOrderResult_Status = transferOrderresult.getParamValueByName("status");
							if (!StringUtils.equalsAnyIgnoreCase("success", transferOrderResult_Status)) {
								// Updating the transfer Order status to 2 in Nafaes Table. So that it will pick
								// the record again in the next batch process
								updateTransferOrder(nafaesData.get("id"), "2");
								continue;
							}

							if (StringUtils.equalsAnyIgnoreCase("success", transferOrderResult_Status)) {
								Result saleOrder = callSaleOrder(nafaesData.get("accessToken"),
										nafaesData.get("referenceId"), dataControllerRequest);
								LOG.debug("======> Sale Order Result " + ResultToJSON.convert(saleOrder));
								String saleOrderResult_Status = saleOrder.getParamValueByName("status");
								if (StringUtils.equalsAnyIgnoreCase("success", saleOrderResult_Status)) {
									updateTransferOrderSellOrder(nafaesData.get("id"), "1", "1");
								}

								Result activateCustomer = activateCustomer(
										createInputParamsForActivateCustomerService(getCustomerData),
										dataControllerRequest);
								LOG.debug("======> Activate Customer: " + ResultToJSON.convert(activateCustomer));
								if (!IjarahHelperMethods.hasSuccessCode(activateCustomer)) {
									IjarahErrors.ERR_ACTIVATE_CUSTOMER_FAILED_005.setErrorCode(result);
									continue;
								}

								Map<String, String> createLoanInputParams = createInputParamsForCreateLoanService(
										customerApplicationData, index, getCustomerData);
								Result createLoanResult = createLoan(createLoanInputParams, dataControllerRequest);
								LOG.debug("======> Create Loan: " + ResultToJSON.convert(createLoanResult));
								if (IjarahHelperMethods.hasSuccessCode(createLoanResult)) {
									Map<String, String> inputParam = new HashMap<>();
									inputParam.put("id",
											customerApplicationData.getRecord(index).getParamValueByName("id"));
									inputParam.put(APPLICATION_STATUS, LOAN_CREATED);
									updateCustomerApplicationData(inputParam, dataControllerRequest);
								} else {
									IjarahErrors.ERR_LOAN_CREATION_FAILED_006.setErrorCode(result);
								}
							}
						}
					} else {
						IjarahErrors.ERR_NAFAES_DATA_NOT_FOUND_007.setErrorCode(result);
					}
				} else {
					IjarahErrors.ERR_NO_CUSTOMER_RECORD_FOUND_004.setErrorCode(result);
				}
			}
		} else {
			IjarahErrors.ERR_CUSTOMER_APPLICATION_DATA_NOT_FOUND_009.setErrorCode(result);
		}
		LOG.debug("======> CreateLoan - End ");
		return result;
	}

	/**
	 * 
	 * @param nafaesId
	 * @param transferorder
	 */
	private void updateTransferOrder(String nafaesId, String transferorder) {
		Map<String, Object> userInputs = new HashMap<>();
		userInputs.put("id", nafaesId);
		userInputs.put("transferorder", transferorder);
		try {
			String updateCustomerResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
					.withOperationId("dbxdb_nafaes_update").withRequestParameters(userInputs).build().getResponse();
			LOG.debug("======> Customer Update Response" + updateCustomerResponse);
		} catch (DBPApplicationException e) {
		}
	}

	/**
	 * 
	 * @param nafaesId
	 * @param transferorder
	 * @param sellOrder
	 */
	private void updateTransferOrderSellOrder(String nafaesId, String transferorder, String sellOrder) {
		Map<String, Object> userInputs = new HashMap<>();
		userInputs.put("id", nafaesId);
		userInputs.put("transferorder", transferorder);
		userInputs.put("sellorder", sellOrder);
		try {
			String updateCustomerResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
					.withOperationId("dbxdb_nafaes_update").withRequestParameters(userInputs).build().getResponse();
			LOG.debug("======> Customer Update Response" + updateCustomerResponse);
		} catch (DBPApplicationException e) {
		}
	}

	/**
	 * 
	 * @param accessToken
	 * @param referenceId
	 * @param dataControllerRequest
	 * @return
	 */
	private Result callTransferOrderResult(String accessToken, String referenceId,
			DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Map<String, String> inputParam = new HashMap<>();
			inputParam.put("uuid", IjarahHelperMethods.generateUUID() + "-TOR");
			inputParam.put("accessToken", accessToken);
			inputParam.put("referenceNo", referenceId);
			inputParam.put("orderType", "TO");
			inputParam.put("lng", "2");
			Result transferOrderResult = ServiceCaller.internal(NAFAES_REST_API_SERVICE_ID,
					TRANSFER_ORDER_RESULT_OPERATION_ID, inputParam, null, dataControllerRequest);
			String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
			String outputResponse = ResultToJSON.convert(transferOrderResult);
			auditLogData(dataControllerRequest, inputRequest, outputResponse,
					NAFAES_REST_API_SERVICE_ID + " : " + TRANSFER_ORDER_RESULT_OPERATION_ID);
			if (IjarahHelperMethods.hasSuccessStatus(transferOrderResult)) {
				StatusEnum.success.setStatus(transferOrderResult);
				return transferOrderResult;
			}
		} catch (Exception ex) {
			LOG.error("ERROR callTransferOrder :: " + ex);
		}
		return result;
	}

	/**
	 * 
	 * @param accessToken
	 * @param referenceId
	 * @param dataControllerRequest
	 * @return
	 */
	private Result callTransferOrder(String accessToken, String referenceId,
			DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Map<String, String> inputParam = new HashMap<>();
			inputParam.put("uuid", IjarahHelperMethods.generateUUID() + "-TO");
			inputParam.put("accessToken", accessToken);
			inputParam.put("referenceNo", referenceId);
			inputParam.put("orderType", "TO");
			inputParam.put("lng", "2");
			Result transferOrderResult = ServiceCaller.internal(NAFAES_REST_API_SERVICE_ID, TRANSFER_ORDER_OPERATION_ID,
					inputParam, null, dataControllerRequest);
			String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
			String outputResponse = ResultToJSON.convert(transferOrderResult);
			auditLogData(dataControllerRequest, inputRequest, outputResponse,
					NAFAES_REST_API_SERVICE_ID + " : " + TRANSFER_ORDER_OPERATION_ID);
			if (IjarahHelperMethods.hasSuccessStatus(transferOrderResult)) {
				StatusEnum.success.setStatus(transferOrderResult);
				return transferOrderResult;
			}
		} catch (Exception ex) {
			LOG.error("ERROR callTransferOrder :: " + ex);
		}
		return result;
	}

	/**
	 * 
	 * @param accessToken
	 * @param referenceId
	 * @param dataControllerRequest
	 * @return
	 */
	private Result callSaleOrder(String accessToken, String referenceId, DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Map<String, String> inputParam = new HashMap<>();
			inputParam.put("uuid", IjarahHelperMethods.generateUUID() + "-SO");
			inputParam.put("accessToken", accessToken);
			inputParam.put("referenceNo", referenceId);
			inputParam.put("orderType", "SO");
			inputParam.put("lng", "2");
			Result saleOrderResult = ServiceCaller.internal(NAFAES_REST_API_SERVICE_ID,
					SALE_ORDER_PUSH_METHOD_OPERATION_ID, inputParam, null, dataControllerRequest);
			String inputRequest = (new ObjectMapper()).writeValueAsString(inputParam);
			String outputResponse = ResultToJSON.convert(saleOrderResult);
			auditLogData(dataControllerRequest, inputRequest, outputResponse,
					NAFAES_REST_API_SERVICE_ID + " : " + SALE_ORDER_PUSH_METHOD_OPERATION_ID);
			if (IjarahHelperMethods.hasSuccessStatus(saleOrderResult)) {
				StatusEnum.success.setStatus(saleOrderResult);
				return saleOrderResult;
			}
		} catch (Exception ex) {
			LOG.error("ERROR callSaleOrder :: " + ex);
		}
		return result;
	}

	/**
	 * 
	 * @param getCustomerData
	 * @param dataControllerRequest
	 * @return
	 */
	private Result getNafaesData(Result getCustomerData, DataControllerRequest dataControllerRequest) {
		Result getNafaesData = StatusEnum.error.setStatus();
		try {
			String currentAppId = HelperMethods.getFieldValue(getCustomerData, "currentAppId");
			Map<String, String> inputParam = new HashMap<>();
			inputParam.put(DBPUtilitiesConstants.FILTER, "applicationid" + DBPUtilitiesConstants.EQUAL + currentAppId);
			getNafaesData = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID, NAFAES_GET_OPERATION_ID, inputParam,
					null, dataControllerRequest);
			StatusEnum.success.setStatus(getNafaesData);
		} catch (Exception ex) {
			LOG.error("ERROR getNafaesData :: " + ex);
		}
		return getNafaesData;
	}

	public int gen() {
		Random r = new Random(System.currentTimeMillis());
		return 10000 + r.nextInt(20000);
	}

	private Map<String, String> createInputParamsForCreateLoanService(Dataset customerApplicationData, int index,
			Result getCustomerData) {
		Map<String, String> inputParams = new HashMap<>();
		try {
			inputParams.put("partyId",
					StringUtils.isNotBlank(HelperMethods.getFieldValue(getCustomerData, "partyId"))
							? HelperMethods.getFieldValue(getCustomerData, "partyId")
							: "");
			inputParams.put("fixedAmount", FIXED_AMOUNT_VALUE);

			inputParams.put("loanApr", customerApplicationData.getRecord(index).getParamValueByName("approx"));
			inputParams.put("amount",
					customerApplicationData.getRecord(index).getParamValueByName("offerAmount").replace(",", ""));
			inputParams.put("fixed", customerApplicationData.getRecord(index).getParamValueByName("loanRate"));
			inputParams.put("term", customerApplicationData.getRecord(index).getParamValueByName("tenor") + "M");
			inputParams.put("sabbNumber",
					StringUtils.isNotBlank(customerApplicationData.getRecord(index).getParamValueByName("sabbNumber"))
							? customerApplicationData.getRecord(index).getParamValueByName("sabbNumber")
							: "");
			String randomSanda = String.valueOf(gen());
			LOG.error("RandomNumber::::::::++++++====" + randomSanda);
			inputParams.put("sadadNumber",
					StringUtils.isNotBlank(customerApplicationData.getRecord(index).getParamValueByName("sadadNumber"))
							? customerApplicationData.getRecord(index).getParamValueByName("sadadNumber")
							: randomSanda);
			inputParams.put("sanadRef", randomSanda);
			inputParams.put("infIoanRef",
					customerApplicationData.getRecord(index).getParamValueByName("applicationID"));
			inputParams.put("mobileNumber", customerApplicationData.getRecord(index).getParamValueByName("mobile"));
			inputParams.put("effectiveDate",
					customerApplicationData.getRecord(index).getParamValueByName("effectiveDate"));
		} catch (Exception ex) {
			LOG.error("ERROR createInputParamsForCreateLoanService :: " + ex);
		}
		return inputParams;
	}

	private Result createLoan(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			LOG.error("createLoan PARTY_ID :: " + inputParams.get("partyId"));
			Result getCreateLoanResult = ServiceCaller.internal(MORA_T24_SERVICE_ID, LOAN_CREATION_OPERATION_ID,
					inputParams, null, dataControllerRequest);
			String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
			String outputResponse = ResultToJSON.convert(getCreateLoanResult);
			auditLogData(dataControllerRequest, inputRequest, outputResponse,
					MORA_T24_SERVICE_ID + " : " + LOAN_CREATION_OPERATION_ID);
			if (IjarahHelperMethods.hasSuccessStatus(getCreateLoanResult)) {
				StatusEnum.success.setStatus(getCreateLoanResult);
				String msg1 = "عزيزي العميل, تمت الموافقة على طلبكم " + inputParams.get("infIoanRef")
						+ ". سوف يتم إشعاركم حال تم إيداع مبلغ التمويل في حسابكم البنكي. شكراً لاختياركم مورا.";

				// String msg2 = " عزيزي العميل، , يمكنكم سداد دفعاتكم عن طريق خدمة سدادحساب
				// "+inputParams.get("sadadNumber") +" من خلال "مورا" (رمز 187)
				// او في الحساب المخصص لكم في بنك ساب "+inputParams.get("sabbNumber")+" مع تحيات
				// مورا للتمويل. ";

				String msg2 = "عزيزي العميل، يمكنكم سداد دفعاتكم عن طريق خدمة سداد حساب  "
						+ inputParams.get("sadadNumber")
						+ "من خلال \"مورا\" (رمز 187) او في الحساب المخصص لكم في بنك ساب  "
						+ inputParams.get("sabbNumber") + "مع تحيات مورا للتمويل.";
				// String msg1 = "Dear Customer,Your application
				// "+inputParams.get("infIoanRef")+" has beenapproved. You will be notified when
				// loanamount is disbursed to your bank account.Thank you for choosing Mora.";
				// String msg2 = "Dear Customer, You can make your payment through SADAD:
				// Account no. "+inputParams.get("sadadNumber") +" Through “Mora” (Biller Code
				// ###)Or in your allocated SABB Payment Account
				// "+inputParams.get("sabbNumber")+" Thank you for choosing Mora.";
				// TriggerNotification.sendMessage(getMessageBody(inputParams,
				// dataControllerRequest),inputParams.get("sadadNumber")
				// );
				TriggerNotification.sendMessage(msg1, inputParams.get("mobileNumber"));
				TriggerNotification.sendMessage(msg2, inputParams.get("mobileNumber"));

				return getCreateLoanResult;
			}
		} catch (Exception ex) {
			LOG.error("ERROR createLoan :: " + ex);
		}
		return result;
	}

	/**
	 * 
	 * @param inputParams
	 * @param dataControllerRequest
	 * @return
	 */
	private static String getMessageBody(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
		String message = EnvironmentConfig.CREATE_LOAN_MESSAGE_TEMPLATE.getValue(dataControllerRequest);
		LOG.error("======> Message Body Before parsing " + message);
		inputParams.put("#SADAD#", inputParams.get("sadadNumber"));
		inputParams.put("#SABB ACCOUNT#", inputParams.get("sabbNumber"));
		String messageBody = TriggerNotification.getJsonFromTemplate(message, inputParams);
		LOG.error("======> Message Body after parsing " + messageBody);
		return messageBody;
	}

	private Result activateCustomer(Map<String, String> inputParams, DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Result getActivateCustomerResult = ServiceCaller.internal(MORA_T24_SERVICE_ID,
					ACTIVATE_CUSTOMER_OPERATION_ID, inputParams, null, dataControllerRequest);
			String inputRequest = (new ObjectMapper()).writeValueAsString(inputParams);
			String outputResponse = ResultToJSON.convert(getActivateCustomerResult);
			auditLogData(dataControllerRequest, inputRequest, outputResponse,
					MORA_T24_SERVICE_ID + " : " + ACTIVATE_CUSTOMER_OPERATION_ID);
			if (IjarahHelperMethods.hasSuccessStatus(getActivateCustomerResult)) {
				StatusEnum.success.setStatus(getActivateCustomerResult);
				return getActivateCustomerResult;
			}
		} catch (Exception ex) {
			LOG.error("ERROR activateCustomer :: " + ex);
		}
		return result;
	}

	private Map<String, String> createInputParamsForActivateCustomerService(Result getCustomerData) {
		Map<String, String> inputParams = new HashMap<>();
		try {
			inputParams.put("partyId", HelperMethods.getFieldValue(getCustomerData, "partyId"));
		} catch (Exception ex) {
			LOG.error("ERROR createInputParamsForActivateCustomerService :: " + ex);
		}
		return inputParams;
	}

	/**
	 * 
	 * @param applicationID
	 * @param dataControllerRequest
	 * @return
	 */
	private Result getPartyIDFromCustomerTable(String applicationID, DataControllerRequest dataControllerRequest) {
		Result getCustomerData = StatusEnum.error.setStatus();
		try {
			LOG.error("======> Application Id: " + applicationID);
			Map<String, String> inputParam = new HashMap<>();
			inputParam.put(DBPUtilitiesConstants.FILTER, "currentAppId" + DBPUtilitiesConstants.EQUAL + applicationID);
			getCustomerData = ServiceCaller.internalDB(DBXDB_SERVICES_SERVICE_ID, CUSTOMER_GET_OPERATION_ID, inputParam,
					null, dataControllerRequest);
			StatusEnum.success.setStatus(getCustomerData);
		} catch (Exception ex) {
			LOG.error("======> Error while processing the getPartyIDFromCustomerTable: " + ex);
		}
		return getCustomerData;
	}

	/**
	 * 
	 * @param dataControllerRequest
	 * @return
	 */
	private Result getCustomerApplicationData(DataControllerRequest dataControllerRequest) {
		Result result = StatusEnum.error.setStatus();
		try {
			Map<String, String> filter = new HashMap<>();
			filter.put(DBPUtilitiesConstants.FILTER,
					APPLICATION_STATUS + DBPUtilitiesConstants.EQUAL + SID_PRO_ACTIVE + DBPUtilitiesConstants.AND
							+ CSA_APPROVAL + DBPUtilitiesConstants.EQUAL + DBPUtilitiesConstants.BOOLEAN_STRING_TRUE
							+ DBPUtilitiesConstants.AND + SANAD_APPROVAL + DBPUtilitiesConstants.EQUAL
							+ DBPUtilitiesConstants.BOOLEAN_STRING_TRUE);
			Result getCustomerApplicationData = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
					CUSTOMER_APPLICATION_GET_OPERATION_ID, filter, null, dataControllerRequest);
			StatusEnum.success.setStatus(getCustomerApplicationData);
			return getCustomerApplicationData;
		} catch (Exception ex) {
			LOG.error("======> Error while processing the getCustomerApplicationData : " + ex);
		}
		return result;
	}

	/**
	 * 
	 * @param getCustomerApplicationData
	 * @return
	 */
	private Dataset extractValuesFromCustomerApplication(Result getCustomerApplicationData) {
		Dataset customerApplicationData = new Dataset();
		try {
			customerApplicationData = getCustomerApplicationData.getDatasetById("tbl_customerapplication");

		} catch (Exception ex) {
			LOG.error("======> Error while processing extractValuesFromCustomerApplication : ", ex);
		}
		return customerApplicationData;
	}

	public static void main(String[] args) {
		String s = "{\"nafaes\":[{\"nationalid\":\"1071950487\",\"transferorder\":\"2\",\"referencenumber\":\"108503\",\"id\":\"f34e1bce-9f41-4d2b-b071-00cec2d59e99\",\"accessToken\":\"52ba6870-27f9-466e-8169-f0696a54d86b\",\"applicationid\":\"M0374149\",\"createdts\":\"2022-12-20 16:43:23.0\"}],\"opstatus\":0,\"httpStatusCode\":0}";
		JSONObject j = new JSONObject(s);

		Result r = JSONToResult.convert(s);
		try {
			CreateLoan c = new CreateLoan();
			Map<String, String> m = c.extractValuesFromNafaes(r);
			if (m.get("transferOrderStatus").equals("2")) {
				System.out.println(true);
			} else {
				System.out.println(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param getNafaesData
	 * @return
	 */
	private Map<String, String> extractValuesFromNafaes(Result getNafaesData) {
		Map<String, String> nafaesData = new HashMap<>();
		try {
			nafaesData.put("id", getNafaesData.getDatasetById("nafaes").getRecord(0).getParamValueByName("id"));
			nafaesData.put("accessToken",
					getNafaesData.getDatasetById("nafaes").getRecord(0).getParamValueByName("accessToken"));
			nafaesData.put("referenceId",
					getNafaesData.getDatasetById("nafaes").getRecord(0).getParamValueByName("referencenumber"));

			if (getNafaesData.getDatasetById("nafaes").getRecord(0).getParamValueByName("transferorder") == null) {
				nafaesData.put("transferOrderStatus", "0");
			} else {
				nafaesData.put("transferOrderStatus",
						getNafaesData.getDatasetById("nafaes").getRecord(0).getParamValueByName("transferorder"));
			}
		} catch (Exception ex) {
			LOG.error("======> Error while processing the Nafaes data:: ", ex);
		}
		return nafaesData;
	}

	/**
	 * 
	 * @param inputParams
	 * @param dataControllerRequest
	 */
	private void updateCustomerApplicationData(Map<String, String> inputParams,
			DataControllerRequest dataControllerRequest) {
		Result updateCustomerApplicationTable = ServiceCaller.internalDB(DB_MORA_SERVICES_SERVICE_ID,
				CUSTOMER_APPLICATION_UPDATE_OPERATION_ID, inputParams, null, dataControllerRequest);
		LOG.debug("======> Update Customer Application Table: " + ResultToJSON.convert(updateCustomerApplicationTable));
	}

	/**
	 * Converts the given String into the JSONObject
	 *
	 * @param jsonString
	 * @return
	 */
	public static JSONObject getStringAsJSONObject(String jsonString) {
		JSONObject generatedJSONObject = new JSONObject();
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		try {
			generatedJSONObject = new JSONObject(jsonString);
			return generatedJSONObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}