package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class ACNAMItem{

	@SerializedName("ACNM7E")
	private String aCNM7E;

	public void setACNM7E(String aCNM7E){
		this.aCNM7E = aCNM7E;
	}

	public String getACNM7E(){
		return aCNM7E;
	}

	@Override
 	public String toString(){
		return 
			"ACNAMItem{" + 
			"aCNM7E = '" + aCNM7E + '\'' + 
			"}";
		}
}