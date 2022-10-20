package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class BOUNCEDCHECKItem{

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

	public void setBCCHECKNO(String bCCHECKNO){
		this.bCCHECKNO = bCCHECKNO;
	}

	public String getBCCHECKNO(){
		return bCCHECKNO;
	}

	public void setBCSTAT(String bCSTAT){
		this.bCSTAT = bCSTAT;
	}

	public String getBCSTAT(){
		return bCSTAT;
	}

	public void setBCCRDTR(String bCCRDTR){
		this.bCCRDTR = bCCRDTR;
	}

	public String getBCCRDTR(){
		return bCCRDTR;
	}

	public void setBCPRD(String bCPRD){
		this.bCPRD = bCPRD;
	}

	public String getBCPRD(){
		return bCPRD;
	}

	public void setBCORIGAMT(String bCORIGAMT){
		this.bCORIGAMT = bCORIGAMT;
	}

	public String getBCORIGAMT(){
		return bCORIGAMT;
	}

	public void setBCCUB(String bCCUB){
		this.bCCUB = bCCUB;
	}

	public String getBCCUB(){
		return bCCUB;
	}

	public void setBCSETTLDDATE(String bCSETTLDDATE){
		this.bCSETTLDDATE = bCSETTLDDATE;
	}

	public String getBCSETTLDDATE(){
		return bCSETTLDDATE;
	}

	public void setBCLOADDT(String bCLOADDT){
		this.bCLOADDT = bCLOADDT;
	}

	public String getBCLOADDT(){
		return bCLOADDT;
	}

	@Override
 	public String toString(){
		return 
			"BOUNCEDCHECKItem{" + 
			"bC_CHECK_NO = '" + bCCHECKNO + '\'' + 
			",bC_STAT = '" + bCSTAT + '\'' + 
			",bC_CRDTR = '" + bCCRDTR + '\'' + 
			",bC_PRD = '" + bCPRD + '\'' + 
			",bC_ORIG_AMT = '" + bCORIGAMT + '\'' + 
			",bC_CUB = '" + bCCUB + '\'' + 
			",bC_SETTLD_DATE = '" + bCSETTLDDATE + '\'' + 
			",bC_LOAD_DT = '" + bCLOADDT + '\'' + 
			"}";
		}
}