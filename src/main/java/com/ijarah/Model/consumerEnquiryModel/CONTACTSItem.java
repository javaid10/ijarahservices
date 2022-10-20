package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CONTACTSItem{

	@SerializedName("CCNT")
	private List<CCNTItem> cCNT;

	public void setCCNT(List<CCNTItem> cCNT){
		this.cCNT = cCNT;
	}

	public List<CCNTItem> getCCNT(){
		return cCNT;
	}

	@Override
 	public String toString(){
		return 
			"CONTACTSItem{" + 
			"cCNT = '" + cCNT + '\'' + 
			"}";
		}
}