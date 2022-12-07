package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class PROVIDED{

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

	public String getPCDOB(){
		return pCDOB;
	}

	public String getPCEML(){
		return pCEML;
	}

	public String getPCNAT(){
		return pCNAT;
	}

	public String getPCGND(){
		return pCGND;
	}

	public String getPCMAR(){
		return pCMAR;
	}
}