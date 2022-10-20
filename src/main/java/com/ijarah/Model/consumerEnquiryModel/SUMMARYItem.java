package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class SUMMARYItem{

	@SerializedName("CNT_GCI")
	private String cNTGCI;

	@SerializedName("TOT_LIAB")
	private String tOTLIAB;

	@SerializedName("CNT_MTDE")
	private String cNTMTDE;

	@SerializedName("CUR_DB")
	private String cURDB;

	@SerializedName("CNT_DEF")
	private String cNTDEF;

	@SerializedName("TOT_DEF")
	private String tOTDEF;

	@SerializedName("TOT_GLIAB")
	private String tOTGLIAB;

	@SerializedName("CNT_CI")
	private String cNTCI;

	@SerializedName("CNT_PE")
	private String cNTPE;

	@SerializedName("TOT_LIM")
	private String tOTLIM;

	@SerializedName("TOT_GLIM")
	private String tOTGLIM;

	@SerializedName("EIID")
	private String eIID;

	public void setCNTGCI(String cNTGCI){
		this.cNTGCI = cNTGCI;
	}

	public String getCNTGCI(){
		return cNTGCI;
	}

	public void setTOTLIAB(String tOTLIAB){
		this.tOTLIAB = tOTLIAB;
	}

	public String getTOTLIAB(){
		return tOTLIAB;
	}

	public void setCNTMTDE(String cNTMTDE){
		this.cNTMTDE = cNTMTDE;
	}

	public String getCNTMTDE(){
		return cNTMTDE;
	}

	public void setCURDB(String cURDB){
		this.cURDB = cURDB;
	}

	public String getCURDB(){
		return cURDB;
	}

	public void setCNTDEF(String cNTDEF){
		this.cNTDEF = cNTDEF;
	}

	public String getCNTDEF(){
		return cNTDEF;
	}

	public void setTOTDEF(String tOTDEF){
		this.tOTDEF = tOTDEF;
	}

	public String getTOTDEF(){
		return tOTDEF;
	}

	public void setTOTGLIAB(String tOTGLIAB){
		this.tOTGLIAB = tOTGLIAB;
	}

	public String getTOTGLIAB(){
		return tOTGLIAB;
	}

	public void setCNTCI(String cNTCI){
		this.cNTCI = cNTCI;
	}

	public String getCNTCI(){
		return cNTCI;
	}

	public void setCNTPE(String cNTPE){
		this.cNTPE = cNTPE;
	}

	public String getCNTPE(){
		return cNTPE;
	}

	public void setTOTLIM(String tOTLIM){
		this.tOTLIM = tOTLIM;
	}

	public String getTOTLIM(){
		return tOTLIM;
	}

	public void setTOTGLIM(String tOTGLIM){
		this.tOTGLIM = tOTGLIM;
	}

	public String getTOTGLIM(){
		return tOTGLIM;
	}

	public void setEIID(String eIID){
		this.eIID = eIID;
	}

	public String getEIID(){
		return eIID;
	}

	@Override
 	public String toString(){
		return 
			"SUMMARYItem{" + 
			"cNT_GCI = '" + cNTGCI + '\'' + 
			",tOT_LIAB = '" + tOTLIAB + '\'' + 
			",cNT_MTDE = '" + cNTMTDE + '\'' + 
			",cUR_DB = '" + cURDB + '\'' + 
			",cNT_DEF = '" + cNTDEF + '\'' + 
			",tOT_DEF = '" + tOTDEF + '\'' + 
			",tOT_GLIAB = '" + tOTGLIAB + '\'' + 
			",cNT_CI = '" + cNTCI + '\'' + 
			",cNT_PE = '" + cNTPE + '\'' + 
			",tOT_LIM = '" + tOTLIM + '\'' + 
			",tOT_GLIM = '" + tOTGLIM + '\'' + 
			",eIID = '" + eIID + '\'' + 
			"}";
		}
}