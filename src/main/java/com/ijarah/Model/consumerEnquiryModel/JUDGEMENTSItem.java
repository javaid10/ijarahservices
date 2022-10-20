package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class JUDGEMENTSItem{

	@SerializedName("JUDGEMENT")
	private List<JUDGEMENTItem> jUDGEMENT;

	public void setJUDGEMENT(List<JUDGEMENTItem> jUDGEMENT){
		this.jUDGEMENT = jUDGEMENT;
	}

	public List<JUDGEMENTItem> getJUDGEMENT(){
		return jUDGEMENT;
	}

	@Override
 	public String toString(){
		return 
			"JUDGEMENTSItem{" + 
			"jUDGEMENT = '" + jUDGEMENT + '\'' + 
			"}";
		}
}