package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class EMPLOYERItem{

	@SerializedName("ELEN")
	private String eLEN;

	@SerializedName("ECEX")
	private String eCEX;

	@SerializedName("EDOE")
	private String eDOE;

	@SerializedName("ETMS")
	private String eTMS;

	@SerializedName("ETYP")
	private String eTYP;

	@SerializedName("EDLD")
	private String eDLD;

	@SerializedName("EOCA")
	private String eOCA;

	@SerializedName("EMBS")
	private String eMBS;

	@SerializedName("EADR")
	private List<EADRItem> eADR;

	@SerializedName("ENMA")
	private String eNMA;

	@SerializedName("ENME")
	private String eNME;

	public void setELEN(String eLEN){
		this.eLEN = eLEN;
	}

	public String getELEN(){
		return eLEN;
	}

	public void setECEX(String eCEX){
		this.eCEX = eCEX;
	}

	public String getECEX(){
		return eCEX;
	}

	public void setEDOE(String eDOE){
		this.eDOE = eDOE;
	}

	public String getEDOE(){
		return eDOE;
	}

	public void setETMS(String eTMS){
		this.eTMS = eTMS;
	}

	public String getETMS(){
		return eTMS;
	}

	public void setETYP(String eTYP){
		this.eTYP = eTYP;
	}

	public String getETYP(){
		return eTYP;
	}

	public void setEDLD(String eDLD){
		this.eDLD = eDLD;
	}

	public String getEDLD(){
		return eDLD;
	}

	public void setEOCA(String eOCA){
		this.eOCA = eOCA;
	}

	public String getEOCA(){
		return eOCA;
	}

	public void setEMBS(String eMBS){
		this.eMBS = eMBS;
	}

	public String getEMBS(){
		return eMBS;
	}

	public void setEADR(List<EADRItem> eADR){
		this.eADR = eADR;
	}

	public List<EADRItem> getEADR(){
		return eADR;
	}

	public void setENMA(String eNMA){
		this.eNMA = eNMA;
	}

	public String getENMA(){
		return eNMA;
	}

	public void setENME(String eNME){
		this.eNME = eNME;
	}

	public String getENME(){
		return eNME;
	}

	@Override
 	public String toString(){
		return 
			"EMPLOYERItem{" + 
			"eLEN = '" + eLEN + '\'' + 
			",eCEX = '" + eCEX + '\'' + 
			",eDOE = '" + eDOE + '\'' + 
			",eTMS = '" + eTMS + '\'' + 
			",eTYP = '" + eTYP + '\'' + 
			",eDLD = '" + eDLD + '\'' + 
			",eOCA = '" + eOCA + '\'' + 
			",eMBS = '" + eMBS + '\'' + 
			",eADR = '" + eADR + '\'' + 
			",eNMA = '" + eNMA + '\'' + 
			",eNME = '" + eNME + '\'' + 
			"}";
		}
}