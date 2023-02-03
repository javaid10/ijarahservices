package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class BOUNCEDCHECKSItem {

	@SerializedName("BC_CHECK_NO")
	private String bCCHECKNO;

	@SerializedName("BC_STAT")
	private String bCSTAT;

	@SerializedName("BC_CRDTR")
	private String bCCRDTR;

	@SerializedName("BC_PRD")
	private String bCPRD;

	@SerializedName("BC_ORIG_AMT")
	private String bCORIGAMT;

	@SerializedName("BC_CUB")
	private String bCCUB;

	@SerializedName("BC_SETTLD_DATE")
	private String bCSETTLDDATE;

	@SerializedName("BC_LOAD_DT")
	private String bCLOADDT;

	public String getBCCHECKNO(){
		return bCCHECKNO;
	}

	public String getBCSTAT(){
		return bCSTAT;
	}

	public String getBCCRDTR(){
		return bCCRDTR;
	}

	public String getBCPRD(){
		return bCPRD;
	}

	public String getBCORIGAMT(){
		return bCORIGAMT;
	}

	public String getBCCUB(){
		return bCCUB;
	}

	public String getBCSETTLDDATE(){
		return bCSETTLDDATE;
	}

	public String getBCLOADDT(){
		return bCLOADDT;
	}
}