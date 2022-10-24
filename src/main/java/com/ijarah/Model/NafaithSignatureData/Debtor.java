package com.ijarah.Model.NafaithSignatureData;

import com.google.gson.annotations.SerializedName;

public class Debtor{

	@SerializedName("national_id")
	private String nationalId;

	public void setNationalId(String nationalId){
		this.nationalId = nationalId;
	}

	public String getNationalId(){
		return nationalId;
	}

	@Override
 	public String toString(){
		return 
			"Debtor{" + 
			"national_id = '" + nationalId + '\'' + 
			"}";
		}
}