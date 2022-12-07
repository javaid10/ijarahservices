package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class CONTACTSItem{

	@SerializedName("CCN5")
	private String cCN5;

	@SerializedName("CCN4")
	private String cCN4;

	@SerializedName("CCN3")
	private String cCN3;

	@SerializedName("CCN2")
	private String cCN2;

	@SerializedName("CCN1")
	private String cCN1;

	public String getCCN5(){
		return cCN5;
	}

	public String getCCN4(){
		return cCN4;
	}

	public String getCCN3(){
		return cCN3;
	}

	public String getCCN2(){
		return cCN2;
	}

	public String getCCN1(){
		return cCN1;
	}
}