package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class CCNTItem{

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

	public void setCCN5(String cCN5){
		this.cCN5 = cCN5;
	}

	public String getCCN5(){
		return cCN5;
	}

	public void setCCN4(String cCN4){
		this.cCN4 = cCN4;
	}

	public String getCCN4(){
		return cCN4;
	}

	public void setCCN3(String cCN3){
		this.cCN3 = cCN3;
	}

	public String getCCN3(){
		return cCN3;
	}

	public void setCCN2(String cCN2){
		this.cCN2 = cCN2;
	}

	public String getCCN2(){
		return cCN2;
	}

	public void setCCN1(String cCN1){
		this.cCN1 = cCN1;
	}

	public String getCCN1(){
		return cCN1;
	}

	@Override
 	public String toString(){
		return 
			"CCNTItem{" + 
			"cCN5 = '" + cCN5 + '\'' + 
			",cCN4 = '" + cCN4 + '\'' + 
			",cCN3 = '" + cCN3 + '\'' + 
			",cCN2 = '" + cCN2 + '\'' + 
			",cCN1 = '" + cCN1 + '\'' + 
			"}";
		}
}