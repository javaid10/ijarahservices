package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ADDRESSESItem{

	@SerializedName("ADDRESS")
	private List<ADDRESSItem> aDDRESS;

	public void setADDRESS(List<ADDRESSItem> aDDRESS){
		this.aDDRESS = aDDRESS;
	}

	public List<ADDRESSItem> getADDRESS(){
		return aDDRESS;
	}

	@Override
 	public String toString(){
		return 
			"ADDRESSESItem{" + 
			"aDDRESS = '" + aDDRESS + '\'' + 
			"}";
		}
}