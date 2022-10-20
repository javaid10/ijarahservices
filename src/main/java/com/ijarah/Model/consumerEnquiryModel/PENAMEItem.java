package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class PENAMEItem{

	@SerializedName("PE_NMFA")
	private String pENMFA;

	@SerializedName("PE_NM1A")
	private String pENM1A;

	public void setPENMFA(String pENMFA){
		this.pENMFA = pENMFA;
	}

	public String getPENMFA(){
		return pENMFA;
	}

	public void setPENM1A(String pENM1A){
		this.pENM1A = pENM1A;
	}

	public String getPENM1A(){
		return pENM1A;
	}

	@Override
 	public String toString(){
		return 
			"PENAMEItem{" + 
			"pE_NMFA = '" + pENMFA + '\'' + 
			",pE_NM1A = '" + pENM1A + '\'' + 
			"}";
		}
}