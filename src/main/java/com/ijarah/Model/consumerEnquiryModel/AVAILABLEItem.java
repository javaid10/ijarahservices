package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AVAILABLEItem{

	@SerializedName("ACEML")
	private String aCEML;

	@SerializedName("ACDOB")
	private String aCDOB;

	@SerializedName("ACGND")
	private String aCGND;

	@SerializedName("ACNAM")
	private List<ACNAMItem> aCNAM;

	@SerializedName("ACNAT")
	private String aCNAT;

	@SerializedName("ACMAR")
	private String aCMAR;

	public void setACEML(String aCEML){
		this.aCEML = aCEML;
	}

	public String getACEML(){
		return aCEML;
	}

	public void setACDOB(String aCDOB){
		this.aCDOB = aCDOB;
	}

	public String getACDOB(){
		return aCDOB;
	}

	public void setACGND(String aCGND){
		this.aCGND = aCGND;
	}

	public String getACGND(){
		return aCGND;
	}

	public void setACNAM(List<ACNAMItem> aCNAM){
		this.aCNAM = aCNAM;
	}

	public List<ACNAMItem> getACNAM(){
		return aCNAM;
	}

	public void setACNAT(String aCNAT){
		this.aCNAT = aCNAT;
	}

	public String getACNAT(){
		return aCNAT;
	}

	public void setACMAR(String aCMAR){
		this.aCMAR = aCMAR;
	}

	public String getACMAR(){
		return aCMAR;
	}

	@Override
 	public String toString(){
		return 
			"AVAILABLEItem{" + 
			"aCEML = '" + aCEML + '\'' + 
			",aCDOB = '" + aCDOB + '\'' + 
			",aCGND = '" + aCGND + '\'' + 
			",aCNAM = '" + aCNAM + '\'' + 
			",aCNAT = '" + aCNAT + '\'' + 
			",aCMAR = '" + aCMAR + '\'' + 
			"}";
		}
}