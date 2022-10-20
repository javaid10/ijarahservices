package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class CIDETAILItem{

	@SerializedName("CI_SAL")
	private String cISAL;

	@SerializedName("CI_LIMIT")
	private String cILIMIT;

	@SerializedName("CI_SUMMRY")
	private String cISUMMRY;

	@SerializedName("CI_ISSU_DT")
	private String cIISSUDT;

	@SerializedName("CI_STATUS")
	private String cISTATUS;

	@SerializedName("CI_FRQ")
	private String cIFRQ;

	@SerializedName("CI_LAST_AMT_PD")
	private String cILASTAMTPD;

	@SerializedName("CI_PRD")
	private String cIPRD;

	@SerializedName("CI_PROD_EXP_DT")
	private String cIPRODEXPDT;

	@SerializedName("CI_ACC_NO")
	private String cIACCNO;

	@SerializedName("CI_ODB")
	private String cIODB;

	@SerializedName("CI_CRDTR")
	private String cICRDTR;

	@SerializedName("CI_CUB")
	private String cICUB;

	@SerializedName("CI_CLSD_DT")
	private String cICLSDDT;

	@SerializedName("CI_LAST_PAY_DT")
	private String cILASTPAYDT;

	@SerializedName("CI_NXT_DU_DT")
	private String cINXTDUDT;

	@SerializedName("CI_INSTL")
	private String cIINSTL;

	@SerializedName("CI_TNR")
	private String cITNR;

	@SerializedName("CI_SEC")
	private String cISEC;

	@SerializedName("CI_AS_OF_DT")
	private String cIASOFDT;

	public void setCISAL(String cISAL){
		this.cISAL = cISAL;
	}

	public String getCISAL(){
		return cISAL;
	}

	public void setCILIMIT(String cILIMIT){
		this.cILIMIT = cILIMIT;
	}

	public String getCILIMIT(){
		return cILIMIT;
	}

	public void setCISUMMRY(String cISUMMRY){
		this.cISUMMRY = cISUMMRY;
	}

	public String getCISUMMRY(){
		return cISUMMRY;
	}

	public void setCIISSUDT(String cIISSUDT){
		this.cIISSUDT = cIISSUDT;
	}

	public String getCIISSUDT(){
		return cIISSUDT;
	}

	public void setCISTATUS(String cISTATUS){
		this.cISTATUS = cISTATUS;
	}

	public String getCISTATUS(){
		return cISTATUS;
	}

	public void setCIFRQ(String cIFRQ){
		this.cIFRQ = cIFRQ;
	}

	public String getCIFRQ(){
		return cIFRQ;
	}

	public void setCILASTAMTPD(String cILASTAMTPD){
		this.cILASTAMTPD = cILASTAMTPD;
	}

	public String getCILASTAMTPD(){
		return cILASTAMTPD;
	}

	public void setCIPRD(String cIPRD){
		this.cIPRD = cIPRD;
	}

	public String getCIPRD(){
		return cIPRD;
	}

	public void setCIPRODEXPDT(String cIPRODEXPDT){
		this.cIPRODEXPDT = cIPRODEXPDT;
	}

	public String getCIPRODEXPDT(){
		return cIPRODEXPDT;
	}

	public void setCIACCNO(String cIACCNO){
		this.cIACCNO = cIACCNO;
	}

	public String getCIACCNO(){
		return cIACCNO;
	}

	public void setCIODB(String cIODB){
		this.cIODB = cIODB;
	}

	public String getCIODB(){
		return cIODB;
	}

	public void setCICRDTR(String cICRDTR){
		this.cICRDTR = cICRDTR;
	}

	public String getCICRDTR(){
		return cICRDTR;
	}

	public void setCICUB(String cICUB){
		this.cICUB = cICUB;
	}

	public String getCICUB(){
		return cICUB;
	}

	public void setCICLSDDT(String cICLSDDT){
		this.cICLSDDT = cICLSDDT;
	}

	public String getCICLSDDT(){
		return cICLSDDT;
	}

	public void setCILASTPAYDT(String cILASTPAYDT){
		this.cILASTPAYDT = cILASTPAYDT;
	}

	public String getCILASTPAYDT(){
		return cILASTPAYDT;
	}

	public void setCINXTDUDT(String cINXTDUDT){
		this.cINXTDUDT = cINXTDUDT;
	}

	public String getCINXTDUDT(){
		return cINXTDUDT;
	}

	public void setCIINSTL(String cIINSTL){
		this.cIINSTL = cIINSTL;
	}

	public String getCIINSTL(){
		return cIINSTL;
	}

	public void setCITNR(String cITNR){
		this.cITNR = cITNR;
	}

	public String getCITNR(){
		return cITNR;
	}

	public void setCISEC(String cISEC){
		this.cISEC = cISEC;
	}

	public String getCISEC(){
		return cISEC;
	}

	public void setCIASOFDT(String cIASOFDT){
		this.cIASOFDT = cIASOFDT;
	}

	public String getCIASOFDT(){
		return cIASOFDT;
	}

	@Override
 	public String toString(){
		return 
			"CIDETAILItem{" + 
			"cI_SAL = '" + cISAL + '\'' + 
			",cI_LIMIT = '" + cILIMIT + '\'' + 
			",cI_SUMMRY = '" + cISUMMRY + '\'' + 
			",cI_ISSU_DT = '" + cIISSUDT + '\'' + 
			",cI_STATUS = '" + cISTATUS + '\'' + 
			",cI_FRQ = '" + cIFRQ + '\'' + 
			",cI_LAST_AMT_PD = '" + cILASTAMTPD + '\'' + 
			",cI_PRD = '" + cIPRD + '\'' + 
			",cI_PROD_EXP_DT = '" + cIPRODEXPDT + '\'' + 
			",cI_ACC_NO = '" + cIACCNO + '\'' + 
			",cI_ODB = '" + cIODB + '\'' + 
			",cI_CRDTR = '" + cICRDTR + '\'' + 
			",cI_CUB = '" + cICUB + '\'' + 
			",cI_CLSD_DT = '" + cICLSDDT + '\'' + 
			",cI_LAST_PAY_DT = '" + cILASTPAYDT + '\'' + 
			",cI_NXT_DU_DT = '" + cINXTDUDT + '\'' + 
			",cI_INSTL = '" + cIINSTL + '\'' + 
			",cI_TNR = '" + cITNR + '\'' + 
			",cI_SEC = '" + cISEC + '\'' + 
			",cI_AS_OF_DT = '" + cIASOFDT + '\'' + 
			"}";
		}
}