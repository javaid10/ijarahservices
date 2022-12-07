package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class HEADER{

	@SerializedName("RUN_NO")
	private String rUNNO;

	@SerializedName("MEMBER_ID")
	private String mEMBERID;

	@SerializedName("USER_ID")
	private String uSERID;

	@SerializedName("TOT_ITEMS")
	private String tOTITEMS;

	@SerializedName("ERR_ITEMS")
	private String eRRITEMS;

	public String getRUNNO(){
		return rUNNO;
	}

	public String getMEMBERID(){
		return mEMBERID;
	}

	public String getUSERID(){
		return uSERID;
	}

	public String getTOTITEMS(){
		return tOTITEMS;
	}

	public String getERRITEMS(){
		return eRRITEMS;
	}
}