package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class DEFAULTItem{

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

	public void setDFCRDTR(String dFCRDTR){
		this.dFCRDTR = dFCRDTR;
	}

	public String getDFCRDTR(){
		return dFCRDTR;
	}

	public void setDFCAPL(String dFCAPL){
		this.dFCAPL = dFCAPL;
	}

	public String getDFCAPL(){
		return dFCAPL;
	}

	public void setDFCUB(String dFCUB){
		this.dFCUB = dFCUB;
	}

	public String getDFCUB(){
		return dFCUB;
	}

	public void setDFPRD(String dFPRD){
		this.dFPRD = dFPRD;
	}

	public String getDFPRD(){
		return dFPRD;
	}

	public void setDFACCNO(String dFACCNO){
		this.dFACCNO = dFACCNO;
	}

	public String getDFACCNO(){
		return dFACCNO;
	}

	public void setDFSETTLDDATE(String dFSETTLDDATE){
		this.dFSETTLDDATE = dFSETTLDDATE;
	}

	public String getDFSETTLDDATE(){
		return dFSETTLDDATE;
	}

	public void setDFLOADDT(String dFLOADDT){
		this.dFLOADDT = dFLOADDT;
	}

	public String getDFLOADDT(){
		return dFLOADDT;
	}

	public void setDFORIGAMT(String dFORIGAMT){
		this.dFORIGAMT = dFORIGAMT;
	}

	public String getDFORIGAMT(){
		return dFORIGAMT;
	}

	public void setDFSTAT(String dFSTAT){
		this.dFSTAT = dFSTAT;
	}

	public String getDFSTAT(){
		return dFSTAT;
	}

	@Override
 	public String toString(){
		return 
			"DEFAULTItem{" + 
			"dF_CRDTR = '" + dFCRDTR + '\'' + 
			",dF_CAPL = '" + dFCAPL + '\'' + 
			",dF_CUB = '" + dFCUB + '\'' + 
			",dF_PRD = '" + dFPRD + '\'' + 
			",dF_ACC_NO = '" + dFACCNO + '\'' + 
			",dF_SETTLD_DATE = '" + dFSETTLDDATE + '\'' + 
			",dF_LOAD_DT = '" + dFLOADDT + '\'' + 
			",dF_ORIG_AMT = '" + dFORIGAMT + '\'' + 
			",dF_STAT = '" + dFSTAT + '\'' + 
			"}";
		}
}