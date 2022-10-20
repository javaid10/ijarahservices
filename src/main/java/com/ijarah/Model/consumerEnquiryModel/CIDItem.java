package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class CIDItem{

	@SerializedName("CID3")
	private String cID3;

	@SerializedName("CID2")
	private String cID2;

	@SerializedName("CID1")
	private String cID1;

	public void setCID3(String cID3){
		this.cID3 = cID3;
	}

	public String getCID3(){
		return cID3;
	}

	public void setCID2(String cID2){
		this.cID2 = cID2;
	}

	public String getCID2(){
		return cID2;
	}

	public void setCID1(String cID1){
		this.cID1 = cID1;
	}

	public String getCID1(){
		return cID1;
	}

	@Override
 	public String toString(){
		return 
			"CIDItem{" + 
			"cID3 = '" + cID3 + '\'' + 
			",cID2 = '" + cID2 + '\'' + 
			",cID1 = '" + cID1 + '\'' + 
			"}";
		}
}