package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PROVIDEDItem{

	@SerializedName("PCNAM")
	private List<PCNAMItem> pCNAM;

	@SerializedName("PCDOB")
	private String pCDOB;

	@SerializedName("PCEML")
	private String pCEML;

	@SerializedName("PCNAT")
	private String pCNAT;

	@SerializedName("PCGND")
	private String pCGND;

	@SerializedName("PCMAR")
	private String pCMAR;

	public void setPCNAM(List<PCNAMItem> pCNAM){
		this.pCNAM = pCNAM;
	}

	public List<PCNAMItem> getPCNAM(){
		return pCNAM;
	}

	public void setPCDOB(String pCDOB){
		this.pCDOB = pCDOB;
	}

	public String getPCDOB(){
		return pCDOB;
	}

	public void setPCEML(String pCEML){
		this.pCEML = pCEML;
	}

	public String getPCEML(){
		return pCEML;
	}

	public void setPCNAT(String pCNAT){
		this.pCNAT = pCNAT;
	}

	public String getPCNAT(){
		return pCNAT;
	}

	public void setPCGND(String pCGND){
		this.pCGND = pCGND;
	}

	public String getPCGND(){
		return pCGND;
	}

	public void setPCMAR(String pCMAR){
		this.pCMAR = pCMAR;
	}

	public String getPCMAR(){
		return pCMAR;
	}

	@Override
 	public String toString(){
		return 
			"PROVIDEDItem{" + 
			"pCNAM = '" + pCNAM + '\'' + 
			",pCDOB = '" + pCDOB + '\'' + 
			",pCEML = '" + pCEML + '\'' + 
			",pCNAT = '" + pCNAT + '\'' + 
			",pCGND = '" + pCGND + '\'' + 
			",pCMAR = '" + pCMAR + '\'' + 
			"}";
		}
}