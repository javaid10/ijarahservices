package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class RSPREPORT{

	@SerializedName("PRODUCT_TYPE")
	private String pRODUCTTYPE;

	@SerializedName("ACCOUNT_TYPE")
	private String aCCOUNTTYPE;

	@SerializedName("REPORT_DATE")
	private String rEPORTDATE;

	@SerializedName("AMOUNT")
	private String aMOUNT;

	@SerializedName("ENQUIRY_NO")
	private String eNQUIRYNO;

	@SerializedName("MBR_TYPE")
	private String mBRTYPE;

	@SerializedName("ENQUIRY_TYPE")
	private String eNQUIRYTYPE;

	@SerializedName("ENQUIRY_REFERENCE")
	private String eNQUIRYREFERENCE;

	@SerializedName("FICOG4")
	private String fICOG4;

	@SerializedName("NO_OF_APPLICANTS")
	private String nOOFAPPLICANTS;

	@SerializedName("MBR_STS")
	private String mBRSTS;

	public String getPRODUCTTYPE(){
		return pRODUCTTYPE;
	}

	public String getACCOUNTTYPE(){
		return aCCOUNTTYPE;
	}

	public String getREPORTDATE(){
		return rEPORTDATE;
	}

	public String getAMOUNT(){
		return aMOUNT;
	}

	public String getENQUIRYNO(){
		return eNQUIRYNO;
	}

	public String getMBRTYPE(){
		return mBRTYPE;
	}

	public String getENQUIRYTYPE(){
		return eNQUIRYTYPE;
	}

	public String getENQUIRYREFERENCE(){
		return eNQUIRYREFERENCE;
	}

	public String getFICOG4(){
		return fICOG4;
	}

	public String getNOOFAPPLICANTS(){
		return nOOFAPPLICANTS;
	}

	public String getMBRSTS(){
		return mBRSTS;
	}
}