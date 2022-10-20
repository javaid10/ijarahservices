package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DATAItem{

	@SerializedName("RESPONSE")
	private List<RESPONSEItem> rESPONSE;

	public void setRESPONSE(List<RESPONSEItem> rESPONSE){
		this.rESPONSE = rESPONSE;
	}

	public List<RESPONSEItem> getRESPONSE(){
		return rESPONSE;
	}

	@Override
 	public String toString(){
		return 
			"DATAItem{" + 
			"rESPONSE = '" + rESPONSE + '\'' + 
			"}";
		}
}