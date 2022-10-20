package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CONSUMERItem{

	@SerializedName("CONTACTS")
	private List<CONTACTSItem> cONTACTS;

	@SerializedName("SCORE")
	private List<SCOREItem> sCORE;

	@SerializedName("CAPL")
	private String cAPL;

	@SerializedName("CVIP")
	private String cVIP;

	@SerializedName("JUDGEMENTS")
	private List<JUDGEMENTSItem> jUDGEMENTS;

	@SerializedName("PROVIDED")
	private List<PROVIDEDItem> pROVIDED;

	@SerializedName("EMPLOYERS")
	private List<EMPLOYERSItem> eMPLOYERS;

	@SerializedName("PREV_ENQUIRIES")
	private List<PREVENQUIRIESItem> pREVENQUIRIES;

	@SerializedName("AVAILABLE")
	private List<AVAILABLEItem> aVAILABLE;

	@SerializedName("ENTITY")
	private String eNTITY;

	@SerializedName("BOUNCED_CHECKS")
	private List<BOUNCEDCHECKSItem> bOUNCEDCHECKS;

	@SerializedName("SUMMARY")
	private List<SUMMARYItem> sUMMARY;

	@SerializedName("CI_DETAILS")
	private List<CIDETAILSItem> cIDETAILS;

	@SerializedName("ADDRESSES")
	private List<ADDRESSESItem> aDDRESSES;

	@SerializedName("DEFAULTS")
	private List<DEFAULTSItem> dEFAULTS;

	@SerializedName("CID")
	private List<CIDItem> cID;

	public void setCONTACTS(List<CONTACTSItem> cONTACTS){
		this.cONTACTS = cONTACTS;
	}

	public List<CONTACTSItem> getCONTACTS(){
		return cONTACTS;
	}

	public void setSCORE(List<SCOREItem> sCORE){
		this.sCORE = sCORE;
	}

	public List<SCOREItem> getSCORE(){
		return sCORE;
	}

	public void setCAPL(String cAPL){
		this.cAPL = cAPL;
	}

	public String getCAPL(){
		return cAPL;
	}

	public void setCVIP(String cVIP){
		this.cVIP = cVIP;
	}

	public String getCVIP(){
		return cVIP;
	}

	public void setJUDGEMENTS(List<JUDGEMENTSItem> jUDGEMENTS){
		this.jUDGEMENTS = jUDGEMENTS;
	}

	public List<JUDGEMENTSItem> getJUDGEMENTS(){
		return jUDGEMENTS;
	}

	public void setPROVIDED(List<PROVIDEDItem> pROVIDED){
		this.pROVIDED = pROVIDED;
	}

	public List<PROVIDEDItem> getPROVIDED(){
		return pROVIDED;
	}

	public void setEMPLOYERS(List<EMPLOYERSItem> eMPLOYERS){
		this.eMPLOYERS = eMPLOYERS;
	}

	public List<EMPLOYERSItem> getEMPLOYERS(){
		return eMPLOYERS;
	}

	public void setPREVENQUIRIES(List<PREVENQUIRIESItem> pREVENQUIRIES){
		this.pREVENQUIRIES = pREVENQUIRIES;
	}

	public List<PREVENQUIRIESItem> getPREVENQUIRIES(){
		return pREVENQUIRIES;
	}

	public void setAVAILABLE(List<AVAILABLEItem> aVAILABLE){
		this.aVAILABLE = aVAILABLE;
	}

	public List<AVAILABLEItem> getAVAILABLE(){
		return aVAILABLE;
	}

	public void setENTITY(String eNTITY){
		this.eNTITY = eNTITY;
	}

	public String getENTITY(){
		return eNTITY;
	}

	public void setBOUNCEDCHECKS(List<BOUNCEDCHECKSItem> bOUNCEDCHECKS){
		this.bOUNCEDCHECKS = bOUNCEDCHECKS;
	}

	public List<BOUNCEDCHECKSItem> getBOUNCEDCHECKS(){
		return bOUNCEDCHECKS;
	}

	public void setSUMMARY(List<SUMMARYItem> sUMMARY){
		this.sUMMARY = sUMMARY;
	}

	public List<SUMMARYItem> getSUMMARY(){
		return sUMMARY;
	}

	public void setCIDETAILS(List<CIDETAILSItem> cIDETAILS){
		this.cIDETAILS = cIDETAILS;
	}

	public List<CIDETAILSItem> getCIDETAILS(){
		return cIDETAILS;
	}

	public void setADDRESSES(List<ADDRESSESItem> aDDRESSES){
		this.aDDRESSES = aDDRESSES;
	}

	public List<ADDRESSESItem> getADDRESSES(){
		return aDDRESSES;
	}

	public void setDEFAULTS(List<DEFAULTSItem> dEFAULTS){
		this.dEFAULTS = dEFAULTS;
	}

	public List<DEFAULTSItem> getDEFAULTS(){
		return dEFAULTS;
	}

	public void setCID(List<CIDItem> cID){
		this.cID = cID;
	}

	public List<CIDItem> getCID(){
		return cID;
	}

	@Override
 	public String toString(){
		return 
			"CONSUMERItem{" + 
			"cONTACTS = '" + cONTACTS + '\'' + 
			",sCORE = '" + sCORE + '\'' + 
			",cAPL = '" + cAPL + '\'' + 
			",cVIP = '" + cVIP + '\'' + 
			",jUDGEMENTS = '" + jUDGEMENTS + '\'' + 
			",pROVIDED = '" + pROVIDED + '\'' + 
			",eMPLOYERS = '" + eMPLOYERS + '\'' + 
			",pREV_ENQUIRIES = '" + pREVENQUIRIES + '\'' + 
			",aVAILABLE = '" + aVAILABLE + '\'' + 
			",eNTITY = '" + eNTITY + '\'' + 
			",bOUNCED_CHECKS = '" + bOUNCEDCHECKS + '\'' + 
			",sUMMARY = '" + sUMMARY + '\'' + 
			",cI_DETAILS = '" + cIDETAILS + '\'' + 
			",aDDRESSES = '" + aDDRESSES + '\'' + 
			",dEFAULTS = '" + dEFAULTS + '\'' + 
			",cID = '" + cID + '\'' + 
			"}";
		}
}