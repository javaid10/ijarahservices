package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RSPREPORTItem{

	@SerializedName("ACCOUNT_TYPE")
	private String aCCOUNTTYPE;

	@SerializedName("REPORT_DATE")
	private String rEPORTDATE;

	@SerializedName("ENQUIRY_NO")
	private String eNQUIRYNO;

	@SerializedName("ENQUIRY_TYPE")
	private String eNQUIRYTYPE;

	@SerializedName("ENQUIRY_REFERENCE")
	private String eNQUIRYREFERENCE;

	@SerializedName("FICOG4")
	private String fICOG4;

	@SerializedName("NO_OF_APPLICANTS")
	private String nOOFAPPLICANTS;

	@SerializedName("PRODUCT_TYPE")
	private String pRODUCTTYPE;

	@SerializedName("DISCLAIMER")
	private List<DISCLAIMERItem> dISCLAIMER;

	@SerializedName("AMOUNT")
	private String aMOUNT;

	@SerializedName("CONSUMER")
	private List<CONSUMERItem> cONSUMER;

	@SerializedName("MBR_TYPE")
	private String mBRTYPE;

	@SerializedName("MBR_STS")
	private String mBRSTS;

	public void setACCOUNTTYPE(String aCCOUNTTYPE){
		this.aCCOUNTTYPE = aCCOUNTTYPE;
	}

	public String getACCOUNTTYPE(){
		return aCCOUNTTYPE;
	}

	public void setREPORTDATE(String rEPORTDATE){
		this.rEPORTDATE = rEPORTDATE;
	}

	public String getREPORTDATE(){
		return rEPORTDATE;
	}

	public void setENQUIRYNO(String eNQUIRYNO){
		this.eNQUIRYNO = eNQUIRYNO;
	}

	public String getENQUIRYNO(){
		return eNQUIRYNO;
	}

	public void setENQUIRYTYPE(String eNQUIRYTYPE){
		this.eNQUIRYTYPE = eNQUIRYTYPE;
	}

	public String getENQUIRYTYPE(){
		return eNQUIRYTYPE;
	}

	public void setENQUIRYREFERENCE(String eNQUIRYREFERENCE){
		this.eNQUIRYREFERENCE = eNQUIRYREFERENCE;
	}

	public String getENQUIRYREFERENCE(){
		return eNQUIRYREFERENCE;
	}

	public void setFICOG4(String fICOG4){
		this.fICOG4 = fICOG4;
	}

	public String getFICOG4(){
		return fICOG4;
	}

	public void setNOOFAPPLICANTS(String nOOFAPPLICANTS){
		this.nOOFAPPLICANTS = nOOFAPPLICANTS;
	}

	public String getNOOFAPPLICANTS(){
		return nOOFAPPLICANTS;
	}

	public void setPRODUCTTYPE(String pRODUCTTYPE){
		this.pRODUCTTYPE = pRODUCTTYPE;
	}

	public String getPRODUCTTYPE(){
		return pRODUCTTYPE;
	}

	public void setDISCLAIMER(List<DISCLAIMERItem> dISCLAIMER){
		this.dISCLAIMER = dISCLAIMER;
	}

	public List<DISCLAIMERItem> getDISCLAIMER(){
		return dISCLAIMER;
	}

	public void setAMOUNT(String aMOUNT){
		this.aMOUNT = aMOUNT;
	}

	public String getAMOUNT(){
		return aMOUNT;
	}

	public void setCONSUMER(List<CONSUMERItem> cONSUMER){
		this.cONSUMER = cONSUMER;
	}

	public List<CONSUMERItem> getCONSUMER(){
		return cONSUMER;
	}

	public void setMBRTYPE(String mBRTYPE){
		this.mBRTYPE = mBRTYPE;
	}

	public String getMBRTYPE(){
		return mBRTYPE;
	}

	public void setMBRSTS(String mBRSTS){
		this.mBRSTS = mBRSTS;
	}

	public String getMBRSTS(){
		return mBRSTS;
	}

	@Override
 	public String toString(){
		return 
			"RSPREPORTItem{" + 
			"aCCOUNT_TYPE = '" + aCCOUNTTYPE + '\'' + 
			",rEPORT_DATE = '" + rEPORTDATE + '\'' + 
			",eNQUIRY_NO = '" + eNQUIRYNO + '\'' + 
			",eNQUIRY_TYPE = '" + eNQUIRYTYPE + '\'' + 
			",eNQUIRY_REFERENCE = '" + eNQUIRYREFERENCE + '\'' + 
			",fICOG4 = '" + fICOG4 + '\'' + 
			",nO_OF_APPLICANTS = '" + nOOFAPPLICANTS + '\'' + 
			",pRODUCT_TYPE = '" + pRODUCTTYPE + '\'' + 
			",dISCLAIMER = '" + dISCLAIMER + '\'' + 
			",aMOUNT = '" + aMOUNT + '\'' + 
			",cONSUMER = '" + cONSUMER + '\'' + 
			",mBR_TYPE = '" + mBRTYPE + '\'' + 
			",mBR_STS = '" + mBRSTS + '\'' + 
			"}";
		}
}