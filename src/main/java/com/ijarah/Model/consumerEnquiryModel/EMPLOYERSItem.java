package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class EMPLOYERSItem{

	@SerializedName("EMPLOYER")
	private List<EMPLOYERItem> eMPLOYER;

	public void setEMPLOYER(List<EMPLOYERItem> eMPLOYER){
		this.eMPLOYER = eMPLOYER;
	}

	public List<EMPLOYERItem> getEMPLOYER(){
		return eMPLOYER;
	}

	@Override
 	public String toString(){
		return 
			"EMPLOYERSItem{" + 
			"eMPLOYER = '" + eMPLOYER + '\'' + 
			"}";
		}
}