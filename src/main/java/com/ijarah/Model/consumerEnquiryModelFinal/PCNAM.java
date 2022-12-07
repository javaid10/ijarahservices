package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class PCNAM{

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

	public String getPCNMFA(){
		return pCNMFA;
	}

	public String getPCNM3A(){
		return pCNM3A;
	}

	public String getPCNMFE(){
		return pCNMFE;
	}

	public String getPCNM1A(){
		return pCNM1A;
	}

	public String getPCNM3E(){
		return pCNM3E;
	}

	public String getPCNM1E(){
		return pCNM1E;
	}
}