package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class DISCLAIMER{

	@SerializedName("DI_TEXT_AR")
	private String dITEXTAR;

	@SerializedName("DI_TEXT")
	private String dITEXT;

	public String getDITEXTAR(){
		return dITEXTAR;
	}

	public String getDITEXT(){
		return dITEXT;
	}
}