package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BOUNCEDCHECKSItem{

	@SerializedName("BOUNCED_CHECK")
	private List<BOUNCEDCHECKItem> bOUNCEDCHECK;

	public void setBOUNCEDCHECK(List<BOUNCEDCHECKItem> bOUNCEDCHECK){
		this.bOUNCEDCHECK = bOUNCEDCHECK;
	}

	public List<BOUNCEDCHECKItem> getBOUNCEDCHECK(){
		return bOUNCEDCHECK;
	}

	@Override
 	public String toString(){
		return 
			"BOUNCEDCHECKSItem{" + 
			"bOUNCED_CHECK = '" + bOUNCEDCHECK + '\'' + 
			"}";
		}
}