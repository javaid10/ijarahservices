package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class PREVENQUIRIESItem{

	@SerializedName("PE_DATE")
	private String pEDATE;

	@SerializedName("PE_AMOUNT")
	private String pEAMOUNT;

	@SerializedName("PE_PRD")
	private String pEPRD;

	@SerializedName("PE_NMFA")
	private String pENMFA;

	@SerializedName("PE_NAME")
	private String pENAME;

	@SerializedName("PE_TYPE")
	private String pETYPE;

	@SerializedName("PE_NM1A")
	private String pENM1A;

	@SerializedName("PE_INQR")
	private String pEINQR;

	@SerializedName("PE_MEMB_REF")
	private String pEMEMBREF;

	public String getPEDATE(){
		return pEDATE;
	}

	public String getPEAMOUNT(){
		return pEAMOUNT;
	}

	public String getPEPRD(){
		return pEPRD;
	}

	public String getPENMFA(){
		return pENMFA;
	}

	public String getPENAME(){
		return pENAME;
	}

	public String getPETYPE(){
		return pETYPE;
	}

	public String getPENM1A(){
		return pENM1A;
	}

	public String getPEINQR(){
		return pEINQR;
	}

	public String getPEMEMBREF(){
		return pEMEMBREF;
	}
}