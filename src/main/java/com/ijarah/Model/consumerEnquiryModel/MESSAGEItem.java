package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MESSAGEItem{

	@SerializedName("ITEM")
	private List<ITEMItem> iTEM;

	public void setITEM(List<ITEMItem> iTEM){
		this.iTEM = iTEM;
	}

	public List<ITEMItem> getITEM(){
		return iTEM;
	}

	@Override
 	public String toString(){
		return 
			"MESSAGEItem{" + 
			"iTEM = '" + iTEM + '\'' + 
			"}";
		}
}