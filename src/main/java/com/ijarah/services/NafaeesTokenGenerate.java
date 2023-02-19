package com.ijarah.services;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.ijarah.utils.HTTPOperations;
import com.ijarah.utils.enums.EnvironmentConfig;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class NafaeesTokenGenerate implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(NafaeesTokenGenerate.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		// TODO Auto-generated method stub

		Result result = new Result();
		
		String nafaesToken = getAccessToken(request);
		LOG.debug("==========> nafaesToken - Begin = " + nafaesToken);
		
		if(saveTokenIntoDB(nafaesToken)) {
			result.addParam("messgae", "success");
		}else
			result.addParam("message", "fail");
			
		
		
		return result;
	}

	private static String getAccessToken(DataControllerRequest dataControllerRequest) {
		LOG.debug("==========> Nafaes - excuteLogin - Begin");
		String authToken = null;

		String loginURL = EnvironmentConfig.CUSTOM_NAFAES_URL.getValue(dataControllerRequest)
				+ "oauth/token?grant_type=password" + "&username="
				+ EnvironmentConfig.NAFAES_USERNAME.getValue(dataControllerRequest) + "&client_id="
				+ EnvironmentConfig.NAFAES_CLIENT_ID.getValue(dataControllerRequest);
		LOG.debug("==========> Login URL  :: " + loginURL);
		HashMap<String, String> paramsMap = new HashMap<>();
		paramsMap.put("password", EnvironmentConfig.NAFAES_PASSWORD.getValue(dataControllerRequest));
		paramsMap.put("client_secret", EnvironmentConfig.NAFAES_CLIENT_SECRET.getValue(dataControllerRequest));

		HashMap<String, String> headersMap = new HashMap<String, String>();

		String endPointResponse = HTTPOperations.hitPOSTServiceAndGetResponse(loginURL, paramsMap, null, headersMap);
		JSONObject responseJson = getStringAsJSONObject(endPointResponse);
		LOG.debug("==========> responseJson :: " + responseJson);
		authToken = responseJson.getString("access_token");
		LOG.debug("==========> authToken :: " + authToken);
		LOG.debug("==========> Nafaes - excuteLogin - End");
		return authToken;
	}

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

	private boolean saveTokenIntoDB(String token) {

		boolean check = false;

		HashMap<String, Object> inpUpdate = new HashMap();
		inpUpdate.put("id", "1");
		inpUpdate.put("token", token);

		try {
			String res = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
					.withOperationId("dbxdb_nafaesToken_create").withRequestParameters(inpUpdate).build().getResponse();

			JSONObject jsonObject = new JSONObject(res);

			if (jsonObject != null && jsonObject.optJSONArray("nafaesToken").length() > 0)
				check = true;

		} catch (DBPApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return check;
	}

}
