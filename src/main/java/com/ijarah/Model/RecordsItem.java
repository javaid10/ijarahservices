package com.ijarah.Model;

import com.google.gson.annotations.SerializedName;

public class RecordsItem {

	@SerializedName("DTI")
	private String dTI;

	public String getDTI(){
		return dTI;
	}
}