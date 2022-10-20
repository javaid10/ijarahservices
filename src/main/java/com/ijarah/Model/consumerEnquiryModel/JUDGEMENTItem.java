package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class JUDGEMENTItem{

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

	public void setEJORIGCLAIMAMT(String eJORIGCLAIMAMT){
		this.eJORIGCLAIMAMT = eJORIGCLAIMAMT;
	}

	public String getEJORIGCLAIMAMT(){
		return eJORIGCLAIMAMT;
	}

	public void setEJEXECTYPE(String eJEXECTYPE){
		this.eJEXECTYPE = eJEXECTYPE;
	}

	public String getEJEXECTYPE(){
		return eJEXECTYPE;
	}

	public void setEJSTATUS(String eJSTATUS){
		this.eJSTATUS = eJSTATUS;
	}

	public String getEJSTATUS(){
		return eJSTATUS;
	}

	public void setEJSETTLEDATE(String eJSETTLEDATE){
		this.eJSETTLEDATE = eJSETTLEDATE;
	}

	public String getEJSETTLEDATE(){
		return eJSETTLEDATE;
	}

	public void setEJENFORCEDATE(String eJENFORCEDATE){
		this.eJENFORCEDATE = eJENFORCEDATE;
	}

	public String getEJENFORCEDATE(){
		return eJENFORCEDATE;
	}

	public void setEJDATELOADED(String eJDATELOADED){
		this.eJDATELOADED = eJDATELOADED;
	}

	public String getEJDATELOADED(){
		return eJDATELOADED;
	}

	public void setEJCITY(String eJCITY){
		this.eJCITY = eJCITY;
	}

	public String getEJCITY(){
		return eJCITY;
	}

	public void setEJCLAIMAMT(String eJCLAIMAMT){
		this.eJCLAIMAMT = eJCLAIMAMT;
	}

	public String getEJCLAIMAMT(){
		return eJCLAIMAMT;
	}

	public void setEJRESNUMBER(String eJRESNUMBER){
		this.eJRESNUMBER = eJRESNUMBER;
	}

	public String getEJRESNUMBER(){
		return eJRESNUMBER;
	}

	public void setEJCASENUMBER(String eJCASENUMBER){
		this.eJCASENUMBER = eJCASENUMBER;
	}

	public String getEJCASENUMBER(){
		return eJCASENUMBER;
	}

	public void setEJCOURTCODE(String eJCOURTCODE){
		this.eJCOURTCODE = eJCOURTCODE;
	}

	public String getEJCOURTCODE(){
		return eJCOURTCODE;
	}

	@Override
 	public String toString(){
		return 
			"JUDGEMENTItem{" + 
			"eJ_ORIG_CLAIM_AMT = '" + eJORIGCLAIMAMT + '\'' + 
			",eJ_EXEC_TYPE = '" + eJEXECTYPE + '\'' + 
			",eJ_STATUS = '" + eJSTATUS + '\'' + 
			",eJ_SETTLE_DATE = '" + eJSETTLEDATE + '\'' + 
			",eJ_ENFORCE_DATE = '" + eJENFORCEDATE + '\'' + 
			",eJ_DATE_LOADED = '" + eJDATELOADED + '\'' + 
			",eJ_CITY = '" + eJCITY + '\'' + 
			",eJ_CLAIM_AMT = '" + eJCLAIMAMT + '\'' + 
			",eJ_RES_NUMBER = '" + eJRESNUMBER + '\'' + 
			",eJ_CASE_NUMBER = '" + eJCASENUMBER + '\'' + 
			",eJ_COURT_CODE = '" + eJCOURTCODE + '\'' + 
			"}";
		}
}