package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class DISCLAIMERItem{

	@SerializedName("DI_TEXT_AR")
	private String dITEXTAR;

	@SerializedName("DI_TEXT")
	private String dITEXT;

	public void setDITEXTAR(String dITEXTAR){
		this.dITEXTAR = dITEXTAR;
	}

	public String getDITEXTAR(){
		return dITEXTAR;
	}

	public void setDITEXT(String dITEXT){
		this.dITEXT = dITEXT;
	}

	public String getDITEXT(){
		return dITEXT;
	}

	@Override
 	public String toString(){
		return 
			"DISCLAIMERItem{" + 
			"dI_TEXT_AR = '" + dITEXTAR + '\'' + 
			",dI_TEXT = '" + dITEXT + '\'' + 
			"}";
		}
}