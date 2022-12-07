package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class SUMMARY{

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

	public String getCNTGCI(){
		return cNTGCI;
	}

	public String getTOTLIAB(){
		return tOTLIAB;
	}

	public String getCNTMTDE(){
		return cNTMTDE;
	}

	public String getCURDB(){
		return cURDB;
	}

	public String getCNTDEF(){
		return cNTDEF;
	}

	public String getTOTDEF(){
		return tOTDEF;
	}

	public String getTOTGLIAB(){
		return tOTGLIAB;
	}

	public String getCNTCI(){
		return cNTCI;
	}

	public String getCNTPE(){
		return cNTPE;
	}

	public String getTOTLIM(){
		return tOTLIM;
	}

	public String getTOTGLIM(){
		return tOTGLIM;
	}

	public String getEIID(){
		return eIID;
	}
}