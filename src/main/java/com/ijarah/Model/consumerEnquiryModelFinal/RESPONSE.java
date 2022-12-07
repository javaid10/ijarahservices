package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class RESPONSE{

	@SerializedName("STATUS")
	private String sTATUS;

	@SerializedName("ACTION")
	private String aCTION;

	@SerializedName("SERVICE")
	private String sERVICE;

	@SerializedName("VERSION_NO")
	private String vERSIONNO;

	public String getSTATUS(){
		return sTATUS;
	}

	public String getACTION(){
		return aCTION;
	}

	public String getSERVICE(){
		return sERVICE;
	}

	public String getVERSIONNO(){
		return vERSIONNO;
	}
}