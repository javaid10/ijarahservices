package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ConsumerEnquiry{

	@SerializedName("DATA")
	private List<DATAItem> dATA;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public void setDATA(List<DATAItem> dATA){
		this.dATA = dATA;
	}

	public List<DATAItem> getDATA(){
		return dATA;
	}

	public void setOpstatus(int opstatus){
		this.opstatus = opstatus;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public void setHttpStatusCode(int httpStatusCode){
		this.httpStatusCode = httpStatusCode;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}

	@Override
 	public String toString(){
		return 
			"ConsumerEnquiry{" + 
			"dATA = '" + dATA + '\'' + 
			",opstatus = '" + opstatus + '\'' + 
			",httpStatusCode = '" + httpStatusCode + '\'' + 
			"}";
		}
}