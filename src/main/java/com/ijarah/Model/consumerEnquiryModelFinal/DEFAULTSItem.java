package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class DEFAULTSItem{

	@SerializedName("DF_CRDTR")
	private String dFCRDTR;

	@SerializedName("DF_CAPL")
	private String dFCAPL;

	@SerializedName("DF_CUB")
	private String dFCUB;

	@SerializedName("DF_PRD")
	private String dFPRD;

	@SerializedName("DF_ACC_NO")
	private String dFACCNO;

	@SerializedName("DF_SETTLD_DATE")
	private String dFSETTLDDATE;

	@SerializedName("DF_LOAD_DT")
	private String dFLOADDT;

	@SerializedName("DF_ORIG_AMT")
	private String dFORIGAMT;

	@SerializedName("DF_STAT")
	private String dFSTAT;

	public String getDFCRDTR(){
		return dFCRDTR;
	}

	public String getDFCAPL(){
		return dFCAPL;
	}

	public String getDFCUB(){
		return dFCUB;
	}

	public String getDFPRD(){
		return dFPRD;
	}

	public String getDFACCNO(){
		return dFACCNO;
	}

	public String getDFSETTLDDATE(){
		return dFSETTLDDATE;
	}

	public String getDFLOADDT(){
		return dFLOADDT;
	}

	public String getDFORIGAMT(){
		return dFORIGAMT;
	}

	public String getDFSTAT(){
		return dFSTAT;
	}
}