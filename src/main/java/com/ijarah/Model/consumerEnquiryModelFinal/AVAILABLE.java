package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class AVAILABLE{

	@SerializedName("ACEML")
	private String aCEML;

	@SerializedName("ACDOB")
	private String aCDOB;

	@SerializedName("ACGND")
	private String aCGND;

	@SerializedName("ACNAT")
	private String aCNAT;

	@SerializedName("ACMAR")
	private String aCMAR;

	public String getACEML(){
		return aCEML;
	}

	public String getACDOB(){
		return aCDOB;
	}

	public String getACGND(){
		return aCGND;
	}

	public String getACNAT(){
		return aCNAT;
	}

	public String getACMAR(){
		return aCMAR;
	}
}