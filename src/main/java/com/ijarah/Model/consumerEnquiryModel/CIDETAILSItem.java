package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CIDETAILSItem{

	@SerializedName("CI_DETAIL")
	private List<CIDETAILItem> cIDETAIL;

	public void setCIDETAIL(List<CIDETAILItem> cIDETAIL){
		this.cIDETAIL = cIDETAIL;
	}

	public List<CIDETAILItem> getCIDETAIL(){
		return cIDETAIL;
	}

	@Override
 	public String toString(){
		return 
			"CIDETAILSItem{" + 
			"cI_DETAIL = '" + cIDETAIL + '\'' + 
			"}";
		}
}