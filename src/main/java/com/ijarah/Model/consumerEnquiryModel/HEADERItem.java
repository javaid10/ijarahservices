package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class HEADERItem{

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

	public void setRUNNO(String rUNNO){
		this.rUNNO = rUNNO;
	}

	public String getRUNNO(){
		return rUNNO;
	}

	public void setMEMBERID(String mEMBERID){
		this.mEMBERID = mEMBERID;
	}

	public String getMEMBERID(){
		return mEMBERID;
	}

	public void setUSERID(String uSERID){
		this.uSERID = uSERID;
	}

	public String getUSERID(){
		return uSERID;
	}

	public void setTOTITEMS(String tOTITEMS){
		this.tOTITEMS = tOTITEMS;
	}

	public String getTOTITEMS(){
		return tOTITEMS;
	}

	public void setERRITEMS(String eRRITEMS){
		this.eRRITEMS = eRRITEMS;
	}

	public String getERRITEMS(){
		return eRRITEMS;
	}

	@Override
 	public String toString(){
		return 
			"HEADERItem{" + 
			"rUN_NO = '" + rUNNO + '\'' + 
			",mEMBER_ID = '" + mEMBERID + '\'' + 
			",uSER_ID = '" + uSERID + '\'' + 
			",tOT_ITEMS = '" + tOTITEMS + '\'' + 
			",eRR_ITEMS = '" + eRRITEMS + '\'' + 
			"}";
		}
}