package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class JUDGEMENTSItem {

	@SerializedName("EJ_ORIG_CLAIM_AMT")
	private String eJORIGCLAIMAMT;

	@SerializedName("EJ_EXEC_TYPE")
	private String eJEXECTYPE;

	@SerializedName("EJ_STATUS")
	private String eJSTATUS;

	@SerializedName("EJ_SETTLE_DATE")
	private String eJSETTLEDATE;

	@SerializedName("EJ_ENFORCE_DATE")
	private String eJENFORCEDATE;

	@SerializedName("EJ_DATE_LOADED")
	private String eJDATELOADED;

	@SerializedName("EJ_CITY")
	private String eJCITY;

	@SerializedName("EJ_CLAIM_AMT")
	private String eJCLAIMAMT;

	@SerializedName("EJ_RES_NUMBER")
	private String eJRESNUMBER;

	@SerializedName("EJ_CASE_NUMBER")
	private String eJCASENUMBER;

	@SerializedName("EJ_COURT_CODE")
	private String eJCOURTCODE;

	public String getEJORIGCLAIMAMT(){
		return eJORIGCLAIMAMT;
	}

	public String getEJEXECTYPE(){
		return eJEXECTYPE;
	}

	public String getEJSTATUS(){
		return eJSTATUS;
	}

	public String getEJSETTLEDATE(){
		return eJSETTLEDATE;
	}

	public String getEJENFORCEDATE(){
		return eJENFORCEDATE;
	}

	public String getEJDATELOADED(){
		return eJDATELOADED;
	}

	public String getEJCITY(){
		return eJCITY;
	}

	public String getEJCLAIMAMT(){
		return eJCLAIMAMT;
	}

	public String getEJRESNUMBER(){
		return eJRESNUMBER;
	}

	public String getEJCASENUMBER(){
		return eJCASENUMBER;
	}

	public String getEJCOURTCODE(){
		return eJCOURTCODE;
	}
}