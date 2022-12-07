package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class CID{

	@SerializedName("CID3")
	private String cID3;

	@SerializedName("CID2")
	private String cID2;

	@SerializedName("CID1")
	private String cID1;

	public String getCID3(){
		return cID3;
	}

	public String getCID2(){
		return cID2;
	}

	public String getCID1(){
		return cID1;
	}
}