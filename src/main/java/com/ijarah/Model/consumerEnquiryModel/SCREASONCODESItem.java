package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class SCREASONCODESItem{

	@SerializedName("SC_REASON_CODE")
	private String sCREASONCODE;

	public void setSCREASONCODE(String sCREASONCODE){
		this.sCREASONCODE = sCREASONCODE;
	}

	public String getSCREASONCODE(){
		return sCREASONCODE;
	}

	@Override
 	public String toString(){
		return 
			"SCREASONCODESItem{" + 
			"sC_REASON_CODE = '" + sCREASONCODE + '\'' + 
			"}";
		}
}