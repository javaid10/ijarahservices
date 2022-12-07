package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class ACNAM{

	@SerializedName("ACNMFA")
	private String aCNMFA;

	@SerializedName("ACNM1A")
	private String aCNM1A;

	public String getACNMFA(){
		return aCNMFA;
	}

	public String getACNM1A(){
		return aCNM1A;
	}
}