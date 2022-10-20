package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class EADRItem{

	@SerializedName("EAD9")
	private String eAD9;

	@SerializedName("EAD8A")
	private String eAD8A;

	@SerializedName("EADT")
	private String eADT;

	@SerializedName("EAD6")
	private String eAD6;

	@SerializedName("EAD7")
	private String eAD7;

	@SerializedName("EAD8E")
	private String eAD8E;

	@SerializedName("EAD1A")
	private String eAD1A;

	public void setEAD9(String eAD9){
		this.eAD9 = eAD9;
	}

	public String getEAD9(){
		return eAD9;
	}

	public void setEAD8A(String eAD8A){
		this.eAD8A = eAD8A;
	}

	public String getEAD8A(){
		return eAD8A;
	}

	public void setEADT(String eADT){
		this.eADT = eADT;
	}

	public String getEADT(){
		return eADT;
	}

	public void setEAD6(String eAD6){
		this.eAD6 = eAD6;
	}

	public String getEAD6(){
		return eAD6;
	}

	public void setEAD7(String eAD7){
		this.eAD7 = eAD7;
	}

	public String getEAD7(){
		return eAD7;
	}

	public void setEAD8E(String eAD8E){
		this.eAD8E = eAD8E;
	}

	public String getEAD8E(){
		return eAD8E;
	}

	public void setEAD1A(String eAD1A){
		this.eAD1A = eAD1A;
	}

	public String getEAD1A(){
		return eAD1A;
	}

	@Override
 	public String toString(){
		return 
			"EADRItem{" + 
			"eAD9 = '" + eAD9 + '\'' + 
			",eAD8A = '" + eAD8A + '\'' + 
			",eADT = '" + eADT + '\'' + 
			",eAD6 = '" + eAD6 + '\'' + 
			",eAD7 = '" + eAD7 + '\'' + 
			",eAD8E = '" + eAD8E + '\'' + 
			",eAD1A = '" + eAD1A + '\'' + 
			"}";
		}
}