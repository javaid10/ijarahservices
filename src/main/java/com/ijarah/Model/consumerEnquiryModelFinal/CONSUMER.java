package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class CONSUMER{

	@SerializedName("ENTITY")
	private String eNTITY;

	@SerializedName("CAPL")
	private String cAPL;

	@SerializedName("CVIP")
	private String cVIP;

	public String getENTITY(){
		return eNTITY;
	}

	public String getCAPL(){
		return cAPL;
	}

	public String getCVIP(){
		return cVIP;
	}
}