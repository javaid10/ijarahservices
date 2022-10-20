package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DEFAULTSItem{

	@SerializedName("DEFAULT")
	private List<DEFAULTItem> dEFAULT;

	public void setDEFAULT(List<DEFAULTItem> dEFAULT){
		this.dEFAULT = dEFAULT;
	}

	public List<DEFAULTItem> getDEFAULT(){
		return dEFAULT;
	}

	@Override
 	public String toString(){
		return 
			"DEFAULTSItem{" + 
			"dEFAULT = '" + dEFAULT + '\'' + 
			"}";
		}
}