package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PREVENQUIRYItem{

	@SerializedName("PE_DATE")
	private String pEDATE;

	@SerializedName("PE_AMOUNT")
	private String pEAMOUNT;

	@SerializedName("PE_PRD")
	private String pEPRD;

	@SerializedName("PE_TYPE")
	private String pETYPE;

	@SerializedName("PE_INQR")
	private String pEINQR;

	@SerializedName("PE_MEMB_REF")
	private String pEMEMBREF;

	public void setPEDATE(String pEDATE){
		this.pEDATE = pEDATE;
	}

	public String getPEDATE(){
		return pEDATE;
	}

	public void setPEAMOUNT(String pEAMOUNT){
		this.pEAMOUNT = pEAMOUNT;
	}

	public String getPEAMOUNT(){
		return pEAMOUNT;
	}

	public void setPEPRD(String pEPRD){
		this.pEPRD = pEPRD;
	}

	public String getPEPRD(){
		return pEPRD;
	}
/*
	public void setPENAME(List<PENAMEItem> pENAME){
		this.pENAME = pENAME;
	}

	public List<PENAMEItem> getPENAME(){
		return pENAME;
	}
	
 */

	public void setPETYPE(String pETYPE){
		this.pETYPE = pETYPE;
	}

	public String getPETYPE(){
		return pETYPE;
	}

	public void setPEINQR(String pEINQR){
		this.pEINQR = pEINQR;
	}

	public String getPEINQR(){
		return pEINQR;
	}

	public void setPEMEMBREF(String pEMEMBREF){
		this.pEMEMBREF = pEMEMBREF;
	}

	public String getPEMEMBREF(){
		return pEMEMBREF;
	}

	@Override
 	public String toString(){
		return 
			"PREVENQUIRYItem{" + 
			"pE_DATE = '" + pEDATE + '\'' + 
			",pE_AMOUNT = '" + pEAMOUNT + '\'' + 
			",pE_PRD = '" + pEPRD + '\'' + 
			/* ",pE_NAME = '" + pENAME + '\'' +  */
			",pE_TYPE = '" + pETYPE + '\'' + 
			",pE_INQR = '" + pEINQR + '\'' + 
			",pE_MEMB_REF = '" + pEMEMBREF + '\'' + 
			"}";
		}
}