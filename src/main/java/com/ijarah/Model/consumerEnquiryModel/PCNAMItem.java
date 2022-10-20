package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class PCNAMItem{

	@SerializedName("PCNMFA")
	private String pCNMFA;

	@SerializedName("PCNM3A")
	private String pCNM3A;

	@SerializedName("PCNMFE")
	private String pCNMFE;

	@SerializedName("PCNM1A")
	private String pCNM1A;

	@SerializedName("PCNM3E")
	private String pCNM3E;

	@SerializedName("PCNM1E")
	private String pCNM1E;

	public void setPCNMFA(String pCNMFA){
		this.pCNMFA = pCNMFA;
	}

	public String getPCNMFA(){
		return pCNMFA;
	}

	public void setPCNM3A(String pCNM3A){
		this.pCNM3A = pCNM3A;
	}

	public String getPCNM3A(){
		return pCNM3A;
	}

	public void setPCNMFE(String pCNMFE){
		this.pCNMFE = pCNMFE;
	}

	public String getPCNMFE(){
		return pCNMFE;
	}

	public void setPCNM1A(String pCNM1A){
		this.pCNM1A = pCNM1A;
	}

	public String getPCNM1A(){
		return pCNM1A;
	}

	public void setPCNM3E(String pCNM3E){
		this.pCNM3E = pCNM3E;
	}

	public String getPCNM3E(){
		return pCNM3E;
	}

	public void setPCNM1E(String pCNM1E){
		this.pCNM1E = pCNM1E;
	}

	public String getPCNM1E(){
		return pCNM1E;
	}

	@Override
 	public String toString(){
		return 
			"PCNAMItem{" + 
			"pCNMFA = '" + pCNMFA + '\'' + 
			",pCNM3A = '" + pCNM3A + '\'' + 
			",pCNMFE = '" + pCNMFE + '\'' + 
			",pCNM1A = '" + pCNM1A + '\'' + 
			",pCNM3E = '" + pCNM3E + '\'' + 
			",pCNM1E = '" + pCNM1E + '\'' + 
			"}";
		}
}